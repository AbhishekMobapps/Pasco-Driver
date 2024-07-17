package com.pasco.pascocustomer.Driver.emergencyhelp.Ui

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.adapter.EmergencyAdapter
import com.pasco.pascocustomer.Driver.emergencyhelp.ViewModel.EmergencyCResponse
import com.pasco.pascocustomer.Driver.emergencyhelp.ViewModel.EmergencyCViewModel
import com.pasco.pascocustomer.databinding.ActivityEmergencyCallBinding
import com.pasco.pascocustomer.language.Originator
import com.pasco.pascocustomer.userFragment.profile.modelview.GetProfileBody
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmergencyCallActivity : Originator() {
    private lateinit var binding: ActivityEmergencyCallBinding
    private var emergencyNumbers: List<EmergencyCResponse.EmergencyResponseData> = ArrayList()
    private lateinit var activity: Activity
    private val emergencyCViewModel: EmergencyCViewModel by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }
    private lateinit var sharedPreferencesLanguageName: SharedPreferences
    private var languageId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergencyCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this

        sharedPreferencesLanguageName = getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
        languageId = sharedPreferencesLanguageName.getString("languageId", "").toString()
        binding.backArrowImgEmergency.setOnClickListener {
            finish()
        }
        // Call API
        getEmergency()
        getEmergencyObserver()
    }

    private fun getEmergencyObserver() {
        emergencyCViewModel.progressIndicator.observe(this, Observer {
        })

        emergencyCViewModel.mGetEmergencyList.observe(this) { response ->
            val message = response.peekContent().msg!!
            emergencyNumbers = response.peekContent().data!!

            if (response.peekContent().status == "False") {
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
            } else {
                if (emergencyNumbers.isEmpty()) {
                    binding.emergencyNoDataTextView.visibility = View.VISIBLE
                    binding.recyclerEmerContactList.visibility = View.GONE
                } else {
                    binding.emergencyNoDataTextView.visibility = View.GONE
                    binding.recyclerEmerContactList.visibility = View.VISIBLE
                    setupRecyclerView()
                }
            }
        }

        emergencyCViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@EmergencyCallActivity, it)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerEmerContactList.apply {
            isVerticalScrollBarEnabled = true
            isVerticalFadingEdgeEnabled = true
            layoutManager =
                LinearLayoutManager(this@EmergencyCallActivity, LinearLayoutManager.VERTICAL, false)
            adapter = EmergencyAdapter(this@EmergencyCallActivity, emergencyNumbers)
        }
    }

    private fun getEmergency() {
        val body = GetProfileBody(
            language = languageId
        )
        emergencyCViewModel.getEmeergencyListData(progressDialog, activity, body)
    }
}
