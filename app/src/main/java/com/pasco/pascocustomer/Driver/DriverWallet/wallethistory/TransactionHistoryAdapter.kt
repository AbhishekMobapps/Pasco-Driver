package com.pasco.pascocustomer.Driver.DriverWallet.wallethistory

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pasco.pascocustome.Driver.Customer.Fragment.CustomerWallet.GetAmountResponse
import com.pasco.pascocustomer.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TransactionHistoryAdapter(
    private val required: Context,
    private var orderList: List<GetAmountResponse.Transaction>

) :
    RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder>() {

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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // holder.userName.text = orderList[position].user
        holder.statusTxt.text = orderList[position].transactionType
        holder.amountTxtC.text = orderList[position].amount.toString()


        val dateTime = orderList[position].createdAt
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US)

        try {
            val parsedDate = inputDateFormat.parse(dateTime)
            outputDateFormat.timeZone = TimeZone.getDefault() // Set to local time zone
            val formattedDateTime = outputDateFormat.format(parsedDate)

            holder.dateTimeTxt.text = formattedDateTime
        } catch (e: ParseException) {
            e.printStackTrace()
        }


    }

    override fun getItemCount(): Int {
        return orderList.size
    }



    private fun showFullAddressDialog(
        pickupLocation: String,
        dropLocations: String
    ) {
        val dialog = Dialog(required)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.show_details)


        val pickUpLocation = dialog.findViewById<TextView>(R.id.pickUpLocation)
        val dropLocation = dialog.findViewById<TextView>(R.id.dropLocation)



        pickUpLocation.text = pickupLocation
        dropLocation.text = dropLocations


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