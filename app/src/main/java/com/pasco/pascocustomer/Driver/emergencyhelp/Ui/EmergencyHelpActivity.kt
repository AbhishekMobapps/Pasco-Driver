package com.pasco.pascocustomer.Driver.emergencyhelp.Ui

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.adapter.DriversListAdapter
import com.pasco.pascocustomer.Driver.emergencyhelp.ViewModel.EmergencyHelpDriverResponse
import com.pasco.pascocustomer.Driver.emergencyhelp.ViewModel.EmergencyHelpDriverViewModel
import com.pasco.pascocustomer.databinding.ActivityEmergencyHelpBinding
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.ArrayList
import java.util.Locale

@AndroidEntryPoint
class EmergencyHelpActivity : AppCompatActivity() {
    private lateinit var binding:ActivityEmergencyHelpBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var activity: Activity
    private var formattedLatitudeSelect: String = ""
    private var formattedLongitudeSelect: String = ""
    private var emergencyDriNumbers: List<EmergencyHelpDriverResponse.EmergencyHelpDriverResponseData> = ArrayList()
    private val emergencyHelpDriverModel: EmergencyHelpDriverViewModel by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }
    private var address: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergencyHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationUpdates()
        if (checkLocationPermission()) {
            requestLocationUpdates()
        } else {
            requestLocationPermission()
        }

        binding.backArrowImgEmegencyDriHelp.setOnClickListener {
            finish()
        }
        getEmergencyObserver()
    }
    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this@EmergencyHelpActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@EmergencyHelpActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@EmergencyHelpActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@EmergencyHelpActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestLocationUpdates() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    showAddress(it)
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun showAddress(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude
        GlobalScope.launch(Dispatchers.IO) {
            val geocoder = Geocoder(this@EmergencyHelpActivity, Locale.getDefault())
            try {
                val addresses: List<Address> = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )!!
                if (addresses.isNotEmpty()) {
                    address = addresses[0].getAddressLine(0)

                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        formattedLatitudeSelect = String.format("%.4f", latitude)
        formattedLongitudeSelect = String.format("%.4f", longitude)
        getEmergency()

    }

    private fun getEmergencyObserver() {
        emergencyHelpDriverModel.progressIndicator.observe(this, Observer {
        })

        emergencyHelpDriverModel.mGetHelpDriverResponse.observe(this) { response ->
            val message = response.peekContent().msg!!
            emergencyDriNumbers = response.peekContent().data!!

            if (response.peekContent().status == "False") {
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
            } else {
                if (emergencyDriNumbers.isEmpty()) {
                    binding.driversListNoDataTextView.visibility = View.VISIBLE
                    binding.recyclerAllDriverList.visibility = View.GONE
                } else {
                    binding.driversListNoDataTextView.visibility = View.GONE
                    binding.recyclerAllDriverList.visibility = View.VISIBLE
                    setupRecyclerView()
                }
            }
        }

        emergencyHelpDriverModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@EmergencyHelpActivity, it)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerAllDriverList.apply {
            isVerticalScrollBarEnabled = true
            isVerticalFadingEdgeEnabled = true
            layoutManager = LinearLayoutManager(this@EmergencyHelpActivity, LinearLayoutManager.VERTICAL, false)
            adapter = DriversListAdapter(this@EmergencyHelpActivity, emergencyDriNumbers)
        }
    }

    private fun getEmergency() {
        emergencyHelpDriverModel.getEmergencyHelpDriverList(progressDialog, activity, formattedLatitudeSelect, formattedLongitudeSelect)
    }

}