package com.pasco.pascocustomer.Driver.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pasco.pascocustomer.BuildConfig
import com.pasco.pascocustomer.Driver.Fragment.DriverTripHistory.CancelledTripResponse
import com.pasco.pascocustomer.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class CancelledTripHistoryAdapter (private val context: Context, private val cancelTripHistory:List<CancelledTripResponse.CancelledData>)  :
    RecyclerView.Adapter<CancelledTripHistoryAdapter.ViewHolder>() {
//hello
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CancelledTripHistoryAdapter.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recycler_cancelled_trip, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CancelledTripHistoryAdapter.ViewHolder, position: Int) {
        val cancelTripHis = cancelTripHistory[position]
        val price = "$${cancelTripHis.bidPrice}"
        val dBookingStatus = cancelTripHis.bookingStatus.toString()
        val dateTime = cancelTripHis.availabilityDatetime.toString()
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US)

        val url = cancelTripHis.userImage
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
            clientNameDriHis.text = cancelTripHis.user.toString()
            totalCostDriverHis.text = price
            val durationInMinutes = cancelTripHis.duration
            val hours = durationInMinutes!! / 60
            val minutes = durationInMinutes % 60
            val durationString = "$hours hours $minutes min"
            arrivalTimeDriverHis.text = durationString

            pickUpDetailsDriHis.text = cancelTripHis.pickupLocation.toString()
            DropDetailsDriHis.text = cancelTripHis.dropLocation.toString()
            bookingstatus.text = "Cancelled"
            cancelReasonTextView.text = cancelTripHis.cancelreason.toString()
        }

    }

    override fun getItemCount(): Int {
        return cancelTripHistory.size
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
        val cancelReasonTextView = itemView.findViewById<TextView>(R.id.cancelReasonTextView)


    }
}