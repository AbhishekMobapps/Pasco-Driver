package com.pasco.pascocustomer.Driver.StartRiding.Ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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
import com.pasco.pascocustomer.Driver.StartRiding.deliveryproof.DeliveryProofViewModel
import com.pasco.pascocustomer.Driver.StartRiding.deliveryproof.MarkerData
import com.pasco.pascocustomer.Driver.UpdateLocation.UpdateLocationViewModel
import com.pasco.pascocustomer.Driver.UpdateLocation.UpdationLocationBody
import com.pasco.pascocustomer.Driver.adapter.PoiInfoAdapter
import com.pasco.pascocustomer.Driver.customerDetails.CustomerDetailsActivity
import com.pasco.pascocustomer.Driver.driverFeedback.DriverFeedbackBody
import com.pasco.pascocustomer.Driver.driverFeedback.DriverFeedbackModelView
import com.pasco.pascocustomer.Driver.emergencyhelp.Ui.EmergencyMainActivity
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.chat.ChatActivity
import com.pasco.pascocustomer.databinding.ActivityDriverStartRidingBinding
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import java.io.File
import java.io.FileOutputStream
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
    private lateinit var poiLocation: LatLng
    private var spinnerDriverSId = ""
    private var isDestinationReached = false
    private var routeType: List<GetRouteUpdateResponse.RouteResponseData>? = null
    private val routeTypeStatic: MutableList<String> = mutableListOf()
    private var Bid = ""
    private var driStatus = ""
    private var driId = ""
    private var orderStatusDriverR = ""
    private var userId = ""
    private val afterStartTripViewModel: AfterStartTripViewModel by viewModels()
    private lateinit var updateLocationBody: UpdationLocationBody
    private val updateLocationViewModel: UpdateLocationViewModel by viewModels()
    private val completeRideViewModel: CompleteRideViewModel by viewModels()
    private val driverFeedbackModelView: DriverFeedbackModelView by viewModels()
    private var formattedLatitudeSelect: String = ""
    private var formattedLongitudeSelect: String = ""
    private lateinit var formattedLatitudeLat: LatLng
    private lateinit var formatteddropLat: LatLng
    private var city: String? = null
    private var address: String? = null
    private var hasReachedLocation = false
    private var handler: Handler? = null
    private lateinit var runnable: Runnable
    private var selectedImageFile: File? = null
    private var imageUrl: String? = null
    private val cameraPermissionCode = 101
    private lateinit var savedImggSelectProof: CircleImageView
    var bottomSheetDialog: BottomSheetDialog? = null
    private val deliveryProofViewModel: DeliveryProofViewModel by viewModels()
    private lateinit var poiName: String
    private lateinit var poiType: String
    private lateinit var couponCode: String
    private lateinit var startDate: String
    private lateinit var endDate: String
    private var limit: Int = 0
    private lateinit var poiAddress: String
    private lateinit var poiCity: String
    private lateinit var poiDesc: String
    private lateinit var poiImage: String
    private var isClick = true
    private lateinit var locationArrayList: ArrayList<LatLng?>
    private lateinit var imagePart: MultipartBody.Part


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverStartRidingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = PascoApp.encryptedPrefs.userId

        val pickupLoc = intent.getStringExtra("pickupLoc").toString()
        val dropLoc = intent.getStringExtra("dropLoc").toString()

        locationArrayList = ArrayList()
        Bid = intent.getStringExtra("BookId").toString()
        driStatus = intent.getStringExtra("driStatus").toString()
        driId = intent.getStringExtra("driverStatusId").toString()
        orderStatusDriverR = intent.getStringExtra("currentOrder").toString()
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
        Log.e("driverStatus", "driverStatus: $driStatus,driverId: $driId")

        Log.d("PickupLocation", "Latitude: $Plat, Longitude: $Plon")
        Log.d("DropLocation", "Latitude: $Dlan, Longitude: $Dlon")

        activity = this
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapStart) as SupportMapFragment
        mapFragment.getMapAsync(this)

        pickupLocation = LatLng(Plat, Plon)
        dropLocation = LatLng(Dlan, Dlon)
        // Request location updates
        requestLocationUpdates()
        //camera permission
        requestPermission()
        driverStatusList()
        driverStatusObserver()

        binding.textViewSeeDetailsSR.setOnClickListener {

            if (isClick) {
                binding.textViewSeeDetailsSR.text = "Hide Details"
                binding.NewConstraintDetailsRide.visibility = View.VISIBLE
                isClick = false
            } else {
                binding.NewConstraintDetailsRide.visibility = View.GONE
                binding.textViewSeeDetailsSR.text = "Show Details"
                isClick = true
            }
        }

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
                            binding.finishTripTextView.visibility = View.VISIBLE
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
        binding.emergencyTextView.setOnClickListener {
            val intent = Intent(this@DriverStartRidingActivity, EmergencyMainActivity::class.java)
            intent.putExtra("bookingIdForHelp", Bid)
            startActivity(intent)
        }
        binding.finishTripTextView.setOnClickListener {
            if (spinnerDriverSId.isNullOrBlank()) {
                Toast.makeText(
                    this@DriverStartRidingActivity,
                    "Please select status",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                completedRideApi()
            }

            completedRideObserver()
            //showFeedbackPopup()
        }


    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this@DriverStartRidingActivity,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@DriverStartRidingActivity,
                    arrayOf(Manifest.permission.CAMERA),
                    cameraPermissionCode
                )
            }
        }
    }

    private fun showDeliveryPopUp() {
        bottomSheetDialog = BottomSheetDialog(this, R.style.TopCircleDialogStyle)
        val view = LayoutInflater.from(this).inflate(R.layout.delivery_proof_popup, null)
        bottomSheetDialog!!.setContentView(view)


        val consUploadDeliveryProof =
            bottomSheetDialog?.findViewById<ConstraintLayout>(R.id.consUploadDeliveryProof)
        val submitBtnDeliveryProof =
            bottomSheetDialog?.findViewById<TextView>(R.id.submitBtnDeliveryProof)
        savedImggSelectProof = bottomSheetDialog?.findViewById(R.id.savedImggSelectProof)!!

        submitBtnDeliveryProof?.setOnClickListener {
            bottomSheetDialog!!.dismiss()
        }

        consUploadDeliveryProof!!.setOnClickListener {
            openCamera()
        }

        submitBtnDeliveryProof!!.setOnClickListener {
            if (selectedImageFile == null) {
                Toast.makeText(
                    this@DriverStartRidingActivity,
                    "Please Upload the Image",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                addDeliveryProofApi()
            }
        }
        addDeliveryObserver()
        bottomSheetDialog!!.show()

    }

    private fun addDeliveryObserver() {
        deliveryProofViewModel.progressIndicator.observe(this, androidx.lifecycle.Observer {
        })
        deliveryProofViewModel.mDeliveryProofResponse.observe(
            this
        ) {
            val status = it.peekContent().status!!
            val message = it.peekContent().msg!!

            if (status == "True") {
                Toast.makeText(this@DriverStartRidingActivity, message, Toast.LENGTH_SHORT).show()
                showFeedbackPopup()
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()

            }

        }

        deliveryProofViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@DriverStartRidingActivity, it)
        }
    }

    private fun addDeliveryProofApi() {
        val BookingID = RequestBody.create(MultipartBody.FORM, Bid)
        val driverID = RequestBody.create(MultipartBody.FORM, userId)
        if (selectedImageFile != null) {
            imagePart = MultipartBody.Part.createFormData(
                "delivery_image",
                selectedImageFile!!.name,
                selectedImageFile!!.asRequestBody("delivery_image/*".toMediaTypeOrNull())
            )
            Log.e("endDate3", "file: " + selectedImageFile)
        } else {
            imagePart = MultipartBody.Part.createFormData(
                "delivery_image", "",
                "".toRequestBody("delivery_image/*".toMediaTypeOrNull())
            )


            Log.e("endDate3", "file:  null " + selectedImageFile)
        }
        deliveryProofViewModel.deliveryProofData(
            progressDialog,
            activity,
            BookingID,
            driverID,
            imagePart

        )
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(this@DriverStartRidingActivity.packageManager) != null) {
            takePictureLauncher.launch(cameraIntent)
        }
    }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                if (imageBitmap != null) {
                    // Generate a dynamic filename using a unique identifier
                    val fileName = "image_${System.currentTimeMillis()}.jpg"
                    // Convert Bitmap to File
                    selectedImageFile = bitmapToFile(imageBitmap, fileName)
                    Log.e("filePathBack", "selectedImageFile:Front " + selectedImageFile)
                    //OMCAApp.encryptedPrefs.frontImagePath = imageFile.toString()
                    savedImggSelectProof.setImageBitmap(imageBitmap)
                } else {
                    Toast.makeText(this, "Image capture canceled", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    private fun bitmapToFile(bitmap: Bitmap, fileName: String): File {
        // Create a new file in the app's cache directory
        val file = File(this@DriverStartRidingActivity.cacheDir, fileName)

        // Use FileOutputStream to write the bitmap data to the file
        try {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file
    }

    private fun afterDetailsObserver() {

        afterStartTripViewModel.mAfterTripResponse.observe(this) { response ->

            val dataGet = response.peekContent().data

            binding.pickUpLocDynamic.text = dataGet?.pickupLocation
            binding.dropLocDynamic.text = dataGet?.dropLocation
            val fullName = dataGet?.user ?: ""
            val firstName = fullName.split(" ").firstOrNull() ?: fullName
            binding.driverNameStartRideTextView.text = firstName

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
/*
                    runnable = object : Runnable {
                        override fun run() {

                            handler?.postDelayed(this, 2000) // 2000 milliseconds = 2 seconds
                        }
                    }

                    // Start the periodic task
                    handler?.post(runnable)*/
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

        formattedLatitudeSelect = String.format("%.4f", latitude)
        formattedLongitudeSelect = String.format("%.4f", longitude)
        formattedLatitudeLat = LatLng(latitude, longitude)

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
                val data = response.peekContent().data!!
                for (item in data) {
                    val poiName = item.poiname ?: ""
                    val poiType = item.poitype ?: ""
                    val couponCode = item.couponcode ?: ""
                    val startDate = item.startdate ?: ""
                    val endDate = item.enddate ?: ""
                    val limit = item.limit ?: 0
                    val poiAddress = item.poiaddress ?: ""
                    val poiCity = item.poicity ?: ""
                    val poiDesc = item.description ?: ""
                    val baseUrl = "http://69.49.235.253:8090"
                    val imagePath = item.poiimage.orEmpty()
                    val imageUrl = "$baseUrl$imagePath"
                    val poiLocation =
                        LatLng(item.poilatitude!!.toDouble(), item.poilongitude!!.toDouble())

                    val markerData = MarkerData(
                        poiName = poiName,
                        poiType = poiType,
                        couponCode = couponCode,
                        startDate = startDate,
                        endDate = endDate,
                        limit = limit,
                        poiAddress = poiAddress,
                        poiCity = poiCity,
                        poiDesc = poiDesc,
                        imageUrl = imageUrl
                    )

                    updatePoiLocation(poiLocation, markerData)
                }

                afterDetailsApi()
                getDistanceApi()
            }
        }
        updateLocationViewModel.errorResponse.observe(this@DriverStartRidingActivity) {
            ErrorUtil.handlerGeneralError(this@DriverStartRidingActivity, it)
        }
    }


    /*
        private fun updatePoiLocation(
            location: LatLng,
            poiName: String,
            poiType: String,
            imageUrl: String?
        ) {
            // Check if mMap is initialized before adding the marker
            if (::mMap.isInitialized) {
                val markerOptions = MarkerOptions()
                    .position(location)
                    .title(poiName) // Set the marker title to poiName
                    .snippet(poiType) // Set the marker snippet (description) to poiType
                   // .icon(BitmapDescriptorFactory.fromBitmap(imageUrl)) // Set the marker icon

                mMap.addMarker(markerOptions)
                mMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
            } else {
                Log.e("updatePoiLocation", "mMap is not initialized")
            }
        }
    */
    private fun updatePoiLocation(location: LatLng, markerData: MarkerData) {
        if (::mMap.isInitialized) {
            Glide.with(this)
                .asBitmap()
                .load(markerData.imageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val greenMarker = Bitmap.createBitmap(70, 70, Bitmap.Config.ARGB_8888)
                        val canvas = Canvas(greenMarker)
                        val paint = Paint()
                        paint.color = Color.GREEN
                        canvas.drawCircle(50f, 50f, 50f, paint)

                        val markerOptions = MarkerOptions()
                            .position(location)
                            .icon(BitmapDescriptorFactory.fromBitmap(greenMarker))

                        val marker = mMap.addMarker(markerOptions)
                        marker?.tag = markerData

                        var isInfoWindowShown = false

                        mMap.setOnMarkerClickListener { marker ->
                            if (!isInfoWindowShown) {
                                marker.showInfoWindow()
                                isInfoWindowShown =
                                    true // Set flag to true since info window is shown
                            } else {
                                marker.hideInfoWindow()
                                isInfoWindowShown =
                                    false // Set flag to false since info window is hidden
                            }

                            true // Return true to consume the event
                        }
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f))
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Handle any cleanup here if needed
                    }
                })
        } else {
            Log.e("updatePoiLocation", "mMap is not initialized")
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
                //  Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                showDeliveryPopUp()


            }
        }
        startTripViewModel.errorResponse.observe(this) {
            // Handle general errors
            ErrorUtil.handlerGeneralError(this, it)
        }
    }

    private fun showFeedbackPopup() {
        bottomSheetDialog = BottomSheetDialog(this, R.style.TopCircleDialogStyle)
        val view = LayoutInflater.from(this).inflate(R.layout.driver_feedback_popup, null)
        bottomSheetDialog!!.setContentView(view)


        val ratingBar = bottomSheetDialog?.findViewById<RatingBar>(R.id.ratingBar)
        val commentTxt = bottomSheetDialog?.findViewById<EditText>(R.id.commentTxt)
        val submitBtn = bottomSheetDialog?.findViewById<TextView>(R.id.submitBtn)
        val skipBtn = bottomSheetDialog?.findViewById<TextView>(R.id.skipBtn)

        var ratingBars = ""
        ratingBar?.setOnRatingBarChangeListener { _, rating, _ ->
            // Toast.makeText(this, "New Rating: $rating", Toast.LENGTH_SHORT).show()
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
            routeTypeStatic.clear()

            for (element in routeType!!) {
                element.status?.let { it1 -> routeTypeStatic.add(it1) }
            }
            val dAdapter = SpinnerAdapter(
                this,
                R.layout.custom_service_type_spinner,
                routeTypeStatic
            )
            dAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dAdapter.add(getString(R.string.selectStatus))
            binding.routeSpinnerSpinner.adapter = dAdapter
            // Determine spinner selection based on orderStatusDriverR condition
            if (orderStatusDriverR == "withoutSelected") {
                val spinnerPosition = if (driStatus.isNotEmpty()) {
                    dAdapter.getPosition(driStatus)
                } else {
                    dAdapter.getPosition(getString(R.string.selectStatus))
                }
                binding.routeSpinnerSpinner.setSelection(spinnerPosition)

            } else {
                val spinnerPosition = dAdapter.getPosition(getString(R.string.selectStatus))
                binding.routeSpinnerSpinner.setSelection(spinnerPosition)
            }
            if (response.peekContent().status == "False") {
            } else {

            }
        }
        getRouteUpdateViewModel.errorResponse.observe(this) {
            // Handle general errors using ErrorUtil
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
                // Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                binding.finishTripTextView.visibility = View.GONE
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
        requestLocationUpdates()

        // Add markers for pickup and drop locations
        if (::formattedLatitudeLat.isInitialized) {
            mMap.addMarker(MarkerOptions().position(formattedLatitudeLat).title("Current Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(formattedLatitudeLat, 17f))
            mMap.addMarker(MarkerOptions().position(pickupLocation).title("Pick-up Location"))
        } else {
            mMap.addMarker(MarkerOptions().position(pickupLocation).title("Pick-up Location"))
            mMap.addMarker(MarkerOptions().position(dropLocation).title("Drop-off Location"))
            // Move camera to the initial pickup location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickupLocation, 14f))
        }

        // Move camera to the first location in locationArrayList (if available)
        if (locationArrayList.isNotEmpty()) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationArrayList[0]!!, 14f))
        }


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

        if (::formattedLatitudeLat.isInitialized) {
            mMap.addMarker(MarkerOptions().position(formattedLatitudeLat).title("Current Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(formattedLatitudeLat, 17f))
            mMap.addMarker(MarkerOptions().position(pickupLocation).title("Pick-up Location"))
            // Get and draw route when map is ready
            getLastLocationAndDrawRoute()
            mMap.setOnMyLocationChangeListener { location ->
                val userLocation = LatLng(location.latitude, location.longitude)
                checkDestinationReached(userLocation, dropLocation)
            }

        } else {
            getLastLocationAndDrawRouteFromPickToDrop1()
            mMap.setOnMyLocationChangeListener { location ->
                val userLocation = LatLng(location.latitude, location.longitude)
                checkDestinationReached(pickupLocation, dropLocation)
            }
        }
        // Assuming this is inside an Activity or Fragment where you have access to a valid context

// Ensure mMap is initialized properly
        if (::mMap.isInitialized) {
            // Initialize the PoiInfoAdapter with appropriate context
            val poiInfoAdapter = PoiInfoAdapter(this)

            // Set the info window adapter for the map
            mMap.setInfoWindowAdapter(poiInfoAdapter)
        } else {
            Log.e("MapError", "mMap is not initialized")
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
                    drawRoute(LatLng(location.latitude, location.longitude), 0)
                    Log.e(
                        "location",
                        "location..${location.latitude} longitude ${location.longitude}"
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

    private fun getLastLocationAndDrawRouteFromPickToDrop1() {
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
                    drawRoute(LatLng(Plat, Plon), 1)
                    Log.e("location", "location..${Plat} longitude ${Plon}")
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

    private fun drawRoute(latLng: LatLng, routeType: Int) {
        val apiKey = "AIzaSyA3KVnFOiaKNlhi4hJB8N2pB8tyoe_rRxQ" // Replace with your actual API key
        val context = GeoApiContext.Builder()
            .apiKey(apiKey)
            .build()

        val origin = when (routeType) {
            0 -> "${latLng.latitude},${latLng.longitude}"
            1 -> "${Plat},${Plon}"
            else -> "${latLng.latitude},${latLng.longitude}"
        }

        val destination = when (routeType) {
            0 -> "${pickupLocation.latitude},${pickupLocation.longitude}"
            1 -> "${dropLocation.latitude},${dropLocation.longitude}"
            else -> "${pickupLocation.latitude},${pickupLocation.longitude}"
        }

        Log.e("location", "origin: $origin, destination: $destination")
        val result: DirectionsResult = DirectionsApi.newRequest(context)
            .mode(TravelMode.DRIVING)
            .origin(origin)
            .destination(destination)
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

    /* override fun onDestroy() {
         super.onDestroy()
         // Remove callbacks to prevent memory leaks
         handler?.removeCallbacks(runnable)
     }*/
}