package com.pasco.pascocustomer.customer.activity.language

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class LanguageResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: List<Datum>? = null

    inner class Datum : Serializable {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("languagename")
        @Expose
        var languagename: String? = null
    }
}