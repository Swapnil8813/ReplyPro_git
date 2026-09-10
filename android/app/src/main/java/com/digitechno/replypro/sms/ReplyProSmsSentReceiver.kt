package com.digitechno.replypro.sms

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.util.Log

class ReplyProSmsSentReceiver : BroadcastReceiver() {

    companion object {

        private const val TAG =
            "ReplyProSmsSentReceiver"

        const val EXTRA_MOBILE =
            "extra_mobile"

        const val EXTRA_REQUEST_ID =
            "extra_request_id"

        const val EXTRA_PART_INDEX =
            "extra_part_index"

        const val EXTRA_TOTAL_PARTS =
            "extra_total_parts"
    }

    override fun onReceive(
        context: Context,
        intent: Intent?
    ) {

        if (
            intent == null
        ) {
            return
        }

        val mobile =
            intent.getStringExtra(
                EXTRA_MOBILE
            )

        val requestId =
            intent.getStringExtra(
                EXTRA_REQUEST_ID
            )

        val partIndex =
            intent.getIntExtra(
                EXTRA_PART_INDEX,
                -1
            )

        val totalParts =
            intent.getIntExtra(
                EXTRA_TOTAL_PARTS,
                -1
            )

        val result =
            resultCode

        Log.d(
            TAG,
            "==================================="
        )

        Log.d(
            TAG,
            "SMS PART RESULT"
        )

        Log.d(
            TAG,
            "Mobile      : $mobile"
        )

        Log.d(
            TAG,
            "Request ID  : $requestId"
        )

        Log.d(
            TAG,
            "Part        : ${partIndex + 1}/$totalParts"
        )

        Log.d(
            TAG,
            "Result code : $result"
        )

        // ========================================================
        // SUCCESS
        // ========================================================

        if (
            result ==
            Activity.RESULT_OK
        ) {

            Log.d(
                TAG,
                "SMS PART SENT SUCCESSFULLY"
            )

            if (
                !requestId.isNullOrBlank()
            ) {

                SmsSender.partSucceeded(
                    context,
                    requestId
                )
            }

        } else {

            Log.e(
                TAG,
                "SMS PART FAILED"
            )

            when (
                result
            ) {

                SmsManager.RESULT_ERROR_GENERIC_FAILURE -> {

                    Log.e(
                        TAG,
                        "RESULT_ERROR_GENERIC_FAILURE"
                    )
                }

                SmsManager.RESULT_ERROR_NO_SERVICE -> {

                    Log.e(
                        TAG,
                        "RESULT_ERROR_NO_SERVICE"
                    )
                }

                SmsManager.RESULT_ERROR_NULL_PDU -> {

                    Log.e(
                        TAG,
                        "RESULT_ERROR_NULL_PDU"
                    )
                }

                SmsManager.RESULT_ERROR_RADIO_OFF -> {

                    Log.e(
                        TAG,
                        "RESULT_ERROR_RADIO_OFF"
                    )
                }

                else -> {

                    Log.e(
                        TAG,
                        "Unknown SMS result: $result"
                    )
                }
            }

            if (
                !requestId.isNullOrBlank()
            ) {

                SmsSender.partFailed(
                    requestId
                )
            }
        }

        Log.d(
            TAG,
            "==================================="
        )
    }
}