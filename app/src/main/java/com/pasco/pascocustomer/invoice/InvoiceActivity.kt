package com.pasco.pascocustomer.invoice

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.databinding.ActivityInvoiceBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InvoiceActivity : AppCompatActivity() {
    private var id = ""
    private lateinit var binding: ActivityInvoiceBinding
    private val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1
    private val progressDialog by lazy { CustomProgressDialog(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener { finish() }
        id = intent.getStringExtra("id").toString()

        val finalUrl = "http://69.49.235.253:8090/api/show-invoice-profile/$id/"

        //startWebView(finalUrl)
        setupWebView()
        // Load the URL
        binding.webView.loadUrl(finalUrl)
      /*  binding.webView.webChromeClient = ChromeClient()
        binding.webView.settings.setGeolocationDatabasePath(this.filesDir.path)*/
    }

/*    class ChromeClient : WebChromeClient() {
        // For Android 5.0
        override fun onProgressChanged(view: WebView, progress: Int) {
            *//* if (progress == 100)
                getActivity().setTitle(R.string.app_name);*//*
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
                Toast.makeText(this@InvoiceActivity, "Error:$description", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.webView.loadUrl(url)
    }*/

    private fun setupWebView() {
        val webSettings: WebSettings =  binding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false

        binding.webView.webViewClient = WebViewClient()

        // Handle file downloads
        binding.webView.setDownloadListener(DownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_WRITE_EXTERNAL_STORAGE)
            } else {
                downloadFile(url, contentDisposition, mimeType)
            }
        })
    }

    private fun downloadFile(url: String, contentDisposition: String?, mimeType: String?) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setMimeType(mimeType)
        val cookies = android.webkit.CookieManager.getInstance().getCookie(url)
        request.addRequestHeader("cookie", cookies)
        request.addRequestHeader("User-Agent",  binding.webView.settings.userAgentString)
        request.setDescription("Downloading file...")
        request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType))
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType))

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
        Toast.makeText(this, "Downloading File", Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, retry the download
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }
}
