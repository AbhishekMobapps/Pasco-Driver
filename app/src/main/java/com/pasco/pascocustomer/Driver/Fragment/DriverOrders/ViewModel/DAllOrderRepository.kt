package com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel

import com.pasco.pascocustomer.application.PascoApp
import io.reactivex.Observable
import com.pasco.pascocustomer.services.ApiServices
import com.pasco.pascocustomer.userFragment.order.odermodel.CustomerOrderBody
import retrofit2.http.Body
import javax.inject.Inject

class DAllOrderRepository@Inject constructor(private val apiService: ApiServices) {
    suspend fun getAllOrderRepo( body : CustomerOrderBody): Observable<DAllOrderResponse> {
        return apiService.getAllOrderDriver(PascoApp.encryptedPrefs.bearerToken,body)
    }
}