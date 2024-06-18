package com.pasco.pascocustomer.Driver.emergencyhelp.ViewModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class SendEmergercyHelpResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null
}