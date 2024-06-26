package com.pasco.pascocustomer.notificationoffon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.databinding.ActivityNotificationOnOffBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationOnOffActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationOnOffBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationOnOffBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}