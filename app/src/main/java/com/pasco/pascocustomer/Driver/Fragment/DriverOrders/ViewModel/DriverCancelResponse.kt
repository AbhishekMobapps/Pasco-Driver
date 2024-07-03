package com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class DriverCancelResponse {

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null
}