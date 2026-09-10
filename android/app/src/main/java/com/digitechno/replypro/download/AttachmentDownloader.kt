package com.digitechno.replypro.download

import android.content.Context
import android.util.Log
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

object AttachmentDownloader {

    private const val TAG =
        "AttachmentDownloader"

    private const val BASE_URL =
        "https://innovatepodarlsjath.com/replypro/public/"

    fun download(
        context: Context,
        attachmentUrl: String,
        fileName: String
    ): File? {

        var connection: HttpURLConnection? = null

        try {

            // ====================================================
            // BUILD REAL URL
            // ====================================================

            val finalUrl =
                buildUrl(attachmentUrl)

            Log.d(
                TAG,
                "Original URL : $attachmentUrl"
            )

            Log.d(
                TAG,
                "Final URL    : $finalUrl"
            )

            val url =
                URL(finalUrl)

            connection =
                url.openConnection()
                        as HttpURLConnection

            connection.requestMethod =
                "GET"

            connection.connectTimeout =
                15000

            connection.readTimeout =
                30000

            connection.instanceFollowRedirects =
                true

            connection.connect()

            val responseCode =
                connection.responseCode

            Log.d(
                TAG,
                "HTTP Response : $responseCode"
            )

            if (
                responseCode !in 200..299
            ) {

                Log.e(
                    TAG,
                    "Attachment download failed. HTTP $responseCode"
                )

                return null
            }

            // ====================================================
            // DETERMINE EXTENSION
            // ====================================================

            val extension =
                getExtension(
                    attachmentUrl
                )

            val safeFileName =
                if (
                    fileName.contains(".")
                ) {

                    fileName

                } else {

                    fileName + extension
                }

            // ====================================================
            // CACHE FILE
            // ====================================================

            val outputFile =
                File(
                    context.cacheDir,
                    safeFileName
                )

            // Delete old file first
            if (
                outputFile.exists()
            ) {

                outputFile.delete()
            }

            // ====================================================
            // DOWNLOAD
            // ====================================================

            connection.inputStream.use { input ->

                outputFile.outputStream().use { output ->

                    val buffer =
                        ByteArray(
                            8192
                        )

                    var bytesRead: Int

                    while (
                        input.read(
                            buffer
                        ).also {
                            bytesRead = it
                        } != -1
                    ) {

                        output.write(
                            buffer,
                            0,
                            bytesRead
                        )
                    }

                    output.flush()
                }
            }

            // ====================================================
            // VERIFY
            // ====================================================

            if (
                !outputFile.exists() ||
                outputFile.length() <= 0
            ) {

                Log.e(
                    TAG,
                    "Downloaded file is empty"
                )

                outputFile.delete()

                return null
            }

            Log.d(
                TAG,
                "Downloaded : ${outputFile.absolutePath}"
            )

            Log.d(
                TAG,
                "File Size  : ${outputFile.length()} bytes"
            )

            return outputFile

        } catch (
            e: Exception
        ) {

            Log.e(
                TAG,
                "Download Error",
                e
            )

            return null

        } finally {

            connection?.disconnect()
        }
    }

    // ============================================================
    // BUILD URL
    // ============================================================

    private fun buildUrl(
        attachmentUrl: String
    ): String {

        val value =
            attachmentUrl.trim()

        // --------------------------------------------------------
        // Already a complete HTTP URL
        // --------------------------------------------------------

        if (
            value.startsWith(
                "http://",
                ignoreCase = true
            ) ||
            value.startsWith(
                "https://",
                ignoreCase = true
            )
        ) {

            return value
        }

        // --------------------------------------------------------
        // Remove leading slash
        // --------------------------------------------------------

        val clean =
            value.removePrefix("/")

        // --------------------------------------------------------
        // Server returned:
        //
        // attachments/file.png
        // --------------------------------------------------------

        if (
            clean.startsWith(
                "attachments/"
            )
        ) {

            return BASE_URL + clean
        }

        // --------------------------------------------------------
        // Server may return:
        //
        // storage/attachments/file.png
        // --------------------------------------------------------

        if (
            clean.startsWith(
                "storage/"
            )
        ) {

            return BASE_URL + clean
        }

        // --------------------------------------------------------
        // Fallback
        // --------------------------------------------------------

        return BASE_URL + clean
    }

    // ============================================================
    // FILE EXTENSION
    // ============================================================

    private fun getExtension(
        url: String
    ): String {

        val clean =
            url.substringBefore("?")

        val lastPart =
            clean.substringAfterLast(
                "/",
                ""
            )

        val extension =
            lastPart.substringAfterLast(
                ".",
                ""
            )

        return if (
            extension.isNotBlank() &&
            extension.length <= 5
        ) {

            ".$extension"

        } else {

            ""
        }
    }
}