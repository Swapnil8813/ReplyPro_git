package com.digitechno.replypro.utils

import android.content.Context

class SessionManager(context: Context) {

    private val pref =
        context.getSharedPreferences("ReplyPro", Context.MODE_PRIVATE)

    fun saveLogin(mobile: String) {
        pref.edit()
            .putBoolean("isLoggedIn", true)
            .putString("mobile", mobile)
            .apply()
    }

    fun isLoggedIn(): Boolean {
        return pref.getBoolean("isLoggedIn", false)
    }

    fun getMobile(): String {
        return pref.getString("mobile", "") ?: ""
    }

    fun logout() {
        pref.edit().clear().apply()
    }
}