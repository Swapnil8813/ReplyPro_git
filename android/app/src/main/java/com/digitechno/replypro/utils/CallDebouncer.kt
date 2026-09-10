package com.digitechno.replypro.utils

object CallDebouncer {

    private var lastNumber = ""

    private var lastTimestamp = 0L

    private const val DEBOUNCE_TIME = 5000L

    @Synchronized
    fun shouldProcess(number: String): Boolean {

        val now = System.currentTimeMillis()

        if (
            lastNumber == number &&
            now - lastTimestamp < DEBOUNCE_TIME
        ) {
            return false
        }

        lastNumber = number
        lastTimestamp = now

        return true
    }

    fun reset() {

        lastNumber = ""

        lastTimestamp = 0L

    }

}