package com.digitechno.replypro.settings

data class SettingsResponse(

    val status: Boolean,

    val settings: Settings

)

data class Settings(

    val whatsapp_enabled: Boolean,

    val whatsapp_message: String?,

    val whatsapp_attachment: String?,

    val attachment_type: String?,

    val sms_enabled: Boolean,

    val sms_message: String?

)