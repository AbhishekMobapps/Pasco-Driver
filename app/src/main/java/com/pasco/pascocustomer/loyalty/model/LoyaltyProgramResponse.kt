package com.pasco.pascocustomer.loyalty.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class LoyaltyProgramResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: List<Datum>? = null

    inner class Datum : Serializable
    {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("program_name")
        @Expose
        var programName: String? = null

        @SerializedName("program_type")
        @Expose
        var programType: String? = null

        @SerializedName("programcode")
        @Expose
        var programcode: String? = null

        @SerializedName("loyaltystartdate")
        @Expose
        var loyaltystartdate: String? = null

        @SerializedName("loyaltyenddate")
        @Expose
        var loyaltyenddate: String? = null

        @SerializedName("loyaltypercent")
        @Expose
        var loyaltypercent: Double? = null

        @SerializedName("programlimit")
        @Expose
        var programlimit: Int? = null

        @SerializedName("created_at")
        @Expose
        var createdAt: String? = null

    }
}