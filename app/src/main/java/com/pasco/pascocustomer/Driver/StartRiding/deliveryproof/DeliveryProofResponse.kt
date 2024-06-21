package com.pasco.pascocustomer.Driver.StartRiding.deliveryproof

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DeliveryProofResponse(
    @SerializedName("status")
    @Expose
    var status: String? = null,

    @SerializedName("message")
    @Expose
    var message: Message? = null
) : Serializable {

    data class Message(
        @SerializedName("driverID")
        @Expose
        var driverID: List<String>? = null
    )
}
