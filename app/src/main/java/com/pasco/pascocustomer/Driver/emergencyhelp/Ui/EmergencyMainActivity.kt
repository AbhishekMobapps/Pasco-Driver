package com.pasco.pascocustomer.Driver.emergencyhelp.Ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.databinding.ActivityEmergencyMainBinding
import com.pasco.pascocustomer.language.Originator
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class EmergencyMainActivity : Originator() {
    private lateinit var binding:ActivityEmergencyMainBinding
    private var bookingId = ""
    private lateinit var sharedPreferencesLanguageName: SharedPreferences
    private var language= ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergencyMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.closeImage.setOnClickListener {
            finish()
        }

        language = sharedPreferencesLanguageName.getString("language_text", "").toString()
        if (Objects.equals(language, "ar")) {
            binding.closeImage.setImageResource(R.drawable.next)
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