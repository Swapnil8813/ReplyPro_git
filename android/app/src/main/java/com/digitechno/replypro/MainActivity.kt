package com.digitechno.replypro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.digitechno.replypro.queue.QueueRecovery
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        QueueRecovery.recover(this)
        setContentView(R.layout.activity_main)
    }
}