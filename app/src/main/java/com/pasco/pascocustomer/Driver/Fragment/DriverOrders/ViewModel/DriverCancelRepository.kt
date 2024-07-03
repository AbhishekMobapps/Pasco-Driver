package com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel

import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.services.ApiServices
import io.reactivex.Observable
import javax.inject.Inject

class DriverCancelRepository @Inject constructor(private val apiService: ApiServices) {
    suspend fun updateDriverSRepository(
        id: String,
        reasonid: String
    ): Observable<DriverCancelResponse> {
        return apiService.cancelledData(
            PascoApp.encryptedPrefs.bearerToken,id,reasonid)
    }
}