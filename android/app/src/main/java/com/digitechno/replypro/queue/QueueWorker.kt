package com.digitechno.replypro.queue

import android.content.Context
import android.util.Log

import com.digitechno.replypro.automation.AutomationFlag
import com.digitechno.replypro.automation.WhatsAppLauncher

import com.digitechno.replypro.database.DuplicateManager
import com.digitechno.replypro.database.PendingMessage

import com.digitechno.replypro.download.AttachmentDownloader

import com.digitechno.replypro.sms.SmsSender

import com.digitechno.replypro.accessibility.AutomationManager

object QueueWorker {

    private const val TAG = "QueueWorker"

    fun process(
        context: Context,
        pending: PendingMessage
    ) {

        try {

            Log.d(
                TAG,
                "==================================="
            )

            Log.d(
                TAG,
                "Queue Worker Started"
            )

            Log.d(
                TAG,
                "Pending ID : ${pending.id}"
            )

            Log.d(
                TAG,
                "Mobile : ${pending.mobile}"
            )

            Log.d(
                TAG,
                "Contact : ${pending.contactName}"
            )

            Log.d(
                TAG,
                "WhatsApp : ${pending.whatsappEnabled}"
            )

            Log.d(
                TAG,
                "SMS : ${pending.smsEnabled}"
            )

            Log.d(
                TAG,
                "Attachment : ${pending.attachmentUrl}"
            )

            Log.d(
                TAG,
                "==================================="
            )

            //--------------------------------------------------
            // REQUESTED MESSAGE AVAILABILITY
            //--------------------------------------------------

            val requestedWhatsApp =
                pending.whatsappEnabled &&
                        pending.message.isNotBlank()

            val requestedSms =
                pending.smsEnabled &&
                        pending.smsMessage.isNotBlank()

            //--------------------------------------------------
            // SAME-DAY DUPLICATE CHECK
            //--------------------------------------------------

            val whatsappAlreadySent =
                if (requestedWhatsApp) {

                    DuplicateManager
                        .whatsappAlreadySentToday(
                            context,
                            pending.mobile
                        )

                } else {

                    false
                }

            val smsAlreadySent =
                if (requestedSms) {

                    DuplicateManager
                        .smsAlreadySentToday(
                            context,
                            pending.mobile
                        )

                } else {

                    false
                }

            Log.d(
                TAG,
                "WhatsApp already sent today : $whatsappAlreadySent"
            )

            Log.d(
                TAG,
                "SMS already sent today : $smsAlreadySent"
            )

            //--------------------------------------------------
            // FINAL SEND DECISION
            //--------------------------------------------------

            val hasWhatsApp =
                requestedWhatsApp &&
                        !whatsappAlreadySent

            val hasSms =
                requestedSms &&
                        !smsAlreadySent

            //--------------------------------------------------
            // NOTHING LEFT
            //--------------------------------------------------

            if (
                !hasWhatsApp &&
                !hasSms
            ) {

                Log.d(
                    TAG,
                    "All requested messages already sent today."
                )

                AutomationFlag.set(
                    context,
                    false
                )

                QueueManager.completed(
                    context,
                    pending.id
                )

                return
            }

            //--------------------------------------------------
            // WHATSAPP
            //--------------------------------------------------

            if (hasWhatsApp) {

                Log.d(
                    TAG,
                    "Preparing WhatsApp..."
                )

                /*
                 * Keep the working Aug-1 automation exactly as it is.
                 */

                AutomationManager.setCurrentTask(
                    pending
                )

                AutomationFlag.set(
                    context,
                    true
                )

                QueueRecovery.startWatchdog(
                    context
                )

                //--------------------------------------------------
                // TEXT ONLY
                //--------------------------------------------------

                if (
                    pending.attachmentUrl.isNullOrBlank()
                ) {

                    WhatsAppLauncher.launch(

                        context,

                        pending.mobile,

                        pending.message
                    )

                } else {

                    //--------------------------------------------------
                    // DOWNLOAD ATTACHMENT
                    //--------------------------------------------------

                    val file =
                        AttachmentDownloader.download(

                            context,

                            pending.attachmentUrl,

                            "replypro_attachment"
                        )

                    if (
                        file != null
                    ) {

                        Log.d(
                            TAG,
                            "Attachment downloaded successfully"
                        )

                        WhatsAppLauncher.launch(

                            context,

                            pending.mobile,

                            pending.message,

                            file,

                            getMimeType(
                                pending.attachmentType
                            )
                        )

                    } else {

                        /*
                         * Keep existing fallback behaviour.
                         */

                        Log.w(
                            TAG,
                            "Attachment download failed. Sending text only."
                        )

                        WhatsAppLauncher.launch(

                            context,

                            pending.mobile,

                            pending.message
                        )
                    }
                }
            }

            //--------------------------------------------------
            // SMS
            //--------------------------------------------------

            if (hasSms) {

                Log.d(
                    TAG,
                    "Sending SMS..."
                )

                val success =
                    SmsSender.send(

                        context,

                        pending.mobile,

                        pending.smsMessage
                    )

                if (success) {

                    Log.d(
                        TAG,
                        "SMS submitted successfully."
                    )

                    /*
                     * IMPORTANT:
                     *
                     * DO NOT call DuplicateManager.markSent()
                     * here.
                     *
                     * SmsSender / ReplyProSmsSentReceiver
                     * marks it only after ALL multipart parts
                     * return RESULT_OK.
                     */

                } else {

                    Log.e(
                        TAG,
                        "SMS Sending Failed"
                    )
                }
            }

            //--------------------------------------------------
            // SMS ONLY
            //--------------------------------------------------

            if (
                !hasWhatsApp &&
                hasSms
            ) {

                Log.d(
                    TAG,
                    "SMS-only task completed."
                )

                AutomationFlag.set(
                    context,
                    false
                )

                QueueManager.completed(
                    context,
                    pending.id
                )

                return
            }

            //--------------------------------------------------
            // WHATSAPP STILL RUNNING
            //--------------------------------------------------

            if (hasWhatsApp) {

                Log.d(
                    TAG,
                    "Waiting for Accessibility to complete WhatsApp..."
                )

                /*
                 * DO NOT complete the queue here.
                 *
                 * ReplyProAccessibilityService completes it
                 * only after Send is clicked.
                 */

                return
            }

            //--------------------------------------------------
            // SAFETY
            //--------------------------------------------------

            AutomationFlag.set(
                context,
                false
            )

            QueueManager.completed(
                context,
                pending.id
            )

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Queue Worker Failed",
                e
            )

            AutomationFlag.set(
                context,
                false
            )

            QueueManager.retry(

                context,

                pending,

                e.message ?: "Unknown Error"
            )
        }
    }

    //--------------------------------------------------
    // MIME TYPES
    //--------------------------------------------------

    private fun getMimeType(
        type: String?
    ): String {

        return when (
            type?.lowercase()
        ) {

            "image" ->
                "image/*"

            "video" ->
                "video/*"

            "audio" ->
                "audio/*"

            "pdf" ->
                "application/pdf"

            "doc" ->
                "application/msword"

            "docx" ->
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"

            "xls" ->
                "application/vnd.ms-excel"

            "xlsx" ->
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

            "ppt" ->
                "application/vnd.ms-powerpoint"

            "pptx" ->
                "application/vnd.openxmlformats-officedocument.presentationml.presentation"

            "zip" ->
                "application/zip"

            "txt" ->
                "text/plain"

            else ->
                "*/*"
        }
    }
}