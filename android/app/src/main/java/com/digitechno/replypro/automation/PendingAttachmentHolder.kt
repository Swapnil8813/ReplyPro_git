package com.digitechno.replypro.automation

import android.net.Uri
import java.io.File

object PendingAttachmentHolder {

    var file: File? = null

    var uri: Uri? = null

    var mime: String? = null

    var message: String? = null

    var captionInserted: Boolean = false

    fun clear() {
        file = null
        uri = null
        mime = null
        message = null
        captionInserted = false
    }
}