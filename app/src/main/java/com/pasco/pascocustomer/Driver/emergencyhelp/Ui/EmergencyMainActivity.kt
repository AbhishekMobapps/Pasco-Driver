package com.pasco.pascocustomer.Driver.emergencyhelp.Ui

import android.content.Intent
import android.os.Bundle
import com.pasco.pascocustomer.databinding.ActivityEmergencyMainBinding
import com.pasco.pascocustomer.language.Originator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmergencyMainActivity : Originator() {
    private lateinit var binding:ActivityEmergencyMainBinding
    private var bookingId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergencyMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.closeImage.setOnClickListener {
            finish()
        }
        bookingId = intent.getStringExtra("bookingIdForHelp").toString()

        binding.consAllNearByDrivers.setOnClickListener {
            val intent = Intent(this@EmergencyMainActivity, EmergencyHelpActivity::class.java)
            intent.putExtra("bookingIdH",bookingId)
            startActivity(intent)
        }
        binding.consHelpContacts.setOnClickListener {
            val intent = Intent(this@EmergencyMainActivity, EmergencyCallActivity::class.java)
            intent.putExtra("bookingIdH",bookingId)
            startActivity(intent)
        }

    }
}