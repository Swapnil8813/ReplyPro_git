package com.digitechno.replypro.utils

import android.content.Context
import android.os.Build
import android.provider.Settings

object DeviceUtil {

    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    fun getDeviceName(): String {
        return "${Build.MANUFACTURER} ${Build.MODEL}"
    }

    fun getAppVersion(): String {
        return "1.0"
    }
}