package com.pasco.pascocustomer.userFragment.home.sliderpage

import com.google.gson.annotations.SerializedName

class SliderHomeBody(
    @SerializedName("user_type") var user_type: String,
    @SerializedName("language") var language: String
)