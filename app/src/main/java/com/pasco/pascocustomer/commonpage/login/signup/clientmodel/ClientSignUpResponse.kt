package com.pasco.pascocustomer.commonpage.login.signup.clientmodel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class ClientSignUpResponse {
    @SerializedName("token")
    @Expose
    var token: Token? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Data : Serializable {
        @SerializedName("user_type")
        @Expose
        var userType: String? = null

        @SerializedName("phone_verify")
        @Expose
        var phoneVerify: String? = null

        @SerializedName("phone_number")
        @Expose
        var phoneNumber: String? = null

    }

    inner class Token : Serializable {
        @SerializedName("refresh")
        @Expose
        var refresh: String? = null

        @SerializedName("access")
        @Expose
        var access: String? = null
    }

}