package com.digitechno.replypro.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_messages")
data class PendingMessage(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val mobile: String,

    val contactName: String?,

    val message: String,
    val whatsappEnabled: Boolean = true,

    val smsEnabled: Boolean = false,

    val smsMessage: String = "",

    val attachmentUrl: String?,

    val attachmentType: String?,

    val status: String = STATUS_PENDING,

    // Retry Information
    val retryCount: Int = 0,

    val lastError: String? = null,

    val nextRetryAt: Long = 0L,

    // Timestamps
    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long = System.currentTimeMillis()

) {

    companion object {

        const val STATUS_PENDING = "PENDING"

        const val STATUS_PROCESSING = "PROCESSING"

        const val STATUS_COMPLETED = "COMPLETED"

        const val STATUS_FAILED = "FAILED"

    }

}