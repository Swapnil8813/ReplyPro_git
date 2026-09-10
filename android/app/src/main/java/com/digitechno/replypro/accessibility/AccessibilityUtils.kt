package com.digitechno.replypro.accessibility

import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo

object AccessibilityUtils {

    private const val TAG = "AccessibilityUtils"

    // ============================================================
    // FIND NODE BY VIEW ID
    // ============================================================

    fun findNodeByViewId(
        root: AccessibilityNodeInfo?,
        id: String
    ): AccessibilityNodeInfo? {

        if (root == null) return null

        return try {

            val nodes =
                root.findAccessibilityNodeInfosByViewId(id)

            nodes.firstOrNull()

        } catch (e: Exception) {

            Log.e(
                TAG,
                "findNodeByViewId failed : $id",
                e
            )

            null
        }
    }

    // ============================================================
    // CLICK
    // ============================================================

    fun click(
        node: AccessibilityNodeInfo?
    ): Boolean {

        if (node == null) return false

        return try {

            if (!node.isVisibleToUser) {
                return false
            }

            if (
                node.isClickable &&
                node.performAction(
                    AccessibilityNodeInfo.ACTION_CLICK
                )
            ) {
                true
            } else {
                clickParent(node)
            }

        } catch (e: Exception) {

            Log.e(
                TAG,
                "click failed",
                e
            )

            false
        }
    }

    // ============================================================
    // CLICK PARENT
    // ============================================================

    private fun clickParent(
        node: AccessibilityNodeInfo?
    ): Boolean {

        var current =
            node?.parent

        repeat(5) {

            if (current == null) {
                return false
            }

            try {

                if (
                    current.isVisibleToUser &&
                    current.isClickable &&
                    current.performAction(
                        AccessibilityNodeInfo.ACTION_CLICK
                    )
                ) {

                    return true
                }

            } catch (_: Exception) {
            }

            current =
                current.parent
        }

        return false
    }

    // ============================================================
    // CLICK BY TEXT
    // ============================================================

    fun clickByText(
        root: AccessibilityNodeInfo?,
        text: String
    ): Boolean {

        if (root == null) return false

        try {

            val nodeText =
                root.text?.toString()

            if (
                nodeText != null &&
                nodeText.equals(
                    text,
                    ignoreCase = true
                ) &&
                root.isVisibleToUser
            ) {

                if (click(root)) {
                    return true
                }
            }

            for (
            i in 0 until root.childCount
            ) {

                if (
                    clickByText(
                        root.getChild(i),
                        text
                    )
                ) {
                    return true
                }
            }

        } catch (e: Exception) {

            Log.e(
                TAG,
                "clickByText failed : $text",
                e
            )
        }

        return false
    }

    // ============================================================
    // CLICK BY DESCRIPTION
    // ============================================================

    fun clickByDescription(
        root: AccessibilityNodeInfo?,
        description: String
    ): Boolean {

        if (root == null) return false

        try {

            val nodeDescription =
                root.contentDescription?.toString()

            if (
                nodeDescription != null &&
                nodeDescription.equals(
                    description,
                    ignoreCase = true
                ) &&
                root.isVisibleToUser
            ) {

                if (click(root)) {
                    return true
                }
            }

            for (
            i in 0 until root.childCount
            ) {

                if (
                    clickByDescription(
                        root.getChild(i),
                        description
                    )
                ) {
                    return true
                }
            }

        } catch (e: Exception) {

            Log.e(
                TAG,
                "clickByDescription failed : $description",
                e
            )
        }

        return false
    }

    // ============================================================
    // ENTER TEXT
    // ============================================================

    fun enterText(
        root: AccessibilityNodeInfo?,
        message: String
    ): Boolean {

        if (
            root == null ||
            message.isBlank()
        ) {
            return false
        }

        // --------------------------------------------------------
        // WhatsApp caption
        // --------------------------------------------------------

        val captionIds =
            listOf(
                "com.whatsapp:id/caption",
                "com.whatsapp.w4b:id/caption"
            )

        for (id in captionIds) {

            try {

                val nodes =
                    root.findAccessibilityNodeInfosByViewId(id)

                for (node in nodes) {

                    if (!node.isVisibleToUser) {
                        continue
                    }

                    val bundle =
                        Bundle()

                    bundle.putCharSequence(
                        AccessibilityNodeInfo
                            .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                        message
                    )

                    if (
                        node.performAction(
                            AccessibilityNodeInfo.ACTION_SET_TEXT,
                            bundle
                        )
                    ) {

                        Log.d(
                            TAG,
                            "Caption inserted : $id"
                        )

                        return true
                    }
                }

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "Caption ID failed : $id",
                    e
                )
            }
        }

