package com.pasco.pascocustomer.Driver.StartRiding.Ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
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
import com.pasco.pascocustomer.Driver.AcceptRideDetails.Ui.AcceptRideActivity
import com.pasco.pascocustomer.Driver.StartRiding.ViewModel.GetRouteUpdateResponse
import com.pasco.pascocustomer.Driver.StartRiding.ViewModel.GetRouteUpdateViewModel
import com.pasco.pascocustomer.Driver.StartRiding.ViewModel.StartTripViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.databinding.ActivityDriverStartRidingBinding
import com.pasco.pascocustomer.utils.ErrorUtil
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
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    private var Plat: Double = 0.0
    private var Plon: Double = 0.0
    private var Dlan: Double = 0.0
    private var Dlon: Double = 0.0
    private lateinit var mMap: GoogleMap
    private lateinit var pickupLocation: LatLng
    private lateinit var dropLocation: LatLng
    private var spinnerDriverSId = ""
    private var isDestinationReached = false
    private var routeType: List<GetRouteUpdateResponse.RouteResponseData>? = null
    private val routeTypeStatic: MutableList<String> = mutableListOf()
    private var Bid = ""

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverStartRidingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pickupLoc = intent.getStringExtra("pickupLoc").toString()
        val dropLoc = intent.getStringExtra("dropLoc").toString()

        Plat = intent.getStringExtra("latitudePickUp")?.toDoubleOrNull() ?: 0.0
        Plon = intent.getStringExtra("longitudePickUp")?.toDoubleOrNull() ?: 0.0
        Dlan = intent.getStringExtra("latitudeDrop")?.toDoubleOrNull() ?: 0.0
        Dlon = intent.getStringExtra("longitudeDrop")?.toDoubleOrNull() ?: 0.0
        val deliveryTime = intent.getStringExtra("deltime")
        val image = intent.getStringExtra("image").toString()
        Log.e("image", "onCreate: " + image)
        Bid = intent.getStringExtra("BookId").toString()

        activity = this
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapStart) as SupportMapFragment
        mapFragment.getMapAsync(this)

        pickupLocation = LatLng(Plat, Plon)
        dropLocation = LatLng(Dlan, Dlon)
        // Request location updates
        requestLocationUpdates()
        driverStatusList()
        driverStatusObserver()
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

        binding.pickUpLocDynamic.text = pickupLoc
        binding.dropLocDynamic.text = dropLoc
        binding.delTimeDynamic.text = deliveryTime
        Glide.with(this)
            .load(image)
            .into(binding.cricleImgUserSR)

        binding.imageBackReqRide.setOnClickListener {
            finish()
        }
        binding.rechedIdPickup.setOnClickListener {
            if (binding.rechedIdPickup.text == "Reached the location") {
                binding.rechedIdPickup.text = "Start Trip"
                // Call your API here

            } else {
                binding.rechedIdPickup.text =

                    "Reached the location"
            }
        }
        //call observer
        startTripObserver()


        binding.cancelDriverOrder.setOnClickListener {
            openCancelPopUp()
        }

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
            binding.routeSpinnerSpinner.setSelection(dAdapter.getPosition(getString(R.string.selectStatus)))

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

        // Request the last known location
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    currentLatitude = location.latitude
                    currentLongitude = location.longitude
                }
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


    private fun openCancelPopUp() {
        val dialog = BottomSheetDialog(this@DriverStartRidingActivity)
        val dialogView = layoutInflater.inflate(R.layout.cancel_popup, null)
        dialog.setContentView(dialogView)
        dialog.window?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val backArrowCancelPopUp = dialogView.findViewById<ImageView>(R.id.backArrowCancelPopUp)
        val reason1Txt = dialogView.findViewById<TextView>(R.id.reason1Txt)
        val reason2Txt = dialogView.findViewById<TextView>(R.id.reason2Txt)

        dialog.show()

        backArrowCancelPopUp.setOnClickListener {
            dialog.dismiss()

            reason1Txt.setOnClickListener {
                dialog.dismiss()
            }

            reason2Txt.setOnClickListener {
                dialog.dismiss()
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickupLocation, 17f))

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

}