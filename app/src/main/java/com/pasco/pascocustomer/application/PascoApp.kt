package com.pasco.pascocustomer.application

import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.content.res.Configuration
import android.text.TextUtils
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.pasco.pascocustomer.utils.PreferenceManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
//abhi comment

class PascoApp :  MultiDexApplication() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
    companion object {
        lateinit var encryptedPrefs: PreferenceManager
        lateinit var instance: PascoApp
        private var mRequestQueue: RequestQueue? = null
    }

    override fun onCreate() {
        super.onCreate()
        encryptedPrefs = PreferenceManager(applicationContext)
        instance = this
    }
    fun getRequestQueue(): RequestQueue {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(applicationContext)
        }
        return mRequestQueue as RequestQueue
    }

    fun <T> addToRequestQueue(req: Request<T>, tag: String?) {
        req.tag = if (TextUtils.isEmpty(tag)) TAG else tag
        getRequestQueue().add(req)
    }

    fun isDarkThemeOn(): Boolean {
        return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}