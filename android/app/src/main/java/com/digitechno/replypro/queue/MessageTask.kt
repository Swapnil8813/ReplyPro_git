package com.digitechno.replypro.queue

data class MessageTask(

    val mobileNumber: String,

    val contactName: String,

    val whatsappEnabled: Boolean,

    val whatsappMessage: String?,

    val smsEnabled: Boolean,

    val smsMessage: String?,

    val attachmentUrl: String?,

    val attachmentType: String?

)