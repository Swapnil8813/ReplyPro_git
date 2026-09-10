package com.digitechno.replypro.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PendingMessageDao {

    @Insert
    fun insert(message: PendingMessage): Long

    @Update
    fun update(message: PendingMessage)

    @Query("""
        SELECT *
        FROM pending_messages
        WHERE status='PENDING'
        ORDER BY createdAt ASC
        LIMIT 1
    """)
    fun getNextPending(): PendingMessage?

    @Query("""
        SELECT *
        FROM pending_messages
        WHERE status='PROCESSING'
        ORDER BY updatedAt ASC
    """)
    fun getProcessing(): List<PendingMessage>

    @Query("""
        SELECT *
        FROM pending_messages
        WHERE id=:id
        LIMIT 1
    """)
    fun getById(id: Long): PendingMessage?

    @Query("""
        UPDATE pending_messages
        SET
            status=:status,
            updatedAt=:updatedAt
        WHERE id=:id
    """)
    fun updateStatus(
        id: Long,
        status: String,
        updatedAt: Long
    )

    //--------------------------------------------------
    // Retry Update
    //--------------------------------------------------

    @Query("""
        UPDATE pending_messages
        SET
            retryCount=:retryCount,
            lastError=:lastError,
            nextRetryAt=:nextRetryAt,
            updatedAt=:updatedAt
        WHERE id=:id
    """)
    fun updateRetry(
        id: Long,
        retryCount: Int,
        lastError: String?,
        nextRetryAt: Long,
        updatedAt: Long
    )

    //--------------------------------------------------
    // Reset Processing -> Pending
    //--------------------------------------------------

    @Query("""
        UPDATE pending_messages
        SET
            status='PENDING',
            updatedAt=:updatedAt
        WHERE id=:id
    """)
    fun resetToPending(
        id: Long,
        updatedAt: Long
    )

    //--------------------------------------------------
    // Dashboard Counts
    //--------------------------------------------------

    @Query("SELECT COUNT(*) FROM pending_messages WHERE status='PENDING'")
    fun pendingCount(): Int

    @Query("SELECT COUNT(*) FROM pending_messages WHERE status='PROCESSING'")
    fun processingCount(): Int

    @Query("SELECT COUNT(*) FROM pending_messages WHERE status='COMPLETED'")
    fun completedCount(): Int

    @Query("SELECT COUNT(*) FROM pending_messages WHERE status='FAILED'")
    fun failedCount(): Int

    //--------------------------------------------------
    // Cleanup
    //--------------------------------------------------

    @Query("""
        DELETE FROM pending_messages
        WHERE status='COMPLETED'
    """)
    fun deleteCompleted()

    @Query("""
        DELETE FROM pending_messages
        WHERE status='FAILED'
    """)
    fun deleteFailed()

}