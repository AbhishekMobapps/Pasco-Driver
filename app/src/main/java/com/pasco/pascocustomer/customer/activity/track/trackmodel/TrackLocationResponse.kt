package com.pasco.pascocustomer.customer.activity.track.trackmodel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class TrackLocationResponse {
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
        @SerializedName("current_latitude")
        @Expose
        var currentLatitude: Double? = null

        @SerializedName("current_longitude")
        @Expose
        var currentLongitude: Double? = null

        @SerializedName("current_city")
        @Expose
        var currentCity: String? = null

        @SerializedName("current_location")
        @Expose
        var currentLocation: String? = null
    }
}