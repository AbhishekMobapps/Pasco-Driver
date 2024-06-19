package com.pasco.pascocustomer.Driver.StartRiding.deliveryproof

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DeliveryProofResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

}