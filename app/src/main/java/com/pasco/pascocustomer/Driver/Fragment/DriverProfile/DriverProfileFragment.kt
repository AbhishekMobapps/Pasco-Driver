package com.pasco.pascocustomer.Driver.Fragment.DriverProfile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Editable
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Profile.PutViewModel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.databinding.FragmentDriverProfileBinding
import com.pasco.pascocustomer.userFragment.profile.modelview.GetProfileBody
import com.pasco.pascocustomer.userFragment.profile.modelview.GetProfileModelView
import com.pasco.pascocustomer.utils.ErrorUtil
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.Objects
import javax.inject.Inject

@AndroidEntryPoint
class DriverProfileFragment : Fragment() {
    private lateinit var binding: FragmentDriverProfileBinding

    @Inject
    lateinit var activity: Activity
    private val getProfileModelView: GetProfileModelView by viewModels()
    val profiViewModel: ProfileViewModel by viewModels()
    private val progressDialog: CustomProgressDialog by lazy {
        CustomProgressDialog(requireActivity())
    }
    private var userType = " "
    private var selectedImageFile: File? = null
    private var imageUrl: String? = null
    private val cameraPermissionCode = 101
    private val galleryPermissionCode = 102
    private lateinit var imagePart: MultipartBody.Part
    private lateinit var sharedPreferencesLanguageName: SharedPreferences
    private var languageId = ""
    private var language = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDriverProfileBinding.inflate(inflater, container, false)
        userType = PascoApp.encryptedPrefs.userType
        // Request camera and gallery permissions if not granted
        requestPermission()

        sharedPreferencesLanguageName = activity.getSharedPreferences(
            "PREFERENCE_NAME",
            AppCompatActivity.MODE_PRIVATE
        )
        languageId = sharedPreferencesLanguageName.getString("languageId", "").toString()
        language = sharedPreferencesLanguageName.getString("language_text", "").toString()
        if (Objects.equals(language, "ar")) {
            binding.driverUserNameP.gravity = Gravity.RIGHT
            binding.driverEmailP.gravity = Gravity.RIGHT
            binding.driverAddressP.gravity = Gravity.RIGHT
        }

        binding.addImgDProfile.setOnClickListener {
            selectImage()
        }
        binding.updateBtndp.setOnClickListener {
            profilePutApi()
        }
        //put profile observer
        ObserverPutUserProfile()

        //getProfileApi call
        getProfileApi()
        //call observer
        getUserProfileObserver()

