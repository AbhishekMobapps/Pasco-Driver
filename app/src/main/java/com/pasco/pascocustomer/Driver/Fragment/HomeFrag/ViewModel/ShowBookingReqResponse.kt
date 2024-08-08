package com.pasco.pascocustomer.Driver.Fragment.HomeFrag.ViewModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class ShowBookingReqResponse : Serializable {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: List<ShowBookingReqData>? = null

    inner class ShowBookingReqData : Serializable {


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
        var driverImage: Any? = null

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

        @SerializedName("basicprice")
        @Expose
        var basicprice: Double? = null

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

        @SerializedName("payment_status")
        @Expose
        var paymentStatus: String? = null

        @SerializedName("additionalservice_name")
        @Expose
        var additionalserviceName: Any? = null

        @SerializedName("additionalservice_amount")
        @Expose
        var additionalserviceAmount: Any? = null

        @SerializedName("pickup_datetime")
        @Expose
        var pickupDatetime: String? = null

        @SerializedName("created_at")
        @Expose
        var createdAt: String? = null

        @SerializedName("bid_status")
        @Expose
        var bidStatus: Boolean? = null

        @SerializedName("reject_status")
        @Expose
        var rejectStatus: Boolean? = null
    }
}