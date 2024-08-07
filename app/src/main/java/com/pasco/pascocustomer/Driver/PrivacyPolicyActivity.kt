package com.pasco.pascocustomer.activity.Driver

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.webkit.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.databinding.ActivityPrivacyPolicyBinding
import com.pasco.pascocustomer.language.Originator
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PrivacyPolicyActivity : Originator() {
    private lateinit var binding: ActivityPrivacyPolicyBinding
    private val progressDialog by lazy { CustomProgressDialog(this) }


    private var language = ""
    private lateinit var sharedPreferencesLanguageName: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)


        sharedPreferencesLanguageName = getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
        language = sharedPreferencesLanguageName.getString("language_text", "").toString()

        if (Objects.equals(language, "ar")) {
            binding.backImagePrivacy.setImageResource(R.drawable.next)
            val color = ContextCompat.getColor(this, R.color.white)
            binding.backImagePrivacy.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        } else {
            val color = ContextCompat.getColor(this, R.color.white)
            binding.backImagePrivacy.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            binding.backImagePrivacy.setImageResource(R.drawable.back)
        }

        binding.backImagePrivacy.setOnClickListener {
            finish()
        }
        val finalUrl = "http://69.49.235.253:8090/api/privacypolicydisplay/"

        startWebView(finalUrl)
        binding.webView.webChromeClient = ChromeClient()
        binding.webView.settings.setGeolocationDatabasePath(this.filesDir.path)
    }

    class ChromeClient : WebChromeClient() {
        // For Android 5.0
        override fun onProgressChanged(view: WebView, progress: Int) {
            /* if (progress == 100)
                getActivity().setTitle(R.string.app_name);*/
        }

        override fun onGeolocationPermissionsShowPrompt(
            origin: String,
            callback: GeolocationPermissions.Callback
        ) {
            callback.invoke(origin, true, false)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun startWebView(url: String) {
        val webSettings: WebSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.allowFileAccess = true
        binding.webView.settings.setSupportZoom(true)
        binding.webView.settings.setBuiltInZoomControls(true)
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.databaseEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.loadsImagesAutomatically = true
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT

        progressDialog.start(getString(R.string.please_wait))
        binding.webView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.startsWith("mailto:")) {
                    try {
                        view.context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse(url)))
                    } catch (e: Exception) {
                        //not an intent uri
                    }
                }
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                progressDialog.stop()
            }

            @Deprecated(
                "Deprecated in Java", ReplaceWith(
                    "Toast.makeText(requireActivity(), \"Error:\$description\", Toast.LENGTH_SHORT).show()",
                    "android.widget.Toast",
                    "android.widget.Toast"
                )
            )

            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                Toast.makeText(this@PrivacyPolicyActivity, "Error:$description", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.webView.loadUrl(url)
    }
}