package com.pasco.pascocustomer.Driver.Fragment.HomeFrag.ViewModel

import com.pasco.pascocustomer.application.PascoApp
import io.reactivex.Observable
import com.pasco.pascocustomer.services.ApiServices
import javax.inject.Inject

class ShowBookingReqRepository@Inject constructor(private val apiService: ApiServices) {
    suspend fun getShowBookingRequests(
        city: String,
        language: String

    ): Observable<ShowBookingReqResponse> {
        return apiService.getBookingReq(PascoApp.encryptedPrefs.bearerToken,city,language)
    }
}