package com.pasco.pascocustomer.customer.activity.vehicledetailactivity

import VehicleTypeResponse
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
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
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.AddVehicle.ServiceListViewModel.ServicesViewModel
import com.pasco.pascocustomer.Driver.DriverDashboard.Ui.DriverDashboardActivity
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.activity.Driver.AddVehicle.ApprovalRequest.ApprovalRequestViewModel
import com.pasco.pascocustomer.activity.Driver.AddVehicle.VehicleType.VehicleTypeViewModel
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.customer.activity.vehicledetailactivity.adddetailsmodel.ServicesResponse
import com.pasco.pascocustomer.customer.activity.vehicledetailactivity.vehicletype.GetVehicleTypeBody
import com.pasco.pascocustomer.databinding.ActivityVehicleDetailsBinding
import com.pasco.pascocustomer.language.Originator
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
import java.util.*

@AndroidEntryPoint
class VehicleDetailsActivity : Originator() {
    private lateinit var binding: ActivityVehicleDetailsBinding

    //private
    private var selectedImageFile: File? = null
    private var selectedImageFileDoc: File? = null
    private var selectedImageFileRc: File? = null
    private val cameraPermissionCode = 101
    private val galleryPermissionCode = 102

    private var spinnerTransportId = ""
    private var spinnerVehicleTypeId = ""
    private var vehicleSize = ""
    private var vehicleLoadCapacity = ""
    private var vehicleCapability = ""
    private val progressDialog by lazy { CustomProgressDialog(this) }

    private var servicesType: List<ServicesResponse.ServicesResponseData>? = null
    private val servicesTypeStatic: MutableList<String> = mutableListOf()
    private var VehicleType: List<VehicleTypeResponse.VehicleTypeData>? = null
    private val vehicleTypeStatic: MutableList<String> = mutableListOf()

    private val servicesViewModel: ServicesViewModel by viewModels()
    private val vehicleTypeViewModel: VehicleTypeViewModel by viewModels()
    private val approvalRequestViewModel: ApprovalRequestViewModel by viewModels()

    private lateinit var sharedPreferencesLanguageName: SharedPreferences
    private var language = ""
    private var languageId = ""
    private var accessToken = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehicleDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Request camera and gallery permissions if not granted
        requestPermission()



        sharedPreferencesLanguageName = getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
        language = sharedPreferencesLanguageName.getString("language_text", "").toString()
        languageId = sharedPreferencesLanguageName.getString("languageId", "").toString()

        accessToken = PascoApp.encryptedPrefs.bearerToken

        Log.e("accessTokenAa", "accessToken..$accessToken")

        binding.backImageaddVeh.setOnClickListener { finish() }
        if (Objects.equals(language, "ar")) {
            binding.vehicleNoAdd.gravity = Gravity.RIGHT
            binding.backImageaddVeh.setImageResource(R.drawable.next)

        }

        binding.submitBtnAddVeh.setOnClickListener {
            validation()
        }
        binding.selectVehicle.setOnClickListener {
            openCameraOrGallery("vehicleImg")
        }
        // OnClickListener for the second ImageView (document)
        binding.selectDrivingDoc.setOnClickListener {
            openCameraOrGallery("document")
        }
        // OnClickListener for the third ImageView (vehicle RC)
        binding.selectVehicleRc.setOnClickListener {
            openCameraOrGallery("vehicleRc")
        }

