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
class SendEmergencyHelpViewModel@Inject constructor(
    application: Application,
    private val repository: SendEmergencyHelpRepository
) : AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mGetHelpDriverResponse = MutableLiveData<Event<SendEmergercyHelpResponse>>()
    var context: Context? = null

    fun sendEmergencyData(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        id: String,
        driver_id: String,
        current_location: String,
        reason :String,
        language :String
    ) =
        viewModelScope.launch {
            getNotesReminderDatas(progressDialog,activity,id,driver_id,current_location,reason,language)
        }

    private suspend fun getNotesReminderDatas(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        id: String,
        driver_id: String,
        current_location: String,
        reason :String,
        language :String
    ) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        repository.sendEmergencyHelpRepo(id, driver_id, current_location,reason,language)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<SendEmergercyHelpResponse>() {
                override fun onNext(value: SendEmergercyHelpResponse) {
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