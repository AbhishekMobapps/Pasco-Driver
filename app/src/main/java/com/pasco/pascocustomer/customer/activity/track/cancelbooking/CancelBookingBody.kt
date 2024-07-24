package com.pasco.pascocustomer.customer.activity.track.cancelbooking

import com.google.gson.annotations.SerializedName

class CancelBookingBody(
    @SerializedName("reasonid") var reasonid: String,
    @SerializedName("language") var language: String,
)