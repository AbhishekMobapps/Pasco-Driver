package com.pasco.pascocustomer.Driver.emergencyhelp.ViewModel

import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.services.ApiServices
import io.reactivex.Observable
import javax.inject.Inject

class SendEmergencyHelpRepository@Inject constructor(private val apiService: ApiServices) {
    suspend fun sendEmergencyHelpRepo(
        id: String,
        driver_id: String,
        current_location :String,
        reason :String
    ): Observable<SendEmergercyHelpResponse>
    {
        return apiService.sendEmergencyHelp(
            PascoApp.encryptedPrefs.bearerToken,
            id,driver_id,current_location,reason)
    }
}