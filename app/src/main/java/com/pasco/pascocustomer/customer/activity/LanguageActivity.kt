package com.pasco.pascocustomer.customer.activity

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
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import com.pasco.pascocustomer.MainActivity
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.customer.activity.language.LanguageResponse
import com.pasco.pascocustomer.customer.activity.language.LanguageTYpeModelView
import com.pasco.pascocustomer.customer.activity.language.UpdateLanguageModelView
import com.pasco.pascocustomer.databinding.ActivityLanguageBinding
import com.pasco.pascocustomer.userFragment.order.odermodel.CustomerOrderBody
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class LanguageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLanguageBinding
    private lateinit var sharedPreferencesLanguageName: SharedPreferences
    private var language = ""
    private var languageId = ""
    private var languageList: List<LanguageResponse.Datum>? = null
    private val languageAdd: MutableList<String> = mutableListOf()
    private val languageModel: LanguageTYpeModelView by viewModels()
    private val updateLanguageModel: UpdateLanguageModelView by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener { finish() }

        sharedPreferencesLanguageName = getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
        language = sharedPreferencesLanguageName.getString("language_text", "en").toString()
        if (Objects.equals(language, "ar")) {
            binding.backBtn.setImageResource(R.drawable.next)
            updateLanguageConstraints("ar")
        }

        binding.englishLanguage.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?, view: View?, i: Int, l: Long
                ) {
                    val selectedLanguage = binding.englishLanguage.selectedItem.toString()
                    if (selectedLanguage.isNotEmpty()) {
                        val languageCode = if (selectedLanguage == "English") "en" else "ar"
                        languageId = languageList!![i].id.toString()
                        if (languageCode != language) {
                            changeLanguage(languageCode)
                            Log.e("ChangeLang", "Changing language to $languageCode")
                        }
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }

        languageType()
        languageTypeObserver()
    }

    private fun changeLanguage(language: String) {
        val editor = sharedPreferencesLanguageName.edit()
        editor.putString("language_text", language)
        editor.putString("languageId", languageId)
        editor.apply()

        updateLocale(language)
        updateLanguageType(languageId)
        updateLanguageObserver()
        updateLanguageConstraints(language)
    }

    private fun updateLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }

    private fun updateLanguageConstraints(language: String) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.languageContainer)

        if (language == "ar") {
            constraintSet.clear(R.id.english_language, ConstraintSet.START)
            constraintSet.connect(R.id.english_language, ConstraintSet.END, R.id.language_container, ConstraintSet.END, 0)
            constraintSet.clear(R.id.see_more_icon, ConstraintSet.END)
            constraintSet.connect(R.id.see_more_icon, ConstraintSet.START, R.id.language_container, ConstraintSet.START, 0)
        } else {
            constraintSet.clear(R.id.english_language, ConstraintSet.END)
            constraintSet.connect(R.id.english_language, ConstraintSet.START, R.id.language_container, ConstraintSet.START, 0)
            constraintSet.clear(R.id.see_more_icon, ConstraintSet.START)
            constraintSet.connect(R.id.see_more_icon, ConstraintSet.END, R.id.language_container, ConstraintSet.END, 0)
        }

        constraintSet.applyTo(binding.languageContainer)
    }

    private fun languageType() {
        languageModel.getNotificationOnOff(this)
    }

    private fun languageTypeObserver() {
        languageModel.mRejectResponse.observe(this) { response ->
            val content = response.peekContent()
            val message = content.message ?: return@observe
            languageList = content.data
            languageAdd.clear()

            if (languageList != null) {
                for (element in languageList!!) {
                    element.languagename?.let { languageAdd.add(it) }
                }

                val dAdapter = ArrayAdapter(
                    this@LanguageActivity,
                    android.R.layout.simple_spinner_item,
                    languageAdd
                )
                dAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.englishLanguage.adapter = dAdapter

                // Set the spinner selection to the previously selected language after the adapter is set
                val initialPosition = if (language == "ar") 1 else 0
                binding.englishLanguage.setSelection(initialPosition)
            }

            if (response.peekContent().status == "False") {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            } else if (response.peekContent().status == "True") {
                // Additional logic if needed
            }
        }

        languageModel.errorResponse.observe(this) {
            // Handle general errors
            ErrorUtil.handlerGeneralError(this, it)
        }
    }

    private fun updateLanguageType(languageId: String) {
        val body = CustomerOrderBody(
            language = languageId
        )
        updateLanguageModel.updateLanguage(
            this, body
        )
    }

    private fun updateLanguageObserver() {
        updateLanguageModel.mRejectResponse.observe(this) { response ->
            val intents = Intent(this@LanguageActivity, MainActivity::class.java)
            intents.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intents)
            finish()
        }

        updateLanguageModel.errorResponse.observe(this) {
            // Handle general errors
            ErrorUtil.handlerGeneralError(this, it)
        }
    }
}
