package com.pasco.pascocustomer.loyalty.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.customer.activity.allbiddsdetailsactivity.model.AllBiddsDetailResponse
import com.pasco.pascocustomer.customer.activity.track.TrackActivity
import com.pasco.pascocustomer.loyalty.model.LoyaltyProgramResponse
import com.pasco.pascocustomer.userFragment.order.acceptedadapter.AcceptedAdapter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class LoyaltyProgramAdapter(
    private val required: Context,
    private var orderList: List<LoyaltyProgramResponse.Datum>
) :
    RecyclerView.Adapter<LoyaltyProgramAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val programNameTxt: TextView = itemView.findViewById(R.id.programNameTxt)
        val programCodeTxt: TextView = itemView.findViewById(R.id.programCodeTxt)
        val programPercentTxt: TextView = itemView.findViewById(R.id.programPercentTxt)
        val programLimitTxt: TextView = itemView.findViewById(R.id.programLimitTxt)
        val loyaltyEndTxt: TextView = itemView.findViewById(R.id.loyaltyEndTxt)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.loyalty_program, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.programNameTxt.text = orderList[position].programName
        holder.programCodeTxt.text = orderList[position].programcode
        holder.programPercentTxt.text = orderList[position].loyaltypercent.toString()
        holder.programLimitTxt.text = orderList[position].programlimit.toString()

        val dateTime = orderList[position].loyaltyenddate
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US)

        try {
            val parsedDate = inputDateFormat.parse(dateTime)
            outputDateFormat.timeZone = TimeZone.getDefault() // Set to local time zone
            val formattedDateTime = outputDateFormat.format(parsedDate)

            holder.loyaltyEndTxt.text = formattedDateTime
        } catch (e: ParseException) {
            e.printStackTrace()
        }


    }

    override fun getItemCount(): Int {
        return orderList.size
    }

}