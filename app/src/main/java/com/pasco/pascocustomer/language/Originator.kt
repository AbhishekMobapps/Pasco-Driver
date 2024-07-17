package com.pasco.pascocustomer.language

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.*

open class Originator : AppCompatActivity() {
    var contextOriginator: Originator? = null

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contextOriginator = this@Originator
        val sharedPreferencesLanguageName = getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
        val lang_txt: String
        val str_language: String
        if (sharedPreferencesLanguageName.getString("language_text", "") == "en") {
            lang_txt = "en"
        } else if (sharedPreferencesLanguageName.getString("language_text", "") == "ar") {
            lang_txt = "ar"
        } else {
            lang_txt = "en"
            str_language = "1"
            val editor = sharedPreferencesLanguageName.edit()
            editor.putString("language_text", lang_txt)
            editor.putString("language_id", str_language)
            editor.apply()
        }
        val locale2 = Locale(lang_txt)
        Locale.setDefault(locale2)
        val config2 = Configuration()
        config2.setLocale(locale2)
        config2.setLayoutDirection(locale2)
        baseContext.resources.updateConfiguration(config2, baseContext.resources.displayMetrics)
    }
}