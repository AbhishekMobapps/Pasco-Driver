package com.pasco.pascocustomer.activity.Driver.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.Driver.Fragment.DriverAllBiddsDetail.Ui.DriverAllBiddsActivity
import com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel.CancelOnClick
import com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel.DAllOrderResponse
import com.pasco.pascocustomer.Driver.StartRiding.Ui.DriverStartRidingActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class DriverAllBiddsAdapter(
    private val context: Context,
    private val onItemClick:CancelOnClick,
    private val driverHistory: List<DAllOrderResponse.DAllOrderResponseData>
) : RecyclerView.Adapter<DriverAllBiddsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DriverAllBiddsAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_all_bids_driver, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DriverAllBiddsAdapter.ViewHolder, position: Int) {
        val driverOrderHis = driverHistory[position]
        val price = "$${driverOrderHis.bidPrice}"
        val dateTime = driverOrderHis.pickupDatetime.toString()
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US)

        try {
            val parsedDate = inputDateFormat.parse(dateTime)
            outputDateFormat.timeZone = TimeZone.getDefault() // Set to local time zone
            val formattedDateTime = outputDateFormat.format(parsedDate!!)
            holder.orderDateTimedO.text = formattedDateTime
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val baseUrl = "http://69.49.235.253:8090"
        val imagePath = driverOrderHis.userImage.orEmpty()
        val imageUrl = "$baseUrl$imagePath"
        val biddingStatus = driverOrderHis.customerStatus.toString()

        holder.biddingStatusTextView.apply {
            background = when (biddingStatus) {
                "Confirmed" -> ContextCompat.getDrawable(context, R.drawable.confirm_button_background)
                "Completed" -> ContextCompat.getDrawable(context, R.drawable.accept_btn_color)
                else -> ContextCompat.getDrawable(context, R.drawable.cancel_button_color)
            }
            setTextColor(Color.parseColor("#FFFFFFFF"))
            text = biddingStatus
        }

        holder.linearDriverHis.setOnClickListener {
            val intent = when (biddingStatus) {
                "Confirmed" -> Intent(context, DriverStartRidingActivity::class.java).apply {
                    putExtra("pickupLoc", driverOrderHis.pickupLocation.toString())
                    putExtra("dropLoc", driverOrderHis.dropLocation.toString())
                    putExtra("latitudePickUp", driverOrderHis.pickupLatitude.toString())
                    putExtra("longitudePickUp", driverOrderHis.pickupLongitude.toString())
                    putExtra("latitudeDrop", driverOrderHis.dropLatitude.toString())
                    putExtra("longitudeDrop", driverOrderHis.dropLongitude.toString())
                    putExtra("deltime", "${driverOrderHis.duration} min")
                    putExtra("image", imageUrl)
                    putExtra("BookId", driverOrderHis.id.toString())
                }
                else -> Intent(context, DriverAllBiddsActivity::class.java).apply {
                    putExtra("id", driverOrderHis.id.toString())
                }
            }
            context.startActivity(intent)
        }

        if (biddingStatus.equals("Pending"))
        {
            holder.cancelButtonBid.visibility = View.VISIBLE
            holder.cancelButtonBid.setOnClickListener {
                onItemClick.cancelOrder(
                    position,driverOrderHis.id!!.toInt())
                notifyDataSetChanged()
            }
        }

        with(holder) {
            userNameDriO.text = driverOrderHis.user.toString()
            orderIDDriOrd.text = driverOrderHis.bookingNumber.toString()
            orderPriceTDriO.text = price
        }
    }

    override fun getItemCount(): Int {
        return driverHistory.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameDriO: TextView = itemView.findViewById(R.id.userNameDriO)
        val orderIDDriOrd: TextView = itemView.findViewById(R.id.orderIDDriOrd)
        val orderPriceTDriO: TextView = itemView.findViewById(R.id.orderPriceTDriO)
        val orderDateTimedO: TextView = itemView.findViewById(R.id.orderDateTimedO)
        val biddingStatusTextView: TextView = itemView.findViewById(R.id.biddingStatusTextView)
        val linearDriverHis: LinearLayout = itemView.findViewById(R.id.linearDriverHis)
        val cancelButtonBid: TextView = itemView.findViewById(R.id.cancelButtonBid)
    }
}
