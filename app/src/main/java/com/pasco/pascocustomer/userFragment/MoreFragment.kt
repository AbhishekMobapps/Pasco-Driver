package com.pasco.pascocustomer.userFragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.commonpage.login.LoginActivity
import com.pasco.pascocustomer.databinding.FragmentMoreBinding
import com.pasco.pascocustomer.userFragment.logoutmodel.LogOutModelView
import com.pasco.pascocustomer.userFragment.logoutmodel.LogoutBody
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoreFragment : Fragment() {
    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!
    private val cargoViewModel: LogOutModelView by viewModels()
    private var refresh = ""
    private lateinit var activity: Activity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMoreBinding.inflate(inflater, container, false)
        val view = binding.root

        activity = requireActivity()
        binding.logOutBtn.setOnClickListener { logOutApi() }


        refresh = PascoApp.encryptedPrefs.token
        logOutObserver()
        return view
    }

    private fun logOutApi() {

        val bookingBody = LogoutBody(
            refresh = refresh
        )
        cargoViewModel.otpCheck(bookingBody, activity)
    }

    private fun logOutObserver() {
        cargoViewModel.mRejectResponse.observe(requireActivity()) { response ->
            val message = response.peekContent().msg
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()

            if (response.peekContent().status == "True")
            {
                PascoApp.encryptedPrefs.bearerToken = ""
                PascoApp.encryptedPrefs.userId = ""
                PascoApp.encryptedPrefs.isFirstTime = true
                val intent = Intent(activity, LoginActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
            }



        }

        cargoViewModel.errorResponse.observe(requireActivity()) {
            ErrorUtil.handlerGeneralError(requireContext(), it)
        }
    }
}