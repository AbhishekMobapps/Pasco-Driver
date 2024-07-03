package com.pasco.pascocustomer.Driver.Fragment.MoreFragDriver.appsurvey

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AppSurveyResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null
}