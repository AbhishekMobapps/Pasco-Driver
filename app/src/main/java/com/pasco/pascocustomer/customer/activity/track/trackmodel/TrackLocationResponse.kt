package com.pasco.pascocustomer.customer.activity.track.trackmodel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class TrackLocationResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Data : Serializable {
        @SerializedName("current_latitude")
        @Expose
        var currentLatitude: Double? = null

        @SerializedName("current_longitude")
        @Expose
        var currentLongitude: Double? = null

        @SerializedName("current_city")
        @Expose
        var currentCity: String? = null

        @SerializedName("current_location")
        @Expose
        var currentLocation: String? = null

        @SerializedName("image")
        @Expose
        var image: String? = null

        @SerializedName("full_name")
        @Expose
        var fullName: String? = null

        @SerializedName("pickup_location")
        @Expose
        var pickupLocation: String? = null

        @SerializedName("pickup_latitude")
        @Expose
        var pickupLatitude: Double? = null

        @SerializedName("pickup_longitude")
        @Expose
        var pickupLongitude: Double? = null

        @SerializedName("pickup_city")
        @Expose
        var pickupCity: String? = null

        @SerializedName("drop_location")
        @Expose
        var dropLocation: String? = null

        @SerializedName("drop_latitude")
        @Expose
        var dropLatitude: Double? = null

        @SerializedName("drop_longitude")
        @Expose
        var dropLongitude: Double? = null

        @SerializedName("drop_city")
        @Expose
        var dropCity: String? = null

        @SerializedName("bid_price")
        @Expose
        var bidPrice: Double? = null

        @SerializedName("leftover_amount")
        @Expose
        var leftoverAmount: String? = null

        @SerializedName("payment_method")
        @Expose
        var paymentMethod: String? = null

        @SerializedName("total_distance")
        @Expose
        var totalDistance: Double? = null

        @SerializedName("duration")
        @Expose
        var duration: Int? = null

        @SerializedName("booking_status")
        @Expose
        var bookingStatus: String? = null

        @SerializedName("driver_status")
        @Expose
        var driverStatus: String? = null

    }
}