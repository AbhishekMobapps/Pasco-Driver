package com.pasco.pascocustomer.Driver.DriverDashboard.Ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
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
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.transportapp.DriverApp.MarkDuty.MarkDutyViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.ApprovalStatus.Ui.ApprovalStatusActivity
import com.pasco.pascocustomer.Driver.CouponDetails.Ui.CouponsAndEarningActivity
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.Driver.ContactWithUsActivity
import com.pasco.pascocustomer.Driver.DriverMessageActivity
import com.pasco.pascocustomer.Driver.EmergencyResponse.Ui.EmergencyCallActivity
import com.pasco.pascocustomer.Driver.Fragment.DriverOrders.DriverHistoryFragment
import com.pasco.pascocustomer.Driver.Fragment.EarningFragment
import com.pasco.pascocustomer.Driver.Fragment.HomeFrag.Ui.HomeFragment
import com.pasco.pascocustomer.Driver.Fragment.SettingsFragment
import com.pasco.pascocustomer.activity.Driver.PrivacyPolicyActivity
import com.pasco.pascocustomer.Driver.UpdateLocation.Ui.UpdateLocationActivity
import com.pasco.pascocustomer.Driver.adapter.TermsAndConditionsActivity
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.commonpage.login.LoginActivity
import com.pasco.pascocustomer.customer.activity.notificaion.notificationcount.NotificationCountViewModel
import com.pasco.pascocustomer.databinding.ActivityDriverDashboardBinding
import com.pasco.pascocustomer.userFragment.logoutmodel.LogOutModelView
import com.pasco.pascocustomer.userFragment.logoutmodel.LogoutBody
import com.pasco.pascocustomer.userFragment.profile.modelview.GetProfileModelView
import com.pasco.pascocustomer.utils.ErrorUtil
import java.io.IOException
import java.util.Locale

