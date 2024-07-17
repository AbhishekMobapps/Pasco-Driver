package com.pasco.pascocustomer.Driver.StartRiding.deliveryproof

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.repository.CommonRepository
import com.pasco.pascocustomer.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class DeliveryVerifyViewModel @Inject constructor(
    application: Application, private val repository: CommonRepository
) : AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mDelVerifyResponse = MutableLiveData<Event<DeliveryProofResponse>>()
    var context: Context? = null


    fun getDriverDetails(
        bookingid: String,
        delivery_code: String,
        language: String,
        activity: Activity,
        progressDialog: CustomProgressDialog
    ) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        repository.verifyDeliveryProof(bookingid, delivery_code,language).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<DeliveryProofResponse>() {
                override fun onNext(value: DeliveryProofResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mDelVerifyResponse.value = Event(value)
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