package com.pasco.pascocustomer.customer.activity.updatevehdetails

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.databinding.ActivityUpdateVehicleDetialsBinding
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
@AndroidEntryPoint
class UpdateVehicleDetialsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateVehicleDetialsBinding
    private val progressDialog by lazy { CustomProgressDialog(this) }
    private var imageUrlVp: String? = null
    private var imageUrlVd: String? = null
    private var imageUrlVRc: String? = null
    private var shipmentName: String? = null
    private var vehicleName: String? = null
    private var selectedImageFile: File? = null
    private var selectedImageFileDoc: File? = null
    private var selectedImageFileRc: File? = null
    private val cameraPermissionCode = 101
    private val galleryPermissionCode = 102
    private val getVDetailsViewModel: GetVDetailsViewModel by viewModels()
    private val putVDetailsViewModel: PutVDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateVehicleDetialsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backImageUpdateVeh.setOnClickListener {
            finish()
        }
        requestPermission()
        getVehicleDetails()
        //get vehicle details api
        getVehicleDetailsObserver()
        binding.submitBtnAddVehUpdate.setOnClickListener {
            //call api
            Log.e("dasdas"+ "onCreate: ","Hellp" )
            putUpdateDetails()
        }

        binding.selectVehicleUPV.setOnClickListener {
            openCameraOrGallery("vehicleImg")
        }
        // OnClickListener for the second ImageView (document)
        binding.selectDrivingDocUPV.setOnClickListener {
            openCameraOrGallery("document")
        }
        // OnClickListener for the third ImageView (vehicle RC)
        binding.selectVehicleRcUPV.setOnClickListener {
            openCameraOrGallery("vehicleRc")
        }

        putUpdateDetailsObserver()

    }


    private fun openCameraOrGallery(section: String) {
        val options =
            arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Image")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Take Photo" -> {
                    // If section is "vehicleImg", directly open the camera
                    if (section == "vehicleImg") {
                        openCamera()
                    } else {
                        // For other sections, differentiate between opening camera 1 and camera 2
                        if (section == "document") {
                            openCameraDoc()
                        } else if (section == "vehicleRc") {
                            openCameraRc()
                        }
                    }
                }

                options[item] == "Choose from Gallery" -> {
                    // If section is "vehicleImg", directly open the gallery
                    if (section == "vehicleImg") {
                        openGallery()
                    } else {
                        // For other sections, differentiate between opening gallery 1 and gallery 2
                        if (section == "document") {
                            openGalleryDoc()
                        } else if (section == "vehicleRc") {
                            openGalleryRc()
                        }
                    }
                }

                options[item] == "Cancel" -> dialog.dismiss()
            }
        }
        builder.show()
    }


    private fun openGalleryRc() {
        val galleryIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*" // Allow all image types
        selectImageLauncherRc.launch(galleryIntent)
    }

    private fun openGalleryDoc() {
        val galleryIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*" // Allow all image types
        selectImageLauncherDoc.launch(galleryIntent)
    }


    private fun openCameraRc() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(this.packageManager) != null) {
            takePictureLauncherRc.launch(cameraIntent)
        }
    }

    private fun openCameraDoc() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(this.packageManager) != null) {
            takePictureLauncherDoc.launch(cameraIntent)
        }
    }

    private val takePictureLauncherRc =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                if (imageBitmap != null) {
                    // Generate a dynamic filename using a unique identifier
                    val fileName = "image_${System.currentTimeMillis()}.jpg"
                    // Convert Bitmap to File
                    selectedImageFileRc = bitmapToFile(imageBitmap, fileName)
                    Log.e(
                        "filePathBack",
                        "selectedImageFile:Front " + selectedImageFileRc
                    )
                    //OMCAApp.encryptedPrefs.frontImagePath = imageFile.toString()
                    binding.cameraImgRcUpdate.setImageBitmap(imageBitmap)
                    //  setUploadedRc()
                } else {
                    Toast.makeText(this, "Image capture canceled", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }


    private val takePictureLauncherDoc =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                if (imageBitmap != null) {
                    // Generate a dynamic filename using a unique identifier
                    val fileName = "image_${System.currentTimeMillis()}.jpg"
                    // Convert Bitmap to File
                    selectedImageFileDoc = bitmapToFile(imageBitmap, fileName)
                    Log.e(
                        "filePathBack",
                        "selectedImageFile:Front " + selectedImageFileDoc
                    )
                    //OMCAApp.encryptedPrefs.frontImagePath = imageFile.toString()
                    binding.cameraImgDocUpdate.setImageBitmap(imageBitmap)

                } else {
                    Toast.makeText(this, "Image capture canceled", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    private fun openGallery() {
        val galleryIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*" // Allow all image types
        selectImageLauncher.launch(galleryIntent)
    }
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(this.packageManager) != null) {
            takePictureLauncher.launch(cameraIntent)
        }
    }

    private val selectImageLauncherRc =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val selectedImageUri = data.data
                    if (selectedImageUri != null) {
                        selectedImageFileRc = convertUriToFile(selectedImageUri)
                        if (selectedImageFileRc != null) {
                            // Now you have the image file in File format, you can use it as needed.
                            setImageOnImageViewRc(selectedImageFileRc)

                            // Load the image using Glide
                            val imagePath =
                                selectedImageFileRc!!.absolutePath
                            Glide.with(this)
                                .load(imagePath)
                                .placeholder(R.drawable.home_bg)
                                .into(binding.cameraImgRcUpdate)


                        }
                    }
                }
            }
        }

    private val selectImageLauncherDoc =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val selectedImageUri = data.data
                    if (selectedImageUri != null) {
                        selectedImageFileDoc = convertUriToFile(selectedImageUri)
                        if (selectedImageFileDoc != null) {
                            // Now you have the image file in File format, you can use it as needed.
                            setImageOnImageViewDoc(selectedImageFileDoc)

                            // Load the image using Glide
                            val imagePath =
                                selectedImageFileDoc!!.absolutePath
                            Glide.with(this)
                                .load(imagePath)
                                .placeholder(R.drawable.home_bg)
                                .into(binding.cameraImgDocUpdate)


                        }
                    }
                }
            }
        }

    private fun setImageOnImageViewRc(imageFile: File?) {
        if (imageFile != null) {
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            // Set the Bitmap to the ImageView
            binding.cameraImgRcUpdate.setImageBitmap(bitmap)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        }
    }


    private fun setImageOnImageViewDoc(imageFile: File?) {
        if (imageFile != null) {
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            // Set the Bitmap to the ImageView
            binding.cameraImgDocUpdate.setImageBitmap(bitmap)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        }
    }

    private fun setImageOnImageView(imageFile: File?) {
        if (imageFile != null) {
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            // Set the Bitmap to the ImageView
            binding.cameraImgVIUpdate.setImageBitmap(bitmap)
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
                                .into(binding.cameraImgVIUpdate)


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
                    // Generate a dynamic filename using a unique identifier
                    val fileName = "image_${System.currentTimeMillis()}.jpg"
                    // Convert Bitmap to File
                    selectedImageFile = bitmapToFile(imageBitmap, fileName)
                    Log.e(
                        "filePathBack",
                        "selectedImageFile:Front " + selectedImageFile
                    )
                    //OMCAApp.encryptedPrefs.frontImagePath = imageFile.toString()
                    binding.cameraImgVIUpdate.setImageBitmap(imageBitmap)

                } else {
                    Toast.makeText(this, "Image capture canceled", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }


    private fun bitmapToFile(bitmap: Bitmap, fileName: String): File {
        // Create a new file in the app's cache directory
        val file = File(this.cacheDir, fileName)

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
        val inputStream: InputStream? = this.contentResolver.openInputStream(uri)
        if (inputStream != null) {
            val fileName = getFileNameFromUri(uri)
            val outputFile = File(this.cacheDir, fileName)
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
        val cursor = this.contentResolver.query(uri, null, null, null, null)
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
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    cameraPermissionCode
                )
            }
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    galleryPermissionCode
                )
            }
        }
    }

    private fun putUpdateDetailsObserver() {
        putVDetailsViewModel.mPutApprovalResponse.observe(this) { response ->
            val message = response.peekContent().msg!!
            if (response.peekContent().status == "False") {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            } else {
                // The condition is true, perform actions here

                Toast.makeText(this, message, Toast.LENGTH_LONG)
                    .show()
                getVehicleDetails()


            }
        }
    }

    private fun putUpdateDetails() {
        val vehicleNo =
            RequestBody.create(MultipartBody.FORM, binding.vehicleNoAddUpdate.text.toString())
        val vehiclePhoto = selectedImageFile?.let {
            it.asRequestBody("image/*".toMediaTypeOrNull())
        }?.let {
            MultipartBody.Part.createFormData(
                "vehicle_photo",
                selectedImageFile!!.name,
                it
            )
        }

        val document = selectedImageFileDoc?.let {
            MultipartBody.Part.createFormData(
                "document",
                selectedImageFileDoc!!.name,
                it.asRequestBody("application/*".toMediaTypeOrNull())
            )
        }

        val drivingLicense = selectedImageFileRc?.let {
            MultipartBody.Part.createFormData(
                "driving_license",
                selectedImageFileRc!!.name,
                it.asRequestBody("application/*".toMediaTypeOrNull())
            )
        }

        vehiclePhoto?.let {
            document?.let { it1 ->
                drivingLicense?.let { it2 ->
                    putVDetailsViewModel.putUpdateReqApprovaldata(
                        progressDialog,
                        this,
                        vehicleNo,
                        it,
                        it1,
                        it2
                    )
                }
            }
        }
    }
    private fun getVehicleDetails() {
        getVDetailsViewModel.getVDetailsData(
            progressDialog,
            this
        )
    }
    private fun getVehicleDetailsObserver() {
        getVDetailsViewModel.progressIndicator.observe(this, Observer {
            // Handle progress indicator changes if needed
        })

        getVDetailsViewModel.mGetVDetails.observe(this) { response ->
            val data = response.peekContent().data
            shipmentName = data!!.shipmentname.toString()
            vehicleName = data!!.vehiclename.toString()

            val baseUrl = "http://69.49.235.253:8090"
            val imagePath = data?.vehiclePhoto.orEmpty()
            val imagePathDoc = data?.document.orEmpty()
            val imagePathRc = data?.drivingLicense.orEmpty()

            imageUrlVp = "$baseUrl$imagePath"
            imageUrlVd = "$baseUrl$imagePathDoc"
            imageUrlVRc = "$baseUrl$imagePathRc"

            if (imageUrlVp!!.isNotEmpty()) {
                Glide.with(this)
                    .load(imageUrlVp)
                    .into(binding.cameraImgVIUpdate)

                if (imageUrlVd!!.isNotEmpty()) {
                    Glide.with(this)
                        .load(imageUrlVd)
                        .into(binding.cameraImgDocUpdate)

                    if (imageUrlVRc!!.isNotEmpty()) {
                        Glide.with(this)
                            .load(imageUrlVRc)
                            .into(binding.cameraImgRcUpdate)

                    }

                    binding.vehicleNoAddUpdate.setText(data.vehiclenumber.toString())
                    binding.shipmentTextview.text = data.shipmentname.toString()
                    binding.vehicleTypeTextview.text = data.vehiclename.toString()
                    getVDetailsViewModel.errorResponse.observe(this) {
                        // Handle general errors
                        ErrorUtil.handlerGeneralError(this, it)
                    }
                }
            }
        }
    }
}