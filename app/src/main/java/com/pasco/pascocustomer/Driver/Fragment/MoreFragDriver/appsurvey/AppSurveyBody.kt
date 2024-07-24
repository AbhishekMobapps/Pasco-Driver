package com.pasco.pascocustomer.Driver.Fragment.MoreFragDriver.appsurvey

import com.google.gson.annotations.SerializedName

class AppSurveyBody (
    @SerializedName("rating") var rating: String,
    @SerializedName("feedback") var feedback: String,
    @SerializedName("language") var language: String,
)