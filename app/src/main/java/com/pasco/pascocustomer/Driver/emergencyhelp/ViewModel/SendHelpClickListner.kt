package com.pasco.pascocustomer.Driver.emergencyhelp.ViewModel

interface SendHelpClickListner {
    fun sendHelp(position: Int, id: Int, comment: String)
}