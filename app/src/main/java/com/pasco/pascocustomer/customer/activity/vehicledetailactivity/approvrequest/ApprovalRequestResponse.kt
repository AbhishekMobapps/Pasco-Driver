package com.pasco.pascocustomer.activity.Driver.AddVehicle.ApprovalRequest

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class ApprovalRequestResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

}