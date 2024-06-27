package com.pasco.pascocustomer.notificationoffon

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.databinding.ActivityNotificationOnOffBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NotificationOnOffActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationOnOffBinding
    private var updateSwitch = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationOnOffBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("SwitchPrefs", Context.MODE_PRIVATE)
    //    val switches = listOf(binding.updateBidSwitch, binding.switch2, binding.switch3, binding.switch4, binding.switch5, binding.switch6, binding.switch7)


        binding.updateBidSwitch.setOnCheckedChangeListener { _, isChecked ->
            updateSwitch = if (isChecked) {
                Log.e("UpdateValueAAA","updateSwitchOn" + updateSwitch)
                // Change color and width when switch is on

                binding.updateBidSwitch.setBackgroundColor(Color.GREEN)
                false

            } else {
                // Change color and width when switch is off
                binding.updateBidSwitch.setBackgroundColor(Color.RED)
                true
            }
        }
    }

}