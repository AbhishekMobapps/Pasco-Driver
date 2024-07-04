package com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel

interface CancelOnClick {
    fun cancelOrder(position:Int,bookingID: String)
    fun cancelList(position: Int,id:Int)
}