package com.pasco.pascocustomer.reminder

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.customer.activity.notificaion.NotificationClickListener
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import java.util.*

class ReminderAdapter(
    private val context: Context,
    private val onItemClick: ReminderItemClick,
    private val notificationData: List<ReminderResponse.Datum>
) : RecyclerView.Adapter<ReminderAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTxt: TextView = itemView.findViewById(R.id.titleTxt)
        val dateTimeTxt: TextView = itemView.findViewById(R.id.dateTimeTxt)
        val descriptionTxt: TextView = itemView.findViewById(R.id.descriptionTxt)
        val okBtn: TextView = itemView.findViewById(R.id.okBtn)


    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReminderAdapter.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.reminder_user_layout, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ReminderAdapter.ViewHolder, position: Int) {
        val notificationItem = notificationData[position]



        holder.titleTxt.text = notificationData[position].title
        holder.dateTimeTxt.text = notificationData[position].reminderdate
        holder.descriptionTxt.text = notificationData[position].description

        holder.okBtn.setOnClickListener {
            var id = notificationItem.id
            onItemClick.reminderItemClick(position,id!!)
            notifyDataSetChanged()
        }

    }

    @SuppressLint("MissingInflatedId")


    override fun getItemCount(): Int {
        return notificationData.size
    }
}