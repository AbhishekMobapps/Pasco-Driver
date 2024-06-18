package com.pasco.pascocustomer.Driver.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.pasco.pascocustomer.Driver.emergencyhelp.ViewModel.EmergencyCResponse
import com.pasco.pascocustomer.R

class EmergencyAdapter(
    private val context: Context,
    private val emergencyNumbers: List<EmergencyCResponse.EmergencyResponseData>
) : RecyclerView.Adapter<EmergencyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_emergencyno, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val emegencyResponse = emergencyNumbers[position]
        val phoneNo = emegencyResponse.emergencynum.toString()
        holder.PhoneNoDynamicEp.text = phoneNo

        holder.countryNameDynamic.text=emegencyResponse.country.toString()

        holder.itemView.setOnClickListener {
            try {
                if (Build.VERSION.SDK_INT > 22) {
                    if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(context as Activity, arrayOf(android.Manifest.permission.CALL_PHONE), 101)
                        return@setOnClickListener
                    }
                }
                val callIntent = Intent(Intent.ACTION_CALL)
                val phone = phoneNo
                callIntent.data = Uri.parse("tel:$phone")
                context.startActivity(callIntent)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    override fun getItemCount(): Int {
        return emergencyNumbers.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var countryNameDynamic: TextView = itemView.findViewById(R.id.countryNameDynamic)
        var emergencyCallImageView: ImageView = itemView.findViewById(R.id.emergencyCallImageView)
        var PhoneNoDynamicEp: TextView = itemView.findViewById(R.id.PhoneNoDynamicEp)
    }
}
