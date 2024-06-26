package com.pasco.pascocustomer.Driver.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasco.pascocustomer.Driver.StartRiding.ViewModel.GetRouteUpdateResponse
import com.pasco.pascocustomer.R

class StatusListAdapter(
    private val context: Context,
    private val statusList: List<GetRouteUpdateResponse.RouteResponseData>
) : RecyclerView.Adapter<StatusListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in 0 until statusList.size) {
            val position = (statusList[position])
        }
    }

    override fun getItemCount(): Int {
        return statusList.size
    }
}