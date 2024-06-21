package com.pasco.pascocustomer.userFragment.order.adapter

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.userFragment.order.odermodel.OrderResponse
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter(
    private val required: Context,
    private var orderList: List<OrderResponse.Datum>
) :
    RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName)
        val oderIdTxt: TextView = itemView.findViewById(R.id.oderIdTxt)
        val dateTime: TextView = itemView.findViewById(R.id.dateTime)
        val totalPriceTxt: TextView = itemView.findViewById(R.id.totalPriceTxt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.oder_recycler_layhout, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // holder.userName.text = orderList[position].user
        holder.userName.text = orderList[position].user


        holder.oderIdTxt.text = truncateBookingNumber(orderList[position].bookingNumber.toString())
        holder.itemView.setOnClickListener {
            showFullAddressDialog(
                orderList[position].user.toString(),
                orderList[position].bookingNumber.toString(),
                orderList[position].pickupLocation.toString(),
                orderList[position].dropLocation.toString(),
                orderList[position].totalDistance.toString(),
                orderList[position].pickupDatetime.toString(),
                orderList[position].basicprice.toString()
            )
        }

        val price = orderList[position].basicprice
        holder.totalPriceTxt.text = "$ $price"

        val dateTime = orderList[position].pickupDatetime
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US)

        try {
            val parsedDate = inputDateFormat.parse(dateTime)
            outputDateFormat.timeZone = TimeZone.getDefault() // Set to local time zone
            val formattedDateTime = outputDateFormat.format(parsedDate)

            holder.dateTime.text = formattedDateTime
        } catch (e: ParseException) {
            e.printStackTrace()
        }


    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    private fun truncateBookingNumber(bookingNumber: String, maxLength: Int = 8): String {
        return if (bookingNumber.length > maxLength) {
            "${bookingNumber.substring(0, maxLength)}..."
        } else {
            bookingNumber
        }
    }
    /*   fun showFullAddressDialog(fullBookingNumber: String) {
           val alertDialogBuilder = AlertDialog.Builder(required)
           alertDialogBuilder.setTitle("Order ID")
           alertDialogBuilder.setMessage(fullBookingNumber)
           alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
               dialog.dismiss()
           }
           val alertDialog = alertDialogBuilder.create()
           alertDialog.show()
       }*/

    private fun showFullAddressDialog(
        name: String,
        bookingNumber: String,
        pickupLocation: String,
        dropLocations: String,
        totalDistance: String,
        pickupDatetime: String,
        basicprice: String

    ) {
        val dialog = Dialog(required)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.show_details)


        val oderIdTxt = dialog.findViewById<TextView>(R.id.oderIdTxt)
        val pickUpLocation = dialog.findViewById<TextView>(R.id.pickUpLocation)
        val dropLocation = dialog.findViewById<TextView>(R.id.dropLocation)
        val distanceTxt = dialog.findViewById<TextView>(R.id.distanceTxt)
        val dateTxt = dialog.findViewById<TextView>(R.id.dateTxt)
        val totalPriceTxt = dialog.findViewById<TextView>(R.id.totalPriceTxt)
        val userName = dialog.findViewById<TextView>(R.id.userName)

        oderIdTxt.text = bookingNumber
        pickUpLocation.text = pickupLocation
        dropLocation.text = dropLocations
        totalPriceTxt.text = "$ $basicprice"
        userName.text = name

        val distanceValue = totalDistance.toDoubleOrNull() ?: 0.0 // Convert distance to Double
        val formattedDistance = String.format("%.2f", distanceValue)
        distanceTxt.text = "$formattedDistance Km"

        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US)

        try {
            val parsedDate = inputDateFormat.parse(pickupDatetime)
            outputDateFormat.timeZone = TimeZone.getDefault() // Set to local time zone
            val formattedDateTime = outputDateFormat.format(parsedDate)

            dateTxt.text = formattedDateTime
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val window = dialog.window
        val lp = window?.attributes
        if (lp != null) {
            lp.width = ActionBar.LayoutParams.MATCH_PARENT
        }
        if (lp != null) {
            lp.height = ActionBar.LayoutParams.WRAP_CONTENT
        }
        if (window != null) {
            window.attributes = lp
        }


        dialog.show()
    }
}