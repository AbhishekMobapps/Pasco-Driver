package com.pasco.pascocustomer.notificationoffon.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class NotificationOnOffResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

}