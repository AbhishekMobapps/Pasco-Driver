package com.pasco.pascocustomer.customer.activity.hometabactivity

import android.Manifest
import android.app.ActionBar
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.BuildConfig
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.customer.activity.hometabactivity.cargoavailable.CargoAvailableBody
import com.pasco.pascocustomer.customer.activity.hometabactivity.cargoavailable.CargoAvailableModelView
import com.pasco.pascocustomer.customer.activity.hometabactivity.checkservicemodel.CheckChargeModelView
import com.pasco.pascocustomer.customer.activity.hometabactivity.checkservicemodel.CheckChargesBody
import com.pasco.pascocustomer.customer.activity.hometabactivity.modelview.BookingOrderBody
import com.pasco.pascocustomer.customer.activity.hometabactivity.modelview.BookingOrderModelView
import com.pasco.pascocustomer.customer.activity.vehicledetailactivity.VehicleDetailsActivity
import com.pasco.pascocustomer.dashboard.UserDashboardActivity
import com.pasco.pascocustomer.databinding.ActivityAllTabPayBinding
import com.pasco.pascocustomer.location.LocationsActivity
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.pasco.pascocustomer.activity.Driver.AddVehicle.VehicleType.VehicleTypeViewModel
import com.pasco.pascocustomer.application.PascoApp
import java.io.IOException
import java.util.*

