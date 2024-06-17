package com.pasco.pascocustomer.customer.activity.notificaion

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.customer.activity.notificaion.adapter.NotificationAdapter
import com.pasco.pascocustomer.customer.activity.notificaion.clearnotification.ClearAllNotifcationViewModel
import com.pasco.pascocustomer.customer.activity.notificaion.delete.DeleteNotificationViewModel
import com.pasco.pascocustomer.customer.activity.notificaion.delete.NotificationBody
import com.pasco.pascocustomer.customer.activity.notificaion.modelview.NotificationModelView
import com.pasco.pascocustomer.customer.activity.notificaion.modelview.NotificationResponse
import com.pasco.pascocustomer.databinding.ActivityNotificationBinding
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class NotificationActivity : AppCompatActivity(), NotificationClickListener {
    private val getNotificationViewModel: NotificationModelView by viewModels()
    private val clearAllNotifcationViewModel: ClearAllNotifcationViewModel by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this@NotificationActivity) }
    private var notificationData: List<NotificationResponse.Datum> = ArrayList()
    private lateinit var binding: ActivityNotificationBinding
    private var notificationAdapter: NotificationAdapter? = null
    private val deleteNotificationViewModel: DeleteNotificationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backBtn.setOnClickListener { finish() }

        getNotification()
        getNotificationObserver()
        deleteNotificationObserver()

        binding.clearAllBtn.setOnClickListener {
            clearAllPopUp()
        }
        clearAllObserver()

    }

    private fun clearAllPopUp() {
        val builder = AlertDialog.Builder(
           this@NotificationActivity,
            R.style.Style_Dialog_Rounded_Corner
        )
        val dialogView = layoutInflater.inflate(R.layout.clear_all_notifications, null)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val noTextViewClearNoti = dialogView.findViewById<TextView>(R.id.noTextViewClearNoti)
        val yesTextViewClearNoti = dialogView.findViewById<TextView>(R.id.yesTextViewClearNoti)
        dialog.show()
        noTextViewClearNoti.setOnClickListener {
            dialog.dismiss()
        }
        yesTextViewClearNoti.setOnClickListener {
            clearAllNotification()
        }
    }

    private fun clearAllObserver() {
        clearAllNotifcationViewModel.progressIndicator.observe(this@NotificationActivity, Observer {
            // Handle progress indicator changes if needed
        })

        clearAllNotifcationViewModel.mClearAllNotificationsResponse.observe(this@NotificationActivity) { response ->
            val message = response.peekContent().msg!!
            val success = response.peekContent().status
            if (success == "True") {
                Toast.makeText(this@NotificationActivity, message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@NotificationActivity, message, Toast.LENGTH_SHORT).show()
            }

        }

        clearAllNotifcationViewModel.errorResponse.observe(this@NotificationActivity) {
            ErrorUtil.handlerGeneralError(this@NotificationActivity, it)
        }
    }

    private fun clearAllNotification() {
        clearAllNotifcationViewModel.getClearAllNotifications(
            progressDialog,
            this

        )
    }

    private fun getNotification() {
        getNotificationViewModel.getNotification(
            this,
            progressDialog
        )
    }

    private fun getNotificationObserver() {
        getNotificationViewModel.progressIndicator.observe(this@NotificationActivity, Observer {
            // Handle progress indicator changes if needed
        })

        getNotificationViewModel.mRejectResponse.observe(this@NotificationActivity) { response ->
            val message = response.peekContent().msg!!

            val success = response.peekContent().status
            if (success == "True") {
                notificationData = response.peekContent().data!!
                Log.e("NotificationList", "aaa" + notificationData.size)
                binding.notificationRecycler.visibility = View.VISIBLE
                binding.notificationRecycler.isVerticalScrollBarEnabled = true
                binding.notificationRecycler.isVerticalFadingEdgeEnabled = true
                binding.notificationRecycler.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                notificationAdapter = NotificationAdapter(this, this, notificationData)
                binding.notificationRecycler.adapter = notificationAdapter
            } else {
                //binding.noDataFoundTxt.visibility = View.VISIBLE
            }
        }

        getNotificationViewModel.errorResponse.observe(this@NotificationActivity) {
            ErrorUtil.handlerGeneralError(this@NotificationActivity, it)
        }
    }

    override fun deleteNotification(position: Int, id: Int) {
        deleteNotificationApi(id.toString())
    }

    override fun allBids(
        position: Int,
        id: Int,
        pickupLatitude: Double?,
        pickupLongitude: Double?
    ) {

    }

    private fun deleteNotificationApi(notiId: String) {
        val body = NotificationBody(
            id = notiId
        )
        deleteNotificationViewModel.getDeleteNotifications(progressDialog, this, body)

    }

    private fun deleteNotificationObserver() {
        deleteNotificationViewModel.mDeleteNotificationResponse.observe(this) { response ->
            val message = response.peekContent().msg!!

            if (response.peekContent().status == "False") {
                Toast.makeText(this, "failed: $message", Toast.LENGTH_LONG).show()
            } else {
                getNotification()
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
        deleteNotificationViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }


}