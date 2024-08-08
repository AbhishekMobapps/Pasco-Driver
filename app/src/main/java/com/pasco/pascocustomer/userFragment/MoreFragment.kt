package com.pasco.pascocustomer.userFragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.pasco.pascocustomer.Driver.ContactWithUsActivity
import com.pasco.pascocustomer.Driver.DriverWallet.DriverWalletActivity
import com.pasco.pascocustomer.Driver.NotesRemainders.Ui.NotesRemainderActivity
import com.pasco.pascocustomer.Driver.adapter.TermsAndConditionsActivity
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.commonpage.login.LoginActivity
import com.pasco.pascocustomer.customer.activity.ChooseLanguageActivity
import com.pasco.pascocustomer.customer.activity.LanguageActivity
import com.pasco.pascocustomer.databinding.FragmentMoreBinding
import com.pasco.pascocustomer.loyalty.LoyaltyActivity
import com.pasco.pascocustomer.notificationoffon.NotificationOnOffActivity
import com.pasco.pascocustomer.userFragment.logoutmodel.LogOutModelView
import com.pasco.pascocustomer.userFragment.logoutmodel.LogoutBody
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MoreFragment : Fragment() {
    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!
    private val logoutViewModel: LogOutModelView by viewModels()
    private var refresh = ""
    private lateinit var activity: Activity
    private lateinit var sharedPreferencesLanguageName: SharedPreferences
    private var language = ""
    private var languageId = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMoreBinding.inflate(inflater, container, false)
        val view = binding.root

        activity = requireActivity()
        sharedPreferencesLanguageName =
            activity.getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
        language = sharedPreferencesLanguageName.getString("language_text", "").toString()
        languageId = sharedPreferencesLanguageName.getString("languageId", "").toString()





        if (Objects.equals(language, "ar")) {
            binding.seeNotificationA.setImageResource(R.drawable.back_left)
            binding.myWalletrightAr.setImageResource(R.drawable.back_left)
            binding.forwardArrowContactSupport.setImageResource(R.drawable.back_left)
            binding.forwardArrowTc.setImageResource(R.drawable.back_left)
            binding.forwardArrowContacUs.setImageResource(R.drawable.back_left)
            binding.forwardArrowTc.setImageResource(R.drawable.back_left)
            binding.forwardArrowContacUs.setImageResource(R.drawable.back_left)
            binding.forwardArrowNotes.setImageResource(R.drawable.back_left)
            binding.loyaltyImgForward.setImageResource(R.drawable.back_left)
            binding.forwardArrowShareApp.setImageResource(R.drawable.back_left)
            binding.chooseLangA.setImageResource(R.drawable.back_left)

        }



        binding.consLogout.setOnClickListener { openLogoutPop() }
        binding.consContactAndSupportInside.setOnClickListener {
            val intent = Intent(requireContext(), ContactWithUsActivity::class.java)
            startActivity(intent)
        }
        binding.consMyWalletVehDetails.setOnClickListener {
            val intent = Intent(requireContext(), DriverWalletActivity::class.java)
            startActivity(intent)
        }


        binding.consUpdateLanguage.setOnClickListener {
            val intent = Intent(requireContext(), LanguageActivity::class.java)
            startActivity(intent)
        }

        binding.notificationConst.setOnClickListener {
            val intent = Intent(requireContext(), NotificationOnOffActivity::class.java)
            startActivity(intent)
        }
        binding.consTermsCondInside.setOnClickListener {
            val intent = Intent(requireContext(), TermsAndConditionsActivity::class.java)
            startActivity(intent)
        }

        binding.consLoyaltyProgram.setOnClickListener {
            val intent = Intent(requireContext(), LoyaltyActivity::class.java)
            startActivity(intent)
        }

        binding.consPrivacyPolicyInside.setOnClickListener {
            val intent = Intent(requireContext(), TermsAndConditionsActivity::class.java)
            startActivity(intent)
        }
        binding.consNotesReminderDri.setOnClickListener {
            val intent = Intent(requireContext(), NotesRemainderActivity::class.java)
            startActivity(intent)
        }

        binding.consShareApp.setOnClickListener { shareApp() }
        refresh = PascoApp.encryptedPrefs.token
        logOutObserver()
        return view
    }

    @SuppressLint("MissingInflatedId")
    private fun openLogoutPop() {
        val builder = AlertDialog.Builder(
            requireContext(),
            R.style.Style_Dialog_Rounded_Corner
        )
        val dialogView = layoutInflater.inflate(R.layout.logout_popup, null)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val cancelLogBtn = dialogView.findViewById<TextView>(R.id.cancelLogBtn)
        val yesLogoutBtn = dialogView.findViewById<TextView>(R.id.yesLogoutBtn)
        dialog.show()
        cancelLogBtn.setOnClickListener {
            dialog.dismiss()
        }
        yesLogoutBtn.setOnClickListener {
            logOutApi()
        }
    }

    private fun logOutApi() {
        val bookingBody = LogoutBody(
            refresh = refresh,
            language = languageId
        )
        logoutViewModel.otpCheck(bookingBody, requireActivity())
    }

    private fun logOutObserver() {
        logoutViewModel.mRejectResponse.observe(requireActivity()) { response ->
            val message = response.peekContent().msg
            Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()

            if (response.peekContent().status == "True") {
                PascoApp.encryptedPrefs.bearerToken = ""
                PascoApp.encryptedPrefs.userId = ""
                PascoApp.encryptedPrefs.isFirstTime = true

                setAppLanguage()


                val intent = Intent(requireActivity(), ChooseLanguageActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }

        }

        logoutViewModel.errorResponse.observe(requireActivity()) {
            ErrorUtil.handlerGeneralError(requireContext(), it)
        }
    }

    private fun shareApp() {
        val appPackageName = "com.pasco.pascocustomer" // Hardcoded package name
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Check out this app!")
            putExtra(
                Intent.EXTRA_TEXT,
                "Check out this amazing app: https://play.google.com/store/apps/details?id=$appPackageName"
            )
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun setAppLanguage() {
        val currentLanguage = sharedPreferencesLanguageName.getString("language_text", "en") ?: "en"
        updateLocale(currentLanguage)
        language = "en"
        changeLanguage(language)
    }

    private fun updateLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        requireActivity().baseContext.resources.updateConfiguration(config,  requireActivity().baseContext.resources.displayMetrics)

    }

    private fun changeLanguage(language: String) {
        val editor = sharedPreferencesLanguageName.edit()
        editor.putString("language_text", language)
        editor.apply()

        // recreate()
        updateLocale(language)
    }
}