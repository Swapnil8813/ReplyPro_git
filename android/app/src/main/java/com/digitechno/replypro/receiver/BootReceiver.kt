package com.digitechno.replypro.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.digitechno.replypro.queue.QueueRecovery

class BootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "BootReceiver"
    }

    override fun onReceive(
        context: Context,
        intent: Intent?
    ) {

        when (intent?.action) {

            //--------------------------------------------------
            // PHONE / APP RESTART
            //--------------------------------------------------

            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> {

                Log.d(
                    TAG,
                    "ReplyPro Restart Recovery"
                )

                QueueRecovery.recover(
                    context
                )
            }

            //--------------------------------------------------
            // PHONE UNLOCKED
            //--------------------------------------------------

            Intent.ACTION_USER_UNLOCKED -> {

                Log.d(
                    TAG,
                    "==================================="
                )

                Log.d(
                    TAG,
                    "ReplyPro: PHONE UNLOCKED"
                )

                Log.d(
                    TAG,
                    "Resuming pending queue..."
                )

                Log.d(
                    TAG,
                    "==================================="
                )

                QueueRecovery.recover(
                    context
                )
            }
        }
    }
}