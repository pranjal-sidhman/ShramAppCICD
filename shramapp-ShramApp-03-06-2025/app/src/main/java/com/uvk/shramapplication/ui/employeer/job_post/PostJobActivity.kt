package com.uvk.shramapplication.ui.employeer.job_post

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uvk.shramapplication.MainActivity
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.ActivityPostJobBinding
import com.uvk.shramapplication.helper.CommenViewModel
import com.uvk.shramapplication.helper.Education
import com.uvk.shramapplication.helper.Experience
import com.uvk.shramapplication.helper.GenderType
import com.uvk.shramapplication.helper.JobType
import com.uvk.shramapplication.helper.SalaryRange
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.response.DistrictData
import com.uvk.shramapplication.response.StateData
import com.uvk.shramapplication.ui.category.Category
import com.uvk.shramapplication.ui.main_category.MainCategory
import com.uvk.shramapplication.ui.registration.StateDTViewModel
import com.uvk.shramapplication.ui.subcategory.SubcategoryModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.token
import com.mahindra.serviceengineer.savedata.userid
import com.uvk.shramapplication.helper.TranslationHelper
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Suppress("UNREACHABLE_CODE")
class PostJobActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostJobBinding


    private lateinit var dialogPost: Dialog
    private lateinit var recyclerViewCat: RecyclerView

    private lateinit var jobList: List<JobType>
    private lateinit var categoryList: List<Category>
    private lateinit var subCategoryList: List<SubcategoryModel>
    private lateinit var expList: List<Experience>
    private lateinit var genderList: List<GenderType>
    private lateinit var educList: List<Education>
    private lateinit var salaryTypeList: List<SalaryRange>
    private lateinit var salaryRangeList: List<SalaryRange>
    private lateinit var mainCatList: List<MainCategory>

    private var selectedCategories = mutableListOf<Map<String, Int>>()
    private var selectedSubCategories = mutableListOf<Map<String, Int>>()
    private lateinit var selectedSubCatItems: BooleanArray
    private lateinit var selectedCatItems: BooleanArray
    private lateinit var selectedMainCatItems: BooleanArray
    private lateinit var selectedJobItems: BooleanArray


    private val selectedMainCatIds: MutableList<Int> = mutableListOf()
    private val selectedJobIds: MutableList<Int> = mutableListOf()
    private val selectedCatIds: MutableList<Int> = mutableListOf()
    private val selectedSubCatIds: MutableList<Int> = mutableListOf()

    private val viewModel by viewModels<CommenViewModel>()
    private val stateViewModel by viewModels<StateDTViewModel>()
    private var pd: TransparentProgressDialog? = null

    private val REQUEST_PERMISSIONS_CODE = 100

    private lateinit var imageView: AppCompatImageView
    private lateinit var currentImageView: ImageView

    private var imgBase64String: String? = null
    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2

    var mainCatId: String = ""
    var salaryTypeId: Int = 0
    var salaryRangeId: Int = 0
    var genderId: Int = 0
    var genderName: String = " "
    var educationId: Int = 0
    var educationName: String = " "

    var mainCatName: String = " "
    var salaryTypeName: String = " "
    var salaryRangeName: String = " "
    val DEFAULT_SALARY_TYPE = "Salary Type"
    val DEFAULT_SALARY_RANGE = "Salary Range"
    val arrayTypeList: ArrayList<String> = ArrayList()
    val arrayRangeList: ArrayList<String> = ArrayList()
    var selectedTypeIndex = 0
    var selectedRangeIndex = 0

    var stateId: Int = 0
    var stateName: String = " "

    var distId: Int = 0
    var distName: String = " "
    private lateinit var stateList: List<StateData>
    private lateinit var districtList: List<DistrictData>
    private lateinit var cameraImageUri: Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPostJobBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pd = TransparentProgressDialog(this, R.drawable.progress)
        binding.backicon.setOnClickListener {
            finish()
        }

        if (!hasPermissions()) {
            requestPermissions()
        }


        getStateList()
        getGenderList()
        getEducationList()

        lifecycleScope.launch {
            binding.tvExper.text = TranslationHelper.translateText(
                "Work Experience",
                languageName
            )

            binding.tvGender.text = TranslationHelper.translateText(
                "Gender *",
                languageName
            )
            binding.etExperiance.hint = TranslationHelper.translateText(
                "Work Experience",
                languageName
            )

        }

        arrayTypeList.add(0, DEFAULT_SALARY_TYPE)
        arrayRangeList.add(0, DEFAULT_SALARY_RANGE)



        observeJobType()
        getSalaryType()
        getMainCategory()
        //observeMainCategory()
        observeCategory()
        observeSubCategory()

        binding.tvJobType.setOnClickListener {
            viewModel.fetchJobType()
        }

        binding.tvJobExpDate.setOnClickListener {
            // Get the current date
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Create DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Create a Calendar instance with selected date
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay)

                    // Format the date in yyyy-MM-dd
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedCalendar.time)

                    // Set date in TextView
                    binding.tvJobExpDate.text = formattedDate
                },
                year, month, day
            )

            // Disable past dates
            datePickerDialog.datePicker.minDate = calendar.timeInMillis

            datePickerDialog.show()
        }


        /* binding.tvMainJobCat.setOnClickListener {
             viewModel.fetchMainCategories()
         }*/

        binding.tvJobCat.setOnClickListener {
            if (!mainCatId.isNullOrEmpty()) { // Ensures the list is not null and not empty
                viewModel.fetchcategories(mainCatId)
            } else {
                lifecycleScope.launch {
                    val msg = TranslationHelper.translateText(
                        "Please Select Main Category",
                        languageName
                    )
                    Toast.makeText(this@PostJobActivity, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tvsubCategory.setOnClickListener {
            if (!selectedCatIds.isNullOrEmpty()) {
           // if (!selectedCategories.isNullOrEmpty()) {
                viewModel.fetchsubcategories(selectedCatIds)
            } else {
                lifecycleScope.launch {
                    val msg = TranslationHelper.translateText(
                        "Please Select Category",
                        languageName
                    )
                    Toast.makeText(this@PostJobActivity, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.ivCamera.setOnClickListener {
            currentImageView = binding.ivCompImage

            showPictureDialog()
        }


        binding.btnSubmit.setOnClickListener {
            if (isOnline) {
                //  if (binding.cbTerms.isChecked){
                when {
                    binding.etTitle.text.isNullOrEmpty() -> {
                        binding.etTitle.error = getString(R.string.enter_title)
                        binding.etTitle.requestFocus()
                    }
                    // !binding.etDesc.text.matches(Regex("^[a-zA-Z ]+\$")) -> {
                    binding.etDesc.text.isNullOrEmpty() -> {
                        binding.etDesc.error = getString(R.string.enter_desc)
                        binding.etDesc.requestFocus()
                    }

                    stateId == 0 -> {
                        Toast.makeText(
                            this@PostJobActivity,
                            getString(R.string.select_state),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    distId == 0 -> {
                        Toast.makeText(
                            this@PostJobActivity,
                            getString(R.string.select_dist),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    genderId == 0 -> {
                        lifecycleScope.launch {
                            val msg = TranslationHelper.translateText(
                                "Please Select Gender",
                                languageName
                            )
                            Toast.makeText(this@PostJobActivity, msg, Toast.LENGTH_SHORT).show()
                        }

                    }

                    binding.etAdd.text.isNullOrEmpty() -> {
                        binding.etAdd.error = getString(R.string.enter_add)
                        binding.etAdd.requestFocus()
                    }

                    selectedJobIds.size == 0 -> {
                        Toast.makeText(
                            this,
                            getString(R.string.select_job_type),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    binding.etCompanyName.text.isNullOrEmpty() -> {
                        binding.etCompanyName.error = getString(R.string.enter_comp_name)
                        binding.etCompanyName.requestFocus()
                    }

                    salaryTypeId == 0 -> {
                        Toast.makeText(
                            this,
                            getString(R.string.select_salary_type),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    salaryRangeId == 0 -> {
                        Toast.makeText(
                            this,
                            getString(R.string.select_salary_range),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    binding.etSalary.visibility == View.VISIBLE && binding.etSalary.text.isNullOrEmpty() -> {
                        Toast.makeText(
                            this,
                            getString(R.string.select_salary_amount),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    mainCatId == "0" -> {
                        Toast.makeText(
                            this,
                            getString(R.string.select_main_cat),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    selectedCatIds.size == 0 -> {
                        lifecycleScope.launch {
                            val msg = TranslationHelper.translateText(
                                "Please Select Main Category",
                                languageName
                            )
                            Toast.makeText(this@PostJobActivity, msg, Toast.LENGTH_SHORT).show()
                        }

                    }

                    /*selectedSubCatIds.size == 0 -> {
                        Toast.makeText(this, getString(R.string.select_sub_cat), Toast.LENGTH_SHORT)
                            .show()
                    }*/



                   /* binding.etKeyResp.text.isNullOrEmpty() -> {
                        binding.etKeyResp.error = getString(R.string.enter_key_resp)
                        binding.etKeyResp.requestFocus()
                    }*/


                    /*educationId == 0 -> {
                        lifecycleScope.launch {
                            val msg = TranslationHelper.translateText(
                                "Please Select Education",
                                languageName
                            )
                            Toast.makeText(this@PostJobActivity, msg, Toast.LENGTH_SHORT).show()
                        }

                    }*/

                    /*binding.etExperiance.text.isNullOrEmpty() -> {
                        lifecycleScope.launch {
                            val msg = TranslationHelper.translateText(
                                "Please Enter Work Experience",
                                languageName
                            )
                            Toast.makeText(this@PostJobActivity, msg, Toast.LENGTH_SHORT).show()
                        }
                    }*/

                    /*binding.etCompDesc.text.isNullOrEmpty() -> {
                        binding.etCompDesc.error = getString(R.string.enter_comp_desc)
                        binding.etCompDesc.requestFocus()
                    }*/

                    binding.tvJobExpDate.text.isNullOrEmpty() -> {
                        binding.tvJobExpDate.error = getString(R.string.select_job_expire_date)
                        binding.tvJobExpDate.requestFocus()
                    }


                    else -> {


                        addJobPostData(
                            userid = userid,
                            title = binding.etTitle.text.toString().trim(),
                            desc = binding.etDesc.text.toString().trim(),
                            address = binding.etAdd.text.toString().trim(),
                            selectedJobIds = selectedJobIds,
                            companyName = binding.etCompanyName.text.toString().trim(),
                            salaryTypeId = salaryTypeId,
                            salaryRangeId = salaryRangeId,
                            selectedMainCatIds = mainCatId,
                            selectedCategories = selectedCategories,  // Ensure this contains the "category_id" and "vacancies"
                            selectedSubCategories = selectedSubCategories,  // Ensure this contains the "sub_category_id" and "vacancies"
                            keyResp = binding.etKeyResp.text.toString().trim(),
                            experiance = binding.etExperiance.text.toString().trim(),
                            compDesc = binding.etCompDesc.text.toString().trim(),
                            jobExpDate = binding.tvJobExpDate.text.toString().trim(),
                            imgBase64String = imgBase64String,
                            salary_amount = binding.etSalary.text.toString().trim()
                        )

                    }
                }

                /*}else{
                    Toast.makeText(this,"Accept Terms and Conditions",Toast.LENGTH_SHORT).show()

                }*/

            } else {
                Toast.makeText(this, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getEducationList() {
        try {
            if (isOnline) {
                viewModel.educationResult.observe(this) { response ->
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

                                        educList = response.data.data


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


                                        if (educList != null && educList.isNotEmpty()) {


                                            // Translate district names
                                            val translations =
                                                educList.mapIndexed { index, item ->
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
                                                this@PostJobActivity,
                                                android.R.layout.simple_spinner_item,
                                                arrayList
                                            )
                                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                            binding.spinnerForEducation.adapter = arrayAdapter

                                            Log.d(
                                                "mainCatId",
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
                                                            educationId = educList[position - 1].id
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
                                            "Error translating edu: ${e.message}"
                                        )
                                    }
                                }


                            }
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss()
                            Log.e("spinnerForEducation", "gender Error: ${response.msg?:""}")
                        }

                        else -> {
                            Log.e("spinnerForEducation", "Unhandled case")
                        }
                    }
                }

                viewModel.fetchEducation()

            } else {
                Toast.makeText(this@PostJobActivity, "Internet not connected", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("pers info", "Error occurred: ${e.localizedMessage}")
        }
    }

    private fun getGenderList() {
        try {
            if (isOnline) {
                viewModel.genderResult.observe(this) { response ->
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

                                            val arrayAdapter = ArrayAdapter(
                                                this@PostJobActivity,
                                                android.R.layout.simple_spinner_item,
                                                arrayList
                                            )
                                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                            binding.spinnerForGender.adapter = arrayAdapter

                                            Log.d(
                                                "mainCatId",
                                                "Adapter set with ${arrayAdapter.count} items"
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
                            Log.e("spinnerMainCat", "gender Error: ${response.msg?:""}")
                        }

                        else -> {
                            Log.e("spinnerMainCat", "Unhandled case")
                        }
                    }
                }

                viewModel.fetchGender()

            } else {
                Toast.makeText(this@PostJobActivity, "Internet not connected", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("pers info", "Error occurred: ${e.localizedMessage}")
        }
    }

    private fun getStateList() {

        stateViewModel.stateresult.observe(this) { response ->
            when (response) {

                is BaseResponse.Loading -> {
                    pd?.show()
                }

                is BaseResponse.Success -> {
                    pd?.dismiss()
                    lifecycleScope.launch {
                        try {
                            response.data?.let { apiResponse ->
                                stateList = apiResponse.data

                                val arrayList = ArrayList<String>()
                                var selectedIndex = 0

                                val translatedTitle = async {
                                    TranslationHelper.translateText("Select State", languageName)
                                }

                                arrayList.add(0, translatedTitle.await()) // Add translated title

                                if (stateList != null && stateList.isNotEmpty()) {
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
                                        this@PostJobActivity,
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

                            }
                        } catch (e: Exception) {
                            Log.e("Translation Error", "Error translating states: ${e.message}")
                        }
                    }


                }

                is BaseResponse.Error -> {
                    pd?.dismiss()
                    // Handle error case
                    Log.e("MainActivity", "Error: ${response.msg}")
                }

                else -> {
                    // Handle other cases if any
                }
            }
        }

        // Call the ViewModel's State method to fetch states
        stateViewModel.State()
    }

    private fun getDistrictList(stateId: Int) {

        stateViewModel.districtresult.observe(this) { response ->
            when (response) {
                is BaseResponse.Loading -> {
                    pd?.show()
                }

                is BaseResponse.Success -> {
                    pd?.dismiss()
                    lifecycleScope.launch {
                        try {
                            response.data?.let { apiResponse ->
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
                                        this@PostJobActivity,
                                        android.R.layout.simple_spinner_item,
                                        arrayList
                                    )
                                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    binding.spinnerForDistrict.adapter = arrayAdapter

                                    // Set the spinner to the desired state by name
                                    binding.spinnerForDistrict.setSelection(selectedIndex)

                                    binding.spinnerForDistrict.onItemSelectedListener =
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

                            }
                        } catch (e: Exception) {
                            Log.e("Translation Error", "Error translating states: ${e.message}")
                        }
                    }

                }

                is BaseResponse.Error -> {
                    pd?.dismiss()
                    // Handle error case
                    Log.e("MainActivity", "Error: ${response.msg}")
                }

                else -> {
                    // Handle other cases if any
                }
            }
        }

        // Call the ViewModel's State method to fetch states
        stateViewModel.district(stateId!!)
    }

    private fun getMainCategory() {
        viewModel.mainCategoryResult.observe(this) { response ->
            pd?.show()
            when (response) {
                is BaseResponse.Loading -> {
                    Log.d("SalaryTypeAPI", "Loading salary types...")
                }

                is BaseResponse.Success -> {
                    pd?.dismiss()
                    lifecycleScope.launch {
                        try {

                            response.data?.let { apiResponse ->
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
                                        this@PostJobActivity,
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
                                                    val item = parent!!.getItemAtPosition(position)
                                                        .toString()
                                                    mainCatId =
                                                        mainCatList[position - 1].id.toString()
                                                    Log.d("mainCatId", "Selected ID: $mainCatId")

                                                    // Clear category & subcategory selections
                                                    selectedCatIds.clear()
                                                    selectedSubCatIds.clear()
                                                    selectedCategories.clear()
                                                    selectedSubCategories.clear()
                                                    binding.cGJobCat.removeAllViews()
                                                    binding.cGSubCat.removeAllViews()


                                                }
                                            }

                                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                                        }


                                } else {
                                    Log.e("spinnerMainCat", "Empty salaryTypeList from API")
                                }
                            }


                        } catch (e: Exception) {
                            Log.e(
                                "Translation Error",
                                "Error translating categories: ${e.message}"
                            )
                        }
                    }

                }

                is BaseResponse.Error -> {
                    pd?.dismiss()
                    Log.e("spinnerMainCat", "Error: ${response.msg}")
                }

                else -> {
                    Log.e("spinnerMainCat", "Unhandled case")
                }
            }
        }

        viewModel.fetchMainCategories()
    }


    private fun addJobPostData(
        userid: String,
        title: String,
        desc: String,
        address: String,
        selectedJobIds: MutableList<Int>,
        companyName: String,
        salaryTypeId: Int,
        salaryRangeId: Int,
        selectedMainCatIds: String,
        selectedCategories: MutableList<Map<String, Int>>,  // Make sure categories have "category_id" and "vacancies"
        selectedSubCategories: MutableList<Map<String, Int>>,  // Make sure subcategories have "sub_category_id" and "vacancies"
        keyResp: String,
        experiance: String,
        compDesc: String,
        jobExpDate: String,
        imgBase64String: String?,
        salary_amount: String
    ) {



        if (!isOnline) {
            Toast.makeText(this, "Internet not connected", Toast.LENGTH_SHORT).show()
            return
        }

        // Observe only once to prevent multiple observers being added
        viewModel.addJobPostResult.observe(this) { response ->
            when (response) {
                is BaseResponse.Loading -> {
                    pd?.show()
                }

                is BaseResponse.Success -> {
                    pd?.dismiss()
                    response.data?.let { data ->
                        Log.e("PostJob", "Code: ${data.code}")
                        Log.e("PostJob", "Status: ${data.status}")
                        Log.e("PostJob", "Message: ${data.message}")
                        if (data.code == 200) {
                            Toast.makeText(this, " ${data.message}", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@PostJobActivity, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, " ${data.message}", Toast.LENGTH_SHORT).show()
                        }

                    }
                }

                is BaseResponse.Error -> {
                    pd?.dismiss()
                    Log.e("PostJob", "Error: ${response.msg}")
                    Toast.makeText(this, "Error: ${response.msg}", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    pd?.dismiss()
                    Log.e("PostJob", "Unexpected response: $response")
                }
            }
        }


        // Convert selected categories and subcategories to the correct structure
        val categories = selectedCategories.map { category ->
            CategoryVacancy(
                category_id = category["category_id"] ?: 0,
                vacancies = category["vacancies"] ?: 0
            )
        }

        val subCategories = selectedSubCategories.map { subCategory ->
            SubCategoryVacancy(
                sub_category_id = subCategory["sub_category_id"] ?: 0,
                vacancies = subCategory["vacancies"] ?: 0
            )
        }



        Log.e("PostJob", "categories: $categories")
        Log.e("PostJob", "subCategories: $subCategories")

        viewModel.addJobPost(
            token = token,
            user_id = userid,
            title = title,
            description = desc,
            state_id = stateId,
            district_id = distId,
            gender_id = genderId,
            location = address,
            job_type_ids = selectedJobIds,
            company_name = companyName,
            salary_type_id = salaryTypeId,
            salary_range_id = salaryRangeId,
            categories = categories,  // Passing the transformed categories
            main_category_id = selectedMainCatIds,
            sub_categories = subCategories,  // Passing the transformed subcategories
            key_responsibilities = keyResp,
            education_id = educationId,
            experience = experiance,
            qualification = "",
            company_description = compDesc,
            job_expiry_date = jobExpDate,
            job_post_image = imgBase64String ?: "",
            salary_amount = salary_amount
        )


    }


    private fun getSalaryType() {
        viewModel.salaryTypeResult.observe(this) { response ->
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
                                salaryTypeList = apiResponse.data

                                Log.d("SalaryTypeList", "Size: ${salaryTypeList?.size}")

                                val arrayList = ArrayList<String>()
                                var selectedIndex = 0

                                val translatedTitle = async {
                                    TranslationHelper.translateText("Salary Type", languageName)
                                }
                                arrayList.add(0, translatedTitle.await())
                                // Translated name -> Original name
                                val translatedToOriginalMap = mutableMapOf<String, String>()


                                if (salaryTypeList != null && salaryTypeList.isNotEmpty()) {


                                   /* for (i in salaryTypeList.indices) {
                                        arrayTypeList.add(salaryTypeList[i].salary_type)
                                        if (salaryTypeList[i].salary_type == salaryTypeName) {
                                            selectedTypeIndex = i + 1
                                        }
                                    }*/

                                    val translations = salaryTypeList.mapIndexed { index, item ->
                                        async {
                                            val translatedName = TranslationHelper.translateText(
                                                item.salary_type,
                                                languageName
                                            )
                                            if (item.salary_type == salaryTypeName) {
                                                selectedIndex = index + 1
                                            }
                                            translatedToOriginalMap[translatedName] = item.salary_type
                                            translatedName
                                        }
                                    }

                                    arrayList.addAll(translations.awaitAll())

                                    Log.d("SalaryTypeArray", "ArrayList: $arrayList")

                                    val arrayAdapter = ArrayAdapter(
                                        this@PostJobActivity,
                                        android.R.layout.simple_spinner_item,
                                        arrayList
                                    )
                                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    binding.spinnerSalaryType.adapter = arrayAdapter

                                    Log.d(
                                        "SalaryTypeAdapter",
                                        "Adapter set with ${arrayAdapter.count} items"
                                    )

                                    binding.spinnerSalaryType.setSelection(selectedTypeIndex)

                                    binding.spinnerSalaryType.onItemSelectedListener =
                                        object : AdapterView.OnItemSelectedListener {
                                            override fun onItemSelected(
                                                parent: AdapterView<*>?,
                                                view: View?,
                                                position: Int,
                                                id: Long
                                            ) {
                                                if (position != 0) {
                                                    val item =
                                                        parent!!.getItemAtPosition(position).toString()
                                                    salaryTypeId = salaryTypeList[position - 1].id
                                                    Log.d(
                                                        "SalaryTypeSelected",
                                                        "Selected ID: $salaryTypeId"
                                                    )

                                                    getSalaryRange(salaryTypeId)
                                                }
                                            }

                                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                                        }
                                } else {
                                    Log.e("SalaryTypeAPI", "Empty salaryTypeList from API")
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
                    Log.e("SalaryTypeAPI", "Error: ${response.msg}")
                }

                else -> {
                    Log.e("SalaryTypeAPI", "Unhandled case")
                }
            }
        }

        viewModel.fetchSalaryType()
    }

    private fun getSalaryRange(salaryTypeId: Int) {
        viewModel.salaryResult.observe(this) { response ->
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

                                salaryRangeList = apiResponse.data

                                Log.d("SalaryTypeList", "Size: ${salaryTypeList?.size}")


                                val arrayList = ArrayList<String>()
                                var selectedIndex = 0

                                val translatedTitle = async {
                                    TranslationHelper.translateText("Select Salary Range", languageName)
                                }
                                arrayList.add(0, translatedTitle.await())

                                // Translated name -> Original name
                                val translatedToOriginalMap = mutableMapOf<String, String>()


                                if (salaryRangeList != null && salaryRangeList.isNotEmpty()) {
                                  /*  for (i in salaryRangeList.indices) {
                                        arrayList.add(salaryRangeList[i].salary)
                                        // Find the index of the state with the name "Delhi"
                                        if (salaryRangeList[i].salary == salaryRangeName) {
                                            selectedIndex =
                                                i + 1 // +1 because we added "Select State" at index 0
                                        }
                                    }*/

                                    val translations = salaryRangeList.mapIndexed { index, item ->
                                        async {
                                            val translatedName = TranslationHelper.translateText(
                                                item.salary,
                                                languageName
                                            )
                                            if (item.salary == salaryRangeName) {
                                                selectedIndex = index + 1
                                            }
                                            translatedToOriginalMap[translatedName] = item.salary
                                            translatedName
                                        }
                                    }

                                    arrayList.addAll(translations.awaitAll())

                                    val arrayAdapter = ArrayAdapter(
                                        this@PostJobActivity,
                                        android.R.layout.simple_spinner_item,
                                        arrayList
                                    )
                                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    binding.spinnerSalaryRange.adapter = arrayAdapter

                                    // Set the spinner to the desired state by name
                                    binding.spinnerSalaryRange.setSelection(selectedIndex)

                                    binding.spinnerSalaryRange.onItemSelectedListener =
                                        object : AdapterView.OnItemSelectedListener {
                                            override fun onItemSelected(
                                                parent: AdapterView<*>?,
                                                view: View?,
                                                position: Int,
                                                id: Long
                                            ) {
                                                if (position != 0) {
                                                    val item =
                                                        parent!!.getItemAtPosition(position).toString()
                                                    salaryRangeId = salaryRangeList[position - 1].id
                                                    salaryRangeName = salaryRangeList[position - 1].salary
                                                    Log.e("tag", "salaryRangeId Id idd $salaryRangeId")

                                                    if (salaryRangeName.equals("Other")) {
                                                        binding.tvOsalary.visibility = View.VISIBLE
                                                        binding.etSalary.visibility = View.VISIBLE
                                                    } else {
                                                        binding.tvOsalary.visibility = View.GONE
                                                        binding.etSalary.visibility = View.GONE
                                                    }
                                                }

                                            }

                                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                                        }

                                } else {
                                    Log.e("SalaryTypeAPI", "Empty salaryTypeList from API")
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
                    Log.e("SalaryTypeAPI", "Error: ${response.msg}")
                }

                else -> {
                    Log.e("SalaryTypeAPI", "Unhandled case")
                }
            }
        }

        viewModel.fetchSalaryRange(salaryTypeId)
    }


    /*private fun observeSubCategory() {
        if (isOnline) {
            viewModel.subcategoryResult.observe(this) { response ->
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


                        *//*Log.d(
                            "Job Post",
                            "Response data: ${response.data}"
                        ) // Log the raw response data

                        // Ensure the data is being correctly mapped into the list
                        subCategoryList = response.data!!
                        //response.data?.data ?: emptyList() // Extract the data from response
                        Log.d("Sub Cat",
                            "cat List size: ${subCategoryList.size}"
                        ) // Log the size of the list

                        // Call method to show multi-select dialog
                        selectedCatItems = BooleanArray(subCategoryList.size)
                        showcSubCategoryListDialog(
                            title = "Select Job Sub Category",
                            list = subCategoryList,
                            selectedItems = selectedSubCatItems,
                            selectedIds = selectedSubCatIds,
                            chipGroup = binding.cGSubCat
                        )*//*
                    }

                    is BaseResponse.Error -> {
                        pd?.dismiss() // Dismiss loading indicator
                        Toast.makeText(this, response.msg, Toast.LENGTH_SHORT)
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
    }*/

    /*  private fun showcSubCategoryListDialog(
          title: String,
          list: List<SubcategoryModel>,
          selectedItems: BooleanArray,
          selectedIds: MutableList<Int>,
          chipGroup: ChipGroup
      ) {
          Log.d("Job Post", "Opening subcategory dialog with list size: ${list.size}")

          if (list.isEmpty()) {
              Toast.makeText(this, "No subcategories found", Toast.LENGTH_SHORT).show()
              return
          }

          list.forEachIndexed { index, subcategory ->
              selectedItems[index] = selectedIds.contains(subcategory.id)
          }

          val dialog = AlertDialog.Builder(this)
              .setTitle(title)
              .setMultiChoiceItems(
                  list.map { it.name }.toTypedArray(),
                  selectedItems
              ) { _, which, isChecked ->
                  selectedItems[which] = isChecked
                  if (isChecked) {
                      if (!selectedIds.contains(list[which].id)) {
                          selectedIds.add(list[which].id)
                      }
                  } else {
                      selectedIds.remove(list[which].id)
                  }
              }
              .setPositiveButton("OK") { _, _ ->
                  chipGroup.removeAllViews()
                  selectedIds.forEach { id ->
                      val chip = Chip(this)
                      chip.text = list.find { it.id == id }?.name
                      chip.isCloseIconVisible = true
                      chip.setOnCloseIconClickListener {
                          selectedIds.remove(id)
                          selectedItems[list.indexOfFirst { it.id == id }] = false
                          showcSubCategoryListDialog(title, list, selectedItems, selectedIds, chipGroup)
                      }
                      chipGroup.addView(chip)
                  }
              }
              .setNegativeButton("Cancel", null)
              .create()

          dialog.show()
      }*/


    /*  private fun showcSubCategoryListDialog(title: String,
                                             list: List<SubcategoryModel>,
                                             selectedItems: BooleanArray,
                                             selectedIds: MutableList<Int>,
                                             chipGroup: ChipGroup) {
          // Ensure selectedItems reflects selectedIds
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
      }*/

    /*private fun observeCategory() {
        if (isOnline) {
            viewModel.categoryResult.observe(this) { response ->
                pd?.show() // Show loading indicator
                when (response) {
                    is BaseResponse.Success -> {
                        pd?.dismiss() // Dismiss loading indicator
                        Log.e(
                            "Job Post**",
                            "Response Category: ${response.data}"
                        ) // Log the raw response data

                        // Ensure the data is being correctly mapped into the list

                        //response.data?.data ?: emptyList() // Extract the data from response
                       // Log the size of the list

                        // Call method to show multi-select dialog
                      //  selectedCatItems = BooleanArray(categoryList.size)
                       *//* showcategoryListDialog(
                            title = "Select Job Category",
                            list = categoryList,
                            selectedItems = selectedCatItems,
                            selectedIds = selectedCatIds,
                            chipGroup = binding.cGJobCat
                        )*//*

                     *//*   val newList = response.data!!
                        for (newItem in newList) {
                            val oldItem = categoryList.find { it.id == newItem.id }
                            if (oldItem != null) {
                                newItem.isSelected = oldItem.isSelected
                                newItem.vacancies = oldItem.vacancies
                            }
                        }
                        categoryList = newList*//*

                        categoryList = response.data!!

                        selectedCatItems = BooleanArray(categoryList.size)

                        // Save pre-selected categories into selectedCategories
                        val preSelectedCategories = categoryList.filter { it.isSelected && it.vacancies > 0 }

                        // Update global selectedCategories with pre-selected categories
                        selectedCategories = preSelectedCategories.map {
                            mapOf("category_id" to it.id, "vacancies" to it.vacancies)
                        }.toMutableList()

                        // Pass the pre-selected categories to the dialog function
                        showCategoryVacancyDialog(categoryList, preSelectedCategories)




                    }

                    is BaseResponse.Error -> {
                        pd?.dismiss() // Dismiss loading indicator
                        Toast.makeText(this, response.msg, Toast.LENGTH_SHORT)
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
    }*/




    private fun observeSubCategory() {
        if (isOnline) {
            viewModel.subcategoryResult.observe(this) { response ->
                pd?.show() // Show loading indicator
                when (response) {
                    is BaseResponse.Success -> {
                        pd?.dismiss() // Dismiss loading indicator
                        Log.e("Job Post**", "Response Category Sub: ${response.data}")

                        subCategoryList = response.data!!

                        // Log the list before setting pre-selected categories
                        Log.e("Job Post**", "Fetched Categories Sub: $subCategoryList")

                    // Set pre-selected categories based on the global selectedCategories
                        subCategoryList.forEach { subCategory ->
                            selectedSubCategories.forEach { selectedSubCategory ->
                                if (selectedSubCategory["sub_category_id"] == subCategory.id) {
                                    subCategory.isSelected = true
                                    subCategory.vacancies = selectedSubCategory["vacancies"] ?: 0
                                }
                            }
                        }

                        // Save pre-selected categories into selectedCategories
                        val preSelectedSubCategories = subCategoryList.filter { it.isSelected && it.vacancies > 0 }

                        // Update global selectedCategories with pre-selected categories
                        selectedSubCategories = preSelectedSubCategories.map {
                            mapOf("sub_category_id" to it.id, "vacancies" to it.vacancies)
                        }.toMutableList()



                        Log.e("Job Post**", "Response selectedCategories Sub: ${selectedSubCategories}")
                        Log.e("Job Post**", "Response preSelectedCategories Sub: ${preSelectedSubCategories}")

                        // Pass the pre-selected categories to the dialog function
                        showcSubCategoryListDialog(subCategoryList, preSelectedSubCategories)
                    }

                    is BaseResponse.Error -> {
                        pd?.dismiss() // Dismiss loading indicator
                        Toast.makeText(this, response.msg, Toast.LENGTH_SHORT).show() // Show error message
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

    private fun showcSubCategoryListDialog(subCategoryList: List<SubcategoryModel>, preSelectedSubCategories: List<SubcategoryModel>) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_category_vacancy_list, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerViewCategory)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)

        // Set the pre-selected categories in the list
        subCategoryList.forEach { subCategory ->
            val preSelectedSubCategories = preSelectedSubCategories.find { it.id == subCategory.id }
            if (preSelectedSubCategories != null) {
                subCategory.isSelected = true
                subCategory.vacancies = preSelectedSubCategories.vacancies
            }
        }

        val adapter = SubCategoryAdapter(this, subCategoryList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val dialog = AlertDialog.Builder(this@PostJobActivity)
            .setView(dialogView)
            .setCancelable(false)  // Prevent dialog from dismissing when tapping outside or pressing back
            .create()


        btnSave.setOnClickListener {
            dialog.dismiss()

            // Clear previous chips
            binding.cGSubCat.removeAllViews()

            // Show chips for selected categories
            for (subCategory in subCategoryList) {
                if (subCategory.isSelected && subCategory.vacancies > 0) {
                    val chip = Chip(this).apply {
                        text = "${subCategory.name} (${subCategory.vacancies})"
                        isCloseIconVisible = true
                        setOnCloseIconClickListener {
                            // Unselect category and reset vacancies
                            subCategory.isSelected = false
                            subCategory.vacancies = 0

                            // Find the corresponding checkbox and uncheck it
                            val viewHolder = recyclerView.findViewHolderForAdapterPosition(subCategoryList.indexOf(subCategory)) as? SubCategoryAdapter.CategoryViewHolder
                            viewHolder?.checkbox?.isChecked = false

                            // Remove chip
                            binding.cGSubCat.removeView(this)

                            // Reopen dialog with updated state
                            showcSubCategoryListDialog(subCategoryList, preSelectedSubCategories)
                        }
                    }
                    binding.cGSubCat.addView(chip)
                }
            }

            // Update selectedCategories globally with the current selected categories
            selectedSubCategories = subCategoryList
                .filter { it.isSelected && it.vacancies > 0 }
                .map {
                    mapOf("sub_category_id" to it.id, "vacancies" to it.vacancies)
                }.toMutableList()

            // Log selected categories
            val jsonArray = JSONArray(selectedSubCategories)
            Log.e("SelectedCategoryList SUB", jsonArray.toString())
        }




        dialog.show()
    }


    private fun observeCategory() {
        if (isOnline) {
            viewModel.categoryResult.observe(this) { response ->
                pd?.show() // Show loading indicator
                when (response) {
                    is BaseResponse.Success -> {
                        pd?.dismiss() // Dismiss loading indicator
                        Log.e("Job Post**", "Response Category: ${response.data}")

                        categoryList = response.data!!

                        // Log the list before setting pre-selected categories
                        Log.e("Job Post**", "Fetched Categories: $categoryList")

                    // Set pre-selected categories based on the global selectedCategories
                        categoryList.forEach { category ->
                            selectedCategories.forEach { selectedCategory ->
                                if (selectedCategory["category_id"] == category.id) {
                                    category.isSelected = true
                                    category.vacancies = selectedCategory["vacancies"] ?: 0
                                }
                            }
                        }

                        // Save pre-selected categories into selectedCategories
                        val preSelectedCategories = categoryList.filter { it.isSelected && it.vacancies > 0 }

                        // Update global selectedCategories with pre-selected categories
                        selectedCategories = preSelectedCategories.map {
                            mapOf("category_id" to it.id, "vacancies" to it.vacancies)
                        }.toMutableList()



                        Log.e("Job Post**", "Response selectedCategories: ${selectedCategories}")
                        Log.e("Job Post**", "Response preSelectedCategories: ${preSelectedCategories}")

                        // Pass the pre-selected categories to the dialog function
                        showCategoryVacancyDialog(categoryList, preSelectedCategories)
                    }

                    is BaseResponse.Error -> {
                        pd?.dismiss() // Dismiss loading indicator
                        Toast.makeText(this, response.msg, Toast.LENGTH_SHORT).show() // Show error message
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




    private fun showCategoryVacancyDialog(categoryList: List<Category>, preSelectedCategories: List<Category>) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_category_vacancy_list, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerViewCategory)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)
        val ivClose = dialogView.findViewById<ImageView>(R.id.ivClose)

        // Set the pre-selected categories in the list
        categoryList.forEach { category ->
            val preSelectedCategory = preSelectedCategories.find { it.id == category.id }
            if (preSelectedCategory != null) {
                category.isSelected = true
                category.vacancies = preSelectedCategory.vacancies
            }
        }

        val adapter = CategoryAdapter(this, categoryList, selectedCatIds) // Pass selectedCatIds to the adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val dialog = AlertDialog.Builder(this@PostJobActivity)
            .setView(dialogView)
            .setCancelable(false)  // Prevent dialog from dismissing when tapping outside or pressing back
            .create()

        btnSave.setOnClickListener {
            dialog.dismiss()
            binding.cGJobCat.removeAllViews()

            for (category in categoryList) {
                if (category.isSelected && category.vacancies > 0) {
                    val chip = Chip(this).apply {
                        isCloseIconVisible = true
                        setOnCloseIconClickListener {
                            category.isSelected = false
                            category.vacancies = 0
                            binding.cGJobCat.removeView(this)
                            showCategoryVacancyDialog(categoryList, preSelectedCategories)
                        }
                    }

                    // Launch coroutine for translation
                    lifecycleScope.launch {
                        val translatedText = TranslationHelper.translateText(
                            category.category_name,
                            languageName
                        )
                        chip.text = "$translatedText (${category.vacancies})"
                        binding.cGJobCat.addView(chip)
                    }
                }
            }

            selectedCategories = categoryList
                .filter { it.isSelected && it.vacancies > 0 }
                .map {
                    mapOf("category_id" to it.id, "vacancies" to it.vacancies)
                }.toMutableList()

            val jsonArray = JSONArray(selectedCategories)
            Log.e("SelectedCategoryList", jsonArray.toString())
        }

        ivClose.setOnClickListener {
            dialog.dismiss()
            Log.d("DialogClose", "Clicked")
        }




        dialog.show()
    }







    /* private fun showCategoryVacancyDialog(categoryList: List<Category>) {
         val dialogView = layoutInflater.inflate(R.layout.dialog_category_vacancy_list, null)
         val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerViewCategory)
         val btnSave = dialogView.findViewById<Button>(R.id.btnOk)

         val adapter = CategoryAdapter(this, categoryList)
         recyclerView.layoutManager = LinearLayoutManager(this)
         recyclerView.adapter = adapter

         val dialog = AlertDialog.Builder(this@PostJobActivity)
             .setView(dialogView)
             .setCancelable(true)
             .create()

         btnSave.setOnClickListener {
             dialog.dismiss()

             // Clear previous chips
             binding.cGJobCat.removeAllViews()

             // Show chips for selected categories
             for (category in categoryList) {
                 if (category.isSelected && category.vacancies > 0) {
                     val chip = Chip(this).apply {
                         text = "${category.category_name} (${category.vacancies})"
                         isCloseIconVisible = true
                         setOnCloseIconClickListener {
                             // ✅ Unselect from data list
                             category.isSelected = false
                             category.vacancies = 0
                             // ✅ Remove chip from UI
                             binding.cGJobCat.removeView(this)
                             // ✅ Reopen dialog with current state
                             showCategoryVacancyDialog(categoryList)
                         }
                     }
                     binding.cGJobCat.addView(chip)
                 }
             }

             // Log selected data
             val selectedCategories = categoryList
                 .filter { it.isSelected && it.vacancies > 0 }
                 .map {
                     mapOf("category_id" to it.id, "vacancies" to it.vacancies)
                 }
             val jsonArray = JSONArray(selectedCategories)
             Log.e("SelectedCategoryList", jsonArray.toString())
         }

         dialog.show()
     }*/







    /*private fun showcategoryListDialog(
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

        val dialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setMultiChoiceItems(
                list.map { it.category_name }.toTypedArray(), // Display experience names as options
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

    private fun showcategoryListDialog(
        title: String,
        list: List<Category>,
        selectedItems: BooleanArray,
        selectedIds: MutableList<Int>,
        chipGroup: ChipGroup
    ) {
        list.forEachIndexed { index, category ->
            selectedItems[index] = selectedIds.contains(category.id)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setMultiChoiceItems(
                list.map { it.category_name }.toTypedArray(),
                selectedItems
            ) { _, which, isChecked ->
                selectedItems[which] = isChecked
                if (isChecked) {
                    if (!selectedIds.contains(list[which].id)) {
                        selectedIds.add(list[which].id)
                    }
                } else {
                    selectedIds.remove(list[which].id)
                }

            }
            .setPositiveButton("OK") { _, _ ->
                chipGroup.removeAllViews()

                selectedIds.forEach { id ->
                    val category = list.find { it.id == id } ?: return@forEach

                    // ✅ Mark category as selected
                    category.isSelected = true

                    val chip = Chip(this)
                    chip.text = if (category.vacancies > 0)
                        "${category.category_name} (${category.vacancies})"
                    else
                        category.category_name

                    chip.isCloseIconVisible = true

                    chip.setOnClickListener {
                        showVacancyInputDialog(category)
                    }

                    chip.setOnCloseIconClickListener {
                        selectedIds.remove(category.id)
                        category.isSelected = false  // ✅ Unmark on removal
                        category.vacancies = 0       // ✅ Reset vacancies
                        selectedItems[list.indexOfFirst { it.id == id }] = false
                        showcategoryListDialog(title, list, selectedItems, selectedIds, chipGroup)
                    }

                    chipGroup.addView(chip)
                }

            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun showVacancyInputDialog(category: Category) {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.setText(category.vacancies.takeIf { it > 0 }?.toString() ?: "")

        AlertDialog.Builder(this)
            .setTitle("Enter vacancies for ${category.category_name}")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val value = input.text.toString().toIntOrNull()
                if (value != null) {
                    category.vacancies = value
                    Toast.makeText(this, "${category.category_name}: $value vacancies", Toast.LENGTH_SHORT).show()
                    val selectedData = getSelectedCategoriesWithVacancies(categoryList)
                    Log.e("API_PAYLOAD", selectedData.toString())

                } else {
                    Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    fun getSelectedCategoriesWithVacancies(list: List<Category>): List<Map<String, Any>> {
        return list.filter { it.isSelected && it.vacancies > 0 }
            .map {
                mapOf(
                    "category_id" to it.id,
                    "vacancies" to it.vacancies
                )
            }
    }




    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Shram")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems) { dialog, which ->
            when (which) {
                0 -> openGallery()
                1 -> openCamera()
            }
        }
        pictureDialog.show()
    }

    /*private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }*/

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
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    /* val bitmap = data?.extras?.get("data") as Bitmap
                     Glide.with(this).load(bitmap).into(currentImageView)
                     val base64String = convertBitmapToBase64(bitmap)
                     if (currentImageView.id == R.id.iv_compImage) {
                         imgBase64String = base64String
                         Log.e("tag", "upiBase64String c : " + imgBase64String)
                     }*/
                    val bitmap = rotateImageIfRequired(cameraImageUri)
                    Glide.with(this).load(bitmap).into(currentImageView)

                    val base64String = convertBitmapToBase64(bitmap)
                    if (currentImageView.id == R.id.iv_compImage) {
                        imgBase64String = base64String
                       // Log.e("tag", "upiBase64String c : $imgBase64String")
                    }

                }

                GALLERY_REQUEST_CODE -> {
                    val uri = data?.data
                    Glide.with(this).load(uri).into(currentImageView)
                    val base64String = convertImageToBase64(uri!!)
                    if (currentImageView.id == R.id.iv_compImage) {
                        imgBase64String = base64String
                        //Log.e("tag", "upiBase64String g : " + imgBase64String)
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


    private fun hasPermissions(): Boolean {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_CODE)
    }


    private fun observeJobType() {
        if (isOnline) {
            viewModel.jobTyperesult.observe(this) { response ->
                pd?.show() // Show loading indicator
                when (response) {
                    is BaseResponse.Success -> {
                        pd?.dismiss() // Dismiss loading indicator
                        Log.d(
                            "FormActivity",
                            "Response data: ${response.data}"
                        ) // Log the raw response data

                        // Ensure the data is being correctly mapped into the list
                        jobList =
                            response.data?.data ?: emptyList() // Extract the data from response
                        Log.d(
                            "PostJob",
                            "Experience List size: ${jobList.size}"
                        ) // Log the size of the list

                        // Call method to show multi-select dialog
                        selectedJobItems = BooleanArray(jobList.size)
                        showjobListDialog(
                            title = "Select Job Type",
                            list = jobList,
                            selectedItems = selectedJobItems,
                            selectedIds = selectedJobIds,
                            chipGroup = binding.cGJobType
                        )
                    }

                    is BaseResponse.Error -> {
                        pd?.dismiss() // Dismiss loading indicator
                        Toast.makeText(this, response.msg, Toast.LENGTH_SHORT)
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

    fun showjobListDialog(
        title: String,
        list: List<JobType>, // The list of experiences
        selectedItems: BooleanArray, // Tracks selected items
        selectedIds: MutableList<Int>, // Stores the selected ids
        chipGroup: ChipGroup // The ChipGroup to display the selected items
    ) {
        // Ensure selectedItems reflects selectedIds
        list.forEachIndexed { index, experience ->
            selectedItems[index] = selectedIds.contains(experience.id)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setMultiChoiceItems(
                list.map { it.job_type }.toTypedArray(), // Display experience names as options
                selectedItems // Initial selection state
            ) { _, which, isChecked ->
                selectedItems[which] = isChecked
                if (isChecked) {
                    if (!selectedIds.contains(list[which].id)) {
                        selectedIds.add(list[which].id) // Add ID if not already selected
                    } else {
                        Toast.makeText(
                            this,
                            "${list[which].job_type} is already selected",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    selectedIds.remove(list[which].id) // Remove ID if unchecked
                }
            }
            .setPositiveButton("OK") { _, _ ->
                // When the user clicks "OK", update the UI with selected items
                Log.d("SelectedJobTypes", "Selected Job Type IDs: $selectedIds")
                // Toast.makeText(this, "Selected Job Type IDs: $selectedIds", Toast.LENGTH_SHORT).show()

                chipGroup.removeAllViews() // Clear previous chips
                selectedIds.forEach { id ->
                    val chip = Chip(this)
                    chip.text = list.find { it.id == id }?.job_type // Display the experience name
                    chip.isCloseIconVisible = true
                    chip.setOnCloseIconClickListener {
                        // Handle chip removal (unselecting an item)
                        selectedIds.remove(id)
                        selectedItems[list.indexOfFirst { it.id == id }] =
                            false // Unselect the item
                        showjobListDialog(
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

    /*   private fun observeSalary() {
           if (isOnline) {

               viewModel.salaryResult.observe(this) { response ->
                   pd?.show() // Show loading indicator
                   when (response) {
                       is BaseResponse.Success -> {
                           pd?.dismiss() // Dismiss loading indicator
                           Log.d(
                               "PostJob",
                               "Response data: ${response.data}"
                           ) // Log the raw response data

                           // Ensure the data is being correctly mapped into the list
                           salaryList =
                               response.data?.data ?: emptyList() // Extract the data from response
                           Log.d(
                               "PostJob",
                               "Salary List size: ${salaryList.size}"
                           ) // Log the size of the list

                           // Call method to show multi-select dialog
                           selectedSalaryItems = BooleanArray(salaryList.size)
                           showSalaryDialog(
                               title = "Select Salary Range",
                               list = salaryList,
                               selectedItems = selectedSalaryItems,
                               selectedIds = selectedSalaryIds,
                               chipGroup = binding.cGSalaryType
                           )
                       }

                       is BaseResponse.Error -> {
                           pd?.dismiss() // Dismiss loading indicator
                           Toast.makeText(this, response.msg, Toast.LENGTH_SHORT)
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
       }*/


    private fun showSalaryDialog(
        title: String,
        list: List<SalaryRange>,
        selectedItems: BooleanArray,
        selectedIds: MutableList<Int>,
        chipGroup: ChipGroup
    ) {
        // Ensure selectedItems reflects selectedIds
        list.forEachIndexed { index, salary ->
            selectedItems[index] = selectedIds.contains(salary.id)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setMultiChoiceItems(
                list.map { it.salary }.toTypedArray(),
                selectedItems
            ) { _, which, isChecked ->
                selectedItems[which] = isChecked
                if (isChecked) {
                    if (!selectedIds.contains(list[which].id)) {
                        selectedIds.add(list[which].id)
                    } else {
                        Toast.makeText(
                            this,
                            "${list[which].salary} is already selected",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    selectedIds.remove(list[which].id)
                }
            }
            .setPositiveButton("OK") { _, _ ->
                // When the user clicks "OK", update the UI with selected items
                chipGroup.removeAllViews()
                selectedIds.forEach { id ->
                    val chip = Chip(this)
                    chip.text = list.find { it.id == id }?.salary
                    chip.isCloseIconVisible = true
                    chip.setOnCloseIconClickListener {
                        // Handle chip removal (unselecting an item)
                        selectedIds.remove(id)
                        selectedItems[list.indexOfFirst { it.id == id }] = false
                        showSalaryDialog(
                            title,
                            list,
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
}