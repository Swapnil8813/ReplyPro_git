package com.digitechno.replypro.settings

import com.digitechno.replypro.utils.FileUtils
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.digitechno.replypro.R
import com.digitechno.replypro.api.RetrofitClient
import com.digitechno.replypro.utils.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsActivity : AppCompatActivity() {

    private lateinit var session: SessionManager

    private lateinit var switchWhatsapp: SwitchMaterial
    private lateinit var switchSms: SwitchMaterial

    private lateinit var radioWhatsapp: RadioButton
    private lateinit var radioWhatsappBusiness: RadioButton

    private lateinit var whatsappPreferences: SharedPreferences

    private lateinit var edtWhatsappMessage: TextInputEditText
    private lateinit var edtSmsMessage: TextInputEditText

    private lateinit var txtAttachment: TextView

    private lateinit var btnReplaceAttachment: MaterialButton
    private lateinit var btnRemoveAttachment: MaterialButton
    private lateinit var btnSaveSettings: MaterialButton

    private lateinit var progressSave: View

    private var attachmentUri: Uri? = null

    //--------------------------------------------------
    // File Picker
    //--------------------------------------------------

    private val attachmentPicker = registerForActivityResult(

        ActivityResultContracts.StartActivityForResult()

    ) { result ->

        if (result.resultCode == Activity.RESULT_OK) {

            attachmentUri = result.data?.data

            attachmentUri?.let {

                txtAttachment.text = "Selected"

                uploadAttachment(it)

            }

        }

    }

    //--------------------------------------------------
    // onCreate
    //--------------------------------------------------

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_settings
        )

        session = SessionManager(this)

        //--------------------------------------------------
        // WhatsApp Selection Preferences
        //--------------------------------------------------

        whatsappPreferences =
            getSharedPreferences(
                "replypro_whatsapp_settings",
                MODE_PRIVATE
            )

        //--------------------------------------------------
        // Views
        //--------------------------------------------------

        switchWhatsapp =
            findViewById(
                R.id.switchWhatsapp
            )

        switchSms =
            findViewById(
                R.id.switchSms
            )

        radioWhatsapp =
            findViewById(
                R.id.radioWhatsapp
            )

        radioWhatsappBusiness =
            findViewById(
                R.id.radioWhatsappBusiness
            )

        edtWhatsappMessage =
            findViewById(
                R.id.edtWhatsappMessage
            )

        edtSmsMessage =
            findViewById(
                R.id.edtSmsMessage
            )

        txtAttachment =
            findViewById(
                R.id.txtAttachment
            )

        btnReplaceAttachment =
            findViewById(
                R.id.btnReplaceAttachment
            )

        btnRemoveAttachment =
            findViewById(
                R.id.btnRemoveAttachment
            )

        btnSaveSettings =
            findViewById(
                R.id.btnSaveSettings
            )

        progressSave =
            findViewById(
                R.id.progressSave
            )

        //--------------------------------------------------
        // Load Existing Settings
        //--------------------------------------------------

        loadSettings()

        //--------------------------------------------------
        // Load WhatsApp App Selection
        //--------------------------------------------------

        loadWhatsappAppSelection()

        //--------------------------------------------------
        // Replace Attachment
        //--------------------------------------------------

        btnReplaceAttachment.setOnClickListener {

            val intent =
                Intent(
                    Intent.ACTION_GET_CONTENT
                )

            intent.type = "*/*"

            intent.addCategory(
                Intent.CATEGORY_OPENABLE
            )

            attachmentPicker.launch(

                Intent.createChooser(
                    intent,
                    "Select Attachment"
                )

            )

        }

        //--------------------------------------------------
        // Remove Attachment
        //--------------------------------------------------

        btnRemoveAttachment.setOnClickListener {

            attachmentUri = null

            txtAttachment.text =
                "No Attachment Selected"

        }

        //--------------------------------------------------
        // Save Settings
        //--------------------------------------------------

        btnSaveSettings.setOnClickListener {

            saveSettings()

        }

    }

    //--------------------------------------------------
    // Load WhatsApp App Selection
    //--------------------------------------------------

    private fun loadWhatsappAppSelection() {

        val selectedApp =
            whatsappPreferences.getString(
                "selected_whatsapp_app",
                "WHATSAPP"
            )

        if (
            selectedApp ==
            "WHATSAPP_BUSINESS"
        ) {

            radioWhatsappBusiness.isChecked =
                true

        } else {

            radioWhatsapp.isChecked =
                true

        }

    }

    //--------------------------------------------------
    // Save WhatsApp App Selection
    //--------------------------------------------------

    private fun saveWhatsappAppSelection() {

        val selectedWhatsappApp =

            if (
                radioWhatsappBusiness.isChecked
            ) {

                "WHATSAPP_BUSINESS"

            } else {

                "WHATSAPP"

            }

        whatsappPreferences
            .edit()
            .putString(
                "selected_whatsapp_app",
                selectedWhatsappApp
            )
            .apply()

    }

    //--------------------------------------------------
    // Load Settings
    //--------------------------------------------------

    private fun loadSettings() {

        progressSave.visibility =
            View.VISIBLE

        RetrofitClient.apiService

            .getSettings(
                session.getMobile()
            )

            .enqueue(
                object :
                    Callback<SettingsResponse> {

                    override fun onResponse(

                        call:
                        Call<SettingsResponse>,

                        response:
                        Response<SettingsResponse>

                    ) {

                        progressSave.visibility =
                            View.GONE

                        if (
                            !response.isSuccessful
                        ) {

                            return

                        }

                        val result =
                            response.body()

                        if (
                            result == null ||
                            !result.status
                        ) {

                            return

                        }

                        //--------------------------------------------------
                        // WhatsApp
                        //--------------------------------------------------

                        switchWhatsapp.isChecked =
                            result.settings
                                .whatsapp_enabled

                        edtWhatsappMessage.setText(

                            result.settings
                                .whatsapp_message
                                ?: ""

                        )

                        //--------------------------------------------------
                        // SMS
                        //--------------------------------------------------

                        switchSms.isChecked =
                            result.settings
                                .sms_enabled

                        edtSmsMessage.setText(

                            result.settings
                                .sms_message
                                ?: ""

                        )

                        //--------------------------------------------------
                        // Attachment
                        //--------------------------------------------------

                        txtAttachment.text =

                            result.settings
                                .whatsapp_attachment
                                ?: "No Attachment Selected"

                    }

                    override fun onFailure(

                        call:
                        Call<SettingsResponse>,

                        t: Throwable

                    ) {

                        progressSave.visibility =
                            View.GONE

                        Toast.makeText(

                            this@SettingsActivity,

                            t.message
                                ?: "Connection Error",

                            Toast.LENGTH_LONG

                        ).show()

                    }

                }
            )

    }

    //--------------------------------------------------
    // Save Settings
    //--------------------------------------------------

    private fun saveSettings() {

        //--------------------------------------------------
        // Save Local WhatsApp App Selection
        //--------------------------------------------------

        saveWhatsappAppSelection()

        //--------------------------------------------------
        // Show Loading
        //--------------------------------------------------

        progressSave.visibility =
            View.VISIBLE

        //--------------------------------------------------
        // Server Settings
        //--------------------------------------------------

        val request =
            SettingsRequest(

                mobile_number =
                    session.getMobile(),

                whatsapp_enabled =
                    switchWhatsapp.isChecked,

                whatsapp_message =
                    edtWhatsappMessage
                        .text
                        .toString()
                        .trim(),

                sms_enabled =
                    switchSms.isChecked,

                sms_message =
                    edtSmsMessage
                        .text
                        .toString()
                        .trim()

            )

        RetrofitClient.apiService

            .updateSettings(request)

            .enqueue(

                object :
                    Callback<UpdateResponse> {

                    override fun onResponse(

                        call:
                        Call<UpdateResponse>,

                        response:
                        Response<UpdateResponse>

                    ) {

                        progressSave.visibility =
                            View.GONE

                        if (
                            !response.isSuccessful
                        ) {

                            Toast.makeText(

                                this@SettingsActivity,

                                "Server Error",

                                Toast.LENGTH_SHORT

                            ).show()

                            return

                        }

                        val result =
                            response.body()

                        if (result == null) {

                            Toast.makeText(

                                this@SettingsActivity,

                                "Empty Response",

                                Toast.LENGTH_SHORT

                            ).show()

                            return

                        }

                        if (result.status) {

                            Toast.makeText(

                                this@SettingsActivity,

                                "✅ Settings Saved Successfully",

                                Toast.LENGTH_LONG

                            ).show()

                        } else {

                            Toast.makeText(

                                this@SettingsActivity,

                                result.message,

                                Toast.LENGTH_LONG

                            ).show()

                        }

                    }

                    override fun onFailure(

                        call:
                        Call<UpdateResponse>,

                        t: Throwable

                    ) {

                        progressSave.visibility =
                            View.GONE

                        Toast.makeText(

                            this@SettingsActivity,

                            t.message
                                ?: "Connection Error",

                            Toast.LENGTH_LONG

                        ).show()

                    }

                }

            )

    }

    //--------------------------------------------------
    // Upload Attachment
    //--------------------------------------------------

    private fun uploadAttachment(
        uri: Uri
    ) {

        progressSave.visibility =
            View.VISIBLE

        try {

            val file =
                FileUtils.getFileFromUri(
                    this,
                    uri
                )

            val requestFile =
                file.asRequestBody(

                    contentResolver
                        .getType(uri)
                        ?.toMediaTypeOrNull()
                        ?: "*/*"
                            .toMediaTypeOrNull()

                )

            val attachment =
                MultipartBody.Part
                    .createFormData(

                        "attachment",

                        file.name,

                        requestFile

                    )

            val mobile =
                session
                    .getMobile()
                    .toRequestBody(
                        "text/plain"
                            .toMediaTypeOrNull()
                    )

            RetrofitClient.apiService

                .uploadAttachment(

                    attachment,

                    mobile

                )

                .enqueue(

                    object :
                        Callback<UpdateResponse> {

                        override fun onResponse(

                            call:
                            Call<UpdateResponse>,

                            response:
                            Response<UpdateResponse>

                        ) {

                            progressSave.visibility =
                                View.GONE

                            if (
                                !response.isSuccessful
                            ) {

                                Toast.makeText(

                                    this@SettingsActivity,

                                    "Upload Failed",

                                    Toast.LENGTH_SHORT

                                ).show()

                                return

                            }

                            val result =
                                response.body()

                            if (
                                result != null &&
                                result.status
                            ) {

                                Toast.makeText(

                                    this@SettingsActivity,

                                    "Attachment Uploaded Successfully",

                                    Toast.LENGTH_LONG

                                ).show()

                                txtAttachment.text =
                                    file.name

                            } else {

                                Toast.makeText(

                                    this@SettingsActivity,

                                    result?.message
                                        ?: "Upload Failed",

                                    Toast.LENGTH_LONG

                                ).show()

                            }

                        }

                        override fun onFailure(

                            call:
                            Call<UpdateResponse>,

                            t: Throwable

                        ) {

                            progressSave.visibility =
                                View.GONE

                            Toast.makeText(

                                this@SettingsActivity,

                                t.message
                                    ?: "Upload Failed",

                                Toast.LENGTH_LONG

                            ).show()

                        }

                    }

                )

        } catch (e: Exception) {

            progressSave.visibility =
                View.GONE

            Toast.makeText(

                this,

                e.message
                    ?: "Upload Failed",

                Toast.LENGTH_LONG

            ).show()

        }

    }

    //--------------------------------------------------
    // Clear Attachment
    //--------------------------------------------------

    private fun clearAttachment() {

        attachmentUri = null

        txtAttachment.text =
            "No Attachment Selected"

    }

    //--------------------------------------------------
    // Loading
    //--------------------------------------------------

    private fun showLoading(
        show: Boolean
    ) {

        progressSave.visibility =

            if (show)
                View.VISIBLE
            else
                View.GONE

        btnSaveSettings.isEnabled =
            !show

    }

    //--------------------------------------------------
    // Toast
    //--------------------------------------------------

    private fun showMessage(
        message: String
    ) {

        Toast.makeText(

            this,

            message,

            Toast.LENGTH_SHORT

        ).show()

    }

}