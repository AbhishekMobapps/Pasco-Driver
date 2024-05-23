package com.pasco.pascocustomer.commonpage.login

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.DriverDashboard.Ui.DriverDashboardActivity
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.commonpage.login.loginmodel.LoginBody
import com.pasco.pascocustomer.commonpage.login.loginmodel.LoginModelView
import com.pasco.pascocustomer.commonpage.login.loginotpcheck.OtpCheckModelView
import com.pasco.pascocustomer.commonpage.login.signup.SignUpActivity
import com.pasco.pascocustomer.commonpage.login.signup.clientmodel.ClientSignupBody
import com.pasco.pascocustomer.customer.activity.vehicledetailactivity.VehicleDetailsActivity
import com.pasco.pascocustomer.dashboard.DashboardActivity
import com.pasco.pascocustomer.dashboard.UserDashboardActivity
import com.pasco.pascocustomer.databinding.ActivityLoginBinding
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var loginValue = ""

    private lateinit var auth: FirebaseAuth
    private var strPhoneNo = ""
    private var countryCode = ""
    private var userType = ""
    private var verificationId: String = ""
    private val otpModel: OtpCheckModelView by viewModels()
    private val loginModel: LoginModelView by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginValue = "user"
        auth = FirebaseAuth.getInstance()

        val deviceModel = Build.MODEL
        binding.asDriverConst.setOnClickListener {
            binding.asDriverConst.setBackgroundResource(R.drawable.as_client_white_background)
            binding.driverTxt.setTextColor(ContextCompat.getColor(this, R.color.grey_dark))
            binding.clientTxt.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.asClientConst.setBackgroundResource(0)
            loginValue = "driver"
        }

        binding.asClientConst.setOnClickListener {
            binding.asClientConst.setBackgroundResource(R.drawable.as_client_white_background)
            binding.driverTxt.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.clientTxt.setTextColor(ContextCompat.getColor(this, R.color.grey_dark))
            binding.asDriverConst.setBackgroundResource(0)
            loginValue = "user"
        }
        binding.signUpBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.putExtra("loginValue", loginValue)
            startActivity(intent)
        }

        binding.continueBtn.setOnClickListener {
            strPhoneNo = binding.phoneNumber.text.toString()
            countryCode = binding.countryCode.text.toString()

            if (binding.phoneNumber.text.isEmpty()) {
                Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show()
            } else {
                if (loginValue == "driver") {
                    otpCheckApi(deviceModel)
                } else {
                    otpCheckApi(deviceModel)
                }
            }

        }
        // Observer
        checkLoginObserver(loginValue)
        loginObserver()
    }

    private fun otpCheckApi(deviceModel: String) {
        val loinBody = ClientSignupBody(
            phone_number = strPhoneNo,
            user_type = loginValue,
            phone_verify = deviceModel
        )
        otpModel.otpCheck(loinBody, this, progressDialog)
    }

    private fun checkLoginObserver(loginValue: String) {
        otpModel.progressIndicator.observe(this) {
        }
        otpModel.mRejectResponse.observe(
            this
        ) {

            val otpStatus = it.peekContent().login
            if (loginValue == "driver") {
                if (otpStatus == 0) {
                    sendVerificationCode("$countryCode$strPhoneNo")
                } else {
                    loginApi()
                }
            } else {
                if (otpStatus == 0) {
                    sendVerificationCode("$countryCode$strPhoneNo")
                } else {
                    loginApi()
                    Log.e("AAAAA", "aaa")
                }
            }

        }
        otpModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@LoginActivity, it)
            // errorDialogs()
        }
    }


    private fun loginApi() {
        //   val codePhone = strPhoneNo
        val loinBody = LoginBody(
            phone_number = strPhoneNo,
            user_type = loginValue
        )
        loginModel.otpCheck(loinBody, this, progressDialog)
    }

    private fun loginObserver() {
        loginModel.progressIndicator.observe(this) {
        }
        loginModel.mRejectResponse.observe(
            this
        ) {

            val token = it.peekContent().token
            val message = it.peekContent().msg
            val userId = it.peekContent().userId
            userType = it.peekContent().userType.toString()
            val approved = it.peekContent().approved
            PascoApp.encryptedPrefs.driverApprovedId = approved?.toString()!!
            PascoApp.encryptedPrefs.token = token?.refresh ?: ""
            PascoApp.encryptedPrefs.bearerToken = "Bearer ${token?.access ?: ""}"
            PascoApp.encryptedPrefs.userId = userId.toString()
            PascoApp.encryptedPrefs.userType = userType
            PascoApp.encryptedPrefs.profileUpdate = it.peekContent().profile.toString()
            PascoApp.encryptedPrefs.isFirstTime = false

            if (approved == 2 && userType == "driver") {
                Log.e("AAAAA", "aaaaaaa....")
                val intent = Intent(this@LoginActivity, VehicleDetailsActivity::class.java)
                startActivity(intent)
            } else if (loginValue == "driver" && userType == "driver") {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LoginActivity, DriverDashboardActivity::class.java)
                startActivity(intent)

            } else if (loginValue == "user" && userType == "user") {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LoginActivity, UserDashboardActivity::class.java)
                startActivity(intent)
            }

        }
        loginModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@LoginActivity, it)
            // errorDialogs()
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
                    this@LoginActivity.verificationId = verificationId

                    val intent = Intent(this@LoginActivity, LoginOtpVerifyActivity::class.java)
                    intent.putExtra("verificationId", verificationId)
                    intent.putExtra("phoneNumber", strPhoneNo)
                    intent.putExtra("loginValue", loginValue)
                    intent.putExtra("countryCode", countryCode)
                    startActivity(intent)

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


}