package com.pasco.pascocustomer.Driver.StartRiding.deliveryproof

data class MarkerData(
    val poiName: String,
    val poiType: String,
    val couponCode: String,
    val startDate: String,
    val endDate: String,
    val limit: Int,
    val poiAddress: String,
    val poiCity: String,
    val poiDesc: String,
    val imageUrl: String
)
