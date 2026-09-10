package com.digitechno.replypro.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File

object FileUtils {

    fun getFileFromUri(
        context: Context,
        uri: Uri
    ): File {

        val resolver = context.contentResolver

        var fileName = "attachment"

        resolver.query(
            uri,
            null,
            null,
            null,
            null
        )?.use { cursor ->

            val nameIndex =
                cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)

            if (cursor.moveToFirst() && nameIndex != -1) {

                fileName = cursor.getString(nameIndex)

            }

        }

        val file = File(

            context.cacheDir,

            fileName

        )

        resolver.openInputStream(uri)?.use { input ->

            file.outputStream().use { output ->

                input.copyTo(output)

            }

        }

        return file

    }

}