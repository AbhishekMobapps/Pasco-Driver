package com.pasco.pascocustomer.Driver.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel.CancelOnClick
import com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel.CancelReasonResponse
import com.pasco.pascocustomer.R

class CancellationReasonAdapter(
    private val context: Context,
    private val onItemClick: CancelOnClick,
    private val cancelList: List<CancelReasonResponse.CancellationList>
) : RecyclerView.Adapter<CancellationReasonAdapter.StatusViewHolder>() {

    inner class StatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var driverStatusTextView: TextView = itemView.findViewById(R.id.driverStatusTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false)
        return StatusViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        val status = cancelList[position]
        holder.driverStatusTextView.text = status.reason.toString()
        val id = status.reasonid
        holder.driverStatusTextView.setOnClickListener {
           onItemClick.cancelList(position,id!!.toInt())
            notifyDataSetChanged()  // Optional: only call this if the dataset has changed
        }
    }

    override fun getItemCount(): Int {
        return cancelList.size
    }
}
