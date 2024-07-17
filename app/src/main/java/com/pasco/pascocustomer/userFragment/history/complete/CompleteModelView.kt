package com.pasco.pascocustomer.userFragment.history.complete

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.Fragment.DriverTripHistory.CompletedTripHistoryResponse
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.repository.CommonRepository
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
class CompleteModelView @Inject constructor(
    application: Application,
    private val driverCancelledHistory: CommonRepository
) : AndroidViewModel(application) {

    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mCancelledHis = MutableLiveData<Event<CompleteHistoryResponse>>()
    var context: Context? = null

    fun driverTripCancelData(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        body :CustomerOrderBody

    ) =
        viewModelScope.launch {
            getServicesDatas(
                progressDialog,
                activity,
                body
            )
        }

    suspend fun getServicesDatas(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        body :CustomerOrderBody
    ) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        driverCancelledHistory.getCustomerCompletedHistory(body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CompleteHistoryResponse>() {
                override fun onNext(value: CompleteHistoryResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mCancelledHis.value = Event(value)
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