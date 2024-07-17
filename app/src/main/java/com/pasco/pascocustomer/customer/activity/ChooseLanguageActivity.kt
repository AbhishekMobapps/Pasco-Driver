package com.pasco.pascocustomer.customer.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.commonpage.login.LoginActivity
import com.pasco.pascocustomer.customer.activity.hometabactivity.modelview.BookingOrderModelView
import com.pasco.pascocustomer.customer.activity.language.LanguageResponse
import com.pasco.pascocustomer.customer.activity.language.LanguageTYpeModelView
import com.pasco.pascocustomer.customer.activity.vehicledetailactivity.VehicleDetailsActivity
import com.pasco.pascocustomer.databinding.ActivityChooseLanguageBinding
import com.pasco.pascocustomer.language.Originator
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ChooseLanguageActivity : Originator() {
    private lateinit var binding: ActivityChooseLanguageBinding
    private val chooseLanguageList = arrayListOf("English", "Arabic")

    private var isRestarting = true
    private var languageList: List<LanguageResponse.Datum>? = null
    private val languageAdd: MutableList<String> = mutableListOf()
    private lateinit var sharedPreferencesLanguageName: SharedPreferences
    private var languageId = ""
    private val languageModel: LanguageTYpeModelView by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChooseLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize sharedPreferencesLanguageName
        sharedPreferencesLanguageName = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)


        // Set initial language
        if (!isRestarting) {
            setAppLanguage()
        }

        binding.continueBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Spinner Adapter
        /* val dAdapter = ArrayAdapter(this, R.layout.custom_spinner_two, chooseLanguageList)
         dAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
         binding.englishLanguage.adapter = dAdapter
         binding.englishLanguage.onItemSelectedListener =
             object : AdapterView.OnItemSelectedListener {
                 override fun onItemSelected(
                     adapterView: AdapterView<*>?, view: View?, i: Int, l: Long
                 ) {
                     val selectedLanguage = binding.englishLanguage.selectedItem.toString()
                     if (selectedLanguage.isNotEmpty()) {
                         val languageCode = if (selectedLanguage == "English") "en" else "ar"
                         changeLanguage(languageCode)

                     }
                 }

                 override fun onNothingSelected(adapterView: AdapterView<*>?) {}
             }*/



        binding.englishLanguage.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?, view: View?, i: Int, l: Long
                ) {
                    val item = binding.englishLanguage.selectedItem.toString()
                    if (item != getString(R.string.choose_language)) {
                        languageId = languageList!![i].id.toString()

                        Log.e("VehicleTypeSpinnerA", "item... $item")
                        if (item.isNotEmpty()) {
                            val languageCode = if (item == "English") "en" else "ar"
                            changeLanguage(languageCode)

                        }
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                    // Do nothing
                }
            }
        languageType()
        languageTypeObserver()
    }

    private fun setAppLanguage() {
        val currentLanguage = sharedPreferencesLanguageName.getString("language_text", "en") ?: "en"
        updateLocale(currentLanguage)
    }

    private fun changeLanguage(language: String) {
        val editor = sharedPreferencesLanguageName.edit()
        editor.putString("language_text", language)
        editor.putString("languageId", languageId)
        editor.apply()

        updateLocale(language)
    }

    private fun updateLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

    }

    private fun languageType() {
        languageModel.getNotificationOnOff(
            this
        )
    }

    private fun languageTypeObserver() {
        languageModel.mRejectResponse.observe(this) { response ->
            val content = response.peekContent()
            val message = content.message ?: return@observe
            languageList = content.data
            languageAdd.clear()

            for (element in languageList!!) {
                element.languagename?.let { it1 -> languageAdd.add(it1) }
            }
            val dAdapter = VehicleDetailsActivity.SpinnerAdapter(
                this@ChooseLanguageActivity, R.layout.custom_service_type_spinner, languageAdd
            )
            dAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dAdapter.add(getString(R.string.choose_language))
            binding.englishLanguage.adapter = dAdapter
            binding.englishLanguage.setSelection(dAdapter.count)



            if (response.peekContent().status.equals("False")) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                //   binding.linearVehDetails.visibility = View.GONE
            } else if (response.peekContent().status.equals("True")) {

            }
        }

        languageModel.errorResponse.observe(this) {
            // Handle general errors
            ErrorUtil.handlerGeneralError(this, it)
        }
    }


}
