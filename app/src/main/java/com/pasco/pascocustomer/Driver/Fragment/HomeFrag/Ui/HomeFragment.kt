package com.pasco.pascocustomer.Driver.Fragment.HomeFrag.Ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasco.pascocustomer.Driver.Fragment.HomeFrag.ViewModel.ShowBookingReqResponse
import com.pasco.pascocustomer.Driver.Fragment.HomeFrag.ViewModel.ShowBookingReqViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.pasco.pascocustomer.Driver.DriverMessageActivity
import com.pasco.pascocustomer.Driver.EmergencyResponse.Ui.EmergencyCallActivity
import com.pasco.pascocustomer.Driver.NotesRemainders.Ui.NotesRemainderActivity
import com.pasco.pascocustomer.Driver.UpdateLocation.Ui.UpdateLocationActivity
import com.pasco.pascocustomer.Driver.adapter.AcceptRideAdapter
import com.pasco.pascocustomer.databinding.FragmentHomeDriverBinding

import java.util.ArrayList
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeDriverBinding
    private var rideRequestList: List<ShowBookingReqResponse.ShowBookingReqData> = ArrayList()
    @Inject lateinit var activity: Activity // Injecting activity
    private val showBookingReqViewModel: ShowBookingReqViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeDriverBinding.inflate(inflater, container, false)
        binding.NotesDriHome.setOnClickListener {
            val intent = Intent(requireContext(), NotesRemainderActivity::class.java)
            startActivity(intent)
        }

        binding.supportsLinearDri.setOnClickListener {
            val intent = Intent(requireContext(), DriverMessageActivity::class.java)
            startActivity(intent)
        }
        binding.linearDriHEmergency.setOnClickListener {
            val intent = Intent(requireContext(), EmergencyCallActivity::class.java)
            startActivity(intent)
        }
        binding.LinearWallHomeF.setOnClickListener {
            val intent = Intent(requireContext(), UpdateLocationActivity::class.java)
            startActivity(intent)
        }
        //call api
        showRideRequestApi()
        setupObservers()
        return binding.root
    }

    private fun showRideRequestApi() {
        showBookingReqViewModel.getShowBookingRequestsData(
            activity
        )
    }

    private fun setupObservers() {
        showBookingReqViewModel.mShowBookingReq.observe(viewLifecycleOwner) { response ->
            val message = response.peekContent().msg
            rideRequestList = response.peekContent().data ?: emptyList()

            if (rideRequestList.isEmpty()) {
                binding.orderBidsHomeFragTextView.visibility = View.VISIBLE
                binding.recycerRideRequest.visibility = View.GONE
            } else {
                binding.orderBidsHomeFragTextView.visibility = View.GONE
                binding.recycerRideRequest.visibility = View.VISIBLE
                setupRecyclerView()
            }

            if (response.peekContent().status == "False") {
                Toast.makeText(requireContext(), "Failed: $message", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupRecyclerView() {
        with(binding.recycerRideRequest) {
            isVerticalScrollBarEnabled = true
            isVerticalFadingEdgeEnabled = true
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = AcceptRideAdapter(requireContext(), rideRequestList)
        }
    }

    override fun onResume() {
        super.onResume()
        //call api
        showRideRequestApi()
    }
}
