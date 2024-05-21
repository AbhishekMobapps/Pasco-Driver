package com.pasco.pascocustomer.application

import android.app.Application
import android.content.res.Configuration
import com.google.firebase.FirebaseApp
import com.pasco.pascocustomer.utils.PreferenceManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
//abhi comment

class PascoApp : Application() {

    companion object {
        lateinit var encryptedPrefs: PreferenceManager
        lateinit var instance: PascoApp
    }

    override fun onCreate() {
        super.onCreate()
        encryptedPrefs = PreferenceManager(applicationContext).getInstance(applicationContext)
        instance = this

        FirebaseApp.initializeApp(this)

    }

    fun isDarkThemeOn(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}