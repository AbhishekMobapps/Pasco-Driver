package com.pasco.pascocustomer.Driver.adapter
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pasco.pascocustomer.BuildConfig
import com.pasco.pascocustomer.Driver.Fragment.DriverTripHistory.CancelledTripResponse.CancelledData
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.historydetails.CompleteHistoryDetailsActivity
import com.pasco.pascocustomer.invoice.InvoiceActivity
import de.hdodenhof.circleimageview.CircleImageView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


class CancelledTripHistoryAdapter(
    private val context: Context,
    private val cancelTripHistory: List<CancelledData>
) :
    RecyclerView.Adapter<CancelledTripHistoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.complete_or_cancel_history, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CancelledTripHistoryAdapter.ViewHolder, position: Int) {
        val driverTripHis = cancelTripHistory[position]
        val price = "$${driverTripHis.bidPrice}"
        val dBookingStatus = driverTripHis.bookingStatus.toString()
        val dateTime = driverTripHis.createdAt.toString()
        holder.consCancellationReason.visibility = View.VISIBLE
        holder.hisArow.visibility = View.GONE
        holder.cancelReasonTextView.text = driverTripHis.cancelreason.toString()


        holder.bookingId.text = cancelTripHistory[position].bookingNumber
        holder.paymentMode.text = cancelTripHistory[position].paymentMethod


        val formattedTotalDistance =
            "%.1f".format(cancelTripHistory[position].totalDistance ?: 0.0)
        holder.distance.text = "$formattedTotalDistance km"


        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputDateFormat.timeZone = TimeZone.getTimeZone("UTC") // Set input format to UTC
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
        outputDateFormat.timeZone = TimeZone.getTimeZone("Asia/Kolkata") // Set output format to IST




        Log.e("bookingStatusaa", "bookingStatus.. " + driverTripHis.bookingStatus.toString())

        val url = driverTripHis.userImage
        Log.e("driverImageAA", "driverImage...$url")
        Glide.with(context).load(BuildConfig.IMAGE_KEY + url)
            .placeholder(R.drawable.man).into(holder.driverProfile)

        try {
            val parsedDate = inputDateFormat.parse(dateTime)
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
                status.setTextColor(Color.parseColor("#FFFFFFFF"))
                statusConst.setBackgroundResource(R.drawable.cancel_back)
            } else {
                statusConst.setBackgroundResource(R.drawable.complete_back)

                holder.itemView.setOnClickListener {
                    val id = cancelTripHistory[position].id
                    val bookingNumber = cancelTripHistory[position].bookingNumber
                    val userNamee = cancelTripHistory[position].user
                    val completedDate = cancelTripHistory[position].pickupDatetime
                    val pick = cancelTripHistory[position].pickupLocation
                    val drop = cancelTripHistory[position].dropLocation
                    val bookingStatus = cancelTripHistory[position].bookingStatus
                    val paymentMethod = cancelTripHistory[position].paymentMethod
                    val distance = cancelTripHistory[position].totalDistance
                    val feedback = cancelTripHistory[position].feedback
                    val totalAmount = cancelTripHistory[position].bidPrice
                    val commissionPrice = cancelTripHistory[position].commisionPrice
                    val userImagee = cancelTripHistory[position].userImage
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
                    intent.putExtra("paymentStatus", cancelTripHistory[position].paymentStatus)
                    context.startActivity(intent)
                }

            }
        }


    }


    override fun getItemCount(): Int {
        return cancelTripHistory.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val status = itemView.findViewById<TextView>(R.id.status)
        val driverName = itemView.findViewById<TextView>(R.id.driverName)
        val bookingId = itemView.findViewById<TextView>(R.id.bookingId)
        val distance = itemView.findViewById<TextView>(R.id.distance)
        val paymentMode = itemView.findViewById<TextView>(R.id.paymentMode)
        val totalAmount = itemView.findViewById<TextView>(R.id.totalAmount)
        val bookingDate = itemView.findViewById<TextView>(R.id.bookingDate)
        val cancelReasonTextView = itemView.findViewById<TextView>(R.id.cancelReasonTextView)
        val driverProfile = itemView.findViewById<CircleImageView>(R.id.driverProfile)
        val hisArow = itemView.findViewById<ImageView>(R.id.hisArrow)
        val statusConst = itemView.findViewById<ConstraintLayout>(R.id.statusConst)
        val consCancellationReason = itemView.findViewById<ConstraintLayout>(R.id.consCancellationReason)
    }
}