package com.pasco.pascocustomer.Driver.CouponDetails.Ui

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.CouponDetails.CouponViewModel.CouponResponse
import com.pasco.pascocustomer.Driver.CouponDetails.CouponViewModel.CouponViewModel
import com.pasco.pascocustomer.Driver.adapter.CouponListAdapter
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.databinding.ActivityCouponsAndEarningBinding
import com.pasco.pascocustomer.language.Originator
import com.pasco.pascocustomer.userFragment.profile.modelview.GetProfileBody
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CouponsAndEarningActivity : Originator() {
    private lateinit var binding: ActivityCouponsAndEarningBinding
    private var checkCouponList: List<CouponResponse.CouponDataList> = ArrayList()
    private lateinit var activity: Activity
    private val couponViewModel: CouponViewModel by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }
    private lateinit var sharedPreferencesLanguageName: SharedPreferences
    private var languageId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCouponsAndEarningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrowImgCouponsBonus.setOnClickListener {
            finish()
        }
        activity = this
        sharedPreferencesLanguageName = getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
        languageId = sharedPreferencesLanguageName.getString("languageId", "").toString()
        //call api
        couponDetailList()
        //call observer
        couponObserver()

        binding.recyclerCouponList.isVerticalScrollBarEnabled = true
        binding.recyclerCouponList.isVerticalFadingEdgeEnabled = true
        binding.recyclerCouponList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerCouponList.adapter = CouponListAdapter(this, checkCouponList)
    }

    private fun couponObserver() {
        couponViewModel.mGetCouponList.observe(this) { response ->
            val message = response.peekContent().msg!!
            checkCouponList = response.peekContent().data ?: emptyList()
            if (checkCouponList?.isEmpty()!!) {
                Toast.makeText(this@CouponsAndEarningActivity, getString(R.string.no_data_found), Toast.LENGTH_SHORT)
                    .show()
                binding.recyclerCouponList.isVerticalScrollBarEnabled = true
                binding.recyclerCouponList.isVerticalFadingEdgeEnabled = true
                binding.recyclerCouponList.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.recyclerCouponList.adapter = CouponListAdapter(this, checkCouponList)
            }

            if (response.peekContent().status.equals("False")) {
                Toast.makeText(this, "failed: $message", Toast.LENGTH_LONG).show()
            } else {
                // Uncomment the RecyclerView setup
                binding.recyclerCouponList.isVerticalScrollBarEnabled = true
                binding.recyclerCouponList.isVerticalFadingEdgeEnabled = true
                binding.recyclerCouponList.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.recyclerCouponList.adapter = CouponListAdapter(this, checkCouponList)
            }

        }
    }

    private fun couponDetailList() {
        val body = GetProfileBody(
            language = languageId
        )
        couponViewModel.getCouponListData(
            progressDialog,
            activity,
            body
        )
    }
}