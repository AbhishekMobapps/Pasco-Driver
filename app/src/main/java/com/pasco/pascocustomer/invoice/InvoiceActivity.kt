package com.pasco.pascocustomer.invoice

import android.Manifest
import android.app.DownloadManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.databinding.ActivityInvoiceBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class InvoiceActivity : AppCompatActivity() {
    private var id = ""
    private var userType = ""
    private lateinit var binding: ActivityInvoiceBinding
    private val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1
    private val progressDialog by lazy { CustomProgressDialog(this) }

    private var pendingDownloadUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener { finish() }
        id = intent.getStringExtra("id").toString()
        userType = PascoApp.encryptedPrefs.userType
        if (userType.equals("driver"))
        {
            val finalUrlDriver = "http://69.49.235.253:8090/api/show-driverinvoice-profile/$id/"
            val finalUrlDriver1 = "http://69.49.235.253:8090/api/download_driverinvoice/$id/"
            binding.webView.loadUrl(finalUrlDriver)

            binding.buttonConst.setOnClickListener {
                initiateDownload(finalUrlDriver1)
            }

        }
        else{
            val finalUrl = "http://69.49.235.253:8090/api/show-invoice-profile/$id/"
            val finalUrl1 = "http://69.49.235.253:8090/api/download_invoice/$id/"

            binding.webView.loadUrl(finalUrl)

            binding.buttonConst.setOnClickListener {
                initiateDownload(finalUrl1)
            }

        }
        setupWebView()

    }



    private fun setupWebView() {
        val webSettings: WebSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false

        binding.webView.webViewClient = WebViewClient()
    }

    private fun initiateDownload(url: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            pendingDownloadUrl = url
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showPermissionExplanation()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_WRITE_EXTERNAL_STORAGE)
            }
        } else {
            downloadFile(url)
        }
    }

    private fun showPermissionExplanation() {
        AlertDialog.Builder(this)
            .setTitle("Storage Permission Needed")
            .setMessage("This app needs storage permission to download files. Please grant this permission.")
            .setPositiveButton("OK") { dialog, _ ->
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_WRITE_EXTERNAL_STORAGE)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Need Permissions")
            .setMessage("This app needs permission to use this feature. You can grant them in app settings.")
            .setPositiveButton("Go to Settings") { dialog, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun downloadFile(url: String) {
        val request = DownloadManager.Request(Uri.parse(url))
        val cookies = CookieManager.getInstance().getCookie(url)
        request.addRequestHeader("cookie", cookies)
        request.addRequestHeader("User-Agent", binding.webView.settings.userAgentString)
        request.setDescription("Downloading file...")

        // Guess the file name from the URL and contentDisposition
        var fileName = URLUtil.guessFileName(url, null, null)

        // Ensure the file name ends with .pdf
        if (!fileName.endsWith(".pdf", ignoreCase = true)) {
            fileName = "$fileName.pdf"
        }

        request.setTitle(fileName)
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
        Toast.makeText(this, "Downloading File as PDF", Toast.LENGTH_LONG).show()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pendingDownloadUrl?.let { url ->
                    downloadFile(url)
                }
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showSettingsDialog()
                } else {
                    Toast.makeText(this, "Permission Denied. Unable to download file.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
