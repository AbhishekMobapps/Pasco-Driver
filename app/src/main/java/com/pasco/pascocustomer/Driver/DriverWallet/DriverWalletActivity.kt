package com.pasco.pascocustomer.Driver.DriverWallet

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustome.Driver.Customer.Fragment.CustomerWallet.AddAmountViewModel
import com.pasco.pascocustomer.Driver.AcceptRideDetails.Ui.AcceptRideActivity
import com.pasco.pascocustomer.ComlpleteStatusActivity
import com.pasco.pascocustomer.Driver.Customer.Fragment.CustomerWallet.GetAmountViewModel
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.databinding.ActivityDriverWalletBinding
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverWalletActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDriverWalletBinding
    private lateinit var dialog: AlertDialog
    private val addAmountViewModel: AddAmountViewModel by viewModels()
    private val getAmountViewModel: GetAmountViewModel by viewModels()
    private  var walletC: String= ""
    private val progressDialog by lazy { CustomProgressDialog(this) }

    private var amountP = ""
    private var addWallet = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)


        walletC  = intent.getStringExtra("wallet").toString()

        addWallet = intent.getStringExtra("addWallet").toString()

        binding.recycerEarningList.isVerticalScrollBarEnabled = true
        binding.recycerEarningList.isVerticalFadingEdgeEnabled = true
        binding.recycerEarningList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.withDrawBtn.setOnClickListener {
            openWithDrawPopUp()
        }
        getTotalAmount()
        getTotalAmountObserver()
    }


    private fun getTotalAmountObserver() {
        getAmountViewModel.mGetAmounttt.observe(this) { response ->
            val message = response.peekContent().msg!!
            val data = response.peekContent().data
            amountP = data?.walletAmount.toString()
            binding.accountBalanceDri.text = "$amountP USD"

            if (response.peekContent().status == "False") {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            } else {
                if (walletC.equals("amount"))
                {
                    val intent = Intent(this@DriverWalletActivity,AcceptRideActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        addAmountViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }



    @SuppressLint("MissingInflatedId")
    private fun openWithDrawPopUp() {
        val builder = AlertDialog.Builder(this, R.style.Style_Dialog_Rounded_Corner)
        val dialogView = layoutInflater.inflate(R.layout.withdrawpopup, null)
        builder.setView(dialogView)

        dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val waCrossImage = dialogView.findViewById<ImageView>(R.id.waCrossImage)
        val submit_WithDrawBtn = dialogView.findViewById<Button>(R.id.submit_WithDrawBtn)
        val amountWithdrawEditD = dialogView.findViewById<EditText>(R.id.amountWithdrawEditD)
        dialog.show()
        waCrossImage.setOnClickListener { dialog.dismiss() }
        submit_WithDrawBtn.setOnClickListener {
            //call api()
            addAmountViewModel.getAddAmountData(
                progressDialog,
                this,
                amountWithdrawEditD.text.toString()
            )
            //observer
            addMoneyObserver()
        }
    }

    private fun addMoneyObserver() {
        addAmountViewModel.mAddAmountResponse.observe(this) { response ->
            val message = response.peekContent().msg!!
            if (response.peekContent().status == "False") {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                val intent = Intent(this,ComlpleteStatusActivity::class.java)
                intent.putExtra("addWallet",addWallet)
                startActivity(intent)
                dialog.dismiss()
                getTotalAmount()

            }
        }
        addAmountViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }

    private fun getTotalAmount() {
        getAmountViewModel.getAmountData(progressDialog, this)
    }

    private fun getTotalAmountObserver() {
        getAmountViewModel.mGetAmounttt.observe(this) { response ->
            val message = response.peekContent().msg!!
            val status = response.peekContent().status!!
            val data = response.peekContent().data


            if (status == "False") {
                Log.e("WalletAmt", "aaa")
                val value = "0"
                binding.accountBalanceDri.text = "$value USD"
            } else {
                amountP = data?.walletAmount.toString()
                binding.accountBalanceDri.text = "$amountP USD"


            }
        }
        addAmountViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }
}