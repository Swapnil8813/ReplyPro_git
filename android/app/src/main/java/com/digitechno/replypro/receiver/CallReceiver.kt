package com.digitechno.replypro.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.digitechno.replypro.call.CallLogWaiter
import com.digitechno.replypro.processor.CallProcessor
import com.digitechno.replypro.utils.CallDebouncer
import android.provider.CallLog

class CallReceiver : BroadcastReceiver() {

    companion object {

        private const val TAG =
            "CallReceiver"

        private var lastState =
            TelephonyManager.CALL_STATE_IDLE

        private var currentNumber =
            ""

        private var callStartTime =
            0L

        private var currentCallType =
            CallLogType.UNKNOWN
    }

    //--------------------------------------------------
    // INTERNAL CALL TYPE
    //--------------------------------------------------

    private enum class CallLogType {
        INCOMING,
        OUTGOING,
        UNKNOWN
    }

    //--------------------------------------------------
    // RECEIVE
    //--------------------------------------------------

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        if (
            intent.action !=
            TelephonyManager.ACTION_PHONE_STATE_CHANGED
        ) {
            return
        }

        val state =
            intent.getStringExtra(
                TelephonyManager.EXTRA_STATE
            ) ?: return

        val incomingNumber =
            intent.getStringExtra(
                TelephonyManager.EXTRA_INCOMING_NUMBER
            )

        //--------------------------------------------------
        // RINGING
        //--------------------------------------------------

        if (
            state ==
            TelephonyManager.EXTRA_STATE_RINGING
        ) {

            if (
                !incomingNumber.isNullOrBlank()
            ) {

                currentNumber =
                    incomingNumber

                currentCallType =
                    CallLogType.INCOMING

            }

            lastState =
                TelephonyManager.CALL_STATE_RINGING

            Log.d(
                TAG,
                "Incoming Ringing : $currentNumber"
            )

            return
        }

        //--------------------------------------------------
        // OFFHOOK
        //--------------------------------------------------

        if (
            state ==
            TelephonyManager.EXTRA_STATE_OFFHOOK
        ) {

            lastState =
                TelephonyManager.CALL_STATE_OFFHOOK

            callStartTime =
                System.currentTimeMillis()

            //--------------------------------------------------
            // If number is already known:
            // answered incoming call
            //--------------------------------------------------

            if (
                currentNumber.isNotBlank()
            ) {

                currentCallType =
                    CallLogType.INCOMING

                Log.d(
                    TAG,
                    "Incoming Call Connected : $currentNumber"
                )

            } else {

                //--------------------------------------------------
                // No incoming number = OUTGOING call
                //--------------------------------------------------

                currentCallType =
                    CallLogType.OUTGOING

                Log.d(
                    TAG,
                    "Outgoing Call Connected"
                )

            }

            Log.d(
                TAG,
                "Call Start Time : $callStartTime"
            )

            return
        }

        //--------------------------------------------------
        // IDLE / CALL END
        //--------------------------------------------------

