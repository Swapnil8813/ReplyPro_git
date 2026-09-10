package com.digitechno.replypro.settings

data class SettingsRequest(

    val mobile_number: String,

    val whatsapp_enabled: Boolean,

    val whatsapp_message: String,

    val sms_enabled: Boolean,

    val sms_message: String

)