package com.pasco.pascocustomer.Driver.StartRiding.deliveryproof

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class DeliveryProofResponse: Serializable {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Data: Serializable
    {

        @SerializedName("booking_confimation")
        @Expose
        var bookingConfimation: Int? = null

        @SerializedName("driverID")
        @Expose
        var driverID: Int? = null

        @SerializedName("delivery_image")
        @Expose
        var deliveryImage: String? = null
    }

}
