package com.pasco.pascocustomer.Driver.DriverWallet.withdraw

import com.google.gson.annotations.SerializedName

class WithdrawAmountBody(
    @SerializedName("amount") val amount: String,
    @SerializedName("language") val language: String

)