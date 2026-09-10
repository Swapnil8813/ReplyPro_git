package com.digitechno.replypro.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.digitechno.replypro.R

object NotificationHelper {

    const val CHANNEL_ID = "replypro_service"

    fun createChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "ReplyPro Background Service",
                NotificationManager.IMPORTANCE_LOW
            )

            channel.description = "Monitors incoming and outgoing calls."

            val manager = context.getSystemService(
                NotificationManager::class.java
            )

            manager.createNotificationChannel(channel)
        }
    }

    fun getNotification(context: Context): Notification {

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("ReplyPro")
            .setContentText("Monitoring calls...")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

    }

}