package com.pasco.pascocustomer.customer.activity.notificaion.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.customer.activity.notificaion.NotificationClickListener
import com.pasco.pascocustomer.customer.activity.notificaion.modelview.NotificationResponse
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class NotificationAdapter(
    private val context: Context,
    private val onItemClick: NotificationClickListener,
    private val notificationData: List<NotificationResponse.Datum>
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notificationHeading: TextView = itemView.findViewById(R.id.notificationHeading)
        val notificationTitle: TextView = itemView.findViewById(R.id.notificationTitle)
        val notificationDateTime: TextView = itemView.findViewById(R.id.notificationDateTime)
        val deleteIConNotification: ImageView = itemView.findViewById(R.id.deleteIConNotification)

    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationAdapter.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recycler_notification_driver, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: NotificationAdapter.ViewHolder, position: Int) {
        val notificationItem = notificationData[position]

        var id = notificationItem.id

        holder.notificationHeading.text = notificationData[position].notificationTitle
        holder.notificationTitle.text = notificationData[position].notificationDescription

        val dateTime = notificationData[position].createdAt

        val zonedDateTime = ZonedDateTime.parse(dateTime)
        val outputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a")
        val formattedDate = zonedDateTime.format(outputFormat)

        holder.notificationDateTime.text = formattedDate
        holder.deleteIConNotification.setOnClickListener {
            openDeleteDialog(position, id!!)
        }

    }

    @SuppressLint("MissingInflatedId")
    private fun openDeleteDialog(position: Int, id: Int) {
        val builder = AlertDialog.Builder(context, R.style.Style_Dialog_Rounded_Corner)
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.delete_notification_popup, null)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val NoBtnAcceptNDel = dialogView.findViewById<TextView>(R.id.NoBtnAcceptNDel)
        val YesBtnAcceptNdel = dialogView.findViewById<TextView>(R.id.YesBtnAcceptNdel)

        NoBtnAcceptNDel.setOnClickListener {
            dialog.dismiss()
        }
        YesBtnAcceptNdel.setOnClickListener {
            onItemClick.deleteNotification(position, id)
            notifyDataSetChanged()
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun getItemCount(): Int {
        return notificationData.size
    }
}