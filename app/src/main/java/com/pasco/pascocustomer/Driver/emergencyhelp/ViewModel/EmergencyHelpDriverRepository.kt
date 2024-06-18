package com.pasco.pascocustomer.Driver.emergencyhelp.ViewModel

import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.services.ApiServices
import io.reactivex.Observable
import javax.inject.Inject

class EmergencyHelpDriverRepository @Inject constructor(private val apiService: ApiServices) {
    suspend fun getDriverHelpRepo(
        driver_latitude: String,
        driver_longitude: String,
    ): Observable<EmergencyHelpDriverResponse>
    {
        return apiService.getDriverEHelp(
            PascoApp.encryptedPrefs.bearerToken,
            driver_latitude,driver_longitude)
    }
}