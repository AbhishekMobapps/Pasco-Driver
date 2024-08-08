package com.pasco.pascocustomer.customer.activity.allbiddsdetailsactivity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.DriverWallet.DriverWalletActivity
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.customer.activity.allbiddsdetailsactivity.acceptreject.AcceptOrRejectBidBody
import com.pasco.pascocustomer.customer.activity.allbiddsdetailsactivity.acceptreject.AcceptOrRejectModelView
import com.pasco.pascocustomer.customer.activity.allbiddsdetailsactivity.acceptreject.RejectViewModel
import com.pasco.pascocustomer.customer.activity.allbiddsdetailsactivity.adapter.AllBiddsDetailsAdapter
import com.pasco.pascocustomer.customer.activity.allbiddsdetailsactivity.model.AllBiddsDetailResponse
import com.pasco.pascocustomer.customer.activity.allbiddsdetailsactivity.model.BiddsDtailsModelView
import com.pasco.pascocustomer.customer.activity.notificaion.NotificationClickListener
import com.pasco.pascocustomer.customer.activity.track.TrackActivity
import com.pasco.pascocustomer.dashboard.UserDashboardActivity
import com.pasco.pascocustomer.databinding.ActivityAllBiddsDetailsBinding
import com.pasco.pascocustomer.language.Originator
import com.pasco.pascocustomer.userFragment.order.odermodel.CustomerOrderBody
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AllBiddsDetailsActivity : Originator(), NotificationClickListener {
    private lateinit var binding: ActivityAllBiddsDetailsBinding
    private val progressDialog by lazy { CustomProgressDialog(this@AllBiddsDetailsActivity) }
    private var biddsDetailsList: List<AllBiddsDetailResponse.Datum> = ArrayList()
    private val detailsModel: BiddsDtailsModelView by viewModels()
    private val paymentAccept: AcceptOrRejectModelView by viewModels()
    private val rejectBids: RejectViewModel by viewModels()
    private var userName = ""
    private var orderId = ""
    private var dateTime = ""
    private var pickupLocation = ""
    private var dropLocation = ""
    private var distance = ""
    private var id = ""
    private var totalPrice = ""
    private var bookingId = ""
    private var language = ""
    private var languageId = ""
    private lateinit var sharedPreferencesLanguageName: SharedPreferences
    private var biddsDetailsAdapter: AllBiddsDetailsAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllBiddsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        sharedPreferencesLanguageName = getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
        language = sharedPreferencesLanguageName.getString("language_text", "").toString()
        languageId = sharedPreferencesLanguageName.getString("languageId", "").toString()


        if (Objects.equals(language, "ar")) {
            binding.backBtn.setImageResource(R.drawable.next)

        }
        /*  userName = intent.getStringExtra("userName").toString()
          orderId = intent.getStringExtra("orderId").toString()
          dateTime = intent.getStringExtra("dateTime").toString()
          pickupLocation = intent.getStringExtra("pickupLocation").toString()
          dropLocation = intent.getStringExtra("dropLocation").toString()
          distance = intent.getStringExtra("distance").toString()
         // id = intent.getStringExtra("id").toString()
          totalPrice = intent.getStringExtra("totalPrice").toString()*/

        id = PascoApp.encryptedPrefs.bidId
        userName = PascoApp.encryptedPrefs.userName
        orderId = PascoApp.encryptedPrefs.orderId
        dateTime = PascoApp.encryptedPrefs.dateTime
        pickupLocation = PascoApp.encryptedPrefs.pickupLocation
        dropLocation = PascoApp.encryptedPrefs.dropLocation
        distance = PascoApp.encryptedPrefs.distance
        totalPrice = PascoApp.encryptedPrefs.totalPrice



        binding.userName.text = userName
        binding.oderIdTxt.text = truncateBookingNumber(orderId)
        binding.oderIdTxt.setOnClickListener {
            showFullAddressDialog(orderId)
        }

        Log.e("idAAAA", "id...$id")

        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US)

        try {
            val parsedDate = inputDateFormat.parse(dateTime)
            outputDateFormat.timeZone = TimeZone.getDefault() // Set to local time zone
            val formattedDateTime = outputDateFormat.format(parsedDate)

            binding.dateTime.text = formattedDateTime
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        binding.pickUpLocation.text = pickupLocation
        binding.dropLocation.text = dropLocation

        val distanceValue = distance.toDoubleOrNull() ?: 0.0 // Convert distance to Double
        val formattedDistance = String.format("%.2f", distanceValue)
        binding.distanceTxt.text = "$formattedDistance Km"

        binding.totalPriceTxt.text = "$ $totalPrice"

        binding.backBtn.setOnClickListener { finish() }

        getBiddsDetailsList()
        biddsDetailsObserver()

    }


    private fun getBiddsDetailsList() {
        val body = CustomerOrderBody(
            language = languageId
        )
        detailsModel.getBidds(id, body, this, progressDialog)
    }

    @SuppressLint("SetTextI18n")
    private fun biddsDetailsObserver() {
        detailsModel.progressIndicator.observe(this@AllBiddsDetailsActivity) {
        }
        detailsModel.mRejectResponse.observe(this@AllBiddsDetailsActivity) {
            val message = it.peekContent().msg
            val success = it.peekContent().status


            if (success == "True") {
                biddsDetailsList = it.peekContent().data!!
                val guestSize = biddsDetailsList.size
                Log.e("eventId", "onCreateSize: " + biddsDetailsList.size)
                //binding.joinDate.text=joinDate.toString()
                binding.detailsRecycler.isVerticalScrollBarEnabled = true
                binding.detailsRecycler.isVerticalFadingEdgeEnabled = true
                binding.detailsRecycler.layoutManager =
                    LinearLayoutManager(
                        this@AllBiddsDetailsActivity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                biddsDetailsAdapter =
                    AllBiddsDetailsAdapter(this@AllBiddsDetailsActivity, biddsDetailsList, this)
                binding.detailsRecycler.adapter = biddsDetailsAdapter


            }

        }
        detailsModel.errorResponse.observe(this@AllBiddsDetailsActivity) {
            ErrorUtil.handlerGeneralError(this@AllBiddsDetailsActivity, it)
            //errorDialogs()
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
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(getString(R.string.order_id))
        alertDialogBuilder.setMessage(fullBookingNumber)
        alertDialogBuilder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    // Reject Bids....................................
    override fun deleteNotification(position: Int, id: Int) {
        Log.e("upFrontPriceAA", "paymentMethod..$id")
        rejectBidsApi(id.toString())
        rejectBidsObserver()
    }

    // ACCEPT Bids....................................
    override fun allBids(
        position: Int,
        id: Int,
        pickupLatitude: Double?,
        pickupLongitude: Double?,
        verificationCode: String?,
        upFrontPrice: Double?,
        paymentMethod: String?
    ) {
        bookingId = id.toString()
        acceptOrRejectApi(bookingId, upFrontPrice, paymentMethod)
        acceptOrRejectObserver(bookingId, pickupLatitude, pickupLongitude, verificationCode)

    }

    private fun acceptOrRejectApi(id: String, upFrontPrice: Double?, paymentMethod: String?) {
        val loinBody = AcceptOrRejectBidBody(
            payment_amount = upFrontPrice.toString(),
            payment_type = "wallet",
            language = languageId
        )
        paymentAccept.otpCheck(id, loinBody, this, progressDialog)
    }

    private fun acceptOrRejectObserver(
        bookingId: String,
        pickupLatitude: Double?,
        pickupLongitude: Double?,
        verificationCode: String?
    ) {
        paymentAccept.progressIndicator.observe(this) {}
        paymentAccept.mRejectResponse.observe(
            this
        ) {
            val msg = it.peekContent().msg
            val status = it.peekContent().status

            if (status == "False") {
                showWalletRequirementPopup()
                Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, TrackActivity::class.java)
                intent.putExtra("bookingId", bookingId)
                intent.putExtra("pickupLatitude", pickupLatitude.toString())
                intent.putExtra("pickupLongitude", pickupLongitude.toString())
                intent.putExtra("verificationCode", verificationCode)
                PascoApp.encryptedPrefs.bidId = ""
                PascoApp.encryptedPrefs.userName = ""
                PascoApp.encryptedPrefs.pickupLocation = ""
                PascoApp.encryptedPrefs.dropLocation = ""
                PascoApp.encryptedPrefs.totalPrice = ""
                PascoApp.encryptedPrefs.dateTime = ""
                PascoApp.encryptedPrefs.distance = ""
                startActivity(intent)
                finish()
                Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
            }


        }
        paymentAccept.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@AllBiddsDetailsActivity, it)
            // errorDialogs()
        }
    }


    private fun rejectBidsApi(id: String) {
        val loinBody = CustomerOrderBody(
            language = languageId
        )
        rejectBids.rejectBids(id, loinBody, this, progressDialog)
    }

    private fun rejectBidsObserver() {
        rejectBids.progressIndicator.observe(this) {}
        rejectBids.mRejectResponse.observe(
            this
        ) {
            val msg = it.peekContent().msg
            val status = it.peekContent().status

            if (status == "False") {
                Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, UserDashboardActivity::class.java)
                startActivity(intent)
                finish()
                Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
            }


        }
        rejectBids.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@AllBiddsDetailsActivity, it)
            // errorDialogs()
        }
    }

    private fun showWalletRequirementPopup() {
        val message = getString(R.string.Please_add_amount_in_your_wallet)

        val builder = AlertDialog.Builder(this@AllBiddsDetailsActivity)
        builder.setTitle(getString(R.string.Insufficient_wallet_amount))
        builder.setMessage(message)
        builder.setPositiveButton(getString(R.string.Add_Funds)) { dialog, _ ->
            val intent = Intent(this, DriverWalletActivity::class.java)
            intent.putExtra("addWallet", "wallet")
            startActivity(intent)
            finish()
            dialog.dismiss()
        }

        builder.setNegativeButton(getString(R.string.skip)) { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }
}