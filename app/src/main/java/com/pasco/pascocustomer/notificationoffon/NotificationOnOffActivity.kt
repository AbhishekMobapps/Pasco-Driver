package com.pasco.pascocustomer.notificationoffon

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.databinding.ActivityNotificationOnOffBinding
import com.pasco.pascocustomer.language.Originator
import com.pasco.pascocustomer.notificationoffon.model.GetNotificationOnOffModelView
import com.pasco.pascocustomer.notificationoffon.model.GetOnOffNotificationBody
import com.pasco.pascocustomer.notificationoffon.model.NotificationOnOffBody
import com.pasco.pascocustomer.notificationoffon.model.NotificationOnOffModelView
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class NotificationOnOffActivity : Originator() {
    private lateinit var binding: ActivityNotificationOnOffBinding
    private var updateSwitch: Boolean = false
    private var statusUpdateSwitch: Boolean = false
    private var adminSwitch: Boolean = false
    private var completeSwitch: Boolean = false
    private var emergencySwitch: Boolean = false
    private var loyaltySwitch: Boolean = false

    private val notificationOnOffViewModel: NotificationOnOffModelView by viewModels()
    private val getNotificationOnOffViewModel: GetNotificationOnOffModelView by viewModels()

    private var language = ""
    private var languageId = ""
    private lateinit var sharedPreferencesLanguageName: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationOnOffBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener { finish() }


        binding.updateBidSwitch.setOnCheckedChangeListener { _, isChecked ->
            updateSwitch = isChecked
            allTypeNotificationApi()
            Log.e("CheckValueA", "updateSwitch..." + updateSwitch)
        }
        binding.statusUpdateSwitch.setOnCheckedChangeListener { _, isChecked ->
            statusUpdateSwitch = isChecked
            allTypeNotificationApi()

        }
        binding.adminSwitch.setOnCheckedChangeListener { _, isChecked ->
            adminSwitch = isChecked
            allTypeNotificationApi()

        }
        binding.completeSwitch.setOnCheckedChangeListener { _, isChecked ->
            completeSwitch = isChecked
            allTypeNotificationApi()

        }
        binding.emergencyhelpSwitch.setOnCheckedChangeListener { _, isChecked ->
            emergencySwitch = isChecked
            allTypeNotificationApi()

        }
        binding.loyaltyprogramSwitch.setOnCheckedChangeListener { _, isChecked ->
            loyaltySwitch = isChecked
            allTypeNotificationApi()

        }

        sharedPreferencesLanguageName = getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
        language = sharedPreferencesLanguageName.getString("language_text", "").toString()
        languageId = sharedPreferencesLanguageName.getString("languageId", "").toString()

        if (Objects.equals(language, "ar")) {
            binding.backBtn.setImageResource(R.drawable.next)
        }


        getAllOnOffNotiApi()
        getNotificationOnObserver()
        allTypeNotificationObserver()
    }


    private fun allTypeNotificationApi() {
        Log.e(
            "CheckValueA",
            "updateSwitch...$updateSwitch $statusUpdateSwitch  $adminSwitch  $completeSwitch $emergencySwitch  $loyaltySwitch"
        )
        val loinBody = NotificationOnOffBody(
            ridebooking = false,
            updatebid = updateSwitch,
            assignbooking = false,
            statusupdate = statusUpdateSwitch,
            adminnotification = adminSwitch,
            cancelbooking = false,
            completebooking = completeSwitch,
            reminder = false,
            emergencyhelp = emergencySwitch,
            coupon = false,
            loyaltyprogram = loyaltySwitch,
            language = languageId

        )
        notificationOnOffViewModel.allNotificationType(loinBody, this)
    }

    private fun allTypeNotificationObserver() {
        notificationOnOffViewModel.progressIndicator.observe(this) {}
        notificationOnOffViewModel.mNotificationResponse.observe(
            this
        ) {
            getAllOnOffNotiApi()
        }
        notificationOnOffViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@NotificationOnOffActivity, it)
        }
    }

    private fun getAllOnOffNotiApi() {
        val body = GetOnOffNotificationBody(
            language = languageId
        )
        getNotificationOnOffViewModel.getNotificationOnOff(this, body)
    }

    private fun getNotificationOnObserver() {
        getNotificationOnOffViewModel.progressIndicator.observe(this, Observer {
        })

        getNotificationOnOffViewModel.mRejectResponse.observe(this) { response ->

            val updateBid = response.peekContent().data?.updatebid
            val statusUpdateSwitch = response.peekContent().data?.statusupdate
            val adminSwitch = response.peekContent().data?.adminnotification
            val completeSwitch = response.peekContent().data?.completebooking
            val emergencySwitch = response.peekContent().data?.emergencyhelp
            val loyaltySwitch = response.peekContent().data?.loyaltyprogram

            binding.updateBidSwitch.isChecked = updateBid == true
            binding.statusUpdateSwitch.isChecked = statusUpdateSwitch == true
            binding.adminSwitch.isChecked = adminSwitch == true
            binding.completeSwitch.isChecked = completeSwitch == true
            binding.emergencyhelpSwitch.isChecked = emergencySwitch == true
            binding.loyaltyprogramSwitch.isChecked = loyaltySwitch == true

            Log.e("CheckStatusA", "updateSwitch....Api$updateBid")

        }

        getNotificationOnOffViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }
}