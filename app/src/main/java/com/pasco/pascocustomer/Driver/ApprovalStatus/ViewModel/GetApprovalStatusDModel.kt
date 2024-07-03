package com.pasco.pascocustomer.Driver.ApprovalStatus.ViewModel

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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
class GetApprovalStatusDModel@Inject constructor(
    application: Application,
    private val getUpdateRepository: CommonRepository
) : AndroidViewModel(application)  {

    val errorResponse = MutableLiveData<Throwable>()
    val mGetVDetails = MutableLiveData<Event<GetVDetailsResponse>>()
    var context: Context? = null

    fun getApprovalDModeData(
        activity: Activity

    ) =
        viewModelScope.launch {
            getApprovalDModeDatas(
                activity)
        }
    suspend fun getApprovalDModeDatas(
        activity: Activity
    )

    {
        getUpdateRepository.getUpdateVDetailRepo()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<GetVDetailsResponse>() {
                override fun onNext(value: GetVDetailsResponse) {
                    mGetVDetails.value = Event(value)
                }

                override fun onError(e: Throwable) {
                    errorResponse.value = e
                }

                override fun onComplete() {
                }
            })
    }
}