package com.digitechno.replypro.sync

data class CallSyncResponse(

    val status: Boolean,

    val customer: Customer,

    val actions: Actions,

    val messages: Messages,

    val attachment: Attachment

)

data class Customer(

    val name: String?,

    val mobile: String?

)

data class Actions(

    val send_whatsapp: Boolean,

    val send_sms: Boolean

)

data class Messages(

    val whatsapp: String?,

    val sms: String?

)

data class Attachment(

    val type: String?,

    val url: String?

)