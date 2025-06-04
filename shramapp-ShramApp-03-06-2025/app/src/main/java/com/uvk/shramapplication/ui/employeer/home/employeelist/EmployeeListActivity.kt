package com.uvk.shramapplication.ui.employeer.home.employeelist

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.uvk.shramapplication.MainActivity
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.ActivityEmployeeListBinding
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.ui.employeer.home.EmployeerViewModel
import com.uvk.shramapplication.ui.employeer.response.EmployeerData
import com.uvk.shramapplication.ui.employeer.response.EmployeerResponse
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.token
import com.mahindra.serviceengineer.savedata.userid
import com.uvk.shramapplication.helper.CommenViewModel
import com.uvk.shramapplication.helper.TranslationHelper
import com.uvk.shramapplication.response.DistrictData
import com.uvk.shramapplication.response.StateData
import com.uvk.shramapplication.ui.main_category.MainCategory
import com.uvk.shramapplication.ui.registration.StateDTViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class EmployeeListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmployeeListBinding
    private var bottomSheetDialog: BottomSheetDialog? = null
    private lateinit var nodataImageView: ImageView
    private lateinit var empListAdapter: EmpListAdapter
    private lateinit var empList: List<EmployeerData>
    private lateinit var jobList: List<EmployeerData>
    private lateinit var selectedEmpList: List<EmployeerResponse>
    private val viewModel by viewModels<EmployeerViewModel>()
    private val commensviewModel by viewModels<CommenViewModel>()
    private val stateViewModel by viewModels<StateDTViewModel>()
    private var pd: TransparentProgressDialog? = null
    private var selectedEmpIds = mutableListOf<Int>() // Store selected employees

    var jobId : Int = 0
    var jobName: String = " "
    var keyword: String? = null

    var catId: String = ""
    private lateinit var mainCatList: List<MainCategory>

    lateinit var btnReset: TextView
    lateinit var spinnerForCategory: Spinner
    lateinit var spinnerForState: Spinner
    lateinit var spinnerForDistrict: Spinner

    var stateId: Int = 0
    var stateName: String = " "

    var mainCatId: Int = 0
    var mainCatName: String = ""

    var distId: Int = 0
    var distName: String = " "
    private lateinit var stateList: List<StateData>
    private lateinit var districtList: List<DistrictData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEmployeeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pd = TransparentProgressDialog(this, R.drawable.progress)
        nodataImageView = findViewById(R.id.nodataimg)

        keyword = intent.getStringExtra("keyword")

        binding.backicon.setOnClickListener {
            finish()
        }

        binding.btnFilter.setOnClickListener {
            filterDialog()
        }

        binding.btnSendReq.setOnClickListener {
            Log.e("tag", "selectedEmpIds before API call: $selectedEmpIds")

            when {

                jobId == 0 -> {
                    Toast.makeText(this, "Please select a job", Toast.LENGTH_SHORT).show()
                }
                selectedEmpIds.isEmpty() -> {
                    Toast.makeText(this, "Please select at least one employee", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    sendSelectedRequest()
                }
            }
        }
        
        getJobDataList(userid!!)



        getEmpList()

    }

    @SuppressLint("MissingInflatedId")
    private fun filterDialog() {
        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)

        val dialogView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_job_filter, null)

        bottomSheetDialog?.setContentView(dialogView)


        // Initialize views
        btnReset = dialogView.findViewById<TextView>(R.id.tvReset)!!
        spinnerForState = dialogView.findViewById<Spinner>(R.id.spinnerForState)!!
        spinnerForDistrict = dialogView.findViewById<Spinner>(R.id.spinnerForDistrict)!!
        spinnerForCategory = dialogView.findViewById<Spinner>(R.id.spinnerForCategory)!!

        getMainCategory()
        getStateList()

        // Handle send button click
        btnReset.setOnClickListener {

            val stateIdParam = if (stateId == 0) "" else stateId.toString()
            val distIdParam = if (distId == 0) "" else distId.toString()
            val mainCatIdParam = if (mainCatId == 0) "" else mainCatId.toString()

            Log.e("tag","stateIdParam idd $stateIdParam")
            Log.e("tag","distIdParam idd $distIdParam")
            Log.e("tag","mainCatIdParam idd $mainCatIdParam")

            sendFilterJobList(stateIdParam,distIdParam,mainCatIdParam)
           // sendFilterJobList(stateId,distId,mainCatId)

            bottomSheetDialog?.dismiss()

            stateId = 0
            distId = 0
            mainCatId = 0




        }

        // Show the dialog
        bottomSheetDialog?.show()
    }

    private fun getJobDataList(userid: String) {
        viewModel.jobDataSpinnerResult.observe(this) { response ->
            when (response) {
                is BaseResponse.Loading -> {
                    // Show a loading indicator (optional)
                }

                is BaseResponse.Success -> {
                    // Stop the loading indicator
                    response.data?.let { apiResponse ->

                        lifecycleScope.launch {
                            try {
                        jobList = apiResponse.data

                                val arrayList = ArrayList<String>()
                                var selectedIndex = 0

                                val translatedTitle = async {
                                    TranslationHelper.translateText("Select Job", languageName)
                                }

                                arrayList.add(0, translatedTitle.await()) // Add translated title

                        if (jobList != null && jobList.isNotEmpty()) {
                           /* for (i in jobList.indices) {
                                arrayList.add(jobList[i].title)
                                // Find the index of the state with the name "Delhi"
                                if (jobList[i].title == jobName) {
                                    selectedIndex =
                                        i + 1 // +1 because we added "Select State" at index 0
                                }
                            }*/
                            // Translate district names
                            val translations = jobList.mapIndexed { index, item ->
                                async {
                                    val translatedName = TranslationHelper.translateText(
                                        item.title, languageName
                                    )

                                    if (item.title == jobName) {
                                        selectedIndex = index + 1
                                    }
                                    translatedName
                                }
                            }

                            arrayList.addAll(translations.awaitAll()) // Wait for all translations

                            val arrayAdapter = ArrayAdapter(
                                this@EmployeeListActivity,
                                android.R.layout.simple_spinner_item,
                                arrayList
                            )
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.spinnerForJob.adapter = arrayAdapter

                            // Set the spinner to the desired state by name
                            binding.spinnerForJob.setSelection(selectedIndex)

                            binding.spinnerForJob.onItemSelectedListener =
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
                                            jobId = jobList[position - 1].id


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
                    // Handle error case
                    Log.e("MainActivity", "Error: ${response.msg}")
                }

                else -> {
                    // Handle other cases if any
                }
            }
        }

        // Call the ViewModel's State method to fetch states
        viewModel.getJobDataSpinner(userid!!)
    }


    private fun getEmpList() {
        try {
            if (isOnline) {
                viewModel.getCategoryWiseEmpResult.observe(this) { response ->
                    pd?.show() // Show loading indicator
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            if (response.data?.data.isNullOrEmpty()) {
                                // If the data is null or empty

                                Toast.makeText(
                                    this@EmployeeListActivity,
                                    response.data!!.message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                nodataImageView.visibility = View.VISIBLE
                                binding.recyclerView.visibility = View.GONE
                                binding.btnSendReq.visibility = View.GONE

                                Glide.with(this@EmployeeListActivity)
                                    .load(R.drawable.no_data_found)
                                    .into(nodataImageView)
                            } else {
                                // If the data is available
                                empList = response.data?.data
                                    ?: emptyList() // Extract the data, or use an empty list if null

                                binding.recyclerView.layoutManager = LinearLayoutManager(this)


                                Log.e("tag","asgs : $empList")

                                if (::empListAdapter.isInitialized) {
                                    empListAdapter.updateList(empList)
                                } else {
                                    empListAdapter = EmpListAdapter(
                                        this,
                                        empList,
                                        selectedEmpIds,
                                        ::selectedEmp
                                    )
                                    binding.recyclerView.adapter = empListAdapter
                                }

                            }
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            Toast.makeText(
                                this@EmployeeListActivity,
                                response.msg?:"",
                                Toast.LENGTH_SHORT
                            ).show() // Show error message
                        }

                        is BaseResponse.Loading -> {
                            // Show loading indicator if needed
                        }
                    }
                }

                val searchKeyword = keyword ?: ""
                val stateIdParam = if (stateId == 0) "" else stateId.toString()
                val distIdParam = if (distId == 0) "" else distId.toString()
                val mainCatIdParam = if (mainCatId == 0) "" else mainCatId.toString()

                Log.e("getEmpList","stateIdParam idd $stateIdParam")
                Log.e("getEmpList","distIdParam idd $distIdParam")
                Log.e("getEmpList","mainCatIdParam idd $mainCatIdParam")
                viewModel.getCategoryWiseEmp(userid!!,mainCatIdParam,stateIdParam,distIdParam, searchKeyword)


            } else {
                Toast.makeText(
                    this@EmployeeListActivity,
                    "Internet not connected",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("categorywise emp", "Error occurred: ${e.localizedMessage}")

        }
    }


    private fun selectedEmp(empIds: List<Int>) {
        Log.e("emp list", "Selected Employees before update: $selectedEmpIds")

        selectedEmpIds = empIds.toMutableList()


        Log.e("emp list", "Selected Employees after update: $selectedEmpIds")
    }


    private fun sendSelectedRequest() {
        try {
            if (isOnline) {
                pd?.show()
                viewModel.addSelectedEmpResult.observe(this) { response ->
                    pd?.dismiss()
                    when (response) {
                        is BaseResponse.Success -> {
                            Toast.makeText(this, response.data?.message, Toast.LENGTH_SHORT).show()

                            Log.e("API Response", "selectedEmpIds before clearing: $selectedEmpIds")

                            startActivity(Intent(this,MainActivity::class.java))

                            // Delay clearing selectedEmpIds until the list is refreshed
                            /*Handler(Looper.getMainLooper()).postDelayed({
                                selectedEmpIds.clear()
                                empListAdapter.updateSelectedEmpIds(selectedEmpIds)
                                Log.e("API Response", "selectedEmpIds after clearing: $selectedEmpIds")
                            }, 1000)*/ // Delay for 1 second to allow list update
                        }

                        is BaseResponse.Error -> {
                            Toast.makeText(this, response.msg, Toast.LENGTH_SHORT).show()
                            Log.e("API Response", "Error: ${response.msg}")
                        }

                        is BaseResponse.Loading -> { }
                    }
                }

                Log.e("API Send before", "selectedEmpIds #: $selectedEmpIds")
                viewModel.addSelectedEmployees(token!!, userid!!,jobId!!, selectedEmpIds,)
                Log.e("API Send after", "selectedEmpIds #: $selectedEmpIds")
            } else {
                Toast.makeText(this, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("Job list", "Error occurred: ${e.localizedMessage}")
        }
    }

    private fun getMainCategory() {
        try {
            if (isOnline) {
                commensviewModel.mainCategoryResult.observe(this) { response ->
                    pd?.show()
                    when (response) {
                        is BaseResponse.Loading -> {
                            pd?.show()
                        }

                        is BaseResponse.Success -> {
                            pd?.dismiss()
                            response.data?.let { apiResponse ->
                                mainCatList = response.data
                                Log.d("mainCatList", "Size: ${mainCatList?.size}")
                                Log.d("mainCatList", "res: ${response.data}")

                                val arrayList = ArrayList<String>()
                                var selectedIndex = 0

                                lifecycleScope.launch {
                                    try {
                                        // Translate "Select Main Category"
                                        val translatedTitle = async {
                                            TranslationHelper.translateText(
                                                "Select Main Category",
                                                languageName
                                            )
                                        }

                                        arrayList.add(0, translatedTitle.await()) // Add translated title

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

                                        Log.d("Main Cat", "ArrayList: $arrayList")

                                        // Set up the Spinner Adapter
                                        val arrayAdapter = ArrayAdapter(
                                            this@EmployeeListActivity,
                                            android.R.layout.simple_spinner_item,
                                            arrayList
                                        )
                                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                        spinnerForCategory.adapter = arrayAdapter

                                        // Set selection based on previous selection
                                        spinnerForCategory.setSelection(selectedIndex)

                                        Log.d("mainCatId", "Adapter set with ${arrayAdapter.count} items")

                                        // Handle Spinner Selection
                                        spinnerForCategory.onItemSelectedListener =
                                            object : AdapterView.OnItemSelectedListener {
                                                override fun onItemSelected(
                                                    parent: AdapterView<*>?,
                                                    view: View?,
                                                    position: Int,
                                                    id: Long
                                                ) {
                                                    if (position != 0) {
                                                        mainCatId = mainCatList[position - 1].id
                                                        Log.e("mainCatId", "Selected ID: $mainCatId")
                                                    }
                                                }

                                                override fun onNothingSelected(parent: AdapterView<*>?) {}
                                            }
                                    } catch (e: Exception) {
                                        Log.e("Translation Error", "Error translating categories: ${e.message}")
                                    }
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

                commensviewModel.fetchMainCategories()
            } else {
                Toast.makeText(this@EmployeeListActivity, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("getMainCategory", "Exception: ${e.message}")
        }
    }

    private fun getStateList() {
        stateViewModel.stateresult.observe(this) { response ->
            when (response) {
                is BaseResponse.Loading -> pd?.show()

                is BaseResponse.Success -> {
                    pd?.dismiss()
                    response.data?.let { apiResponse ->
                        stateList = apiResponse.data
                        val arrayList = ArrayList<String>()
                        var selectedIndex = 0

                        lifecycleScope.launch {
                            try {
                                // Translate "Select State"
                                val translatedTitle = async {
                                    TranslationHelper.translateText("Select State", languageName)
                                }

                                arrayList.add(0, translatedTitle.await()) // Add translated title

                                // Translate state names
                                val translations = stateList.mapIndexed { index, item ->
                                    async {
                                        val translatedName = TranslationHelper.translateText(
                                            item.state_name, languageName
                                        )

                                        if (item.state_name == stateName) {
                                            selectedIndex = index + 1 // Offset due to "Select State"
                                        }
                                        translatedName
                                    }
                                }

                                arrayList.addAll(translations.awaitAll()) // Wait for all translations

                                Log.d("State List", "ArrayList: $arrayList")

                                // Set up the Spinner Adapter
                                val arrayAdapter = ArrayAdapter(
                                    this@EmployeeListActivity,
                                    android.R.layout.simple_spinner_item,
                                    arrayList
                                )
                                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                spinnerForState.adapter = arrayAdapter

                                // Set previously selected state
                                spinnerForState.setSelection(selectedIndex)

                                spinnerForState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>?, view: View?, position: Int, id: Long
                                    ) {
                                        if (position != 0) {
                                            stateId = stateList[position - 1].id
                                            Log.e("tag", "Selected State ID: $stateId")
                                            getDistrictList(stateId!!)
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        stateId = 0
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
                    Log.e("State Fetch Error", "Error: ${response.msg}")
                }

                else -> {}
            }
        }

        // Fetch states from ViewModel
        stateViewModel.State()
    }

    private fun getDistrictList(stateId: Int) {
        stateViewModel.districtresult.observe(this) { response ->
            when (response) {
                is BaseResponse.Loading -> pd?.show()

                is BaseResponse.Success -> {
                    pd?.dismiss()
                    response.data?.let { apiResponse ->
                        districtList = apiResponse.data
                        val arrayList = ArrayList<String>()
                        var selectedIndex = 0

                        lifecycleScope.launch {
                            try {
                                // Translate "Select District"
                                val translatedTitle = async {
                                    TranslationHelper.translateText("Select District", languageName)
                                }

                                arrayList.add(0, translatedTitle.await()) // Add translated title

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

                                Log.d("District List", "ArrayList: $arrayList")

                                // Set up the Spinner Adapter
                                val arrayAdapter = ArrayAdapter(
                                    this@EmployeeListActivity,
                                    android.R.layout.simple_spinner_item,
                                    arrayList
                                )
                                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                spinnerForDistrict.adapter = arrayAdapter

                                // Set previously selected district
                                spinnerForDistrict.setSelection(selectedIndex)

                                spinnerForDistrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>?, view: View?, position: Int, id: Long
                                    ) {
                                        if (position != 0) {
                                            distId = districtList[position - 1].id
                                            Log.e("tag", "Selected District ID: $distId")
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        distId = 0
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("Translation Error", "Error translating districts: ${e.message}")
                            }
                        }
                    }
                }

                is BaseResponse.Error -> {
                    pd?.dismiss()
                    Log.e("District Fetch Error", "Error: ${response.msg}")
                }

                else -> {}
            }
        }

        // Fetch districts from ViewModel
        stateViewModel.district(stateId)
    }

    private fun sendFilterJobList(stateIdParam: String, distIdParam: String , mainCatIdParam : String) {

        val searchKeyword = keyword ?: ""
        viewModel.getCategoryWiseEmp(userid!!, mainCatIdParam,stateIdParam,distIdParam,searchKeyword)

    }
}