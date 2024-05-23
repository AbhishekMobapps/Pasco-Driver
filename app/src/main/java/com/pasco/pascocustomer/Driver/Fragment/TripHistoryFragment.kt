package com.pasco.pascocustomer.Driver.Fragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.databinding.FragmentTripHistoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TripHistoryFragment : Fragment() {
    private lateinit var binding:FragmentTripHistoryBinding
    private var refersh = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTripHistoryBinding.inflate(inflater, container, false)

        refersh = PascoApp.encryptedPrefs.token
        return binding.root
    }

}