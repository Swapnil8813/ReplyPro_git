package com.digitechno.replypro.accessibility

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent

import com.digitechno.replypro.automation.AutomationFlag
import com.digitechno.replypro.automation.PendingAttachmentHolder
import com.digitechno.replypro.database.DuplicateManager
import com.digitechno.replypro.database.SentMessage
import com.digitechno.replypro.queue.QueueManager
import com.digitechno.replypro.queue.QueueRecovery

class ReplyProAccessibilityService :
    AccessibilityService() {

    companion object {

        private const val TAG =
            "ReplyProAccessibilityService"

        /*
         * Keep polling reasonably frequently.
         *
         * WhatsApp media composer can take several seconds
         * to expose caption/send nodes.
         */
        private const val POLL_DELAY = 600L

        /*
         * Initial delay after WhatsApp becomes active.
         */
        private const val START_DELAY = 900L

        /*
         * Give WhatsApp enough time to finish opening the
         * media composer.
         */
        private const val MAX_WAIT_TIME = 30000L

        /*
         * After clicking Send, don't immediately assume that
         * WhatsApp actually left the composer.
         */
        private const val SEND_VERIFY_DELAY = 1200L
    }

    private val handler =
        Handler(Looper.getMainLooper())

    @Volatile
    private var polling = false

    @Volatile
    private var sendLocked = false

    @Volatile
    private var completingTask = false

    private var automationStarted = false

    private var automationStartTime = 0L

    //--------------------------------------------------
    // SERVICE CONNECTED
    //--------------------------------------------------

    override fun onServiceConnected() {

        super.onServiceConnected()

        Log.d(
            TAG,
            "==================================="
        )

        Log.d(
            TAG,
            "ReplyPro Accessibility Connected"
        )

        Log.d(
            TAG,
            "==================================="
        )
    }

    //--------------------------------------------------
    // ACCESSIBILITY EVENT
    //--------------------------------------------------

    override fun onAccessibilityEvent(
        event: AccessibilityEvent?
    ) {

        if (event == null) {
            return
        }

        /*
         * ReplyPro automation must be active.
         */
        if (
            !AutomationFlag.isFromApp(this)
        ) {

            stopPolling()

            return
        }

        val packageName =
            event.packageName
                ?.toString()
                ?: return

        val isWhatsApp =
            packageName.equals(
                "com.whatsapp",
                ignoreCase = true
            ) ||
                    packageName.equals(
                        "com.whatsapp.w4b",
                        ignoreCase = true
                    )

        if (!isWhatsApp) {
            return
        }

        /*
         * IMPORTANT:
         *
         * Every WhatsApp event can restart/refresh the polling
         * process if necessary.
         *
         * This prevents the second queued call from getting
         * stuck while WhatsApp is transitioning screens.
         */
        startPolling()
    }

    //--------------------------------------------------
    // START POLLING
    //--------------------------------------------------

    @Synchronized
    private fun startPolling() {

        if (sendLocked) {
            return
        }

        if (
            !AutomationFlag.isFromApp(this)
        ) {
            return
        }

        /*
         * Already polling.
         */
        if (polling) {
            return
        }

        val task =
            AutomationManager
                .getCurrentTask()

        if (task == null) {

            Log.d(
                TAG,
                "Cannot start polling - no current task"
            )

            return
        }

        polling = true

        automationStarted = true

        automationStartTime =
            System.currentTimeMillis()

        Log.d(
            TAG,
            "==================================="
        )

        Log.d(
            TAG,
            "WhatsApp Accessibility Polling Started"
        )

        Log.d(
            TAG,
            "Pending ID : ${task.id}"
        )

        Log.d(
            TAG,
            "Mobile : ${task.mobile}"
        )

        Log.d(
            TAG,
            "==================================="
        )

        handler.postDelayed(
            {
                pollWhatsApp()
            },
            START_DELAY
        )
    }

    //--------------------------------------------------
    // POLL WHATSAPP
    //--------------------------------------------------

    private fun pollWhatsApp() {

        if (sendLocked) {

            stopPolling()

            return
        }

        if (completingTask) {

            stopPolling()

            return
        }

        /*
         * If automation was cancelled, stop immediately.
         */
        if (
            !AutomationFlag.isFromApp(this)
        ) {

            Log.d(
                TAG,
                "Automation flag disabled - stopping"
            )

            stopPolling()

            return
        }

        val task =
            AutomationManager
                .getCurrentTask()

        if (task == null) {

            Log.d(
                TAG,
                "No active AutomationManager task"
            )

            stopPolling()

            return
        }

        //--------------------------------------------------
        // SAFETY TIMEOUT
        //--------------------------------------------------

        val elapsed =
            System.currentTimeMillis() -
                    automationStartTime

        if (
            automationStartTime > 0L &&
            elapsed > MAX_WAIT_TIME
        ) {

            Log.e(
                TAG,
                "WhatsApp automation timeout"
            )

            Log.e(
                TAG,
                "Mobile : ${task.mobile}"
            )

            /*
             * Do NOT mark WhatsApp as sent.
             *
             * Let QueueRecovery / retry logic handle
             * the interrupted task.
             */

            stopPolling()

            return
        }

        //--------------------------------------------------
        // ACTIVE WINDOW
        //--------------------------------------------------

        val root =
            rootInActiveWindow

        if (root == null) {

            Log.d(
                TAG,
                "WhatsApp root is null - retrying"
            )

            scheduleNextPoll()

            return
        }

        val packageName =
            root.packageName
                ?.toString()
                ?: ""

        val isWhatsApp =
            packageName.equals(
                "com.whatsapp",
                ignoreCase = true
            ) ||
                    packageName.equals(
                        "com.whatsapp.w4b",
                        ignoreCase = true
                    )

        if (!isWhatsApp) {

            Log.d(
                TAG,
                "Active window is not WhatsApp: $packageName"
            )

            /*
             * IMPORTANT:
             *
             * Do NOT terminate the automation.
             * WhatsApp can temporarily have another window.
             */

            scheduleNextPoll()

            return
        }

        Log.d(
            TAG,
            "==================================="
        )

        Log.d(
            TAG,
            "WhatsApp Window Active"
        )

        Log.d(
            TAG,
            "Package : $packageName"
        )

        Log.d(
            TAG,
            "Mobile  : ${task.mobile}"
        )

        Log.d(
            TAG,
            "Elapsed : ${elapsed}ms"
        )

        Log.d(
            TAG,
            "==================================="
        )

        //--------------------------------------------------
        // ALLOW
        //--------------------------------------------------

        if (
            AccessibilityUtils.clickByText(
                root,
                "Allow"
            )
        ) {

            Log.d(
                TAG,
                "Clicked Allow"
            )

            scheduleNextPoll()

            return
        }

        //--------------------------------------------------
        // CONTINUE
        //--------------------------------------------------

        if (
            AccessibilityUtils.clickByText(
                root,
                "Continue"
            )
        ) {

            Log.d(
                TAG,
                "Clicked Continue"
            )

            scheduleNextPoll()

            return
        }

        //--------------------------------------------------
        // OK
        //--------------------------------------------------

        if (
            AccessibilityUtils.clickByText(
                root,
                "OK"
            )
        ) {

            Log.d(
                TAG,
                "Clicked OK"
            )

            scheduleNextPoll()

            return
        }

        //--------------------------------------------------
        // USE THIS CHAT
        //--------------------------------------------------

        if (
            AccessibilityUtils.clickByText(
                root,
                "Use this chat"
            )
        ) {

            Log.d(
                TAG,
                "Clicked Use this chat"
            )

            scheduleNextPoll()

            return
        }

        //--------------------------------------------------
        // MEDIA FLOW
        //--------------------------------------------------

        val attachment =
            PendingAttachmentHolder.uri

        val message =
            PendingAttachmentHolder.message

        if (attachment != null) {

            Log.d(
                TAG,
                "Media automation active"
            )

            //--------------------------------------------------
            // CAPTION
            //--------------------------------------------------

            if (
                !PendingAttachmentHolder.captionInserted &&
                !message.isNullOrBlank()
            ) {

                Log.d(
                    TAG,
                    "Trying to insert caption..."
                )

                val inserted =
                    AccessibilityUtils.enterText(
                        root,
                        message
                    )

                if (inserted) {

                    PendingAttachmentHolder
                        .captionInserted = true

                    Log.d(
                        TAG,
                        "==================================="
                    )

                    Log.d(
                        TAG,
                        "CAPTION INSERTED"
                    )

                    Log.d(
                        TAG,
                        "Waiting for Send button..."
                    )

                    Log.d(
                        TAG,
                        "==================================="
                    )

                    /*
                     * Don't immediately try Send in the same
                     * accessibility tree.
                     *
                     * Give WhatsApp a moment to redraw the
                     * media composer.
                     */

                    handler.postDelayed(
                        {
                            if (
                                !sendLocked &&
                                !completingTask &&
                                AutomationFlag.isFromApp(
                                    this
                                )
                            ) {

                                pollWhatsApp()
                            }
                        },
                        500L
                    )

                    return
                }

                Log.d(
                    TAG,
                    "Caption not available yet"
                )

                scheduleNextPoll()

                return
            }

            //--------------------------------------------------
            // SEND
            //--------------------------------------------------

            Log.d(
                TAG,
                "Checking media Send button..."
            )

            val clicked =
                AccessibilityUtils.clickSendButton(
                    root
                )

            if (clicked) {

                /*
                 * LOCK IMMEDIATELY.
                 *
                 * Prevents duplicate clicks caused by multiple
                 * AccessibilityEvents.
                 */

                sendLocked = true

                polling = false

                Log.d(
                    TAG,
                    "==================================="
                )

                Log.d(
                    TAG,
                    "WHATSAPP SEND BUTTON CLICKED"
                )

                Log.d(
                    TAG,
                    "Waiting ${SEND_VERIFY_DELAY}ms for WhatsApp..."
                )

                Log.d(
                    TAG,
                    "==================================="
                )

                handler.removeCallbacksAndMessages(
                    null
                )

                /*
                 * Wait briefly before completing the task.
                 *
                 * This prevents us from immediately clearing
                 * the task while WhatsApp is still processing
                 * the click.
                 */

                handler.postDelayed(
                    {
                        verifySendAndComplete()
                    },
                    SEND_VERIFY_DELAY
                )

                return
            }

            Log.d(
                TAG,
                "Send button not ready yet"
            )

            /*
             * IMPORTANT:
             *
             * Keep polling.
             *
             * This is what fixes the intermittent second-call
             * failure.
             */

            scheduleNextPoll()

            return
        }

        //--------------------------------------------------
        // TEXT ONLY
        //--------------------------------------------------

        if (
            message.isNullOrBlank()
        ) {

            scheduleNextPoll()

            return
        }

        Log.d(
            TAG,
            "Text-only automation"
        )

        val clicked =
            AccessibilityUtils.clickSendButton(
                root
            )

        if (clicked) {

            sendLocked = true

            polling = false

            handler.removeCallbacksAndMessages(
                null
            )

            Log.d(
                TAG,
                "WHATSAPP TEXT SEND BUTTON CLICKED"
            )

            handler.postDelayed(
                {
                    verifySendAndComplete()
                },
                SEND_VERIFY_DELAY
            )

            return
        }

        scheduleNextPoll()
    }

    //--------------------------------------------------
    // VERIFY SEND
    //--------------------------------------------------

    private fun verifySendAndComplete() {

        if (completingTask) {
            return
        }

        val task =
            AutomationManager
                .getCurrentTask()

        if (task == null) {

            Log.e(
                TAG,
                "Send verification: task disappeared"
            )

            sendLocked = false

            return
        }

        Log.d(
            TAG,
            "==================================="
        )

        Log.d(
            TAG,
            "VERIFYING WHATSAPP SEND"
        )

        Log.d(
            TAG,
            "Mobile : ${task.mobile}"
        )

        Log.d(
            TAG,
            "==================================="
        )

        /*
         * At this point the click has already been performed.
         *
         * We intentionally treat a successful click on
         * WhatsApp's exposed media Send control as the send
         * action, then complete the queue after the short
         * processing delay.
         */

        completeCurrentTask()
    }

    //--------------------------------------------------
    // COMPLETE CURRENT TASK
    //--------------------------------------------------

    @Synchronized
    private fun completeCurrentTask() {

        if (completingTask) {
            return
        }

        completingTask = true

        /*
         * Snapshot BEFORE clearing AutomationManager.
         */

        val task =
            AutomationManager
                .getCurrentTask()

        val pendingId =
            AutomationManager
                .getCurrentPendingId()

        Log.d(
            TAG,
            "==================================="
        )

        Log.d(
            TAG,
            "Completing WhatsApp Task"
        )

        Log.d(
            TAG,
            "Pending ID : $pendingId"
        )

        Log.d(
            TAG,
            "Mobile : ${task?.mobile}"
        )

        Log.d(
            TAG,
            "==================================="
        )

        //--------------------------------------------------
        // MARK WHATSAPP SENT
        //--------------------------------------------------

        try {

            if (task != null) {

                DuplicateManager.markSent(

                    applicationContext,

                    task.mobile,

                    SentMessage.TYPE_WHATSAPP
                )

                Log.d(
                    TAG,
                    "WhatsApp sent record created"
                )
            }

        } catch (e: Exception) {

            Log.e(
                TAG,
                "WhatsApp sent record error",
                e
            )
        }

        //--------------------------------------------------
        // STOP WATCHDOG
        //--------------------------------------------------

        try {

            QueueRecovery.stopWatchdog()

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Watchdog stop error",
                e
            )
        }

        //--------------------------------------------------
        // CLEAR ATTACHMENT
        //--------------------------------------------------

        try {

            PendingAttachmentHolder.clear()

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Attachment clear error",
                e
            )
        }

        //--------------------------------------------------
        // CLEAR AUTOMATION TASK
        //--------------------------------------------------

        try {

            AutomationManager.finishTask()

        } catch (e: Exception) {

            Log.e(
                TAG,
                "AutomationManager finish error",
                e
            )
        }

        //--------------------------------------------------
        // AUTOMATION OFF
        //--------------------------------------------------

        AutomationFlag.set(
            this,
            false
        )

        //--------------------------------------------------
        // COMPLETE QUEUE
        //--------------------------------------------------

        if (pendingId > 0L) {

            Log.d(
                TAG,
                "Calling QueueManager.completed()"
            )

            try {

                QueueManager.completed(

                    applicationContext,

                    pendingId

                )

                Log.d(
                    TAG,
                    "QueueManager.completed() called"
                )

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "QueueManager.completed() FAILED",
                    e
                )

                /*
                 * Do not silently leave the queue stuck.
                 */

                QueueManager.processNext(
                    applicationContext
                )
            }

        } else {

            Log.e(
                TAG,
                "INVALID pendingId"
            )

            QueueManager.processNext(
                applicationContext
            )
        }

        //--------------------------------------------------
        // FINAL STATE
        //--------------------------------------------------

        polling = false
        automationStarted = false

        /*
         * Don't reset sendLocked immediately.
         *
         * The next WhatsApp task gets its own service events
         * and the service can be reused safely.
         */

        Log.d(
            TAG,
            "==================================="
        )

        Log.d(
            TAG,
            "WHATSAPP AUTOMATION FINISHED"
        )

        Log.d(
            TAG,
            "QUEUE TASK COMPLETED"
        )

        Log.d(
            TAG,
            "NEXT QUEUE ITEM CAN START"
        )

        Log.d(
            TAG,
            "==================================="

        )

        /*
         * Allow the service to handle the next queued task.
         */
        sendLocked = false
        completingTask = false
    }

    //--------------------------------------------------
    // SCHEDULE NEXT POLL
    //--------------------------------------------------

    private fun scheduleNextPoll() {

        if (sendLocked) {
            return
        }

        if (completingTask) {
            return
        }

        if (!polling) {
            return
        }

        if (
            !AutomationFlag.isFromApp(this)
        ) {

            stopPolling()

            return
        }

        handler.removeCallbacks(
            pollRunnable
        )

        handler.postDelayed(
            pollRunnable,
            POLL_DELAY
        )
    }

    //--------------------------------------------------
    // POLL RUNNABLE
    //--------------------------------------------------

    private val pollRunnable =
        Runnable {

            if (
                !sendLocked &&
                !completingTask &&
                AutomationFlag.isFromApp(this)
            ) {

                pollWhatsApp()
            }
        }

    //--------------------------------------------------
    // STOP POLLING
    //--------------------------------------------------

    private fun stopPolling() {

        polling = false

        handler.removeCallbacks(
            pollRunnable
        )
    }

    //--------------------------------------------------
    // INTERRUPT
    //--------------------------------------------------

    override fun onInterrupt() {

        Log.d(
            TAG,
            "Accessibility interrupted"
        )

        stopPolling()
    }

    //--------------------------------------------------
    // DESTROY
    //--------------------------------------------------

    override fun onDestroy() {

        stopPolling()

        handler.removeCallbacksAndMessages(
            null
        )

        /*
         * Do not mark anything as sent here.
         */

        sendLocked = true

        completingTask = false
        automationStarted = false

        super.onDestroy()
    }
}