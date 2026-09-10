package com.digitechno.replypro.accessibility

import android.util.Log
import com.digitechno.replypro.database.PendingMessage

object AutomationManager {

    private const val TAG = "AutomationManager"

    @Volatile
    private var currentTask: PendingMessage? = null

    //--------------------------------------------------
    // Set Room Task
    //--------------------------------------------------

    @Synchronized
    fun setCurrentTask(task: PendingMessage) {

        currentTask = task

        Log.d(
            TAG,
            "Automation Task Set"
        )

        Log.d(
            TAG,
            "ID : ${task.id}"
        )

        Log.d(
            TAG,
            "Mobile : ${task.mobile}"
        )
    }

    //--------------------------------------------------
    // Get Current Task
    //--------------------------------------------------

    fun getCurrentTask(): PendingMessage? {

        return currentTask
    }

    //--------------------------------------------------
    // Get Pending ID
    //--------------------------------------------------

    fun getCurrentPendingId(): Long {

        return currentTask?.id ?: 0L
    }

    //--------------------------------------------------
    // Is Running
    //--------------------------------------------------

    fun hasRunningTask(): Boolean {

        return currentTask != null
    }

    //--------------------------------------------------
    // Finish
    //--------------------------------------------------

    @Synchronized
    fun finishTask() {

        Log.d(
            TAG,
            "Automation Finished"
        )

        currentTask = null
    }

    //--------------------------------------------------
    // Clear
    //--------------------------------------------------

    @Synchronized
    fun clear() {

        currentTask = null
    }
}