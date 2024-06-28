package com.pasco.pascocustomer.customer.activity.track

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
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
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.chat.ChatActivity
import com.pasco.pascocustomer.customer.activity.track.cancelbooking.CancelBookingBody
import com.pasco.pascocustomer.customer.activity.track.cancelbooking.CancelBookingModelView
import com.pasco.pascocustomer.customer.activity.track.trackmodel.TrackLocationBody
import com.pasco.pascocustomer.customer.activity.track.trackmodel.TrackLocationDetailsModelView
import com.pasco.pascocustomer.customer.activity.track.trackmodel.TrackLocationModelView
import com.pasco.pascocustomer.customerfeedback.CustomerFeedbackBody
import com.pasco.pascocustomer.customerfeedback.CustomerFeedbackModelView
import com.pasco.pascocustomer.dashboard.UserDashboardActivity
import com.pasco.pascocustomer.databinding.ActivityTrackBinding
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Suppress("DEPRECATION")
@AndroidEntryPoint
class TrackActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var pickupLocation: LatLng // Define your pickup location
    private var dropLocation: LatLng? = null// Define your drop location

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityTrackBinding

    private val trackModelView: TrackLocationModelView by viewModels()
    private val trackDetailsModelView: TrackLocationDetailsModelView by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }
    private val feedbackModelView: CustomerFeedbackModelView by viewModels()
    var bottomSheetDialog: BottomSheetDialog? = null
    private var dialog: Dialog? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    private var isDestinationReached = false

    private var pickupLatitude = ""
    private var pickupLongitude = ""
    private var dropLatitude = ""
    private var dropLongitude = ""
    private var bookingId = ""
    private var lat = ""
    private var long = ""
    private var verificationCode = ""
    private var isClick = true

    private var handler: Handler? = null
    private lateinit var runnable: Runnable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageBackReqRide.setOnClickListener { finish() }
        //   showFeedbackPopup()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapsa) as SupportMapFragment
        mapFragment.getMapAsync(this)

        pickupLatitude = intent.getStringExtra("pickupLatitude").toString()
        pickupLongitude = intent.getStringExtra("pickupLongitude").toString()
        bookingId = intent.getStringExtra("bookingId").toString()
        verificationCode = intent.getStringExtra("verificationCode").toString()


        binding.textViewSeeDetails.setOnClickListener {

            if (isClick) {
                binding.textViewSeeDetails.text = "Hide Details"
                binding.NewConstraintDetails.visibility = View.VISIBLE
                isClick = false
            } else {
                binding.NewConstraintDetails.visibility = View.GONE
                binding.textViewSeeDetails.text = "Show Details"
                isClick = true
            }
        }

        // Los Angeles


        binding.chatBtn.setOnClickListener {
            val intent = Intent(this@TrackActivity, ChatActivity::class.java)
            startActivity(intent)
        }
        handler = Handler(Looper.getMainLooper())


        runnable = object : Runnable {
            override fun run() {
                locationLatApi()
                locationLatObserver()
                handler?.postDelayed(this, 10000) // 2000 milliseconds = 2 seconds
            }
        }

        // Start the periodic task
        handler?.post(runnable)

        locationDetailsApi()
        locationDetailsObserver()


    }

    private fun locationDetailsApi() {

        val bookingBody = TrackLocationBody(
            driverbookedid = bookingId
        )
        trackDetailsModelView.trackLocation(bookingBody, this)
    }

    private fun locationDetailsObserver() {

        trackDetailsModelView.mDetailsResponse.observe(this) { response ->

            val dataGet = response.peekContent().data

            binding.pickUpLocBidd.text = response.peekContent().data?.pickupLocation
            binding.dropLocBidd.text = response.peekContent().data?.dropLocation
            binding.orderIdStaticTextView.text = response.peekContent().data?.bidPrice.toString()

            Log.e("ShowDetails", "API....Details")
            val distance = response.peekContent().data?.totalDistance

            val formattedTotalDistance =
                "%.1f".format(response.peekContent().data?.totalDistance ?: 0.0)
            binding.totalDistanceBidd.text = "$formattedTotalDistance km"

            binding.verificationCode.text = verificationCode

            val url = response.peekContent().data!!.image
            Glide.with(this).load(BuildConfig.IMAGE_KEY + url).into(binding.profileImgUserBid)
            Log.e("AAAAAA", "0001")

            val duration = response?.peekContent()?.data!!.duration.toString()
            val durationInSeconds = duration.toIntOrNull() ?: 0
            val formattedDuration = if (durationInSeconds < 60) {
                "$durationInSeconds sec"
            } else {
                val hours = durationInSeconds / 3600
                val minutes = (durationInSeconds % 3600) / 60
                val seconds = durationInSeconds % 60
                if (hours > 0) {
                    String.format("%d hr %02d min", hours, minutes)
                } else {
                    String.format("%d min ", minutes)
                }
            }

            binding.routeTime.text = formattedDuration
        }

        trackDetailsModelView.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isTrafficEnabled = true

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                dropLocation?.let { it1 -> updateRoute(LatLng(it.latitude, it.longitude), it1) }
                zoomMapToLocation(LatLng(it.latitude, it.longitude))
            }
        }
        mMap.isMyLocationEnabled = true
    }


    private fun drawRoute(startLatLng: LatLng, endLatLng: LatLng?) {
        val apiKey = "AIzaSyA3KVnFOiaKNlhi4hJB8N2pB8tyoe_rRxQ" // Replace with your actual API key
        val context = GeoApiContext.Builder().apiKey(apiKey).build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result: DirectionsResult =
                    DirectionsApi.newRequest(context).mode(TravelMode.DRIVING)
                        .origin("${startLatLng.latitude},${startLatLng.longitude}")
                        .destination("${endLatLng?.latitude},${endLatLng?.longitude}").await()

                Log.e(
                    "location",
                    "location.." + endLatLng?.latitude + "longitude " + endLatLng?.longitude
                )
                // Decode polyline and draw on map
                val points = decodePolyline(result.routes[0].overviewPolyline.encodedPath)

                withContext(Dispatchers.Main) {
                    // Clear previous route
                    mMap.clear()
                    // Add polyline to map
                    mMap.addPolyline(PolylineOptions().addAll(points).color(R.color.earth_yellow))
                }
            } catch (e: Exception) {
                Log.e("DrawRoute", "Error drawing route: ${e.message}")
            }
        }
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

    private fun locationLatApi() {
        val bookingBody = TrackLocationBody(
            driverbookedid = bookingId
        )
        trackModelView.trackLocation(bookingBody, this)
    }

    private fun locationLatObserver() {
        trackModelView.mRejectResponse.observe(this) { response ->

            val driverCurrentLat = response.peekContent().data?.currentLatitude.toString()
            val driverCurrentLong = response.peekContent().data?.currentLongitude.toString()
            val userPickUpLat = response.peekContent().data?.pickupLatitude.toString()
            val userPickUpLong = response.peekContent().data?.pickupLongitude.toString()
            val userDropLat = response.peekContent().data?.dropLatitude.toString()
            val userDropLong = response.peekContent().data?.dropLongitude.toString()

            Log.e("ShowDetails", "API....Repeat...Details")

            drawRoute(
                LatLng(driverCurrentLat.toDouble(), driverCurrentLong.toDouble()),
                LatLng(userPickUpLat.toDouble(), userPickUpLong.toDouble())
            )
            if (response.peekContent().data?.driverStatus == null) {

            } else {
                if (response.peekContent().data!!.bookingStatus == "Completed") {
                    handler?.removeCallbacks(runnable)
                    binding.onTheWayTxt.text = response.peekContent().data?.bookingStatus
                    showFeedbackPopup()
                } else {
                    binding.onTheWayTxt.text = response.peekContent().data?.driverStatus
                    Log.e("ReachedAAA", "aa" + response.peekContent().data?.driverStatus)

                    if (response.peekContent().data!!.driverStatus == "Reached at  PickUp Location") {
                        // Clear the first route
                        mMap.clear()
                        // Draw the second route from pickup to drop location
                        drawRoute(
                            LatLng(driverCurrentLat.toDouble(), driverCurrentLong.toDouble()),
                            LatLng(userDropLat.toDouble(), userDropLong.toDouble())
                        )
                    } else {
                        // Draw the first route from driver current location to pickup location
                        drawRoute(
                            LatLng(driverCurrentLat.toDouble(), driverCurrentLong.toDouble()),
                            LatLng(userPickUpLat.toDouble(), userPickUpLong.toDouble())
                        )
                    }
                }

            }
        }

        trackModelView.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove callbacks to prevent memory leaks
        handler?.removeCallbacks(runnable)
    }

    private fun updateRoute(currentLocation: LatLng, destination: LatLng) {
        val context =
            GeoApiContext.Builder().apiKey("AIzaSyA3KVnFOiaKNlhi4hJB8N2pB8tyoe_rRxQ").build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val directionsResult: DirectionsResult = DirectionsApi.newRequest(context).origin(
                    com.google.maps.model.LatLng(
                        currentLocation.latitude, currentLocation.longitude
                    )
                ).destination(
                    com.google.maps.model.LatLng(
                        destination.latitude, destination.longitude
                    )
                ).await()

                val path = directionsResult.routes[0].overviewPolyline.decodePath().map {
                    LatLng(it.lat, it.lng)
                }

                withContext(Dispatchers.Main) {
                    mMap.clear()
                    mMap.addPolyline(PolylineOptions().addAll(path).color(R.color.purple_700))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10f))
                }
            } catch (e: Exception) {
                Log.e("UpdateRoute", "Error updating route: ${e.message}")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mMap.isMyLocationEnabled = true

                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        dropLocation?.let { it1 ->
                            updateRoute(
                                LatLng(it.latitude, it.longitude), it1
                            )
                        }
                    }
                }
            }
        }
    }


    private fun showFeedbackPopup() {
        bottomSheetDialog = BottomSheetDialog(this, R.style.TopCircleDialogStyle)
        val view = LayoutInflater.from(this).inflate(R.layout.feedback_popup, null)
        bottomSheetDialog!!.setContentView(view)

        Log.e("SHowFeed", "AAAA")
        val ratingBar = bottomSheetDialog?.findViewById<RatingBar>(R.id.ratingBar)
        val commentTxt = bottomSheetDialog?.findViewById<EditText>(R.id.commentTxt)
        val submitBtn = bottomSheetDialog?.findViewById<TextView>(R.id.submitBtn)
        val skipBtn = bottomSheetDialog?.findViewById<TextView>(R.id.skipBtn)

        var ratingBars = ""
        ratingBar?.setOnRatingBarChangeListener { _, rating, _ ->
            ratingBars = rating.toString()
        }

        submitBtn?.setOnClickListener {
            feedbackApi(commentTxt?.text.toString(), ratingBars)
            feedbackObserver()
        }
        skipBtn?.setOnClickListener { bottomSheetDialog?.dismiss() }

        // Get the window of the dialog and set its height to match parent
        val dialogWindow = bottomSheetDialog?.window
        val layoutParams = dialogWindow?.attributes
        layoutParams?.height = WindowManager.LayoutParams.MATCH_PARENT
        dialogWindow?.attributes = layoutParams

        bottomSheetDialog?.show()
    }


    private fun feedbackApi(commentTxt: String, ratingBars: String) {
        //   val codePhone = strPhoneNo
        val loinBody = CustomerFeedbackBody(
            bookingconfirmation = bookingId, rating = ratingBars, feedback = commentTxt
        )
        feedbackModelView.cancelBooking(loinBody, this, progressDialog)
    }

    private fun feedbackObserver() {
        feedbackModelView.progressIndicator.observe(this) {}
        feedbackModelView.mRejectResponse.observe(
            this
        ) {
            val msg = it.peekContent().msg
            Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()

            val intent = Intent(this, UserDashboardActivity::class.java)
            startActivity(intent)
        }
        feedbackModelView.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@TrackActivity, it)
            // errorDialogs()
        }
    }

    private fun zoomMapToLocation(location: LatLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }


}
