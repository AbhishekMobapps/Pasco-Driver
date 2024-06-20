package com.pasco.pascocustomer.Driver.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.pasco.pascocustomer.Driver.StartRiding.deliveryproof.MarkerData
import com.pasco.pascocustomer.R

class PoiInfoAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {

    private val window: View = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        .inflate(R.layout.poi_details_popup, null)

    override fun getInfoWindow(marker: Marker): View? {
        render(marker, window)
        return window
    }

    override fun getInfoContents(marker: Marker): View? {
        return null
    }

    private fun render(marker: Marker, view: View) {
        val markerData = marker.tag as? MarkerData ?: return

        val couponImage = view.findViewById<ImageView>(R.id.iv_coupon_image)
        val couponTitle = view.findViewById<TextView>(R.id.tv_coupon_title)
        val couponType = view.findViewById<TextView>(R.id.tv_coupon_type)
        val couponCode = view.findViewById<TextView>(R.id.tv_coupon_code)
        val startDate = view.findViewById<TextView>(R.id.tv_coupon_start_date)
        val endDate = view.findViewById<TextView>(R.id.tv_coupon_end_date)
        val limit = view.findViewById<TextView>(R.id.tv_coupon_limit)
        val poiAddress = view.findViewById<TextView>(R.id.tv_poi_address)
        val poiCity = view.findViewById<TextView>(R.id.tv_poi_city)
        val poiDesc = view.findViewById<TextView>(R.id.tv_poi_desc)

        couponTitle.text = markerData.poiName
        couponType.text = markerData.poiType
        couponCode.text = markerData.couponCode
        startDate.text = "Start Date: ${markerData.startDate}"
        endDate.text = "End Date: ${markerData.endDate}"
        limit.text = "Limit: ${markerData.limit}"
        poiAddress.text = "Address: ${markerData.poiAddress}"
        poiCity.text = "City: ${markerData.poiCity}"
        poiDesc.text = markerData.poiDesc

        Glide.with(context).load(markerData.imageUrl).into(couponImage)
    }
}
