package com.example.transportapp.DriverApp.MarkDuty

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import com.pasco.pascocustomer.utils.Event
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MarkDutyViewModel  @Inject constructor(
    application: Application,
    private val markDutyRepository: MarkDutyRepository
) : AndroidViewModel(application) {
    val errorResponse = MutableLiveData<Throwable>()
    val mmarkDutyResponse= MutableLiveData<Event<MarkDutyResponse>>()
    var context: Context? = null

    fun putMarkOn(
        activity: Activity
    ) =
        viewModelScope.launch {
            userBookingHList(activity)
        }

    suspend fun userBookingHList(
        activity: Activity
    ) {
        markDutyRepository.putMarkRepository()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<MarkDutyResponse>() {
                override fun onNext(value: MarkDutyResponse) {
                    mmarkDutyResponse.value = Event(value)
                }

                override fun onError(e: Throwable) {
                    errorResponse.value = e
                }

                override fun onComplete() {
                }
            })
    }
}