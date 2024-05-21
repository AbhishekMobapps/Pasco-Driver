package com.example.transportapp.DriverApp.MarkDuty

import io.reactivex.Observable
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.services.ApiServices
import javax.inject.Inject

class MarkDutyRepository @Inject constructor(private val apiService: ApiServices) {
    suspend fun putMarkRepository(

    ): Observable<MarkDutyResponse> {
        return apiService.putMarkDuty(
            PascoApp.encryptedPrefs.bearerToken)
    }
}