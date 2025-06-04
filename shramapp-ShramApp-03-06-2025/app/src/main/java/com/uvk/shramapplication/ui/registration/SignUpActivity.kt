package com.uvk.shramapplication.ui.registration

import android.Manifest
import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.annotation.SuppressLint
//import android.R
import com.uvk.shramapplication.R
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AlertDialog.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.contentValuesOf
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.ActivitySignUpBinding
import com.uvk.shramapplication.helper.CommenViewModel
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.response.DistrictData
import com.uvk.shramapplication.response.StateData
import com.uvk.shramapplication.ui.category.Category
import com.uvk.shramapplication.ui.login.LoginActivity
import com.uvk.shramapplication.ui.main_category.MainCategory
import com.uvk.shramapplication.ui.subcategory.SubcategoryModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.isuserlgin
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.roleId
import com.uvk.shramapplication.MainActivity
import com.uvk.shramapplication.MainActivity.Companion
import com.uvk.shramapplication.helper.Education
import com.uvk.shramapplication.helper.GenderType
import com.uvk.shramapplication.helper.TranslationHelper
import com.uvk.shramapplication.ui.map.EmpGoogleMapActivity
import com.uvk.shramapplication.ui.map.GoogleMapActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    private val viewModel by viewModels<StateDTViewModel>()
    private val commenviewModel by viewModels<CommenViewModel>()
    private val signUpviewModel by viewModels<SignupViewModel>()
    private lateinit var stateList: List<StateData>
    private lateinit var districtList: List<DistrictData>
    private lateinit var categoryList: List<Category>
    private lateinit var subCategoryList: List<SubcategoryModel>
    private lateinit var mainCatList: List<MainCategory>
    private lateinit var genderList: List<GenderType>
    private lateinit var educationList: List<Education>

    private val selectedRoles: MutableList<String> = mutableListOf()
    private val selectedCatIds: MutableList<Int> = mutableListOf()
    private val selectedSubCatIds: MutableList<Int> = mutableListOf()

    private lateinit var selectedSubCatItems: BooleanArray
    private lateinit var selectedCatItems: BooleanArray


    var salaryTypeId: Int = 0
    var salaryRangeId: Int = 0

    var role: String? = null

    var genderId: Int = 0
    var genderName: String = " "
    var educationId: Int = 0
    var educationName: String = " "

    var stateId: Int = 0
    var stateName: String = " "


    var distId: Int = 0
    var distName: String = ""

    var mainCatName: String = ""
    var selectedTransmainCatName: String = ""
    var mainCatId: String = ""

    val TAG = "SignUpActivity"

    private var aadharBase64String: String? = null
    private var profileBase64String: String? = null
    var pd: TransparentProgressDialog? = null

    private var selectedFileUri: Uri? = null
    private var selectedFile: File? = null
    private lateinit var cameraImageUri: Uri

    private val designationMap: Map<String, List<String>> = mapOf(
        "Construction" to listOf("Site Supervisor", "Mason", "Civil Engineer", "Laborer", "Crane Operator", "Surveyor"),
        "Agricultural" to listOf("Farm Worker", "Irrigation Technician", "Crop Harvester", "Tractor Operator", "Agricultural Supervisor"),
        "Manufacturing" to listOf("Machine Operator", "Quality Inspector", "Assembler", "Production Supervisor", "Fitter"),
        "Hospitality" to listOf("Hotel Receptionist", "Housekeeping Staff", "Chef", "Steward", "Front Office Executive", "Room Service Attendant"),
        "HealthCare" to listOf("Nurse", "Ward Boy", "Caretaker", "Lab Technician", "Medical Assistant", "Pharmacist"),
        "Daily Basis Jobs" to listOf("Daily Laborer", "Helper", "Construction Assistant", "Delivery Helper", "Cleaning Staff"),
        "Weekly Basis Jobs" to listOf("Weekly Contract Laborer", "Packaging Assistant", "Warehouse Helper", "Event Staff", "House Cleaner"),
        "Domestic Work" to listOf("Maid", "Cook", "Nanny", "House Cleaner", "Elderly Caretaker"),
        "Other" to listOf("Miscellaneous Worker", "General Assistant", "Multi-tasking Staff", "Helper"),
        "Logistic Packaging" to listOf("Packer", "Loader/Unloader", "Sorting Staff", "Packaging Supervisor", "Warehouse Assistant"),
        "Ware House Workers" to listOf("Warehouse Picker", "Inventory Manager", "Forklift Operator", "Storekeeper", "Packing Staff"),
        "Security" to listOf("Security Guard", "Watchman", "Bouncer", "CCTV Operator", "Supervisor (Security)"),
        "Driver" to listOf("Light Vehicle Driver", "Heavy Vehicle Driver", "Personal Driver", "Delivery Driver", "Auto Driver"),
        "Electrician" to listOf("Residential Electrician", "Industrial Electrician", "Electrical Technician", "Wireman", "Electrical Supervisor"),
        "Plumber" to listOf("Residential Plumber", "Pipe Fitter", "Plumbing Helper", "Sanitary Technician"),
        "Carpenter" to listOf("Furniture Carpenter", "Finishing Carpenter", "Shuttering Carpenter", "Wood Polisher", "Cabinet Maker"),
        "Painter" to listOf("Wall Painter", "Industrial Painter", "Spray Painter", "Decorative Painter"),
        "Garage Mechanic" to listOf("Two-Wheeler Mechanic", "Four-Wheeler Mechanic", "Auto Electrician", "Engine Specialist", "Assistant Mechanic"),
        "Home Appliance Repair" to listOf("AC Technician", "Washing Machine Technician", "Refrigerator Repair Technician", "TV Repair Technician", "Microwave Technician"),
        "Transport" to listOf("Delivery Executive", "Transport Coordinator", "Vehicle Loader", "Fleet Manager", "Logistic Support Staff"),
        "Laundry" to listOf("Washerman", "Ironing Staff", "Laundry Supervisor", "Dry Cleaning Operator")
    )


    private suspend fun getTranslatedDesignationMap(languageName: String): Map<String, List<String>> =
        withContext(Dispatchers.Default) {
            designationMap.mapValues { (_, designations) ->
                designations.map { designation ->
                    async {
                        TranslationHelper.translateText(designation, languageName)
                    }
                }.awaitAll()
            }
        }





    private lateinit var currentImageView: ImageView

    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2

    private val REQUIRED_PERMISSIONS = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.READ_MEDIA_IMAGES // For Android 13+
    )

    companion object {
        const val MY_FINE_LOCATION_REQUEST = 99
        private const val MY_BACKGROUND_LOCATION_REQUEST = 100
        private const val STOP_SERVICE_REQUEST_CODE = 123
        private const val REQUEST_SCHEDULE_EXACT_ALARM_PERMISSION = 101
        private const val LOCATION_PERMISSION_REQUEST_CODE = 102
        private val REQUEST_CODE_SCHEDULE_EXACT_ALARM = 1234
        private var REQUEST_LOCATION_CODE = 101
    }


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pd = TransparentProgressDialog(this, com.uvk.shramapplication.R.drawable.progress)

        binding.backButton.setOnClickListener {
            finish()
        }

        lifecycleScope.launch {
            binding.etExperiance.hint = TranslationHelper.translateText(
                "Ex. 2 years experience",
                languageName
            )
            binding.tvJobCat.hint = TranslationHelper.translateText(
                "Select Category",
                languageName
            )

            binding.tvsubCategory.hint = TranslationHelper.translateText(
                "Select Sub Category",
                languageName
            )
        }



        binding.tvSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        checkAndRequestPermissions()
        mapPermission()

        binding.ivCamedit.setOnClickListener {
            currentImageView = binding.ivProfileImg

            showPictureDialog()
        }

        binding.llUpload.setOnClickListener {
            currentImageView = binding.ivAadharImage

            showPictureDialog()


        }

        binding.etSkill.visibility = View.GONE
        binding.llExpe.visibility = View.GONE
        binding.tvMCat.visibility = View.GONE
        binding.llCat.visibility = View.GONE
        binding.llsubCat.visibility = View.GONE
        binding.tvMSubcat.visibility = View.GONE
        binding.rlEdu.visibility = View.GONE
        binding.tvSkill.visibility = View.GONE
        binding.tvMainCatHeading.visibility = View.GONE
        binding.rlMainCat.visibility = View.GONE

        binding.rdGroup.setOnCheckedChangeListener { group, checkedId ->
            // Find the selected RadioButton
            val selectedRadioButton = findViewById<RadioButton>(checkedId)

            // Get the text or ID of the selected RadioButton
            val selectedText = selectedRadioButton.text.toString()
            val selectedId = selectedRadioButton.id

            // if (selectedText.equals("Job Giver")) {
            if (selectedText == getString(com.uvk.shramapplication.R.string.job_giver)) {
                role = "2"
                //   binding.etDesignation.visibility = View.GONE
                binding.etCompanyName.visibility = View.VISIBLE
                //  binding.etSkill.visibility = View.GONE
                //  binding.tvSkill.visibility = View.GONE
                binding.rlEdu.visibility = View.GONE
                binding.tvMCat.visibility = View.GONE
                binding.llCat.visibility = View.GONE
                //  binding.llsubCat.visibility = View.GONE
                //   binding.tvMSubcat.visibility = View.GONE
                binding.llExpe.visibility = View.GONE
                binding.tvMainCatHeading.visibility = View.VISIBLE
                binding.rlMainCat.visibility = View.VISIBLE

                //  Clear Main Category selection and related views
                binding.spinnerMainCat.setSelection(0)
                mainCatId = ""
                selectedTransmainCatName = ""
                binding.etRoles.visibility = View.GONE
                binding.cGJobCat.removeAllViews()
                binding.cGSubCat.removeAllViews()
                selectedCatIds.clear()
                selectedSubCatIds.clear()

                lifecycleScope.launch {
                    val msg = TranslationHelper.translateText(
                        "Upload Your Company Aadhar Card.",
                        languageName
                    )
                    binding.tvFileName.text = msg
                }


            } else {
                role = "3"
                //  binding.etDesignation.visibility = View.VISIBLE
                binding.etCompanyName.visibility = View.GONE
                //  binding.etSkill.visibility = View.VISIBLE
                //  binding.tvSkill.visibility = View.VISIBLE
                binding.rlEdu.visibility = View.VISIBLE
                binding.llExpe.visibility = View.VISIBLE
                binding.tvMCat.visibility = View.VISIBLE
                binding.llCat.visibility = View.VISIBLE
                //  binding.llsubCat.visibility = View.VISIBLE
                // binding.tvMSubcat.visibility = View.VISIBLE
                binding.tvMainCatHeading.visibility = View.VISIBLE
                binding.rlMainCat.visibility = View.VISIBLE

                //  Clear Main Category selection and related views
                binding.spinnerMainCat.setSelection(0)
                mainCatId = ""
                selectedTransmainCatName = ""
                binding.etRoles.visibility = View.GONE
                binding.cGJobCat.removeAllViews()
                binding.cGSubCat.removeAllViews()
                selectedCatIds.clear()
                selectedSubCatIds.clear()

                lifecycleScope.launch {
                    val msg = TranslationHelper.translateText(
                        "Upload Your Aadhar Card.",
                        languageName
                    )
                    binding.tvFileName.text = msg
                }

            }

            // Print the name and ID
            Log.d("RadioGroup", "Selected Text: $selectedText, Selected ID: $selectedId")
            // Toast.makeText(this, "Selected: $selectedText", Toast.LENGTH_SHORT).show()
        }

        getGenderList()
        getEducationList()
        getStateList()
        getMainCategory()
        observeCategory()
        observeSubCategory()


        binding.etDesignation.setOnClickListener {
            binding.etDesignation.showDropDown()
        }



        binding.tvJobCat.setOnClickListener {
            if (!mainCatId.isNullOrEmpty()) { // Ensures the list is not null and not empty
                commenviewModel.fetchcategories(mainCatId)
            } else {
                Toast.makeText(
                    this@SignUpActivity,
                    "Please select " + getString(R.string.main_category),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.tvsubCategory.setOnClickListener {
            if (!selectedCatIds.isNullOrEmpty()) {
                commenviewModel.fetchsubcategories(selectedCatIds)
            } else {
                Toast.makeText(
                    this@SignUpActivity,
                    "Please select " + getString(R.string.job_category),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }



        binding.btnSubmit.setOnClickListener {

            //startActivity(Intent(this@SignUpActivity, FilterActivity::class.java))
            if (validateInputs()) {

                submitData()

            }
        }
    }

    private fun mapPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {

                    AlertDialog.Builder(this).apply {
                        setTitle(R.string.background_permission)
                        setMessage(R.string.location_permission)
                        setPositiveButton(R.string.accept,
                            DialogInterface.OnClickListener { dialog, id ->
                                requestBackgroundLocationPermission()
                                startForegroundPermission()

                                Log.e(TAG, "*permission request*")


                            })
                        setNeutralButton(R.string.deny,
                            DialogInterface.OnClickListener { dialog, id ->
                                dialog.dismiss();
                                Log.e(TAG, "*permission deny request*")
                            })
                    }.create().show()

                } else if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    Log.e(TAG, "*permission granted*")
                    Log.e(TAG, "GO Map 1")
                }
            } else {

                Log.e(TAG, "*permission granted*")
                Log.e(TAG, "GO Map 2")

            }

        } else if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                lifecycleScope.launch {
                    val translatedTitle = TranslationHelper.translateText(
                        "ACCESS FINE LOCATION", languageName
                    )
                    val translatedMessage = TranslationHelper.translateText(
                        "Location permission required", languageName
                    )

                    AlertDialog.Builder(this@SignUpActivity)
                        .setTitle(translatedTitle)
                        .setMessage(translatedMessage)
                        .setPositiveButton(
                            "OK"
                        ) { _, _ ->
                            requestFineLocationPermission()

                        }
                        .create()
                        .show()
                }
            } else {
                requestFineLocationPermission()
            }

        }
    }

    private fun requestBackgroundLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(ACCESS_BACKGROUND_LOCATION), MY_BACKGROUND_LOCATION_REQUEST
        )
    }

    private fun requestFineLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            MY_FINE_LOCATION_REQUEST
        )
    }

    private fun startForegroundPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+ (API level 33), check both LOCATION and FOREGROUND_SERVICE_LOCATION permissions
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.FOREGROUND_SERVICE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                // Request the missing permissions
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.FOREGROUND_SERVICE_LOCATION
                    ),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            } else {
                Log.e(TAG, "Permissions are granted, start the foreground service")
                // Permissions are granted, start the foreground service
                // startForegroundServiceWithLocation()
            }
        } else {
            // For Android versions lower than 13, check the normal location permissions
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                // Request the location permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            } else {
                Log.e(TAG, "Permissions are granted, start the foreground service")
                // Permissions are granted, start the foreground service
                //   startForegroundServiceWithLocation()
            }
        }

    }


    private fun submitData() {
        try {
            if (isOnline) {

                val roleText = binding.etRoles.text.toString().trim()
                if (roleText.isNotEmpty()) {
                    selectedRoles.add(roleText)
                    Log.e("role", "Added Role: $roleText")
                } else {
                    Log.e("role", "Input is empty")
                }

                Log.e("role", "Roles: $selectedRoles")

                val name = binding.etName.text.toString().trim()
                val mobileNo = binding.etMobile.text.toString().trim()
                val email = binding.etEmail.text.toString().trim()
                val address = binding.etAddress.text.toString().trim()
                val state = stateId
                val district = distId
                val companyName = binding.etCompanyName.text.toString().trim()
                val pincode = binding.etPincode.text.toString().trim()
                val gender = genderId
                val education = educationId
                val experiance = binding.etExperiance.text.toString().trim()
                val mainCatId = mainCatId
                val selectedCatIds = selectedCatIds
                val selectedSubCatIds = selectedSubCatIds
                val designation = binding.etDesignation.text.toString().trim()
                val skill = binding.etSkill.text.toString().trim()
                val role = role
                val profileBase64String = profileBase64String
                val aadharBase64String = aadharBase64String
                //val other_category_name = selectedRoles
                val other_category_name = binding.etRoles.text.toString().trim()

                pd?.show() // Show loading indicator
                // Observe response once
                signUpviewModel.signUpResult.removeObservers(this)
                signUpviewModel.signUpResult.observe(this) { response ->

                    when (response) {
                        is BaseResponse.Loading -> {
                            pd?.show()
                        }

                        is BaseResponse.Success -> {
                            pd?.dismiss()
                            response.data?.let { data ->
                                Log.e(TAG, "Code: ${data.code}")
                                Log.e(TAG, "Status: ${data.status}")
                                Log.e(TAG, "Message: ${data.message}")

                                Toast.makeText(this, data.message, Toast.LENGTH_SHORT).show()

                                if (data.code == "200") {
                                   startActivity(Intent(this, LoginActivity::class.java))
                                    finish()
                                }
                            }
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss()
                            Log.e(TAG, "Error: ${response.msg ?: ""}")
                            Toast.makeText(this, "Error: ${response.msg ?: ""}", Toast.LENGTH_SHORT)
                                .show()
                        }

                        else -> {
                            pd?.dismiss()
                            Log.e(TAG, "Unexpected response: $response")
                        }
                    }
                }

                signUpviewModel.uploadSignupData(
                    profile_image = profileBase64String.toString(),
                    name = name,
                    mobile_no = mobileNo,
                    email = email,
                    address = address,
                    state = state,
                    district = district,
                    pincode = pincode,
                    gender_id = gender,
                    education_id = education,
                    experience = experiance,
                    main_category_id = mainCatId,
                    category_ids = selectedCatIds,
                    sub_category_ids = selectedSubCatIds,
                    skill = skill,
                    role = role!!,
                    company_name = companyName,
                    designation = designation,
                    aadhar_image = aadharBase64String.toString(),
                    other_category_name = other_category_name
                )

            } else {
                Toast.makeText(
                    this@SignUpActivity,
                    "Internet not connected",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e(TAG, "Error occurred: Signup ${e.localizedMessage}")

        }
    }


    private fun observeSubCategory() {
        if (isOnline) {
            commenviewModel.subcategoryResult.observe(this) { response ->
                pd?.show() // Show loading indicator
                when (response) {
                    is BaseResponse.Success -> {
                        pd?.dismiss() // Dismiss loading indicator
                        Log.d("Job Post", "Response data: ${response.data}") // Log response data

                        if (response.data.isNullOrEmpty()) {
                            Log.e("Job Post", "Subcategory list is empty!")
                            Toast.makeText(this, "No subcategories found.", Toast.LENGTH_SHORT)
                                .show()
                            return@observe
                        }

                        subCategoryList = response.data!! // Assign data
                        Log.d(
                            "Sub Cat",
                            "cat List size: ${subCategoryList.size}"
                        ) // Log the list size

                        selectedSubCatItems = BooleanArray(subCategoryList.size)
                        showcSubCategoryListDialog(
                            title = "Select Job Sub Category",
                            list = subCategoryList,
                            selectedItems = selectedSubCatItems,
                            selectedIds = selectedSubCatIds,
                            chipGroup = binding.cGSubCat
                        )

                    }

                    is BaseResponse.Error -> {
                        pd?.dismiss() // Dismiss loading indicator
                        Toast.makeText(this@SignUpActivity, response.msg ?: "", Toast.LENGTH_SHORT)
                            .show() // Show error message
                    }

                    is BaseResponse.Loading -> {
                        // Show loading indicator if needed
                    }
                }
            }
        } else {
            Toast.makeText(this, "Internet not connected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showcSubCategoryListDialog(
        title: String,
        list: List<SubcategoryModel>,
        selectedItems: BooleanArray,
        selectedIds: MutableList<Int>,
        chipGroup: ChipGroup
    ) {

        list.forEachIndexed { index, experience ->
            selectedItems[index] = selectedIds.contains(experience.id)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setMultiChoiceItems(
                list.map { it.name }.toTypedArray(), // Display experience names as options
                selectedItems // Initial selection state
            ) { _, which, isChecked ->
                selectedItems[which] = isChecked
                if (isChecked) {
                    if (!selectedIds.contains(list[which].id)) {
                        selectedIds.add(list[which].id) // Add ID if not already selected
                    } else {
                        Toast.makeText(
                            this,
                            "${list[which].name} is already selected",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    selectedIds.remove(list[which].id) // Remove ID if unchecked
                }
            }
            .setPositiveButton("OK") { _, _ ->
                // When the user clicks "OK", update the UI with selected items
                chipGroup.removeAllViews() // Clear previous chips
                selectedIds.forEach { id ->
                    val chip = Chip(this)
                    chip.text = list.find { it.id == id }?.name // Display the experience name
                    chip.isCloseIconVisible = true
                    chip.setOnCloseIconClickListener {
                        // Handle chip removal (unselecting an item)
                        selectedIds.remove(id)
                        selectedItems[list.indexOfFirst { it.id == id }] =
                            false // Unselect the item
                        showcSubCategoryListDialog(
                            title,
                            list,
                            selectedItems,
                            selectedIds,
                            chipGroup
                        ) // Refresh dialog
                    }
                    chipGroup.addView(chip) // Add the chip to the ChipGroup
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    /*private fun getMainCategory() {
        commenviewModel.mainCategoryResult.observe(this) { response ->
            pd?.show()
            when (response) {
                is BaseResponse.Loading -> {
                    Log.d("SalaryTypeAPI", "Loading salary types...")
                }

                is BaseResponse.Success -> {
                    pd?.dismiss()
                    response.data?.let { apiResponse ->
                        lifecycleScope.launch {
                            try {

                                mainCatList = response.data

                                Log.d("mainCatList", "Size: ${mainCatList?.size}")

                                val arrayList = ArrayList<String>()
                                var selectedIndex = 0

                                // Translate "Select Main Category"
                                val translatedTitle = async {
                                    TranslationHelper.translateText(
                                        "Select Main Category",
                                        languageName
                                    )
                                }

                                arrayList.add(0, translatedTitle.await()) // Add translated title

                                if (mainCatList != null && mainCatList.isNotEmpty()) {
                                    *//* for (i in mainCatList.indices) {
                                         arrayList.add(mainCatList[i].name)
                                         if (mainCatList[i].name == mainCatName) {
                                             selectedIndex = i + 1
                                         }
                                     }

                                     Log.d("Main Cat", "ArrayList: $arrayList")*//*
                                    // Translate main category names
                                    val translations = mainCatList.mapIndexed { index, item ->
                                        async {
                                            val translatedName = TranslationHelper.translateText(
                                                item.name, languageName
                                            )

                                            if (item.name == mainCatName) {
                                                selectedIndex = index + 1
                                            }
                                            translatedName
                                        }
                                    }

                                    arrayList.addAll(translations.awaitAll()) // Wait for all translations

                                    val arrayAdapter = ArrayAdapter(
                                        this@SignUpActivity,
                                        android.R.layout.simple_spinner_item,
                                        arrayList
                                    )
                                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    binding.spinnerMainCat.adapter = arrayAdapter

                                    Log.d(
                                        "mainCatId",
                                        "Adapter set with ${arrayAdapter.count} items"
                                    )

                                    binding.spinnerMainCat.setSelection(selectedIndex)

                                    binding.spinnerMainCat.onItemSelectedListener =
                                        object : AdapterView.OnItemSelectedListener {
                                            override fun onItemSelected(
                                                parent: AdapterView<*>?,
                                                view: View?,
                                                position: Int,
                                                id: Long
                                            ) {
                                                if (position != 0) {
                                                    val item =
                                                        parent!!.getItemAtPosition(position)
                                                            .toString()
                                                    mainCatId =
                                                        mainCatList[position - 1].id.toString()
                                                    Log.d("mainCatId", "Selected ID: $mainCatId")

                                                    // Clear category & subcategory selections
                                                    selectedCatIds.clear()
                                                    selectedSubCatIds.clear()
                                                    binding.cGJobCat.removeAllViews()
                                                    binding.cGSubCat.removeAllViews()


                                                    val selectedCategory: String = parent!!.getItemAtPosition(position).toString()
                                                    val designationList: List<String> = designationMap[selectedCategory] ?: emptyList()

                                                    val designationAdapter = ArrayAdapter(
                                                        this@SignUpActivity,
                                                        android.R.layout.simple_dropdown_item_1line,
                                                        designationList
                                                    )
                                                    binding.etDesignation.requestFocus()

                                                    binding.etDesignation.visibility = View.VISIBLE
                                                    binding.etDesignation.threshold = 1
                                                    binding.etDesignation.setAdapter(designationAdapter)
                                                   // binding.etDesignation.setText("") // Optional
                                                    binding.etDesignation.post {
                                                        binding.etDesignation.showDropDown() // Optional
                                                    }
                                                    // ✅ optional: clear any previous selection


                                                }
                                            }

                                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                                        }

                                } else {
                                    Log.e("spinnerMainCat", "Empty salaryTypeList from API")
                                }


                            } catch (e: Exception) {
                                Log.e(
                                    "Translation Error",
                                    "Error translating categories: ${e.message}"
                                )
                            }
                        }


                    }
                }

                is BaseResponse.Error -> {
                    pd?.dismiss()
                    Log.e("spinnerMainCat", "Error: ${response.msg ?: ""}")
                }

                else -> {
                    Log.e("spinnerMainCat", "Unhandled case")
                }
            }
        }

        commenviewModel.fetchMainCategories()
    }*/

    /*private fun getMainCategory() {
        commenviewModel.mainCategoryResult.observe(this) { response ->
            pd?.show()
            when (response) {
                is BaseResponse.Loading -> {
                    Log.d("SalaryTypeAPI", "Loading salary types...")
                }

                is BaseResponse.Success -> {
                    pd?.dismiss()
                    response.data?.let { apiResponse ->
                        lifecycleScope.launch {
                            try {
                                mainCatList = response.data
                                Log.d("mainCatList", "Size: ${mainCatList?.size}")

                                val arrayList = ArrayList<String>()
                                var selectedIndex = 0

                                val translatedTitle = async {
                                    TranslationHelper.translateText("Select Main Category", languageName)
                                }
                                arrayList.add(0, translatedTitle.await())

                                // Map: TranslatedName -> OriginalName
                                val translatedToOriginalMap = mutableMapOf<String, String>()

                                if (!mainCatList.isNullOrEmpty()) {
                                    val translations = mainCatList.mapIndexed { index, item ->
                                        async {
                                            val translatedName = TranslationHelper.translateText(
                                                item.name,
                                                languageName
                                            )
                                            if (item.name == mainCatName) {
                                                selectedIndex = index + 1
                                            }
                                            translatedToOriginalMap[translatedName] = item.name
                                            translatedName
                                        }
                                    }

                                    arrayList.addAll(translations.awaitAll())

                                    val arrayAdapter = ArrayAdapter(
                                        this@SignUpActivity,
                                        android.R.layout.simple_spinner_item,
                                        arrayList
                                    )
                                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    binding.spinnerMainCat.adapter = arrayAdapter
                                    binding.spinnerMainCat.setSelection(selectedIndex)

                                    Log.d("mainCatId", "Adapter set with ${arrayAdapter.count} items")

                                    val translatedMap = getTranslatedDesignationMap(languageName)

                                    // ✅ Spinner selection listener
                                    binding.spinnerMainCat.onItemSelectedListener =
                                        object : AdapterView.OnItemSelectedListener {
                                            override fun onItemSelected(
                                                parent: AdapterView<*>?,
                                                view: View?,
                                                position: Int,
                                                id: Long
                                            ) {
                                                if (position != 0) {

                                                    // Set mainCatId
                                                    mainCatId = mainCatList[position - 1].id.toString()
                                                    Log.d("mainCatId", "Selected ID: $mainCatId")

                                                    // Clear selections
                                                    selectedCatIds.clear()
                                                    selectedSubCatIds.clear()
                                                    binding.cGJobCat.removeAllViews()
                                                    binding.cGSubCat.removeAllViews()


                                                    selectedTransmainCatName = parent!!.getItemAtPosition(position).toString()
                                                    val originalCategoryName = translatedToOriginalMap[selectedTransmainCatName]

                                                    val translatedDesignations = translatedMap[originalCategoryName] ?: emptyList()

                                                    val designationAdapter = ArrayAdapter(
                                                        this@SignUpActivity,
                                                        android.R.layout.simple_dropdown_item_1line,
                                                        translatedDesignations
                                                    )

                                                    if(binding.etDesignation.visibility == View.VISIBLE) {
                                                        binding.etDesignation.apply {
                                                            visibility = View.VISIBLE
                                                            threshold = 1
                                                            setAdapter(designationAdapter)
                                                            requestFocus()
                                                            post { showDropDown() }
                                                        }
                                                    }
                                                }
                                            }

                                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                                        }
                                }
                            } catch (e: Exception) {
                                pd?.dismiss()
                                Log.e("MainCatError", "Translation or setup failed: ${e.localizedMessage}")
                            }
                        }
                    }
                }

                is BaseResponse.Error -> {
                    pd?.dismiss()
                    Log.e("SalaryTypeAPI", "Error fetching salary types: ${response.msg}")
                }
            }
        }
        commenviewModel.fetchMainCategories()
    }*/

    private fun getMainCategory() {
        commenviewModel.mainCategoryResult.observe(this) { response ->
            pd?.show()
            when (response) {
                is BaseResponse.Loading -> {
                    Log.d("MainCategoryAPI", "Loading...")
                }

                is BaseResponse.Success -> {
                    pd?.dismiss()
                    response.data?.let { apiResponse ->
                        lifecycleScope.launch {
                            try {
                                mainCatList = response.data
                                Log.d("mainCatList", "Size: ${mainCatList?.size}")

                                val arrayList = ArrayList<String>()
                                var selectedIndex = 0

                                val translatedTitle = async {
                                    TranslationHelper.translateText("Select Main Category", languageName)
                                }
                                arrayList.add(0, translatedTitle.await())

                                // Translated name -> Original name
                                val translatedToOriginalMap = mutableMapOf<String, String>()

                                if (!mainCatList.isNullOrEmpty()) {
                                    val translations = mainCatList.mapIndexed { index, item ->
                                        async {
                                            val translatedName = TranslationHelper.translateText(
                                                item.name,
                                                languageName
                                            )
                                            if (item.name == mainCatName) {
                                                selectedIndex = index + 1
                                            }
                                            translatedToOriginalMap[translatedName] = item.name
                                            translatedName
                                        }
                                    }

                                    arrayList.addAll(translations.awaitAll())

                                    val arrayAdapter = ArrayAdapter(
                                        this@SignUpActivity,
                                        android.R.layout.simple_spinner_item,
                                        arrayList
                                    )
                                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    binding.spinnerMainCat.adapter = arrayAdapter
                                    binding.spinnerMainCat.setSelection(selectedIndex)

                                    val translatedDesignationMap = getTranslatedDesignationMap(languageName)

                                    // ✅ Main Category Selection Listener
                                    binding.spinnerMainCat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                        override fun onItemSelected(
                                            parent: AdapterView<*>?,
                                            view: View?,
                                            position: Int,
                                            id: Long
                                        ) {
                                            if (position != 0) {
                                                /*selectedTransmainCatName = parent!!.getItemAtPosition(position).toString()
                                                val originalCategoryName = translatedToOriginalMap[selectedTransmainCatName]

                                                // Get designation list
                                                val translatedDesignations = translatedDesignationMap[originalCategoryName] ?: emptyList()

                                                // Clear previous
                                                binding.etDesignation.setText("")
                                                binding.etDesignation.setAdapter(null)

                                                val designationAdapter = ArrayAdapter(
                                                    this@SignUpActivity,
                                                    android.R.layout.simple_dropdown_item_1line,
                                                    translatedDesignations
                                                )

                                                binding.etDesignation.apply {
                                                    visibility = View.VISIBLE
                                                    threshold = 1
                                                    setAdapter(designationAdapter)
                                                    requestFocus()
                                                }*/

                                                // Reset others
                                                mainCatId = mainCatList[position - 1].id.toString()
                                                selectedCatIds.clear()
                                                selectedSubCatIds.clear()
                                                binding.cGJobCat.removeAllViews()
                                                binding.cGSubCat.removeAllViews()

                                                selectedTransmainCatName = parent!!.getItemAtPosition(position).toString()
                                                if(role == "3") {
                                                    if (selectedTransmainCatName == "Other") {
                                                        binding.etRoles.visibility = View.VISIBLE
                                                        binding.tvMCat.visibility = View.GONE
                                                        binding.llCat.visibility = View.GONE
                                                        binding.cGJobCat.visibility = View.GONE
                                                    } else {
                                                        binding.etRoles.visibility = View.GONE
                                                        binding.tvMCat.visibility = View.VISIBLE
                                                        binding.llCat.visibility = View.VISIBLE
                                                        binding.cGJobCat.visibility = View.VISIBLE
                                                    }
                                                }
                                            }
                                        }

                                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                                    }
                                }
                            } catch (e: Exception) {
                                pd?.dismiss()
                                Log.e("MainCatError", "Error: ${e.localizedMessage}")
                            }
                        }
                    }
                }

                is BaseResponse.Error -> {
                    pd?.dismiss()
                    Log.e("MainCategoryAPI", "Error: ${response.msg}")
                }
            }
        }
        commenviewModel.fetchMainCategories()

        // ✅ Show dropdown on click or focus
        binding.etDesignation.setOnClickListener {
            binding.etDesignation.showDropDown()
        }
        binding.etDesignation.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.etDesignation.showDropDown()
            }
        }
    }

    private fun observeCategory() {
        if (isOnline) {
            commenviewModel.categoryResult.observe(this) { response ->
                pd?.show()
                when (response) {
                    is BaseResponse.Success -> {
                        pd?.dismiss()
                        categoryList = response.data!!

                        // Launch coroutine to translate names
                        lifecycleScope.launch {
                            val translatedNames = categoryList.map {
                                TranslationHelper.translateText(it.category_name, languageName)
                            }
                            val title = TranslationHelper.translateText("Select Job Role", languageName)

                            selectedCatItems = BooleanArray(categoryList.size)
                            showcategoryListDialog(
                                title = title,
                                list = categoryList,
                                translatedNames = translatedNames,
                                selectedItems = selectedCatItems,
                                selectedIds = selectedCatIds,
                                chipGroup = binding.cGJobCat
                            )
                        }
                    }

                    is BaseResponse.Error -> {
                        pd?.dismiss()
                        Toast.makeText(this@SignUpActivity, response.msg ?: "", Toast.LENGTH_SHORT)
                            .show()
                    }

                    is BaseResponse.Loading -> { /* optional */ }
                }
            }
        } else {
            Toast.makeText(this, "Internet not connected", Toast.LENGTH_SHORT).show()
        }
    }
    private fun showcategoryListDialog(
        title: String,
        list: List<Category>,
        translatedNames: List<String>,
        selectedItems: BooleanArray,
        selectedIds: MutableList<Int>,
        chipGroup: ChipGroup
    ) {
        list.forEachIndexed { index, experience ->
            selectedItems[index] = selectedIds.contains(experience.id)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setMultiChoiceItems(
                translatedNames.toTypedArray(), // Use translated names here
                selectedItems
            ) { _, which, isChecked ->
                selectedItems[which] = isChecked
                if (isChecked) {
                    if (!selectedIds.contains(list[which].id)) {
                        selectedIds.add(list[which].id)
                    } else {
                        Toast.makeText(this, "${list[which].category_name} is already selected", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    selectedIds.remove(list[which].id)
                }
            }
            .setPositiveButton("OK") { _, _ ->
                chipGroup.removeAllViews()
                selectedIds.forEach { id ->
                    val chip = Chip(this)

                    lifecycleScope.launch {
                        val translatedText = TranslationHelper.translateText(
                            list.find { it.id == id }?.category_name ?: "",
                            languageName
                        )
                        chip.text = translatedText
                    }

                    chip.isCloseIconVisible = true
                    chip.setOnCloseIconClickListener {
                        selectedIds.remove(id)
                        selectedItems[list.indexOfFirst { it.id == id }] = false
                        showcategoryListDialog(
                            title,
                            list,
                            translatedNames,
                            selectedItems,
                            selectedIds,
                            chipGroup
                        )
                    }
                    chipGroup.addView(chip)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }


    /*private fun observeCategory() {
        if (isOnline) {
            commenviewModel.categoryResult.observe(this) { response ->
                pd?.show() // Show loading indicator
                when (response) {
                    is BaseResponse.Success -> {
                        pd?.dismiss() // Dismiss loading indicator
                        Log.d(
                            "Job Post",
                            "Response data: ${response.data}"
                        ) // Log the raw response data

                        // Ensure the data is being correctly mapped into the list
                        categoryList = response.data!!
                        //response.data?.data ?: emptyList() // Extract the data from response
                        Log.d(
                            "Cat",
                            "cat List size: ${categoryList.size}"
                        ) // Log the size of the list


                        // Call method to show multi-select dialog
                        selectedCatItems = BooleanArray(categoryList.size)
                        showcategoryListDialog(
                            title = "Select Job Category",
                            list = categoryList,
                            selectedItems = selectedCatItems,
                            selectedIds = selectedCatIds,
                            chipGroup = binding.cGJobCat
                        )
                    }

                    is BaseResponse.Error -> {
                        pd?.dismiss() // Dismiss loading indicator
                        Toast.makeText(this@SignUpActivity, response.msg ?: "", Toast.LENGTH_SHORT)
                            .show() // Show error message
                    }

                    is BaseResponse.Loading -> {
                        // Show loading indicator if needed
                    }
                }
            }
        } else {
            Toast.makeText(this, "Internet not connected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showcategoryListDialog(
        title: String,
        list: List<Category>,
        selectedItems: BooleanArray,
        selectedIds: MutableList<Int>,
        chipGroup: ChipGroup
    ) {

        // Ensure selectedItems reflects selectedIds
        list.forEachIndexed { index, experience ->
            selectedItems[index] = selectedIds.contains(experience.id)
        }


        val dialog = Builder(this)
            .setTitle(title)
            .setMultiChoiceItems(
            //  list.map { it.category_name }.toTypedArray(), // Display experience names as options
                list.map { translateCategoryName(it.category_name) }.toTypedArray(),
                selectedItems // Initial selection state
            ) { _, which, isChecked ->
                selectedItems[which] = isChecked
                if (isChecked) {
                    if (!selectedIds.contains(list[which].id)) {
                        selectedIds.add(list[which].id) // Add ID if not already selected
                    } else {
                        Toast.makeText(
                            this,
                            "${list[which].category_name} is already selected",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    selectedIds.remove(list[which].id) // Remove ID if unchecked
                }
            }
            .setPositiveButton("OK") { _, _ ->
                // When the user clicks "OK", update the UI with selected items
                chipGroup.removeAllViews() // Clear previous chips
                selectedIds.forEach { id ->
                    val chip = Chip(this)
                    chip.text =
                        list.find { it.id == id }?.category_name // Display the experience name
                    chip.isCloseIconVisible = true
                    chip.setOnCloseIconClickListener {
                        // Handle chip removal (unselecting an item)
                        selectedIds.remove(id)
                        selectedItems[list.indexOfFirst { it.id == id }] =
                            false // Unselect the item
                        showcategoryListDialog(
                            title,
                            list,
                            selectedItems,
                            selectedIds,
                            chipGroup
                        ) // Refresh dialog
                    }
                    chipGroup.addView(chip) // Add the chip to the ChipGroup
                }

            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()

    }*/




    private fun getEducationList() {
        try {
            if (isOnline) {
                commenviewModel.educationResult.observe(this) { response ->
                    pd?.show()
                    when (response) {
                        is BaseResponse.Loading -> {
                            pd?.show()
                        }

                        is BaseResponse.Success -> {
                            pd?.dismiss()
                            response.data?.let { apiResponse ->

                                lifecycleScope.launch {
                                    try {

                                        educationList = response.data.data


                                        val arrayList = ArrayList<String>()
                                        var selectedIndex = 0

                                        val translatedTitle = async {
                                            TranslationHelper.translateText(
                                                "Select Education",
                                                languageName
                                            )
                                        }

                                        arrayList.add(
                                            0,
                                            translatedTitle.await()
                                        ) // Add translated title


                                        if (educationList != null && educationList.isNotEmpty()) {


                                            // Translate district names
                                            val translations =
                                                educationList.mapIndexed { index, item ->
                                                    async {
                                                        val translatedName =
                                                            TranslationHelper.translateText(
                                                                item.education,
                                                                languageName
                                                            )

                                                        if (item.education == educationName) {
                                                            selectedIndex = index + 1
                                                        }
                                                        translatedName
                                                    }
                                                }

                                            arrayList.addAll(translations.awaitAll()) // Wait for all translations

                                            Log.d("Main Cat", "ArrayList: $arrayList")

                                            val arrayAdapter = ArrayAdapter(
                                                this@SignUpActivity,
                                                android.R.layout.simple_spinner_item,
                                                arrayList
                                            )
                                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                            binding.spinnerForEducation.adapter = arrayAdapter

                                            Log.d(
                                                "Education",
                                                "Adapter set with ${arrayAdapter.count} items"
                                            )

                                            // Set the spinner to the desired state by name
                                            binding.spinnerForEducation.setSelection(selectedIndex)

                                            binding.spinnerForEducation.onItemSelectedListener =
                                                object : AdapterView.OnItemSelectedListener {
                                                    override fun onItemSelected(
                                                        parent: AdapterView<*>?,
                                                        view: View?,
                                                        position: Int,
                                                        id: Long
                                                    ) {
                                                        if (position != 0) {
                                                            val item =
                                                                parent!!.getItemAtPosition(position)
                                                                    .toString()
                                                            educationId =
                                                                educationList[position - 1].id
                                                            println("education idd $educationId")

                                                        }
                                                    }

                                                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                                                }

                                        } else {
                                            Log.e("spinnerEdu", "Empty educationList from API")
                                        }
                                    } catch (e: Exception) {
                                        Log.e(
                                            "Translation Error",
                                            "Error translating states: ${e.message}"
                                        )
                                    }
                                }


                            }
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss()
                            Log.e("spinnerForEducation", "gender Error: ${response.msg ?: ""}")
                        }

                        else -> {
                            Log.e("spinnerForEducation", "Unhandled case")
                        }
                    }
                }

                commenviewModel.fetchEducation()

            } else {
                Toast.makeText(this@SignUpActivity, "Internet not connected", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("pers info", "Error occurred:edu  ${e.localizedMessage}")
        }
    }

    private fun getGenderList() {
        try {
            if (isOnline) {
                commenviewModel.genderResult.observe(this) { response ->
                    pd?.show()
                    when (response) {
                        is BaseResponse.Loading -> {
                            pd?.show()
                        }

                        is BaseResponse.Success -> {
                            pd?.dismiss()
                            response.data?.let { apiResponse ->

                                lifecycleScope.launch {
                                    try {

                                        genderList = response.data.data
                                        Log.d("genderList", "Size: ${genderList?.size}")
                                        Log.d("genderList", "res: ${response.data}")

                                        val arrayList = ArrayList<String>()
                                        var selectedIndex = 0

                                        val translatedTitle = async {
                                            TranslationHelper.translateText(
                                                "Select Gender",
                                                languageName
                                            )
                                        }

                                        arrayList.add(
                                            0,
                                            translatedTitle.await()
                                        ) // Add translated title


                                        if (genderList != null && genderList.isNotEmpty()) {


                                            // Translate district names
                                            val translations =
                                                genderList.mapIndexed { index, item ->
                                                    async {
                                                        val translatedName =
                                                            TranslationHelper.translateText(
                                                                item.gender,
                                                                languageName
                                                            )

                                                        if (item.gender == genderName) {
                                                            selectedIndex = index + 1
                                                        }
                                                        translatedName
                                                    }
                                                }

                                            arrayList.addAll(translations.awaitAll()) // Wait for all translations

                                            Log.d("Main Cat", "ArrayList: $arrayList")

                                            /*val arrayAdapter = ArrayAdapter(
                                                this@SignUpActivity,
                                                android.R.layout.simple_spinner_item,
                                                arrayList
                                            )
                                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                            binding.spinnerForGender.adapter = arrayAdapter*/

                                            val adapter = object : ArrayAdapter<String>(
                                                this@SignUpActivity,
                                                android.R.layout.simple_spinner_item,
                                                arrayList) {
                                                override fun isEnabled(position: Int): Boolean {
                                                    return position != 0
                                                }

                                                override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                                                    val view = super.getDropDownView(position, convertView, parent)
                                                    val tv = view as TextView
                                                    tv.setTextColor(if (position == 0) Color.GRAY else Color.BLACK)
                                                    return view
                                                }
                                            }
                                            binding.spinnerForGender.adapter = adapter


                                            Log.d(
                                                "Gender",
                                                "Adapter set with ${adapter.count} items"
                                            )

                                            // Set the spinner to the desired state by name
                                            binding.spinnerForGender.setSelection(selectedIndex)

                                            binding.spinnerForGender.onItemSelectedListener =
                                                object : AdapterView.OnItemSelectedListener {
                                                    override fun onItemSelected(
                                                        parent: AdapterView<*>?,
                                                        view: View?,
                                                        position: Int,
                                                        id: Long
                                                    ) {
                                                        if (position != 0) {
                                                            val item =
                                                                parent!!.getItemAtPosition(position)
                                                                    .toString()
                                                            genderId = genderList[position - 1].id
                                                            println("gender idd $genderId")

                                                        }
                                                    }

                                                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                                                }

                                        } else {
                                            Log.e("spinnerMainCat", "Empty salaryTypeList from API")
                                        }
                                    } catch (e: Exception) {
                                        Log.e(
                                            "Translation Error",
                                            "Error translating states: ${e.message}"
                                        )
                                    }
                                }
                            }
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss()

                        }

                        else -> {
                            Log.e("spinnerMainCat", "Unhandled case")
                        }
                    }
                }

                commenviewModel.fetchGender()

            } else {
                Toast.makeText(this@SignUpActivity, "Internet not connected", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e(TAG, "Error occurred: gender ${e.localizedMessage}")
        }
    }

    private fun getStateList() {

        viewModel.stateresult.observe(this) { response ->
            when (response) {

                is BaseResponse.Loading -> {
                    pd?.show()
                }

                is BaseResponse.Success -> {
                    pd?.dismiss()
                    // Stop the loading indicator
                    response.data?.let { apiResponse ->
                        lifecycleScope.launch {
                            try {

                                stateList = apiResponse.data

                                // val dataList: List<StateData>? = stateApiResponse?.DATA
                                val arrayList = ArrayList<String>()
                                var selectedIndex = 0

                                val translatedTitle = async {
                                    TranslationHelper.translateText("Select State", languageName)
                                }

                                arrayList.add(0, translatedTitle.await()) // Add translated title

                                if (stateList != null && stateList.isNotEmpty()) {
                                    // Translate state names
                                    val translations = stateList.mapIndexed { index, item ->
                                        async {
                                            val translatedName = TranslationHelper.translateText(
                                                item.state_name, languageName
                                            )

                                            if (item.state_name == stateName) {
                                                selectedIndex =
                                                    index + 1 // Offset due to "Select State"
                                            }
                                            translatedName
                                        }
                                    }

                                    arrayList.addAll(translations.awaitAll()) // Wait for all translations

                                    val arrayAdapter = ArrayAdapter(
                                        this@SignUpActivity,
                                        android.R.layout.simple_spinner_item,
                                        arrayList
                                    )
                                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    binding.spinnerForState.adapter = arrayAdapter

                                    // Set the spinner to the desired state by name
                                    binding.spinnerForState.setSelection(selectedIndex)

                                    binding.spinnerForState.onItemSelectedListener =
                                        object : AdapterView.OnItemSelectedListener {
                                            override fun onItemSelected(
                                                parent: AdapterView<*>?,
                                                view: View?,
                                                position: Int,
                                                id: Long
                                            ) {
                                                if (position != 0) {
                                                    val item =
                                                        parent!!.getItemAtPosition(position)
                                                            .toString()
                                                    stateId = stateList[position - 1].id
                                                    println("stateId idd $stateId")
                                                    getDistrictList(stateId!!)

                                                }
                                            }

                                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                                        }
                                }
                            } catch (e: Exception) {
                                Log.e("Translation Error", "Error translating states: ${e.message}")
                            }
                        }
                    }
                }

                is BaseResponse.Error -> {
                    pd?.dismiss()
                    // Handle error case

                }

                else -> {
                    // Handle other cases if any
                }
            }
        }

        // Call the ViewModel's State method to fetch states
        viewModel.State()
    }


    private fun getDistrictList(stateId: Int) {

        viewModel.districtresult.observe(this) { response ->
            when (response) {
                is BaseResponse.Loading -> {
                    pd?.show()
                }

                is BaseResponse.Success -> {
                    pd?.dismiss()
                    // Stop the loading indicator
                    response.data?.let { apiResponse ->

                        lifecycleScope.launch {
                            try {

                                districtList = apiResponse.data

                                val arrayList = ArrayList<String>()
                                var selectedIndex = 0

                                val translatedTitle = async {
                                    TranslationHelper.translateText("Select District", languageName)
                                }

                                arrayList.add(0, translatedTitle.await()) // Add translated title

                                if (districtList != null && districtList.isNotEmpty()) {
                                    // Translate district names
                                    val translations = districtList.mapIndexed { index, item ->
                                        async {
                                            val translatedName = TranslationHelper.translateText(
                                                item.district_name, languageName
                                            )

                                            if (item.district_name == distName) {
                                                selectedIndex = index + 1
                                            }
                                            translatedName
                                        }
                                    }

                                    arrayList.addAll(translations.awaitAll()) // Wait for all translations


                                    val arrayAdapter = ArrayAdapter(
                                        this@SignUpActivity,
                                        android.R.layout.simple_spinner_item,
                                        arrayList
                                    )
                                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    binding.spinnerForDitrict.adapter = arrayAdapter

                                    // Set the spinner to the desired state by name
                                    binding.spinnerForDitrict.setSelection(selectedIndex)

                                    binding.spinnerForDitrict.onItemSelectedListener =
                                        object : AdapterView.OnItemSelectedListener {
                                            override fun onItemSelected(
                                                parent: AdapterView<*>?,
                                                view: View?,
                                                position: Int,
                                                id: Long
                                            ) {
                                                if (position != 0) {
                                                    val item =
                                                        parent!!.getItemAtPosition(position)
                                                            .toString()
                                                    distId = districtList[position - 1].id
                                                    println("Disterict Id idd $distId")


                                                }
                                            }

                                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                                        }
                                }
                            } catch (e: Exception) {
                                Log.e("Translation Error", "Error translating states: ${e.message}")
                            }
                        }
                    }
                }

                is BaseResponse.Error -> {
                    pd?.dismiss()
                    // Handle error case

                }

                else -> {
                    // Handle other cases if any
                }
            }
        }

        // Call the ViewModel's State method to fetch states
        viewModel.district(stateId!!)
    }


    /*private fun validateInputs(): Boolean {


        val spinnerState = binding.spinnerForState.selectedItemPosition
        val spinnerDistrict = binding.spinnerForDitrict.selectedItemPosition
        val spinnerEducation = binding.spinnerForEducation.selectedItemPosition


        val name = binding.etName.text.toString().trim()
        val mobileNo = binding.etMobile.text.toString().trim()
        //  val Email = binding.etEmail.text.toString().trim()
        val Address = binding.etAddress.text.toString().trim()
        val pincode = binding.etPincode.text.toString().trim()
        val spinnerGender = binding.spinnerForGender.selectedItemPosition
        val experience = binding.etExperiance.text.toString().trim()
        val spinnerMainCategory = binding.spinnerMainCat.selectedItemPosition
        val skill = binding.etSkill.text.toString().trim()
        val designation = binding.etDesignation.text.toString().trim()
        val companyName = binding.etCompanyName.text.toString().trim()

        val mobileNumberPattern = "^[6-9][0-9]{9}$"
        val emailPattern = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,3}$"
        val usernamePattern = "^[a-zA-Z\\s]+$"

       *//* if (profileBase64String == null) {
            Toast.makeText(this, getString(R.string.select_prof_img), Toast.LENGTH_SHORT).show()
            return false
        }*//*

        if (TextUtils.isEmpty(name)) {
            binding.etName.setError(getString(R.string.enter_name))
        } else if (!name.matches(usernamePattern.toRegex())) {
            binding.etName.setError(getString(R.string.enter_valid_name))
        } else {
            binding.etName.setError(null) // Clear error
        }

        if (TextUtils.isEmpty(mobileNo)) {
            binding.etMobile.setError(getString(R.string.enter_mobile))
        } else if (!mobileNo.matches(mobileNumberPattern.toRegex())) {
            binding.etMobile.setError(getString(R.string.enter_valid_mobile))
        } else {
            binding.etMobile.setError(null) // Clear error
        }


        if (TextUtils.isEmpty(Address)) {
            binding.etAddress.setError(getString(R.string.enter_add))
            // return false
        } else {
            binding.etAddress.setError(null) // Clear error
        }

        if (spinnerState == 0) {
            // If no item is selected, show error and return false
            Toast.makeText(this, getString(R.string.select_state), Toast.LENGTH_SHORT).show()
            return false
        }

        if (spinnerDistrict == 0) {
            // If no item is selected, show error and return false
            Toast.makeText(this, getString(R.string.select_dist), Toast.LENGTH_SHORT).show()
            return false
        }

        if (spinnerGender == 0) {
            lifecycleScope.launch {
                val msg = TranslationHelper.translateText(
                    "Please Select Gender",
                    languageName
                )
                Toast.makeText(this@SignUpActivity, msg, Toast.LENGTH_SHORT).show()
            }
        }

        if (role.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.select_role), Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.etCompanyName.visibility == View.VISIBLE) {
            if (TextUtils.isEmpty(companyName)) {
                binding.etCompanyName.setError(getString(R.string.enter_comp_name))
                // return false
            } else {
                binding.etCompanyName.setError(null) // Clear error
            }

           *//* if (aadharBase64String == null) {
                lifecycleScope.launch {
                    val translatedText = TranslationHelper.translateText(
                        "Please Select Company Aadhar Card",
                        languageName
                    )
                    Toast.makeText(this@SignUpActivity, translatedText, Toast.LENGTH_SHORT).show()
                }
                return false
            }*//*
        }



        if (binding.etDesignation.visibility == View.VISIBLE) {

            if (TextUtils.isEmpty(designation)) {
                binding.etDesignation.setError(getString(R.string.enter_designation))
                // return false
            } else {
                binding.etDesignation.setError(null) // Clear error
            }

           *//* if (aadharBase64String == null) {
                Toast.makeText(this, getString(R.string.select_aadhar_img), Toast.LENGTH_SHORT)
                    .show()
                return false
            }*//*
        }

        if (binding.llExpe.visibility == View.VISIBLE) {
            if (TextUtils.isEmpty(experience)) {
                lifecycleScope.launch {
                    val msg = TranslationHelper.translateText(
                        "Please Enter Work Experience",
                        languageName
                    )
                    binding.etExperiance.setError(msg)
                    // return false
                }

            } else {
                binding.etExperiance.setError(null) // Clear error
            }
        }

        if (spinnerMainCategory == 0) {
            Toast.makeText(this, getString(R.string.select_main_cat), Toast.LENGTH_SHORT)
                .show()
            return false
        }

        if (binding.llCat.visibility == View.VISIBLE) {
            if (selectedCatIds.size == 0) {
                Toast.makeText(this, getString(R.string.select_cat), Toast.LENGTH_SHORT).show()
                return false
            }
        }





        if (binding.etSkill.visibility == View.VISIBLE) {
            if (TextUtils.isEmpty(skill)) {
                binding.etSkill.setError(getString(R.string.enter_skill))
                return false
            } else {
                binding.etSkill.setError(null)
            }
        }







        return true
    }*/
    private fun validateInputs(): Boolean {

        val scrollView = binding.scrollView  // Replace with your actual ScrollView ID

        var focusView: View? = null
        var isValid = true

        val spinnerState = binding.spinnerForState.selectedItemPosition
        val spinnerDistrict = binding.spinnerForDitrict.selectedItemPosition
        val spinnerGender = binding.spinnerForGender.selectedItemPosition
        val spinnerMainCategory = binding.spinnerMainCat.selectedItemPosition

        val name = binding.etName.text.toString().trim()
        val mobileNo = binding.etMobile.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val experience = binding.etExperiance.text.toString().trim()
        val companyName = binding.etCompanyName.text.toString().trim()
        val designation = binding.etDesignation.text.toString().trim()
        val Roles = binding.etRoles.text.toString().trim()
      //  val skill = binding.etSkill.text.toString().trim()

        val mobileNumberPattern = "^[6-9][0-9]{9}$"
        val usernamePattern = "^[a-zA-Z\\s]+$"
        val emailPattern = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,3}$"



        if (TextUtils.isEmpty(name)) {
            binding.etName.error = getString(R.string.enter_name)
            focusView = binding.etName
            isValid = false
        } else if (!name.matches(usernamePattern.toRegex())) {
            binding.etName.error = getString(R.string.enter_valid_name)
            focusView = binding.etName
            isValid = false
        }

        if (TextUtils.isEmpty(mobileNo)) {
            binding.etMobile.error = getString(R.string.enter_mobile)
            if (focusView == null) focusView = binding.etMobile
            isValid = false
        } else if (!mobileNo.matches(mobileNumberPattern.toRegex())) {
            binding.etMobile.error = getString(R.string.enter_valid_mobile)
            if (focusView == null) focusView = binding.etMobile
            isValid = false
        }

        // Check only if email is entered
        if (email.isNotEmpty() && !email.matches(emailPattern.toRegex())) {
            lifecycleScope.launch {
                val errorMsg = TranslationHelper.translateText(
                    "Please Enter Valid Email", languageName)
                binding.etEmail.error = errorMsg
                if (focusView == null) focusView = binding.etEmail
            }
            isValid = false
        } else {
            binding.etEmail.error = null
        }

        if (TextUtils.isEmpty(address)) {
            binding.etAddress.error = getString(R.string.enter_add)
            if (focusView == null) focusView = binding.etAddress
            isValid = false
        }

        if (spinnerState == 0) {
            Toast.makeText(this, getString(R.string.select_state), Toast.LENGTH_SHORT).show()
            focusView = binding.spinnerForState
            isValid = false
        }

        if (spinnerDistrict == 0) {
            Toast.makeText(this, getString(R.string.select_dist), Toast.LENGTH_SHORT).show()
            if (focusView == null) focusView = binding.spinnerForDitrict
            isValid = false
        }

        if (spinnerGender == 0) {
            lifecycleScope.launch {
                val errorMsg = TranslationHelper.translateText(
                    "Please select gender", languageName)
                Toast.makeText(this@SignUpActivity, errorMsg, Toast.LENGTH_SHORT).show()
                if (focusView == null) focusView = binding.spinnerForGender
            }
            isValid = false
        }
        if (role.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.select_role), Toast.LENGTH_SHORT).show()
            if (focusView == null) focusView = binding.rdGroup
            isValid = false
        }
        if (binding.etCompanyName.visibility == View.VISIBLE && TextUtils.isEmpty(companyName)) {
            binding.etCompanyName.error = getString(R.string.enter_comp_name)
            if (focusView == null) focusView = binding.etCompanyName
            isValid = false
        }

        /*if (binding.etDesignation.visibility == View.VISIBLE && TextUtils.isEmpty(designation)) {
            binding.etDesignation.error = getString(R.string.enter_designation)
            if (focusView == null) focusView = binding.etDesignation
            isValid = false
        }*/

        /*if (binding.llExpe.visibility == View.VISIBLE && TextUtils.isEmpty(experience)) {
            binding.etExperiance.error = "Please enter work experience"
            if (focusView == null) focusView = binding.etExperiance
            isValid = false
        }*/

        if (spinnerMainCategory == 0) {
            Toast.makeText(this, getString(R.string.select_main_cat), Toast.LENGTH_SHORT).show()
            if (focusView == null) focusView = binding.spinnerMainCat
            isValid = false
        }

        if (binding.llCat.visibility == View.VISIBLE && selectedCatIds.isEmpty()) {
            Toast.makeText(this, getString(R.string.select_cat), Toast.LENGTH_SHORT).show()
            if (focusView == null) focusView = binding.llCat
            isValid = false
        }

      //  if (binding.etRoles.visibility == View.VISIBLE && selectedRoles.isEmpty()) {
        if (binding.etRoles.visibility == View.VISIBLE && (TextUtils.isEmpty(Roles))) {
            binding.etRoles.error =  getString(R.string.enter_role)
            if (focusView == null) focusView = binding.etRoles
            isValid = false
        }



       /* if (binding.etSkill.visibility == View.VISIBLE && TextUtils.isEmpty(skill)) {
            binding.etSkill.error = getString(R.string.enter_skill)
            if (focusView == null) focusView = binding.etSkill
            isValid = false
        }*/

        // Finally scroll to the first error if any
        if (!isValid && focusView != null) {
            scrollView.post {
                scrollView.smoothScrollTo(0, focusView!!.top)
                focusView!!.requestFocus()
            }
        }

        return isValid
    }


    private fun checkAndRequestPermissions() {
        if (REQUIRED_PERMISSIONS.any {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, CAMERA_REQUEST_CODE)
        } else {
            // showPictureDialog()
        }
    }

    /* override fun onRequestPermissionsResult(
         requestCode: Int,
         permissions: Array<String>,
         grantResults: IntArray
     ) {
         super.onRequestPermissionsResult(requestCode, permissions, grantResults)
         if (requestCode == CAMERA_REQUEST_CODE) {
             if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                 //showPictureDialog()
             } else {
                 Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT).show()
             }
         }


     }*/
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // You can call your camera functionality here
                    // showPictureDialog()
                } else {
                    Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    Log.e(TAG, "startLocationService granted")
                    // All requested permissions are granted, start the foreground service
                    // startLocationService()
                } else {
                    Log.e(TAG, "startLocationService denide")
                    startForegroundPermission()
                    // Permission denied, show an appropriate message
                    Toast.makeText(
                        this,
                        "Location and foreground service permissions are required",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            REQUEST_LOCATION_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Toast.makeText(this, "permission granted", Toast.LENGTH_LONG).show()
                    }
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }


            MY_FINE_LOCATION_REQUEST -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        requestBackgroundLocationPermission()
                    }

                } else {
                    Toast.makeText(
                        this,
                        "ACCESS FINE LOCATION permission denied",
                        Toast.LENGTH_LONG
                    ).show()
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", this.packageName, null)
                            ),
                        )
                    }
                }
                return
            }

            MY_BACKGROUND_LOCATION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Toast.makeText(
                            this,
                            "Background location Permission Granted",
                            Toast.LENGTH_LONG
                        ).show()
                        // Call your location-starting logic here if needed
                    }
                } else {
                    Toast.makeText(this, "Background location permission denied", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }


    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Shram")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems) { dialog, which ->
            when (which) {
                0 -> openGallery()
                1 -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        openCamera() // Your method to launch the camera intent
                    } else {
                        checkAndRequestPermissions()
                    }

                }
            }
        }
        pictureDialog.show()
    }

    private fun openCamera() {
        val photoFile = createImageFile()
        cameraImageUri = FileProvider.getUriForFile(
            this,
            "${"com.uvk.shramapplication"}.provider",
            photoFile
        )

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    private fun createImageFile(): File {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    private fun openGallery() {
        /* val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
         startActivityForResult(intent, GALLERY_REQUEST_CODE)*/
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*" // Allows multiple file types
            putExtra(
                Intent.EXTRA_MIME_TYPES,
                arrayOf("image/png", "image/jpg", "image/jpeg", "application/pdf")
            )
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, GALLERY_REQUEST_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val bitmap = rotateImageIfRequired(cameraImageUri)
                    Glide.with(this).load(bitmap).into(currentImageView)

                    val base64String = convertBitmapToBase64(bitmap)

                    if (currentImageView.id == R.id.iv_ProfileImg) {
                        profileBase64String = base64String
                        //   Log.e("tag", "Profile Base64 (Camera): $profileBase64String")
                    } else if (currentImageView.id == R.id.iv_aadharImage) {
                        aadharBase64String = base64String
                        binding.llText.visibility = View.GONE
                        binding.ivAadharImage.visibility = View.VISIBLE
                    }
                    /* val bitmap = data?.extras?.get("data") as Bitmap
                     Glide.with(this).load(bitmap).into(currentImageView)
                     val base64String = convertBitmapToBase64(bitmap)

                     if (currentImageView.id == R.id.iv_ProfileImg) {
                         profileBase64String = base64String
                      //   Log.e("tag", "Profile Base64 (Camera): $profileBase64String")
                     } else if (currentImageView.id == R.id.iv_aadharImage) {
                         aadharBase64String = base64String
                         binding.llText.visibility = View.GONE
                         binding.ivAadharImage.visibility = View.VISIBLE
                     }*/
                }

                GALLERY_REQUEST_CODE -> {
                    val uri = data?.data
                    Glide.with(this).load(uri).into(currentImageView)

                    if (currentImageView.id == R.id.iv_ProfileImg) {
                        val base64String = convertImageToBase64(uri!!)
                        profileBase64String = base64String

                    } else if (currentImageView.id == R.id.iv_aadharImage) {
                        val base64String = uriToBase64(uri!!)
                        aadharBase64String = base64String
                        binding.tvFileName.text = getFileName(uri!!)


                        val fileName = getFileName(uri!!)
                        if (fileName.endsWith(".pdf", true)) {
                            // If it's a PDF, show PDF icon and set click listener to open PDF

                            binding.llText.visibility = View.VISIBLE
                            binding.ivAadharImage.visibility = View.GONE
                            binding.ivAadharPdf.setOnClickListener {
                                openPdf(uri)
                            }
                        } else {
                            binding.llText.visibility = View.GONE
                            binding.ivAadharImage.visibility = View.VISIBLE
                            // If it's an image, load it using Glide
                            Glide.with(this).load(uri).into(currentImageView)
                        }
                    }
                }


            }
        }
    }

    private fun rotateImageIfRequired(uri: Uri): Bitmap {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val pfd = contentResolver.openFileDescriptor(uri, "r") ?: return bitmap
        val exif = ExifInterface(pfd.fileDescriptor)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
            else -> bitmap
        }
    }


    private fun rotateBitmap(bitmap: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun openPdf(pdfUri: Uri?) {
        if (pdfUri == null) {
            Toast.makeText(this, "Invalid PDF file", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(pdfUri, "application/pdf")
            intent.flags =
                Intent.FLAG_GRANT_READ_URI_PERMISSION  // Grant permission for external apps
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No PDF viewer found", Toast.LENGTH_SHORT).show()
        }
    }


    private fun convertImageToBase64(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
        return convertBitmapToBase64(bitmap)
    }

    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun getFileName(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            if (it.moveToFirst()) {
                it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            } else "Unknown File"
        } ?: "Unknown File"
    }

    private fun uriToBase64(uri: Uri): String {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }


}


