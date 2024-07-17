package com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel

import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.services.ApiServices
import com.pasco.pascocustomer.userFragment.order.odermodel.CustomerOrderBody
import io.reactivex.Observable
import javax.inject.Inject

class CancelReasonRepository @Inject constructor(private val apiService: ApiServices) {
    suspend fun cancelReason(body: CustomerOrderBody): Observable<CancelReasonResponse> {
        return apiService.CancelReason(PascoApp.encryptedPrefs.bearerToken, body)
    }
}