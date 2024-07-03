package com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.R
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
class DriverCancelViewModel@Inject constructor(
    application: Application,
    private val repository: DriverCancelRepository
) : AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mUrDriverResponse= MutableLiveData<Event<DriverCancelResponse>>()
    var context: Context? = null

    fun getUpdateDdStatusData(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        id: String,
        reasonid:String
    ) =
        viewModelScope.launch {
            getStartTripDatas(progressDialog,activity, id,reasonid)
        }

    suspend fun getStartTripDatas(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        id: String,
        reasonid: String,
    ) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        repository.updateDriverSRepository(id,reasonid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<DriverCancelResponse>() {
                override fun onNext(value: DriverCancelResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mUrDriverResponse.value = Event(value)
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