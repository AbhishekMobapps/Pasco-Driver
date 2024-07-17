package com.pasco.pascocustomer.Driver.Fragment.DriverAllBiddsDetail.Ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.Fragment.DriverAllBiddsDetail.ViewModel.GetDriverBidDetailsDataViewModel
import com.pasco.pascocustomer.Driver.customerDetails.CustomerDetailsActivity
import dagger.hilt.android.AndroidEntryPoint
import com.pasco.pascocustomer.activity.Driver.adapter.DriverAllBiddDetailAdapter
import com.pasco.pascocustomer.databinding.ActivityDriverAllBiddsBinding
import com.pasco.pascocustomer.userFragment.order.odermodel.CustomerOrderBody
import com.pasco.pascocustomer.utils.ErrorUtil
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class DriverAllBiddsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDriverAllBiddsBinding
    private val getDriverBidDetailsDataViewModel: GetDriverBidDetailsDataViewModel by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this@DriverAllBiddsActivity) }
    private lateinit var activity: Activity
    private var BookingID = ""
    private lateinit var sharedPreferencesLanguageName: SharedPreferences
    private var languageId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverAllBiddsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this

        sharedPreferencesLanguageName = getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
        languageId = sharedPreferencesLanguageName.getString("languageId", "").toString()


        binding.backArrowBiddingDetailsORD.setOnClickListener {
            finish()
        }
        BookingID = intent.getStringExtra("id").toString()
        getDriverDataApi()
        getDriverDataObserver()
    }

    private fun getDriverDataApi() {
        val body = CustomerOrderBody(
            language = languageId
        )
        getDriverBidDetailsDataViewModel.getDriverBidingData(
            progressDialog,
            activity,
            BookingID,
            body
        )
    }

    private fun getDriverDataObserver() {
        getDriverBidDetailsDataViewModel.progressIndicator.observe(this@DriverAllBiddsActivity) {
            // Handle progress indicator changes if needed
        }

        getDriverBidDetailsDataViewModel.mgetDBiddDataResponse.observe(this@DriverAllBiddsActivity) { response ->
            val message = response.peekContent().msg!!
            val data = response.peekContent().data

            val BookIddd = data!!.id.toString()
            val price = "$${data.basicprice}"
            val bPrice = "$${data.bidPrice}"
            val commissionPrice = "$${data.commissionPrice}"
            val dateTime = data.pickupDatetime.toString()
            val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val outputDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US)

            val baseUrl = "http://69.49.235.253:8090"
            val imagePath = data.userImage.orEmpty()

            val imageUrl = "$baseUrl$imagePath"
            Glide.with(this@DriverAllBiddsActivity)
                .load(imageUrl)
                .into(binding.driverSeeUserProfile)

            try {
                val parsedDate = inputDateFormat.parse(dateTime)
                outputDateFormat.timeZone = TimeZone.getDefault() // Set to local time zone
                val formattedDateTime = outputDateFormat.format(parsedDate)
                binding.orderDetailDR.text = formattedDateTime
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            val status = data.customerStatus.toString()
            Log.e("status", "onBindViewHolder: $status")

            val pickupCity = data.pickupLocation.toString()
            val dropCity = data.dropLocation.toString()
            binding.pickUpDetailsORD.text = pickupCity
            binding.DropDetailsORD.text = dropCity
            val formattedDistance = String.format("%.1f", data.totalDistance)
            binding.distanceDORD.text = "$formattedDistance km"

            binding.totalPricestaticDORD.text = price
            binding.maxPriceDORD.text = bPrice
            binding.cPriceDORD.text = commissionPrice
            binding.clientNameOrdR.text = data.user
            binding.orderIdDynamicDORD.text = truncateBookingNumber(data.bookingNumber.toString())
            binding.driverSeeUserProfile.setOnClickListener {
                val id = data.id.toString()
                val intent =
                    Intent(this@DriverAllBiddsActivity, CustomerDetailsActivity::class.java)
                intent.putExtra("customerId", id)
                startActivity(intent)
            }

            binding.orderIdDynamicDORD.setOnClickListener {
                showFullAddressDialog(data.bookingNumber.toString())
            }

            if (response.peekContent().status == "False") {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        getDriverBidDetailsDataViewModel.errorResponse.observe(this@DriverAllBiddsActivity) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }

    private fun truncateBookingNumber(bookingNumber: String, maxLength: Int = 8): String {
        return if (bookingNumber.length > maxLength) {
            "${bookingNumber.substring(0, maxLength)}..."
        } else {
            bookingNumber
        }
    }

    private fun showFullAddressDialog(fullBookingNumber: String) {
        val alertDialogBuilder = AlertDialog.Builder(this@DriverAllBiddsActivity)
        alertDialogBuilder.setTitle("Order ID")
        alertDialogBuilder.setMessage(fullBookingNumber)
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onResume() {
        super.onResume()
        // Call API to refresh data
        getDriverDataApi()
    }
}
