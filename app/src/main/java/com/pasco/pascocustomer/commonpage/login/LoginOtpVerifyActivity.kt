package com.pasco.pascocustomer.commonpage.login

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.pasco.pascocustomer.dashboard.DashboardActivity
import com.pasco.pascocustomer.databinding.ActivityLoginOtpVerifyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginOtpVerifyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginOtpVerifyBinding
    private lateinit var mAuth: FirebaseAuth
    private var strPhoneNo = ""
    private var loginValue = ""
    var verificationId = ""

    private val editTextList = mutableListOf<EditText>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginOtpVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        mAuth = FirebaseAuth.getInstance()
        val deviceModel = Build.MODEL
        verificationId = intent.getStringExtra("verificationId").toString()
        strPhoneNo = intent.getStringExtra("phoneNumber").toString()
        loginValue = intent.getStringExtra("loginValue").toString()


        binding.continueBtn.setOnClickListener {
             val verificationCode =
                 "${binding.box5.text}${binding.box1.text}${binding.box2.text}${binding.box3.text}${binding.box4.text}${binding.box6.text}"
             val credential: PhoneAuthCredential =
                 PhoneAuthProvider.getCredential(verificationId, verificationCode)
             signInWithPhoneAuthCredential(credential,deviceModel)
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }
        editTextList.addAll(
            listOf(
                binding.box5,
                binding.box1,
                binding.box2,
                binding.box3,
                binding.box4,
                binding.box6
            )
        )

        for (i in 0 until editTextList.size - 1) {
            editTextList[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1) {
                        editTextList[i + 1].requestFocus()
                    }
                }
            })
        }

        binding.box3.setOnEditorActionListener { _, actionId, _ ->
            actionId == EditorInfo.IME_ACTION_DONE
        }
    }



    private fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential,
        deviceModel: String
    ) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in successful, go to the next activity or perform desired actions
                    if (loginValue == "driver") {
                       // getLogin(deviceModel)
                    } else {
                     //   getUserLogin(deviceModel)
                    }
                } else {
                    // Sign in failed
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
            }
    }
}