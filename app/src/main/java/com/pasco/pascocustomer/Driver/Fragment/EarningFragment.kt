package com.pasco.pascocustomer.Driver.Fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustome.Driver.Customer.Fragment.CustomerWallet.AddAmountViewModel
import com.pasco.pascocustomer.Driver.Customer.Fragment.CustomerWallet.GetAmountViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.databinding.FragmentEarningBinding
import com.pasco.pascocustomer.utils.ErrorUtil


@AndroidEntryPoint
class EarningFragment : Fragment() {
    private lateinit var binding: FragmentEarningBinding
    private lateinit var dialog: AlertDialog
    private val addAmountViewModel: AddAmountViewModel by viewModels()
    private val getAmountViewModel: GetAmountViewModel by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(requireActivity()) }

    private var amountP = ""
    //private var onEarningList: List<EarningResponse> = ArrayList()
  //  private var onCreditList: List<CheckStatusResponse> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEarningBinding.inflate(inflater, container, false)

        binding.recycerEarningList.isVerticalScrollBarEnabled = true
        binding.recycerEarningList.isVerticalFadingEdgeEnabled = true
        binding.recycerEarningList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
      //  binding.recycerEarningList.adapter = EarningAdapter(requireContext(), onEarningList)

       /* binding.crediText.setOnClickListener {
            binding.crediText.background =
                requireActivity().resources.getDrawable(R.drawable.debit_background)
            binding.historyText.background =
                requireActivity().resources.getDrawable(R.drawable.creditback)
            binding.crediText.setTextColor(Color.parseColor("#FFFFFF"))
            binding.historyText.setTextColor(Color.parseColor("#383F45"))
            binding.recycerEarningList.isVerticalScrollBarEnabled = true
            binding.recycerEarningList.isVerticalFadingEdgeEnabled = true
            binding.recycerEarningList.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.recycerEarningList.adapter = CreditAmountAdapter(requireContext(), onCreditList)
        }

        binding.historyText.setOnClickListener {
            binding.crediText.background =
                requireActivity().resources.getDrawable(R.drawable.creditback)
            binding.historyText.background =
                requireActivity().resources.getDrawable(R.drawable.debit_background)
            binding.historyText.setTextColor(Color.parseColor("#FFFFFF"))
            binding.crediText.setTextColor(Color.parseColor("#383F45"))
            binding.recycerEarningList.isVerticalScrollBarEnabled = true
            binding.recycerEarningList.isVerticalFadingEdgeEnabled = true
            binding.recycerEarningList.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.recycerEarningList.adapter = EarningAdapter(requireContext(), onEarningList)
        }*/

        binding.withDrawBtn.setOnClickListener {
            openWithDrawPopUp()
        }
        getTotalAmount()
        getTotalAmountObserver()
        return binding.root
    }

    private fun getTotalAmountObserver() {
        getAmountViewModel.mGetAmounttt.observe(viewLifecycleOwner) { response ->
            val message = response.peekContent().msg!!
            val data = response.peekContent().data
            amountP = data?.walletAmount.toString()
            binding.accountBalanceDri.text = "$amountP USD"

            if (response.peekContent().status == "False") {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            } else {
                // Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
        addAmountViewModel.errorResponse.observe(viewLifecycleOwner) {
            ErrorUtil.handlerGeneralError(requireActivity(), it)
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun openWithDrawPopUp() {
        val builder = AlertDialog.Builder(requireContext(), R.style.Style_Dialog_Rounded_Corner)
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
                requireActivity(),
                amountWithdrawEditD.text.toString()
            )
            //observer
            addMoneyObserver()
        }
    }

    private fun addMoneyObserver() {
        addAmountViewModel.mAddAmountResponse.observe(viewLifecycleOwner) { response ->
            val message = response.peekContent().msg!!
            if (response.peekContent().status == "False") {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                getTotalAmount()

            }
        }
        addAmountViewModel.errorResponse.observe(viewLifecycleOwner) {
            ErrorUtil.handlerGeneralError(requireActivity(), it)
        }
    }

    private fun getTotalAmount() {
        getAmountViewModel.getAmountData(progressDialog, requireActivity())
    }
}
