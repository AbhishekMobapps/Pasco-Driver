package com.pasco.pascocustomer.Driver

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasco.pascocustomer.Driver.adapter.CouponUsedAdapter
import com.pasco.pascocustomer.databinding.ActivityCouponUsedBinding
import com.pasco.pascocustomer.language.Originator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CouponUsedActivity : Originator() {
    private lateinit var binding: ActivityCouponUsedBinding
    private var couponUsedByList: List<RideRequestResponse> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCouponUsedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrowImgCouponsUBY.setOnClickListener {
            finish()
        }

        binding.recyclerCouponUsedByList.isVerticalScrollBarEnabled = true
        binding.recyclerCouponUsedByList.isVerticalFadingEdgeEnabled = true
        binding.recyclerCouponUsedByList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerCouponUsedByList.adapter = CouponUsedAdapter(this, couponUsedByList)
    }
}