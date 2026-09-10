package com.digitechno.replypro.processor

import android.content.Context
import android.provider.CallLog
import android.util.Log
import com.digitechno.replypro.database.DuplicateManager
import com.digitechno.replypro.database.ReplyProDatabase
import com.digitechno.replypro.database.SentMessage
import com.digitechno.replypro.call.CallLogReader
import com.digitechno.replypro.model.CallData
import com.digitechno.replypro.sync.LaravelSyncManager

class CallProcessor(
    private val context: Context
) {

    companion object {

        private const val TAG = "CallProcessor"

        private const val MIN_CALL_DURATION = 2L

        private const val DUPLICATE_INTERVAL = 3000L
    }

    private var lastNumber: String? = null

    private var lastTime: Long = 0

    //--------------------------------------------------
    // Process
    //--------------------------------------------------

    fun process(callData: CallData? = null) {

        Log.d(
            TAG,
            "========== CALL PROCESSOR =========="
        )

        //--------------------------------------------------
        // IMPORTANT:
        // If CallData is supplied, process THAT call.
        // Do not search the call log again.
        //--------------------------------------------------

        val latestCall = callData
            ?: CallLogReader(context).getLatestCall()

        if (latestCall == null) {

            Log.d(
                TAG,
                "No Call Found"
            )

            return
        }

        Log.d(
            TAG,
            "Number : ${latestCall.number}"
        )

        Log.d(
            TAG,
            "Contact : ${latestCall.contactName}"
        )

        Log.d(
            TAG,
            "Duration : ${latestCall.duration}"
        )

        Log.d(
            TAG,
            "Call Type : ${getCallType(latestCall.type)}"
        )

        Log.d(
            TAG,
            "Date : ${latestCall.date}"
        )

        //--------------------------------------------------
        // Number
        //--------------------------------------------------

        if (latestCall.number.isBlank()) {

            Log.d(
                TAG,
                "Ignored : Empty Number"
            )

            return
        }

        //--------------------------------------------------
        // Duration
        //--------------------------------------------------

        if (latestCall.duration < MIN_CALL_DURATION) {

            Log.d(
                TAG,
                "Ignored : Short Call"
            )

            return
        }

        //--------------------------------------------------
        // Call Type
        //--------------------------------------------------

        if (
            latestCall.type != CallLog.Calls.INCOMING_TYPE &&
            latestCall.type != CallLog.Calls.OUTGOING_TYPE
        ) {

            Log.d(
                TAG,
                "Ignored : Unsupported Call Type"
            )

            return
        }

        //--------------------------------------------------
        // Duplicate event protection
        //--------------------------------------------------

        if (isDuplicate(latestCall)) {

            Log.d(
                TAG,
                "Ignored : Duplicate Call Event"
            )

            return
        }

        //--------------------------------------------------
        // Check whether BOTH messages are already sent today
        //--------------------------------------------------

        val whatsappSent =
            DuplicateManager.alreadySentToday(
                context,
                latestCall.number,
                SentMessage.TYPE_WHATSAPP
            )

        val smsSent =
            DuplicateManager.alreadySentToday(
                context,
                latestCall.number,
                SentMessage.TYPE_SMS
            )

        if (whatsappSent && smsSent) {

            Log.d(
                TAG,
                "Ignored : WhatsApp and SMS already sent today"
            )

            return
        }

        //--------------------------------------------------
        // Valid New Call
        //--------------------------------------------------

        Log.d(
            TAG,
            "Valid New Call"
        )

        //--------------------------------------------------
        // Send actual call to Laravel
        //--------------------------------------------------

        LaravelSyncManager(
            context
        ).sync(
            latestCall
        )

        Log.d(
            TAG,
            "Processing Completed"
        )
    }

    //--------------------------------------------------
    // Duplicate Event
    //--------------------------------------------------

    private fun isDuplicate(
        call: CallData
    ): Boolean {

        val now =
            System.currentTimeMillis()

        val duplicate =
            lastNumber == call.number &&
                    (now - lastTime) < DUPLICATE_INTERVAL

        if (!duplicate) {

            lastNumber =
                call.number

            lastTime =
                now
        }

        return duplicate
    }

    //--------------------------------------------------
    // Call Type
    //--------------------------------------------------

    private fun getCallType(
        type: Int
    ): String {

        return when (type) {

            CallLog.Calls.INCOMING_TYPE ->
                "Incoming"

            CallLog.Calls.OUTGOING_TYPE ->
                "Outgoing"

            CallLog.Calls.MISSED_TYPE ->
                "Missed"

            CallLog.Calls.REJECTED_TYPE ->
                "Rejected"

            CallLog.Calls.BLOCKED_TYPE ->
                "Blocked"

            CallLog.Calls.VOICEMAIL_TYPE ->
                "Voicemail"

            else ->
                "Unknown"
        }
    }
}