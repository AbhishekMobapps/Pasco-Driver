package com.pasco.pascocustomer.Driver.Fragment.MoreFragDriver

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.ContactWithUsActivity
import com.pasco.pascocustomer.Driver.DriverDashboard.Ui.DriverDashboardActivity
import com.pasco.pascocustomer.Driver.DriverWallet.DriverWalletActivity
import com.pasco.pascocustomer.Driver.Fragment.MoreFragDriver.appsurvey.AppSurveyBody
import com.pasco.pascocustomer.Driver.Fragment.MoreFragDriver.appsurvey.AppSurveyViewModel
import com.pasco.pascocustomer.Driver.NotesRemainders.Ui.NotesRemainderActivity
import com.pasco.pascocustomer.Driver.adapter.TermsAndConditionsActivity
import com.pasco.pascocustomer.Driver.driverFeedback.DriverFeedbackBody
import dagger.hilt.android.AndroidEntryPoint
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.activity.Driver.PrivacyPolicyActivity
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.commonpage.login.LoginActivity
import com.pasco.pascocustomer.customer.activity.updatevehdetails.UpdateVehicleDetailsActivity
import com.pasco.pascocustomer.databinding.FragmentDriverMoreBinding
import com.pasco.pascocustomer.loyalty.LoyaltyActivity
import com.pasco.pascocustomer.notificationoffon.NotificationOnOffActivity
import com.pasco.pascocustomer.userFragment.logoutmodel.LogOutModelView
import com.pasco.pascocustomer.userFragment.logoutmodel.LogoutBody
import com.pasco.pascocustomer.utils.ErrorUtil


@AndroidEntryPoint
class DriverMoreFragment : Fragment() {
    private lateinit var binding: FragmentDriverMoreBinding
    private val logoutViewModel: LogOutModelView by viewModels()
    private val appSurveyViewModel: AppSurveyViewModel by viewModels()
    private var dAdminApprovedId: String? = ""
    private val progressDialog by lazy { CustomProgressDialog(requireContext()) }
    var bottomSheetDialog: BottomSheetDialog? = null
    private var refersh = ""

    //hello
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDriverMoreBinding.inflate(inflater, container, false)

        refersh = PascoApp.encryptedPrefs.token
        dAdminApprovedId = PascoApp.encryptedPrefs.driverApprovedId

        if (dAdminApprovedId == "0") {
            disableAllExceptFour()
            openPopUp()
        } else if (dAdminApprovedId == "1") {
            enableAll()
        }

        binding.consUpdateVehDetails.setOnClickListener {
            val intent = Intent(requireActivity(), UpdateVehicleDetailsActivity::class.java)
            startActivity(intent)
        }

        binding.consLoyaltyProgram.setOnClickListener {
            val intent = Intent(requireContext(), LoyaltyActivity::class.java)
            startActivity(intent)
        }
        binding.consContactAndSupportInside.setOnClickListener {
            val intent = Intent(requireContext(), ContactWithUsActivity::class.java)
            startActivity(intent)
        }
        binding.consMyWalletVehDetails.setOnClickListener {
            val intent = Intent(requireContext(), DriverWalletActivity::class.java)
            startActivity(intent)
        }
        binding.notificationConstDriver.setOnClickListener {
            val intent = Intent(requireContext(), NotificationOnOffActivity::class.java)
            startActivity(intent)
        }
        binding.consTermsCondInside.setOnClickListener {
            val intent = Intent(requireContext(), TermsAndConditionsActivity::class.java)
            startActivity(intent)
        }

        binding.consPrivacyPolicyInside.setOnClickListener {
            val intent = Intent(requireContext(), PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }
        binding.consNotesReminderDri.setOnClickListener {
            val intent = Intent(requireContext(), NotesRemainderActivity::class.java)
            startActivity(intent)
        }
        binding.consShareAppDriver.setOnClickListener { shareApp() }

        binding.consAppSurvey.setOnClickListener {
            showSurveyPopup()
        }


        binding.consLogout.setOnClickListener {
            openLogoutPop()
            //logout observer
            logOutObserver()
        }

        return binding.root
    }

