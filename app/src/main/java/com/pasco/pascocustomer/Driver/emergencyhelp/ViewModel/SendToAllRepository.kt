package com.pasco.pascocustomer.Driver.emergencyhelp.ViewModel
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.services.ApiServices
import io.reactivex.Observable
import retrofit2.http.Body
import javax.inject.Inject

class SendToAllRepository @Inject constructor(private val apiService: ApiServices) {
     fun sendToAllRepository(
        @Body body: SendToAllBody
    ): Observable<SendEmergercyHelpResponse> {
        return apiService.sendToAllDriver(PascoApp.encryptedPrefs.bearerToken, body)
    }
}