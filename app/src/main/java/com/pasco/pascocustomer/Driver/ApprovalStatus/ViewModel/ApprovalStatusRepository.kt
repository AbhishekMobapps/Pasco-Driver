package com.pasco.pascocustomer.Driver.ApprovalStatus.ViewModel

import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.services.ApiServices
import com.pasco.pascocustomer.userFragment.order.odermodel.CustomerOrderBody
import io.reactivex.Observable
import retrofit2.http.Body
import javax.inject.Inject

class ApprovalStatusRepository @Inject constructor(private val apiServices: ApiServices) {
    fun getcheckStatusRepository( body : CustomerOrderBody):
            Observable<ApprovalStatusResponse> {
        return apiServices.getApprovedData(PascoApp.encryptedPrefs.bearerToken,body)
    }
}