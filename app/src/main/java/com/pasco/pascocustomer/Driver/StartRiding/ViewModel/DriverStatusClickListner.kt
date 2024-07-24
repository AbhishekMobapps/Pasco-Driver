package com.pasco.pascocustomer.Driver.StartRiding.ViewModel

interface DriverStatusClickListner {
    fun driverStatusUpdate(position:Int,id:String,status:String)
}