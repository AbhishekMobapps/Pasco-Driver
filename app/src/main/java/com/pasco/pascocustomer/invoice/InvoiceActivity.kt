package com.pasco.pascocustomer.invoice

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.print.PrintAttributes
import android.print.pdf.PrintedPdfDocument
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.databinding.ActivityInvoiceBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class InvoiceActivity : AppCompatActivity() {
    private var id = ""
    private lateinit var binding: ActivityInvoiceBinding
    private val progressDialog by lazy { CustomProgressDialog(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermissions()
        binding.backBtn.setOnClickListener { finish() }
        id = intent.getStringExtra("id").toString()

        val finalUrl = "http://69.49.235.253:8090/api/show-invoice-profile/$id/"

        startWebView(finalUrl)
        binding.webView.webChromeClient = ChromeClient()
        binding.webView.settings.setGeolocationDatabasePath(this.filesDir.path)

        binding.downloadBtn.setOnClickListener {createWebViewPdf()  }
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
                Toast.makeText(this@InvoiceActivity, "Error:$description", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.webView.loadUrl(url)
    }
    private fun createWebViewPdf() {
        val printAttributes = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .setResolution(PrintAttributes.Resolution("pdf", "pdf", 300, 300))
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
            .build()

        val pdfDocument = PrintedPdfDocument(this, printAttributes)
        val pageInfo = PdfDocument.PageInfo.Builder(binding.webView.width, binding.webView.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        binding.webView.draw(page.canvas)
        pdfDocument.finishPage(page)

        val directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
        val file = File(directoryPath, "webview_page.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(this, "PDF Downloaded Successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted
            } else {
                // Permission denied
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
    }
}