        // --------------------------------------------------------
        // WhatsApp entry
        // --------------------------------------------------------

        val entryIds =
            listOf(
                "com.whatsapp:id/entry",
                "com.whatsapp.w4b:id/entry"
            )

        for (id in entryIds) {

            try {

                val nodes =
                    root.findAccessibilityNodeInfosByViewId(id)

                for (node in nodes) {

                    if (!node.isVisibleToUser) {
                        continue
                    }

                    val bundle =
                        Bundle()

                    bundle.putCharSequence(
                        AccessibilityNodeInfo
                            .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                        message
                    )

                    if (
                        node.performAction(
                            AccessibilityNodeInfo.ACTION_SET_TEXT,
                            bundle
                        )
                    ) {

                        Log.d(
                            TAG,
                            "Caption inserted : $id"
                        )

                        return true
                    }
                }

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "Entry ID failed : $id",
                    e
                )
            }
        }

        // --------------------------------------------------------
        // Generic editable fallback
        // --------------------------------------------------------

        val editableNodes =
            mutableListOf<AccessibilityNodeInfo>()

        collectEditableNodes(
            root,
            editableNodes
        )

        Log.d(
            TAG,
            "Editable nodes found : ${editableNodes.size}"
        )

        for (node in editableNodes) {

            try {

                if (!node.isVisibleToUser) {
                    continue
                }

                val bundle =
                    Bundle()

                bundle.putCharSequence(
                    AccessibilityNodeInfo
                        .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    message
                )

                if (
                    node.performAction(
                        AccessibilityNodeInfo.ACTION_SET_TEXT,
                        bundle
                    )
                ) {

                    Log.d(
                        TAG,
                        "Caption inserted using editable fallback"
                    )

                    return true
                }

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "Editable fallback failed",
                    e
                )
            }
        }

        Log.d(
            TAG,
            "MEDIA CAPTION NOT FOUND"
        )

        return false
    }

    // ============================================================
    // COLLECT EDITABLE NODES
    // ============================================================

    private fun collectEditableNodes(
        root: AccessibilityNodeInfo?,
        result: MutableList<AccessibilityNodeInfo>
    ) {

        if (root == null) return

        try {

            if (root.isEditable) {
                result.add(root)
            }

            for (
            i in 0 until root.childCount
            ) {

                collectEditableNodes(
                    root.getChild(i),
                    result
                )
            }

        } catch (e: Exception) {

            Log.e(
                TAG,
                "collectEditableNodes failed",
                e
            )
        }
    }

    // ============================================================
    // CLICK SEND BUTTON
    //
    // IMPORTANT:
    // This is ONLY called by ReplyPro's active automation task.
    // It does NOT search arbitrary WhatsApp screens for Send.
    // ============================================================

    fun clickSendButton(
        root: AccessibilityNodeInfo?
    ): Boolean {

        if (root == null) {
            Log.d(
                TAG,
                "Send check: root is null"
            )
            return false
        }

        // ========================================================
        // 1. EXACT AUG-1 MEDIA SEND ID
        // ========================================================

        val sendIds =
            listOf(
                "com.whatsapp:id/send_media_btn",
                "com.whatsapp.w4b:id/send_media_btn"
            )

        for (id in sendIds) {

            try {

                val nodes =
                    root.findAccessibilityNodeInfosByViewId(id)

                Log.d(
                    TAG,
                    "Send ID $id nodes = ${nodes.size}"
                )

                for (node in nodes) {

                    if (!node.isVisibleToUser) {
                        continue
                    }

                    Log.d(
                        TAG,
                        "Found media Send node : $id"
                    )

                    Log.d(
                        TAG,
                        "class=${node.className}"
                    )

                    Log.d(
                        TAG,
                        "clickable=${node.isClickable}"
                    )

                    // Direct click
                    if (
                        node.performAction(
                            AccessibilityNodeInfo.ACTION_CLICK
                        )
                    ) {

                        Log.d(
                            TAG,
                            "==================================="
                        )

                        Log.d(
                            TAG,
                            "SEND CLICKED : $id"
                        )

                        Log.d(
                            TAG,
                            "==================================="
                        )

                        return true
                    }

                    // Parent click fallback
                    if (
                        clickParent(node)
                    ) {

                        Log.d(
                            TAG,
                            "SEND CLICKED THROUGH PARENT : $id"
                        )

                        return true
                    }
                }

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "Media Send ID failed : $id",
                    e
                )
            }
        }

        // ========================================================
        // 2. SEARCH ACCESSIBILITY CONTENT DESCRIPTION
        // ========================================================

        val descriptions =
            setOf(
                "send",
                "send message",
                "send now"
            )

        if (
            findAndClickSendDescription(
                root,
                descriptions
            )
        ) {

            return true
        }

        // ========================================================
        // 3. LOG WHAT WHATSAPP ACTUALLY EXPOSED
        //
        // This is important for the next test.
        // ========================================================

        Log.d(
            TAG,
            "No known Send node found. Dumping visible buttons..."
        )

        dumpVisibleButtons(
            root
        )

        return false
    }

    // ============================================================
    // FIND SEND DESCRIPTION
    // ============================================================

    private fun findAndClickSendDescription(
        root: AccessibilityNodeInfo?,
        descriptions: Set<String>
    ): Boolean {

        if (root == null) {
            return false
        }

        try {

            val description =
                root.contentDescription
                    ?.toString()
                    ?.trim()
                    ?.lowercase()

            val text =
                root.text
                    ?.toString()
                    ?.trim()
                    ?.lowercase()

            val className =
                root.className
                    ?.toString()
                    ?: ""

            val validClass =
                className.contains(
                    "Button",
                    ignoreCase = true
                ) ||
                        className.contains(
                            "Image",
                            ignoreCase = true
                        )

            val matches =
                validClass &&
                        (
                                descriptions.contains(
                                    description
                                ) ||
                                        descriptions.contains(
                                            text
                                        )
                                )

            if (
                matches &&
                root.isVisibleToUser
            ) {

                Log.d(
                    TAG,
                    "Possible Send button found"
                )

                Log.d(
                    TAG,
                    "description=$description"
                )

                Log.d(
                    TAG,
                    "text=$text"
                )

                Log.d(
                    TAG,
                    "class=$className"
                )

                if (click(root)) {

                    Log.d(
                        TAG,
                        "Generic Send clicked"
                    )

                    return true
                }
            }

            for (
            i in 0 until root.childCount
            ) {

                if (
                    findAndClickSendDescription(
                        root.getChild(i),
                        descriptions
                    )
                ) {

                    return true
                }
            }

        } catch (e: Exception) {

            Log.e(
                TAG,
                "findAndClickSendDescription failed",
                e
            )
        }

        return false
    }

    // ============================================================
    // DEBUG VISIBLE BUTTONS
    // ============================================================

    private fun dumpVisibleButtons(
        root: AccessibilityNodeInfo?
    ) {

        if (root == null) return

        try {

            val className =
                root.className
                    ?.toString()
                    ?: ""

            if (
                root.isVisibleToUser &&
                (
                        className.contains(
                            "Button",
                            ignoreCase = true
                        ) ||
                                className.contains(
                                    "ImageButton",
                                    ignoreCase = true
                                )
                        )
            ) {

                Log.d(
                    TAG,
                    "VISIBLE BUTTON -> " +
                            "id=${root.viewIdResourceName}, " +
                            "text=${root.text}, " +
                            "description=${root.contentDescription}, " +
                            "class=$className, " +
                            "clickable=${root.isClickable}"
                )
            }

            for (
            i in 0 until root.childCount
            ) {

                dumpVisibleButtons(
                    root.getChild(i)
                )
            }

        } catch (e: Exception) {

            Log.e(
                TAG,
                "dumpVisibleButtons failed",
                e
            )
        }
    }
}