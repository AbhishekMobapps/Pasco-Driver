package com.pasco.pascocustomer.notificationoffon.model

import com.google.gson.annotations.SerializedName

class NotificationOnOffBody(
    @SerializedName("ridebooking") val ridebooking: Boolean = false,
    @SerializedName("updatebid") val updatebid: Boolean = false,
    @SerializedName("assignbooking") val assignbooking: Boolean = false,
    @SerializedName("statusupdate") val statusupdate: Boolean = false,
    @SerializedName("adminnotification") val adminnotification: Boolean = false,
    @SerializedName("cancelbooking") val cancelbooking: Boolean = false,
    @SerializedName("completebooking") val completebooking: Boolean = false,
    @SerializedName("reminder") val reminder: Boolean = false,
    @SerializedName("emergencyhelp") val emergencyhelp: Boolean = false,
    @SerializedName("coupon") val coupon: Boolean = false,
    @SerializedName("loyaltyprogram") val loyaltyprogram: Boolean = false,
    @SerializedName("language") val language: String
)