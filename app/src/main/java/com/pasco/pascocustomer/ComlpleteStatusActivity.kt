package com.pasco.pascocustomer

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.pasco.pascocustomer.Driver.AcceptRideDetails.Ui.AcceptRideActivity
import com.pasco.pascocustomer.Driver.DriverWallet.DriverWalletActivity
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.customer.activity.allbiddsdetailsactivity.AllBiddsDetailsActivity
import com.pasco.pascocustomer.customer.activity.hometabactivity.AllTabPayActivity
import com.pasco.pascocustomer.customer.activity.track.TrackActivity
import com.pasco.pascocustomer.databinding.ActivityComlpleteStatusBinding
import com.pasco.pascocustomer.language.Originator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ComlpleteStatusActivity : Originator() {
    private var addWallet = ""
    private var walletC = ""
    private lateinit var binding: ActivityComlpleteStatusBinding
    private var user = ""
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        binding = ActivityComlpleteStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)
        user = PascoApp.encryptedPrefs.userType
        addWallet = intent.getStringExtra("addWallet").toString()
        walletC = intent.getStringExtra("walletC").toString()

        if (user == "user") {
            if (addWallet == "wallet") {

                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    val intent = Intent(this, AllBiddsDetailsActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 2000) // 2000 milliseconds = 2 seconds


            }else if(addWallet =="track")
            {
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    val intent = Intent(this, TrackActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 2000) // 2000 milliseconds = 2 seconds

            }
            else {
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    val intent = Intent(this, DriverWalletActivity::class.java)
                    startActivity(intent)
                    finish()

                }, 2000) // 2000 milliseconds = 2 seconds
            }
        } else {
            if (walletC == "amount") {

                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    val intent = Intent(this, AcceptRideActivity::class.java)
                    startActivity(intent)
                    finish()

                }, 2000) // 2000 milliseconds = 2 seconds


            } else {
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    val intent = Intent(this, DriverWalletActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 2000) // 2000 milliseconds = 2 seconds
            }
        }


    }

}