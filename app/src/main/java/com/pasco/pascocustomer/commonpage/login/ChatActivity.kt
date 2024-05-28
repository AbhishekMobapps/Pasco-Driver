package com.pasco.pascocustomer.commonpage.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.databinding.ActivityChatBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {
    private lateinit var binding:ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}