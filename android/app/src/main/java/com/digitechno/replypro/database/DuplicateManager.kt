package com.digitechno.replypro.database

import android.content.Context
import java.util.Calendar
import java.util.concurrent.Executors

object DuplicateManager {

    private val databaseExecutor =
        Executors.newSingleThreadExecutor()

    //--------------------------------------------------
    // Normalize Number
    //--------------------------------------------------

    private fun normalize(mobile: String): String {

        var number = mobile

        number = number
            .replace("+", "")
            .replace(" ", "")
            .replace("-", "")
            .replace("(", "")
            .replace(")", "")

        if (number.startsWith("91") && number.length > 10) {
            number = number.substring(2)
        }

        return number
    }

    //--------------------------------------------------
    // Check Specific Message Type
    //--------------------------------------------------

    fun alreadySent(
        context: Context,
        mobile: String,
        messageType: String
    ): Boolean {

        val number = normalize(mobile)

        val lastSent = ReplyProDatabase
            .get(context)
            .sentMessageDao()
            .getLastSentTime(
                number,
                messageType
            ) ?: return false

        return isToday(lastSent)
    }

    //--------------------------------------------------
    // Check WhatsApp
    //--------------------------------------------------

    fun whatsappAlreadySentToday(
        context: Context,
        mobile: String
    ): Boolean {

        return alreadySent(
            context,
            mobile,
            SentMessage.TYPE_WHATSAPP
        )
    }

    //--------------------------------------------------
    // Check SMS
    //--------------------------------------------------

    fun smsAlreadySentToday(
        context: Context,
        mobile: String
    ): Boolean {

        return alreadySent(
            context,
            mobile,
            SentMessage.TYPE_SMS
        )
    }

    //--------------------------------------------------
    // OLD COMPATIBILITY
    //--------------------------------------------------

    fun alreadySentToday(
        context: Context,
        mobile: String
    ): Boolean {

        return whatsappAlreadySentToday(
            context,
            mobile
        ) &&
                smsAlreadySentToday(
                    context,
                    mobile
                )
    }

    //--------------------------------------------------
    // COMPATIBILITY - 3 PARAMETERS
    //--------------------------------------------------

    fun alreadySentToday(
        context: Context,
        mobile: String,
        messageType: String
    ): Boolean {

        return alreadySent(
            context,
            mobile,
            messageType
        )
    }

    //--------------------------------------------------
    // Mark Message Sent
    //
    // IMPORTANT:
    // Room write MUST NOT happen on main thread.
    //--------------------------------------------------

    fun markSent(
        context: Context,
        mobile: String,
        messageType: String
    ) {

        val number = normalize(mobile)

        databaseExecutor.execute {

            try {

                ReplyProDatabase
                    .get(context.applicationContext)
                    .sentMessageDao()
                    .insert(

                        SentMessage(
                            mobile = number,
                            messageType = messageType,
                            sentTime = System.currentTimeMillis()
                        )

                    )

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }

    //--------------------------------------------------
    // Calendar Day Check
    //--------------------------------------------------

    private fun isToday(timestamp: Long): Boolean {

        val sentCalendar = Calendar.getInstance()
        sentCalendar.timeInMillis = timestamp

        val todayCalendar = Calendar.getInstance()

        return (
                sentCalendar.get(Calendar.YEAR) ==
                        todayCalendar.get(Calendar.YEAR)

                        &&

                        sentCalendar.get(Calendar.DAY_OF_YEAR) ==
                        todayCalendar.get(Calendar.DAY_OF_YEAR)
                )
    }
}