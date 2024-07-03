package com.pasco.pascocustomer.userFragment.history.complete

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class CompleteHistoryResponse {

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: List<Datum>? = null

    inner class Datum : java.io.Serializable {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("booking_number")
        @Expose
        var bookingNumber: String? = null

        @SerializedName("user")
        @Expose
        var user: String? = null

        @SerializedName("user_image")
        @Expose
        var userImage: String? = null

        @SerializedName("driver")
        @Expose
        var driver: String? = null

        @SerializedName("driver_image")
        @Expose
        var driverImage: String? = null

        @SerializedName("shipmentname")
        @Expose
        var shipmentname: String? = null

        @SerializedName("vehiclename")
        @Expose
        var vehiclename: String? = null

        @SerializedName("pickup_location")
        @Expose
        var pickupLocation: String? = null

        @SerializedName("pickup_latitude")
        @Expose
        var pickupLatitude: Double? = null

        @SerializedName("pickup_longitude")
        @Expose
        var pickupLongitude: Double? = null

        @SerializedName("drop_location")
        @Expose
        var dropLocation: String? = null

        @SerializedName("drop_latitude")
        @Expose
        var dropLatitude: Double? = null

        @SerializedName("drop_longitude")
        @Expose
        var dropLongitude: Double? = null

        @SerializedName("total_distance")
        @Expose
        var totalDistance: Double? = null

        @SerializedName("duration")
        @Expose
        var duration: Int? = null

        @SerializedName("basicprice")
        @Expose
        var basicprice: Double? = null

        @SerializedName("booking_status")
        @Expose
        var bookingStatus: String? = null

        @SerializedName("payment_status")
        @Expose
        var paymentStatus: String? = null

        @SerializedName("pickup_datetime")
        @Expose
        var pickupDatetime: String? = null

        @SerializedName("commision_price")
        @Expose
        var commisionPrice: Double? = null

        @SerializedName("message")
        @Expose
        var message: String? = null

        @SerializedName("payment_method")
        @Expose
        var paymentMethod: String? = null

        @SerializedName("customer_status")
        @Expose
        var customerStatus: String? = null

        @SerializedName("additionalservice_name")
        @Expose
        var additionalserviceName: Any? = null

        @SerializedName("additionalservice_amount")
        @Expose
        var additionalserviceAmount: Any? = null

        @SerializedName("created_at")
        @Expose
        var createdAt: String? = null

        @SerializedName("availability_datetime")
        @Expose
        var availabilityDatetime: String? = null

        @SerializedName("bid_price")
        @Expose
        var bidPrice: Double? = null

        @SerializedName("feedback")
        @Expose
        var feedback: Int? = null
    }


}