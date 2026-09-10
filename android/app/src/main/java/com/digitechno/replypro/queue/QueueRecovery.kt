package com.digitechno.replypro.queue

import android.content.Context
import android.util.Log

import com.digitechno.replypro.database.DuplicateManager
import com.digitechno.replypro.database.PendingMessage
import com.digitechno.replypro.database.SentMessage
import com.digitechno.replypro.database.ReplyProDatabase

object QueueRecovery {

    private const val TAG = "QueueRecovery"

    //--------------------------------------------------
    // Recover Queue After Restart / Unlock
    //--------------------------------------------------

    fun recover(
        context: Context
    ) {

        Thread {

            try {

                val dao =
                    ReplyProDatabase
                        .get(context)
                        .pendingMessageDao()

                val processing =
                    dao.getProcessing()

                Log.d(
                    TAG,
                    "==================================="
                )

                Log.d(
                    TAG,
                    "ReplyPro Queue Recovery"
                )

                Log.d(
                    TAG,
                    "Processing tasks : ${processing.size}"
                )

                Log.d(
                    TAG,
                    "==================================="
                )

                //--------------------------------------------------
                // NO PROCESSING TASK
                //--------------------------------------------------

                if (
                    processing.isEmpty()
                ) {

                    Log.d(
                        TAG,
                        "No interrupted task found."
                    )

                    /*
                     * There may still be normal PENDING tasks
                     * waiting in Room.
                     */

                    QueueManager.processNext(
                        context
                    )

                    return@Thread
                }

                //--------------------------------------------------
                // CHECK EACH PROCESSING TASK
                //--------------------------------------------------

                processing.forEach { task ->

                    val whatsappSent =
                        if (
                            task.whatsappEnabled &&
                            task.message.isNotBlank()
                        ) {

                            DuplicateManager.alreadySent(
                                context,
                                task.mobile,
                                SentMessage.TYPE_WHATSAPP
                            )

                        } else {

                            false
                        }

                    val smsSent =
                        if (
                            task.smsEnabled &&
                            task.smsMessage.isNotBlank()
                        ) {

                            DuplicateManager.alreadySent(
                                context,
                                task.mobile,
                                SentMessage.TYPE_SMS
                            )

                        } else {

                            false
                        }

                    Log.d(
                        TAG,
                        "-----------------------------------"
                    )

                    Log.d(
                        TAG,
                        "Recovery Task"
                    )

                    Log.d(
                        TAG,
                        "ID : ${task.id}"
                    )

                    Log.d(
                        TAG,
                        "Mobile : ${task.mobile}"
                    )

                    Log.d(
                        TAG,
                        "WhatsApp sent today : $whatsappSent"
                    )

                    Log.d(
                        TAG,
                        "SMS sent today : $smsSent"
                    )

                    //--------------------------------------------------
                    // WHATSAPP ALREADY SENT
                    //--------------------------------------------------

                    if (
                        whatsappSent
                    ) {

                        Log.d(
                            TAG,
                            "WhatsApp already sent."
                        )

                        /*
                         * If SMS is also already sent, the whole
                         * queue item is safely completed.
                         */

                        if (
                            !task.smsEnabled ||
                            smsSent
                        ) {

                            dao.updateStatus(

                                task.id,

                                PendingMessage.STATUS_COMPLETED,

                                System.currentTimeMillis()

                            )

                            Log.d(
                                TAG,
                                "Task already completed. No resend."
                            )

                            return@forEach
                        }

                        /*
                         * WhatsApp is already sent but SMS is not.
                         *
                         * Put the task back into PENDING so the
                         * existing QueueWorker can skip WhatsApp
                         * through duplicate protection and send
                         * only SMS.
                         */

                        dao.resetToPending(

                            task.id,

                            System.currentTimeMillis()

                        )

                        Log.d(
                            TAG,
                            "WhatsApp already sent."
                        )

                        Log.d(
                            TAG,
                            "SMS still pending."
                        )

                        Log.d(
                            TAG,
                            "Task reset to PENDING for SMS."
                        )

                        return@forEach
                    }

                    //--------------------------------------------------
                    // WHATSAPP NOT SENT
                    //--------------------------------------------------

                    /*
                     * IMPORTANT:
                     *
                     * No WhatsApp sent record exists.
                     *
                     * Therefore it is safe to resume this task.
                     */

                    dao.resetToPending(

                        task.id,

                        System.currentTimeMillis()

                    )

                    Log.d(
                        TAG,
                        "WhatsApp not confirmed as sent."
                    )

                    Log.d(
                        TAG,
                        "Task reset to PENDING."
                    )
                }

                //--------------------------------------------------
                // RESUME QUEUE
                //--------------------------------------------------

                Log.d(
                    TAG,
                    "==================================="
                )

                Log.d(
                    TAG,
                    "Starting recovered queue..."
                )

                Log.d(
                    TAG,
                    "==================================="
                )

                QueueManager.processNext(
                    context
                )

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "Recovery Failed",
                    e
                )
            }

        }.start()
    }

    //--------------------------------------------------
    // WATCHDOG
    //--------------------------------------------------

    fun startWatchdog(
        context: Context
    ) {

        /*
         * DO NOT reset PROCESSING automatically.
         *
         * Recovery is responsible for deciding whether a
         * PROCESSING task was actually sent.
         */

        Log.d(
            TAG,
            "Watchdog started"
        )
    }

    //--------------------------------------------------
    // STOP WATCHDOG
    //--------------------------------------------------

    fun stopWatchdog() {

        Log.d(
            TAG,
            "Watchdog stopped"
        )
    }
}