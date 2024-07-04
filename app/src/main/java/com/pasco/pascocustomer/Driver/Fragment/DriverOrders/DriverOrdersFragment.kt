package com.pasco.pascocustomer.Driver.Fragment.DriverOrders

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel.CancelOnClick
import com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel.CancelReasonResponse
import com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel.CancelReasonViewModel
import com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel.CurrentOrdersViewModel
import com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel.DAllOrderResponse
import com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel.DAllOrdersViewModel
import com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel.DriverCancelViewModel
import com.pasco.pascocustomer.Driver.adapter.CancellationReasonAdapter
import dagger.hilt.android.AndroidEntryPoint
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.activity.Driver.adapter.DriverAllBiddsAdapter
import com.pasco.pascocustomer.Driver.adapter.DriverHistoryAdapter
import com.pasco.pascocustomer.databinding.FragmentDriverOrdersBinding
import com.pasco.pascocustomer.utils.ErrorUtil

@AndroidEntryPoint
class DriverOrdersFragment : Fragment(),CancelOnClick{
    private lateinit var binding:FragmentDriverOrdersBinding
    private lateinit var activity: Activity
    private var driverHistory:List<DAllOrderResponse.DAllOrderResponseData> = ArrayList()
    private var cancelList:List<CancelReasonResponse.CancellationList> = ArrayList()
    private val dAllOrdersViewModel: DAllOrdersViewModel by viewModels()
    private val currentOrdersViewModel: CurrentOrdersViewModel by viewModels()
    private val cReasonViewModel: CancelReasonViewModel by viewModels()
    private val driveCancelReasonViewModel: DriverCancelViewModel by viewModels()
    private lateinit var dialog: BottomSheetDialog
    private lateinit var recycler_StatusList: RecyclerView
    private  var bID: String= ""
    private lateinit var staticTextViewEmptyData: TextView
    private val progressDialog by lazy { CustomProgressDialog(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDriverOrdersBinding.inflate(inflater, container, false)
        activity = requireActivity()
        allOrdersApi()
        allBiddsObserver()

        binding.allBiddsTextIdD.setOnClickListener {
            binding.allBiddsTextIdD.background = ContextCompat.getDrawable(requireActivity(), R.drawable.order_bidding_yellow)
            binding.currentOrderTextIdD.background = null
            binding.allBiddsTextIdD.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.currentOrderTextIdD.setTextColor(Color.parseColor("#FF000000"))
            allOrdersApi()
            allBiddsObserver()

        }
        binding.currentOrderTextIdD.setOnClickListener {
            binding.allBiddsTextIdD.background = null
            binding.currentOrderTextIdD.background = ContextCompat.getDrawable(requireActivity(), R.drawable.accept_bidd_background)
            binding.allBiddsTextIdD.setTextColor(Color.parseColor("#FF000000"))
            binding.currentOrderTextIdD.setTextColor(Color.parseColor("#FFFFFFFF"))
            currentOrdersApi()
            currentOrdersObserver()
        }

        return binding.root
    }

    private fun driverCancelReasonObserver() {
        driveCancelReasonViewModel.progressIndicator.observe(requireActivity(), Observer {
            // Handle progress indicator changes if needed
        })

        driveCancelReasonViewModel.mUrDriverResponse.observe(requireActivity()) { response ->
            val content = response.peekContent()
            val message = content.msg ?: return@observe


            if (response.peekContent().status == "False") {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                allOrdersApi()
                dialog.dismiss()
            }
        }
        driveCancelReasonViewModel.errorResponse.observe(requireActivity()) {
            // Handle general errors using ErrorUtil
            ErrorUtil.handlerGeneralError(requireActivity(), it)
        }
    }

    private fun currentOrdersApi() {
        currentOrdersViewModel.getCurrentOrdersData(
            progressDialog,
            activity
        )
    }

    private fun currentOrdersObserver() {
        currentOrdersViewModel.progressIndicator.observe(requireActivity(), Observer {
            // Handle progress indicator changes if needed
        })

        currentOrdersViewModel.mAllOrderResponse.observe(requireActivity()) { response ->
            val message = response.peekContent().msg!!
            driverHistory = response.peekContent().data ?: emptyList()

            if (response.peekContent().status == "False") {
                binding.bidingStatusNoDataTextView.visibility = View.VISIBLE
                binding.recycerHistoryList.visibility = View.GONE
                binding.recycerHistoryList.isVerticalScrollBarEnabled = true
                binding.recycerHistoryList.isVerticalFadingEdgeEnabled = true
                binding.recycerHistoryList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.recycerHistoryList.adapter = DriverHistoryAdapter(requireContext(), driverHistory)
                Toast.makeText(requireActivity(), "$message", Toast.LENGTH_LONG).show()
            } else {
                if (driverHistory.isEmpty()) {
                    //hello
                    binding.bidingStatusNoDataTextView.visibility = View.VISIBLE
                    binding.recycerHistoryList.visibility = View.GONE
                }
                else{
                    binding.bidingStatusNoDataTextView.visibility = View.GONE
                    binding.recycerHistoryList.visibility = View.VISIBLE
                    binding.recycerHistoryList.isVerticalScrollBarEnabled = true
                    binding.recycerHistoryList.isVerticalFadingEdgeEnabled = true
                    binding.recycerHistoryList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    binding.recycerHistoryList.adapter = DriverHistoryAdapter(requireContext(), driverHistory)
                    // Toast.makeText(this@BiddingDetailsActivity, message, Toast.LENGTH_SHORT).show()

                }

            }
        }

        currentOrdersViewModel.errorResponse.observe(requireActivity()) {
            ErrorUtil.handlerGeneralError(requireActivity(), it)
        }
    }

//hello
    private fun allBiddsObserver() {
        dAllOrdersViewModel.progressIndicator.observe(requireActivity(), Observer {
            // Handle progress indicator changes if needed
        })

        dAllOrdersViewModel.mAllOrderResponse.observe(requireActivity()) { response ->
            val message = response.peekContent().msg!!
            driverHistory = response.peekContent().data ?: emptyList()

            if (response.peekContent().status == "False") {
                binding.recycerHistoryList.isVerticalScrollBarEnabled = true
                binding.recycerHistoryList.isVerticalFadingEdgeEnabled = true
                binding.recycerHistoryList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.recycerHistoryList.adapter = DriverAllBiddsAdapter(requireContext(),this,driverHistory)
                Toast.makeText(requireActivity(), "$message", Toast.LENGTH_LONG).show()
            } else {
                if (driverHistory.isEmpty()) {
                    binding.bidingStatusNoDataTextView.visibility = View.VISIBLE
                    binding.recycerHistoryList.visibility = View.GONE
                }
                else{
                    binding.bidingStatusNoDataTextView.visibility = View.GONE
                    binding.recycerHistoryList.visibility = View.VISIBLE
                    binding.recycerHistoryList.isVerticalScrollBarEnabled = true
                    binding.recycerHistoryList.isVerticalFadingEdgeEnabled = true
                    binding.recycerHistoryList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    binding.recycerHistoryList.adapter = DriverAllBiddsAdapter(requireContext(),this,driverHistory)
                    // Toast.makeText(this@BiddingDetailsActivity, message, Toast.LENGTH_SHORT).show()
                }


            }
        }

        dAllOrdersViewModel.errorResponse.observe(requireActivity()) {
            ErrorUtil.handlerGeneralError(requireActivity(), it)
        }
    }

    private fun allOrdersApi() {
        dAllOrdersViewModel.getAllOrdersData(
            progressDialog,
            activity
        )
    }

    override fun onResume() {
        super.onResume()
        allOrdersApi()
        currentOrdersApi()
    }

    override fun cancelOrder(position: Int, bookID: String) {
       //call api
        bID = bookID
        selectStatusPopUp()
    }

    override fun cancelList(position: Int, ids: Int) {
        val ids = ids.toString()
        cancelReasonnn(ids)
        driverCancelReasonObserver()
    }

    private fun cancelReasonnn(ids: String) {
        driveCancelReasonViewModel.getUpdateDdStatusData(
            progressDialog,
            requireActivity(),bID,ids
        )
    }


    private fun selectStatusPopUp() {
        val dialogView = layoutInflater.inflate(R.layout.select_status_popup, null)
        dialog = BottomSheetDialog(requireContext())
        val backArrowCancelPopUp =
            dialogView.findViewById<ImageView>(R.id.backArrowCancelPopUp)
        recycler_StatusList = dialogView.findViewById(R.id.recycler_StatusList)
        staticTextViewEmptyData = dialogView.findViewById(R.id.staticTextViewEmptyData)
        dialog.setContentView(dialogView)
        dialog.show()
        cancelApi()
        backArrowCancelPopUp.setOnClickListener {
            dialog.dismiss()
        }
        cancelApiObsever(dialog)
    }

    private fun cancelApiObsever(dialog: BottomSheetDialog) {
        cReasonViewModel.progressIndicator.observe(this, Observer {
            // Handle progress indicator changes if needed
        })

        cReasonViewModel.mCancelOrderResponse.observe(this) { response ->
            val content = response.peekContent()
            val message = content.msg ?: return@observe
            cancelList = content.data!!


            if (response.peekContent().status == "False") {
            } else {
                if (cancelList.isEmpty()) {
                    //hello
                    staticTextViewEmptyData.visibility = View.VISIBLE
                    recycler_StatusList.visibility = View.GONE
                } else {
                    staticTextViewEmptyData.visibility = View.GONE
                    recycler_StatusList.visibility = View.VISIBLE
                    recycler_StatusList.isVerticalScrollBarEnabled = true
                    recycler_StatusList.isVerticalFadingEdgeEnabled = true
                    recycler_StatusList.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    recycler_StatusList.adapter = CancellationReasonAdapter(requireContext(),this, cancelList)
                    // Toast.makeText(this@BiddingDetailsActivity, message, Toast.LENGTH_SHORT).show()

                }
            }
        }
        cReasonViewModel.errorResponse.observe(requireActivity()) {
            // Handle general errors using ErrorUtil
            ErrorUtil.handlerGeneralError(requireActivity(), it)
        }
    }

    private fun cancelApi() {
        cReasonViewModel.getCancelReason(
            progressDialog,
            requireActivity()
        )
    }


}


