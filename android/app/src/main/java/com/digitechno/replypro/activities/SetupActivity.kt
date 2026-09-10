package com.digitechno.replypro.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.digitechno.replypro.R

class SetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_setup)

    }

    override fun onResume() {
        super.onResume()

        // Next:
        // Check Phone Permission
        // Check Call Log Permission
        // Check Accessibility
        // Check Battery Optimization

    }

}