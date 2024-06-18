package com.pasco.pascocustomer.Driver.emergencyhelp.ViewModel

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
class EmergencyHelpDriverViewModel@Inject constructor(
    application: Application,
    private val repository: EmergencyHelpDriverRepository
) : AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mGetHelpDriverResponse = MutableLiveData<Event<EmergencyHelpDriverResponse>>()
    var context: Context? = null

    fun getEmergencyHelpDriverList(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        driver_latitude: String,
        driver_longitude: String
    ) =
        viewModelScope.launch {
            getNotesReminderDatas(progressDialog,activity,driver_latitude,driver_longitude)
        }

    private suspend fun getNotesReminderDatas(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        driver_latitude: String,
        driver_longitude: String
    ) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        repository.getDriverHelpRepo(driver_latitude, driver_longitude)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<EmergencyHelpDriverResponse>() {
                override fun onNext(value: EmergencyHelpDriverResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mGetHelpDriverResponse.value = Event(value)
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