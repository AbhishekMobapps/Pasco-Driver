package com.pasco.pascocustomer.commonpage.login.signup

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.pasco.pascocustomer.commonpage.login.LoginActivity
import com.pasco.pascocustomer.databinding.ActivitySignUpBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding


    private lateinit var auth: FirebaseAuth
    private var strPhoneNo = ""
    private var verificationId: String = ""
    private var loginValue = ""
    private var strUserName = ""
    private var strEmail = ""
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var pickUplatitude = 0.0
    private var pickUplongitude = 0.0
    private var formattedLatitudeSelect: String = ""
    private var formattedLongitudeSelect: String = ""
    private var city: String? = null
    private var address: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        loginValue = intent.getStringExtra("loginValue").toString()



        if (loginValue == "driver")
        {
            binding.asDriverSignup.visibility = View.VISIBLE
            binding.asCustomerSignup.visibility = View.GONE
        }else
        {
            binding.asCustomerSignup.visibility = View.VISIBLE
            binding.asDriverSignup.visibility = View.GONE
        }


        binding.signInBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.signUpBtn.setOnClickListener {
            strUserName = binding.userName.text.toString()
            strEmail = binding.driverEmail.text.toString()
            address = binding.addressTxt.text.toString()
            if (loginValue == "driver")
            {
                validationDriver()
            }
            else
            {
                validationUser()
            }


        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        requestLocationUpdates()

        if (checkLocationPermission()) {
            requestLocationUpdates()
        } else {
            requestLocationPermission()
        }


    }
    private fun validationDriver() {
        with(binding) {
            if (userName.text.isNullOrBlank())
            {
                Toast.makeText(
                    applicationContext,
                    "Please enter name",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else if (driverEmail.text.isNullOrBlank())
            {
                Toast.makeText(
                    applicationContext,
                    "Please enter email",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else if (phoneNumber.text.isNullOrBlank()) {
                Toast.makeText(
                    applicationContext,
                    "Please enter phone number",
                    Toast.LENGTH_SHORT
                ).show()
            } else if(addressTxt.text.isNullOrBlank()) {
                Toast.makeText(
                    applicationContext,
                    "Please enter address",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else
            {
                strPhoneNo = binding.phoneNumber.text.toString()
                sendVerificationCode("+91$strPhoneNo")
            }
        }
    }

    private fun validationUser() {
        with(binding) {

             if (userPhoneNumber.text.isNullOrBlank()) {
                Toast.makeText(
                    applicationContext, "Please enter phone number", Toast.LENGTH_SHORT).show()
            }
            else
            {
                strPhoneNo = binding.userPhoneNumber.text.toString()
                sendVerificationCode("+91$strPhoneNo")
            }
        }
    }
    private fun sendVerificationCode(phoneNumber: String) {

        // showLoader()
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Automatically sign in the user when verification is done
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // Handle error
                    Log.e("UserMessage", "Verification failed: ${e.message}")
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    // Save the verification ID
                    this@SignUpActivity.verificationId = verificationId
                    if (loginValue =="driver")
                    {
                        val intent = Intent(this@SignUpActivity, OtpVerifyActivity::class.java)
                        intent.putExtra("verificationId", verificationId)
                        intent.putExtra("phoneNumber", strPhoneNo)
                        intent.putExtra("city", city)
                        intent.putExtra("email", strEmail)
                        intent.putExtra("address", address)
                        intent.putExtra("userName", strUserName)
                        intent.putExtra("loginValue", loginValue)
                        intent.putExtra("formattedLatitudeSelect", formattedLatitudeSelect)
                        intent.putExtra("formattedLongitudeSelect", formattedLongitudeSelect)
                        startActivity(intent)
                    }else
                    {
                        val intent = Intent(this@SignUpActivity, OtpVerifyActivity::class.java)
                        intent.putExtra("verificationId", verificationId)
                        intent.putExtra("phoneNumber", strPhoneNo)
                        intent.putExtra("loginValue", loginValue)
                        startActivity(intent)
                    }



                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in successful, go to the next activity or perform desired actions
                    Log.e("UserMessage", "onCreate: Successfully")

                } else {
                    // Sign in failed
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
            }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this@SignUpActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@SignUpActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@SignUpActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@SignUpActivity,
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

        pickUplatitude = latitude
        pickUplongitude = longitude
        formattedLatitudeSelect= String.format("%.5f", pickUplatitude)
        formattedLongitudeSelect = String.format("%.5f", pickUplongitude)

        GlobalScope.launch(Dispatchers.IO) {
            val geocoder = Geocoder(this@SignUpActivity, Locale.getDefault())
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
            binding.addressTxt.text = "$city"
        }
    }

}