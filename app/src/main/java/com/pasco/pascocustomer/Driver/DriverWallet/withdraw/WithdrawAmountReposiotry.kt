package com.pasco.pascocustomer.Driver.DriverWallet.withdraw

import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.services.ApiServices
import io.reactivex.Observable
import retrofit2.http.Body
import javax.inject.Inject

class WithdrawAmountReposiotry@Inject constructor(private val apiService: ApiServices) {
    suspend fun withdrawAmountRepo(
        @Body body: WithdrawAmountBody


    ): Observable<WithdrawAmountResponse> {
        return apiService.withdrawAmount(PascoApp.encryptedPrefs.bearerToken, body)
    }
}