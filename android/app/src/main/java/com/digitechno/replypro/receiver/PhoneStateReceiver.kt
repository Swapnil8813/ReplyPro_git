package com.digitechno.replypro.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast

class PhoneStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

        Log.d("ReplyPro", "State = $state")
        Toast.makeText(context, "State = $state", Toast.LENGTH_SHORT).show()

    }
}