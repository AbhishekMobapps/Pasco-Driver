package com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.userFragment.order.odermodel.CustomerOrderBody
import com.pasco.pascocustomer.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class CancelReasonViewModel@Inject constructor(
    application: Application,
    private val repository: CancelReasonRepository
) : AndroidViewModel(application)  {

    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mCancelOrderResponse = MutableLiveData<Event<CancelReasonResponse>>()
    var context: Context? = null

    fun getCancelReason(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        body: CustomerOrderBody

    ) =
        viewModelScope.launch {
            cancelReason( progressDialog,
                activity,body)
        }
    suspend fun cancelReason(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        body: CustomerOrderBody
    )

    {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        repository.cancelReason(body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CancelReasonResponse>() {
                override fun onNext(value: CancelReasonResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mCancelOrderResponse.value = Event(value)
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