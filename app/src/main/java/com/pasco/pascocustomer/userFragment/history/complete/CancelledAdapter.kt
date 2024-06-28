package com.pasco.pascocustomer.userFragment.history.complete

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pasco.pascocustomer.BuildConfig
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.customerfeedback.CustomerFeedbackBody
import com.pasco.pascocustomer.customerfeedback.CustomerFeedbackModelView
import com.pasco.pascocustomer.dashboard.UserDashboardActivity
import com.pasco.pascocustomer.invoice.InvoiceActivity
import com.pasco.pascocustomer.utils.ErrorUtil
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CancelledAdapter(
    private val context: Context,
    private val driverTripHistory: List<CompleteHistoryResponse.Datum>,
    private val activity: AppCompatActivity
) :
    RecyclerView.Adapter<CancelledAdapter.ViewHolder>() {
    var bottomSheetDialog: BottomSheetDialog? = null
    private val feedbackModelView: CustomerFeedbackModelView by lazy {
        ViewModelProvider(activity)[CustomerFeedbackModelView::class.java]
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CancelledAdapter.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.complete_history_layout, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CancelledAdapter.ViewHolder, position: Int) {
        val driverTripHis = driverTripHistory[position]
        val price = "$${driverTripHis.bidPrice}"
        val dBookingStatus = driverTripHis.bookingStatus.toString()
        val dateTime = driverTripHis.availabilityDatetime.toString()
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US)

        Log.e("bookingStatusaa", "bookingStatus.. " + driverTripHis.bookingStatus.toString())

        if (driverTripHis.bookingStatus.toString() == "Completed") {
            holder.invoice.visibility = View.VISIBLE
            holder.feedbackBtn.visibility = View.VISIBLE
        } else {
            holder.invoice.visibility = View.GONE
            holder.feedbackBtn.visibility = View.GONE
        }
        val url = driverTripHis.userImage
        Glide.with(context).load(BuildConfig.IMAGE_KEY + url)
            .placeholder(R.drawable.home_bg).into(holder.driverProfileCth)

        try {
            val parsedDate = inputDateFormat.parse(dateTime)
            outputDateFormat.timeZone = TimeZone.getDefault() // Set to local time zone
            val formattedDateTime = outputDateFormat.format(parsedDate)
            holder.dateTimeDriHis.text = formattedDateTime
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        with(holder) {
            clientNameDriHis.text = driverTripHis.driver.toString()
            totalCostDriverHis.text = price
            val durationInMinutes = driverTripHis.duration.toString()

            val durationInSeconds = durationInMinutes.toIntOrNull() ?: 0
            val formattedDuration = if (durationInSeconds < 60) {
                "$durationInSeconds sec"
            } else {
                val hours = durationInSeconds / 3600
                val minutes = (durationInSeconds % 3600) / 60
                val seconds = durationInSeconds % 60
                if (hours > 0) {
                    String.format("%d hr %02d min ", hours, minutes)
                } else {
                    String.format("%d min ", minutes)
                }
            }

            arrivalTimeDriverHis.text = formattedDuration

            pickUpDetailsDriHis.text = driverTripHis.pickupLocation.toString()
            DropDetailsDriHis.text = driverTripHis.dropLocation.toString()
            bookingstatus.text = dBookingStatus
            if (dBookingStatus == "Cancelled") {
                bookingstatus.setTextColor(Color.parseColor("#BC2A0A"))
            } else {
                bookingstatus.setTextColor(Color.parseColor("#0ABC3C"))
            }
        }

        holder.invoice.setOnClickListener {
            val id = driverTripHistory[position].id
            val intent = Intent(context, InvoiceActivity::class.java)
            intent.putExtra("id", id.toString())
            context.startActivity(intent)
        }

        holder.feedbackBtn.setOnClickListener {
            val id = driverTripHistory[position].id
            showFeedbackPopup(id)
        }

    }

    override fun getItemCount(): Int {
        return driverTripHistory.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clientNameDriHis = itemView.findViewById<TextView>(R.id.clientNameDriHis)
        val totalCostDriverHis = itemView.findViewById<TextView>(R.id.totalCostDriverHis)
        val arrivalTimeDriverHis = itemView.findViewById<TextView>(R.id.arrivalTimeDriverHis)
        val pickUpDetailsDriHis = itemView.findViewById<TextView>(R.id.pickUpDetailsDriHis)
        val DropDetailsDriHis = itemView.findViewById<TextView>(R.id.DropDetailsDriHis)
        val dateTimeDriHis = itemView.findViewById<TextView>(R.id.dateTimeDriHis)
        val driverProfileCth = itemView.findViewById<ImageView>(R.id.driverProfileCth)
        val bookingstatus = itemView.findViewById<TextView>(R.id.bookingstatus)
        val invoice = itemView.findViewById<TextView>(R.id.invoice)
        val feedbackBtn = itemView.findViewById<TextView>(R.id.feedbackBtn)


    }

    private fun showFeedbackPopup(id: Int?) {
        bottomSheetDialog = BottomSheetDialog(context, R.style.TopCircleDialogStyle)
        val view = LayoutInflater.from(context).inflate(R.layout.feedback_popup, null)
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
            feedbackApi(commentTxt?.text.toString(), ratingBars, id)
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
        //   val codePhone = strPhoneNo
        val loinBody = CustomerFeedbackBody(
            bookingconfirmation = id.toString(), rating = ratingBars, feedback = commentTxt
        )
        feedbackModelView.cancelBooking(loinBody, activity)
    }

    private fun feedbackObserver() {
        feedbackModelView.progressIndicator.observe(activity) {}
        feedbackModelView.mRejectResponse.observe(
            activity
        ) {
            val msg = it.peekContent().msg
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

            val intent = Intent(context, UserDashboardActivity::class.java)
            context.startActivity(intent)
        }
        feedbackModelView.errorResponse.observe(activity) {
            ErrorUtil.handlerGeneralError(context, it)
            // errorDialogs()
        }
    }
}