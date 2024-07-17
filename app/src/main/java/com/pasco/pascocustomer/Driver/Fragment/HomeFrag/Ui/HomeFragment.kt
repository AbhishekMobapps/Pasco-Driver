package com.pasco.pascocustomer.Driver.Fragment.HomeFrag.Ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
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
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.ApprovalStatus.ViewModel.GetApprovalStatusDModel
import com.pasco.pascocustomer.Driver.Fragment.HomeFrag.ViewModel.ShowBookingReqResponse
import com.pasco.pascocustomer.Driver.Fragment.HomeFrag.ViewModel.ShowBookingReqViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.pasco.pascocustomer.Driver.DriverMessageActivity
import com.pasco.pascocustomer.Driver.UpdateLocation.Ui.UpdateLocationActivity
import com.pasco.pascocustomer.Driver.adapter.AcceptRideAdapter
import com.pasco.pascocustomer.Driver.adapter.UpdateAddressAdapter
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.commonpage.login.signup.UpdateCity.UpdateCityBody
import com.pasco.pascocustomer.commonpage.login.signup.UpdateCity.UpdateCityResponse
import com.pasco.pascocustomer.commonpage.login.signup.UpdateCity.UpdateCityViewModel
import com.pasco.pascocustomer.customer.activity.SignUpCityName
import com.pasco.pascocustomer.customer.activity.updatevehdetails.GetVehicleDetailsBody
import com.pasco.pascocustomer.databinding.FragmentHomeDriverBinding
import com.pasco.pascocustomer.userFragment.home.sliderpage.SliderHomeBody
import com.pasco.pascocustomer.userFragment.home.sliderpage.SliderHomeModelView
import com.pasco.pascocustomer.userFragment.home.sliderpage.SliderHomeResponse
import com.pasco.pascocustomer.userFragment.pageradaper.ViewPagerAdapter
import com.pasco.pascocustomer.userFragment.profile.modelview.GetProfileBody
import com.pasco.pascocustomer.userFragment.profile.modelview.GetProfileModelView
import com.pasco.pascocustomer.utils.ErrorUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

