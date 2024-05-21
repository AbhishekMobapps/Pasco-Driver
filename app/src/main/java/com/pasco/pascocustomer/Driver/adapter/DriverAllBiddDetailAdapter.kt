package com.pasco.pascocustomer.activity.Driver.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pasco.pascocustomer.Driver.Fragment.DriverAllBiddsDetail.ViewModel.GetDriverBidDetailsDataResponse
import com.pasco.pascocustomer.Driver.StartRiding.Ui.DriverStartRidingActivity
import de.hdodenhof.circleimageview.CircleImageView
import com.pasco.pascocustomer.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class DriverAllBiddDetailAdapter(
    private val context: Context,
    private val getDriverData: List<GetDriverBidDetailsDataResponse.DriverAllBidData>
) : RecyclerView.Adapter<DriverAllBiddDetailAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val driverProfile: CircleImageView = itemView.findViewById(R.id.driverProfile)
        val pickUpDetailsORD: TextView = itemView.findViewById(R.id.pickUpDetailsORD)
        val DropDetailsORD: TextView = itemView.findViewById(R.id.DropDetailsORD)
        val distanceDORD: TextView = itemView.findViewById(R.id.distanceDORD)
        val totalPricestaticDORD: TextView = itemView.findViewById(R.id.totalPricestaticDORD)
        val maxPriceDORD: TextView = itemView.findViewById(R.id.maxPriceDORD)
        val orderIdDynamicDORD: TextView = itemView.findViewById(R.id.orderIdDynamicDORD)
        val clientNameOrdR: TextView = itemView.findViewById(R.id.clientNameOrdR)
        val orderDetailDR: TextView = itemView.findViewById(R.id.orderDetailDR)
        val acceptDORD: TextView = itemView.findViewById(R.id.acceptDORD)
        val consAccepORD: ConstraintLayout = itemView.findViewById(R.id.consAccepORD)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recycler_order_details, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return getDriverData.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bookingReq = getDriverData[position]
        val price = "$${bookingReq.basicprice}"
        val bPrice = "$${bookingReq.bidPrice}"
        val dateTime = bookingReq.pickupDatetime.toString()
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US)

        val baseUrl = "http://69.49.235.253:8090"
        val imagePath = bookingReq?.userImage.orEmpty()

        val imageUrl = "$baseUrl$imagePath"
        Glide.with(context)
            .load(imageUrl)
            .into(holder.driverProfile)

        try {
            val parsedDate = inputDateFormat.parse(dateTime)
            outputDateFormat.timeZone = TimeZone.getDefault() // Set to local time zone
            val formattedDateTime = outputDateFormat.format(parsedDate)
            holder.orderDetailDR.text = formattedDateTime
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val status = bookingReq.customerStatus.toString()
        if (status.equals("confirmed"))
        {
            holder.consAccepORD.visibility = View.VISIBLE
        }
        else
        {
            holder.consAccepORD.visibility = View.GONE
        }
        with(holder) {
            val pickupCity = bookingReq.pickupLocation.toString()
            val dropCity = bookingReq.dropLocation.toString()
            pickUpDetailsORD.text = pickupCity
            DropDetailsORD.text = dropCity
            val formattedDistance = String.format("%.1f", bookingReq.totalDistance)
            distanceDORD.text = "$formattedDistance km"

            totalPricestaticDORD.text = price
            maxPriceDORD.text = bPrice
            clientNameOrdR.text = bookingReq.user
            orderIdDynamicDORD.text = truncateBookingNumber(bookingReq.bookingNumber.toString())

            orderIdDynamicDORD.setOnClickListener {
                showFullAddressDialog(bookingReq.bookingNumber.toString())
            }
            holder.acceptDORD.setOnClickListener {
                val intent = Intent(context, DriverStartRidingActivity::class.java).apply {
                    putExtra("pickupLoc", bookingReq.pickupLocation.toString())
                    putExtra("dropLoc", bookingReq.dropLocation.toString())
                    putExtra("latitudePickUp", bookingReq.pickupLatitude.toString())  // corrected to pickupLatitude
                    putExtra("longitudePickUp", bookingReq.pickupLongitude.toString())
                    putExtra("latitudeDrop", bookingReq.dropLatitude.toString())
                    putExtra("longitudeDrop", bookingReq.dropLongitude.toString())
                    putExtra("deltime", "${bookingReq.duration.toString()} min")
                    putExtra("image", imageUrl)
                    putExtra("BookId", bookingReq.id.toString())
                }
                context.startActivity(intent)
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

}
