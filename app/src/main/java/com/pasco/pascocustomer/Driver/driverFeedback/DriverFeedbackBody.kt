package com.pasco.pascocustomer.Driver.driverFeedback

import com.google.gson.annotations.SerializedName

class DriverFeedbackBody(
    @SerializedName("bookingconfirmation") var bookingconfirmation: String,
    @SerializedName("rating") var rating: String,
    @SerializedName("feedback") var feedback: String,
    @SerializedName("language") var language: String
)