package com.pasco.pascocustomer.historydetails

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
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
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.BuildConfig
import com.pasco.pascocustomer.Driver.DriverDashboard.Ui.DriverDashboardActivity
import com.pasco.pascocustomer.Driver.driverFeedback.DriverFeedbackBody
import com.pasco.pascocustomer.Driver.driverFeedback.DriverFeedbackModelView
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.application.PascoApp
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
    private var userName = ""
    private var userImage = ""
    private var paymentStatus = ""
    private var userType = ""
    private var upfrontPayment = ""
    private val driverFeedbackModelView: DriverFeedbackModelView by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }
    var bottomSheetDialog: BottomSheetDialog? = null
    private val feedbackModelView: CustomerFeedbackModelView by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompleteHistoryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userType = PascoApp.encryptedPrefs.userType

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
        userImage = intent.getStringExtra("driverImage").toString()
        paymentStatus = intent.getStringExtra("paymentStatus").toString()
        userName = intent.getStringExtra("userNamee").toString()
        userImage = intent.getStringExtra("userImagee").toString()
        upfrontPayment = intent.getStringExtra("upfrontPayment").toString()
        if (userType == "driver") {
            Glide.with(this).load(BuildConfig.IMAGE_KEY + userImage)
                .placeholder(R.drawable.man).into(binding.driverProfile)

            binding.driverName.text = userName
            binding.upfrontAmountConst.visibility = View.GONE
        } else {
            Glide.with(this).load(BuildConfig.IMAGE_KEY + driverImage).placeholder(R.drawable.man)
                .into(binding.driverProfile)

            binding.driverName.text = driverName
            binding.upfrontAmountConst.visibility = View.VISIBLE
        }


        Log.e("CompleteHistoryA", "feedback...$feedback  $totalAmount $driverImage")


        binding.bookingId.text = bookingNumber
        binding.pickupTxt.text = pick
        binding.dropTxt.text = drop
        binding.paymentMode.text = paymentMethod
        binding.paymentStatusMode.text = paymentStatus
        binding.totalAmount.text = totalAmount
        binding.bookingStatus.text = bookingStatus
        binding.upfrontAmount.text = upfrontPayment


        val formattedTotalDistance =
            "%.1f".format(distance.toDouble())
        binding.distance.text = "$formattedTotalDistance km"
        if (feedback == "1") {

            binding.feedbackBtn.visibility = View.GONE
        } else {
            binding.feedbackBtn.visibility = View.VISIBLE
        }

        if (userType.equals("user"))
        {
            binding.feedbackBtn.setOnClickListener { showFeedbackPopup(id) }
        }
        else{
            binding.feedbackBtn.setOnClickListener { showFeedbackDriverPopup(id) }
        }




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

    private fun showFeedbackDriverPopup(id: String) {
        bottomSheetDialog = BottomSheetDialog(this, R.style.TopCircleDialogStyle)
        val view = LayoutInflater.from(this).inflate(R.layout.driver_feedback_popup, null)
        bottomSheetDialog!!.setContentView(view)


        val ratingBar = bottomSheetDialog?.findViewById<RatingBar>(R.id.ratingBar)
        val commentTxt = bottomSheetDialog?.findViewById<EditText>(R.id.commentTxt)
        val submitBtn = bottomSheetDialog?.findViewById<TextView>(R.id.submitBtn)
        val skipBtn = bottomSheetDialog?.findViewById<TextView>(R.id.skipBtn)

        var ratingBars = ""
        ratingBar?.setOnRatingBarChangeListener { _, rating, _ ->
            // Toast.makeText(this, "New Rating: $rating", Toast.LENGTH_SHORT).show()
            ratingBars = rating.toString()
        }

        submitBtn?.setOnClickListener {
            val comment = commentTxt?.text.toString()
            if (ratingBars.isEmpty()) {
                Toast.makeText(this, "Please add a rating", Toast.LENGTH_SHORT).show()
            } else if (comment.isBlank()) {
                Toast.makeText(this, "Please add a comment", Toast.LENGTH_SHORT).show()
            } else {
                feedbackApiDriver(comment, ratingBars)
                feedbackObserver()
            }
        }
        skipBtn?.setOnClickListener {
            bottomSheetDialog?.dismiss()
        }

        val displayMetrics = DisplayMetrics()
        (this as AppCompatActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels
        val halfScreenHeight = screenHeight * .58
        val eightyPercentScreenHeight = screenHeight * .58

        // Set the initial height of the bottom sheet to 50% of the screen height
        val layoutParams = view.layoutParams
        layoutParams.height = halfScreenHeight.toInt()
        view.layoutParams = layoutParams

        var isExpanded = false
        view.setOnClickListener {
            // Expand or collapse the bottom sheet when it is touched
            if (isExpanded) {
                layoutParams.height = halfScreenHeight.toInt()
            } else {
                layoutParams.height = eightyPercentScreenHeight.toInt()
            }
            view.layoutParams = layoutParams
            isExpanded = !isExpanded
        }

        bottomSheetDialog!!.show()

    }

    private fun feedbackApiDriver(comment: String, ratingBars: String) {
        val loinBody = DriverFeedbackBody(
            bookingconfirmation = id,
            rating = ratingBars,
            feedback = comment
        )
        driverFeedbackModelView.cancelBooking(loinBody, this, progressDialog)
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

            if (userType.equals("driver")) {
                val intent = Intent(this, DriverDashboardActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, UserDashboardActivity::class.java)
                startActivity(intent)
            }


            finish()
        }
        feedbackModelView.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
// errorDialogs()
        }
    }
}