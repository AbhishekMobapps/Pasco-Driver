package com.pasco.pascocustomer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pasco.pascocustomer.Driver.DriverWallet.DriverWalletActivity
import com.pasco.pascocustomer.customer.activity.hometabactivity.AllTabPayActivity
import com.pasco.pascocustomer.databinding.ActivityComlpleteStatusBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ComlpleteStatusActivity : AppCompatActivity() {
    private var addWallet = ""
    private lateinit var binding: ActivityComlpleteStatusBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComlpleteStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addWallet = intent.getStringExtra("addWallet").toString()

        if (addWallet == "wallet") {
            val intent = Intent(this, AllTabPayActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, DriverWalletActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}