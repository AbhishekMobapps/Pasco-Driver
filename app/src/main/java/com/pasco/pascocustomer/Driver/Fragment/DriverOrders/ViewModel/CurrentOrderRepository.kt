package com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel

import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.services.ApiServices
import com.pasco.pascocustomer.userFragment.order.odermodel.CustomerOrderBody
import io.reactivex.Observable
import retrofit2.http.Body
import javax.inject.Inject

class CurrentOrderRepository @Inject constructor(private val apiService: ApiServices) {
    suspend fun getCurrentOrderRepo(body: CustomerOrderBody): Observable<DAllOrderResponse> {
        return apiService.bookingDriverOnGoing(PascoApp.encryptedPrefs.bearerToken,body)
    }
}