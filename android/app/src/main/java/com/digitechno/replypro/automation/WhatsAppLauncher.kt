package com.digitechno.replypro.automation

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File

object WhatsAppLauncher {

    private const val TAG = "WhatsAppLauncher"

    private const val WHATSAPP = "com.whatsapp"
    private const val WHATSAPP_BUSINESS = "com.whatsapp.w4b"

    // ============================================================
    // SELECT WHATSAPP APP
    //
    // IMPORTANT:
    // SettingsActivity saves:
    //
    // SharedPreferences:
    // "replypro_whatsapp_settings"
    //
    // Key:
    // "selected_whatsapp_app"
    //
    // Values:
    // "WHATSAPP"
    // "WHATSAPP_BUSINESS"
    // ============================================================

    private fun getSelectedPackage(
        context: Context
    ): String? {

        val prefs =
            context.getSharedPreferences(
                "replypro_whatsapp_settings",
                Context.MODE_PRIVATE
            )

        val selected =
            prefs.getString(
                "selected_whatsapp_app",
                "WHATSAPP"
            )

        Log.d(
            TAG,
            "Selected WhatsApp app : $selected"
        )

        return when (selected) {

            "WHATSAPP_BUSINESS" -> {

                if (
                    isInstalled(
                        context,
                        WHATSAPP_BUSINESS
                    )
                ) {

                    Log.d(
                        TAG,
                        "Using WhatsApp Business : $WHATSAPP_BUSINESS"
                    )

                    WHATSAPP_BUSINESS

                } else {

                    Log.e(
                        TAG,
                        "WhatsApp Business selected but not installed"
                    )

                    null
                }
            }

            else -> {

                if (
                    isInstalled(
                        context,
                        WHATSAPP
                    )
                ) {

                    Log.d(
                        TAG,
                        "Using normal WhatsApp : $WHATSAPP"
                    )

                    WHATSAPP

                } else {

                    Log.e(
                        TAG,
                        "Normal WhatsApp is not installed"
                    )

                    null
                }
            }
        }
    }

    // ============================================================
    // CHECK INSTALLATION
    // ============================================================

    private fun isInstalled(
        context: Context,
        packageName: String
    ): Boolean {

        return try {

            context.packageManager.getPackageInfo(
                packageName,
                0
            )

            true

        } catch (
            e: Exception
        ) {

            false
        }
    }

    // ============================================================
    // NORMALIZE NUMBER
    // ============================================================

    private fun normalizeNumber(
        mobile: String
    ): String {

        var number =
            mobile
                .replace("+", "")
                .replace(" ", "")
                .replace("-", "")
                .replace("(", "")
                .replace(")", "")
                .trim()

        /*
         * Keep the international country code exactly as the
         * original ReplyPro flow expects.
         *
         * Example:
         *
         * +91 9168026412
         *       ↓
         * 919168026412
         */

        if (
            number.length == 10 &&
            number.all { it.isDigit() }
        ) {

            number =
                "91$number"
        }

        return number
    }

    // ============================================================
    // TEXT MESSAGE
    // ============================================================

    fun launch(
        context: Context,
        mobile: String,
        message: String
    ) {

        val number =
            normalizeNumber(mobile)

        val packageName =
            getSelectedPackage(context)
                ?: return

        try {

            val intent =
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        "https://wa.me/$number?text=${Uri.encode(message)}"
                    )
                )

            intent.setPackage(
                packageName
            )

            intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
            )

            context.startActivity(
                intent
            )

            Log.d(
                TAG,
                "WhatsApp text chat opened"
            )

        } catch (
            e: Exception
        ) {

            Log.e(
                TAG,
                "WhatsApp text launch failed",
                e
            )
        }
    }

    // ============================================================
    // MEDIA + CAPTION
    //
    // AUG-1 ORIGINAL FLOW
    //
    // IMPORTANT:
    //
    // NO:
    // - ACTION_VIEW before media
    // - Gallery
    // - contact picker
    // - Send To automation
    // - Next automation
    //
    // Direct ACTION_SEND + JID only.
    // ============================================================

    fun launch(
        context: Context,
        mobile: String,
        message: String,
        file: File,
        mimeType: String
    ) {

        val number =
            normalizeNumber(mobile)

        val packageName =
            getSelectedPackage(context)
                ?: return

        val jid =
            "$number@s.whatsapp.net"

        try {

            // ====================================================
            // FILE PROVIDER URI
            // ====================================================

            val uri =
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )

            // ====================================================
            // SAVE PENDING ATTACHMENT
            // ====================================================

            PendingAttachmentHolder.file =
                file

            PendingAttachmentHolder.uri =
                uri

            PendingAttachmentHolder.mime =
                mimeType

            PendingAttachmentHolder.message =
                message

            PendingAttachmentHolder.captionInserted =
                false

            // ====================================================
            // AUTOMATION FLAG
            // ====================================================

            AutomationFlag.set(
                context,
                true
            )

            // ====================================================
            // EXPLICIT URI PERMISSION
            // ====================================================

            try {

                context.grantUriPermission(
                    packageName,
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                Log.d(
                    TAG,
                    "Explicit WhatsApp URI permission granted"
                )

            } catch (
                e: Exception
            ) {

                Log.w(
                    TAG,
                    "URI permission failed",
                    e
                )
            }

            // ====================================================
            // AUG-1 DIRECT MEDIA INTENT
            // ====================================================

            val intent =
                Intent(
                    Intent.ACTION_SEND
                )

            intent.type =
                mimeType

            // ====================================================
            // MEDIA
            // ====================================================

            intent.putExtra(
                Intent.EXTRA_STREAM,
                uri
            )

            // ====================================================
            // CAPTION
            // ====================================================

            if (
                message.isNotBlank()
            ) {

                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    message
                )
            }

            // ====================================================
            // TARGET WHATSAPP CONVERSATION
            // ====================================================

            intent.putExtra(
                "jid",
                jid
            )

            // ====================================================
            // SELECTED WHATSAPP APP
            //
            // This is the ONLY part changed from your working
            // version: package now follows SettingsActivity.
            // ====================================================

            intent.setPackage(
                packageName
            )

            // ====================================================
            // URI ACCESS
            // ====================================================

            intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
            )

            // ====================================================
            // CLIPDATA
            // ====================================================

            intent.clipData =
                ClipData.newRawUri(
                    "ReplyPro Attachment",
                    uri
                )

            // ====================================================
            // LOG
            // ====================================================

            Log.d(
                TAG,
                "==================================="
            )

            Log.d(
                TAG,
                "AUG-1 DIRECT JID MEDIA FLOW"
            )

            Log.d(
                TAG,
                "Selected App : $packageName"
            )

            Log.d(
                TAG,
                "Number       : $number"
            )

            Log.d(
                TAG,
                "JID          : $jid"
            )

            Log.d(
                TAG,
                "File         : ${file.absolutePath}"
            )

            Log.d(
                TAG,
                "URI          : $uri"
            )

            Log.d(
                TAG,
                "Mime         : $mimeType"
            )

            Log.d(
                TAG,
                "Caption      : $message"
            )

            Log.d(
                TAG,
                "ACTION_SEND ONLY"
            )

            Log.d(
                TAG,
                "NO ACTION_VIEW"
            )

            Log.d(
                TAG,
                "NO GALLERY"
            )

            Log.d(
                TAG,
                "NO CONTACT PICKER"
            )

            Log.d(
                TAG,
                "NO MANUAL RECIPIENT SELECTION"
            )

            Log.d(
                TAG,
                "==================================="
            )

            // ====================================================
            // START SELECTED WHATSAPP
            // ====================================================

            context.startActivity(
                intent
            )

            Log.d(
                TAG,
                "Selected WhatsApp direct media intent launched"
            )

        } catch (
            e: Exception
        ) {

            Log.e(
                TAG,
                "WhatsApp media launch failed",
                e
            )
        }
    }
}