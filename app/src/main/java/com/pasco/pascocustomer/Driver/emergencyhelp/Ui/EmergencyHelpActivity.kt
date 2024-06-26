package com.pasco.pascocustomer.Driver.emergencyhelp.Ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
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
import com.pasco.pascocustomer.Driver.emergencyhelp.ViewModel.SendEmergencyHelpViewModel
import com.pasco.pascocustomer.Driver.emergencyhelp.ViewModel.SendHelpClickListner
import com.pasco.pascocustomer.Driver.emergencyhelp.ViewModel.SendToAllBody
import com.pasco.pascocustomer.Driver.emergencyhelp.ViewModel.SendToAllViewModel
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.application.PascoApp
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
class EmergencyHelpActivity : AppCompatActivity(),SendHelpClickListner {
    private lateinit var binding:ActivityEmergencyHelpBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var activity: Activity
    private var formattedLatitudeSelect: String = ""
    private var formattedLongitudeSelect: String = ""
    private var bookingId = ""
    private var emergencyDriNumbers: List<EmergencyHelpDriverResponse.EmergencyHelpDriverResponseData> = ArrayList()
    private val emergencyHelpDriverModel: EmergencyHelpDriverViewModel by viewModels()
    private val sendEmergencyHelpViewModel: SendEmergencyHelpViewModel by viewModels()
    private val sendToAllViewModel: SendToAllViewModel by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }
    private var driversListAdapter: DriversListAdapter? = null
    private lateinit var body: SendToAllBody
    private var address: String? = null
    private var driverId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergencyHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this

        bookingId = intent.getStringExtra("bookingIdH").toString()
        driverId = PascoApp.encryptedPrefs.userId
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
        binding.sReqAllDrivers.setOnClickListener {
            sendHelpAlertPopup()
        }
        getEmergencyObserver()
        sendHelpObserver()
    }
    @SuppressLint("MissingInflatedId")
    private fun sendHelpAlertPopup() {
        val builder = AlertDialog.Builder(this, R.style.Style_Dialog_Rounded_Corner)
        val dialogView = layoutInflater.inflate(R.layout.send_req_all_confirmation_popup, null)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val noTextViewCp = dialogView.findViewById<TextView>(R.id.noTextViewCp)
        val yesTextViewCp = dialogView.findViewById<TextView>(R.id.yesTextViewCp)
        noTextViewCp.setOnClickListener {
            dialog.dismiss()
        }
        yesTextViewCp.setOnClickListener {
            sendToAllApi()
        }
        dialog.show()

        sendToAllObserver(dialog)
    }

    private fun sendToAllObserver(dialog: AlertDialog) {
        sendToAllViewModel.mSendToAllResponse.observe(this) { response ->
            val message = response.peekContent().msg!!

            if (response.peekContent().status == "False") {
                Toast.makeText(this, "$message", Toast.LENGTH_LONG).show()
            } else {
                dialog.dismiss()
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
        sendEmergencyHelpViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }

    private fun sendToAllApi() {
        body = SendToAllBody(
            address.toString(),
            formattedLatitudeSelect,
            formattedLongitudeSelect
        )
        sendToAllViewModel.sendHelpToAllData(bookingId,body, activity, progressDialog)
    }

    private fun sendHelpObserver() {
        sendEmergencyHelpViewModel.mGetHelpDriverResponse.observe(this) { response ->
            val message = response.peekContent().msg!!

            if (response.peekContent().status == "False") {
                Toast.makeText(this, "$message", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
        sendEmergencyHelpViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }
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
                    binding.sReqAllDrivers.visibility = View.GONE
                } else {
                    binding.driversListNoDataTextView.visibility = View.GONE
                    binding.recyclerAllDriverList.visibility = View.VISIBLE
                    binding.sReqAllDrivers.visibility = View.VISIBLE
                    setupRecyclerView()
                }
            }
        }

        emergencyHelpDriverModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@EmergencyHelpActivity, it)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerAllDriverList.isVerticalScrollBarEnabled = true
        binding.recyclerAllDriverList.isVerticalFadingEdgeEnabled = true
        binding.recyclerAllDriverList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        driversListAdapter = DriversListAdapter(this, this, emergencyDriNumbers,bookingId)
        binding.recyclerAllDriverList.adapter = driversListAdapter
    }

    private fun getEmergency() {
        emergencyHelpDriverModel.getEmergencyHelpDriverList(progressDialog, activity, formattedLatitudeSelect, formattedLongitudeSelect)
    }

    override fun sendHelp(position: Int, id: Int, comment: String) {

        sendHelpApi(id,comment)
    }

    private fun sendHelpApi(id: Int, comment: String) {
           val reason = comment
        sendEmergencyHelpViewModel.sendEmergencyData(progressDialog,activity,bookingId,driverId,address.toString(),reason)

        }

    }