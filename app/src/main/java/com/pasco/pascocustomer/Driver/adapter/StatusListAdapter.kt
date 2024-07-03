package com.pasco.pascocustomer.Driver.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel.CancelReasonResponse
import com.pasco.pascocustomer.Driver.Fragment.DriverTripHistory.AddFeedbackOnClickListner
import com.pasco.pascocustomer.Driver.StartRiding.ViewModel.DriverStatusClickListner
import com.pasco.pascocustomer.Driver.StartRiding.ViewModel.GetRouteUpdateResponse
import com.pasco.pascocustomer.R

class StatusListAdapter(
    private val context: Context,
    private val onItemClick: DriverStatusClickListner,
    private val statusList: List<GetRouteUpdateResponse.RouteResponseData>
) : RecyclerView.Adapter<StatusListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var driverStatusTextView: TextView = itemView.findViewById(R.id.driverStatusTextView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val status = statusList[position]
        holder.driverStatusTextView.text = status.status.toString()
        holder.driverStatusTextView.setOnClickListener {
            onItemClick.driverStatusUpdate(
                position,
                status.id!!,status.status.toString())
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return statusList.size
    }
}