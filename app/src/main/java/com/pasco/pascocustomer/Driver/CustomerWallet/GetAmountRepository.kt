package com.pasco.pascocustome.Driver.Customer.Fragment.CustomerWallet

import com.pasco.pascocustomer.Driver.DriverWallet.wallethistory.GetAddWalletDataBody
import com.pasco.pascocustomer.Driver.UpdateLocation.UpdationLocationBody
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.services.ApiServices
import io.reactivex.Observable
import retrofit2.http.Body
import javax.inject.Inject

class GetAmountRepository @Inject constructor(private val apiService: ApiServices) {
    suspend fun getAmountRepository(

        @Body body: GetAddWalletDataBody
    ): Observable<GetAmountResponse> {
        return apiService.getUserWallet(
            PascoApp.encryptedPrefs.bearerToken,
            body
        )
    }
}