package com.pasco.pascocustomer.Driver.StartRiding.Ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.android.volley.AuthFailureError
import com.android.volley.NetworkError
import com.android.volley.NoConnectionError
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.BuildConfig
import com.pasco.pascocustomer.Driver.DriverDashboard.Ui.DriverDashboardActivity
import com.pasco.pascocustomer.Driver.StartRiding.ViewModel.AfterStartTripViewModel
import com.pasco.pascocustomer.Driver.StartRiding.ViewModel.CompleteRideViewModel
import com.pasco.pascocustomer.Driver.StartRiding.ViewModel.GetRouteUpdateResponse
import com.pasco.pascocustomer.Driver.StartRiding.ViewModel.GetRouteUpdateViewModel
import com.pasco.pascocustomer.Driver.StartRiding.ViewModel.StartTripViewModel
import com.pasco.pascocustomer.Driver.UpdateLocation.UpdateLocationViewModel
import com.pasco.pascocustomer.Driver.UpdateLocation.UpdationLocationBody
import com.pasco.pascocustomer.Driver.customerDetails.CustomerDetailsActivity
import com.pasco.pascocustomer.Driver.driverFeedback.DriverFeedbackBody
import com.pasco.pascocustomer.Driver.driverFeedback.DriverFeedbackModelView
import dagger.hilt.android.AndroidEntryPoint
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.chat.ChatActivity
import com.pasco.pascocustomer.dashboard.UserDashboardActivity
import com.pasco.pascocustomer.databinding.ActivityDriverStartRidingBinding
import com.pasco.pascocustomer.utils.ErrorUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException
import java.io.IOException
import java.text.DecimalFormat
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@AndroidEntryPoint
class DriverStartRidingActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityDriverStartRidingBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var activity: Activity
    private val startTripViewModel: StartTripViewModel by viewModels()
    private val getRouteUpdateViewModel: GetRouteUpdateViewModel by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }
    private var Plat: Double = 0.0
    private var Plon: Double = 0.0
    private var Dlan: Double = 0.0
    private var Dlon: Double = 0.0
    private lateinit var mMap: GoogleMap
    private lateinit var pickupLocation: LatLng
    private lateinit var dropLocation: LatLng
    private var spinnerDriverSId = ""
    private var driverStatus = ""
    private var isDestinationReached = false
    private var routeType: List<GetRouteUpdateResponse.RouteResponseData>? = null
    private val routeTypeStatic: MutableList<String> = mutableListOf()
    private var Bid = ""
    private val afterStartTripViewModel: AfterStartTripViewModel by viewModels()
    private lateinit var updateLocationBody: UpdationLocationBody
    private val updateLocationViewModel: UpdateLocationViewModel by viewModels()
    private val completeRideViewModel: CompleteRideViewModel by viewModels()
    private val driverFeedbackModelView: DriverFeedbackModelView by viewModels()
    private var formattedLatitudeSelect: String = ""
    private var formattedLongitudeSelect: String = ""
    private var city: String? = null
    private var address: String? = null
    private var hasReachedLocation = false
    private var handler: Handler? = null
    private lateinit var runnable: Runnable
    var bottomSheetDialog: BottomSheetDialog? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverStartRidingBinding.inflate(layoutInflater)
        setContentView(binding.root)
