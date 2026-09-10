package com.digitechno.replypro.activities
import com.digitechno.replypro.utils.SessionManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.digitechno.replypro.R
import com.digitechno.replypro.api.ActivationRequest
import com.digitechno.replypro.api.ActivationResponse
import com.digitechno.replypro.api.RetrofitClient
import com.digitechno.replypro.utils.DeviceUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var etMobile: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etMobile = findViewById(R.id.etMobile)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {

            val mobile = etMobile.text.toString().trim()

            if (mobile.length != 10) {
                etMobile.error = "Enter valid mobile number"
                return@setOnClickListener
            }

            btnLogin.isEnabled = false

            val request = ActivationRequest(
                api_key = "REPLYPRO_V1_SECRET",
                mobile_number = mobile,
                device_id = DeviceUtil.getDeviceId(this),
                device_name = DeviceUtil.getDeviceName(),
                app_version = DeviceUtil.getAppVersion()
            )

            RetrofitClient.apiService.checkActivation(request)
                .enqueue(object : Callback<ActivationResponse> {

                    override fun onResponse(
                        call: Call<ActivationResponse>,
                        response: Response<ActivationResponse>
                    ) {

                        btnLogin.isEnabled = true

                        if (response.isSuccessful && response.body() != null) {

                            val result = response.body()!!

                            if (result.status) {

                                Toast.makeText(
                                    this@LoginActivity,
                                    result.message,
                                    Toast.LENGTH_SHORT
                                ).show()

                                SessionManager(this@LoginActivity)
                                    .saveLogin(mobile)

                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        DashboardActivity::class.java
                                    )
                                )

                                finish()

                            } else {

                                Toast.makeText(
                                    this@LoginActivity,
                                    result.message,
                                    Toast.LENGTH_LONG
                                ).show()

                            }

                        } else {

                            when (response.code()) {

                                401 -> Toast.makeText(
                                    this@LoginActivity,
                                    "Unauthorized",
                                    Toast.LENGTH_LONG
                                ).show()

                                403 -> {

                                    val errorBody = response.errorBody()?.string()

                                    when {
                                        errorBody?.contains("BLOCKED") == true ->
                                            Toast.makeText(
                                                this@LoginActivity,
                                                "Your account has been blocked by the administrator.",
                                                Toast.LENGTH_LONG
                                            ).show()

                                        errorBody?.contains("EXPIRED") == true ->
                                            Toast.makeText(
                                                this@LoginActivity,
                                                "Your subscription has expired.",
                                                Toast.LENGTH_LONG
                                            ).show()

                                        errorBody?.contains("DEVICE_MISMATCH") == true ->
                                            Toast.makeText(
                                                this@LoginActivity,
                                                "This mobile number is already activated on another device.",
                                                Toast.LENGTH_LONG
                                            ).show()

                                        else ->
                                            Toast.makeText(
                                                this@LoginActivity,
                                                "Activation failed.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                    }
                                }

                                404 -> Toast.makeText(
                                    this@LoginActivity,
                                    "Activation not found",
                                    Toast.LENGTH_LONG
                                ).show()

                                else -> Toast.makeText(
                                    this@LoginActivity,
                                    "Server Error",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        }
                    }

                    override fun onFailure(
                        call: Call<ActivationResponse>,
                        t: Throwable
                    ) {

                        btnLogin.isEnabled = true

                        Toast.makeText(
                            this@LoginActivity,
                            t.message,
                            Toast.LENGTH_LONG
                        ).show()

                    }

                })

        }

    }
}