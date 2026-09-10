package com.digitechno.replypro.api

data class ActivationRequest(

    val api_key: String,

    val mobile_number: String,

    val device_id: String,

    val device_name: String,

    val app_version: String

)