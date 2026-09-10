package com.digitechno.replypro.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.digitechno.replypro.R
import com.digitechno.replypro.settings.SettingsActivity
import com.digitechno.replypro.utils.SessionManager

class DashboardActivity : AppCompatActivity() {

    companion object {

        private const val BASIC_PERMISSION_REQUEST = 100
        private const val SMS_PERMISSION_REQUEST = 101
        private const val NOTIFICATION_PERMISSION_REQUEST = 102
    }

    private lateinit var session: SessionManager

    private lateinit var txtMobile: TextView
    private lateinit var txtLicense: TextView

    private lateinit var txtPhonePermission: TextView
    private lateinit var txtCallLogPermission: TextView
    private lateinit var txtSmsPermission: TextView
    private lateinit var txtAccessibility: TextView

    private lateinit var btnAccessibility: Button
    private lateinit var btnRefresh: Button
    private lateinit var btnSettings: Button
    private lateinit var btnLogout: Button

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_dashboard
        )

        session = SessionManager(this)

        //--------------------------------------------------
        // Views
        //--------------------------------------------------

        txtMobile =
            findViewById(R.id.txtMobile)

        txtLicense =
            findViewById(R.id.txtLicense)

        txtPhonePermission =
            findViewById(R.id.txtPhonePermission)

        txtCallLogPermission =
            findViewById(R.id.txtCallLogPermission)

        txtSmsPermission =
            findViewById(R.id.txtSmsPermission)

        txtAccessibility =
            findViewById(R.id.txtAccessibility)

        btnAccessibility =
            findViewById(R.id.btnAccessibility)

        btnRefresh =
            findViewById(R.id.btnRefresh)

        btnSettings =
            findViewById(R.id.btnSettings)

        btnLogout =
            findViewById(R.id.btnLogout)

        //--------------------------------------------------
        // User Info
        //--------------------------------------------------

        txtMobile.text =
            session.getMobile()

        txtLicense.text =
            "🟢 License Active"

        //--------------------------------------------------
        // Status
        //--------------------------------------------------

        updateStatus()

        //--------------------------------------------------
        // Permissions
        //--------------------------------------------------

        requestBasicPermissions()

        //--------------------------------------------------
        // Accessibility
        //--------------------------------------------------

        btnAccessibility.setOnClickListener {

            startActivity(
                Intent(
                    Settings.ACTION_ACCESSIBILITY_SETTINGS
                )
            )
        }

        //--------------------------------------------------
        // Refresh
        //--------------------------------------------------

        btnRefresh.setOnClickListener {

            updateStatus()

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.SEND_SMS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                requestSmsPermission()

            } else {

                Toast.makeText(
                    this,
                    "Permissions checked",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        //--------------------------------------------------
        // Settings
        //--------------------------------------------------

        btnSettings.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    SettingsActivity::class.java
                )
            )
        }

        //--------------------------------------------------
        // Logout
        //--------------------------------------------------

        btnLogout.setOnClickListener {

            session.logout()

            startActivity(
                Intent(
                    this,
                    LoginActivity::class.java
                )
            )

            finish()
        }
    }

    //--------------------------------------------------
    // Resume
    //--------------------------------------------------

    override fun onResume() {

        super.onResume()

        updateStatus()

    }

    //--------------------------------------------------
    // Status
    //--------------------------------------------------

    private fun updateStatus() {

        //--------------------------------------------------
        // Phone
        //--------------------------------------------------

        txtPhonePermission.text =
            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                "🟢 Granted"

            } else {

                "🔴 Not Granted"
            }

        //--------------------------------------------------
        // Call Log
        //--------------------------------------------------

        txtCallLogPermission.text =
            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_CALL_LOG
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                "🟢 Granted"

            } else {

                "🔴 Not Granted"
            }

        //--------------------------------------------------
        // SMS
        //--------------------------------------------------

        txtSmsPermission.text =
            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.SEND_SMS
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                "🟢 Granted"

            } else {

                "🔴 Not Granted"
            }

        //--------------------------------------------------
        // Accessibility
        //--------------------------------------------------

        val enabledServices =
            Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: ""

        val serviceName =
            "$packageName/com.digitechno.replypro.accessibility.ReplyProAccessibilityService"

        val accessibilityEnabled =
            enabledServices.contains(
                serviceName,
                true
            )

        if (accessibilityEnabled) {

            txtAccessibility.text =
                "🟢 Enabled"

            btnAccessibility.text =
                "♿ Accessibility Enabled"

            btnAccessibility.isEnabled =
                false

        } else {

            txtAccessibility.text =
                "🔴 Disabled"

            btnAccessibility.text =
                "♿ Enable Accessibility"

            btnAccessibility.isEnabled =
                true
        }
    }

    //--------------------------------------------------
    // Basic Permissions
    //--------------------------------------------------

    private fun requestBasicPermissions() {

        val permissions =
            mutableListOf<String>()

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            permissions.add(
                Manifest.permission.READ_PHONE_STATE
            )
        }

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            permissions.add(
                Manifest.permission.READ_CALL_LOG
            )
        }

        if (permissions.isNotEmpty()) {

            ActivityCompat.requestPermissions(

                this,

                permissions.toTypedArray(),

                BASIC_PERMISSION_REQUEST
            )

        } else {

            // Basic permissions already granted.
            // Now handle SMS separately.
            requestSmsPermission()
        }
    }

    //--------------------------------------------------
    // SMS Permission
    //--------------------------------------------------

    private fun requestSmsPermission() {

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            updateStatus()

            requestNotificationPermissionIfNeeded()

            return
        }

        ActivityCompat.requestPermissions(

            this,

            arrayOf(
                Manifest.permission.SEND_SMS
            ),

            SMS_PERMISSION_REQUEST
        )
    }

    //--------------------------------------------------
    // Notification Permission
    //--------------------------------------------------

    private fun requestNotificationPermissionIfNeeded() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(

                    this,

                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS
                    ),

                    NOTIFICATION_PERMISSION_REQUEST
                )
            }
        }
    }

    //--------------------------------------------------
    // Permission Result
    //--------------------------------------------------

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(

        requestCode: Int,

        permissions: Array<out String>,

        grantResults: IntArray

    ) {

        super.onRequestPermissionsResult(

            requestCode,

            permissions,

            grantResults
        )

        when (requestCode) {

            BASIC_PERMISSION_REQUEST -> {

                updateStatus()

                // Ask SMS separately.
                requestSmsPermission()
            }

            SMS_PERMISSION_REQUEST -> {

                updateStatus()

                if (
                    grantResults.isNotEmpty() &&
                    grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {

                    Toast.makeText(

                        this,

                        "✅ SMS Permission Granted",

                        Toast.LENGTH_LONG

                    ).show()

                    requestNotificationPermissionIfNeeded()

                } else {

                    Toast.makeText(

                        this,

                        "⚠️ SMS permission is required to send SMS",

                        Toast.LENGTH_LONG

                    ).show()
                }
            }

            NOTIFICATION_PERMISSION_REQUEST -> {

                updateStatus()
            }
        }
    }
}