        if (
            state ==
            TelephonyManager.EXTRA_STATE_IDLE
        ) {

            if (
                lastState !=
                TelephonyManager.CALL_STATE_OFFHOOK
            ) {

                //--------------------------------------------------
                // Example:
                // incoming call rejected / missed
                //
                // No processing.
                //--------------------------------------------------

                Log.d(
                    TAG,
                    "Call ended without OFFHOOK. Ignoring."
                )

                lastState =
                    TelephonyManager.CALL_STATE_IDLE

                currentNumber = ""

                currentCallType =
                    CallLogType.UNKNOWN

                callStartTime = 0L

                return
            }

            //--------------------------------------------------
            // CAPTURE EVERYTHING BEFORE THREAD
            //--------------------------------------------------

            val numberToProcess =
                currentNumber

            val typeToProcess =
                currentCallType

            val startTimeToProcess =
                callStartTime

            //--------------------------------------------------
            // LOG
            //--------------------------------------------------

            Log.d(
                TAG,
                "==================================="
            )

            Log.d(
                TAG,
                "CALL ENDED"
            )

            Log.d(
                TAG,
                "Captured Number : $numberToProcess"
            )

            Log.d(
                TAG,
                "Captured Type   : $typeToProcess"
            )

            Log.d(
                TAG,
                "Start Time      : $startTimeToProcess"
            )

            Log.d(
                TAG,
                "==================================="
            )

            //--------------------------------------------------
            // CLEAR RECEIVER STATE
            //--------------------------------------------------

            lastState =
                TelephonyManager.CALL_STATE_IDLE

            currentNumber = ""

            currentCallType =
                CallLogType.UNKNOWN

            callStartTime = 0L

            //--------------------------------------------------
            // DEBOUNCE
            //
            // For outgoing calls number is unknown here,
            // so DON'T debounce using empty number.
            //--------------------------------------------------

            if (
                typeToProcess ==
                CallLogType.INCOMING
            ) {

                if (
                    numberToProcess.isBlank()
                ) {

                    Log.w(
                        TAG,
                        "Incoming call number is empty"
                    )

                    return
                }

                if (
                    !CallDebouncer.shouldProcess(
                        numberToProcess
                    )
                ) {

                    Log.d(
                        TAG,
                        "Duplicate Call Ignored : $numberToProcess"
                    )

                    return
                }
            }

            //--------------------------------------------------
            // BACKGROUND PROCESS
            //--------------------------------------------------

            Thread {

                try {

                    Log.d(
                        TAG,
                        "==================================="
                    )

                    Log.d(
                        TAG,
                        "Waiting For Call Log..."
                    )

                    Log.d(
                        TAG,
                        "Number : $numberToProcess"
                    )

                    Log.d(
                        TAG,
                        "Type : $typeToProcess"
                    )

                    Log.d(
                        TAG,
                        "==================================="
                    )

                    //--------------------------------------------------
                    // EXPECTED TYPE
                    //--------------------------------------------------

                    val expectedType =
                        when (
                            typeToProcess
                        ) {

                            CallLogType.INCOMING ->
                                CallLog.Calls.INCOMING_TYPE

                            CallLogType.OUTGOING ->
                                CallLog.Calls.OUTGOING_TYPE

                            else ->
                                null
                        }

                    //--------------------------------------------------
                    // WAIT FOR CALL LOG
                    //--------------------------------------------------

                    val callData =
                        CallLogWaiter.waitForLatestCall(

                            context = context,

                            expectedNumber =
                                numberToProcess,

                            expectedType =
                                expectedType,

                            afterTime =
                                startTimeToProcess,

                            timeout =
                                10000L
                        )

                    //--------------------------------------------------
                    // FOUND
                    //--------------------------------------------------

                    if (
                        callData != null
                    ) {

                        Log.d(
                            TAG,
                            "==================================="
                        )

                        Log.d(
                            TAG,
                            "CALL LOG FOUND"
                        )

                        Log.d(
                            TAG,
                            "Number : ${callData.number}"
                        )

                        Log.d(
                            TAG,
                            "Duration : ${callData.duration}"
                        )

                        Log.d(
                            TAG,
                            "Type : ${callData.type}"
                        )

                        Log.d(
                            TAG,
                            "==================================="
                        )

                        //--------------------------------------------------
                        // OUTGOING DEBOUNCE
                        //
                        // Now we finally know the number.
                        //--------------------------------------------------

                        if (
                            typeToProcess ==
                            CallLogType.OUTGOING
                        ) {

                            if (
                                !CallDebouncer.shouldProcess(
                                    callData.number
                                )
                            ) {

                                Log.d(
                                    TAG,
                                    "Duplicate outgoing call ignored : ${callData.number}"
                                )

                                return@Thread
                            }
                        }

                        //--------------------------------------------------
                        // PROCESS
                        //--------------------------------------------------

                        CallProcessor(
                            context
                        ).process(
                            callData
                        )

                    } else {

                        Log.w(
                            TAG,
                            "Could not find new call in call log"
                        )
                    }

                } catch (
                    e: Exception
                ) {

                    Log.e(
                        TAG,
                        "Call Processing Failed",
                        e
                    )
                }

            }.start()
        }
    }
}