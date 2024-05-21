package com.pasco.pascocustomer.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.databinding.ActivityDashboardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}