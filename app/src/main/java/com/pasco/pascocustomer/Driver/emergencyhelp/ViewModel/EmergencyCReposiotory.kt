package com.pasco.pascocustomer.Driver.emergencyhelp.ViewModel

import io.reactivex.Observable
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.services.ApiServices
import com.pasco.pascocustomer.userFragment.profile.modelview.GetProfileBody
import javax.inject.Inject

    class EmergencyCReposiotory@Inject constructor(private val apiService: ApiServices) {
        suspend fun getEmregencyRepo(
            body: GetProfileBody

        ): Observable<EmergencyCResponse> {
            return apiService.getEmergencyList(
                PascoApp.encryptedPrefs.bearerToken
            ,body)
        }
    }