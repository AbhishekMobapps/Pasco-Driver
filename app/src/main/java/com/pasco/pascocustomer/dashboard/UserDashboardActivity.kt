package com.pasco.pascocustomer.dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.BuildConfig
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.customer.activity.notificaion.NotificationActivity
import com.pasco.pascocustomer.customer.activity.notificaion.notificationcount.NotificationCountViewModel
import com.pasco.pascocustomer.databinding.ActivityUserDashboardBinding
import com.pasco.pascocustomer.userFragment.MoreFragment
import com.pasco.pascocustomer.userFragment.UserHomeFragment
import com.pasco.pascocustomer.userFragment.order.OrderFragment
import com.pasco.pascocustomer.userFragment.profile.ProfileFragment
import com.pasco.pascocustomer.userFragment.profile.modelview.GetProfileModelView
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDashboardBinding

    private val shouldLoadHomeFragOnBackPress = true
    private var doubleBackToExitPressedOnce = false
    private var navItemIndex = 0
    private val TAG_DASH_BOARD = "dashboard"
    private var CURRENT_TAG = TAG_DASH_BOARD
    val notificationCountViewModel: NotificationCountViewModel by viewModels()
    private val TAG_NEXT = "next"
    private val getProfileModelView: GetProfileModelView by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }
    private var profileUpdate = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        profileUpdate = intent.getStringExtra("profileUpdate").toString()

        if (profileUpdate =="update")
        {
            navItemIndex = 1
            CURRENT_TAG = TAG_NEXT
            binding.homeIconUser.setImageResource(R.drawable.home_icon)
            binding.orderIconUsers.setImageResource(R.drawable.order_icon)
            binding.walletIconUsers.setImageResource(R.drawable.more_icon)
            binding.hisIconUser.setImageResource(R.drawable.hostory_icon)
            binding.profileIconUser.setImageResource(R.drawable.profile_1)

            binding.orderTextUser.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding.homeTextUser.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding.walletTextUser.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding.hisTextViewUser.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding.accountTextUser.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.headerTxt.visibility = View.VISIBLE
            binding.consUserDashBoard.visibility = View.GONE

            replaceFragment(ProfileFragment())
        }
        else
        {
            val userHomeFragment = UserHomeFragment()
            replaceFragment(userHomeFragment)
        }

        binding.homeTextUser.setTextColor(ContextCompat.getColor(this, R.color.black))

        binding.notificationBtn.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }

        binding.HomeFragmentUser.setOnClickListener {
            navItemIndex = 1
            CURRENT_TAG = TAG_NEXT
            binding.homeIconUser.setImageResource(R.drawable.home_1)
            binding.orderIconUsers.setImageResource(R.drawable.order_icon)
            binding.walletIconUsers.setImageResource(R.drawable.more_icon)
            binding.hisIconUser.setImageResource(R.drawable.hostory_icon)
            binding.profileIconUser.setImageResource(R.drawable.profile_icon)

            binding.orderTextUser.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding.homeTextUser.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.walletTextUser.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding.hisTextViewUser.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding.accountTextUser.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding.consUserDashBoard.visibility = View.VISIBLE
            binding.headerTxt.visibility = View.GONE
            getProfileApi()
            replaceFragment(UserHomeFragment())
        }

        binding.ordersFragmentUser.setOnClickListener {
            navItemIndex = 2
            CURRENT_TAG = TAG_NEXT
            binding.homeIconUser.setImageResource(R.drawable.home_icon)
            binding.orderIconUsers.setImageResource(R.drawable.order_1)
            binding.walletIconUsers.setImageResource(R.drawable.more_icon)
            binding.hisIconUser.setImageResource(R.drawable.hostory_icon)
            binding.profileIconUser.setImageResource(R.drawable.profile_icon)

            binding.orderTextUser.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.homeTextUser.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding.walletTextUser.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding.hisTextViewUser.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding.accountTextUser.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding.consUserDashBoard.visibility = View.VISIBLE
            binding.headerTxt.visibility = View.GONE
            replaceFragment(OrderFragment())
        }

        binding.profileDUserFragment.setOnClickListener {
            navItemIndex = 1
            CURRENT_TAG = TAG_NEXT
            binding.homeIconUser.setImageResource(R.drawable.home_icon)
            binding.orderIconUsers.setImageResource(R.drawable.order_icon)
            binding.walletIconUsers.setImageResource(R.drawable.more_icon)
            binding.hisIconUser.setImageResource(R.drawable.hostory_icon)
            binding.profileIconUser.setImageResource(R.drawable.profile_1)

            binding.orderTextUser.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding.homeTextUser.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding.walletTextUser.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding.hisTextViewUser.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding.accountTextUser.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.headerTxt.visibility = View.VISIBLE
            binding.consUserDashBoard.visibility = View.GONE

            replaceFragment(ProfileFragment())
        }

        binding.linearWalletUser.setOnClickListener {
            navItemIndex = 1
            CURRENT_TAG = TAG_NEXT
            binding.homeIconUser.setImageResource(R.drawable.home_icon)
            binding.orderIconUsers.setImageResource(R.drawable.order_icon)
            binding.walletIconUsers.setImageResource(R.drawable.more_1)
            binding.hisIconUser.setImageResource(R.drawable.hostory_icon)
            binding.profileIconUser.setImageResource(R.drawable.profile_icon)

            binding.orderTextUser.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding.homeTextUser.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding.walletTextUser.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.hisTextViewUser.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding.accountTextUser.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding.headerTxt.visibility = View.GONE
            binding.consUserDashBoard.visibility = View.VISIBLE
            replaceFragment(MoreFragment())
        }


        // Api and observer
        getProfileApi()
        getProfileObserver()
        getNotificationCountApi()
        notificationCountObserver()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.userFrameLayout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        if (shouldLoadHomeFragOnBackPress) {
            if (navItemIndex != 0) {
                navItemIndex = 0
                CURRENT_TAG = TAG_DASH_BOARD
                val eventListFragment = UserHomeFragment()
                backFragment(eventListFragment)
            } else {
                if (doubleBackToExitPressedOnce) {
                    //super.onBackPressed()
                    onBackPressedDispatcher.onBackPressed()
                    return
                }
                doubleBackToExitPressedOnce = true
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    // Your Code
                    doubleBackToExitPressedOnce = false
                }, 2000)
            }
        }
    }

    private fun backFragment(fragment: Fragment) {
        binding.homeIconUser.setImageResource(R.drawable.home_1)
        binding.orderIconUsers.setImageResource(R.drawable.order_icon)
        binding.walletIconUsers.setImageResource(R.drawable.more_icon)
        binding.hisIconUser.setImageResource(R.drawable.hostory_icon)
        binding.profileIconUser.setImageResource(R.drawable.profile_icon)
        binding.homeTextUser.setTextColor(ContextCompat.getColor(this, R.color.black))

        val fragmentManager = supportFragmentManager
        (findViewById<View>(R.id.userFrameLayout) as FrameLayout).removeAllViews()
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.userFrameLayout, fragment)
        fragmentTransaction.commit()
    }

    private fun getProfileApi() {
        getProfileModelView.getProfile(this, progressDialog)
    }

    private fun getProfileObserver() {
        getProfileModelView.progressIndicator.observe(this) {
        }
        getProfileModelView.mRejectResponse.observe(this) {
            val message = it.peekContent().msg
            val success = it.peekContent().status
            val users = it.peekContent().data
            binding.userName.text = users?.fullName

            val url = it.peekContent().data?.image
            Glide.with(this).load(BuildConfig.IMAGE_KEY + url)
                .placeholder(R.drawable.home_bg).into(binding.userProfile)

        }

        getProfileModelView.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
            //errorDialogs()
        }
    }

    private fun getNotificationCountApi() {
        notificationCountViewModel.getCountNoti()
    }

    private fun notificationCountObserver() {
        notificationCountViewModel.mNotiCountResponse.observe(this) {
        }
        notificationCountViewModel.mNotiCountResponse.observe(this) {
            val message = it.peekContent().msg
            val success = it.peekContent().status
            val countNotification = it.peekContent().count

            if (countNotification == 0)
            {
                binding.countNotification.visibility = View.GONE
            }
            else
            {
                binding.countNotification.visibility = View.GONE
                binding.countNotification.text = countNotification.toString()
            }


        }

        notificationCountViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
            //errorDialogs()
        }
    }
}