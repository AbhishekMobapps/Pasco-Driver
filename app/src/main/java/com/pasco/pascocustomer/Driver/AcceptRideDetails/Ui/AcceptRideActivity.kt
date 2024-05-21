package com.pasco.pascocustomer.Driver.AcceptRideDetails.Ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import dagger.hilt.android.AndroidEntryPoint
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.Driver.AcceptRideDetails.ViewModel.AcceptRideViewModel
import com.pasco.pascocustomer.Driver.AcceptRideDetails.ViewModel.AddBiddingBody
import com.pasco.pascocustomer.Driver.AcceptRideDetails.ViewModel.AddBidingViewModel
import com.pasco.pascocustomer.Driver.DriverDashboard.Ui.DriverDashboardActivity
import com.pasco.pascocustomer.databinding.ActivityAcceptRideBinding
import com.pasco.pascocustomer.utils.ErrorUtil
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@AndroidEntryPoint
class AcceptRideActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var gooMap: GoogleMap
    private lateinit var binding: ActivityAcceptRideBinding
    private lateinit var activity: Activity
    private val acceptRideViewModel: AcceptRideViewModel by viewModels()
    private val addBidingViewModel: AddBidingViewModel by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0
    private var reqId = ""
    private var bookingNumber = ""
    private var dateTimes = ""
    private var pickUpLocBid = ""
    private var dropUpLocBid = ""
    private var totalDistanceLoc = ""
    private var totalTimeLoc = ""
    private var totalPriceLoc = ""
    private var bookingID = ""
    private var PickUpLoc: LatLng = LatLng(0.0, 0.0)
    private var DropLoc: LatLng = LatLng(0.0, 0.0)
    private var currentLatitudePickup: Double = 0.0
    private var currentLongitudePickup: Double = 0.0
    private var currentLatitudeDrop: Double = 0.0
    private var currentLongitudeDrop: Double = 0.0
    private lateinit var addBiddingBody: AddBiddingBody
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAcceptRideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Request location updates
        requestLocationUpdates()
        activity = this
        reqId = intent.getStringExtra("rideReqId").toString()
        bookingNumber = intent.getStringExtra("bookingNumb").toString()
        binding.imageBackReqRide.setOnClickListener {
            finish()
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //call getBidDetailsApi
        if (!reqId.isNullOrBlank()) {
            getBidDetailsApi()
        }
        //call reqObsever
        getBidObserver()
        //callObserver
        addBidingObserver()
        binding.showPriceEditTextdasdas.setOnClickListener {
            addAvailabilityPopUp()
        }
        binding.imgSsadasd.setOnClickListener {
            addAvailabilityPopUp()
        }
        binding.acceptOrderCButton.setOnClickListener {
            val dateTime = dateTimes
            val bidPrice = binding.showPriceEditText.text.toString()

            if (dateTime.isEmpty()) {
                Toast.makeText(
                    this@AcceptRideActivity,
                    "Please add the availability",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (bidPrice.isEmpty()) {
                Toast.makeText(
                    this@AcceptRideActivity,
                    "Please enter bid price",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!bidPrice.matches("[0-9]+".toRegex())) {
                Toast.makeText(
                    this@AcceptRideActivity,
                    "Please enter a valid bid price",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Call API

                addBiding()
            }
        }
    }
    private fun requestLocationUpdates() {
        // Check for location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        // Request the last known location
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {

                }
            }
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Check if permissions were granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, request location updates
                requestLocationUpdates()
            } else {
                // Permissions denied, handle accordingly (e.g., show a message or disable location features)
            }
        }
    }

    private fun getBidObserver() {
        acceptRideViewModel.mAcceptRideResponse.observe(this) { response ->
            val message = response.peekContent().msg!!
            if (response.peekContent().status == "True") {
                val allData = response.peekContent().data
                binding.pickUpLocBidd.text = truncateAddress(allData?.pickupLocation.toString())
                binding.dropLocBidd.text = truncateAddress(allData?.dropLocation.toString())
                binding.pickUpLocBidd.setOnClickListener {
                    showFullAddressDialog(allData?.pickupLocation.toString())
                }
                binding.dropLocBidd.setOnClickListener {
                    showFullAddressDialog(allData?.dropLocation.toString())
                }
                // Format total distance
                val formattedTotalDistance = "%.1f".format(allData?.totalDistance ?: 0.0)
                binding.totalDistanceBidd.text = "$formattedTotalDistance km"
                binding.WholePrice.text = "$" + allData?.basicprice.toString()
              //  binding.orderIdStaticTextView.text = "$" + allData?.price.toString()
                val id = allData?.id
                Log.e("id", "getBidObserver: $id")
                binding.cashDynamic.text = allData?.paymentStatus.toString()
                bookingID = allData?.id.toString()
                totalTimeLoc = allData?.pickupDatetime.toString()
                totalPriceLoc = "$" + allData?.basicprice.toString()
                currentLatitudePickup = allData?.pickupLatitude.toString()!!.toDouble()
                currentLongitudePickup = allData?.pickupLongitude.toString()!!.toDouble()
                currentLatitudeDrop = allData?.dropLatitude.toString()!!.toDouble()
                currentLongitudeDrop = allData?.dropLongitude.toString()!!.toDouble()

                PickUpLoc = LatLng(currentLatitudePickup, currentLongitudePickup)
                DropLoc = LatLng(currentLatitudeDrop, currentLongitudeDrop)

                drawRoute(PickUpLoc, DropLoc)

            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }

        acceptRideViewModel.errorResponse.observe(this) {
            // Handle general errors
            ErrorUtil.handlerGeneralError(this, it)
        }
    }

    fun showFullAddressDialog(address: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Address")
        alertDialogBuilder.setMessage(address)
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun truncateAddress(address: String): String {
        val words = address.split(" ")
        return if (words.size > 3) {
            words.subList(0, 3).joinToString(" ") + "..."
        } else {
            address
        }
    }

    private fun addBidingObserver() {
        addBidingViewModel.mAddBiddibgResponse.observe(this) { response ->
            val message = response.peekContent().msg!!

            if (response.peekContent().status.equals("False")) {
                Toast.makeText(this, "failed: $message", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "$message", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@AcceptRideActivity, DriverDashboardActivity::class.java)
                intent.putExtra("bookingID", bookingID)
                intent.putExtra("pickUpLocBidd", pickUpLocBid)
                intent.putExtra("dropUpLocBid", dropUpLocBid)
                intent.putExtra("totalDistanceLoc", totalDistanceLoc)
                intent.putExtra("pickUpLocBid", totalTimeLoc)
                intent.putExtra("totalPriceLoc", totalPriceLoc)
                startActivity(intent)

            }

        }
        addBidingViewModel.errorResponse.observe(this) {
            // Handle general errors
            ErrorUtil.handlerGeneralError(this, it)
        }
    }

    private fun addBiding() {
        val pricee = binding.showPriceEditText.text.toString()
        addBiddingBody = AddBiddingBody(
            dateTimes, pricee
        )
        addBidingViewModel.addBidingData(
            progressDialog,
            activity,
            reqId,
            addBiddingBody
        )
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatDateTime(dateTimeString: String?): String {
        if (dateTimeString.isNullOrEmpty()) return ""

        // Parse the datetime string into a ZonedDateTime object
        val dateTime = ZonedDateTime.parse(dateTimeString)

        // Define the desired format
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        // Format the datetime and return as a string
        return dateTime.format(formatter)
    }

    private fun getBidDetailsApi() {
        acceptRideViewModel.getAcceptRideData(
            activity,
            reqId
        )
    }
//comment
    @SuppressLint("MissingInflatedId")
    private fun addAvailabilityPopUp() {
        val builder =
            AlertDialog.Builder(this@AcceptRideActivity, R.style.Style_Dialog_Rounded_Corner)
        val dialogView = layoutInflater.inflate(R.layout.add_avilability_popup, null)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val backArrowAddAvPopup = dialogView.findViewById<ImageView>(R.id.backArrowAddAvPopup)
        val startDateTxtPop = dialogView.findViewById<TextView>(R.id.startDateTxtPop)
        val startTimetxtPop = dialogView.findViewById<TextView>(R.id.startTimetxtPop)
        val createSlotsBtnPop = dialogView.findViewById<Button>(R.id.createSlotsBtnPop)
        dialog.show()
        backArrowAddAvPopup.setOnClickListener {
            dialog.dismiss()
        }

        startDateTxtPop.setOnClickListener {
            val calendar = Calendar.getInstance()

            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH)
            day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this@AcceptRideActivity, R.style.MyTimePicker,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    val formattedMonth = String.format("%02d", monthOfYear + 1)
                    val formatDay = String.format("%02d", dayOfMonth)
                    val date = "$year-$formattedMonth-$formatDay"

                    startDateTxtPop.text = date
                },
                year, month, day
            )

            datePickerDialog.show()
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY)
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        }

        startTimetxtPop.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute = mcurrentTime.get(Calendar.MINUTE)
            val mTimePicker: TimePickerDialog

            mTimePicker = TimePickerDialog(
                this@AcceptRideActivity, R.style.MyTimePicker,
                TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                    val formattedHour = String.format("%02d", selectedHour)
                    val formatMinutes = String.format("%02d", selectedMinute)
                    startTimetxtPop.text = "$formattedHour:$formatMinutes"
                },
                hour, minute, true
            )
            mTimePicker.show()
        }

        createSlotsBtnPop.setOnClickListener {
            if (!startDateTxtPop.text.isEmpty() && !startTimetxtPop.text.isEmpty()) {
                val dateCheck = startDateTxtPop.text.toString()
                val timeCheck = startTimetxtPop.text.toString()
                val dateTime = dateCheck + " " + timeCheck
                dateTimes = dateTime
                if (!dateTimes.isNullOrBlank()) {
                    binding.showPriceEditTextdasdas.text = dateTimes
                }
                Toast.makeText(this@AcceptRideActivity, "Added Successfully", Toast.LENGTH_SHORT)
                    .show()
                Log.d("DateTime", dateTime)
                dialog.dismiss() // Dismiss the dialog explicitly
            } else {
                Toast.makeText(
                    this@AcceptRideActivity,
                    "Please select both start date and start time",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        gooMap = googleMap
        gooMap.addMarker(MarkerOptions().position(PickUpLoc).title("PickUp Location"))
        gooMap.addMarker(MarkerOptions().position(DropLoc).title("Drop Location"))
        val centerLat = (PickUpLoc.latitude + DropLoc.latitude) / 2
        val centerLng = (PickUpLoc.longitude + DropLoc.longitude) / 2
        val centerPoint = LatLng(centerLat, centerLng)
        gooMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerPoint, 13f))
    }


    private fun drawRoute(PickUpLoc: LatLng, DropLoc: LatLng) {
     /*   gooMap.addPolyline(
            PolylineOptions()
                .add(origin, destination)
                .width(4f)
                .color(android.graphics.Color.RED)
        )*/
        val polylineOptions = PolylineOptions()
            .add(PickUpLoc)
            .add(DropLoc)
            .color(Color.RED)
            .width(5f)
        gooMap.addPolyline(polylineOptions)
    }


}
