package com.digitechno.replypro.whatsapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

class WhatsAppManager(
    private val context: Context
) {

    companion object {

        private const val TAG = "WhatsAppManager"

    }

    var state = WhatsAppState.IDLE

        private set

    fun openChat(
        mobile: String
    ) {

        state = WhatsAppState.OPENING

        val number = mobile
            .replace("+", "")
            .replace(" ", "")

        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://wa.me/$number")
        )

        intent.setPackage("com.whatsapp")

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(intent)

        Log.d(TAG, "Opening Chat")

    }

    fun chatOpened() {

        state = WhatsAppState.CHAT_OPENED

        Log.d(TAG, "Chat Opened")

    }

}