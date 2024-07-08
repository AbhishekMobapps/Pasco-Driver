package com.pasco.pascocustomer.customer.activity.track.completededuct

import com.google.gson.annotations.SerializedName

class CompletedDeductAmountBody(
    @SerializedName("payment_amount") val payment_amount: String,
    @SerializedName("payment_type") val payment_type: String
)
