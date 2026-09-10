package com.digitechno.replypro.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sent_messages")
data class SentMessage(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val mobile: String,

    val messageType: String,

    val sentTime: Long
) {

    companion object {

        const val TYPE_WHATSAPP = "WHATSAPP"
        const val TYPE_SMS = "SMS"
    }
}