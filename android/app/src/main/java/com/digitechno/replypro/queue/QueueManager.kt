package com.digitechno.replypro.queue

import android.content.Context
import android.util.Log
import com.digitechno.replypro.database.PendingMessage
import com.digitechno.replypro.database.ReplyProDatabase

object QueueManager {

    private const val TAG = "QueueManager"

    private const val MAX_RETRY = 3

    @Volatile
    private var processing = false

    //--------------------------------------------------
    // ADD NEW TASK
    //--------------------------------------------------

    fun enqueue(
        context: Context,
        message: PendingMessage
    ) {

        Thread {

            try {

                val dao =
                    ReplyProDatabase
                        .get(context)
                        .pendingMessageDao()

                dao.insert(message)

                Log.d(
                    TAG,
                    "==================================="
                )

                Log.d(
                    TAG,
                    "Added To Queue"
                )

                Log.d(
                    TAG,
                    "Mobile : ${message.mobile}"
                )

                Log.d(
                    TAG,
                    "==================================="
                )

                /*
                 * IMPORTANT:
                 *
                 * Always call processNext().
                 *
                 * If another task is already processing,
                 * processNext() safely returns.
                 *
                 * When that task finishes, completed()
                 * calls processNext() again.
                 */

                processNext(context)

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "Enqueue Error",
                    e
                )
            }

        }.start()
    }

    //--------------------------------------------------
    // PROCESS NEXT
    //--------------------------------------------------

    @Synchronized
    fun processNext(
        context: Context
    ) {

        /*
         * Only one queue worker at a time.
         */

        if (processing) {

            Log.d(
                TAG,
                "Queue Already Processing"
            )

            return
        }

        processing = true

        Thread {

            try {

                val dao =
                    ReplyProDatabase
                        .get(context)
                        .pendingMessageDao()

                /*
                 * Find the oldest pending task.
                 */

                val next =
                    dao.getNextPending()

                if (next == null) {

                    processing = false

                    Log.d(
                        TAG,
                        "Queue Empty"
                    )

                    return@Thread
                }

                /*
                 * Mark it PROCESSING before handing it
                 * to QueueWorker.
                 */

                dao.updateStatus(

                    next.id,

                    PendingMessage.STATUS_PROCESSING,

                    System.currentTimeMillis()

                )

                Log.d(
                    TAG,
                    "==================================="
                )

                Log.d(
                    TAG,
                    "Processing Queue"
                )

                Log.d(
                    TAG,
                    "Pending ID : ${next.id}"
                )

                Log.d(
                    TAG,
                    "Mobile : ${next.mobile}"
                )

                Log.d(
                    TAG,
                    "Retry : ${next.retryCount}"
                )

                Log.d(
                    TAG,
                    "==================================="
                )

                /*
                 * QueueWorker owns the task from this point.
                 *
                 * SMS-only tasks call completed() themselves.
                 *
                 * WhatsApp tasks call completed() from
                 * ReplyProAccessibilityService after Send.
                 */

                QueueWorker.process(

                    context,

                    next

                )

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "Queue Error",
                    e
                )

                /*
                 * Never leave the in-memory queue lock stuck
                 * because of an unexpected exception.
                 */

                processing = false
            }

        }.start()
    }

    //--------------------------------------------------
    // TASK COMPLETED
    //--------------------------------------------------

    fun completed(
        context: Context,
        id: Long
    ) {

        Thread {

            try {

                val dao =
                    ReplyProDatabase
                        .get(context)
                        .pendingMessageDao()

                if (id > 0L) {

                    dao.updateStatus(

                        id,

                        PendingMessage.STATUS_COMPLETED,

                        System.currentTimeMillis()

                    )

                    Log.d(
                        TAG,
                        "Pending task marked COMPLETED"
                    )

                    Log.d(
                        TAG,
                        "Completed ID : $id"
                    )
                }

                /*
                 * IMPORTANT:
                 *
                 * Release queue lock BEFORE starting
                 * the next task.
                 */

                synchronized(this) {

                    processing = false
                }

                Log.d(
                    TAG,
                    "Queue Processing Lock Released"
                )

                /*
                 * Immediately continue with next queued item.
                 */

                processNext(context)

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "Completion Error",
                    e
                )

                /*
                 * Even if database completion fails,
                 * don't leave the queue permanently locked.
                 */

                synchronized(this) {

                    processing = false
                }

                /*
                 * Try the queue again.
                 */

                processNext(context)
            }

        }.start()
    }

    //--------------------------------------------------
    // RETRY FAILED TASK
    //--------------------------------------------------

    fun retry(
        context: Context,
        pending: PendingMessage,
        error: String
    ) {

        Thread {

            try {

                val dao =
                    ReplyProDatabase
                        .get(context)
                        .pendingMessageDao()

                val retry =
                    pending.retryCount + 1

                if (
                    retry >= MAX_RETRY
                ) {

                    dao.updateStatus(

                        pending.id,

                        PendingMessage.STATUS_FAILED,

                        System.currentTimeMillis()

                    )

                    Log.e(
                        TAG,
                        "==================================="
                    )

                    Log.e(
                        TAG,
                        "Task Failed"
                    )

                    Log.e(
                        TAG,
                        "Mobile : ${pending.mobile}"
                    )

                    Log.e(
                        TAG,
                        "Reason : $error"
                    )

                    Log.e(
                        TAG,
                        "==================================="
                    )

                } else {

                    dao.updateRetry(

                        pending.id,

                        retry,

                        error,

                        System.currentTimeMillis() + 30000,

                        System.currentTimeMillis()

                    )

                    dao.resetToPending(

                        pending.id,

                        System.currentTimeMillis()

                    )

                    Log.d(
                        TAG,
                        "==================================="
                    )

                    Log.d(
                        TAG,
                        "Retry Scheduled"
                    )

                    Log.d(
                        TAG,
                        "Mobile : ${pending.mobile}"
                    )

                    Log.d(
                        TAG,
                        "Retry : $retry"
                    )

                    Log.d(
                        TAG,
                        "==================================="
                    )
                }

                synchronized(this) {

                    processing = false
                }

                /*
                 * Continue with next queue item.
                 */

                processNext(context)

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "Retry Error",
                    e
                )

                synchronized(this) {

                    processing = false
                }

                processNext(context)
            }

        }.start()
    }
}