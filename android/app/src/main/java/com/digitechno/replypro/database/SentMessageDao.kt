package com.digitechno.replypro.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SentMessageDao {

    @Query("""
        SELECT sentTime
        FROM sent_messages
        WHERE mobile = :mobile
        AND messageType = :messageType
        ORDER BY sentTime DESC
        LIMIT 1
    """)
    fun getLastSentTime(
        mobile: String,
        messageType: String
    ): Long?

    @Insert
    fun insert(
        message: SentMessage
    )
}