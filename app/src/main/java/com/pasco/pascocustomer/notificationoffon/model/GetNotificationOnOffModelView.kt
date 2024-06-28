package com.pasco.pascocustomer.notificationoffon.model

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
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
class GetNotificationOnOffModelView @Inject constructor(
    application: Application, private val repository: CommonRepository
) : AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mRejectResponse = MutableLiveData<Event<NotificationOnOffResponse>>()
    var context: Context? = null


    fun getNotificationOnOff(activity: Activity
    ) {
        //progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        repository.getNotificationOnOff().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<NotificationOnOffResponse>() {
                override fun onNext(value: NotificationOnOffResponse) {
                    progressIndicator.value = false
                 //   progressDialog.stop()
                    mRejectResponse.value = Event(value)
                }

                override fun onError(e: Throwable) {
                    progressIndicator.value = false
                  //  progressDialog.stop()
                    errorResponse.value = e
                }

                override fun onComplete() {
                  //  progressDialog.stop()
                    progressIndicator.value = false
                }
            })
    }
}