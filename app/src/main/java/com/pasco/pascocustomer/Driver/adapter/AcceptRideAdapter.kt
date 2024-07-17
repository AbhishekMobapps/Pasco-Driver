package com.pasco.pascocustomer.Driver.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pasco.pascocustomer.Driver.AcceptRideDetails.Ui.AcceptRideActivity
import com.pasco.pascocustomer.Driver.Fragment.HomeFrag.ViewModel.ShowBookingReqResponse
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.application.PascoApp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class AcceptRideAdapter(
    private val context: Context,
    private val bookingReqData: List<ShowBookingReqResponse.ShowBookingReqData>,
    private var language: String
) : RecyclerView.Adapter<AcceptRideAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val startPointTextViewReq: TextView = itemView.findViewById(R.id.startPoint_textviewReq)
        val endPointTextViewReq: TextView = itemView.findViewById(R.id.endPointTextViewReq)
        val priceDynamicTextViewReq: TextView = itemView.findViewById(R.id.priceDynamic_textviewReq)
        val orderIdDynamicReq: TextView = itemView.findViewById(R.id.orderIdDymanicReq)
        val userNameBD: TextView = itemView.findViewById(R.id.userNameBD)
        val biddReqDateTime: TextView = itemView.findViewById(R.id.biddReqDateTime)
        val imgUserOrderD: ImageView = itemView.findViewById(R.id.imgUserOrderD)
        val bidsStatus: TextView = itemView.findViewById(R.id.bidsStatus)
        val constraintNC: ConstraintLayout = itemView.findViewById(R.id.constraintNC)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recycler_accept_ride_request, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bookingReqData.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bookingReq = bookingReqData[position]
        val price = "$${bookingReq.basicprice}"
        val dateTime = bookingReq.pickupDatetime.toString()
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US)

        try {
            val parsedDate = inputDateFormat.parse(dateTime)
            outputDateFormat.timeZone = TimeZone.getDefault() // Set to local time zone
            val formattedDateTime = outputDateFormat.format(parsedDate)
            holder.biddReqDateTime.text = formattedDateTime
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val baseUrl = "http://69.49.235.253:8090"
        val imagePath = bookingReq?.userImage.orEmpty()
        val bStatus = bookingReq.bidStatus
        if (!bStatus!!) {
            holder.bidsStatus.visibility = View.GONE
        } else {
            holder.bidsStatus.visibility = View.VISIBLE
            holder.bidsStatus.text = context.getString(R.string.A_bid_has_already_been_placed)
        }

        val imageUrl = "$baseUrl$imagePath"
        Glide.with(context)
            .load(imageUrl)
            .into(holder.imgUserOrderD)
        with(holder) {
            val pickupCity = extractCityName(bookingReq.pickupLocation.toString())
            val dropCity = extractCityName(bookingReq.dropLocation.toString())
            startPointTextViewReq.text = pickupCity
            endPointTextViewReq.text = dropCity
            priceDynamicTextViewReq.text = price
            userNameBD.text = bookingReq.user
            orderIdDynamicReq.text = truncateBookingNumber(bookingReq.bookingNumber.toString())

            if (!bStatus) {
                holder.bidsStatus.visibility = View.GONE
                itemView.setOnClickListener {
                    val id = bookingReq.id.toString()
                    val bookingId = bookingReq.bookingNumber.toString()
                    PascoApp.encryptedPrefs.requestOrderId = id
                    val intent = Intent(context, AcceptRideActivity::class.java)
                    intent.putExtra("rideReqId", id)
                    intent.putExtra("bookingNumb", bookingId)
                    intent.putExtra("pickuplatitudea", bookingReq.pickupLatitude.toString())
                    intent.putExtra("pickuplongitudea", bookingReq.pickupLongitude.toString())
                    intent.putExtra("droplatitudea", bookingReq.dropLatitude.toString())
                    intent.putExtra("droplongitudea", bookingReq.dropLongitude.toString())
                    intent.putExtra("one", 1)
                    context.startActivity(intent)
                }
            } else {
                itemView.setOnClickListener {
                    Toast.makeText(
                        context,
                        "You cannot place another bid. If you can, please cancel the current bids before placing new ones",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }

            // Set click listener for the accept button

            orderIdDynamicReq.setOnClickListener {
                showFullAddressDialog(bookingReq.bookingNumber.toString())
            }
        }
    }

    fun truncateBookingNumber(bookingNumber: String, maxLength: Int = 8): String {
        return if (bookingNumber.length > maxLength) {
            "${bookingNumber.substring(0, maxLength)}..."
        } else {
            bookingNumber
        }
    }

    fun showFullAddressDialog(fullBookingNumber: String) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Order ID")
        alertDialogBuilder.setMessage(fullBookingNumber)
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun extractCityName(location: String): String {
        return location.split(",")[0].trim() // Extracting the city part before comma
    }


}