package com.pasco.pascocustomer.Driver.Fragment

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.DriverDashboard.Ui.DriverDashboardActivity
import com.pasco.pascocustomer.Driver.Fragment.DriverTripHistory.AddFeedbackOnClickListner
import com.pasco.pascocustomer.Driver.Fragment.DriverTripHistory.CancelledTripResponse
import com.pasco.pascocustomer.Driver.Fragment.DriverTripHistory.CancelledTripViewModel
import com.pasco.pascocustomer.Driver.Fragment.DriverTripHistory.CompletedTripHistoryResponse
import com.pasco.pascocustomer.Driver.Fragment.DriverTripHistory.CompletedTripHistoryViewModel
import com.pasco.pascocustomer.Driver.adapter.CancelledTripHistoryAdapter
import com.pasco.pascocustomer.Driver.adapter.CompletedTripHistoryAdapter
import com.pasco.pascocustomer.Driver.driverFeedback.DriverFeedbackBody
import com.pasco.pascocustomer.Driver.driverFeedback.DriverFeedbackModelView
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.databinding.FragmentTripHistoryBinding
import com.pasco.pascocustomer.userFragment.order.odermodel.CustomerOrderBody
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class TripHistoryFragment : Fragment(), AddFeedbackOnClickListner {
    private lateinit var activity: Activity
    private lateinit var binding: FragmentTripHistoryBinding
    private var driverTripHistory: List<CompletedTripHistoryResponse.DriverTripHistoryData> =
        ArrayList()
    private var cancelledTrips: List<CancelledTripResponse.CancelledData> = ArrayList()
    private var refersh = ""
    private val completedTripHistoryViewModel: CompletedTripHistoryViewModel by viewModels()
    private val cancelledTripViewModel: CancelledTripViewModel by viewModels()
    private val driverFeedbackModelView: DriverFeedbackModelView by viewModels()
    var bottomSheetDialog1: BottomSheetDialog? = null
    private val progressDialog by lazy { CustomProgressDialog(requireActivity()) }

    private lateinit var sharedPreferencesLanguageName: SharedPreferences
    private var language = ""
    private var languageId = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTripHistoryBinding.inflate(inflater, container, false)
        refersh = PascoApp.encryptedPrefs.token

        activity = requireActivity()
        completedApi()
        completedObserver()
        feedbackObserver()

        sharedPreferencesLanguageName = activity.getSharedPreferences(
            "PREFERENCE_NAME",
            AppCompatActivity.MODE_PRIVATE
        )
        language = sharedPreferencesLanguageName.getString("language_text", "").toString()
        languageId = sharedPreferencesLanguageName.getString("language_text", "").toString()

        if (Objects.equals(language, "ar")) {
            binding.completedHisTextview.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.accept_bidd_background)

            binding.completedHisTextview.setOnClickListener {
                binding.completedHisTextview.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.accept_bidd_background)
                binding.cancelledHisTextview.background = null
                binding.completedHisTextview.setTextColor(Color.parseColor("#FFFFFFFF"))
                binding.cancelledHisTextview.setTextColor(Color.parseColor("#FF000000"))
                completedApi()
                completedObserver()

            }

            binding.cancelledHisTextview.setOnClickListener {
                binding.completedHisTextview.background = null
                binding.cancelledHisTextview.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.order_bidding_yellow)
                binding.completedHisTextview.setTextColor(Color.parseColor("#FF000000"))
                binding.cancelledHisTextview.setTextColor(Color.parseColor("#FFFFFFFF"))
                cancelledApi()
                cancelledObserver()
            }
        } else {
            binding.completedHisTextview.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.order_bidding_yellow)
            binding.completedHisTextview.setOnClickListener {
                binding.completedHisTextview.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.order_bidding_yellow)
                binding.cancelledHisTextview.background = null
                binding.completedHisTextview.setTextColor(Color.parseColor("#FFFFFFFF"))
                binding.cancelledHisTextview.setTextColor(Color.parseColor("#FF000000"))
                completedApi()
                completedObserver()

            }
            binding.cancelledHisTextview.setOnClickListener {
                binding.completedHisTextview.background = null
                binding.cancelledHisTextview.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.accept_bidd_background)
                binding.completedHisTextview.setTextColor(Color.parseColor("#FF000000"))
                binding.cancelledHisTextview.setTextColor(Color.parseColor("#FFFFFFFF"))
                cancelledApi()
                cancelledObserver()
            }
        }

        return binding.root
    }

    private fun feedbackObserver() {
        driverFeedbackModelView.progressIndicator.observe(requireActivity()) {}
        driverFeedbackModelView.mRejectResponse.observe(
            requireActivity()
        ) {
            val msg = it.peekContent().msg
            val status = it.peekContent().status
            Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
            if (status == "False") {

            } else {
                val intent = Intent(requireActivity(), DriverDashboardActivity::class.java)
                startActivity(intent)
            }


        }
        driverFeedbackModelView.errorResponse.observe(requireActivity()) {
            ErrorUtil.handlerGeneralError(requireActivity(), it)
            // errorDialogs()
        }
    }


    private fun cancelledApi() {
        val body = CustomerOrderBody(
            language = language
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
            cancelledTrips = response.peekContent().data ?: emptyList()

            if (response.peekContent().status == "False") {
                //Toast.makeText(requireActivity(), "$message", Toast.LENGTH_LONG).show()
            } else {
                if (cancelledTrips.isEmpty()) {
                    binding.staticCTextview.visibility = View.VISIBLE
                    binding.staticCTextview.text = "No cancellation data has been found."
                    binding.recycerHistoryDriverList.visibility = View.GONE
                } else {
                    binding.staticCTextview.visibility = View.GONE
                    binding.recycerHistoryDriverList.visibility = View.VISIBLE
                    binding.recycerHistoryDriverList.isVerticalScrollBarEnabled = true
                    binding.recycerHistoryDriverList.isVerticalFadingEdgeEnabled = true
                    binding.recycerHistoryDriverList.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    binding.recycerHistoryDriverList.adapter =
                        CancelledTripHistoryAdapter(requireContext(), cancelledTrips)

                }

            }
        }

        cancelledTripViewModel.errorResponse.observe(requireActivity()) {
            ErrorUtil.handlerGeneralError(requireActivity(), it)
        }
    }

    private fun completedApi() {
        val body = CustomerOrderBody(
            language = languageId
        )
        completedTripHistoryViewModel.driverTripHisData(
            progressDialog,
            requireActivity(),
            body
        )
    }

    private fun completedObserver() {
        completedTripHistoryViewModel.progressIndicator.observe(requireActivity(), Observer {
            // Handle progress indicator changes if needed
        })

        completedTripHistoryViewModel.mGetServices.observe(requireActivity()) { response ->
            val message = response.peekContent().msg!!
            driverTripHistory = response.peekContent().data ?: emptyList()

            if (response.peekContent().status == "False") {
                //Toast.makeText(requireActivity(), "$message", Toast.LENGTH_LONG).show()
            } else {
                if (driverTripHistory.isEmpty()) {
                    binding.staticCTextview.visibility = View.VISIBLE
                    binding.recycerHistoryDriverList.visibility = View.GONE
                } else {
                    binding.staticCTextview.visibility = View.GONE
                    binding.recycerHistoryDriverList.visibility = View.VISIBLE
                    binding.recycerHistoryDriverList.isVerticalScrollBarEnabled = true
                    binding.recycerHistoryDriverList.isVerticalFadingEdgeEnabled = true
                    binding.recycerHistoryDriverList.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    binding.recycerHistoryDriverList.adapter =
                        CompletedTripHistoryAdapter(requireContext(), driverTripHistory)
                    // Toast.makeText(this@BiddingDetailsActivity, message, Toast.LENGTH_SHORT).show()

                }

            }
        }

        completedTripHistoryViewModel.errorResponse.observe(requireActivity()) {
            ErrorUtil.handlerGeneralError(requireActivity(), it)
        }
    }


    override fun onResume() {
        super.onResume()
        completedApi()
    }

    override fun addFeedbackItemClick(
        position: Int,
        id: Int,
        comment: String,
        ratingBars: String,
        bottomSheetDialog: BottomSheetDialog
    ) {
        feedbackApi(id, comment, ratingBars)
    }

    private fun feedbackApi(id: Int, comment: String, ratingBars: String) {
        val loinBody = DriverFeedbackBody(
            bookingconfirmation = id.toString(),
            rating = ratingBars,
            feedback = comment,
            language = languageId
        )
        driverFeedbackModelView.cancelBooking(loinBody, requireActivity(), progressDialog)
    }

}