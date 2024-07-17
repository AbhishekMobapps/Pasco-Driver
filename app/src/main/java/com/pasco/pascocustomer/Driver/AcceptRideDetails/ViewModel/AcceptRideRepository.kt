package com.pasco.pascocustomer.Driver.AcceptRideDetails.ViewModel

import com.pasco.pascocustomer.application.PascoApp
import io.reactivex.Observable

import com.pasco.pascocustomer.services.ApiServices
import com.pasco.pascocustomer.userFragment.order.odermodel.CustomerOrderBody
import javax.inject.Inject

class AcceptRideRepository@Inject constructor(private val apiService: ApiServices) {
    suspend fun putMarkRepository(
        id: String,
         body : CustomerOrderBody

    ): Observable<AcceptRideResponse> {
        return apiService.getBidDetails(
            id, PascoApp.encryptedPrefs.bearerToken,body)
    }
}