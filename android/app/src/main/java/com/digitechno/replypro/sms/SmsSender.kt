package com.digitechno.replypro.sms

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.digitechno.replypro.database.DuplicateManager
import com.digitechno.replypro.database.SentMessage
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

object SmsSender {

    private const val TAG = "SmsSender"

    private const val SMS_REQUEST_BASE = 7000

    /*
     * Tracks multipart SMS parts.
     *
     * key = unique SMS request id
     *
     * value = tracker containing:
     * total parts
     * successful parts
     * failed parts
     * mobile number
     */
    private val trackers =
        ConcurrentHashMap<String, SmsTracker>()

    data class SmsTracker(
        val mobile: String,
        val totalParts: Int,
        val successfulParts: AtomicInteger =
            AtomicInteger(0),
        val failedParts: AtomicInteger =
            AtomicInteger(0)
    )

    fun send(
        context: Context,
        mobile: String,
        message: String
    ): Boolean {

        return try {

            // ====================================================
            // PERMISSION
            // ====================================================

            if (
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.SEND_SMS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                Log.e(
                    TAG,
                    "SEND_SMS permission is not granted"
                )

                return false
            }

            // ====================================================
            // MESSAGE
            // ====================================================

            if (
                message.isBlank()
            ) {

                Log.w(
                    TAG,
                    "SMS message is empty"
                )

                return false
            }

            // ====================================================
            // DUPLICATE PROTECTION
            // ====================================================

            if (
                DuplicateManager.alreadySentToday(
                    context,
                    mobile,
                    SentMessage.TYPE_SMS
                )
            ) {

                Log.d(
                    TAG,
                    "SMS already sent today. Skipping."
                )

                return true
            }

            // ====================================================
            // SMS MANAGER
            // ====================================================

            val smsManager =
                if (
                    Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.S
                ) {

                    context.getSystemService(
                        SmsManager::class.java
                    )

                } else {

                    @Suppress("DEPRECATION")
                    SmsManager.getDefault()
                }

            // ====================================================
            // DIVIDE LONG / UNICODE MESSAGE
            // ====================================================

            val parts =
                smsManager.divideMessage(
                    message
                )

            if (
                parts.isEmpty()
            ) {

                Log.e(
                    TAG,
                    "SMS divided into zero parts"
                )

                return false
            }

            Log.d(
                TAG,
                "==================================="
            )

            Log.d(
                TAG,
                "Preparing SMS"
            )

            Log.d(
                TAG,
                "Mobile       : $mobile"
            )

            Log.d(
                TAG,
                "Message size : ${message.length}"
            )

            Log.d(
                TAG,
                "SMS parts    : ${parts.size}"
            )

            // ====================================================
            // UNIQUE REQUEST ID
            // ====================================================

            val requestId =
                "${mobile}_${System.currentTimeMillis()}"

            // ====================================================
            // TRACKER
            // ====================================================

            val tracker =
                SmsTracker(
                    mobile = mobile,
                    totalParts = parts.size
                )

            trackers[
                requestId
            ] = tracker

            // ====================================================
            // PENDING INTENTS
            // ====================================================

            val sentIntents =
                ArrayList<PendingIntent>()

            parts.forEachIndexed {
                    index,
                    _ ->

                val intent =
                    Intent(
                        context,
                        ReplyProSmsSentReceiver::class.java
                    ).apply {

                        putExtra(
                            ReplyProSmsSentReceiver.EXTRA_MOBILE,
                            mobile
                        )

                        putExtra(
                            ReplyProSmsSentReceiver.EXTRA_REQUEST_ID,
                            requestId
                        )

                        putExtra(
                            ReplyProSmsSentReceiver.EXTRA_PART_INDEX,
                            index
                        )

                        putExtra(
                            ReplyProSmsSentReceiver.EXTRA_TOTAL_PARTS,
                            parts.size
                        )
                    }

                val pendingIntent =
                    PendingIntent.getBroadcast(

                        context,

                        SMS_REQUEST_BASE +
                                (System.currentTimeMillis()
                                    .toInt() and 0x7FFF) +
                                index,

                        intent,

                        PendingIntent.FLAG_UPDATE_CURRENT or
                                PendingIntent.FLAG_IMMUTABLE
                    )

                sentIntents.add(
                    pendingIntent
                )
            }

            // ====================================================
            // SEND MULTIPART SMS
            // ====================================================

            Log.d(
                TAG,
                "Sending multipart SMS..."
            )

            smsManager.sendMultipartTextMessage(

                mobile,

                null,

                parts,

                sentIntents,

                null
            )

            // ====================================================
            // IMPORTANT
            //
            // DO NOT MARK SENT HERE.
            //
            // ReplyProSmsSentReceiver marks it only after
            // every part reports RESULT_OK.
            // ====================================================

            Log.d(
                TAG,
                "Multipart SMS submitted"
            )

            Log.d(
                TAG,
                "Waiting for ${parts.size} SMS result callbacks..."
            )

            Log.d(
                TAG,
                "==================================="

            )

            true

        } catch (
            e: Exception
        ) {

            Log.e(
                TAG,
                "Multipart SMS failed",
                e
            )

            false
        }
    }

    // ============================================================
    // CALLBACK — SUCCESS
    // ============================================================

    fun partSucceeded(
        context: Context,
        requestId: String
    ) {

        val tracker =
            trackers[
                requestId
            ]
                ?: return

        val successful =
            tracker.successfulParts
                .incrementAndGet()

        Log.d(
            TAG,
            "SMS part successful: $successful/${tracker.totalParts}"
        )

        // --------------------------------------------------------
        // ALL PARTS SUCCESSFUL
        // --------------------------------------------------------

        if (
            successful ==
            tracker.totalParts
        ) {

            Log.d(
                TAG,
                "ALL SMS PARTS SENT SUCCESSFULLY"
            )

            DuplicateManager.markSent(

                context,

                tracker.mobile,

                SentMessage.TYPE_SMS
            )

            Log.d(
                TAG,
                "SMS marked as sent"
            )

            trackers.remove(
                requestId
            )
        }
    }

    // ============================================================
    // CALLBACK — FAILURE
    // ============================================================

    fun partFailed(
        requestId: String
    ) {

        val tracker =
            trackers[
                requestId
            ]
                ?: return

        val failed =
            tracker.failedParts
                .incrementAndGet()

        Log.e(
            TAG,
            "SMS part failed: $failed/${tracker.totalParts}"
        )

        /*
         * Remove tracker immediately.
         *
         * IMPORTANT:
         * Do NOT mark SMS as sent.
         */

        trackers.remove(
            requestId
        )
    }
}