package com.pasco.pascocustomer.Driver.DriverWallet

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustome.Driver.Customer.Fragment.CustomerWallet.AddAmountViewModel
import com.pasco.pascocustome.Driver.Customer.Fragment.CustomerWallet.GetAmountResponse
import com.pasco.pascocustomer.ComlpleteStatusActivity
import com.pasco.pascocustomer.Driver.Customer.Fragment.CustomerWallet.GetAmountViewModel
import com.pasco.pascocustomer.Driver.DriverWallet.wallethistory.GetAddWalletDataBody
import com.pasco.pascocustomer.Driver.DriverWallet.wallethistory.TransactionHistoryAdapter
import com.pasco.pascocustomer.Driver.DriverWallet.withdraw.WithdrawAmountBody
import com.pasco.pascocustomer.Driver.DriverWallet.withdraw.WithdrawAmountViewModel
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.databinding.ActivityDriverWalletBinding
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverWalletActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDriverWalletBinding
    private lateinit var dialog: AlertDialog
    private val addAmountViewModel: AddAmountViewModel by viewModels()
    private val getAmountViewModel: GetAmountViewModel by viewModels()
    private val withdrawAmountViewModel: WithdrawAmountViewModel by viewModels()
    private lateinit var withdrawAmountBody: WithdrawAmountBody
    private var walletC: String = ""
    private var userType: String = ""
    private val progressDialog by lazy { CustomProgressDialog(this) }

    private var amountP = ""
    private var addWallet = ""
    private var itemValue = ""
    private var transactionHistoryAdapter: TransactionHistoryAdapter? = null
    private var transactionList: List<GetAmountResponse.Transaction> = ArrayList()


    private val chooseLanguageList = ArrayList<String>()
    private val strLangList = java.util.ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        walletC = intent.getStringExtra("wallet").toString()

        userType = PascoApp.encryptedPrefs.userType

        binding.backArrowImgBhdDetails.setOnClickListener {
            finish()
        }

      

        if (userType == "driver")
        {
           binding.withdrawAmountBtn.visibility = View.VISIBLE
           binding.linearTransactionLimitt.visibility = View.GONE
           binding.linearTransactionLimitMyTransaction.visibility = View.VISIBLE
           binding.consTopDesign.visibility = View.VISIBLE

            binding.withdrawAmountBtn.setOnClickListener {
               addWithPop()
            }
        }
        else{
 
            binding.withdrawAmountBtn.visibility = View.GONE
            binding.linearTransactionLimitt.visibility = View.VISIBLE
            binding.linearTransactionLimitMyTransaction.visibility = View.GONE
            binding.consTopDesign.visibility = View.GONE
        }


 
        binding.addBtn.setOnClickListener {
            openWithDrawPopUp()
        }


        chooseLanguageList.add("Credit")
        chooseLanguageList.add("Debit")

        //Spinner Adapter
        val dAdapter = spinnerAdapter(this, R.layout.custom_spinner_two, strLangList)
        dAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dAdapter.add("Filter")
        dAdapter.addAll(chooseLanguageList)
        binding.spinnerFilter.adapter = dAdapter



        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?, view: View?, i: Int, l: Long
            ) {
                //Toast.makeText(requireActivity(), "Country Spinner Working **********", Toast.LENGTH_SHORT).show()
                itemValue = binding.spinnerFilter.selectedItem.toString()

                if (itemValue == getString(R.string.select_event)) {

                } else {
                    getTotalAmount()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
        }

        getTotalAmount()
        getTotalAmountObserver()
        // getTotalDriverAmountObserver()
    }

    @SuppressLint("MissingInflatedId")
    private fun addWithPop() {
        val builder = AlertDialog.Builder(this, R.style.Style_Dialog_Rounded_Corner)
        val dialogView = layoutInflater.inflate(R.layout.withdrawpopup, null)
        builder.setView(dialogView)

        dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val waCrossImage = dialogView.findViewById<ImageView>(R.id.waCrossImage)
        val submit_WithDrawBtn = dialogView.findViewById<Button>(R.id.submit_WithDrawBtn)
        val amountWithdrawEditD = dialogView.findViewById<EditText>(R.id.amountWithdrawEditD)
        val addAmStaticTextview = dialogView.findViewById<TextView>(R.id.addAmStaticTextview)
        addAmStaticTextview.text = "Withdraw Amount"
        dialog.show()
        waCrossImage.setOnClickListener { dialog.dismiss() }
        submit_WithDrawBtn.setOnClickListener {
            withdrawAmountBody = WithdrawAmountBody(
                amountWithdrawEditD.text.toString()
            )
            //call api()
            withdrawAmountViewModel.getWithdrawData(
                progressDialog,
                this,
                withdrawAmountBody
            )
            //observer
            addMoneyObserver()
        }


    @SuppressLint("MissingInflatedId")
    private fun addWithPop() {
        val builder = AlertDialog.Builder(this, R.style.Style_Dialog_Rounded_Corner)
        val dialogView = layoutInflater.inflate(R.layout.withdrawpopup, null)
        builder.setView(dialogView)

        dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val waCrossImage = dialogView.findViewById<ImageView>(R.id.waCrossImage)
        val submit_WithDrawBtn = dialogView.findViewById<Button>(R.id.submit_WithDrawBtn)
        val amountWithdrawEditD = dialogView.findViewById<EditText>(R.id.amountWithdrawEditD)
        val addAmStaticTextview = dialogView.findViewById<TextView>(R.id.addAmStaticTextview)
        addAmStaticTextview.text = "Withdraw Amount"
        dialog.show()
        waCrossImage.setOnClickListener { dialog.dismiss() }

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
            withdrawMoneyObserver()
            addMoneyObserver()
        }
    }

    private fun withdrawMoneyObserver() {
        withdrawAmountViewModel.mGetWithdrawList.observe(this) { response ->
            val message = response.peekContent().msg!!
            if (response.peekContent().status == "False") {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                getTotalAmount()

            }
        }
        addAmountViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }

    private fun addMoneyObserver() {
        addAmountViewModel.mAddAmountResponse.observe(this) { response ->
            val message = response.peekContent().msg!!
            if (response.peekContent().status == "False") {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                val intent = Intent(this, ComlpleteStatusActivity::class.java)
                intent.putExtra("addWallet", addWallet)
                intent.putExtra("walletC", walletC)
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
        val body = GetAddWalletDataBody(

            transaction_type = itemValue
        )

        getAmountViewModel.getAmountData(progressDialog, this, body)

    }

    private fun getTotalAmountObserver() {
        getAmountViewModel.mGetAmounttt.observe(this) { response ->
            val message = response.peekContent().msg!!
            val status = response.peekContent().status!!
            val data = response.peekContent().data
            transactionList = response.peekContent().data?.transactions!!


            if (status == "False") {
                Log.e("WalletAmt", "aaa")
                val value = "0"
                binding.accountBalanceDri.text = "$value USD"
            } else {
                amountP = data?.walletAmount.toString()
                binding.accountBalanceDri.text = "$amountP USD"

                binding.recycerEarningList.visibility = View.VISIBLE
                binding.recycerEarningList.isVerticalScrollBarEnabled = true
                binding.recycerEarningList.isVerticalFadingEdgeEnabled = true
                binding.recycerEarningList.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                transactionHistoryAdapter = TransactionHistoryAdapter(this, transactionList)
                binding.recycerEarningList.adapter = transactionHistoryAdapter

            }
        }
        addAmountViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }

    }
}

    class spinnerAdapter constructor(
        context: Context, textViewResourceId: Int, strInterestedList: List<String>
    ) : ArrayAdapter<String?>(context, textViewResourceId)

}