@AndroidEntryPoint
class DriverDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDriverDashboardBinding
    private lateinit var naview: NavigationView
    private var city: String? = null
    private var address: String? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val handler = Handler(Looper.getMainLooper())
    lateinit var myDashConstraint: ConstraintLayout
    lateinit var CheckApproveConstraint: ConstraintLayout
    lateinit var couponsEarningsConstraint: ConstraintLayout
    lateinit var CouponsConstraint: ConstraintLayout
    lateinit var loyaltyProgramConstraint: ConstraintLayout
    lateinit var myMessageConstraint: ConstraintLayout
    lateinit var myTermsConditionConstraint: ConstraintLayout
    lateinit var myPrivacyPolicyConstraint: ConstraintLayout
    lateinit var contactWithusConstraint: ConstraintLayout
    lateinit var whousConstraint: ConstraintLayout
    lateinit var logOutConstraint: ConstraintLayout
    lateinit var deleteConstraint: ConstraintLayout
    lateinit var procircleImage: CircleImageView
    lateinit var userProNameText: TextView
    lateinit var userProEmailText: TextView
    private var lastBackPressTime = 0L
    private val backPressInterval = 2000
    private var shouldLoadHomeFragOnBackPress = true
    val markDutyViewModel: MarkDutyViewModel by viewModels()
    private val getProfileModelView: GetProfileModelView by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }
    private lateinit var activity: Activity
    private var driverId = ""
    private var approvedID = ""
    private var navItemIndex = 1
    private var refersh = ""
    var isCouponsVisible = false
    private var switchChecked = false
    private val logoutViewModel: LogOutModelView by viewModels()
    val notificationCountViewModel: NotificationCountViewModel by viewModels()
    private var countDri = ""
    private var switcCheck = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this
        driverId = PascoApp.encryptedPrefs.userId
        approvedID = PascoApp.encryptedPrefs.approvedId
        refersh = PascoApp.encryptedPrefs.token
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationUpdates()
        if (checkLocationPermission()) {
            requestLocationUpdates()
        } else {
            requestLocationPermission()
        }
        binding.firstConsLayouttt.visibility = View.VISIBLE
        

        //call get notiCount
        getCountDri()
        //getCountObserver
        getCountObserverDri()
        getProfileApi()
        getUserProfileObserver()


        naview = findViewById(R.id.naview)
        procircleImage = findViewById(R.id.procircleImage)
        userProNameText = findViewById(R.id.userProNameText)
        userProEmailText = findViewById(R.id.userProEmailText)
        myDashConstraint = findViewById(R.id.myDashConstraint)
        CheckApproveConstraint = findViewById(R.id.CheckApproveConstraint)
        myMessageConstraint = findViewById(R.id.myMessageConstraint)
        myTermsConditionConstraint = findViewById(R.id.myTermsConditionConstraint)
        myPrivacyPolicyConstraint = findViewById(R.id.myPrivacyPolicyConstraint)
        whousConstraint = findViewById(R.id.whousConstraint)
        contactWithusConstraint = findViewById(R.id.contactWithusConstraint)
        logOutConstraint = findViewById(R.id.logOutConstraint)
        deleteConstraint = findViewById(R.id.deleteConstraint)
        couponsEarningsConstraint = findViewById(R.id.couponsEarningsConstraint)
        CouponsConstraint = findViewById(R.id.CouponsConstraint)
        loyaltyProgramConstraint = findViewById(R.id.loyaltyProgramConstraint)

        naview.itemIconTintList = null
        binding.userIconDashBoard.setOnClickListener {
            binding.drawer.openDrawer(GravityCompat.START)
            //getProfileApi call
            getProfileApi()
            //call observer
            getUserProfileObserver()
        }
        //call observer
        markOnObserver()

        // Retrieve the value from storage
        switcCheck = PascoApp.encryptedPrefs.CheckedType
        binding.switchbtn.isChecked = switcCheck == "1"
        binding.switchbtn.setOnCheckedChangeListener { buttonView, isChecked ->
            val value = if (isChecked) "1" else "0"
            PascoApp.encryptedPrefs.CheckedType = value
            if (isChecked) {
                markOnDuty()
            } else {
                markOnDuty()
            }
        }
        if (switcCheck == "0") {
            binding.switchbtn.isChecked = false
        }

        val homeFragment = HomeFragment()
        replace_fragment(homeFragment)

        binding.HomeFragmentDri.setOnClickListener {
            binding.firstConsLayouttt.visibility = View.VISIBLE
            val homeFragment = HomeFragment()
            replace_fragment(homeFragment)
            getProfileApi()
            getUserProfileObserver()
            navItemIndex = 1
            binding.SettingIconDri.setColorFilter(application.resources.getColor(R.color.grey))
            binding.notificationIconDri.setColorFilter(application.resources.getColor(R.color.grey))
            binding.notiTextDri.setTextColor(application.resources.getColor(R.color.grey))
            binding.earningIcon.setColorFilter(application.resources.getColor(R.color.grey))
            binding.bookingIconDri.setColorFilter(application.resources.getColor(R.color.grey))
            binding.homeIconDri.setColorFilter(application.resources.getColor(R.color.logo_color))
            binding.SettingTextDri.setTextColor(application.resources.getColor(R.color.grey))
            binding.homeTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.bookingTextDri.setTextColor(application.resources.getColor(R.color.grey))
            binding.earningTextDri.setTextColor(application.resources.getColor(R.color.grey))
        }

        binding.HistotyFragmentDri.setOnClickListener {
            binding.firstConsLayouttt.visibility = View.VISIBLE
            val driverHistoryFragment = DriverHistoryFragment()
            replace_fragment(driverHistoryFragment)
            navItemIndex = 2
            binding.SettingIconDri.setColorFilter(application.resources.getColor(R.color.grey))
            binding.notificationIconDri.setColorFilter(application.resources.getColor(R.color.grey))
            binding.notiTextDri.setTextColor(application.resources.getColor(R.color.grey))
            binding.earningIcon.setColorFilter(application.resources.getColor(R.color.grey))
            binding.bookingIconDri.setColorFilter(application.resources.getColor(R.color.logo_color))
            binding.homeIconDri.setColorFilter(application.resources.getColor(R.color.grey))
            binding.SettingTextDri.setTextColor(application.resources.getColor(R.color.grey))
            binding.homeTextDri.setTextColor(application.resources.getColor(R.color.grey))
            binding.bookingTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.earningTextDri.setTextColor(application.resources.getColor(R.color.grey))
        }
        binding.LinearEarning.setOnClickListener {
            binding.firstConsLayouttt.visibility = View.VISIBLE
            val earningFragment = EarningFragment()
            replace_fragment(earningFragment)
            navItemIndex = 3
            binding.SettingIconDri.setColorFilter(application.resources.getColor(R.color.grey))
            binding.notificationIconDri.setColorFilter(application.resources.getColor(R.color.grey))
            binding.notiTextDri.setTextColor(application.resources.getColor(R.color.grey))
            binding.earningIcon.setColorFilter(application.resources.getColor(R.color.logo_color))
            binding.bookingIconDri.setColorFilter(application.resources.getColor(R.color.grey))
            binding.homeIconDri.setColorFilter(application.resources.getColor(R.color.grey))
            binding.SettingTextDri.setTextColor(application.resources.getColor(R.color.grey))
            binding.homeTextDri.setTextColor(application.resources.getColor(R.color.grey))
            binding.bookingTextDri.setTextColor(application.resources.getColor(R.color.grey))
            binding.earningTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
        }

        binding.NotificationFragmentDri.setOnClickListener {
            binding.firstConsLayouttt.visibility = View.VISIBLE
          //  val notificationFragment = NotificationFragment()
            //replace_fragment(notificationFragment)
            navItemIndex = 4
            binding.notificationIconDri.setColorFilter(application.resources.getColor(R.color.logo_color))
            binding.notiTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.SettingIconDri.setColorFilter(application.resources.getColor(R.color.grey))
            binding.earningIcon.setColorFilter(application.resources.getColor(R.color.grey))
            binding.bookingIconDri.setColorFilter(application.resources.getColor(R.color.grey))
            binding.homeIconDri.setColorFilter(application.resources.getColor(R.color.grey))
            binding.SettingTextDri.setTextColor(application.resources.getColor(R.color.grey))
            binding.homeTextDri.setTextColor(application.resources.getColor(R.color.grey))
            binding.bookingTextDri.setTextColor(application.resources.getColor(R.color.grey))
            binding.earningTextDri.setTextColor(application.resources.getColor(R.color.grey))
        }

        binding.SeetingDfragment.setOnClickListener {
            binding.firstConsLayouttt.visibility = View.GONE
            val settingsFragment = SettingsFragment()
            replace_fragment(settingsFragment)
            navItemIndex = 5
            binding.SettingIconDri.setColorFilter(application.resources.getColor(R.color.logo_color))
            binding.notificationIconDri.setColorFilter(application.resources.getColor(R.color.grey))
            binding.notiTextDri.setTextColor(application.resources.getColor(R.color.grey))
            binding.earningIcon.setColorFilter(application.resources.getColor(R.color.grey))
            binding.bookingIconDri.setColorFilter(application.resources.getColor(R.color.grey))
            binding.homeIconDri.setColorFilter(application.resources.getColor(R.color.grey))
            binding.SettingTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.homeTextDri.setTextColor(application.resources.getColor(R.color.grey))
            binding.bookingTextDri.setTextColor(application.resources.getColor(R.color.grey))
            binding.earningTextDri.setTextColor(application.resources.getColor(R.color.grey))
        }

        // Set listeners using a function
        setClickListeners()


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
                    location.latitude,
                    location.longitude,
                    1
                )!!
                if (addresses.isNotEmpty()) {
                    address = addresses[0].getAddressLine(0)
                    city = addresses[0].locality
                    city?.let { updateUI(it) }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun updateUI(city: String) {
        handler.post {
            binding.driverGreeting.text = "$city"
        }
    }

    private fun getCountObserverDri() {
        notificationCountViewModel.mNotiCountResponse.observe(this@DriverDashboardActivity) { response ->
            val message = response.peekContent().msg!!

            if (response.peekContent().status == "False") {
                Toast.makeText(this@DriverDashboardActivity, "failed: $message", Toast.LENGTH_LONG)
                    .show()
            } else if (response.peekContent().status == "True") {
                countDri = response.peekContent().count.toString()
                binding.countttDri.text = countDri

            }
        }
        notificationCountViewModel.errorResponse.observe(this@DriverDashboardActivity) {
            ErrorUtil.handlerGeneralError(this@DriverDashboardActivity, it)
        }
    }

    private fun getCountDri() {
        notificationCountViewModel.getCountNoti()
    }

    private fun getProfileApi() {
        getProfileModelView.getProfile(
            activity,
            progressDialog

        )
    }

    private fun getUserProfileObserver() {
        getProfileModelView.progressIndicator.observe(this, Observer {
        })
        getProfileModelView.mRejectResponse.observe(this) { response ->
            val data = response.peekContent().data
            val fullname = data?.fullName.toString()
            val email = data?.email.toString()
            val baseUrl = "http://69.49.235.253:8090"
            val imagePath = data?.image.orEmpty()

            val imageUrl = "$baseUrl$imagePath"

            if (imageUrl.isNotEmpty()) {
                Glide.with(this)
                    .load(imageUrl)
                    .into(procircleImage)
            } else {
                procircleImage.setImageResource(R.drawable.ic_launcher_background)
            }

            if (imageUrl.isNotEmpty()) {
                Glide.with(this)
                    .load(imageUrl)
                    .into(binding.userIconDashBoard)
            } else {
                binding.userIconDashBoard.setImageResource(R.drawable.ic_launcher_background)
            }

            Log.e("getDetails", "ObservergetUserProfile: ")

            userProNameText.text = fullname
            userProEmailText.text = email
            val helloName = data?.fullName.toString()
            var hName = "Hello $helloName"
            binding.driverNameDash.text = hName


        }

        getProfileModelView.errorResponse.observe(this) {
            // Handle general errors
            ErrorUtil.handlerGeneralError(this@DriverDashboardActivity, it)

        }
    }

    private fun markOnObserver() {
        markDutyViewModel.mmarkDutyResponse.observe(this) { response ->
            val message = response.peekContent().msg!!
            if (response.peekContent().status == "True") {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()


            }
        }

        markDutyViewModel.errorResponse.observe(this) {
            // Handle general errors
            ErrorUtil.handlerGeneralError(this, it)
        }
    }


    private fun markOnDuty() {
        markDutyViewModel.putMarkOn(
            activity
        )
    }

    private fun setClickListeners() {
        myDashConstraint.setOnClickListener {
            resetAllViews()
            myDashConstraint.setBackgroundResource(R.drawable.nav_txt_background)
            val intent =
                Intent(this@DriverDashboardActivity, DriverDashboardActivity::class.java)
            startActivity(intent)
        }

        myMessageConstraint.setOnClickListener {
            resetAllViews()
            myMessageConstraint.setBackgroundResource(R.drawable.nav_txt_background)
            val intent = Intent(this@DriverDashboardActivity, DriverMessageActivity::class.java)
            startActivity(intent)
        }
        couponsEarningsConstraint.setOnClickListener {
            resetAllViews()
            if (isCouponsVisible) {
                CouponsConstraint.visibility = View.GONE
                loyaltyProgramConstraint.visibility = View.GONE
                couponsEarningsConstraint.setBackgroundResource(0)
            } else {
                CouponsConstraint.visibility = View.VISIBLE
                loyaltyProgramConstraint.visibility = View.VISIBLE
                couponsEarningsConstraint.setBackgroundResource(R.drawable.nav_txt_background)
                CouponsConstraint.setOnClickListener {
                    resetAllViews()
                    CouponsConstraint.setBackgroundResource(R.drawable.nav_txt_background)
                    val intent = Intent(
                        this@DriverDashboardActivity,
                        CouponsAndEarningActivity::class.java
                    )
                    startActivity(intent)
                }
             /*   loyaltyProgramConstraint.setOnClickListener {
                    resetAllViews()
                    loyaltyProgramConstraint.setBackgroundResource(R.drawable.nav_txt_background)
                    val intent =
                        Intent(this@DriverDashboardActivity, LoyaltyBonusActivity::class.java)
                    startActivity(intent)
                }*/
            }
            isCouponsVisible = !isCouponsVisible // Toggle the flag
        }
        CheckApproveConstraint.setOnClickListener {
            resetAllViews()
            CheckApproveConstraint.setBackgroundResource(R.drawable.nav_txt_background)
            val intent =
                Intent(this@DriverDashboardActivity, ApprovalStatusActivity::class.java)
            startActivity(intent)
        }

        myTermsConditionConstraint.setOnClickListener {
            resetAllViews()
            myTermsConditionConstraint.setBackgroundResource(R.drawable.nav_txt_background)
            val intent =
                Intent(this@DriverDashboardActivity, TermsAndConditionsActivity::class.java)
            startActivity(intent)
        }

        myPrivacyPolicyConstraint.setOnClickListener {
            resetAllViews()
            myPrivacyPolicyConstraint.setBackgroundResource(R.drawable.nav_txt_background)
            val intent = Intent(this@DriverDashboardActivity, PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }
        contactWithusConstraint.setOnClickListener {
            resetAllViews()
            contactWithusConstraint.setBackgroundResource(R.drawable.nav_txt_background)
            val intent = Intent(this@DriverDashboardActivity, ContactWithUsActivity::class.java)
            startActivity(intent)
        }

        logOutConstraint.setOnClickListener {
            resetAllViews()
            logOutConstraint.setBackgroundResource(R.drawable.nav_txt_background)
            openLogoutPop()
            //logout observer
            logOutObserver()
        }
    }


    private fun resetAllViews() {
        val constraints = arrayOf(
            myDashConstraint,
            CheckApproveConstraint,
            couponsEarningsConstraint,
            CouponsConstraint,
            myMessageConstraint,
            myTermsConditionConstraint,
            myPrivacyPolicyConstraint,
            whousConstraint,
            loyaltyProgramConstraint,
            contactWithusConstraint
        )
        constraints.forEach { it.setBackgroundResource(0) }
    }

    @SuppressLint("MissingInflatedId")
    private fun openLogoutPop() {
        val builder = AlertDialog.Builder(
            this@DriverDashboardActivity,
            R.style.Style_Dialog_Rounded_Corner
        )
        val dialogView = layoutInflater.inflate(R.layout.logout_popup, null)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val cancelLogBtn = dialogView.findViewById<TextView>(R.id.cancelLogBtn)
        val yesLogoutBtn = dialogView.findViewById<TextView>(R.id.yesLogoutBtn)
        dialog.show()
        cancelLogBtn.setOnClickListener {
            dialog.dismiss()
        }
        yesLogoutBtn.setOnClickListener {
            logOutApi()
        }
    }

    private fun logOutApi() {
        val bookingBody = LogoutBody(
            refresh = refersh
        )
        logoutViewModel.otpCheck(bookingBody, activity)
    }

    private fun logOutObserver() {
        logoutViewModel.mRejectResponse.observe(this) { response ->
            val message = response.peekContent().msg
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()

            if (response.peekContent().status == "True")
            {
                PascoApp.encryptedPrefs.bearerToken = ""
                PascoApp.encryptedPrefs.userId = ""
                PascoApp.encryptedPrefs.isFirstTime = true
                val intent = Intent(activity, LoginActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
            }



        }

        logoutViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }


    override fun onBackPressed() {
        if (shouldLoadHomeFragOnBackPress) {
            when (navItemIndex) {
                5 -> {
                    binding.firstConsLayouttt.visibility = View.GONE
                }

                4, 3, 2 -> {
                    navItemIndex = 1
                    with(application.resources) {
                        binding.firstConsLayouttt.visibility = View.VISIBLE
                        binding.SettingIconDri.setColorFilter(getColor(R.color.grey))
                        binding.notificationIconDri.setColorFilter(getColor(R.color.grey))
                        binding.notiTextDri.setTextColor(getColor(R.color.grey))
                        binding.earningIcon.setColorFilter(getColor(R.color.grey))
                        binding.bookingIconDri.setColorFilter(getColor(R.color.grey))
                        binding.homeIconDri.setColorFilter(getColor(R.color.logo_color))
                        binding.SettingTextDri.setTextColor(getColor(R.color.grey))
                        binding.homeTextDri.setTextColor(getColor(R.color.logo_color))
                        binding.bookingTextDri.setTextColor(getColor(R.color.grey))
                        binding.earningTextDri.setTextColor(getColor(R.color.grey))
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
                            "Please click BACK again to exit",
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
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    //Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                    openWarningPopUp()
                }
            }
        }
    }

    private fun openWarningPopUp() {
        val builder = AlertDialog.Builder(this@DriverDashboardActivity, R.style.Style_Dialog_Rounded_Corner)
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
        getProfileApi()
    }

}