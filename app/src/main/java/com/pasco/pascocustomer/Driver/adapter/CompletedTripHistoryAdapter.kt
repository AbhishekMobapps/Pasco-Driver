package com.pasco.pascocustomer.Driver.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pasco.pascocustomer.BuildConfig
import com.pasco.pascocustomer.Driver.Fragment.DriverTripHistory.AddFeedbackOnClickListner
import com.pasco.pascocustomer.Driver.Fragment.DriverTripHistory.CompletedTripHistoryResponse
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.invoice.InvoiceActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class CompletedTripHistoryAdapter(
    private val context: Context,
    private val onItemClick: AddFeedbackOnClickListner,
    private val driverTripHistory: List<CompletedTripHistoryResponse.DriverTripHistoryData>
) :
    RecyclerView.Adapter<CompletedTripHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CompletedTripHistoryAdapter.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recycler_driver_trip_history, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CompletedTripHistoryAdapter.ViewHolder, position: Int) {
        val driverTripHis = driverTripHistory[position]
        val price = "$${driverTripHis.bidPrice}"
        val dBookingStatus = driverTripHis.bookingStatus.toString()
        val dateTime = driverTripHis.availabilityDatetime.toString()

        Log.e("dateTimeDriHis", "dateTimeDriHis.." + dateTime)

        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US)

        val url = driverTripHis.userImage
        Glide.with(context).load(BuildConfig.IMAGE_KEY + url)
            .placeholder(R.drawable.home_bg).into(holder.driverProfileCth)

        try {
            val parsedDate = inputDateFormat.parse(dateTime)
            outputDateFormat.timeZone = TimeZone.getDefault() // Set to local time zone
            val formattedDateTime = outputDateFormat.format(parsedDate)
            holder.dateTimeDriHis.text = formattedDateTime
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        with(holder) {
            clientNameDriHis.text = driverTripHis.user.toString()
            totalCostDriverHis.text = price
            val durationInSeconds = driverTripHis.duration
            val durationInMinutes = durationInSeconds!! / 60
            val hours = durationInMinutes / 60
            val minutes = durationInMinutes % 60
            val durationString = "$hours hours $minutes min"
            arrivalTimeDriverHis.text = durationString

            pickUpDetailsDriHis.text = driverTripHis.pickupLocation.toString()
            DropDetailsDriHis.text = driverTripHis.dropLocation.toString()
            bookingstatus.text = dBookingStatus.toString()
        }
        holder.invoiceTextView.setOnClickListener {
            val id = driverTripHistory[position].id
            val intent = Intent(context, InvoiceActivity::class.java)
            intent.putExtra("id", id.toString())
            context.startActivity(intent)
        }
        val feedback = driverTripHis.feedback
        if (feedback == 1) {
            holder.addFeedbackTextView.visibility = View.GONE
        } else {
            holder.addFeedbackTextView.visibility = View.VISIBLE
            holder.addFeedbackTextView.setOnClickListener {
                val bottomSheetDialog = BottomSheetDialog(context, R.style.TopCircleDialogStyle)
                val view =
                    LayoutInflater.from(context).inflate(R.layout.driver_feedback_popup, null)
                bottomSheetDialog!!.setContentView(view)


                val ratingBar = bottomSheetDialog?.findViewById<RatingBar>(R.id.ratingBar)
                val commentTxt = bottomSheetDialog?.findViewById<EditText>(R.id.commentTxt)
                val submitBtn = bottomSheetDialog?.findViewById<TextView>(R.id.submitBtn)
                val skipBtn = bottomSheetDialog?.findViewById<TextView>(R.id.skipBtn)

                var ratingBars = ""
                ratingBar?.setOnRatingBarChangeListener { _, rating, _ ->
                    // Toast.makeText(this, "New Rating: $rating", Toast.LENGTH_SHORT).show()
                    ratingBars = rating.toString()
                }
                submitBtn?.setOnClickListener {
                    val comment = commentTxt?.text.toString()
                    if (ratingBars.isEmpty()) {
                        Toast.makeText(context, "Please add a rating", Toast.LENGTH_SHORT).show()
                    } else if (comment.isBlank()) {
                        Toast.makeText(context, "Please add a comment", Toast.LENGTH_SHORT).show()
                    } else {
                        onItemClick.addFeedbackItemClick(
                            position,
                            driverTripHis.id!!,
                            comment,
                            ratingBars,
                            bottomSheetDialog
                        )
                        notifyDataSetChanged()
                    }
                }

                skipBtn?.setOnClickListener { bottomSheetDialog?.dismiss() }

                val displayMetrics = DisplayMetrics()
                val windowManager =
                    context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                windowManager.defaultDisplay.getMetrics(displayMetrics)

                val screenHeight = displayMetrics.heightPixels
                val halfScreenHeight = (screenHeight * 0.6).toInt()
                val eightyPercentScreenHeight = (screenHeight * 0.8).toInt()

                // Set the initial height of the bottom sheet to 60% of the screen height
                val layoutParams = view.layoutParams
                layoutParams.height = halfScreenHeight
                view.layoutParams = layoutParams

                var isExpanded = false
                view.setOnClickListener {
                    // Expand or collapse the bottom sheet when it is touched
                    layoutParams.height = if (isExpanded) {
                        halfScreenHeight
                    } else {
                        eightyPercentScreenHeight
                    }
                    view.layoutParams = layoutParams
                    isExpanded = !isExpanded
                }

                bottomSheetDialog.show()
            }
        }

    }

    override fun getItemCount(): Int {
        return driverTripHistory.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clientNameDriHis = itemView.findViewById<TextView>(R.id.clientNameDriHis)
        val totalCostDriverHis: TextView = itemView.findViewById<TextView>(R.id.totalCostDriverHis)
        val arrivalTimeDriverHis: TextView =
            itemView.findViewById<TextView>(R.id.arrivalTimeDriverHis)
        val pickUpDetailsDriHis = itemView.findViewById<TextView>(R.id.pickUpDetailsDriHis)
        val DropDetailsDriHis = itemView.findViewById<TextView>(R.id.DropDetailsDriHis)
        val dateTimeDriHis = itemView.findViewById<TextView>(R.id.dateTimeDriHis)
        val driverProfileCth = itemView.findViewById<ImageView>(R.id.driverProfileCth)
        val bookingstatus = itemView.findViewById<TextView>(R.id.bookingstatus)
        val invoiceTextView = itemView.findViewById<TextView>(R.id.invoiceTextView)
        val addFeedbackTextView = itemView.findViewById<TextView>(R.id.addFeedbackTextView)


    }
}