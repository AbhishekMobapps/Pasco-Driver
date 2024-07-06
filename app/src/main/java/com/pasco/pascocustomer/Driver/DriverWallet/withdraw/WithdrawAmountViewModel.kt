package com.pasco.pascocustomer.Driver.DriverWallet.withdraw

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
class WithdrawAmountViewModel @Inject constructor(
    application: Application,
    private val withdrawAmountReposiotry: WithdrawAmountReposiotry
) : AndroidViewModel(application)  {

    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mGetWithdrawList = MutableLiveData<Event<WithdrawAmountResponse>>()
    var context: Context? = null

    fun getWithdrawData(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        body: WithdrawAmountBody

    ) =
        viewModelScope.launch {
            getCouponListDatas( progressDialog,
                activity,body)
        }
    suspend fun getCouponListDatas(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        body: WithdrawAmountBody
    )

    {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        withdrawAmountReposiotry.withdrawAmountRepo(body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<WithdrawAmountResponse>() {
                override fun onNext(value: WithdrawAmountResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mGetWithdrawList.value = Event(value)
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