package com.pasco.pascocustomer.Driver.DriverWallet.withdraw

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WithdrawAmountResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("amount")
    @Expose
    var amount: Double? = null
}