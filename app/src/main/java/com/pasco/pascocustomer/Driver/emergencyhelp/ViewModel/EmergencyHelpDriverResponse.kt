package com.pasco.pascocustomer.Driver.emergencyhelp.ViewModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class EmergencyHelpDriverResponse:Serializable {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: List<EmergencyHelpDriverResponseData>? = null

    inner class EmergencyHelpDriverResponseData:Serializable{
        @SerializedName("driverid")
        @Expose
        var driverid: Int? = null

        @SerializedName("user")
        @Expose
        var user: String? = null

    }
}