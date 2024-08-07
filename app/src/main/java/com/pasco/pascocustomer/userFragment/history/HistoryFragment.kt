package com.pasco.pascocustomer.userFragment.history

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.customer.activity.allbiddsdetailsactivity.model.AllBiddsDetailResponse
import com.pasco.pascocustomer.databinding.FragmentHistoryBinding
import com.pasco.pascocustomer.userFragment.history.complete.CancelledAdapter
import com.pasco.pascocustomer.userFragment.history.complete.CompleteHistoryResponse
import com.pasco.pascocustomer.userFragment.history.complete.CompleteModelView
import com.pasco.pascocustomer.userFragment.history.model.CustBookingCancelViewModel
import com.pasco.pascocustomer.userFragment.logoutmodel.LogOutModelView
import com.pasco.pascocustomer.userFragment.order.acceptedadapter.AcceptedAdapter
import com.pasco.pascocustomer.userFragment.order.acceptedmodel.AcceptedModelView
import com.pasco.pascocustomer.userFragment.order.odermodel.CustomerOrderBody
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.Objects

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var activity: Activity
    private var completeHistoryList: List<CompleteHistoryResponse.Datum> =
        ArrayList()
    private val cancelledTripViewModel: CustBookingCancelViewModel by viewModels()
    private val completedTripViewModel: CompleteModelView by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(activity) }
    private lateinit var sharedPreferencesLanguageName: SharedPreferences
    private var language = ""
    private var languageId = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val view = binding.root

        activity = requireActivity()

        sharedPreferencesLanguageName = activity.getSharedPreferences(
            "PREFERENCE_NAME",
            AppCompatActivity.MODE_PRIVATE
        )
        language = sharedPreferencesLanguageName.getString("language_text", "").toString()
        languageId = sharedPreferencesLanguageName.getString("languageId", "").toString()

        if (Objects.equals(language, "ar")) {
            binding.allBiddsConst.setBackgroundResource(R.drawable.accept_back)

            binding.allBiddsConst.setOnClickListener {
                binding.allBiddsConst.setBackgroundResource(R.drawable.accept_back)
                binding.acceptTxt.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                binding.biddsTxt.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                binding.oderRecycler.visibility = View.GONE
                binding.acceptRecycler.visibility = View.GONE
                binding.asAcceptConst.setBackgroundResource(0)

                completedApi()
            }


            binding.asAcceptConst.setOnClickListener {
                binding.asAcceptConst.setBackgroundResource(R.drawable.orders_tab_back)
                binding.acceptTxt.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                binding.biddsTxt.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                binding.oderRecycler.visibility = View.GONE
                binding.allBiddsRecycler.visibility = View.GONE
                binding.allBiddsConst.setBackgroundResource(0)
                cancelledApi()
            }
        } else {
            binding.allBiddsConst.setBackgroundResource(R.drawable.complete_button_back)
            binding.allBiddsConst.setOnClickListener {
                binding.allBiddsConst.setBackgroundResource(R.drawable.complete_button_back)
                binding.acceptTxt.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                binding.biddsTxt.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                binding.oderRecycler.visibility = View.GONE
                binding.acceptRecycler.visibility = View.GONE
                binding.asAcceptConst.setBackgroundResource(0)

                completedApi()
            }

            binding.asAcceptConst.setOnClickListener {
                binding.asAcceptConst.setBackgroundResource(R.drawable.accept_back)
                binding.acceptTxt.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                binding.biddsTxt.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                binding.oderRecycler.visibility = View.GONE
                binding.allBiddsRecycler.visibility = View.GONE
                binding.allBiddsConst.setBackgroundResource(0)
                cancelledApi()
            }
        }



        completedApi()
        completedObserver()
        cancelledObserver()
        return view
    }

    private fun cancelledApi() {
        val body = CustomerOrderBody(
            language = languageId
        )
        cancelledTripViewModel.driverTripCancelData(
            progressDialog,
            requireActivity(),
            body
        )
    }

    private fun cancelledObserver() {
        cancelledTripViewModel.progressIndicator.observe(requireActivity(), Observer {
            // Handle progress indicator changes if needed
        })

        cancelledTripViewModel.mCancelledHis.observe(requireActivity()) { response ->
            val message = response.peekContent().msg!!
            completeHistoryList = response.peekContent().data ?: emptyList()
            val msg = response.peekContent().msg
            Log.e("CancelTripAA", "aaaa.." + msg)

            if (response.peekContent().status == "False") {
                binding.noDataFoundTxt.visibility = View.VISIBLE
                binding.noDataFoundTxt.text = "You have not cancelled any trips yet."
                binding.oderRecycler.visibility = View.GONE
                //Toast.makeText(requireActivity(), "$message", Toast.LENGTH_LONG).show()
            } else {
                binding.noDataFoundTxt.visibility = View.GONE
                binding.oderRecycler.visibility = View.VISIBLE
                binding.oderRecycler.isVerticalScrollBarEnabled = true
                binding.oderRecycler.isVerticalFadingEdgeEnabled = true
                binding.oderRecycler.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.oderRecycler.adapter =
                    CancelledAdapter(
                        requireContext(),
                        completeHistoryList,
                        activity as AppCompatActivity, language
                    )
                // Toast.makeText(this@BiddingDetailsActivity, message, Toast.LENGTH_SHORT).show()

            }
        }

        cancelledTripViewModel.errorResponse.observe(requireActivity()) {
            ErrorUtil.handlerGeneralError(requireActivity(), it)
        }
    }

    private fun completedApi() {
        val body = CustomerOrderBody(
            language = languageId)
        completedTripViewModel.driverTripCancelData(
            progressDialog,
            requireActivity(),
            body
        )
    }

    private fun completedObserver() {
        completedTripViewModel.progressIndicator.observe(requireActivity(), Observer {
            // Handle progress indicator changes if needed
        })

        completedTripViewModel.mCancelledHis.observe(requireActivity()) { response ->
            val message = response.peekContent().msg!!
            completeHistoryList = response.peekContent().data ?: emptyList()
            Log.e("CancelTripAA", "aaaa..AAA" + message)

            if (response.peekContent().status == "False") {
                binding.noDataFoundTxt.visibility = View.VISIBLE
                binding.allBiddsRecycler.visibility = View.GONE
                //Toast.makeText(requireActivity(), "$message", Toast.LENGTH_LONG).show()
            } else {
                binding.noDataFoundTxt.visibility = View.GONE
                binding.allBiddsRecycler.visibility = View.VISIBLE
                binding.allBiddsRecycler.isVerticalScrollBarEnabled = true
                binding.allBiddsRecycler.isVerticalFadingEdgeEnabled = true
                binding.allBiddsRecycler.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.allBiddsRecycler.adapter =
                    CancelledAdapter(
                        requireContext(),
                        completeHistoryList,
                        activity as AppCompatActivity,
                        language
                    )
                // Toast.makeText(this@BiddingDetailsActivity, message, Toast.LENGTH_SHORT).show()

            }
        }

        completedTripViewModel.errorResponse.observe(requireActivity()) {
            ErrorUtil.handlerGeneralError(requireActivity(), it)
        }
    }


}