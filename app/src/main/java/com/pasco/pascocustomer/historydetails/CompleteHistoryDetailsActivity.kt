package com.pasco.pascocustomer.historydetails

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pasco.pascocustomer.BuildConfig
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.customerfeedback.CustomerFeedbackBody
import com.pasco.pascocustomer.customerfeedback.CustomerFeedbackModelView
import com.pasco.pascocustomer.dashboard.UserDashboardActivity
import com.pasco.pascocustomer.databinding.ActivityCompleteHistoryDetailsBinding
import com.pasco.pascocustomer.invoice.InvoiceActivity
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CompleteHistoryDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCompleteHistoryDetailsBinding
    private var id = ""
    private var bookingNumber = ""
    private var driverName = ""
    private var distance = ""
    private var feedback = ""
    private var completedDate = ""
    private var bookingStatus = ""
    private var paymentMethod = ""
    private var commissionPrice = ""
    private var totalAmount = ""
    private var pick = ""
    private var drop = ""
    private var driverImage = ""
    private var paymentStatus = ""

    var bottomSheetDialog: BottomSheetDialog? = null
    private val feedbackModelView: CustomerFeedbackModelView by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompleteHistoryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener { finish() }

        id = intent.getStringExtra("id").toString()
        bookingNumber = intent.getStringExtra("bookingNumber").toString()
        driverName = intent.getStringExtra("driverName").toString()
        completedDate = intent.getStringExtra("completedDate").toString()
        pick = intent.getStringExtra("pick").toString()
        drop = intent.getStringExtra("drop").toString()
        bookingStatus = intent.getStringExtra("bookingStatus").toString()
        paymentMethod = intent.getStringExtra("paymentMethod").toString()
        distance = intent.getStringExtra("distance").toString()
        feedback = intent.getStringExtra("feedback").toString()
        totalAmount = intent.getStringExtra("totalAmount").toString()
        commissionPrice = intent.getStringExtra("commissionPrice").toString()
        driverImage = intent.getStringExtra("driverImage").toString()
        paymentStatus = intent.getStringExtra("paymentStatus").toString()


        Log.e("CompleteHistoryA", "feedback...$feedback  $totalAmount $driverImage")

        Glide.with(this).load(BuildConfig.IMAGE_KEY + driverImage).placeholder(R.drawable.man)
            .into(binding.driverProfile)

        binding.driverName.text = driverName
        binding.bookingId.text = bookingNumber
        binding.pickupTxt.text = pick
        binding.dropTxt.text = drop
        binding.paymentMode.text = paymentMethod
        binding.paymentStatusMode.text = paymentStatus
        binding.totalAmount.text = totalAmount
        binding.bookingStatus.text = bookingStatus


        val formattedTotalDistance =
            "%.1f".format(distance.toDouble())
        binding.distance.text = "$formattedTotalDistance km"
        if (feedback == "1") {

            binding.feedbackBtn.visibility = View.GONE
        } else {
            binding.feedbackBtn.visibility = View.VISIBLE
        }

        binding.feedbackBtn.setOnClickListener { showFeedbackPopup(id) }


        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US)

        val dateTime = completedDate

        try {
            val parsedDate = inputDateFormat.parse(dateTime)
            outputDateFormat.timeZone = TimeZone.getDefault() // Set to local time zone
            val formattedDateTime = outputDateFormat.format(parsedDate)
            binding.bookingDate.text = formattedDateTime
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        binding.invoiceBtn.setOnClickListener {
            val intent = Intent(this, InvoiceActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
        }

    }

    private fun showFeedbackPopup(id: String) {
        bottomSheetDialog = BottomSheetDialog(this, R.style.TopCircleDialogStyle)
        val view = LayoutInflater.from(this).inflate(R.layout.feedback_popup, null)
        bottomSheetDialog!!.setContentView(view)

        Log.e("SHowFeed", "AAAA")
        val ratingBar = bottomSheetDialog?.findViewById<RatingBar>(R.id.ratingBar)
        val commentTxt = bottomSheetDialog?.findViewById<EditText>(R.id.commentTxt)
        val submitBtn = bottomSheetDialog?.findViewById<TextView>(R.id.submitBtn)
        val skipBtn = bottomSheetDialog?.findViewById<TextView>(R.id.skipBtn)

        var ratingBars = ""
        ratingBar?.setOnRatingBarChangeListener { _, rating, _ ->
            ratingBars = rating.toString()
        }

        submitBtn?.setOnClickListener {
            feedbackApi(commentTxt?.text.toString(), ratingBars, id.toInt())
            feedbackObserver()
        }
        skipBtn?.setOnClickListener { bottomSheetDialog?.dismiss() }

// Get the window of the dialog and set its height to match parent
        val dialogWindow = bottomSheetDialog?.window
        val layoutParams = dialogWindow?.attributes
        layoutParams?.height = WindowManager.LayoutParams.MATCH_PARENT
        dialogWindow?.attributes = layoutParams

        bottomSheetDialog?.show()
    }

    private fun feedbackApi(commentTxt: String, ratingBars: String, id: Int?) {
        val loinBody = CustomerFeedbackBody(
            bookingconfirmation = id.toString(), rating = ratingBars, feedback = commentTxt
        )
        feedbackModelView.cancelBooking(loinBody, this)
    }

    private fun feedbackObserver() {
        feedbackModelView.progressIndicator.observe(this) {}
        feedbackModelView.mRejectResponse.observe(
            this
        ) {
            val msg = it.peekContent().msg
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

            val intent = Intent(this, UserDashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
        feedbackModelView.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
// errorDialogs()
        }
    }
}