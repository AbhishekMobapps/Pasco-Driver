package com.pasco.pascocustomer.customer.activity.driverdetails

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.BuildConfig
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.customer.activity.driverdetails.modelview.DriverDetailsModelView
import com.pasco.pascocustomer.databinding.ActivityDriverDetailsBinding
import com.pasco.pascocustomer.language.Originator
import com.pasco.pascocustomer.userFragment.order.odermodel.CustomerOrderBody
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.Objects

@AndroidEntryPoint
class DriverDetailsActivity : Originator() {
    private lateinit var binding: ActivityDriverDetailsBinding
    private val detailsModel: DriverDetailsModelView by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }
    private var id = ""
    private lateinit var sharedPreferencesLanguageName: SharedPreferences
    private var languageId = ""
    private var language = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDriverDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesLanguageName = getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
        languageId = sharedPreferencesLanguageName.getString("languageId", "").toString()
        language = sharedPreferencesLanguageName.getString("language_text", "en").toString()


        binding.imageBackReqRide.setOnClickListener { finish() }
        id = intent.getStringExtra("id").toString()
        getDriverDetails()
        driverDetailsObserver()
        if (Objects.equals(language,"ar"))
        {
            binding.imageBackReqRide.setImageResource(R.drawable.next)
        }

    }

    private fun getDriverDetails() {
        val body = CustomerOrderBody(
            language = languageId
        )
        detailsModel.getDriverDetails(id, this, progressDialog,body)
    }

    @SuppressLint("SetTextI18n")
    private fun driverDetailsObserver() {
        detailsModel.progressIndicator.observe(this@DriverDetailsActivity) {
        }
        detailsModel.mRejectResponse.observe(this@DriverDetailsActivity) {
            val message = it.peekContent().msg
            val success = it.peekContent().status

            binding.userName.text = it.peekContent().data?.driver
            binding.emailTxtA.text = it.peekContent().data?.email
            binding.contactTxtA.text = it.peekContent().data?.phoneNumber
            binding.currentCityTxt.text = it.peekContent().data?.currentCity

            val url = it.peekContent().data?.image
            Glide.with(this).load(BuildConfig.IMAGE_KEY + url).into(binding.profileImg)

        }
        detailsModel.errorResponse.observe(this@DriverDetailsActivity) {
            ErrorUtil.handlerGeneralError(this@DriverDetailsActivity, it)
            //errorDialogs()
        }
    }
}