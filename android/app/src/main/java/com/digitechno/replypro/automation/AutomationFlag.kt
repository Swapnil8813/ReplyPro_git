package com.digitechno.replypro.automation

import android.content.Context

object AutomationFlag {

    private const val PREF = "replypro"
    private const val KEY = "is_from_app"

    fun set(
        context: Context,
        value: Boolean
    ) {

        context
            .getSharedPreferences(
                PREF,
                Context.MODE_PRIVATE
            )
            .edit()
            .putBoolean(
                KEY,
                value
            )
            .apply()
    }

    fun isFromApp(
        context: Context
    ): Boolean {

        return context
            .getSharedPreferences(
                PREF,
                Context.MODE_PRIVATE
            )
            .getBoolean(
                KEY,
                false
            )
    }
}