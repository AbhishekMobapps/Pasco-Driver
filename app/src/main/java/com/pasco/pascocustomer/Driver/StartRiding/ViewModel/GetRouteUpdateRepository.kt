package com.pasco.pascocustomer.Driver.StartRiding.ViewModel
import com.pasco.pascocustomer.application.PascoApp
import io.reactivex.Observable
import com.pasco.pascocustomer.services.ApiServices
import com.pasco.pascocustomer.userFragment.order.odermodel.CustomerOrderBody
import retrofit2.http.Body
import javax.inject.Inject

class GetRouteUpdateRepository@Inject constructor(private val apiService: ApiServices) {
    suspend fun getDriverStatusRepo(
        body : CustomerOrderBody
    ): Observable<GetRouteUpdateResponse> {
        return apiService.getDriverStatus(PascoApp.encryptedPrefs.bearerToken,body)
    }
}