package com.pasco.pascocustomer.reminder

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class ReminderResponse {
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

        @SerializedName("user")
        @Expose
        var user: String? = null

        @SerializedName("title")
        @Expose
        var title: String? = null

        @SerializedName("description")
        @Expose
        var description: String? = null

        @SerializedName("reminderdate")
        @Expose
        var reminderdate: String? = null
    }
}