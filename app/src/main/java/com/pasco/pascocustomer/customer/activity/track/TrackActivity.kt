package com.pasco.pascocustomer.customer.activity.track

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
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
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.BuildConfig
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.customer.activity.track.trackmodel.TrackLocationBody
import com.pasco.pascocustomer.customer.activity.track.trackmodel.TrackLocationModelView
import com.pasco.pascocustomer.databinding.ActivityTrackBinding
import com.pasco.pascocustomer.userFragment.home.sliderpage.SliderHomeBody
import com.pasco.pascocustomer.userFragment.order.acceptedadapter.AcceptedAdapter
import com.pasco.pascocustomer.userFragment.order.acceptedmodel.AcceptedModelView
import com.pasco.pascocustomer.userFragment.pageradaper.ViewPagerAdapter
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@AndroidEntryPoint
class TrackActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var pickupLocation: LatLng // Define your pickup location
    private lateinit var dropLocation: LatLng // Define your drop location

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityTrackBinding
    private val acceptedModelView: AcceptedModelView by viewModels()
    private val trackModelView: TrackLocationModelView by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    private var isDestinationReached = false

    private var pickupLatitude = ""
    private var pickupLongitude = ""
    private var dropLatitude = 0.0
    private var dropLongitude = 0.0
    private var bookingId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageBackReqRide.setOnClickListener { finish() }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapsa) as SupportMapFragment
        mapFragment.getMapAsync(this)

        pickupLatitude = intent.getStringExtra("pickupLatitude").toString()
        pickupLongitude = intent.getStringExtra("pickupLongitude").toString()
        bookingId = intent.getStringExtra("bookingId").toString()
        //dropLatitude = intent.getStringExtra("dropLatitude").toString()
        //dropLongitude = intent.getStringExtra("dropLongitude").toString()


        pickupLocation = LatLng(pickupLatitude.toDouble(), pickupLongitude.toDouble()) // New York City
        dropLocation = LatLng(dropLatitude, dropLongitude) // Los Angeles



        getAcceptedApi()
        acceptedObserver()
        locationApi()
        locationObserver()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isTrafficEnabled = true
        // Add markers for pickup and drop locations
        mMap.addMarker(MarkerOptions().position(pickupLocation).title("Pickup Location"))
        mMap.addMarker(MarkerOptions().position(dropLocation).title("Drop Location"))

        // Move camera to the initial pickup location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickupLocation, 12f))

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
        val points = decodePolyline(result.routes[0].overviewPolyline.encodedPath)

        // Add polyline to map
        mMap.addPolyline(PolylineOptions().addAll(points).color(Color.BLUE))

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


    private fun getAcceptedApi() {
        acceptedModelView.acceptedBids(this, progressDialog)
    }

    private fun acceptedObserver() {
        acceptedModelView.progressIndicator.observe(this) {
        }
        acceptedModelView.mRejectResponse.observe(this) {
            val message = it.peekContent().msg
            val success = it.peekContent().status
            val list = it.peekContent().data

            if (list != null) {
                for (i in list) {
                    val url = i.driverImage
                    Glide.with(this).load(BuildConfig.IMAGE_KEY + url)
                        .into(binding.profileImgUserBid)

                    val pickLocation = i.pickupLocation
                    binding.pickUpLocBidd.text = pickLocation

                    val dropLocation = i.dropLocation
                    binding.dropLocBidd.text = dropLocation
                    val price = i.bidPrice

                    binding.orderIdStaticTextView.text = "$ $price"
                }
            }
        }

        acceptedModelView.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
            //errorDialogs()
        }
    }

    private fun locationApi() {

        val bookingBody = TrackLocationBody(
            driverbookedid = bookingId
        )
        trackModelView.trackLocation(bookingBody, this)
    }

    private fun locationObserver() {

        trackModelView.mRejectResponse.observe(this) { response ->

            val dataGet = response.peekContent().data
            dropLatitude = response.peekContent().data?.currentLatitude!!
            dropLongitude = response.peekContent().data?.currentLongitude!!
            Log.e("CheckData","dropLatitude   "  +dropLatitude +"dropLongitude... " +dropLongitude)


        }

        trackModelView.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }

}