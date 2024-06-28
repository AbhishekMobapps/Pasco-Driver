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

        getAllOnOffNotiApi()
        getNotificationOnObserver()
        allTypeNotificationObserver()
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