    private fun showSurveyPopup() {
        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.TopCircleDialogStyle)
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.app_survey_popup, null)
        bottomSheetDialog!!.setContentView(view)


        val ratingBarSurvey = bottomSheetDialog?.findViewById<RatingBar>(R.id.ratingBarSurvey)
        val commentTxtSurvey = bottomSheetDialog?.findViewById<EditText>(R.id.commentTxtSurvey)
        val submitBtnSurvey = bottomSheetDialog?.findViewById<TextView>(R.id.submitBtnSurvey)
        val skipBtnSurvey = bottomSheetDialog?.findViewById<TextView>(R.id.skipBtnSurvey)

        var ratingBars = ""
        ratingBarSurvey?.setOnRatingBarChangeListener { _, rating, _ ->
            // Toast.makeText(this, "New Rating: $rating", Toast.LENGTH_SHORT).show()
            ratingBars = rating.toString()
        }

        submitBtnSurvey?.setOnClickListener {
            val comment = commentTxtSurvey?.text.toString()
            if (ratingBars.isEmpty()) {
                Toast.makeText(requireContext(), "Please add a rating", Toast.LENGTH_SHORT).show()
            } else if (comment.isBlank()) {
                Toast.makeText(requireContext(), "Please add a comment", Toast.LENGTH_SHORT).show()
            } else {
                appSurveyApi(comment, ratingBars)
                appSurveyObserver()
            }
        }
        skipBtnSurvey?.setOnClickListener {
            bottomSheetDialog?.dismiss()
        }

        val displayMetrics = DisplayMetrics()
        (requireActivity() as AppCompatActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels
        val halfScreenHeight = screenHeight * .58
        val eightyPercentScreenHeight = screenHeight * .58

        // Set the initial height of the bottom sheet to 50% of the screen height
        val layoutParams = view.layoutParams
        layoutParams.height = halfScreenHeight.toInt()
        view.layoutParams = layoutParams

        var isExpanded = false
        view.setOnClickListener {
            // Expand or collapse the bottom sheet when it is touched
            if (isExpanded) {
                layoutParams.height = halfScreenHeight.toInt()
            } else {
                layoutParams.height = eightyPercentScreenHeight.toInt()
            }
            view.layoutParams = layoutParams
            isExpanded = !isExpanded
        }

        bottomSheetDialog!!.show()

    }

    private fun appSurveyObserver() {
        appSurveyViewModel.progressIndicator.observe(requireActivity()) {}
        appSurveyViewModel.mAppSurveyResponse.observe(
            requireActivity()
        ) {
            val msg = it.peekContent().msg
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

            val intent = Intent(requireActivity(), DriverDashboardActivity::class.java)
            startActivity(intent)
        }
        appSurveyViewModel.errorResponse.observe(requireActivity()) {
            ErrorUtil.handlerGeneralError(requireContext(), it)
            // errorDialogs()
        }
    }

    private fun appSurveyApi(commentTxt: String, ratingBars: String) {
        //   val codePhone = strPhoneNo
        val body = AppSurveyBody(
            rating = ratingBars,
            feedback = commentTxt
        )
        appSurveyViewModel.appSurveyData(body, requireActivity(), progressDialog)
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

    private fun enableAll() {
        binding.consContactAndSupportInside.isEnabled = true
        binding.consMyWalletVehDetails.isEnabled = true
        binding.consTermsCondInside.isEnabled = true
        binding.consPrivacyPolicyInside.isEnabled = true
    }

    private fun openPopUp() {
        val builder =
            AlertDialog.Builder(requireContext(), R.style.Style_Dialog_Rounded_Corner)
        val dialogView = layoutInflater.inflate(R.layout.admin_approval_status, null)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val okButtonAdminA = dialogView.findViewById<TextView>(R.id.okButtonAdminA)
        dialog.show()
        okButtonAdminA.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun disableAllExceptFour() {
        binding.consNotesReminderDri.isEnabled = false
        binding.consMyWalletVehDetails.isEnabled = false
        binding.consNotesReminderDri.isEnabled = false
        binding.consUpdateVehDetails.isEnabled = false
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
            refresh = refersh
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
                PascoApp.encryptedPrefs.driverApprovedId = ""
                PascoApp.encryptedPrefs.isFirstTime = true
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }


        }

        logoutViewModel.errorResponse.observe(requireActivity()) {
            ErrorUtil.handlerGeneralError(requireContext(), it)
        }
    }

}