        binding.transporterSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View?,
                    i: Int,
                    l: Long
                ) {
                    // Toast.makeText(activity, "Country Spinner Working **********", Toast.LENGTH_SHORT).show()

                    val item = binding.transporterSpinner.selectedItem.toString()
                    if (item == getString(R.string.selectTransType)) {

                    } else {
                        spinnerTransportId = servicesType?.get(i)?.shipmentid.toString()
                        Log.e("onItemSelected", spinnerTransportId)

                        //call vehicleType
                        if (!spinnerTransportId.isNullOrBlank()) {
                            val sId = spinnerTransportId
                            callVehicleType(sId)
                        }
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                    // Do nothing
                }
            }


        //call servicesListApi
        servicesList()
        //call services Observer
        servicesObserver()
        //call vehicleTypeObserver
        vehicleTypeObserver()
        //call api
        approvalRequestObserver()

    }

    private fun servicesList() {
        Log.e("LanguageIAAA", "languageId...$languageId $accessToken")
        val getVehicleTypeBody = GetVehicleTypeBody(
            language = languageId
        )
        servicesViewModel.getServicesData(
            progressDialog,
            this,
            getVehicleTypeBody
        )
    }

    private fun servicesObserver() {
        servicesViewModel.progressIndicator.observe(this, Observer {
            // Handle progress indicator changes if needed
        })

        servicesViewModel.mGetServices.observe(this) { response ->
            val content = response.peekContent()
            val message = content.msg ?: return@observe
            servicesType = content.data

            // Clear the list before adding new items
            servicesTypeStatic.clear()

            for (element in servicesType!!) {
                element.shipmentname?.let { it1 -> servicesTypeStatic.add(it1) }
            }
            val dAdapter =
                SpinnerAdapter(
                    this@VehicleDetailsActivity,
                    R.layout.custom_service_type_spinner,
                    servicesTypeStatic
                )
            dAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            //dAdapter.addAll(strCatNameList)
            dAdapter.add(getString(R.string.selectTransType))
            binding.transporterSpinner.adapter = dAdapter
            binding.transporterSpinner.setSelection(dAdapter.count)
            binding.transporterSpinner.setSelection(dAdapter.getPosition(getString(R.string.selectTransType)))


            if (response.peekContent().status.equals("False")) {
                Toast.makeText(this@VehicleDetailsActivity, message, Toast.LENGTH_LONG).show()
            } else {

            }
        }

        servicesViewModel.errorResponse.observe(this) {
            // Handle general errors
            ErrorUtil.handlerGeneralError(this, it)
        }
    }


    private fun callVehicleType(sId: String) {
        vehicleTypeViewModel.getVehicleTypeData(
            progressDialog,
            this,
            sId,
            languageId
        )
    }

    private fun vehicleTypeObserver() {
        vehicleTypeViewModel.mVehicleTypeResponse.observe(this) { response ->
            val content = response.peekContent()
            val message = content.msg ?: return@observe
            VehicleType = content.data
            vehicleTypeStatic.clear()

            for (element in VehicleType!!) {
                element.vehiclename?.let { it1 -> vehicleTypeStatic.add(it1) }
            }
            val dAdapter = SpinnerAdapter(
                this@VehicleDetailsActivity,
                R.layout.custom_service_type_spinner,
                vehicleTypeStatic
            )
            dAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dAdapter.add(getString(R.string.selectVehicleType))
            binding.vehicleTypeSpinner.adapter = dAdapter
            binding.vehicleTypeSpinner.setSelection(dAdapter.count)
            binding.vehicleTypeSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>?,
                        view: View?,
                        i: Int,
                        l: Long
                    ) {
                        val item = binding.vehicleTypeSpinner.selectedItem.toString()
                        if (item != getString(R.string.selectVehicleType)) {
                            spinnerVehicleTypeId = VehicleType!![i].uniqueCode.toString()
                            vehicleSize = VehicleType!![i].vehiclesize.toString()
                            vehicleLoadCapacity = VehicleType!![i].vehicleweight.toString()
                            //vehicleCapability = VehicleType!![i].capabilityname.toString()


                        }
                    }

                    override fun onNothingSelected(adapterView: AdapterView<*>?) {
                        // Do nothing
                    }
                }

            if (response.peekContent().status.equals("False")) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                //   binding.linearVehDetails.visibility = View.GONE
            } else if (response.peekContent().status.equals("True")) {

            }
        }

        vehicleTypeViewModel.errorResponse.observe(this) {
            // Handle general errors
            ErrorUtil.handlerGeneralError(this, it)
        }
    }

    private fun reqApproval() {
        val vehicleNo =
            binding.vehicleNoAdd.text.toString().toRequestBody(MultipartBody.FORM)
        val vehicleTy = spinnerVehicleTypeId.toRequestBody(MultipartBody.FORM)
        val languageId = languageId.toRequestBody(MultipartBody.FORM)
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
                    approvalRequestViewModel.getReqApproval(
                        progressDialog,
                        this,
                        vehicleTy,
                        vehicleNo,
                        it,
                        it1,
                        it2,
                        languageId
                    )
                }
            }
        }
    }

    private fun approvalRequestObserver() {
        approvalRequestViewModel.mApprovalResponse.observe(this) { response ->
            val message = response.peekContent().msg!!
            if (response.peekContent().status == "False") {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            } else {
                val data = response.peekContent().data
                PascoApp.encryptedPrefs.approvalStatus ="True"
                PascoApp.encryptedPrefs.driverApprovedId = data!!.approved?.toString()!!
                // The condition is true, perform actions here
                Toast.makeText(this@VehicleDetailsActivity, message, Toast.LENGTH_LONG)
                    .show()
                val intent =
                    Intent(this@VehicleDetailsActivity, DriverDashboardActivity::class.java)
                startActivity(intent)


            }
        }

        approvalRequestViewModel.errorResponse.observe(this) {
            // Handle general errors
            ErrorUtil.handlerGeneralError(this, it)
        }
    }

    private fun validation() {
        if (binding.transporterSpinner.selectedItem.toString() == resources.getString(R.string.selectTransType)) {
            Toast.makeText(this, getString(R.string.Please_Select_Transporter), Toast.LENGTH_SHORT)
                .show()
        } else if (binding.vehicleTypeSpinner.selectedItem.toString() == resources.getString(
                R.string.selectVehicleType
            )
        ) {
            Toast.makeText(this, getString(R.string.Please_Select_Vehicle), Toast.LENGTH_SHORT)
                .show()
        } else if (binding.vehicleNoAdd.text.isNullOrBlank()) {
            Toast.makeText(this, getString(R.string.Please_add_vehicle_no), Toast.LENGTH_SHORT)
                .show()
        } else if (selectedImageFile == null) {
            Toast.makeText(
                applicationContext,
                getString(R.string.please_upload_vehicle_photo),
                Toast.LENGTH_SHORT
            )
                .show()
        } else if (selectedImageFileDoc == null) {
            Toast.makeText(
                applicationContext,
                getString(R.string.please_upload_Doc),
                Toast.LENGTH_SHORT
            )
                .show()
        } else if (selectedImageFileRc == null) {
            Toast.makeText(
                applicationContext,
                getString(R.string.please_upload_vehicle_reg_no),
                Toast.LENGTH_SHORT
            )
                .show()
        } else {
            // Call the API for approval request
            reqApproval()
        }
    }

    class SpinnerAdapter(
        context: Context, textViewResourceId: Int, smonking: List<String>
    ) :
        ArrayAdapter<String>(context, textViewResourceId, smonking) {

        override fun getCount(): Int {
            val count = super.getCount()
            return if (count > 0) count - 1 else count
        }
    }


    private fun openCameraOrGallery(section: String) {
        val options =
            arrayOf<CharSequence>(
                getString(R.string.take_photo),
                getString(R.string.gallery),
                getString(R.string.cancel)
            )
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.select_image))
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == getString(R.string.take_photo) -> {
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

                options[item] == getString(R.string.gallery) -> {
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

                options[item] == getString(R.string.cancel) -> dialog.dismiss()
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
                    binding.cameraImgRc.setImageBitmap(imageBitmap)
                    //  setUploadedRc()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.Image_capture_canceled),
                        Toast.LENGTH_SHORT
                    )
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
                    binding.cameraImgDoc.setImageBitmap(imageBitmap)

                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.Image_capture_canceled),
                        Toast.LENGTH_SHORT
                    )
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
                                .into(binding.cameraImgRc)


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
                                .into(binding.cameraImgDoc)


                        }
                    }
                }
            }
        }

    private fun setImageOnImageViewRc(imageFile: File?) {
        if (imageFile != null) {
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            // Set the Bitmap to the ImageView
            binding.cameraImgRc.setImageBitmap(bitmap)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        }
    }


    private fun setImageOnImageViewDoc(imageFile: File?) {
        if (imageFile != null) {
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            // Set the Bitmap to the ImageView
            binding.cameraImgDoc.setImageBitmap(bitmap)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        }
    }

    private fun setImageOnImageView(imageFile: File?) {
        if (imageFile != null) {
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            // Set the Bitmap to the ImageView
            binding.cameraImgVI.setImageBitmap(bitmap)
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
                                .into(binding.cameraImgVI)


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
                    binding.cameraImgVI.setImageBitmap(imageBitmap)

                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.Image_capture_canceled),
                        Toast.LENGTH_SHORT
                    )
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
}