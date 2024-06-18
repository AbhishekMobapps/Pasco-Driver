package com.pasco.pascocustomer.Driver.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pasco.pascocustomer.Driver.emergencyhelp.ViewModel.EmergencyHelpDriverResponse
import com.pasco.pascocustomer.R

class DriversListAdapter(
    private val context: Context,
    private val emergencyDriNumbers: List<EmergencyHelpDriverResponse.EmergencyHelpDriverResponseData>
) : RecyclerView.Adapter<DriversListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_send_help_to_driver, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val emegencyDriResponse = emergencyDriNumbers[position]
        holder.driverNameSHelp.text=emegencyDriResponse.user.toString()

        holder.sendReqButton.setOnClickListener {

        }

    }

    override fun getItemCount(): Int {
        return emergencyDriNumbers.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var driverNameSHelp: TextView = itemView.findViewById(R.id.driverNameSHelp)
        var sendReqButton: TextView = itemView.findViewById(R.id.sendReqButton)
    }
}