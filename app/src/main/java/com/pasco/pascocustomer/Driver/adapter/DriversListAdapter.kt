package com.pasco.pascocustomer.Driver.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.pasco.pascocustomer.Driver.emergencyhelp.ViewModel.EmergencyHelpDriverResponse
import com.pasco.pascocustomer.Driver.emergencyhelp.ViewModel.SendHelpClickListner
import com.pasco.pascocustomer.R

class DriversListAdapter(
    private val context: Context,
    private val onItemClick: SendHelpClickListner,
    private val emergencyDriNumbers: List<EmergencyHelpDriverResponse.EmergencyHelpDriverResponseData>,
    private val bookingId: String
) : RecyclerView.Adapter<DriversListAdapter.ViewHolder>() {
    var isChecked: Boolean = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.send_help_to_driver, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val emegencyDriResponse = emergencyDriNumbers[position]
        holder.driverNameSHelp.text=emegencyDriResponse.user.toString()
        val id = emergencyDriNumbers[position].driverid

        holder.checkBoxForHelp.isChecked = isChecked

        holder.checkBoxForHelp.setOnCheckedChangeListener { _, isChecked ->
            if (bookingId == null) {
                Toast.makeText(context, "You cannot request assistance until you start a trip.", Toast.LENGTH_SHORT).show()
            } else {
                if (isChecked) {
                    onItemClick.sendHelp(position, id!!)
                    notifyDataSetChanged()
                }
            }
            // Update isChecked state
            this.isChecked = isChecked
        }
    }
    override fun getItemCount(): Int {
        return emergencyDriNumbers.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var driverNameSHelp: TextView = itemView.findViewById(R.id.driverNameSHelp)
        var checkBoxForHelp: CheckBox = itemView.findViewById(R.id.checkBoxForHelp)

    }
}