//pickup loc
        val pickupLoc = intent.getStringExtra("pickupLoc").toString()
        val dropLoc = intent.getStringExtra("dropLoc").toString()

        Bid = intent.getStringExtra("BookId").toString()
        Plat = intent.getStringExtra("latitudePickUp")?.toDoubleOrNull() ?: 0.0
        Plon = intent.getStringExtra("longitudePickUp")?.toDoubleOrNull() ?: 0.0
        Dlan = intent.getStringExtra("latitudeDrop")?.toDoubleOrNull() ?: 0.0
        Dlon = intent.getStringExtra("longitudeDrop")?.toDoubleOrNull() ?: 0.0
        val deliveryTime = intent.getStringExtra("deltime")
        val image = intent.getStringExtra("image").toString()


        // Log the values for checking
        Log.e("IntentValues", "BookId: $Bid")
        Log.e("IntentValues", "latitudePickUp: $Plat")
        Log.e("IntentValues", "longitudePickUp: $Plon")
        Log.e("IntentValues", "latitudeDrop: $Dlan")
        Log.e("IntentValues", "longitudeDrop: $Dlon")
        Log.e("IntentValues", "deltime: $deliveryTime")
        Log.e("IntentValues", "image: $image")

        Log.d("PickupLocation", "Latitude: $Plat, Longitude: $Plon")
        Log.d("DropLocation", "Latitude: $Dlan, Longitude: $Dlon")

        activity = this
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapStart) as SupportMapFragment
        mapFragment.getMapAsync(this)

        pickupLocation = LatLng(Plat, Plon)
        dropLocation = LatLng(Dlan, Dlon)

        binding.finishTripTextView.setOnClickListener {
            completedRideApi()
            completedRideObserver()
        }


        driverStatusList()
        driverStatusObserver()
        // Request location updates
        requestLocationUpdates()
        //call observer
        updateLocationObserver()
        handler = Handler(Looper.getMainLooper())


        binding.routeSpinnerSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View?,
                    i: Int,
                    l: Long
                ) {
                    // Toast.makeText(activity, "Country Spinner Working **********", Toast.LENGTH_SHORT).show()

                    val item = binding.routeSpinnerSpinner.selectedItem.toString()
                    if (item == getString(R.string.selectStatus)) {

                    } else {
                        spinnerDriverSId = routeType?.get(i)?.id.toString()
                        Log.e("onItemSelected", spinnerDriverSId)

                        //call vehicleType
                        if (!spinnerDriverSId.isNullOrBlank()) {
                            val sId = spinnerDriverSId
                            startTrip(sId)
                        }
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                    // Do nothing
                }
            }

        binding.imageBackReqRide.setOnClickListener {
            finish()
        }

        afterDetailsObserver()
        //get Api
        if (!Bid.isNullOrBlank()) {
            afterDetailsApi()
        }
        binding.cricleImgUserSR.setOnClickListener {
            val intent = Intent(this@DriverStartRidingActivity, CustomerDetailsActivity::class.java)
            intent.putExtra("customerId", Bid)
            startActivity(intent)
        }
        //call observer
        startTripObserver()


        binding.chatImageStartRidingImageView.setOnClickListener {
            val intent = Intent(this@DriverStartRidingActivity, ChatActivity::class.java)
            startActivity(intent)
        }

        binding.finishTripTextView.setOnClickListener {
            completedRideApi()
            completedRideObserver()
        }


    }

    private fun afterDetailsObserver() {

        afterStartTripViewModel.mAfterTripResponse.observe(this) { response ->

            val dataGet = response.peekContent().data

            binding.pickUpLocDynamic.text = dataGet?.pickupLocation
            binding.dropLocDynamic.text = dataGet?.dropLocation
            // Convert duration to hours and minutes if more than 60 seconds
            dataGet?.duration?.let { durationInSeconds ->
                val formattedDuration = if (durationInSeconds < 60) {
                    durationInSeconds.toString() + " seconds"
                } else {
                    val hours = durationInSeconds / 3600
                    val minutes = (durationInSeconds % 3600) / 60
                    String.format("%02d:%02d", hours, minutes)
                }
                binding.delTimeDynamic.text = formattedDuration
            }

            val url = dataGet?.userImage
            Glide.with(this).load(BuildConfig.IMAGE_KEY + url).into(binding.cricleImgUserSR)
        }

        afterStartTripViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }

    private fun afterDetailsApi() {
        afterStartTripViewModel.getAfterTripsData(Bid, this@DriverStartRidingActivity)
    }


    private fun requestLocationUpdates() {
        // Check for location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
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

        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {

                    runnable = object : Runnable {
                        override fun run() {
                            showAddress(it)
                            handler?.postDelayed(this, 2000) // 2000 milliseconds = 2 seconds
                        }
                    }

                    // Start the periodic task
                    handler?.post(runnable)
                }

            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun showAddress(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude

        Plat = latitude
        Plon = longitude
        formattedLatitudeSelect = String.format("%.4f", Plat)
        formattedLongitudeSelect = String.format("%.4f", Plon)

        /*  Log.e(
              "LocationDetails", "Formatted Latitude: $formattedLatitudeSelect," +
                      " Formatted Longitude: $formattedLongitudeSelect"
          )*/

        GlobalScope.launch(Dispatchers.IO) {
            val geocoder = Geocoder(this@DriverStartRidingActivity, Locale.getDefault())
            try {
                val addresses: List<Address> = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1
                )!!
                if (addresses.isNotEmpty()) {
                    val addressObj = addresses[0]
                    address = addressObj.getAddressLine(0)
                    city = addressObj.locality
                    city?.let { updateUI(it) }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        updateLocationDetails()
    }

    private fun updateLocationDetails() {
        Log.e(
            "LocationDetails", "Formatted Latitude: $formattedLatitudeSelect," +
                    " Formatted Longitude: $formattedLongitudeSelect"
        )
        updateLocationBody = UpdationLocationBody(
            city.toString(),
            address.toString(),
            formattedLatitudeSelect,
            formattedLongitudeSelect
        )
        updateLocationViewModel.updateLocationDriver(activity, updateLocationBody)

    }

    private fun updateLocationObserver() {

        updateLocationViewModel.mUpdateLocationResponse.observe(this) { response ->
            val message = response.peekContent().msg!!
            if (response.peekContent().status.equals("False")) {
                Toast.makeText(this@DriverStartRidingActivity, "$message", Toast.LENGTH_LONG).show()
            } else {
                afterDetailsApi()
                getDistanceApi()
            }
        }
        updateLocationViewModel.errorResponse.observe(this@DriverStartRidingActivity) {
            ErrorUtil.handlerGeneralError(this@DriverStartRidingActivity, it)
            // errorDialogs()
        }
    }

    private fun completedRideApi() {
        completeRideViewModel.getCompletedRideData(progressDialog, activity, Bid)

    }

    private fun completedRideObserver() {
        completeRideViewModel.mCRideResponse.observe(this) { response ->
            val message = response.peekContent().msg!!
            if (response.peekContent().status == "False") {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                showFeedbackPopup()
            }
        }
        startTripViewModel.errorResponse.observe(this) {
            // Handle general errors
            ErrorUtil.handlerGeneralError(this, it)
        }
    }

    private fun showFeedbackPopup() {
        bottomSheetDialog = BottomSheetDialog(this, R.style.TopCircleDialogStyle)
        val view = LayoutInflater.from(this).inflate(R.layout.feedback_popup, null)
        bottomSheetDialog!!.setContentView(view)


        val ratingBar = bottomSheetDialog?.findViewById<RatingBar>(R.id.ratingBar)
        val commentTxt = bottomSheetDialog?.findViewById<EditText>(R.id.commentTxt)
        val submitBtn = bottomSheetDialog?.findViewById<TextView>(R.id.submitBtn)
        val skipBtn = bottomSheetDialog?.findViewById<TextView>(R.id.skipBtn)

        var ratingBars = ""
        ratingBar?.setOnRatingBarChangeListener { _, rating, _ ->
            Toast.makeText(this, "New Rating: $rating", Toast.LENGTH_SHORT).show()
            ratingBars = rating.toString()
        }

        submitBtn?.setOnClickListener {

            feedbackApi(commentTxt?.text.toString(), ratingBars)
            feedbackObserver()
        }
        skipBtn?.setOnClickListener { bottomSheetDialog?.dismiss() }

        val displayMetrics = DisplayMetrics()
        (this as AppCompatActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels
        val halfScreenHeight = screenHeight * .6
        val eightyPercentScreenHeight = screenHeight * .6

        // Set the initial height of the bottom sheet to 50% of the screen height
        val layoutParams = view.layoutParams
        layoutParams.height = halfScreenHeight.toInt()
        view.layoutParams = layoutParams

        var isExpanded = false
        view.setOnClickListener {
            // Expand or collapse the bottom sheet when it is touched
            if (isExpanded) {
                layoutParams.height = halfScreenHeight.toInt()
            } else {
                layoutParams.height = eightyPercentScreenHeight.toInt()
            }
            view.layoutParams = layoutParams
            isExpanded = !isExpanded
        }

        bottomSheetDialog!!.show()

    }

    private fun feedbackApi(commentTxt: String, ratingBars: String) {
        //   val codePhone = strPhoneNo
        val loinBody = DriverFeedbackBody(
            bookingconfirmation = Bid,
            rating = ratingBars,
            feedback = commentTxt
        )
        driverFeedbackModelView.cancelBooking(loinBody, this, progressDialog)
    }

    private fun feedbackObserver() {
        driverFeedbackModelView.progressIndicator.observe(this) {}
        driverFeedbackModelView.mRejectResponse.observe(
            this
        ) {
            val msg = it.peekContent().msg
            Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()

            val intent = Intent(this, DriverDashboardActivity::class.java)
            startActivity(intent)
        }
        driverFeedbackModelView.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@DriverStartRidingActivity, it)
            // errorDialogs()
        }
    }

    private fun getDistanceApi() {
        val urljsonobjGroup =
            "https://maps.googleapis.com/maps/api/distancematrix/json?origins=$formattedLatitudeSelect,$formattedLongitudeSelect&destinations=$Dlan,$Dlon&mode=driving&key=AIzaSyA3KVnFOiaKNlhi4hJB8N2pB8tyoe_rRxQ"
        val jsonObjReqGroup = JsonObjectRequest(
            Request.Method.GET, urljsonobjGroup, null,
            { response ->
                try {
                    val jsonArray = response.getJSONArray("rows")
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject1 = jsonArray.getJSONObject(i)
                        val elements = jsonObject1.getJSONArray("elements")
                        for (r in 0 until elements.length()) {
                            val jsonObject2 = elements.getJSONObject(r) // Use 'r' instead of 'i'
                            val strDistance = jsonObject2.getJSONObject("distance")
                            val strDistanceText = strDistance.getString("text")
                            val strDistanceValue =
                                strDistance.getString("value") // This is in meters
                            val strDuration = jsonObject2.getJSONObject("duration")
                            val strDurationText = strDuration.getString("text")
                            val strDurationValue = strDuration.getString("value")

                            val distanceMeters = strDistanceValue.toDouble()
                            val durationSeconds = strDurationValue.toDouble()

                            val distanceKm = distanceMeters / 1000
                            val distanceMiles = distanceMeters / 1609.34
                            val distanceFeet = distanceMeters * 3.28084

                            val durationMinutes = durationSeconds / 60

                            val formattedDistanceKm =
                                DecimalFormat("##.##").format(distanceKm).toDouble()
                            val formattedDistanceMiles =
                                DecimalFormat("##.##").format(distanceMiles).toDouble()
                            val formattedDistanceFeet =
                                DecimalFormat("##.##").format(distanceFeet).toDouble()
                            val formattedDuration =
                                DecimalFormat("##.##").format(durationMinutes).toDouble()

                            /* binding.distanceTxt.text = "$formattedDistanceKm km"
                            binding.durationTimeTxt.text = "$formattedDuration mins"*/
                            if (distanceMeters < 50) {
                                // binding.finishTripTextView.visibility = View.VISIBLE
                                // completedRideApi()
                                //completedRideObserver()
                            } else {
                                //  binding.finishTripTextView.visibility = View.GONE
                            }
                            Log.e(
                                "BookMap",
                                "$formattedDistanceKm km, $formattedDistanceMiles miles, $formattedDistanceFeet feet, $formattedDuration mins"
                            )
                        }
                    }
                } catch (e: JSONException) {
                    PascoApp.instance.getRequestQueue().cancelAll("survey_list")
                }
            },
            { error ->
                PascoApp.instance.getRequestQueue().cancelAll("survey_list")
                val errorMessage = when (error) {
                    is NetworkError -> "Cannot connect to Internet...Please check your connection!"
                    is ServerError -> "The server could not be found. Please try again after some time!!"
                    is AuthFailureError -> "Cannot connect to Internet...Please check your connection!"
                    is ParseError -> "Parsing error! Please try again after some time!!"
                    is NoConnectionError -> "Cannot connect to Internet...Please check your connection!"
                    is TimeoutError -> "Connection TimeOut! Please check your internet connection."
                    else -> error.message ?: "An error occurred"
                }
                Toast.makeText(this@DriverStartRidingActivity, errorMessage, Toast.LENGTH_SHORT)
                    .show()
            }
        )
        PascoApp.instance.addToRequestQueue(jsonObjReqGroup, "survey_list")
    }


    private fun driverStatusObserver() {
        getRouteUpdateViewModel.progressIndicator.observe(this, Observer {
            // Handle progress indicator changes if needed
        })

        getRouteUpdateViewModel.mGetRouteUpdate.observe(this) { response ->
            val content = response.peekContent()
            val message = content.msg ?: return@observe
            routeType = content.data!!

            // Clear the list before adding new items
            routeTypeStatic.clear()

            for (element in routeType!!) {
                element.status?.let { it1 -> routeTypeStatic.add(it1) }
            }
            val dAdapter =
                SpinnerAdapter(
                    this,
                    R.layout.custom_service_type_spinner,
                    routeTypeStatic
                )
            dAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            //dAdapter.addAll(strCatNameList)
            dAdapter.add(getString(R.string.selectStatus))
            binding.routeSpinnerSpinner.adapter = dAdapter
            binding.routeSpinnerSpinner.setSelection(dAdapter.count)
            dAdapter.getPosition(getString(R.string.selectStatus))

            if (response.peekContent().status.equals("False")) {

            } else {


            }
        }

        getRouteUpdateViewModel.errorResponse.observe(this) {
            // Handle general errors
            ErrorUtil.handlerGeneralError(this, it)
        }
    }

    private fun driverStatusList() {
        getRouteUpdateViewModel.getDriverStatusData(
            progressDialog,
            this
        )
    }

    private fun startTripObserver() {
        startTripViewModel.mStartTripResponse.observe(this) { response ->
            val message = response.peekContent().msg!!
            if (response.peekContent().status == "True") {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()


            }
        }

        startTripViewModel.errorResponse.observe(this) {
            // Handle general errors
            ErrorUtil.handlerGeneralError(this, it)
        }
    }

    private fun startTrip(sId: String) {
        val Iddd = sId
        startTripViewModel.getStartTripData(progressDialog, activity, Bid, Iddd)
    }


    private fun updateUI(city: String) {
        // Update the UI on the main thread
        runOnUiThread {

        }
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isTrafficEnabled = true
        // Add markers for pickup and drop locations
        mMap.addMarker(MarkerOptions().position(pickupLocation).title("Pickup Location"))
        mMap.addMarker(MarkerOptions().position(dropLocation).title("Drop Location"))

        // Move camera to the initial pickup location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickupLocation, 14f))

        // Enable my location button and request location permission
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        mMap.isMyLocationEnabled = true

        // Get and draw route when map is ready
        getLastLocationAndDrawRoute()

        mMap.setOnMyLocationChangeListener { location ->
            val userLocation = LatLng(location.latitude, location.longitude)
            checkDestinationReached(userLocation, dropLocation)
        }
    }


    private fun getLastLocationAndDrawRoute() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    drawRoute(LatLng(location.latitude, location.longitude))
                    Log.e(
                        "location",
                        "location.." + location.latitude + "longitude " + location.longitude
                    )
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Failed to get location: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun checkDestinationReached(userLocation: LatLng, destinationLocation: LatLng) {
        val distanceToDestination = calculateDistance(userLocation, destinationLocation)
        val thresholdDistance = 100 // Define your threshold distance in meters

        if (distanceToDestination <= thresholdDistance && !isDestinationReached) {
            // Destination reached
            isDestinationReached = true
            Toast.makeText(this, "You have reached your destination!", Toast.LENGTH_SHORT).show()
            // Perform any action you want when the destination is reached
        }
    }

    // Calculate distance between two LatLng points using Haversine formula
    private fun calculateDistance(startLatLng: LatLng, endLatLng: LatLng): Float {
        val earthRadius = 6371000 // Radius of the Earth in meters
        val dLat = Math.toRadians((endLatLng.latitude - startLatLng.latitude).toDouble())
        val dLng = Math.toRadians((endLatLng.longitude - startLatLng.longitude).toDouble())
        val a = (sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(startLatLng.latitude)) * cos(Math.toRadians(endLatLng.latitude)) *
                sin(dLng / 2) * sin(dLng / 2))
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (earthRadius * c).toFloat()
    }

    private fun drawRoute(latLng: LatLng) {
        val apiKey = "AIzaSyA3KVnFOiaKNlhi4hJB8N2pB8tyoe_rRxQ" // Replace with your actual API key
        val context = GeoApiContext.Builder()
            .apiKey(apiKey)
            .build()

        Log.e("location", "location.." + latLng.latitude + "longitude " + latLng.longitude)
        val result: DirectionsResult = DirectionsApi.newRequest(context)
            .mode(TravelMode.DRIVING)
            .origin("${latLng.latitude},${latLng.longitude}")
            .destination("${dropLocation.latitude},${dropLocation.longitude}")
            .await()

        // Decode polyline and draw on map
        if (result.routes.isNotEmpty()) {
            val points = decodePolyline(result.routes[0].overviewPolyline.encodedPath)
            mMap.addPolyline(PolylineOptions().addAll(points).color(android.graphics.Color.BLUE))
        } else {
            Log.e("drawRoute", "No routes found")
            Toast.makeText(this, "No routes found", Toast.LENGTH_SHORT).show()
        }
        // mMap.clear() // Clear previous route

    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else (result shr 1)
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else (result shr 1)
            lng += dlng
            val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(p)
        }
        return poly
    }


    class SpinnerAdapter(context: Context, textViewResourceId: Int, smonking: List<String>) :
        ArrayAdapter<String>(context, textViewResourceId, smonking) {

        override fun getCount(): Int {
            val count = super.getCount()
            return if (count > 0) count - 1 else count
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove callbacks to prevent memory leaks
        handler?.removeCallbacks(runnable)
    }
}