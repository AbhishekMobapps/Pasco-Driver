package com.pasco.pascocustomer.Driver.DriverDashboard.Ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.transportapp.DriverApp.MarkDuty.MarkDutyViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.ApprovalStatus.ViewModel.GetApprovalStatusDModel
import com.pasco.pascocustomer.Driver.DriverDashboard.ViewModel.MarkDutyBody
import com.pasco.pascocustomer.Driver.Fragment.DriverOrders.DriverOrdersFragment
import com.pasco.pascocustomer.Driver.Fragment.DriverProfile.DriverProfileFragment
import com.pasco.pascocustomer.Driver.Fragment.HomeFrag.Ui.HomeFragment
import com.pasco.pascocustomer.Driver.Fragment.MoreFragDriver.DriverMoreFragment
import com.pasco.pascocustomer.Driver.Fragment.TripHistoryFragment
import com.pasco.pascocustomer.Driver.UpdateLocation.UpdateLocationViewModel
import com.pasco.pascocustomer.Driver.UpdateLocation.UpdationLocationBody
import com.pasco.pascocustomer.Driver.driverFeedback.DriverFeedbackBody
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.customer.activity.notificaion.NotificationActivity
import com.pasco.pascocustomer.customer.activity.notificaion.notificationcount.NotificationCountViewModel
import com.pasco.pascocustomer.customer.activity.updatevehdetails.GetVehicleDetailsBody
import com.pasco.pascocustomer.databinding.ActivityDriverDashboardBinding
import com.pasco.pascocustomer.language.Originator
import com.pasco.pascocustomer.userFragment.profile.modelview.GetProfileBody
import com.pasco.pascocustomer.userFragment.profile.modelview.GetProfileModelView
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

