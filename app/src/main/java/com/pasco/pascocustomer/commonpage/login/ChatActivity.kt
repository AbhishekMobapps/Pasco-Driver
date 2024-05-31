package com.pasco.pascocustomer.commonpage.login

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.databinding.ActivityChatBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var outputFilePath: String? = null

    private val RECORD_REQUEST_CODE = 101
    private val STORAGE_REQUEST_CODE = 102
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.stopRecord.isEnabled = false
        binding.placeRecord.isEnabled = false
        binding.sendBtn.isEnabled = false


        if (!checkPermission(Manifest.permission.RECORD_AUDIO)) {
            requestPermission(Manifest.permission.RECORD_AUDIO, RECORD_REQUEST_CODE)
        }

        if (!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_REQUEST_CODE)
        }

        outputFilePath = externalCacheDir?.absolutePath + "/audiorecordtest.3gp"

        binding.placeRecord.setOnClickListener {
            startRecording()
            binding.placeRecord.visibility = View.GONE
            binding.stopRecord.visibility = View.VISIBLE

        }

        binding.stopRecord.setOnClickListener {
            stopRecording()
            binding.stopRecord.visibility = View.GONE
            binding.placeRecord.visibility = View.VISIBLE
        }

        binding.selectImg.setOnClickListener {
            startPlaying()
        }


    }

    private fun startRecording() {
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(outputFilePath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            try {
                prepare()
                start()
                binding.stopRecord.isEnabled = false
            } catch (e: IOException) {
                Log.e("AUDIO_RECORD", "prepare() failed")
            }
        }
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
            mediaRecorder = null
            //recordButton.isEnabled = true
            binding.stopRecord.isEnabled = false
            binding.placeRecord.isEnabled = false
            binding.sendBtn.isEnabled = false
        }
    }

    private fun startPlaying() {
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(outputFilePath)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e("AUDIO_PLAY", "prepare() failed")
            }
        }
    }



    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_REQUEST_CODE || requestCode == STORAGE_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, proceed with recording
            } else {
                // Permission denied, disable functionality that depends on this permission.
            }
        }
    }

}