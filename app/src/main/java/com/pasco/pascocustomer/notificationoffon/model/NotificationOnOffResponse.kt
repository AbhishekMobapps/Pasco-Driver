package com.pasco.pascocustomer.notificationoffon.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class NotificationOnOffResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Data : Serializable {
        @SerializedName("user")
        @Expose
        var user: Int? = null

        @SerializedName("ridebooking")
        @Expose
        var ridebooking: Boolean? = null

        @SerializedName("updatebid")
        @Expose
        var updatebid: Boolean? = null

        @SerializedName("assignbooking")
        @Expose
        var assignbooking: Boolean? = null

        @SerializedName("statusupdate")
        @Expose
        var statusupdate: Boolean? = null

        @SerializedName("adminnotification")
        @Expose
        var adminnotification: Boolean? = null

        @SerializedName("cancelbooking")
        @Expose
        var cancelbooking: Boolean? = null

        @SerializedName("completebooking")
        @Expose
        var completebooking: Boolean? = null

        @SerializedName("reminder")
        @Expose
        var reminder: Boolean? = null

        @SerializedName("emergencyhelp")
        @Expose
        var emergencyhelp: Boolean? = null

        @SerializedName("coupon")
        @Expose
        var coupon: Boolean? = null

        @SerializedName("loyaltyprogram")
        @Expose
        var loyaltyprogram: Boolean? = null

    }
}