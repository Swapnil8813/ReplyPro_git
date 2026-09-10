package com.digitechno.replypro.call

import android.content.Context
import android.provider.CallLog
import android.util.Log
import com.digitechno.replypro.model.CallData

object CallLogWaiter {

    private const val TAG = "CallLogWaiter"

    //--------------------------------------------------
    // EXISTING API
    //--------------------------------------------------

    fun waitForLatestCall(
        context: Context,
        expectedNumber: String,
        timeout: Long = 10000L
    ): CallData? {

        return waitForLatestCall(
            context = context,
            expectedNumber = expectedNumber,
            expectedType = null,
            afterTime = 0L,
            timeout = timeout
        )
    }

    //--------------------------------------------------
    // MAIN METHOD
    //--------------------------------------------------

    fun waitForLatestCall(
        context: Context,
        expectedNumber: String,
        expectedType: Int?,
        afterTime: Long,
        timeout: Long = 10000L
    ): CallData? {

        val resolver = context.contentResolver

        val startTime = System.currentTimeMillis()

        val cleanExpected =
            clean(expectedNumber)

        val outgoingMode =
            cleanExpected.isBlank() &&
                    expectedType == CallLog.Calls.OUTGOING_TYPE

        Log.d(TAG, "===================================")
        Log.d(TAG, "Waiting For Call Log")
        Log.d(TAG, "Expected Number : $cleanExpected")
        Log.d(TAG, "Expected Type   : $expectedType")
        Log.d(TAG, "Outgoing Mode   : $outgoingMode")
        Log.d(TAG, "After Time      : $afterTime")
        Log.d(TAG, "Timeout         : ${timeout}ms")
        Log.d(TAG, "===================================")

        while (
            System.currentTimeMillis() - startTime < timeout
        ) {

            try {

                Thread.sleep(500)

                resolver.query(
                    CallLog.Calls.CONTENT_URI,

                    arrayOf(
                        CallLog.Calls.NUMBER,
                        CallLog.Calls.TYPE,
                        CallLog.Calls.DURATION,
                        CallLog.Calls.DATE,
                        CallLog.Calls.CACHED_NAME
                    ),

                    null,
                    null,

                    "${CallLog.Calls.DATE} DESC"

                )?.use { cursor ->

                    var checked = 0

                    while (
                        cursor.moveToNext() &&
                        checked < 20
                    ) {

                        checked++

                        //--------------------------------------------------
                        // NUMBER
                        //--------------------------------------------------

                        val rawNumber =
                            cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                    CallLog.Calls.NUMBER
                                )
                            ) ?: ""

                        val number =
                            clean(rawNumber)

                        //--------------------------------------------------
                        // TYPE
                        //--------------------------------------------------

                        val type =
                            cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                    CallLog.Calls.TYPE
                                )
                            )

                        //--------------------------------------------------
                        // DURATION
                        //--------------------------------------------------

                        val duration =
                            cursor.getLong(
                                cursor.getColumnIndexOrThrow(
                                    CallLog.Calls.DURATION
                                )
                            )

                        //--------------------------------------------------
                        // DATE
                        //--------------------------------------------------

                        val date =
                            cursor.getLong(
                                cursor.getColumnIndexOrThrow(
                                    CallLog.Calls.DATE
                                )
                            )

                        //--------------------------------------------------
                        // CONTACT NAME
                        //--------------------------------------------------

                        val contactName =
                            cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                    CallLog.Calls.CACHED_NAME
                                )
                            ) ?: ""

                        //--------------------------------------------------
                        // TYPE CHECK
                        //--------------------------------------------------

                        val validType =
                            type == CallLog.Calls.INCOMING_TYPE ||
                                    type == CallLog.Calls.OUTGOING_TYPE

                        if (!validType) {
                            continue
                        }

                        //--------------------------------------------------
                        // EXPECTED TYPE CHECK
                        //--------------------------------------------------

                        if (
                            expectedType != null &&
                            type != expectedType
                        ) {
                            continue
                        }

                        //--------------------------------------------------
                        // RECENCY
                        //
                        // Samsung can write the call-log date slightly
                        // before/after the phone-state timestamp.
                        //--------------------------------------------------

                        val age =
                            System.currentTimeMillis() - date

                        val recent =
                            age >= -5000L &&
                                    age <= 60000L

                        if (!recent) {

                            Log.d(
                                TAG,
                                "Skipping old record #$checked : " +
                                        "number=$number age=${age}ms"
                            )

                            continue
                        }

                        //--------------------------------------------------
                        // OUTGOING CALL
                        //
                        // Android did not provide the number through
                        // PHONE_STATE, so use the newest recent
                        // outgoing call-log entry.
                        //--------------------------------------------------

                        if (outgoingMode) {

                            Log.d(
                                TAG,
                                "==================================="
                            )

                            Log.d(
                                TAG,
                                "OUTGOING CALL FOUND"
                            )

                            Log.d(
                                TAG,
                                "Number : $number"
                            )

                            Log.d(
                                TAG,
                                "Duration : $duration"
                            )

                            Log.d(
                                TAG,
                                "Date : $date"
                            )

                            Log.d(
                                TAG,
                                "Age : ${age}ms"
                            )

                            Log.d(
                                TAG,
                                "==================================="
                            )

                            return CallData(
                                number = number,
                                duration = duration,
                                type = type,
                                date = date,
                                contactName = contactName
                            )
                        }

                        //--------------------------------------------------
                        // KNOWN NUMBER
                        //--------------------------------------------------

                        val numberMatch =
                            number == cleanExpected

                        Log.d(
                            TAG,
                            "Record #$checked : " +
                                    "Number=$number " +
                                    "Type=$type " +
                                    "Duration=$duration " +
                                    "NumberMatch=$numberMatch " +
                                    "Age=${age}ms"
                        )

                        if (numberMatch) {

                            Log.d(
                                TAG,
                                "==================================="
                            )

                            Log.d(
                                TAG,
                                "CORRECT CALL FOUND"
                            )

                            Log.d(
                                TAG,
                                "Number : $number"
                            )

                            Log.d(
                                TAG,
                                "Duration : $duration"
                            )

                            Log.d(
                                TAG,
                                "Type : $type"
                            )

                            Log.d(
                                TAG,
                                "==================================="
                            )

                            return CallData(
                                number = number,
                                duration = duration,
                                type = type,
                                date = date,
                                contactName = contactName
                            )
                        }
                    }
                }

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "Call Log Query Failed",
                    e
                )
            }
        }

        //--------------------------------------------------
        // TIMEOUT
        //--------------------------------------------------

        Log.w(
            TAG,
            "==================================="
        )

        Log.w(
            TAG,
            "TIMEOUT WAITING FOR CALL"
        )

        Log.w(
            TAG,
            "Expected Number : $cleanExpected"
        )

        Log.w(
            TAG,
            "Expected Type   : $expectedType"
        )

        Log.w(
            TAG,
            "==================================="
        )

        return null
    }

    //--------------------------------------------------
    // CLEAN NUMBER
    //--------------------------------------------------

    private fun clean(
        number: String
    ): String {

        return number
            .replace(" ", "")
            .replace("-", "")
            .replace("(", "")
            .replace(")", "")
            .replace("+91", "")
            .replace("+", "")
            .trim()
    }
}