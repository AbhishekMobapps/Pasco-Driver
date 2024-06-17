package com.pasco.pascocustomer.loyalty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.databinding.ActivityLoyaltyBinding
import com.pasco.pascocustomer.loyalty.adapter.LoyaltyProgramAdapter
import com.pasco.pascocustomer.loyalty.model.LoyaltyProgramModelView
import com.pasco.pascocustomer.loyalty.model.LoyaltyProgramResponse
import com.pasco.pascocustomer.loyalty.useloyaltycode.LoyaltyCodeUseBody
import com.pasco.pascocustomer.loyalty.useloyaltycode.LoyaltyCodeUseModelView
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoyaltyActivity : AppCompatActivity(), LoyaltyProgramItemClick {
    private lateinit var binding: ActivityLoyaltyBinding

    private var loyaltyList: List<LoyaltyProgramResponse.Datum> = ArrayList()

    private val loyaltyViewModel: LoyaltyProgramModelView by viewModels()
    private val loyaltyCodeViewModel: LoyaltyCodeUseModelView by viewModels()

    private val progressDialog by lazy { CustomProgressDialog(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoyaltyBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.backBtn.setOnClickListener { finish() }
        loyaltyApi()
        loyaltyObserver()
        loyaltyCodeObserver()
    }

    private fun loyaltyApi() {
        loyaltyViewModel.getReminder(
            this,
            progressDialog
        )
    }

    private fun loyaltyObserver() {
        loyaltyViewModel.progressIndicator.observe(this, Observer {
        })

        loyaltyViewModel.mRejectResponse.observe(this) { response ->
            loyaltyList = response.peekContent().data ?: emptyList()
            val msg = response.peekContent().msg
            Log.e("CancelTripAA", "aaaa.." + msg)

            if (response.peekContent().status == "False") {
                binding.loyaltyRecycler.visibility = View.VISIBLE
                // binding.loyaltyRecycler.text = "You have not cancelled any trips yet."
                binding.loyaltyRecycler.visibility = View.GONE
            } else {
                binding.loyaltyRecycler.visibility = View.GONE
                binding.loyaltyRecycler.visibility = View.VISIBLE
                binding.loyaltyRecycler.isVerticalScrollBarEnabled = true
                binding.loyaltyRecycler.isVerticalFadingEdgeEnabled = true
                binding.loyaltyRecycler.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.loyaltyRecycler.adapter = LoyaltyProgramAdapter(this, loyaltyList, this)

            }
        }

        loyaltyViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }

    override fun loyaltyItemClick(position: Int, id: Int) {
        loyaltyCodeApi(id)

    }

    private fun loyaltyCodeApi(id: Int) {
        Log.e("LoyaltyId", "id...$id")
        val loinBody = LoyaltyCodeUseBody(
            loyalty_program = id.toString()
        )
        loyaltyCodeViewModel.cancelBooking(loinBody, this)
    }

    private fun loyaltyCodeObserver() {
        loyaltyCodeViewModel.progressIndicator.observe(this) {}
        loyaltyCodeViewModel.mRejectResponse.observe(
            this
        ) {
            val msg = it.peekContent().msg
            loyaltyApi()
        }
        loyaltyCodeViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@LoyaltyActivity, it)
            // errorDialogs()
        }
    }
}