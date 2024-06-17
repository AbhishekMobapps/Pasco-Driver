package com.pasco.pascocustomer.customer.activity.notificaion.clearnotification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ClearAllNotificationResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null
}