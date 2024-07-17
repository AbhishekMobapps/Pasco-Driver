package com.pasco.pascocustomer.Driver.emergencyhelp.ViewModel

import com.google.gson.annotations.SerializedName

class SendToAllBody (
    @SerializedName("current_location") var current_location:String,
    @SerializedName("driver_latitude") var driver_latitude:String,
    @SerializedName("driver_longitude") var driver_longitude: String,
    @SerializedName("language") var language: String
)
