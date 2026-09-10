package com.digitechno.replypro.sync

import android.content.Context
import android.provider.CallLog
import android.util.Log
import com.digitechno.replypro.api.RetrofitClient
import com.digitechno.replypro.database.DuplicateManager
import com.digitechno.replypro.database.PendingMessage
import com.digitechno.replypro.database.SentMessage
import com.digitechno.replypro.model.CallData
import com.digitechno.replypro.queue.QueueManager
import com.digitechno.replypro.utils.DeviceUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LaravelSyncManager(
    private val context: Context
) {

    companion object {

        private const val TAG = "LaravelSync"

        private const val API_KEY = "REPLYPRO_V1_SECRET"
    }

    //--------------------------------------------------
    // Sync Actual Call
    //--------------------------------------------------

    fun sync(callData: CallData) {

        Log.d(TAG, "===================================")
        Log.d(TAG, "Laravel Sync Started")
        Log.d(TAG, "Number : ${callData.number}")
        Log.d(TAG, "Contact : ${callData.contactName}")
        Log.d(TAG, "Duration : ${callData.duration}")
        Log.d(TAG, "===================================")

        //--------------------------------------------------
        // Check today's status separately
        //--------------------------------------------------

        val whatsappAlreadySent =
            DuplicateManager.alreadySentToday(
                context,
                callData.number,
                SentMessage.TYPE_WHATSAPP
            )

        val smsAlreadySent =
            DuplicateManager.alreadySentToday(
                context,
                callData.number,
                SentMessage.TYPE_SMS
            )

        //--------------------------------------------------
        // Nothing left to send
        //--------------------------------------------------

        if (whatsappAlreadySent && smsAlreadySent) {

            Log.d(
                TAG,
                "WhatsApp and SMS already sent today. Skipping."
            )

            return
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
        // Device
        //--------------------------------------------------

        Log.d(
            TAG,
            "Device ID = ${DeviceUtil.getDeviceId(context)}"
        )

        //--------------------------------------------------
        // API Request
        //--------------------------------------------------

        val request = CallSyncRequest(

            api_key = API_KEY,

            mobile_number = callData.number,

            contact_name = callData.contactName,

            call_type = getCallType(callData.type),

            duration = callData.duration,

            call_date = callData.date,

            device_id = DeviceUtil.getDeviceId(context)

        )

        RetrofitClient.apiService
            .syncCall(request)
            .enqueue(object : Callback<CallSyncResponse> {

                override fun onResponse(
                    call: Call<CallSyncResponse>,
                    response: Response<CallSyncResponse>
                ) {

                    if (!response.isSuccessful) {

                        Log.e(
                            TAG,
                            "Server Error : ${response.code()}"
                        )

                        return
                    }

                    val result = response.body()

                    if (result == null) {

                        Log.e(
                            TAG,
                            "Empty Response"
                        )

                        return
                    }

                    Log.d(TAG, "========== SERVER ==========")
                    Log.d(TAG, "Status : ${result.status}")

                    if (!result.status) {
                        return
                    }

                    //--------------------------------------------------
                    // Server Settings
                    //--------------------------------------------------

                    val serverWhatsApp =
                        result.actions.send_whatsapp

                    val serverSms =
                        result.actions.send_sms

                    Log.d(
                        TAG,
                        "WhatsApp Enabled : $serverWhatsApp"
                    )

                    Log.d(
                        TAG,
                        "SMS Enabled : $serverSms"
                    )

                    //--------------------------------------------------
                    // Final channel decision
                    //--------------------------------------------------

                    val shouldSendWhatsApp =
                        serverWhatsApp &&
                                !whatsappAlreadySent

                    val shouldSendSms =
                        serverSms &&
                                !smsAlreadySent

                    //--------------------------------------------------
                    // Nothing to queue
                    //--------------------------------------------------

                    if (!shouldSendWhatsApp && !shouldSendSms) {

                        Log.d(
                            TAG,
                            "No messages need to be sent."
                        )

                        return
                    }

                    //--------------------------------------------------
                    // Create Pending Message
                    //--------------------------------------------------

                    val pending = PendingMessage(

                        mobile = callData.number,

                        contactName = callData.contactName,

                        message =
                            result.messages.whatsapp ?: "",

                        whatsappEnabled =
                            shouldSendWhatsApp,

                        smsEnabled =
                            shouldSendSms,

                        smsMessage =
                            result.messages.sms ?: "",

                        attachmentUrl =
                            result.attachment.url,

                        attachmentType =
                            result.attachment.type
                    )

                    //--------------------------------------------------
                    // Add to Queue
                    //--------------------------------------------------

                    QueueManager.enqueue(

                        context,

                        pending
                    )

                    Log.d(
                        TAG,
                        "Pending Message Added"
                    )

                    Log.d(
                        TAG,
                        "WhatsApp : $shouldSendWhatsApp"
                    )

                    Log.d(
                        TAG,
                        "SMS : $shouldSendSms"
                    )
                }

                override fun onFailure(
                    call: Call<CallSyncResponse>,
                    t: Throwable
                ) {

                    Log.e(
                        TAG,
                        t.message ?: "Unknown Error"
                    )
                }
            })
    }

    //--------------------------------------------------
    // Call Type
    //--------------------------------------------------

    private fun getCallType(type: Int): String {

        return when (type) {

            CallLog.Calls.INCOMING_TYPE ->
                "Incoming"

            CallLog.Calls.OUTGOING_TYPE ->
                "Outgoing"

            CallLog.Calls.REJECTED_TYPE ->
                "Rejected"

            CallLog.Calls.BLOCKED_TYPE ->
                "Blocked"

            else ->
                "Unknown"
        }
    }
}