package com.digitechno.replypro.whatsapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

class WhatsAppSender(
    private val context: Context
) {

    companion object {
        private const val TAG = "WhatsAppSender"
    }

    fun send(
        mobile: String,
        message: String
    ) {

        try {

            val number = mobile
                .replace("+", "")
                .replace(" ", "")

            val intent = Intent(
                Intent.ACTION_VIEW
            )

            intent.data = Uri.parse(
                "https://wa.me/$number?text=${Uri.encode(message)}"
            )

            intent.setPackage("com.whatsapp")

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(intent)

            Log.d(TAG, "WhatsApp Opened")

        } catch (e: Exception) {

            Log.e(TAG, "Error", e)

        }

    }

}