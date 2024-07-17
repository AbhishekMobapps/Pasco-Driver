package com.pasco.pascocustomer.Driver

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasco.pascocustomer.Driver.adapter.SendMessageAdapter
import com.pasco.pascocustomer.databinding.ActivityDriverMessageBinding
import com.pasco.pascocustomer.language.Originator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverMessageActivity : Originator() {
    private lateinit var binding: ActivityDriverMessageBinding
    private var sendMessageData: List<RideRequestResponse> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backImageDriver.setOnClickListener {
            finish()
        }

        binding.recyclerChatDriver.apply {
            isVerticalScrollBarEnabled = true
            isVerticalFadingEdgeEnabled = true
            binding.recyclerChatDriver.layoutManager = LinearLayoutManager(this@DriverMessageActivity, LinearLayoutManager.VERTICAL, false)
            adapter = SendMessageAdapter(this@DriverMessageActivity, sendMessageData)
        }
    }
}
