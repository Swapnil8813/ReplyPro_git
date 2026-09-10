package com.digitechno.replypro.sync

data class CallSyncRequest(

    val api_key: String,

    val mobile_number: String,

    val contact_name: String,

    val call_type: String,

    val duration: Long,

    val call_date: Long,

    val device_id: String

)