@AndroidEntryPoint
class DriverDashboardActivity : Originator() {
    private lateinit var binding: ActivityDriverDashboardBinding
    private val getVDetailsViewModel: GetApprovalStatusDModel by viewModels()
    private var city: String? = null
    private var address: String? = null
    private var dAdminApprovedStatus: String? = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastBackPressTime = 0L
    private val backPressInterval = 2000
    private var shouldLoadHomeFragOnBackPress = true
    private val markDutyViewModel: MarkDutyViewModel by viewModels()
    private val getProfileModelView: GetProfileModelView by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }
    private lateinit var activity: Activity
    private var driverId = ""
    private var navItemIndex = 1
    private var dutyOccupied: Int = 0
    private var refersh = ""
    private var handler: Handler? = null
    private val notificationCountViewModel: NotificationCountViewModel by viewModels()
    private var switchCheck = ""
    private var OnDutyStatus = ""
    private var one: Int = -1
    private var notificaion = ""
    private var countryName: String? = null
    private lateinit var updateLocationBody: UpdationLocationBody
    private val updateLocationViewModel: UpdateLocationViewModel by viewModels()
    private var formattedLatitudeSelect: String = ""
    private var formattedLongitudeSelect: String = ""
    private lateinit var runnable: Runnable
    private var isCheck = true
    private lateinit var sharedPreferencesLanguageName: SharedPreferences
    private var languageId = ""

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)


        sharedPreferencesLanguageName = getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
        languageId = sharedPreferencesLanguageName.getString("languageId", "").toString()

        getVehicleDetails()
        getVehicleDetailsObserver()

        binding.switchbtn.isChecked = false

        getNotificationPermission()
        activity = this
        driverId = PascoApp.encryptedPrefs.userId
        getVehicleDetailsObserver()
        getVehicleDetails()
        dAdminApprovedStatus = PascoApp.encryptedPrefs.driverStatuss



        getProfileApi()
        getUserProfileObserver()


        switchCheck = PascoApp.encryptedPrefs.CheckedType
        runnable = object : Runnable {
            override fun run() {
               // getVehicleDetails()
               // getVehicleDetailsObserver()
                // Schedule the next execution
                handler!!.postDelayed(this, 2000) // 2000 milliseconds = 2 seconds
            }
        }

        Log.e("switchValue", "switchCheck: " + dAdminApprovedStatus)

        refersh = PascoApp.encryptedPrefs.token
        requestLocationPermission()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationUpdates()
        if (checkLocationPermission()) {
            requestLocationUpdates()
        } else {
            requestLocationPermission()
        }

        handler = Handler(Looper.getMainLooper())
        binding.firstConsLayouttt.visibility = View.VISIBLE


        val homeFragment = HomeFragment()
        replace_fragment(homeFragment)

        //Api and Observer
        getNotificationCountDApi()
        notificationCountDObserver()

        binding.notificationBtnDriver.setOnClickListener {
            notificaion = "1"
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }
        one = intent.getIntExtra("onee", -1)

        // Conditional logic to check the switch button state
        if (switchCheck == "1" || one == 1) {
            binding.switchbtn.isChecked = true
            switchCheck = "ON"
            markOnDuty()
        } else {
            binding.switchbtn.isChecked = false
            switchCheck = "OFF"
            markOffDuty()
        }

        // Set up listener for switch button changes
        binding.switchbtn.setOnCheckedChangeListener { _, isChecked ->
            switchCheck = if (isChecked) {
                "ON"
            } else {
                "OFF"
            }
            markOnDuty()
        }
        markOnObserver()
        updateLocationObserver()



        binding.HomeFragmentDri.setOnClickListener {
            binding.firstConsLayouttt.visibility = View.VISIBLE
            requestLocationUpdates()
            val homeFragment = HomeFragment()
            replace_fragment(homeFragment)
            getUserProfileObserver()
            navItemIndex = 1
            binding.homeIconDri.setImageResource(R.drawable.home_1)
            binding.orderIconDri.setImageResource(R.drawable.order_icon)
            binding.moreIcon.setImageResource(R.drawable.more_icon)
            binding.tripHisIconDri.setImageResource(R.drawable.hostory_icon)
            binding.profileIconDri.setImageResource(R.drawable.profile)

            binding.profileTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.tripHistoryTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.moreTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.orderTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.homeTextDri.setTextColor(application.resources.getColor(R.color.black))
        }

        binding.OrderFragmentDri.setOnClickListener {
            binding.firstConsLayouttt.visibility = View.VISIBLE
            requestLocationUpdates()
            val driverOrdersFragment = DriverOrdersFragment()
            replace_fragment(driverOrdersFragment)
            navItemIndex = 2
            binding.homeIconDri.setImageResource(R.drawable.home_icon)
            binding.orderIconDri.setImageResource(R.drawable.order_1)
            binding.moreIcon.setImageResource(R.drawable.more_icon)
            binding.tripHisIconDri.setImageResource(R.drawable.hostory_icon)
            binding.profileIconDri.setImageResource(R.drawable.profile)

            binding.profileTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.tripHistoryTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.moreTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.homeTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.orderTextDri.setTextColor(application.resources.getColor(R.color.black))
        }
        binding.LinearMoreIcon.setOnClickListener {
            binding.firstConsLayouttt.visibility = View.VISIBLE
            requestLocationUpdates()
            val driverMoreFragment = DriverMoreFragment()
            replace_fragment(driverMoreFragment)
            navItemIndex = 3
            binding.homeIconDri.setImageResource(R.drawable.home_icon)
            binding.orderIconDri.setImageResource(R.drawable.order)
            binding.moreIcon.setImageResource(R.drawable.more_1)
            binding.tripHisIconDri.setImageResource(R.drawable.hostory_icon)
            binding.profileIconDri.setImageResource(R.drawable.profile)

            binding.profileTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.tripHistoryTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.moreTextDri.setTextColor(application.resources.getColor(R.color.black))
            binding.homeTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.orderTextDri.setTextColor(application.resources.getColor(R.color.logo_color))

        }
        binding.tripHistoryFragmentDri.setOnClickListener {
            binding.firstConsLayouttt.visibility = View.VISIBLE
            requestLocationUpdates()
            val tripHistoryFragment = TripHistoryFragment()
            replace_fragment(tripHistoryFragment)
            navItemIndex = 4
            binding.homeIconDri.setImageResource(R.drawable.home_icon)
            binding.orderIconDri.setImageResource(R.drawable.order)
            binding.moreIcon.setImageResource(R.drawable.more)
            binding.tripHisIconDri.setImageResource(R.drawable.history_1)
            binding.profileIconDri.setImageResource(R.drawable.profile)

            binding.profileTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.tripHistoryTextDri.setTextColor(application.resources.getColor(R.color.black))
            binding.moreTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.homeTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.orderTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
        }

        binding.PrfileDfragment.setOnClickListener {
            binding.firstConsLayouttt.visibility = View.GONE
            requestLocationUpdates()
            val driverProfileFragment = DriverProfileFragment()
            replace_fragment(driverProfileFragment)
            navItemIndex = 5
            binding.homeIconDri.setImageResource(R.drawable.home_icon)
            binding.orderIconDri.setImageResource(R.drawable.order)
            binding.moreIcon.setImageResource(R.drawable.more)
            binding.tripHisIconDri.setImageResource(R.drawable.hostory_icon)
            binding.profileIconDri.setImageResource(R.drawable.profile_1)

            binding.profileTextDri.setTextColor(application.resources.getColor(R.color.black))
            binding.tripHistoryTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.moreTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.homeTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.orderTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
        }


    }


    private fun getVehicleDetails() {
        val body = GetVehicleDetailsBody(
            language = languageId
        )
        getVDetailsViewModel.getApprovalDModeData(
            this,
            body
        )
    }

    private fun getVehicleDetailsObserver() {

        getVDetailsViewModel.mGetVDetails.observe(this) { response ->
            val data = response.peekContent().data
            val status = data?.approvalStatus
            PascoApp.encryptedPrefs.driverStatuss = status.toString()
            if (data!!.approvalStatus != "Approved") {
                if (isCheck) {
                    disableAllExceptMore()
                    openPopUp()
                    isCheck = false
                }

            } else if (data!!.approvalStatus == "Approved") {
                enableAll()
            }
            Log.e("status", "getVehicleDetailsObserver: " + PascoApp.encryptedPrefs.driverStatuss)

        }
    }

    private fun updateLocationObserver() {

        updateLocationViewModel.mUpdateLocationResponse.observe(this) { response ->
            val message = response.peekContent().msg!!
            if (response.peekContent().status.equals("False")) {
                // Toast.makeText(this@DriverDashboardActivity, "$message", Toast.LENGTH_LONG).show()
            } else {

                //   Toast.makeText(this@DriverDashboardActivity, "$message", Toast.LENGTH_LONG).show()
            }
        }
        updateLocationViewModel.errorResponse.observe(this@DriverDashboardActivity) {
            ErrorUtil.handlerGeneralError(this@DriverDashboardActivity, it)
            // errorDialogs()
        }
    }

    private fun markOffDuty() {
        // Assuming similar logic for marking off duty
        val body = MarkDutyBody(
            mark_status = switchCheck,
            language = languageId
        )
        markDutyViewModel.putMarkOn(progressDialog, activity, body)
    }

    private fun enableAll() {
        binding.HomeFragmentDri.isEnabled = true
        binding.OrderFragmentDri.isEnabled = true
        binding.tripHistoryFragmentDri.isEnabled = true
        binding.PrfileDfragment.isEnabled = true
        binding.notificationBtnDriver.isEnabled = true
        binding.switchbtn.isEnabled = true
        binding.LinearMoreIcon.isEnabled = true
    }

    private fun disableAllExceptMore() {
        binding.HomeFragmentDri.isEnabled = false
        binding.OrderFragmentDri.isEnabled = false
        binding.tripHistoryFragmentDri.isEnabled = false
        binding.PrfileDfragment.isEnabled = false
        binding.notificationBtnDriver.isEnabled = false
        binding.switchbtn.isEnabled = false
        binding.LinearMoreIcon.isEnabled = true
    }

    private fun openPopUp() {
        val builder = AlertDialog.Builder(this, R.style.Style_Dialog_Rounded_Corner)
        val dialogView = layoutInflater.inflate(R.layout.admin_approval_status, null)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val okButtonAdminA = dialogView.findViewById<TextView>(R.id.okButtonAdminA)
        dialog.show()
        okButtonAdminA.setOnClickListener {
            dialog.dismiss()
        }

    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this@DriverDashboardActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@DriverDashboardActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@DriverDashboardActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@DriverDashboardActivity,
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
                    runnable = object : Runnable {
                        override fun run() {

                            handler?.postDelayed(this, 2000) // 2000 milliseconds = 2 seconds
                        }
                    }

                    // Start the periodic task
                    handler?.post(runnable)
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
            val geocoder = Geocoder(this@DriverDashboardActivity, Locale.getDefault())
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
                    val countryCode = addressObj.countryCode
                    countryName = addressObj.countryName.toString()
                    val address = addresses[0].getAddressLine(0)
                    Log.e("City", city ?: "City not found")
                    city?.let { updateUI(it) }
                    if (address.isEmpty()) {
                        withContext(Dispatchers.Main) {
                            requestLocationPermission()
                            requestLocationUpdates()
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (!formattedLatitudeSelect.isNullOrBlank() && !formattedLongitudeSelect.isNullOrBlank()
            && !city.isNullOrBlank() && address.isNullOrBlank()
        ) {
            updateLocationDetails()
        }


    }

    private fun updateLocationDetails() {
        updateLocationBody = UpdationLocationBody(
            city.toString(),
            address.toString(),
            formattedLatitudeSelect,
            formattedLongitudeSelect, countryName.toString(),
            language = languageId
        )
        updateLocationViewModel.updateLocationDriver(
            activity,
            updateLocationBody
        )
    }


    private fun updateUI(city: String) {
        handler!!.post {
        }
    }

    private fun getProfileApi() {
        val loinBody = GetProfileBody(
            language = languageId
        )
        getProfileModelView.getProfile(this, progressDialog, loinBody)
    }


    private fun getUserProfileObserver() {
        getProfileModelView.progressIndicator.observe(this, Observer {
        })
        getProfileModelView.mRejectResponse.observe(this) { response ->
            val data = response.peekContent().data
            val baseUrl = "http://69.49.235.253:8090"
            val imagePath = data?.image.orEmpty()
            city = response.peekContent().data!!.currentCity
            if (city == "null") {
                binding.driverGreeting.text = "City"
            } else {
                binding.driverGreeting.text = city
            }
            val imageUrl = "$baseUrl$imagePath"
            if (imageUrl.isNotEmpty()) {
                Glide.with(this)
                    .load(imageUrl)
                    .into(binding.userIconDashBoard)
            }
            dutyOccupied = response.peekContent().duty!!.toInt()
            if (dutyOccupied == 2) {
                binding.switchbtn.isEnabled = false
                Toast.makeText(
                    this@DriverDashboardActivity,
                    getString(R.string.ride_before_completing_the_current_ride),
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                binding.switchbtn.isEnabled = true
            }



            Log.e("getDetails", "ObservergetUserProfile: " + dutyOccupied)
            // val helloName = data?.fullName?.split(" ")?.firstOrNull().orEmpty()
            //val hName = "Hello $helloName"
            binding.driverNameDash.text = response.peekContent().data!!.fullName

        }

        getProfileModelView.errorResponse.observe(this) {
            // Handle general errors
            ErrorUtil.handlerGeneralError(this@DriverDashboardActivity, it)

        }
    }

    private fun markOnDuty() {
        val body = MarkDutyBody(
            mark_status = switchCheck,
            language = languageId
        )
        markDutyViewModel.putMarkOn(
            progressDialog, activity, body
        )
    }

    //h
    private fun markOnObserver() {
        markDutyViewModel.mmarkDutyResponse.observe(this) { response ->
            val message = response.peekContent().msg!!
            if (response.peekContent().status == "True") {

                //  Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                //  Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                OnDutyStatus = response.peekContent().duty.toString()
                PascoApp.encryptedPrefs.CheckedType = OnDutyStatus
                val homeFragment = HomeFragment()
                replace_fragment(homeFragment)
            }

        }

        markDutyViewModel.errorResponse.observe(this) {
            // Handle general errors
            ErrorUtil.handlerGeneralError(this, it)
        }
    }

    private fun getNotificationCountDApi() {
        val body = GetProfileBody(language = languageId)
        notificationCountViewModel.getCountNoti(body)
    }

    private fun notificationCountDObserver() {
        notificationCountViewModel.mNotiCountResponse.observe(this) {
        }
        notificationCountViewModel.mNotiCountResponse.observe(this) {
            val message = it.peekContent().msg
            val success = it.peekContent().status
            val countNotification = it.peekContent().count
            if (countNotification == 0) {
                binding.countNotificationDri.visibility = View.GONE
            } else {
                binding.countNotificationDri.visibility = View.VISIBLE
                binding.countNotificationDri.text = countNotification.toString()
            }
        }

        notificationCountViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
            //errorDialogs()
        }
    }


    override fun onBackPressed() {
        if (shouldLoadHomeFragOnBackPress) {
            when (navItemIndex) {
                5 -> {
                    binding.firstConsLayouttt.visibility = View.GONE
                    val homeFragment = HomeFragment()
                    replace_fragment(homeFragment)
                    binding.profileIconDri.setColorFilter(application.resources.getColor(R.color.logo_color))
                    binding.tripHisIconDri.setColorFilter(application.resources.getColor(R.color.logo_color))
                    binding.moreIcon.setColorFilter(application.resources.getColor(R.color.logo_color))
                    binding.orderIconDri.setColorFilter(application.resources.getColor(R.color.logo_color))
                    binding.homeIconDri.setColorFilter(application.resources.getColor(R.color.black))
                    binding.profileTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
                    binding.tripHistoryTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
                    binding.moreTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
                    binding.orderTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
                    binding.homeTextDri.setTextColor(application.resources.getColor(R.color.black))
                }

                4, 3, 2 -> {
                    navItemIndex = 1
                    with(application.resources) {
                        binding.firstConsLayouttt.visibility = View.VISIBLE
                        val homeFragment = HomeFragment()
                        replace_fragment(homeFragment)
                        binding.profileIconDri.setColorFilter(application.resources.getColor(R.color.logo_color))
                        binding.tripHisIconDri.setColorFilter(application.resources.getColor(R.color.logo_color))
                        binding.moreIcon.setColorFilter(application.resources.getColor(R.color.logo_color))
                        binding.orderIconDri.setColorFilter(application.resources.getColor(R.color.logo_color))
                        binding.homeIconDri.setColorFilter(application.resources.getColor(R.color.black))
                        binding.profileTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
                        binding.tripHistoryTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
                        binding.moreTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
                        binding.orderTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
                        binding.homeTextDri.setTextColor(application.resources.getColor(R.color.black))
                    }
                }

                else -> {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastBackPressTime < backPressInterval) {
                        super.onBackPressed()
                        finishAffinity() // Closes all activities of the app
                    } else {
                        lastBackPressTime = currentTime
                        Toast.makeText(
                            this@DriverDashboardActivity,
                            getString(R.string.Please_again_to_exit),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }


    private fun replace_fragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.driverFrameLayout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            this@DriverDashboardActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Toast.makeText(this, getString(R.string.Permission_Granted), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    //Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                    openWarningPopUp()
                }
            }
        }
    }

    private fun openWarningPopUp() {
        val builder =
            AlertDialog.Builder(this@DriverDashboardActivity, R.style.Style_Dialog_Rounded_Corner)
        val dialogView = layoutInflater.inflate(R.layout.custom_permission_popup, null)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val okButtonWarning = dialogView.findViewById<Button>(R.id.okButtonWarning)
        dialog.show()

        okButtonWarning.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        //getProfileApi call
        if (notificaion == "1") {
            getNotificationCountDApi()
        }
        getProfileApi()
        requestLocationUpdates()
    }

    private fun getNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this@DriverDashboardActivity,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@DriverDashboardActivity,
                arrayOf<String>(Manifest.permission.POST_NOTIFICATIONS),
                101
            )
        }
    }

    fun startRepeatingTask() {
        runnable.run()
    }

    fun stopRepeatingTask() {
        handler!!.removeCallbacks(runnable)
    }

    override fun onStart() {
        super.onStart()
        startRepeatingTask()
    }

    override fun onStop() {
        super.onStop()
        stopRepeatingTask()
    }
}