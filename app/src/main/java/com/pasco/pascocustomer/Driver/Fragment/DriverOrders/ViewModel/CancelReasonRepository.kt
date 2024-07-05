package com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel

import com.pasco.pascocustomer.services.ApiServices
import io.reactivex.Observable
import javax.inject.Inject

class CancelReasonRepository @Inject constructor(private val apiService: ApiServices) {
    suspend fun cancelReason(

    ): Observable<CancelReasonResponse> {
        return apiService.CancelReason()
    }
}