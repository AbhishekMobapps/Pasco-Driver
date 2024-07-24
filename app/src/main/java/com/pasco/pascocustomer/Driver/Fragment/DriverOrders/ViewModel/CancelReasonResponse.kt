package com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class CancelReasonResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: List<CancellationList>? = null

    inner class CancellationList
    {

        @SerializedName("reasonid")
        @Expose
        var reasonid: Int? = null

        @SerializedName("reason")
        @Expose
        var reason: String? = null

        @SerializedName("reason_code")
        @Expose
        var reasonCode: String? = null
    }

}