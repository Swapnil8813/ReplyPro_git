package com.digitechno.replypro.call

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CallLog
import androidx.core.content.ContextCompat
import com.digitechno.replypro.model.CallData

class CallLogReader(
    private val context: Context
) {

    fun getLatestCall(): CallData? {

        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        val projection = arrayOf(
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION,
            CallLog.Calls.CACHED_NAME
        )

        val cursor = context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            projection,
            null,
            null,
            "${CallLog.Calls.DATE} DESC"
        )

        cursor?.use {

            if (it.moveToFirst()) {

                val number = it.getString(
                    it.getColumnIndexOrThrow(CallLog.Calls.NUMBER)
                ) ?: ""

                val duration = it.getLong(
                    it.getColumnIndexOrThrow(CallLog.Calls.DURATION)
                )

                val type = it.getInt(
                    it.getColumnIndexOrThrow(CallLog.Calls.TYPE)
                )

                val date = it.getLong(
                    it.getColumnIndexOrThrow(CallLog.Calls.DATE)
                )

                val name = it.getString(
                    it.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME)
                ) ?: ""

                return CallData(
                    number = number,
                    duration = duration,
                    type = type,
                    date = date,
                    contactName = name
                )
            }
        }

        return null
    }
}