package com.pasco.pascocustomer.userFragment.order

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel.CancelOnClick
import com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel.CancelReasonResponse
import com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel.CancelReasonViewModel
import com.pasco.pascocustomer.Driver.adapter.CancellationReasonAdapter
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.customer.activity.allbiddsdetailsactivity.model.AllBiddsDetailResponse
import com.pasco.pascocustomer.customer.activity.track.cancelbooking.CancelBookingBody
import com.pasco.pascocustomer.customer.activity.track.cancelbooking.CancelBookingModelView
import com.pasco.pascocustomer.databinding.FragmentOrderBinding
import com.pasco.pascocustomer.reminder.ReminderItemClick
import com.pasco.pascocustomer.userFragment.allbidds.AllBiddsAdapter
import com.pasco.pascocustomer.userFragment.allbidds.AllBiddsModelView
import com.pasco.pascocustomer.userFragment.order.acceptedadapter.AcceptedAdapter
import com.pasco.pascocustomer.userFragment.order.acceptedmodel.AcceptedModelView
import com.pasco.pascocustomer.userFragment.order.adapter.OrderAdapter
import com.pasco.pascocustomer.userFragment.order.odermodel.CustomerOrderBody
import com.pasco.pascocustomer.userFragment.order.odermodel.OrderModelView
import com.pasco.pascocustomer.userFragment.order.odermodel.OrderResponse
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class OrderFragment : Fragment(), ReminderItemClick, CancelOnClick {
    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!
    private lateinit var activity: Activity

    private var customAdapter: OrderAdapter? = null
    private var allBiddsAdapter: AllBiddsAdapter? = null
    private var acceptedAdapter: AcceptedAdapter? = null
    private val orderModelView: OrderModelView by viewModels()
    private val acceptedModelView: AcceptedModelView by viewModels()
    private val allBiddsModelView: AllBiddsModelView by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(activity) }
    private var isValue = false
    private var bookMarkList: List<OrderResponse.Datum> = ArrayList()
    private var allBiddsList: List<OrderResponse.Datum> = ArrayList()
    private var acceptedList: List<AllBiddsDetailResponse.Datum> = ArrayList()
    private val cancelBookingModelView: CancelBookingModelView by viewModels()

    private val cReasonViewModel: CancelReasonViewModel by viewModels()//
    private var cancelListA: List<CancelReasonResponse.CancellationList> = ArrayList()
    private lateinit var recycler_StatusList: RecyclerView
    private var orderId: String = ""
    private lateinit var staticTextViewEmptyData: TextView
    private var dialog: Dialog? = null
    private lateinit var sharedPreferencesLanguageName: SharedPreferences
    private var language = ""
    private var languageId = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        val view = binding.root

        activity = requireActivity()

        sharedPreferencesLanguageName = activity.getSharedPreferences(
            "PREFERENCE_NAME", AppCompatActivity.MODE_PRIVATE
        )
        language = sharedPreferencesLanguageName.getString("language_text", "").toString()
        languageId = sharedPreferencesLanguageName.getString("languageId", "").toString()

        if (Objects.equals(language, "ar")) {
            binding.ordersConst.setBackgroundResource(R.drawable.accept_back)
            getOrderApi()

            binding.ordersConst.setOnClickListener {
                binding.ordersConst.setBackgroundResource(R.drawable.accept_back)
                binding.acceptTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                binding.biddsTxt.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
                binding.orderTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                binding.oderRecycler.visibility = View.VISIBLE
                binding.allBiddsRecycler.visibility = View.GONE
                binding.acceptRecycler.visibility = View.GONE
                binding.noDataFoundTxt.visibility = View.GONE
                binding.asAcceptConst.setBackgroundResource(0)
                binding.allBiddsConst.setBackgroundResource(0)
                getOrderApi()

                binding.asAcceptConst.setOnClickListener {
                    binding.asAcceptConst.setBackgroundResource(R.drawable.orders_tab_back)
                    binding.acceptTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    binding.orderTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    binding.biddsTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    binding.oderRecycler.visibility = View.GONE
                    binding.allBiddsRecycler.visibility = View.GONE
                    binding.acceptRecycler.visibility = View.VISIBLE
                    binding.noDataFoundTxt.visibility = View.GONE
                    binding.allBiddsConst.setBackgroundResource(0)
                    binding.ordersConst.setBackgroundResource(0)
                    getAcceptedApi()

                }

                binding.allBiddsConst.setOnClickListener {
                    binding.allBiddsConst.setBackgroundResource(R.drawable.all_bidds_back)
                    binding.acceptTxt.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                    binding.orderTxt.setTextColor(
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
                    binding.allBiddsRecycler.visibility = View.VISIBLE
                    binding.acceptRecycler.visibility = View.GONE
                    binding.noDataFoundTxt.visibility = View.GONE
                    binding.asAcceptConst.setBackgroundResource(0)
                    binding.ordersConst.setBackgroundResource(0)
                    getAllBiddsApi()
                }

            }
        } else {
            binding.ordersConst.setOnClickListener {
                binding.ordersConst.setBackgroundResource(R.drawable.orders_tab_back)
                binding.acceptTxt.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                binding.biddsTxt.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                binding.orderTxt.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                binding.oderRecycler.visibility = View.VISIBLE
                binding.allBiddsRecycler.visibility = View.GONE
                binding.acceptRecycler.visibility = View.GONE
                binding.noDataFoundTxt.visibility = View.GONE
                binding.asAcceptConst.setBackgroundResource(0)
                binding.allBiddsConst.setBackgroundResource(0)
                getOrderApi()

            }

            binding.allBiddsConst.setOnClickListener {
                binding.allBiddsConst.setBackgroundResource(R.drawable.all_bidds_back)
                binding.acceptTxt.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                binding.orderTxt.setTextColor(
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
                binding.allBiddsRecycler.visibility = View.VISIBLE
                binding.acceptRecycler.visibility = View.GONE
                binding.noDataFoundTxt.visibility = View.GONE
                binding.asAcceptConst.setBackgroundResource(0)
                binding.ordersConst.setBackgroundResource(0)
                getAllBiddsApi()
            }

            binding.asAcceptConst.setOnClickListener {
                binding.asAcceptConst.setBackgroundResource(R.drawable.accept_back)
                binding.acceptTxt.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                binding.orderTxt.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
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
                binding.acceptRecycler.visibility = View.VISIBLE
                binding.noDataFoundTxt.visibility = View.GONE
                binding.allBiddsConst.setBackgroundResource(0)
                binding.ordersConst.setBackgroundResource(0)
                getAcceptedApi()

            }

        }


        getOrderApi()
        orderObserver()
        allBiddsObserver()
        acceptedObserver()
        return view
    }


    private fun cancelApi() {
        val body = CustomerOrderBody(
            language = languageId
        )
        cReasonViewModel.getCancelReason(
            progressDialog,
            requireActivity(),
            body
        )
    }


    private fun cancelApiObserver() {
        cReasonViewModel.progressIndicator.observe(this, Observer {

        })

        cReasonViewModel.mCancelOrderResponse.observe(this) { response ->
            val content = response.peekContent()
            val message = content.msg ?: return@observe
            cancelListA = content.data!!


            if (response.peekContent().status == "False") {
            } else {
                if (cancelListA.isEmpty()) {
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
                    recycler_StatusList.adapter =
                        CancellationReasonAdapter(requireContext(), this, cancelListA)
                    // Toast.makeText(this@BiddingDetailsActivity, message, Toast.LENGTH_SHORT).show()

                }
            }
        }
        cReasonViewModel.errorResponse.observe(requireActivity()) {
            // Handle general errors using ErrorUtil
            ErrorUtil.handlerGeneralError(requireActivity(), it)
        }
    }


    private fun getOrderApi() {
        val body = CustomerOrderBody(
            language = languageId
        )
        orderModelView.otpCheck(activity, progressDialog, body)
    }

    private fun orderObserver() {
        orderModelView.progressIndicator.observe(requireActivity()) {
        }
        orderModelView.mRejectResponse.observe(requireActivity()) {
            val message = it.peekContent().msg
            val success = it.peekContent().status
            if (success == "True") {
                bookMarkList = it.peekContent().data!!
                if (bookMarkList.isEmpty()) {
                    binding.oderRecycler.visibility = View.GONE
                    binding.noDataFoundTxt.visibility = View.VISIBLE
                    binding.noDataFoundTxt.text = getString(R.string.no_Orders)
                } else {
                    binding.oderRecycler.visibility = View.VISIBLE
                    binding.oderRecycler.isVerticalScrollBarEnabled = true
                    binding.oderRecycler.isVerticalFadingEdgeEnabled = true
                    binding.oderRecycler.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    customAdapter = OrderAdapter(requireContext(), bookMarkList, this)
                    binding.oderRecycler.adapter = customAdapter
                }

            }
        }

        orderModelView.errorResponse.observe(requireActivity()) {
            ErrorUtil.handlerGeneralError(requireActivity(), it)
            //errorDialogs()
        }
    }


    private fun getAllBiddsApi() {
        val body = CustomerOrderBody(
            language = languageId
        )
        allBiddsModelView.otpCheck(activity, progressDialog, body)
    }

    private fun allBiddsObserver() {
        allBiddsModelView.progressIndicator.observe(requireActivity()) {
        }
        allBiddsModelView.mRejectResponse.observe(requireActivity()) {
            val message = it.peekContent().msg
            val success = it.peekContent().status
            if (success == "True") {
                allBiddsList = it.peekContent().data!!
                if (allBiddsList.isEmpty()) {
                    binding.noDataFoundTxt.visibility = View.VISIBLE
                    binding.noDataFoundTxt.text = getString(R.string.no_bids)
                } else {
                    binding.allBiddsRecycler.visibility = View.VISIBLE
                    binding.allBiddsRecycler.isVerticalScrollBarEnabled = true
                    binding.allBiddsRecycler.isVerticalFadingEdgeEnabled = true
                    binding.allBiddsRecycler.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    allBiddsAdapter = AllBiddsAdapter(requireContext(), allBiddsList, language)
                    binding.allBiddsRecycler.adapter = allBiddsAdapter
                }

            }
        }

        allBiddsModelView.errorResponse.observe(requireActivity()) {
            ErrorUtil.handlerGeneralError(requireActivity(), it)
            //errorDialogs()
        }
    }


    private fun getAcceptedApi() {
        val body = CustomerOrderBody(
            language = languageId
        )
        acceptedModelView.acceptedBids(activity, progressDialog, body)
    }

    private fun acceptedObserver() {
        acceptedModelView.progressIndicator.observe(requireActivity()) {
        }
        acceptedModelView.mRejectResponse.observe(requireActivity()) {
            val message = it.peekContent().msg
            val success = it.peekContent().status
            if (success == "True") {
                acceptedList = it.peekContent().data!!
                if (acceptedList.isEmpty()) {
                    binding.noDataFoundTxt.visibility = View.VISIBLE
                    binding.noDataFoundTxt.text = getString(R.string.No_Accepted)
                } else {
                    binding.acceptRecycler.visibility = View.VISIBLE
                    binding.acceptRecycler.isVerticalScrollBarEnabled = true
                    binding.acceptRecycler.isVerticalFadingEdgeEnabled = true
                    binding.acceptRecycler.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    acceptedAdapter = AcceptedAdapter(requireContext(), acceptedList, language)
                    binding.acceptRecycler.adapter = acceptedAdapter
                }

            }
        }

        acceptedModelView.errorResponse.observe(requireActivity()) {
            ErrorUtil.handlerGeneralError(requireActivity(), it)
            //errorDialogs()
        }
    }

    override fun reminderItemClick(position: Int, id: Int) {
        orderId = id.toString()
        showCancelPopup()
    }


    override fun cancelOrder(position: Int, bookingID: String) {

    }

    override fun cancelList(position: Int, id: String) {
        cancelBooking(id)
        cancelBookingObserver()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun showCancelPopup() {
        dialog = Dialog(requireContext())
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(true)
        dialog?.setContentView(R.layout.cancel_booking)


        recycler_StatusList = dialog?.findViewById(R.id.recycler_StatusList)!!
        staticTextViewEmptyData = dialog?.findViewById(R.id.staticTextViewEmptyData)!!

        val window = dialog?.window
        val lp = window?.attributes
        if (lp != null) {
            lp.width = ActionBar.LayoutParams.MATCH_PARENT
        }
        if (lp != null) {
            lp.height = ActionBar.LayoutParams.WRAP_CONTENT
        }
        if (window != null) {
            window.attributes = lp
        }
        cancelApi()
        cancelApiObserver()
        dialog?.show()
    }

    private fun cancelBooking(id: String) {
        val loinBody = CancelBookingBody(
            reasonid = id,
            language = languageId
        )
        cancelBookingModelView.cancelBooking(orderId, loinBody, activity, progressDialog)
    }

    private fun cancelBookingObserver() {
        cancelBookingModelView.progressIndicator.observe(this) {}
        cancelBookingModelView.mRejectResponse.observe(
            this
        ) {
            val msg = it.peekContent().msg
            val status = it.peekContent().status

            if (status == "True") {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                getOrderApi()
                dialog?.dismiss()
            } else {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }

        }
        cancelBookingModelView.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(requireContext(), it)
            // errorDialogs()
        }
    }


}