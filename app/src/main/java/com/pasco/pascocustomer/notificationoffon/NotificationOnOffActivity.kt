package com.pasco.pascocustomer.notificationoffon

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasco.pascocustomer.databinding.ActivityNotificationOnOffBinding
import com.pasco.pascocustomer.loyalty.adapter.LoyaltyProgramAdapter
import com.pasco.pascocustomer.notificationoffon.model.GetNotificationOnOffModelView
import com.pasco.pascocustomer.notificationoffon.model.NotificationOnOffBody
import com.pasco.pascocustomer.notificationoffon.model.NotificationOnOffModelView
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NotificationOnOffActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationOnOffBinding
    private var updateSwitch: Boolean = false
    private var statusUpdateSwitch: Boolean = false
    private var adminSwitch: Boolean = false
    private var completeSwitch: Boolean = false
    private var emergencySwitch: Boolean = false
    private var loyaltySwitch: Boolean = false

    private val notificationOnOffViewModel: NotificationOnOffModelView by viewModels()
    private val getNotificationOnOffViewModel: GetNotificationOnOffModelView by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationOnOffBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener { finish() }


        binding.updateBidSwitch.setOnCheckedChangeListener { _, isChecked ->
            updateSwitch = isChecked
            allTypeNotificationApi()
            allTypeNotificationObserver()
            Log.e("CheckStatusA", "updateSwitch....$updateSwitch")
        }
        binding.statusUpdateSwitch.setOnCheckedChangeListener { _, isChecked ->
            statusUpdateSwitch = isChecked
            allTypeNotificationApi()
            allTypeNotificationObserver()
        }
        binding.adminSwitch.setOnCheckedChangeListener { _, isChecked ->
            adminSwitch = isChecked
            allTypeNotificationApi()
            allTypeNotificationObserver()
        }
        binding.completeSwitch.setOnCheckedChangeListener { _, isChecked ->
            completeSwitch = isChecked
            allTypeNotificationApi()
            allTypeNotificationObserver()
        }
        binding.emergencyhelpSwitch.setOnCheckedChangeListener { _, isChecked ->
            emergencySwitch = isChecked
            allTypeNotificationApi()
            allTypeNotificationObserver()
        }
        binding.loyaltyprogramSwitch.setOnCheckedChangeListener { _, isChecked ->
            loyaltySwitch = isChecked
            allTypeNotificationApi()
            allTypeNotificationObserver()
        }

        getAllOnOffNotiApi()
        getNotificationOnObserver()
    }

    private fun setupSwitchListeners() {


    }


    private fun allTypeNotificationApi() {

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
            loyaltyprogram = loyaltySwitch

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
        getNotificationOnOffViewModel.getNotificationOnOff(this)
    }

    private fun getNotificationOnObserver() {
        getNotificationOnOffViewModel.progressIndicator.observe(this, Observer {
        })

        getNotificationOnOffViewModel.mRejectResponse.observe(this) { response ->

            val updateBid = response.peekContent().data?.updatebid
            binding.updateBidSwitch.isChecked = updateBid == true

        }

        getNotificationOnOffViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }
}