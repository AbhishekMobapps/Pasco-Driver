package com.pasco.pascocustomer.Driver.adapter

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
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
        holder.driverNameSHelp.text = emegencyDriResponse.user.toString()
        val id = emergencyDriNumbers[position].driverid
        holder.sendHelptextView.background = ContextCompat.getDrawable(context, R.drawable.button_background)

        holder.sendHelptextView.setOnClickListener {
            holder.sendHelptextView.background = ContextCompat.getDrawable(context, R.drawable.accept_btn_color)
            if (bookingId == null) {
                Toast.makeText(
                    context,
                    context.getString(R.string.assistance_until_you_start_trip),
                    Toast.LENGTH_SHORT
                ).show()
            }
                else
                {

                    val builder = AlertDialog.Builder(context, R.style.Style_Dialog_Rounded_Corner)
                    val dialogView = LayoutInflater.from(context).inflate(R.layout.emergency_help_popup, null)
                    builder.setView(dialogView)

                    val dialog = builder.create()
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    val crossCancelImageView = dialogView.findViewById<ImageView>(R.id.crossCancelImageView)
                    val yesLogoutBtn = dialogView.findViewById<TextView>(R.id.addReasonTextView)
                    val addEmergencyReason = dialogView.findViewById<EditText>(R.id.addEmergencyReason)
                    var anAccident: ConstraintLayout = dialogView.findViewById(R.id.consMyWalletVehDetailsPop)
                    var chestPain: ConstraintLayout = dialogView.findViewById(R.id.consContactAndSupportInsidePop)
                    var breathlessness: ConstraintLayout = dialogView.findViewById(R.id.consTermsCondInsidePop)
                    var ubconciousness: ConstraintLayout = dialogView.findViewById(R.id.consPrivacyPolicyInsidePop)

                    anAccident.setOnClickListener {
                        addEmergencyReason.setText(context.getString(R.string.accident))
                    }

                    chestPain.setOnClickListener {
                        addEmergencyReason.setText(context.getString(R.string.Chest_Pain))
                    }

                    breathlessness.setOnClickListener {
                        addEmergencyReason.setText(context.getString(R.string.Breathlessness))
                    }

                    ubconciousness.setOnClickListener {
                        addEmergencyReason.setText(context.getString(R.string.Unconsciousness))
                    }

                    crossCancelImageView.setOnClickListener {
                        dialog.dismiss()
                    }
                    yesLogoutBtn.setOnClickListener {
                        val comment = addEmergencyReason?.text.toString()
                        onItemClick.sendHelp(position, id!!,comment)
                        notifyDataSetChanged()
                        dialog.dismiss()
                    }
                    dialog.show()
                }
        }

     /*   holder.sendHelptextView.setOnCheckedChangeListener { buttonView, isChecked ->
            holder.checkBoxForHelp.isChecked = isChecked

            }
            if (isChecked) {

            } else {

            }*/
        }

    override fun getItemCount(): Int {
        return emergencyDriNumbers.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var driverNameSHelp: TextView = itemView.findViewById(R.id.driverNameSHelp)
        var sendHelptextView: TextView = itemView.findViewById(R.id.sendHelptextView)
        var checkBoxForHelp: CheckBox = itemView.findViewById(R.id.checkBoxForHelp)

    }
}