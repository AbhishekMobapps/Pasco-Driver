package com.pasco.pascocustomer.customer.activity.track

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import com.pasco.pascocustomer.BuildConfig
import com.pasco.pascocustomer.Driver.DriverWallet.DriverWalletActivity
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.chat.ChatActivity
import com.pasco.pascocustomer.customer.activity.track.completededuct.CompletedDeductAmountBody
import com.pasco.pascocustomer.customer.activity.track.completededuct.CompletedDeductAmtModelView
import com.pasco.pascocustomer.customer.activity.track.trackmodel.TrackLocationBody
import com.pasco.pascocustomer.customer.activity.track.trackmodel.TrackLocationDetailsModelView
import com.pasco.pascocustomer.customer.activity.track.trackmodel.TrackLocationModelView
import com.pasco.pascocustomer.customerfeedback.CustomerFeedbackBody
import com.pasco.pascocustomer.customerfeedback.CustomerFeedbackModelView
import com.pasco.pascocustomer.dashboard.UserDashboardActivity
import com.pasco.pascocustomer.databinding.ActivityTrackBinding
import com.pasco.pascocustomer.language.Originator
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@Suppress("DEPRECATION")
@AndroidEntryPoint
class TrackActivity : Originator(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private var dropLocation: LatLng? = null// Define your drop location

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityTrackBinding
    private val trackModelView: TrackLocationModelView by viewModels()
    private val trackDetailsModelView: TrackLocationDetailsModelView by viewModels()
    private val feedbackModelView: CustomerFeedbackModelView by viewModels()
    private val completeDeduct: CompletedDeductAmtModelView by viewModels()
    var bottomSheetDialog: BottomSheetDialog? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }


    private var pickupLatitude = ""
    private var pickupLongitude = ""
    private var bookingId = ""
    private var lat = ""
    private var long = ""
    private var verificationCode = ""
    private var isClick = true
    private var mMarkerOptions: Marker? = null


    private var completeStatus = true

    var mHandler: Handler? = null
    private var cars: Bitmap? = null

    private lateinit var sharedPreferencesLanguageName: SharedPreferences
    private var language = ""
    private var languageId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackBinding.inflate(layoutInflater)
        setContentView(binding.root)


        sharedPreferencesLanguageName = getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
        language = sharedPreferencesLanguageName.getString("language_text", "").toString()
        languageId = sharedPreferencesLanguageName.getString("languageId", "").toString()


        if (Objects.equals(language, "ar")) {
            binding.imageBackReqRide.setImageResource(R.drawable.next)

        }

        binding.imageBackReqRide.setOnClickListener { finish() }
        //   showFeedbackPopup()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapsa) as SupportMapFragment
        mapFragment.getMapAsync(this)

        pickupLatitude = intent.getStringExtra("pickupLatitude").toString()
        pickupLongitude = intent.getStringExtra("pickupLongitude").toString()
        bookingId = intent.getStringExtra("bookingId").toString()
        verificationCode = intent.getStringExtra("verificationCode").toString()

        Log.e("bookingIdAAA", "bookingId..$bookingId")


        binding.textViewSeeDetails.setOnClickListener {

            if (isClick) {
                binding.textViewSeeDetails.text = R.string.hide_details.toString()
                binding.NewConstraintDetails.visibility = View.VISIBLE
                isClick = false
            } else {
                binding.NewConstraintDetails.visibility = View.GONE
                binding.textViewSeeDetails.text = R.string.show_details.toString()
                isClick = true
            }
        }

        // Los Angeles
        //handler = Handler(Looper.getMainLooper())


        /* runnable = object : Runnable {
             override fun run() {
                 locationLatApi()
                 locationLatObserver()
                 handler?.postDelayed(this, 10000) // 2000 milliseconds = 2 seconds
             }
         }
 */
        // Start the periodic task
        // handler?.post(runnable)

        binding.chatBtn.setOnClickListener {
            val intent = Intent(this@TrackActivity, ChatActivity::class.java)
            startActivity(intent)
        }

        val dcar = ResourcesCompat.getDrawable(resources, R.drawable.man, null)
        val widthcar = 95
        val heightcar = 95
        dcar!!.level = 1234
        val bdcar = dcar.current as BitmapDrawable
        val bcar = bdcar.bitmap
        cars = Bitmap.createScaledBitmap(bcar, widthcar, heightcar, false)

        handlerStart()
        locationDetailsApi()
        locationDetailsObserver()
        locationLatApi()
        locationLatObserver()

    }


    private fun handlerStart() {
        mHandler = Handler(Looper.getMainLooper())
        mHandler!!.postDelayed(mRunnable, 10000)
    }

    private val mRunnable = object : Runnable {
        override fun run() {

            locationLatApi()
            locationLatObserver()
            mHandler?.postDelayed(this, 10000)
        }
    }

    private fun locationDetailsApi() {

        val bookingBody = TrackLocationBody(
            driverbookedid = bookingId,
            language = languageId
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
            }
        }
        mMap.isMyLocationEnabled = true
    }


    private fun locationLatApi() {
        val bookingBody = TrackLocationBody(
            driverbookedid = bookingId,
            language = languageId
        )
        trackModelView.trackLocation(bookingBody, this)
    }

    private fun locationLatObserver() {
        trackModelView.mRejectResponse.observe(this) { response ->


            val status = response.peekContent().status
            if (status == "True") {

            }
            val driverCurrentLat = response.peekContent().data?.currentLatitude.toString()
            val driverCurrentLong = response.peekContent().data?.currentLongitude.toString()
            val userPickUpLat = response.peekContent().data?.pickupLatitude.toString()
            val userPickUpLong = response.peekContent().data?.pickupLongitude.toString()
            val userDropLat = response.peekContent().data?.dropLatitude.toString()
            val userDropLong = response.peekContent().data?.dropLongitude.toString()

            Log.e("ShowDetails", "API....Repeat...Details")

            val driverLatLng = LatLng(driverCurrentLat.toDouble(), driverCurrentLong.toDouble())
            val userDropLatLng = LatLng(userDropLat.toDouble(), userDropLong.toDouble())

            drawRoute(
                LatLng(driverCurrentLat.toDouble(), driverCurrentLong.toDouble()),
                LatLng(userPickUpLat.toDouble(), userPickUpLong.toDouble())
            )

            val driverMarker =
                BitmapDescriptorFactory.fromResource(R.drawable.man) // replace with your image name
            val pickUpMarker =
                BitmapDescriptorFactory.fromResource(R.drawable.man) // replace with your image name


            mMarkerOptions =
                mMap?.addMarker(
                    MarkerOptions().position(driverLatLng!!)
                        .anchor(0.5f, 0.5f)
                        .title("Driver Location").icon(
                            BitmapDescriptorFactory.fromBitmap(cars!!)
                        )
                )
            Log.e("ReachedAAS", "Statusss..Out")


            if (response.peekContent().data?.driverStatus != null) {
                if (response.peekContent().data!!.bookingStatus == "Completed") {
                    mHandler?.removeCallbacks(mRunnable)
                    binding.onTheWayTxt.text = response.peekContent().data?.bookingStatus

                    if (response.peekContent().data!!.paymentMethod == "Cash") {
                        showFeedbackPopup()
                    } else {
                        if (completeStatus) {
                            Log.e("ReachedAAS", "Statusss..in")
                            deductAmountApi(response.peekContent().data!!.leftoverAmount)
                            deductAmountObserver()
                            completeStatus = false
                        }
                    }


                } else {
                    binding.onTheWayTxt.text = response.peekContent().data?.driverStatus
                    if (response.peekContent().data!!.driverStatus == "Reached at PickUp Location") {
                        // Clear the first route
                        mMap.clear()
                        // Draw the second route from pickup to drop location


                        mMarkerOptions =
                            mMap?.addMarker(
                                MarkerOptions().position(driverLatLng!!)
                                    .anchor(0.5f, 0.5f)
                                    .title(getString(R.string.Driver_Location)).icon(
                                        BitmapDescriptorFactory.fromBitmap(cars!!)
                                    )
                            )



                        mMarkerOptions =
                            mMap?.addMarker(
                                MarkerOptions().position(userDropLatLng!!)
                                    .anchor(0.5f, 0.5f)
                                    .title(getString(R.string.Driver_Location)).icon(
                                        BitmapDescriptorFactory.fromBitmap(cars!!)
                                    )
                            )

                        Log.e("ReachedAAA", "aa......." + response.peekContent().data?.driverStatus)
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
                        Log.e(
                            "ReachedAAA",
                            "aa ELSE..." + response.peekContent().data?.driverStatus
                        )
                    }
                }
            }
        }

        trackModelView.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }


    private fun updateRoute(currentLocation: LatLng, destination: LatLng) {
        val context =
            GeoApiContext.Builder().apiKey("AIzaSyA_VxG35IaFz_h_F0G_786p77XvwRKG_WM").build()

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
        skipBtn?.setOnClickListener {
            val intent = Intent(this, UserDashboardActivity::class.java)
            startActivity(intent)
            finish()
            bottomSheetDialog?.dismiss()
        }

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
            bookingconfirmation = bookingId,
            rating = ratingBars,
            feedback = commentTxt,
            language = languageId
        )
        feedbackModelView.cancelBooking(loinBody, this)
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
            finish()
        }
        feedbackModelView.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@TrackActivity, it)
            // errorDialogs()
        }
    }


    private fun drawRoute(mOrigin: LatLng, mDestination: LatLng) {
        val apiKey = "AIzaSyA_VxG35IaFz_h_F0G_786p77XvwRKG_WM" // Replace with your actual API key
        val context = GeoApiContext.Builder()
            .apiKey(apiKey)
            .build()

        Log.e(
            "locationAAA",
            "location.." + mOrigin.latitude + "longitude " + mOrigin.longitude
        )
        val result: DirectionsResult = DirectionsApi.newRequest(context)
            .mode(TravelMode.DRIVING)
            .origin("${mOrigin.latitude},${mOrigin.longitude}")
            .destination("${mDestination.latitude},${mDestination.longitude}")
            .await()


        //Decode polyline and draw on map
        val points = decodePolyline(result.routes[0].overviewPolyline.encodedPath)

        //Add polyline to map
        val polyline =
            mMap?.addPolyline(PolylineOptions().addAll(points).color(Color.YELLOW).width(8.0F))
        //mMap.clear() // Clear previous route

        zoomToFitPolyline(polyline!!)
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = java.util.ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else (result shr 1)
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
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

    private fun zoomToFitPolyline(polyline: Polyline) {
        val boundsBuilder = LatLngBounds.Builder()
        for (latLng in polyline.points) {
            boundsBuilder.include(latLng)
        }
        val bounds = boundsBuilder.build()

        val padding = 100 // offset from edges of the map in pixels
        // Move the camera to fit the polyline within the screen
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        mMap?.animateCamera(cameraUpdate)


    }

    private fun multipleEventAddPopup() {
        val dialogView = layoutInflater.inflate(R.layout.wallet_not_amount, null)
        val builder = AlertDialog.Builder(this).setView(dialogView)
        val dialog = builder.create()
        //venueTitlePopUp = dialogView.findViewById(R.id.venueTitlePopUp)

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        //venueTitlePopUp.setText(StEventTitlepopup)

    }

    private fun deductAmountApi(leftoverAmount: String?) {
        val loinBody = CompletedDeductAmountBody(
            payment_amount = leftoverAmount!!,
            payment_type = "wallet",
            language = languageId
        )
        completeDeduct.deductAmount(bookingId, loinBody, this@TrackActivity)
    }

    private fun deductAmountObserver() {
        completeDeduct.progressIndicator.observe(this) {}
        completeDeduct.mRejectResponse.observe(
            this
        ) {
            val msg = it.peekContent().msg
            val status = it.peekContent().status

            if (status == "True") {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                showFeedbackPopup()

            } else {
                showWalletRequirementPopup()
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }

        }
        completeDeduct.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
            // errorDialogs()
        }
    }

    private fun showWalletRequirementPopup() {
        val message =getString(R.string.Please_add_amount_in_your_wallet)

        val builder = AlertDialog.Builder(this@TrackActivity)
        builder.setTitle(getString(R.string.Insufficient_wallet_amount))
        builder.setMessage(message)
        builder.setPositiveButton(getString(R.string.Add_Funds)) { dialog, _ ->
            val intent = Intent(this, DriverWalletActivity::class.java)
            intent.putExtra("addWallet", "wallet")
            startActivity(intent)
            finish()
            dialog.dismiss()
        }

        builder.setNegativeButton(getString(R.string.skip)) { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

}