        return binding.root
    }

    private fun profilePutApi() {
        val Pname = binding.driverUserNameP.text.toString().toRequestBody(MultipartBody.FORM)
        val Pemail = binding.driverEmailP.text.toString().toRequestBody(MultipartBody.FORM)
        val languageId = languageId.toRequestBody(MultipartBody.FORM)
        if (selectedImageFile != null) {
            imagePart = MultipartBody.Part.createFormData(
                "image",
                selectedImageFile!!.name,
                selectedImageFile!!.asRequestBody("image/*".toMediaTypeOrNull())
            )
            Log.e("endDate3", "file: " + selectedImageFile)
        } else {
            imagePart = MultipartBody.Part.createFormData(
                "image", "",
                "".toRequestBody("image/*".toMediaTypeOrNull())
            )


        }
        profiViewModel.putProfile(
            progressDialog,
            activity,
            Pname,
            Pemail,
            languageId,
            imagePart
        )

    }

    private fun ObserverPutUserProfile() {
        profiViewModel.progressIndicator.observe(requireActivity(), Observer {
        })
        profiViewModel.mputProfileResponse.observe(requireActivity()) { response ->
            val message = response.peekContent().msg!!
            getProfileApi()
            Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()

        }

        profiViewModel.errorResponse.observe(requireActivity()) {
            // Handle general errors
            ErrorUtil.handlerGeneralError(requireActivity(), it)
        }
    }

    private fun getProfileApi() {
        val body = GetProfileBody(
            language = languageId
        )
        getProfileModelView.getProfile(
            activity,
            progressDialog,
            body

        )
    }


    private fun getUserProfileObserver() {
        getProfileModelView.progressIndicator.observe(requireActivity(), Observer {
        })
        getProfileModelView.mRejectResponse.observe(requireActivity()) { response ->
            val data = response.peekContent().data
            val address = data?.currentCity.toString()
            val baseUrl = "http://69.49.235.253:8090"
            val imagePath = data?.image.orEmpty()
            val users = response.peekContent().data
            imageUrl = "$baseUrl$imagePath"

            if (imageUrl!!.isNotEmpty()) {
                Glide.with(this)
                    .load(imageUrl)
                    .into(binding.cricleImgDp)

                val imageFile = File(imageUrl!!)
                imagePart = MultipartBody.Part.createFormData(
                    "image",
                    imageFile.name,
                    imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                )
            } else {
                Glide.with(this)
                    .load(R.drawable.ic_launcher_background)
                    .into(binding.cricleImgDp)

                imagePart = MultipartBody.Part.createFormData(
                    "image",
                    "",
                    "".toRequestBody("image/*".toMediaTypeOrNull())
                )
            }
            binding.driverUserNameP.text =
                Editable.Factory.getInstance().newEditable(users?.fullName)
            binding.driverEmailP.text = Editable.Factory.getInstance().newEditable(users?.email)
            binding.driverNoP.text = users?.phoneNumber
            binding.driverAddressP.text = address

        }

        getProfileModelView.errorResponse.observe(requireActivity()) {
            // Handle general errors
            ErrorUtil.handlerGeneralError(requireActivity(), it)

        }
    }


    private fun selectImage() {
        val options = arrayOf<CharSequence>(
            getString(R.string.take_photo),
            getString(R.string.gallery),
            getString(R.string.cancel)
        )
        val builder = android.app.AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.select_image))
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == getString(R.string.take_photo) -> openCamera()
                options[item] == getString(R.string.gallery) -> openGallery()
                options[item] == getString(R.string.cancel) -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*" // Allow all image types
        selectImageLauncher.launch(galleryIntent)
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(requireActivity().packageManager) != null) {
            takePictureLauncher.launch(cameraIntent)
        }
    }

    private fun setImageOnImageView(imageFile: File?) {
        if (imageFile != null) {
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            // Set the Bitmap to the ImageView
            binding.cricleImgDp.setImageBitmap(bitmap)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        }
    }

    private val selectImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val selectedImageUri = data.data
                    if (selectedImageUri != null) {
                        selectedImageFile = convertUriToFile(selectedImageUri)
                        if (selectedImageFile != null) {
                            // Now you have the image file in File format, you can use it as needed.
                            setImageOnImageView(selectedImageFile)

                            // Load the image using Glide
                            val imagePath =
                                selectedImageFile!!.absolutePath
                            Glide.with(this)
                                .load(imagePath)
                                .placeholder(R.drawable.home_bg)
                                .into(binding.cricleImgDp)
                        }
                    }
                }
            }
        }


    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                if (imageBitmap != null) {
                    val fileName = "image_${System.currentTimeMillis()}.jpg"
                    selectedImageFile = bitmapToFile(imageBitmap, fileName)
                    binding.cricleImgDp.setImageBitmap(imageBitmap)
                } else {
                    Toast.makeText(requireActivity(),getString(R.string.Image_capture_canceled), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    private fun bitmapToFile(bitmap: Bitmap, fileName: String): File {
        // Create a new file in the app's cache directory
        val file = File(requireActivity().cacheDir, fileName)

        // Use FileOutputStream to write the bitmap data to the file
        try {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file
    }

    private fun convertUriToFile(uri: Uri): File? {
        val inputStream: InputStream? = requireActivity().contentResolver.openInputStream(uri)
        if (inputStream != null) {
            val fileName = getFileNameFromUri(uri)
            val outputFile = File(requireActivity().cacheDir, fileName)
            val outputStream = FileOutputStream(outputFile)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            return outputFile
        }
        return null
    }

    private fun getFileNameFromUri(uri: Uri): String {
        var result: String? = null
        val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    result = it.getString(displayNameIndex)
                }
            }
        }
        return result ?: "file"
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    cameraPermissionCode
                )
            }
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    galleryPermissionCode
                )
            }
        }
    }

}