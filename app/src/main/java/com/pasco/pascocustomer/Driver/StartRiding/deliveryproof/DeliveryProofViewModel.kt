package com.pasco.pascocustomer.Driver.StartRiding.deliveryproof

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.repository.CommonRepository
import com.pasco.pascocustomer.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class DeliveryProofViewModel@Inject constructor(
    application: Application, private val repository: CommonRepository
) : AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mDeliveryProofResponse = MutableLiveData<Event<DeliveryProofResponse>>()
    var context: Context? = null

    fun deliveryProofData(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        booking_confimation: RequestBody,
        driverID: RequestBody,
        identify_document: MultipartBody.Part

    ) = viewModelScope.launch {
        updateProfiles(
            progressDialog,
            activity,
            booking_confimation,
            driverID,
            identify_document

        )
    }

    private fun updateProfiles(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        booking_confimation: RequestBody,
        driverID: RequestBody,
        identify_document: MultipartBody.Part
    ) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        repository.uploadDeliveryProofRepo(
            booking_confimation,
            driverID,
            identify_document
            ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<DeliveryProofResponse>() {
                override fun onNext(value: DeliveryProofResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mDeliveryProofResponse.value = Event(value)
                }

                override fun onError(e: Throwable) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    errorResponse.value = e
                }

                override fun onComplete() {
                    progressDialog.stop()
                    progressIndicator.value = false
                }
            })
    }
}