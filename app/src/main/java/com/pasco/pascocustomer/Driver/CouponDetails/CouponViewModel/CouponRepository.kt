package com.pasco.pascocustomer.Driver.CouponDetails.CouponViewModel

import io.reactivex.Observable
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.services.ApiServices
import com.pasco.pascocustomer.userFragment.profile.modelview.GetProfileBody
import retrofit2.http.Body
import javax.inject.Inject

class CouponRepository @Inject constructor(private val apiService: ApiServices) {
    suspend fun getCouponRepo(
        body: GetProfileBody

    ): Observable<CouponResponse> {
        return apiService.getCouponList(
            PascoApp.encryptedPrefs.bearerToken,body
        )
    }
}