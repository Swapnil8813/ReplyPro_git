package com.digitechno.replypro.api

data class ActivationResponse(
    val status: Boolean,
    val message: String,
    val code: String?
)