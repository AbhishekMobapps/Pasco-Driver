package com.pasco.pascocustomer.Driver.StartRiding.ViewModel

import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.services.ApiServices
import com.pasco.pascocustomer.userFragment.profile.modelview.GetProfileBody
import io.reactivex.Observable
import retrofit2.http.Body
import javax.inject.Inject

class CompleteRideRepository@Inject constructor(private val apiService: ApiServices) {
    suspend fun getCompleteDriverRide(
        Id:String,
        body: GetProfileBody

    ): Observable<CompleteRideResponse> {
        return apiService.getCompletedRide(
            Id,
            PascoApp.encryptedPrefs.bearerToken,
            body
        )
    }
}