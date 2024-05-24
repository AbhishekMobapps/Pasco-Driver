package com.pasco.pascocustomer.activity.Driver.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.AcceptRideDetails.Ui.AcceptRideActivity
import com.pasco.pascocustomer.Driver.Fragment.DriverAllBiddsDetail.ViewModel.GetDriverBidDetailsDataResponse
import com.pasco.pascocustomer.Driver.StartRiding.Ui.DriverStartRidingActivity
import com.pasco.pascocustomer.Driver.StartRiding.ViewModel.GetRouteUpdateResponse
import com.pasco.pascocustomer.Driver.StartRiding.ViewModel.GetRouteUpdateViewModel
import com.pasco.pascocustomer.Driver.StartRiding.ViewModel.StartTripViewModel
import com.pasco.pascocustomer.Driver.customerDetails.CustomerDetailsActivity
import de.hdodenhof.circleimageview.CircleImageView
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.utils.ErrorUtil
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class DriverAllBiddDetailAdapter(
    private val context: Context,
    private val getDriverData: List<GetDriverBidDetailsDataResponse.DriverAllBidData>,
    private val activity: FragmentActivity,
    private val getRouteUpdateViewModel: GetRouteUpdateViewModel,
    private val startTripViewModel: StartTripViewModel,
    private var spinnerDriverSId: String ="",
    private var BookIddd: String =""
) : RecyclerView.Adapter<DriverAllBiddDetailAdapter.ViewHolder>() {

    private var routeType: List<GetRouteUpdateResponse.RouteResponseData>? = null
    private val routeTypeStatic: MutableList<String> = mutableListOf()
    private val progressDialog by lazy { CustomProgressDialog(activity) }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val driverSeeUserProfile: CircleImageView = itemView.findViewById(R.id.driverSeeUserProfile)
        val pickUpDetailsORD: TextView = itemView.findViewById(R.id.pickUpDetailsORD)
        val DropDetailsORD: TextView = itemView.findViewById(R.id.DropDetailsORD)
        val distanceDORD: TextView = itemView.findViewById(R.id.distanceDORD)
        val totalPricestaticDORD: TextView = itemView.findViewById(R.id.totalPricestaticDORD)
        val maxPriceDORD: TextView = itemView.findViewById(R.id.maxPriceDORD)
        val orderIdDynamicDORD: TextView = itemView.findViewById(R.id.orderIdDynamicDORD)
        val clientNameOrdR: TextView = itemView.findViewById(R.id.clientNameOrdR)
        val orderDetailDR: TextView = itemView.findViewById(R.id.orderDetailDR)
        val acceptDORD: TextView = itemView.findViewById(R.id.acceptDORD)
        val cPriceDORD: TextView = itemView.findViewById(R.id.cPriceDORD)
        val consAccepORD: ConstraintLayout = itemView.findViewById(R.id.consAccepORD)
        val routeSpinnerSpinnerSRD: Spinner =
            itemView.findViewById(R.id.routeSpinnerSpinnerSRD) // Add Spinner view reference
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recycler_order_details, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return getDriverData.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bookingReq = getDriverData[position]
        BookIddd = bookingReq.id.toString()
        val price = "$${bookingReq.basicprice}"
        val bPrice = "$${bookingReq.bidPrice}"
        val commissionPrice = "$${bookingReq.commissionPrice}"
        val dateTime = bookingReq.pickupDatetime.toString()
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US)

        val baseUrl = "http://69.49.235.253:8090"
        val imagePath = bookingReq.userImage.orEmpty()

        val imageUrl = "$baseUrl$imagePath"
        Glide.with(context)
            .load(imageUrl)
            .into(holder.driverSeeUserProfile)


        try {
            val parsedDate = inputDateFormat.parse(dateTime)
            outputDateFormat.timeZone = TimeZone.getDefault() // Set to local time zone
            val formattedDateTime = outputDateFormat.format(parsedDate)
            holder.orderDetailDR.text = formattedDateTime
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val status = bookingReq.customerStatus.toString()
        if (status == "confirmed") {
            holder.consAccepORD.visibility = View.VISIBLE
        } else {
            holder.consAccepORD.visibility = View.GONE
        }
        with(holder) {
            val pickupCity = bookingReq.pickupLocation.toString()
            val dropCity = bookingReq.dropLocation.toString()
            pickUpDetailsORD.text = pickupCity
            DropDetailsORD.text = dropCity
            val formattedDistance = String.format("%.1f", bookingReq.totalDistance)
            distanceDORD.text = "$formattedDistance km"

            totalPricestaticDORD.text = price
            maxPriceDORD.text = bPrice
            cPriceDORD.text = commissionPrice
            clientNameOrdR.text = bookingReq.user
            orderIdDynamicDORD.text = truncateBookingNumber(bookingReq.bookingNumber.toString())
            driverSeeUserProfile.setOnClickListener {
                val id = bookingReq.id.toString()
                val intent = Intent(context, CustomerDetailsActivity::class.java)
                intent.putExtra("customerId",id)
                context.startActivity(intent)
                // openDialogBox(id, bookingId)
            }

            orderIdDynamicDORD.setOnClickListener {
                showFullAddressDialog(bookingReq.bookingNumber.toString())
            }
            // Call API
            getRouteUpdateViewModel.getDriverStatusData(progressDialog, activity)

            // Observe the response
            getRouteUpdateViewModel.mGetRouteUpdate.observe(activity) { response ->
                val content = response.peekContent()
                val message = content.msg ?: return@observe
                routeType = content.data!!

                // Clear the list before adding new items
                routeTypeStatic.clear()

                for (element in routeType!!) {
                    element.status?.let { routeTypeStatic.add(it) }
                }
                val dAdapter = SpinnerAdapter(
                    context,
                    R.layout.custom_service_type_spinner,
                    routeTypeStatic
                )
                dAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                dAdapter.add(context.getString(R.string.selectStatus))
                holder.routeSpinnerSpinnerSRD.adapter = dAdapter // Set adapter to Spinner
                holder.routeSpinnerSpinnerSRD.setSelection(dAdapter.count)
                holder.routeSpinnerSpinnerSRD.setSelection(dAdapter.getPosition(context.getString(R.string.selectStatus)))

                if (response.peekContent().status == "False") {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                } else {
                    //  Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }

            getRouteUpdateViewModel.errorResponse.observe(activity) {
                // Handle general errors
                ErrorUtil.handlerGeneralError(context, it)
            }

            holder.routeSpinnerSpinnerSRD.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>?,
                        view: View?,
                        i: Int,
                        l: Long
                    ) {
                        // Toast.makeText(activity, "Country Spinner Working **********", Toast.LENGTH_SHORT).show()

                        val item = holder.routeSpinnerSpinnerSRD.selectedItem.toString()
                        if (item == context.getString(R.string.selectStatus)) {

                        } else {
                            spinnerDriverSId = routeType?.get(i)?.id.toString()
                            Log.e("onItemSelected", spinnerDriverSId)
                        }
                    }

                    override fun onNothingSelected(adapterView: AdapterView<*>?) {
                        // Do nothing
                    }
                }
        }
        holder.acceptDORD.setOnClickListener {
            if (spinnerDriverSId.isNullOrBlank())
            {
                Toast.makeText(context, "Please select the status", Toast.LENGTH_SHORT).show()
            }
            else
            {
                startTripViewModel.getStartTripData(progressDialog, activity,BookIddd,spinnerDriverSId)

            }
            startTripViewModel.mStartTripResponse.observe(activity) { response ->
                val message = response.peekContent().msg!!
                if (response.peekContent().status == "True") {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    val intent = Intent(context, DriverStartRidingActivity::class.java).apply {
                        putExtra("pickupLoc", bookingReq.pickupLocation.toString())
                        putExtra("dropLoc", bookingReq.dropLocation.toString())
                        putExtra("latitudePickUp", bookingReq.pickupLatitude.toString())
                        putExtra("longitudePickUp", bookingReq.pickupLongitude.toString())
                        putExtra("latitudeDrop", bookingReq.dropLatitude.toString())
                        putExtra("longitudeDrop", bookingReq.dropLongitude.toString())
                        putExtra("deltime", "${bookingReq.duration.toString()} min")
                        putExtra("image", imageUrl)
                        putExtra("BookId", bookingReq.id.toString())
                        putExtra("sID",spinnerDriverSId)
                    }
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            }

            startTripViewModel.errorResponse.observe(activity) {
                // Handle general errors
                ErrorUtil.handlerGeneralError(context, it)
            }
        }
    }

    fun truncateBookingNumber(bookingNumber: String, maxLength: Int = 8): String {
        return if (bookingNumber.length > maxLength) {
            "${bookingNumber.substring(0, maxLength)}..."
        } else {
            bookingNumber
        }
    }

    fun showFullAddressDialog(fullBookingNumber: String) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Order ID")
        alertDialogBuilder.setMessage(fullBookingNumber)
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
    class SpinnerAdapter(context: Context, textViewResourceId: Int, smonking: List<String>) :
        ArrayAdapter<String>(context, textViewResourceId, smonking) {

        override fun getCount(): Int {
            val count = super.getCount()
            return if (count > 0) count - 1 else count
        }
    }
}