package com.pasco.pascocustomer.customer.activity.allbiddsdetailsactivity.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pasco.pascocustomer.BuildConfig
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.customer.activity.allbiddsdetailsactivity.model.AllBiddsDetailResponse
import com.pasco.pascocustomer.customer.activity.driverdetails.DriverDetailsActivity
import com.pasco.pascocustomer.customer.activity.notificaion.NotificationClickListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class AllBiddsDetailsAdapter(
    private val required: Context,
    private var orderList: List<AllBiddsDetailResponse.Datum>,
    private val onItemClick: NotificationClickListener,
) :
    RecyclerView.Adapter<AllBiddsDetailsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName)
        val oderIdTxt: TextView = itemView.findViewById(R.id.oderIdTxt)
        val availTimeTxt: TextView = itemView.findViewById(R.id.availTimeTxt)
        val pickUpLocation: TextView = itemView.findViewById(R.id.pickUpLocation)
        val dropLocation: TextView = itemView.findViewById(R.id.dropLocation)
        val distanceTxt: TextView = itemView.findViewById(R.id.distanceTxt)
        val totalPriceTx: TextView = itemView.findViewById(R.id.totalPriceTx)
        val bidPriceTx: TextView = itemView.findViewById(R.id.bidPriceTx)
        val upFrontTxt: TextView = itemView.findViewById(R.id.upFrontTxt)
        val driverProfile: ImageView = itemView.findViewById(R.id.driverProfile)
        val acceptBtn: ConstraintLayout = itemView.findViewById(R.id.acceptBtn)
        val rejectBtn: ConstraintLayout = itemView.findViewById(R.id.rejectBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.bidds_details_accept_reject, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // holder.userName.text = orderList[position].user
        holder.userName.text = orderList[position].driver
        val url = orderList[position].driverImage

        Glide.with(required).load(BuildConfig.IMAGE_KEY + url)
            .placeholder(R.drawable.ic_launcher_background).into(holder.driverProfile)

        holder.oderIdTxt.text = orderList[position].bookingNumber
        holder.pickUpLocation.text = orderList[position].pickupLocation
        holder.dropLocation.text = orderList[position].dropLocation

        val estimatePrice = orderList[position].basicprice
        val bidPrice = orderList[position].bidPrice

        val formattedEstimate = String.format("%.2f", estimatePrice)
        val formattedBid = String.format("%.2f", bidPrice)
        holder.totalPriceTx.text = "$$formattedEstimate"
        holder.bidPriceTx.text = "$$formattedBid"


        val upFrontPrice = orderList[position].upfrontPayment
        holder.upFrontTxt.text = "$$upFrontPrice"

        val distance = orderList[position].totalDistance
        val formattedDistance = String.format("%.2f", distance)
        holder.distanceTxt.text = "$formattedDistance Km"

        val dateTime = orderList[position].pickupDatetime
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US)

        try {
            val parsedDate = inputDateFormat.parse(dateTime)
            outputDateFormat.timeZone = TimeZone.getDefault() // Set to local time zone
            val formattedDateTime = outputDateFormat.format(parsedDate)
            holder.availTimeTxt.text = formattedDateTime


        } catch (e: ParseException) {
            e.printStackTrace()
        }
        Log.e("upFrontPriceAA", "paymentMethod.." +orderList[position].id)

        holder.driverProfile.setOnClickListener {
            val id = orderList[position].id
            val intent = Intent(required, DriverDetailsActivity::class.java)

            intent.putExtra("id", id.toString())
            required.startActivity(intent)
        }
        holder.acceptBtn.setOnClickListener {
            val upFrontPrice = orderList[position].upfrontPayment
            val id = orderList[position].id
            val verificationCode = orderList[position].deliverycode
            val pickupLatitude = orderList[position].pickupLatitude
            val pickupLongitude = orderList[position].pickupLongitude
            val paymentMethod = orderList[position].paymentMethod
            onItemClick.allBids(position, id!!,pickupLatitude,pickupLongitude,verificationCode,upFrontPrice,paymentMethod)
        }


        holder.rejectBtn.setOnClickListener {
            val id = orderList[position].id
            onItemClick.deleteNotification(position,id!!)
        }
    }



    override fun getItemCount(): Int {
        return orderList.size
    }



    fun showFullAddressDialog(fullBookingNumber: String) {
        val alertDialogBuilder = AlertDialog.Builder(required)
        alertDialogBuilder.setTitle(required.getString(R.string.order_id))
        alertDialogBuilder.setMessage(fullBookingNumber)
        alertDialogBuilder.setPositiveButton(required.getString(R.string.ok)) { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}