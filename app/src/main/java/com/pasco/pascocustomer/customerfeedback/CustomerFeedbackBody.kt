package com.pasco.pascocustomer.customerfeedback

import com.google.gson.annotations.SerializedName

class CustomerFeedbackBody (
    @SerializedName("bookingconfirmation") var bookingconfirmation: String,
    @SerializedName("rating") var rating: String,
    @SerializedName("feedback") var feedback: String,
    @SerializedName("language") var language: String
)