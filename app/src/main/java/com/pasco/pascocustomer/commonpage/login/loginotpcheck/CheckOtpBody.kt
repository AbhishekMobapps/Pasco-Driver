package com.pasco.pascocustomer.commonpage.login.loginotpcheck

import com.google.gson.annotations.SerializedName

class CheckOtpBody(
    @SerializedName("phone_number") val phone_number: String,
    @SerializedName("user_type") val user_type: String,
    @SerializedName("phone_verify") val phone_verify: String,
    @SerializedName("language") val language: String
)