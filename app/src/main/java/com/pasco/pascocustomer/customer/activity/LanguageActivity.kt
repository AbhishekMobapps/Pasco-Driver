package com.pasco.pascocustomer.customer.activity

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.pasco.pascocustomer.MainActivity
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.databinding.ActivityLanguageBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class LanguageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLanguageBinding
    private lateinit var sharedPreferencesLanguageName: SharedPreferences
    private var language = ""
    private val chooseLanguageList = arrayListOf("English", "Arabic")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener { finish() }

        sharedPreferencesLanguageName = getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
        language = sharedPreferencesLanguageName.getString("language_text", "en").toString()

        val dAdapter = ArrayAdapter(this, R.layout.custom_spinner_two, chooseLanguageList)
        dAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.englishLanguage.adapter = dAdapter

        // Set the spinner selection to the previously selected language
        val initialPosition = if (language == "ar") 1 else 0
        binding.englishLanguage.setSelection(initialPosition)

        binding.englishLanguage.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?, view: View?, i: Int, l: Long
                ) {
                    val selectedLanguage = binding.englishLanguage.selectedItem.toString()
                    if (selectedLanguage.isNotEmpty()) {
                        val languageCode = if (selectedLanguage == "English") "en" else "ar"
                        if (languageCode != language) {
                            changeLanguage(languageCode)
                            Log.e("ChangeLang", "Changing language to $languageCode")
                        }
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
    }

    private fun changeLanguage(language: String) {
        val editor = sharedPreferencesLanguageName.edit()
        editor.putString("language_text", language)
        editor.apply()

        updateLocale(language)

        val intents = Intent(this@LanguageActivity, MainActivity::class.java)
        intents.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intents)
        finish()
    }

    private fun updateLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

    }
}