import java.util.ArrayList
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(), SignUpCityName {
    private lateinit var binding: FragmentHomeDriverBinding
    private var dAdminApprovedId: String? = ""
    private var userType = ""
    private var currentPage = 0
    private var isLastPage = false
    private val NUM_PAGES = 3
    private var currentCityName: String? = ""
    private var address: String? = null
    private val sliderViewModel: SliderHomeModelView by viewModels()
    private val getVDetailsViewModel: GetApprovalStatusDModel by viewModels()
    private val getProfileModelView: GetProfileModelView by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(activity) }
    private var sliderList: ArrayList<SliderHomeResponse.Datum>? = null
    private var formattedCountryCode = ""
    private var selectCityName = ""
    private var countryName: String? = ""
    private var alertDialog: Dialog? = null
    private var rideRequestList: List<ShowBookingReqResponse.ShowBookingReqData> = ArrayList()

    @Inject
    lateinit var activity: Activity // Injecting activity
    private val showBookingReqViewModel: ShowBookingReqViewModel by viewModels()
    private lateinit var dialog: AlertDialog
    private var dialogRecyclerView: RecyclerView? = null
    private val updateCityViewModel: UpdateCityViewModel by viewModels()
    private var updateCityList: List<UpdateCityResponse.updateCityList> = ArrayList()
    private var updateAddressAdapter: UpdateAddressAdapter? = null


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    private lateinit var sharedPreferencesLanguageName: SharedPreferences
    private var language = ""
    private var languageId = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeDriverBinding.inflate(inflater, container, false)
        userType = PascoApp.encryptedPrefs.userType


        sharedPreferencesLanguageName = activity.getSharedPreferences(
            "PREFERENCE_NAME",
            AppCompatActivity.MODE_PRIVATE
        )
        language = sharedPreferencesLanguageName.getString("language_text", "").toString()
        languageId = sharedPreferencesLanguageName.getString("languageId", "").toString()

        getProfileApi()
        getUserProfileObserver()
        setupLocationClient()
        requestLocationPermission()
        getVehicleDetailsObserver()
        getVehicleDetails()

        binding.viewPagerDriver.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                currentPage = position

            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        val handler = Handler()
        val update: Runnable = Runnable {
            if (currentPage == NUM_PAGES) {
                currentPage = 0
            }
            binding.viewPagerDriver.setCurrentItem(currentPage++, true)
        }

        val swipeTimer = Timer()
        swipeTimer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, 2000, 2000)
        dAdminApprovedId = PascoApp.encryptedPrefs.driverStatuss
        //   showRideRequestApi(currentCityName)
        setupObservers()
        binding.LinearShareLocation.setOnClickListener {
            checkLocationPermissionAndShare()

        }

        binding.supportsLinearDri.setOnClickListener {
            val intent = Intent(requireContext(), DriverMessageActivity::class.java)
            startActivity(intent)
        }
        binding.LinearUpdateServiceLoc.setOnClickListener {
            val intent = Intent(requireContext(), UpdateLocationActivity::class.java)
            startActivity(intent)
        }
        sliderPageApi()
        sliderPageObserver()

        binding.consFilter.setOnClickListener {
            formattedCountryCode
            openFilterPopUp()
        }
        return binding.root
    }


    private fun getProfileApi() {
        val body = GetProfileBody(
            language = languageId
        )
        getProfileModelView.getProfile(
            activity,
            progressDialog,
            body
        )
    }

    private fun getUserProfileObserver() {
        getProfileModelView.progressIndicator.observe(requireActivity(), Observer {
        })
        getProfileModelView.mRejectResponse.observe(requireActivity()) { response ->
            val data = response.peekContent().data
            val baseUrl = "http://69.49.235.253:8090"
            val imagePath = data?.image.orEmpty()
            currentCityName = response.peekContent().data!!.currentCity
            if (currentCityName == "null") {
                currentCityName = "City"
            } else {
                currentCityName = response.peekContent().data!!.currentCity
                showRideRequestApi(currentCityName)
            }

        }

        getProfileModelView.errorResponse.observe(requireActivity()) {
            // Handle general errors
            ErrorUtil.handlerGeneralError(requireContext(), it)

        }
    }

    private fun getVehicleDetails() {
        val body = GetVehicleDetailsBody(
            language = languageId
        )
        getVDetailsViewModel.getApprovalDModeData(
            requireActivity(),
            body
        )
    }

    private fun getVehicleDetailsObserver() {

        getVDetailsViewModel.mGetVDetails.observe(requireActivity()) { response ->
            val data = response.peekContent().data
            val status = data?.approvalStatus
            PascoApp.encryptedPrefs.driverStatuss = status.toString()
            if (data!!.approvalStatus != "Approved") {
                disableAll()
            } else if (data!!.approvalStatus == "Approved") {
                enableAll()
            }
            Log.e("status", "getVehicleDetailsObserver: " + PascoApp.encryptedPrefs.driverStatuss)

        }
    }

    private fun setupLocationClient() {
        requestLocationPermission()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                updateLocation(it)
            }
        }
    }

    private fun updateLocation(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            try {
                val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)!!
                if (addresses.isNotEmpty()) {
                    val addressObj = addresses[0]
                    address = addressObj.getAddressLine(0)
                    // currentCityName = addressObj.locality
                    val countryCode = addressObj.countryCode
                    countryName = addressObj.countryName

                    // Get the phone country code using libphonenumber
                    val phoneUtil = PhoneNumberUtil.getInstance()
                    val phoneCountryCode = phoneUtil.getCountryCodeForRegion(countryCode)

                    // Log the country code and country name
                    Log.e("cityName", "$currentCityName: No country code found")
                    Log.e("Country Name", countryName ?: "No country name found")
                    Log.e("Phone Country Code", "+$phoneCountryCode")

                    formattedCountryCode = "+$phoneCountryCode"

                    PascoApp.encryptedPrefs.countryCode = formattedCountryCode
                    Log.e("hello", "city: $currentCityName")
                    if (address.isNullOrEmpty()) {
                        withContext(Dispatchers.Main) {
                            requestLocationPermission()
                        }
                    }


                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }

    private fun openFilterPopUp() {
        alertDialog = Dialog(requireActivity())
        alertDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog?.setCancelable(true)
        alertDialog?.setContentView(R.layout.filter_popup)
        alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //  alertDialog?.window?.setLayout(750, 1200)
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)

        val searchCountryName =
            alertDialog?.findViewById<androidx.appcompat.widget.SearchView>(R.id.searchCityNameFilter)
        dialogRecyclerView =
            alertDialog?.findViewById(R.id.searchableSpinnerRecycleViewFilter)!!



        searchCountryName?.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })



        getCityList()
        getCityListObserver(dialogRecyclerView!!)
        alertDialog?.show()
    }

    private fun getCityList() {
        Log.e("formattedCountryCode", "formattedCountryCode..AA" + formattedCountryCode)

        val cityBody = UpdateCityBody(
            countrycode = formattedCountryCode,
            language = languageId
        )
        updateCityViewModel.cityListData(cityBody, requireActivity(), progressDialog)
    }

    private fun getCityListObserver(dialogRecyclerView: RecyclerView) {
        updateCityViewModel.progressIndicator.observe(requireActivity(), Observer {
            // Handle progress indicator changes if needed
        })

        updateCityViewModel.mgetCityListResponse.observe(requireActivity()) { response ->
            val message = response.peekContent().msg
            updateCityList = response.peekContent().data ?: emptyList()

            if (response.peekContent().status == "False") {
                //Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            } else {
                dialogRecyclerView.isVerticalScrollBarEnabled = true
                dialogRecyclerView.isVerticalFadingEdgeEnabled = true
                dialogRecyclerView.layoutManager =
                    LinearLayoutManager(
                        requireActivity(),
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                updateAddressAdapter =
                    UpdateAddressAdapter(requireActivity(), updateCityList, this)
                dialogRecyclerView.adapter = updateAddressAdapter

                updateCityViewModel.errorResponse.observe(requireActivity()) {
                    ErrorUtil.handlerGeneralError(requireActivity(), it)
                }
            }
        }
    }

    private fun filterList(query: String?) {
        if (query != null) {
            val lowercaseQuery = query.lowercase(Locale.ROOT)
            val uppercaseQuery = query.uppercase(Locale.ROOT)
            val filterList = ArrayList<UpdateCityResponse.updateCityList>()
            for (i in updateCityList) {
                if (i.cityname?.lowercase(Locale.ROOT)
                        ?.contains(lowercaseQuery) == true || i.cityname?.uppercase(Locale.ROOT)
                        ?.contains(uppercaseQuery) == true
                ) {
                    filterList.add(i)
                }
            }
            if (filterList.isEmpty()) {
                Toast.makeText(requireActivity(), "No Data found", Toast.LENGTH_LONG).show()
            } else {
                updateAddressAdapter?.setFilteredList(filterList)
            }
        }
    }

    private fun checkLocationPermissionAndShare() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            shareLocation()
        }
    }

    private fun shareLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted, return early.
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    val locationUri = "https://maps.google.com/?q=$latitude,$longitude"
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Here's my location: $locationUri")
                        type = "text/plain"
                    }

                    startActivity(Intent.createChooser(shareIntent, "Share location using"))
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Unable to get current location.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                shareLocation()
            } else {
                Toast.makeText(requireContext(), "Location permission denied.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun sliderPageApi() {

        val bookingBody = SliderHomeBody(
            user_type = userType,
            language = languageId
        )
        sliderViewModel.otpCheck(bookingBody, requireActivity())
    }

    private fun sliderPageObserver() {

        sliderViewModel.mRejectResponse.observe(requireActivity()) { response ->
            sliderList = response.peekContent().data
            binding.viewPagerDriver.adapter = ViewPagerAdapter(requireContext(), sliderList!!)
            binding.indicator.setViewPager(binding.viewPagerDriver)

            binding.indicator.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageSelected(position: Int) {
                    currentPage = position
                }

                override fun onPageScrolled(pos: Int, arg1: Float, arg2: Int) {}
                override fun onPageScrollStateChanged(pos: Int) {}
            })
        }

        sliderViewModel.errorResponse.observe(requireActivity()) {
            ErrorUtil.handlerGeneralError(requireContext(), it)
        }
    }

    private fun enableAll() {
        binding.LinearShareLocation.isEnabled = true
        binding.supportsLinearDri.isEnabled = true
        binding.LinearUpdateServiceLoc.isEnabled = true
        binding.linearDriHEmergency.isEnabled = true
    }

    private fun disableAll() {
        binding.LinearShareLocation.isEnabled = false
        binding.supportsLinearDri.isEnabled = false
        binding.LinearUpdateServiceLoc.isEnabled = false
        binding.linearDriHEmergency.isEnabled = false
    }

    private fun showRideRequestApi(currentCityNames: String?) {
        showBookingReqViewModel.getShowBookingRequestsData(
            activity, currentCityNames.toString(), languageId
        )
    }

    private fun setupObservers() {
        showBookingReqViewModel.mShowBookingReq.observe(viewLifecycleOwner) { response ->
            val message = response.peekContent().msg
            rideRequestList = response.peekContent().data ?: emptyList()

            if (rideRequestList.isEmpty()) {
                binding.orderBidsHomeFragTextView.visibility = View.VISIBLE
                binding.recycerRideRequest.visibility = View.GONE
            } else {
                binding.orderBidsHomeFragTextView.visibility = View.GONE
                binding.recycerRideRequest.visibility = View.VISIBLE
                setupRecyclerView()
            }

            if (response.peekContent().status == "False") {
                // Toast.makeText(requireContext(), "$message", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupRecyclerView() {
        with(binding.recycerRideRequest) {
            isVerticalScrollBarEnabled = true
            isVerticalFadingEdgeEnabled = true
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = AcceptRideAdapter(requireContext(), rideRequestList, language)
        }
    }

    override fun onResume() {
        super.onResume()
        //call api
        getProfileApi()
    }

    override fun itemCity(id: Int, cityName: String) {
        selectCityName = cityName
        showRideRequestStatusApi(selectCityName)
        alertDialog?.dismiss()

    }

    private fun showRideRequestStatusApi(selectCityName: String) {
        showBookingReqViewModel.getShowBookingRequestsData(
            activity, selectCityName, languageId
        )
    }


}
