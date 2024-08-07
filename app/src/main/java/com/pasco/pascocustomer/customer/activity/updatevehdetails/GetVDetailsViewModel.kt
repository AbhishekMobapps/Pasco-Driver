package com.pasco.pascocustomer.customer.activity.updatevehdetails

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.customer.activity.updatevehdetails.GetVDetailsResponse
import com.pasco.pascocustomer.repository.CommonRepository
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
class GetVDetailsViewModel@Inject constructor(
    application: Application,
    private val getUpdateRepository: CommonRepository
) : AndroidViewModel(application)  {

    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mGetVDetails = MutableLiveData<Event<GetVDetailsResponse>>()
    var context: Context? = null

    fun getVDetailsData(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        body : GetVehicleDetailsBody

    ) =
        viewModelScope.launch {
            getVDetailsDatas( progressDialog,
                activity,
                body)
        }
    suspend fun getVDetailsDatas(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        body : GetVehicleDetailsBody
    )

    {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        getUpdateRepository.getUpdateVDetailRepo(body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<GetVDetailsResponse>() {
                override fun onNext(value: GetVDetailsResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mGetVDetails.value = Event(value)
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