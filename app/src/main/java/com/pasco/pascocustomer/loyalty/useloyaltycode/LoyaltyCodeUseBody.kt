package com.pasco.pascocustomer.loyalty.useloyaltycode

import com.google.gson.annotations.SerializedName

class LoyaltyCodeUseBody(
    @SerializedName("loyalty_program") val loyalty_program: String,
    @SerializedName("language") val language: String
)

