package com.pasco.pascocustomer.Driver.DriverWallet.wallethistory

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.pasco.pascocustome.Driver.Customer.Fragment.CustomerWallet.GetAmountResponse
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.application.PascoApp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class TransactionHistoryAdapter(
    private val required: Context,
    private var orderList: List<GetAmountResponse.Transaction>


) :
    RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder>() {
    private var userType: String = PascoApp.encryptedPrefs.userType

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amountStatus: TextView = itemView.findViewById(R.id.amountStatus)
        val dateTimeTxt: TextView = itemView.findViewById(R.id.dateTimeTxt)
        val tripIdTxt: TextView = itemView.findViewById(R.id.tripIdTxt)
        val amountTxtC: TextView = itemView.findViewById(R.id.amountTxtC)
        val statusTxt: TextView = itemView.findViewById(R.id.statusTxt)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.credit_debit_history, parent, false)
        return ViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // holder.userName.text = orderList[position].user
        holder.statusTxt.text = orderList[position].transactionType
        val amount = orderList[position].amount.toString()

        holder.amountTxtC.text = "$ $amount"
        if (userType == "driver") {
            holder.amountStatus.text = "Total Amount"
        } else {
            holder.amountStatus.text = orderList[position].paymentStatus
        }

        holder.tripIdTxt.text = orderList[position].id.toString()
        val dateTime = orderList[position].createdAt


        val zonedDateTime = ZonedDateTime.parse(dateTime)
        val outputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a")
        val formattedDate = zonedDateTime.format(outputFormat)
        holder.dateTimeTxt.text = formattedDate

        holder.itemView.setOnClickListener {
            val statusCheck = orderList[position].paymentStatus
            if (statusCheck == "Add Amount") {

            } else {
                showFullAddressDialog(
                    orderList[position].pickupLocation!!,
                    orderList[position].dropLocation!!,
                    orderList[position].orderid
                )
            }


        }
    }

    override fun getItemCount(): Int {
        return orderList.size
    }


    private fun showFullAddressDialog(
        pickupLocation: String,
        dropLocations: String,
        orderid: Int?
    ) {
        val dialog = Dialog(required)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.show_details_final)


        val pickUpLocation = dialog.findViewById<TextView>(R.id.pickUpLocation)
        val dropLocation = dialog.findViewById<TextView>(R.id.dropLocation)
        val tripId = dialog.findViewById<TextView>(R.id.tripId)



        pickUpLocation.text = pickupLocation
        dropLocation.text = dropLocations
        tripId.text = orderid.toString()


        val window = dialog.window
        val lp = window?.attributes
        if (lp != null) {
            lp.width = ActionBar.LayoutParams.MATCH_PARENT
        }
        if (lp != null) {
            lp.height = ActionBar.LayoutParams.WRAP_CONTENT
        }
        if (window != null) {
            window.attributes = lp
        }


        dialog.show()
    }
}
