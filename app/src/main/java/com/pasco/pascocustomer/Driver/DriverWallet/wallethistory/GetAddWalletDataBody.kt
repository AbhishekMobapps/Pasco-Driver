package com.pasco.pascocustomer.Driver.DriverWallet.wallethistory

import com.google.gson.annotations.SerializedName

class GetAddWalletDataBody(
    @SerializedName("transaction_type") val transaction_type: String
)