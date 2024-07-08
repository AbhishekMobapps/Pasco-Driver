package com.pasco.pascocustomer.customer.activity.track.completededuct

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.emergencyhelp.ViewModel.SendEmergercyHelpResponse
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
class CompletedDeductAmtModelView @Inject constructor(
    application: Application, private val repository: CommonRepository
) : AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mRejectResponse = MutableLiveData<Event<SendEmergercyHelpResponse>>()
    var context: Context? = null


    fun deductAmount(
        id: String,
        acceptBody: CompletedDeductAmountBody,
        activity: Activity
    ) {
        //progressDialog.start(activity.getString(R.string.please_wait))
      //  progressIndicator.value = true
        repository.deductAmount(acceptBody, id).subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread()
        )
            .subscribe(object : DisposableObserver<SendEmergercyHelpResponse>() {
                override fun onNext(value: SendEmergercyHelpResponse) {
                    progressIndicator.value = false
                //    progressDialog.stop()
                    mRejectResponse.value = Event(value)
                }

                override fun onError(e: Throwable) {
                    progressIndicator.value = false
                    //progressDialog.stop()
                    errorResponse.value = e
                }

                override fun onComplete() {
                   // progressDialog.stop()
                    progressIndicator.value = false
                }
            })
    }
}