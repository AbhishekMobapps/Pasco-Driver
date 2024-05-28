package com.pasco.pascocustomer.userFragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.databinding.FragmentHistoryBinding
import com.pasco.pascocustomer.databinding.FragmentMoreBinding
import com.pasco.pascocustomer.userFragment.logoutmodel.LogOutModelView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val logoutViewModel: LogOutModelView by viewModels()
    private var refresh = ""
    private lateinit var activity: Activity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.ordersConst.setOnClickListener {
            binding.ordersConst.setBackgroundResource(R.drawable.orders_tab_back)
            binding.acceptTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.biddsTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.orderTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.oderRecycler.visibility = View.VISIBLE
            binding.allBiddsRecycler.visibility = View.GONE
            binding.acceptRecycler.visibility = View.GONE
            binding.asAcceptConst.setBackgroundResource(0)
            binding.allBiddsConst.setBackgroundResource(0)


        }

        binding.allBiddsConst.setOnClickListener {
            binding.allBiddsConst.setBackgroundResource(R.drawable.all_bidds_back)
            binding.acceptTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.orderTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.biddsTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.oderRecycler.visibility = View.GONE
            binding.allBiddsRecycler.visibility = View.VISIBLE
            binding.acceptRecycler.visibility = View.GONE
            binding.asAcceptConst.setBackgroundResource(0)
            binding.ordersConst.setBackgroundResource(0)

        }

        binding.asAcceptConst.setOnClickListener {
            binding.asAcceptConst.setBackgroundResource(R.drawable.accept_back)
            binding.acceptTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.orderTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.biddsTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.oderRecycler.visibility = View.GONE
            binding.allBiddsRecycler.visibility = View.GONE
            binding.acceptRecycler.visibility = View.VISIBLE
            binding.allBiddsConst.setBackgroundResource(0)
            binding.ordersConst.setBackgroundResource(0)


        }
        return view
    }


}