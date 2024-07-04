package com.pasco.pascocustomer.Driver.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pasco.pascocustomer.BuildConfig
import com.pasco.pascocustomer.Driver.Fragment.DriverTripHistory.AddFeedbackOnClickListner
import com.pasco.pascocustomer.Driver.Fragment.DriverTripHistory.CompletedTripHistoryResponse
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.historydetails.CompleteHistoryDetailsActivity
import com.pasco.pascocustomer.invoice.InvoiceActivity
import de.hdodenhof.circleimageview.CircleImageView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class CompletedTripHistoryAdapter(
    private val context: Context,
    private val driverTripHistory: List<CompletedTripHistoryResponse.DriverTripHistoryData>
) :
    RecyclerView.Adapter<CompletedTripHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CompletedTripHistoryAdapter.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.complete_or_cancel_history, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CompletedTripHistoryAdapter.ViewHolder, position: Int) {
        val driverTripHis = driverTripHistory[position]
        val price = "$${driverTripHis.bidPrice}"
        val dBookingStatus = driverTripHis.bookingStatus.toString()
        val dateTime = driverTripHis.availabilityDatetime.toString()

        holder.bookingId.text = driverTripHistory[position].bookingNumber
        holder.paymentMode.text = driverTripHistory[position].paymentMethod



        val formattedTotalDistance =
            "%.1f".format(driverTripHistory[position].totalDistance ?: 0.0)
        holder.distance.text = "$formattedTotalDistance km"


        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US)

        Log.e("bookingStatusaa", "bookingStatus.. " + driverTripHis.bookingStatus.toString())

        val url = driverTripHis.userImage
        Log.e("driverImageAA", "driverImage...$url")
        Glide.with(context).load(BuildConfig.IMAGE_KEY + url)
            .placeholder(R.drawable.man).into(holder.driverProfile)

        try {
            val parsedDate = inputDateFormat.parse(dateTime)
            outputDateFormat.timeZone = TimeZone.getDefault() // Set to local time zone
            val formattedDateTime = outputDateFormat.format(parsedDate)
            holder.bookingDate.text = formattedDateTime
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        with(holder) {
            driverName.text = driverTripHis.user.toString()
            totalAmount.text = price
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


            status.text = dBookingStatus
            if (dBookingStatus == "Cancelled") {
                status.setTextColor(Color.parseColor("#BC2A0A"))
                statusConst.setBackgroundResource(R.drawable.cancel_back)
            } else {
                statusConst.setBackgroundResource(R.drawable.complete_back)

                holder.itemView.setOnClickListener {
                    val id = driverTripHistory[position].id
                    val bookingNumber = driverTripHistory[position].bookingNumber
                    val userNamee = driverTripHistory[position].user
                    val completedDate = driverTripHistory[position].pickupDatetime
                    val pick = driverTripHistory[position].pickupLocation
                    val drop = driverTripHistory[position].dropLocation
                    val bookingStatus = driverTripHistory[position].bookingStatus
                    val paymentMethod = driverTripHistory[position].paymentMethod
                    val distance = driverTripHistory[position].totalDistance
                    val feedback = driverTripHistory[position].feedback
                    val totalAmount = driverTripHistory[position].bidPrice
                    val commissionPrice = driverTripHistory[position].commisionPrice
                    val userImagee = driverTripHistory[position].userImage
                    Log.e("CompleteHistoryA", "feedback...$feedback  $totalAmount")
                    val intent = Intent(context, CompleteHistoryDetailsActivity::class.java)

                    intent.putExtra("id", id.toString())
                    intent.putExtra("bookingNumber", bookingNumber)
                    intent.putExtra("userNamee", userNamee)
                    intent.putExtra("completedDate", completedDate)
                    intent.putExtra("pick", pick)
                    intent.putExtra("drop", drop)
                    intent.putExtra("bookingStatus", bookingStatus)
                    intent.putExtra("paymentMethod", paymentMethod)
                    intent.putExtra("distance", distance.toString())
                    intent.putExtra("feedback", feedback.toString())
                    intent.putExtra("totalAmount", totalAmount.toString())
                    intent.putExtra("commissionPrice", commissionPrice.toString())
                    intent.putExtra("userImagee", url)
                    intent.putExtra("paymentStatus", driverTripHistory[position].paymentStatus)
                    context.startActivity(intent)
                }

            }
        }



    }

    override fun getItemCount(): Int {
        return driverTripHistory.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val status = itemView.findViewById<TextView>(R.id.status)
        val driverName = itemView.findViewById<TextView>(R.id.driverName)
        val bookingId = itemView.findViewById<TextView>(R.id.bookingId)
        val distance = itemView.findViewById<TextView>(R.id.distance)
        val paymentMode = itemView.findViewById<TextView>(R.id.paymentMode)
        val totalAmount = itemView.findViewById<TextView>(R.id.totalAmount)
        val bookingDate = itemView.findViewById<TextView>(R.id.bookingDate)
        val driverProfile = itemView.findViewById<CircleImageView>(R.id.driverProfile)
        val statusConst = itemView.findViewById<ConstraintLayout>(R.id.statusConst)


    }
}