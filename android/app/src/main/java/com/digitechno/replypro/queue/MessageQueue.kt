package com.digitechno.replypro.queue

import android.util.Log
import java.util.concurrent.ConcurrentLinkedQueue

object MessageQueue {

    private const val TAG = "MessageQueue"

    private val queue = ConcurrentLinkedQueue<MessageTask>()

    fun enqueue(task: MessageTask) {

        queue.add(task)

        Log.d(TAG, "Task Added")

        Log.d(TAG, "Queue Size : ${queue.size}")

    }

    fun dequeue(): MessageTask? {

        val task = queue.poll()

        Log.d(TAG, "Task Removed")

        Log.d(TAG, "Queue Size : ${queue.size}")

        return task

    }

    fun hasTask(): Boolean {

        return queue.isNotEmpty()

    }

}