@Suppress("DEPRECATION")
@AndroidEntryPoint
class AllTabPayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAllTabPayBinding
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var lightTrans = ""
    private var selectedOption = ""
    private var count = 0
    private var indexOfLocation = -1
    private val PLACE_PICKUP_REQUEST_CODE = 1001
    private var vehicleId = 0
    private var addressLineDrop: String = ""
    private var addressList: List<Address>? = null
    private var lat1 = ""
    private var lon1 = ""
    private var lat2 = ""
    private var lon2 = ""
    private var el1: Double = 0.0
    private var el2: Double = 0.0

    var pickupLatitude = ""
    private var dropupLongitude: String = ""
    private var cityNamePickUp: String? = null
    private var cityNameDrop: String? = null
    private var pickUpLatitude = 0.0
    private var pickUpLongitude = 0.0
    private var destinationLatitude = 0.0
    private var destinationLongitude = 0.0
    private var selectedDate = ""
    private var selectedTime = ""

    private var spinnerVehicleTypeId = ""
    private var vehicleSize = ""
    private var vehicleLoadCapacity = ""
    private var vehicleCapability = ""
    private var profile = ""

    private var formattedLatitudeDropSelect: String = ""
    var formattedLatitudeSelect: String = ""
    var formattedLongitudeSelect: String = ""
    var formattedLongitudeDropSelect: String = ""

    private var VehicleType: List<VehicleTypeResponse.VehicleTypeData>? = null
    private val vehicleTypeStatic: MutableList<String> = mutableListOf()
    private val progressDialog by lazy { CustomProgressDialog(this) }
    private val bookingRideViewModel: BookingOrderModelView by viewModels()
    private val checkChargeViewModel: CheckChargeModelView by viewModels()
    private val cargoViewModel: CargoAvailableModelView by viewModels()
    private val vehicleTypeViewModel: VehicleTypeViewModel by viewModels()

    private var carImg: ImageView? = null
    private var carName: TextView? = null
    private var hourTxt: TextView? = null
    private var kmTxt: TextView? = null
    private var timeDurationTxt: TextView? = null
    private var bookingAmount: TextView? = null
    private var totalAmount: TextView? = null
    private var dateTxt: TextView? = null
    private var timeTxt: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllTabPayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener { finish() }

        profile = PascoApp.encryptedPrefs.profileUpdate

        if (profile ==  "0")
        {
            showCalenderPopup()
        }
        else
        {

        }

        lightTrans = intent.getStringExtra("lightTrans").toString()
        vehicleId = intent.getIntExtra("vehicleId", 0)
        Log.e("currentLatLng", "vehicleId...   $vehicleId")

        when (lightTrans) {
            "lightTrans" -> {
                binding.headerTxt.text = "Light Transport"
            }
            "heavyTrans" -> {
                binding.headerTxt.text = "Heavy Transport"
            }
            else -> {
                binding.headerTxt.text = "Cheese Burger"
            }
        }




        binding.addBtn.setOnClickListener {
            count++
            binding.cargoQtyTxt.text = count.toString()
        }

        binding.subtractBtn.setOnClickListener {
            if (count > 0) { // Check if count is greater than 0 before subtracting
                count--
                binding.cargoQtyTxt.text = count.toString()
            }
        }

        binding.calenderBtn.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Do something with the selected date
                    selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                    binding.dateTxt.text = selectedDate
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        binding.timeBtn.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->
                    // Do something with the selected time
                    val formattedHour = if (selectedHour % 12 == 0) 12 else selectedHour % 12
                    val amPm = if (selectedHour < 12) "AM" else "PM"
                    selectedTime = String.format("%02d:%02d%s", formattedHour, selectedMinute, amPm)
                    binding.timeTxt.text = selectedTime

                    Log.e("selectedTimesa", "selectedTime..." + selectedTime)
                },
                hour,
                minute,
                false // Set to false for 12-hour format with AM/PM
            )
            timePickerDialog.show()
        }


        binding.pickUp.setOnClickListener {
            indexOfLocation = -2
            val intent = Intent(this, LocationsActivity::class.java)
            intent.putExtra("pickYourLocation", "pickYourLocation")
            startActivityForResult(intent, PLACE_PICKUP_REQUEST_CODE)
        }

        binding.dropUp.setOnClickListener {
            indexOfLocation = -3
            val intent = Intent(this, LocationsActivity::class.java)
            intent.putExtra("pickYourLocation", "destinationLocation")
            startActivityForResult(intent, PLACE_PICKUP_REQUEST_CODE)
        }

        binding.confirmBtn.setOnClickListener {
            validation()
        }

        binding.vehicleTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View?,
                    i: Int,
                    l: Long
                ) {
                    val item = binding.vehicleTypeSpinner.selectedItem.toString()
                    if (item != getString(R.string.selectVehicleType)) {
                        spinnerVehicleTypeId = VehicleType!![i].id.toString()
                        vehicleSize = VehicleType!![i].vehiclesize.toString()
                        vehicleLoadCapacity = VehicleType!![i].vehicleweight.toString()
                        vehicleCapability = VehicleType!![i].capabilityname.toString()

                        cargoApi(spinnerVehicleTypeId)
                        Log.e("VehicleTypeSpinner", "spinnerVehicleTypeId $spinnerVehicleTypeId")
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                    // Do nothing
                }
            }


        // Vehicle Api and observer
        callVehicleType()
        vehicleTypeObserver()
        bookingObserver()
        checkChargeObserver()
        cargoObserver()
    }


    private fun callVehicleType() {
        vehicleTypeViewModel.getVehicleTypeData(
            progressDialog,
            this,
            vehicleId.toString()
        )
    }

    private fun vehicleTypeObserver() {
        vehicleTypeViewModel.mVehicleTypeResponse.observe(this) { response ->
            val content = response.peekContent()
            val message = content.msg ?: return@observe
            VehicleType = content.data
            vehicleTypeStatic.clear()

            for (element in VehicleType!!) {
                element.vehiclename?.let { it1 -> vehicleTypeStatic.add(it1) }
            }
            val dAdapter = VehicleDetailsActivity.SpinnerAdapter(
                this@AllTabPayActivity,
                R.layout.custom_service_type_spinner,
                vehicleTypeStatic
            )
            dAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dAdapter.add(getString(R.string.selectVehicleType))
            binding.vehicleTypeSpinner.adapter = dAdapter
            binding.vehicleTypeSpinner.setSelection(dAdapter.count)



            if (response.peekContent().status.equals("False")) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                //   binding.linearVehDetails.visibility = View.GONE
            } else if (response.peekContent().status.equals("True")) {

            }
        }

        vehicleTypeViewModel.errorResponse.observe(this) {
            // Handle general errors
            ErrorUtil.handlerGeneralError(this, it)
        }
    }

    fun onCashConsClicked(view: View) {
        // Check the radio button when ConstraintLayout is clicked
        binding.cashRadioButton.isChecked = true
        binding.walletRadioButton.isChecked = false
        binding.visaRadioButton.isChecked = false
        selectedOption = "Cash"

    }

    fun onWalletConsClicked(view: View) {
        // Check the radio button when ConstraintLayout is clicked
        binding.walletRadioButton.isChecked = true
        binding.cashRadioButton.isChecked = false
        binding.visaRadioButton.isChecked = false
        selectedOption = "Wallet"

    }

    fun onVisaConsClicked(view: View) {
        // Check the radio button when ConstraintLayout is clicked
        binding.visaRadioButton.isChecked = true
        binding.cashRadioButton.isChecked = false
        binding.walletRadioButton.isChecked = false
        selectedOption = "Visa"


    }

    private fun validation() {
        if (binding.pickStartPoint.text.isNullOrBlank()) {
            Toast.makeText(this, "Please select pick-up location", Toast.LENGTH_SHORT).show()
        } else if (binding.pickDestinationPoint.text.isNullOrBlank()) {
            Toast.makeText(this, "Please select destination location", Toast.LENGTH_SHORT).show()
        } else if (binding.vehicleTypeSpinner.selectedItem.toString() == resources.getString(R.string.selectVehicleType)) {
            Toast.makeText(this, "Please Select Vehicle", Toast.LENGTH_SHORT).show()
        } else if (binding.cargoQtyTxt.text.isNullOrBlank()) {
            Toast.makeText(this, "Please enter quantity", Toast.LENGTH_SHORT).show()
        } else if (binding.dateTxt.text.isNullOrBlank()) {
            Toast.makeText(this, "Please select date", Toast.LENGTH_SHORT).show()
        } else if (binding.timeTxt.text.isNullOrBlank()) {
            Toast.makeText(this, "Please select time", Toast.LENGTH_SHORT).show()
        } /*else if (binding.cashRadioButton.isChecked || binding.walletRadioButton.isChecked || binding.visaRadioButton.isChecked) {
            Toast.makeText(this, "Please select payment method", Toast.LENGTH_SHORT).show()
        }*/ else {
            bookTripPopup()
        }
    }

    override fun onResume() {
        super.onResume()
        if (indexOfLocation == -2) {
            if (LocationsActivity.myPlace != null) {
                val geocoder = Geocoder(this)
                try {
                    Log.e("currentLatLng", "currentLatLng.123.." + LocationsActivity.myPlace)
                    addressList = geocoder.getFromLocationName(LocationsActivity.myPlace, 5)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                if (addressList != null && addressList!!.isNotEmpty()) {
                    // The list is not null and has at least one element
                    val locations = addressList!![0]
                    // Now you can use 'locations' as needed
                    val currentLatLng = locations?.let {
                        LatLng(locations.latitude, it.longitude)
                    }

                    if (currentLatLng != null) {
                        getAddressFromLocation(currentLatLng)

                    }
                } else {
                }
            }

        } else if (indexOfLocation == -2) {
            getLocationDestination(this, LocationsActivity.myPlace)
        }

        if (indexOfLocation == -3) {
            if (LocationsActivity.myPlace != null) {
                val geocoder = Geocoder(this)
                try {
                    addressList = geocoder.getFromLocationName(LocationsActivity.myPlace, 5)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                if (addressList != null && addressList!!.isNotEmpty()) {
                    // The list is not null and has at least one element
                    val locations = addressList!![0]
                    // Now you can use 'locations' as needed
                    val currentLatLng = locations?.let { LatLng(locations.latitude, it.longitude) }

                    Log.e("currentLatLng", "currentLatLng..." + LocationsActivity.myPlace)

                    if (currentLatLng != null) {
                        getAddressFromLocation1(currentLatLng)

                    }
                } else {
                    // Handle the case when the list is null or empty
                    // You might want to display an error message or take appropriate action
                }
            }

            //  getLocationAddress(BookingActivity.this, place);
        } else if (indexOfLocation == -3) {
            getLocationDestination1(this, LocationsActivity.myPlace)
        }

    }

    private fun getAddressFromLocation(location: LatLng) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )

            if (addresses != null && addresses.isNotEmpty()) {
                val currentAddress: Address = addresses[0]
                // Get latitude and longitude separately
                pickUpLatitude = currentAddress.latitude
                pickUpLongitude = currentAddress.longitude

                // Format latitude and longitude with 5 decimal places
                formattedLatitudeSelect = String.format("%.5f", pickUpLatitude)
                formattedLongitudeSelect = String.format("%.5f", pickUpLongitude)

                val address: String = addresses[0].getAddressLine(0) ?: "Address not available"
                cityNamePickUp = currentAddress.locality

                binding.pickStartPoint.text = address
            } else {
                binding.pickStartPoint.text = "Address not found"
            }

        } catch (e: IOException) {
            e.printStackTrace()

        }
    }

    private fun getAddressFromLocation1(location: LatLng) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )

            if (addresses != null && addresses.isNotEmpty()) {
                val currentAddress: Address = addresses[0]
                // Get latitude and longitude separately
                destinationLatitude = currentAddress.latitude
                destinationLongitude = currentAddress.longitude
                // Format latitude and longitude with 5 decimal places
                formattedLatitudeDropSelect = String.format("%.5f", destinationLatitude)
                formattedLongitudeDropSelect = String.format("%.5f", destinationLongitude)
                val address: String = addresses[0].getAddressLine(0) ?: "Address not available"
                cityNameDrop = currentAddress.locality
                binding.pickDestinationPoint.text = address
            } else {
                binding.pickDestinationPoint.text = "Address not found"
            }

        } catch (e: IOException) {
            e.printStackTrace()

        }
    }

    private fun getLocationDestination1(context: Context, strAddress: String): LatLng? {
        val coder = Geocoder(context)
        var p1: LatLng? = null

        if (strAddress == null) {
            // Handle the case where strAddress is null if needed
        } else {
            try {
                // May throw an IOException
                val addresses = coder.getFromLocationName(strAddress, 5)
                if (addresses == null) {
                    return null
                }

                val location: Address = addresses[0]
                p1 = LatLng(location.latitude, location.longitude)
                lat2 = location.latitude.toString()
                lon2 = location.longitude.toString()

                addressLineDrop = location.getAddressLine(0).toString()
                el2 = getLocationAltitude(addresses[0])

                if (!lat1.isEmpty() || !lon2.isEmpty()) {
                    if (!lat1.isEmpty() && !lon2.isEmpty()) {
                        binding.pickDestinationPoint.text = addressLineDrop
                    } else if (!dropupLongitude.isEmpty()) {
                        binding.pickDestinationPoint.text = addressLineDrop
                        // confirmPickUpDropBtn.visibility = View.VISIBLE
                    }
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
        return p1
    }

    private fun getLocationDestination(context: Context, strAddress: String): LatLng? {
        val coder = Geocoder(context)
        var p1: LatLng? = null

        if (strAddress == null) {
            // Handle the case where strAddress is null if needed
        } else {
            try {
                // May throw an IOException
                val addresses = coder.getFromLocationName(strAddress, 5) ?: return null

                val location: Address = addresses[0]
                p1 = LatLng(location.latitude, location.longitude)

                lat1 = location.latitude.toString()
                lon1 = location.longitude.toString()
                el1 = getLocationAltitude(addresses[0])


                addressLineDrop = location.getAddressLine(0).toString()

                if (!pickupLatitude.isEmpty() && !dropupLongitude.isEmpty()) {
                    // Both pickUplatitude and drop_longitude are not empty
                    binding.pickStartPoint.text = addressLineDrop
                    // Additional code if needed
                } else if (!dropupLongitude.isEmpty()) {
                    // Only drop_longitude is not empty
                    binding.pickStartPoint.text = addressLineDrop
                    // Additional code if needed
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
        return p1
    }


    private fun getLocationAltitude(address: Address): Double {
        // Check if location is available
        return if (address.hasLatitude() && address.hasLongitude()) {
            val location = Location("dummyProvider")
            location.latitude = address.latitude
            location.longitude = address.longitude
            location.altitude
        } else {
            0.0
        }
    }

    private fun bookTripPopup() {

        bottomSheetDialog = BottomSheetDialog(this, R.style.TopCircleDialogStyle)
        val view = LayoutInflater.from(this).inflate(R.layout.booking_ride_popup, null)
        bottomSheetDialog!!.setContentView(view)

        val bottomSheetBehavior = BottomSheetBehavior.from((view.parent) as View)

        view.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            }
        }

        carImg = bottomSheetDialog?.findViewById<ImageView>(R.id.carImg)
        carName = bottomSheetDialog?.findViewById<TextView>(R.id.carName)
        hourTxt = bottomSheetDialog?.findViewById<TextView>(R.id.hourTxt)
        kmTxt = bottomSheetDialog?.findViewById<TextView>(R.id.kmTxt)
        timeDurationTxt = bottomSheetDialog?.findViewById<TextView>(R.id.timeDurationTxt)
        //bookingAmount = bottomSheetDialog.findViewById<TextView>(R.id.bookingAmount)
        totalAmount = bottomSheetDialog?.findViewById<TextView>(R.id.totalAmount)
        val cancelBtn = bottomSheetDialog?.findViewById<TextView>(R.id.cancelBtn)
        val proceedBtn = bottomSheetDialog?.findViewById<TextView>(R.id.proceedBtn)
        val dateTxt = bottomSheetDialog?.findViewById<TextView>(R.id.dateTxt)
        val timeTxt = bottomSheetDialog?.findViewById<TextView>(R.id.timeTxt)
        //  dateTxt = bottomSheetDialog.findViewById<TextView>(R.id.dateTxt)
        // timeTxt = bottomSheetDialog.findViewById<TextView>(R.id.timeTxt)

        dateTxt?.text = selectedDate
        timeTxt?.text = selectedTime

        cancelBtn?.setOnClickListener { bottomSheetDialog?.dismiss() }
        checkChargeApi()

        proceedBtn?.setOnClickListener { bookingApi() }
        bottomSheetDialog?.show()
    }

    private fun bookingApi() {
        val dateTime = "$selectedDate $selectedTime"
        val bookingBody = BookingOrderBody(
            cargo = spinnerVehicleTypeId,
            cargo_quantity = binding.cargoQtyTxt.text.toString(),
            pickup_location = binding.pickStartPoint.text.toString(),
            drop_location = binding.pickDestinationPoint.text.toString(),
            pickup_city = cityNamePickUp.toString(),
            drop_city = cityNameDrop.toString(),
            pickup_latitude = formattedLatitudeSelect,
            pickup_longitude = formattedLongitudeSelect,
            drop_latitude = formattedLatitudeDropSelect,
            drop_longitude = formattedLongitudeDropSelect,
            pickup_datetime = dateTime,
            message = binding.yourMsg.text.toString(),
            payment_method = selectedOption

        )
        bookingRideViewModel.otpCheck(bookingBody, this, progressDialog)
    }

    private fun bookingObserver() {
        bookingRideViewModel.mRejectResponse.observe(this@AllTabPayActivity) { response ->
            val message = response.peekContent().msg
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            bottomSheetDialog?.dismiss()
            binding.cargoQtyTxt.text = ""
            binding.pickStartPoint.text = ""
            binding.pickDestinationPoint.text = ""
            formattedLatitudeSelect = ""
            formattedLongitudeSelect = ""
            formattedLatitudeDropSelect = ""
            formattedLongitudeDropSelect = ""
            selectedDate = ""
            selectedTime = ""
            binding.yourMsg.setText("")
            val intent = Intent(this,UserDashboardActivity::class.java)
            startActivity(intent)
        }

        bookingRideViewModel.errorResponse.observe(this@AllTabPayActivity) {
            ErrorUtil.handlerGeneralError(this@AllTabPayActivity, it)
        }
    }

    private fun checkChargeApi() {

        val bookingBody = CheckChargesBody(
            cargo = spinnerVehicleTypeId,
            pickup_location = binding.pickStartPoint.text.toString(),
            drop_location = binding.pickDestinationPoint.text.toString(),
            pickup_latitude = formattedLatitudeSelect,
            pickup_longitude = formattedLongitudeSelect,
            drop_latitude = formattedLatitudeDropSelect,
            drop_longitude = formattedLongitudeDropSelect
        )
        checkChargeViewModel.otpCheck(bookingBody, this, progressDialog)
    }

    private fun checkChargeObserver() {
        checkChargeViewModel.mRejectResponse.observe(this@AllTabPayActivity) { response ->
            carName?.text = response.peekContent().cargoname

            val hours = response.peekContent().duration?.hours
            val min = response.peekContent().duration?.minutes
            val distance = response.peekContent().distance

            val timeDuration = "$hours:$min"
            val formattedDistance = String.format("%.2f", distance)
            kmTxt?.text = "$formattedDistance Km"
            hourTxt?.text = "$timeDuration hrs"
            timeDurationTxt?.text = "$timeDuration hrs"
            val price = response.peekContent().price
            val totalPrice = String.format("%.2f", price)
            totalAmount?.text = "$$totalPrice"
            val url = response.peekContent().cargoimage
            Glide.with(this).load(BuildConfig.IMAGE_KEY + url).placeholder(R.drawable.home_bg)
                .into(carImg!!)

        }

        checkChargeViewModel.errorResponse.observe(this@AllTabPayActivity) {
            ErrorUtil.handlerGeneralError(this@AllTabPayActivity, it)
        }
    }


    private fun cargoApi(spinnerVehicleTypeId: String) {
        Log.e(
            "CheckCity",
            "city.." + cityNamePickUp.toString() + " drop_city " + cityNameDrop.toString() + " spinnerVehicleTypeId " + spinnerVehicleTypeId
        )

        val bookingBody = CargoAvailableBody(
            cargo = spinnerVehicleTypeId,
            pickup_location = binding.pickStartPoint.text.toString(),
            drop_location = binding.pickDestinationPoint.text.toString(),
            pickup_city = cityNamePickUp.toString(),
            drop_city = cityNameDrop.toString()
        )
        cargoViewModel.otpCheck(bookingBody, this)
    }

    private fun cargoObserver() {
        cargoViewModel.mRejectResponse.observe(this@AllTabPayActivity) { response ->


            if (response.peekContent().availableDriver == 0) {
                binding.numberOfVehicleTxt.text = "Cargo is not available"
            } else {
                binding.numberOfVehicleTxt.text = response.peekContent().availableDriver.toString()
            }
            binding.numberOfVehicleConst.visibility = View.VISIBLE
        }

        cargoViewModel.errorResponse.observe(this@AllTabPayActivity) {
            ErrorUtil.handlerGeneralError(this@AllTabPayActivity, it)
        }
    }

    private fun showCalenderPopup() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.update_profile_popup)

        val yesBtn = dialog.findViewById<TextView>(R.id.yesBtn)
        val cancel = dialog.findViewById<TextView>(R.id.cancelBtn)

        cancel.setOnClickListener { finish() }
        yesBtn.setOnClickListener {
            val intent = Intent(this,UserDashboardActivity::class.java)
            intent.putExtra("profileUpdate","update")
            startActivity(intent)
        }
        val window = dialog.window
        val lp = window?.attributes
        if (lp != null) {
            lp.width = ActionBar.LayoutParams.MATCH_PARENT
        }
        if (lp != null) {
            lp.height = ActionBar.LayoutParams.WRAP_CONTENT
        }
        if (window != null) {
            window.attributes = lp
        }

        dialog.show()
    }

}