package com.uvk.shramapplication.ui.joblist.available_job_list

import android.annotation.SuppressLint
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
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.ActivityAvailableJobListBinding
import com.uvk.shramapplication.helper.CommenViewModel
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.response.DistrictData
import com.uvk.shramapplication.response.StateData
import com.uvk.shramapplication.ui.joblist.Job
import com.uvk.shramapplication.ui.joblist.JobListViewModel
import com.uvk.shramapplication.ui.main_category.MainCategory
import com.uvk.shramapplication.ui.registration.StateDTViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.token
import com.mahindra.serviceengineer.savedata.userid
import com.uvk.shramapplication.helper.TranslationHelper
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class AvailableJobListActivity : AppCompatActivity() {
    lateinit var binding : ActivityAvailableJobListBinding
    private var bottomSheetDialog: BottomSheetDialog? = null
    private lateinit var nodataImageView: ImageView
    private lateinit var jobAdapter: AvailJobListAdapter
    private lateinit var jobList: List<Job>
    private val viewModel by viewModels<JobListViewModel>()
    private val commensviewModel by viewModels<CommenViewModel>()
    private val stateViewModel by viewModels<StateDTViewModel>()
    private var pd: TransparentProgressDialog? = null
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
        binding  = ActivityAvailableJobListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        pd = TransparentProgressDialog(this, R.drawable.progress)
        nodataImageView = findViewById(R.id.nodataimg)

        binding.backicon.setOnClickListener {
            finish()
        }

        getFilterCategory()
        getJobList()

        binding.btnFilter.setOnClickListener {
            filterDialog()
        }


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

          //  viewModel.getJobList(userid!!,catId,"", stateId,distId)
            val stateIdParam = if (stateId == 0) "" else stateId.toString()
            val distIdParam = if (distId == 0) "" else distId.toString()
            val mainCatIdParam = if (mainCatId == 0) "" else mainCatId.toString()

            Log.e("tag","stateIdParam idd $stateIdParam")
            Log.e("tag","distIdParam idd $distIdParam")
            Log.e("tag","mainCatIdParam idd $mainCatIdParam")

            sendFilterJobList(stateIdParam,distIdParam,mainCatIdParam)

            bottomSheetDialog?.dismiss()

            stateId = 0
            distId = 0
            mainCatId = 0




        }

        // Show the dialog
        bottomSheetDialog?.show()
    }

    private fun sendFilterJobList(stateIdParam: String, distIdParam: String , mainCatIdParam : String) {

        viewModel.getJobList(userid!!,mainCatIdParam,"", stateIdParam,distIdParam)
       /* try {
            if (isOnline) {
                viewModel.jobListResult.observe(this) { response ->
                    pd?.show() // Show loading indicator
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            Log.e("tag","sendFilterJobList ${response.data?.status}")

                            if (response.data?.data.isNullOrEmpty()) {
                                // If the data is null or empty
                                nodataImageView.visibility = View.VISIBLE
                                binding.recyclerView.visibility = View.GONE

                                Glide.with(this@AvailableJobListActivity)
                                    .load(R.drawable.no_data_found)
                                    .into(nodataImageView)
                            } else {
                                nodataImageView.visibility = View.GONE
                                binding.recyclerView.visibility = View.VISIBLE

                                // If the data is available
                                jobList = response.data?.data
                                    ?: emptyList() // Extract the data, or use an empty list if null

                                binding.recyclerView.layoutManager = LinearLayoutManager(this)

                                jobAdapter = AvailJobListAdapter(
                                    this,
                                    jobList,
                                    ::savedJob
                                )

                                binding.recyclerView.adapter = jobAdapter

                            }
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            Toast.makeText(this@AvailableJobListActivity, response.msg, Toast.LENGTH_SHORT)
                                .show() // Show error message
                        }

                        is BaseResponse.Loading -> {
                            // Show loading indicator if needed
                        }
                    }
                }

                viewModel.getJobList(userid!!,mainCatIdParam,"", stateIdParam,distIdParam)

            } else {
                Toast.makeText(this@AvailableJobListActivity, "Internet not connected", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("Job list", "Error occurred: ${e.localizedMessage}")

        }*/

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
                                           this@AvailableJobListActivity,
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
               Toast.makeText(this@AvailableJobListActivity, "Internet not connected", Toast.LENGTH_SHORT).show()
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
                                    this@AvailableJobListActivity,
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
                                    this@AvailableJobListActivity,
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



    /* private fun getStateList() {

         stateViewModel.stateresult.observe(this) { response ->
             when (response) {

                 is BaseResponse.Loading -> {
                     pd?.show()
                 }

                 is BaseResponse.Success -> {
                     pd?.dismiss()
                     // Stop the loading indicator
                     response.data?.let { apiResponse ->
                         stateList = apiResponse.data

                         // val dataList: List<StateData>? = stateApiResponse?.DATA
                         val arrayList: ArrayList<String> = ArrayList()
                         arrayList.add(0, "Select State")

                         var selectedIndex = 0

                         if (stateList != null && stateList.isNotEmpty()) {
                             for (i in stateList.indices) {
                                 arrayList.add(stateList[i].state_name)
                                 // Find the index of the state with the name "Delhi"
                                 if (stateList[i].state_name == stateName) {
                                     selectedIndex =
                                         i + 1 // +1 because we added "Select State" at index 0
                                 }
                             }

                             val arrayAdapter = ArrayAdapter(
                                 this@AvailableJobListActivity,
                                 android.R.layout.simple_spinner_item,
                                 arrayList
                             )
                             arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                             spinnerForState.adapter = arrayAdapter

                             // Set the spinner to the desired state by name
                             spinnerForState.setSelection(selectedIndex)

                             spinnerForState.onItemSelectedListener =
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
                                             stateId = stateList[position - 1].id
                                            Log.e("tag","stateId idd $stateId")
                                             getDistrictList(stateId!!)

                                         }
                                     }

                                     override fun onNothingSelected(parent: AdapterView<*>?) {
                                         stateId = 0
                                         Log.e("tag","stateId not idd $stateId")
                                     }
                                 }
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
                     // Stop the loading indicator
                     response.data?.let { apiResponse ->
                         districtList = apiResponse.data

                         // val dataList: List<StateData>? = stateApiResponse?.DATA
                         val arrayList: ArrayList<String> = ArrayList()
                         arrayList.add(0, "Select District")

                         var selectedIndex = 0

                         if (districtList != null && districtList.isNotEmpty()) {
                             for (i in districtList.indices) {
                                 arrayList.add(districtList[i].district_name)
                                 // Find the index of the state with the name "Delhi"
                                 if (districtList[i].district_name == distName) {
                                     selectedIndex =
                                         i + 1 // +1 because we added "Select State" at index 0
                                 }
                             }

                             val arrayAdapter = ArrayAdapter(
                                 this@AvailableJobListActivity,
                                 android.R.layout.simple_spinner_item,
                                 arrayList
                             )
                             arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                             spinnerForDistrict.adapter = arrayAdapter

                             // Set the spinner to the desired state by name
                             spinnerForDistrict.setSelection(selectedIndex)

                             spinnerForDistrict.onItemSelectedListener =
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
                                             distId = districtList[position - 1].id
                                             Log.e("tag","distId idd $distId")


                                         }
                                     }

                                     override fun onNothingSelected(parent: AdapterView<*>?) {
                                         distId = 0
                                         Log.e("tag","distId not idd $distId")
                                     }
                                 }
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
     }*/

    private fun getFilterCategory() {
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
                                if (response.data!!.isNullOrEmpty()) {
                                    // If the data is null or empty



                                } else {
                                    // If the data is available
                                    mainCatList = response.data
                                    // Set the layout manager to horizontal
                                    binding.rvSkill.layoutManager = LinearLayoutManager(
                                        this,
                                        LinearLayoutManager.HORIZONTAL,
                                        false
                                    )

                                    // Set the adapter
                                    val adapter =
                                        com.uvk.shramapplication.ui.employeer.home.SkillAdapter(
                                            this,
                                            mainCatList
                                        ) { selectedId ->


                                            catId = selectedId
                                            getJobList()

                                            Log.e("Selected Category ID", catId)
                                        }
                                    binding.rvSkill.adapter = adapter
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
                Toast.makeText(this, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("Employer home", "Error occurred: ${e.localizedMessage}")
        }
    }

    private fun getJobList() {
        try {
            if (isOnline) {
                viewModel.jobListResult.observe(this) { response ->
                    pd?.show() // Show loading indicator
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            if (response.data?.data.isNullOrEmpty()) {
                                // If the data is null or empty

                                Toast.makeText(
                                    this@AvailableJobListActivity,
                                    response.data!!.message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                nodataImageView.visibility = View.VISIBLE
                                binding.recyclerView.visibility = View.GONE

                                Glide.with(this@AvailableJobListActivity)
                                    .load(R.drawable.no_data_found)
                                    .into(nodataImageView)
                            } else {

                                nodataImageView.visibility = View.GONE
                                binding.recyclerView.visibility = View.VISIBLE
                                // If the data is available
                                jobList = response.data?.data
                                    ?: emptyList() // Extract the data, or use an empty list if null

                                binding.recyclerView.layoutManager = LinearLayoutManager(this)

                                jobAdapter = AvailJobListAdapter(
                                    this,
                                    jobList,
                                    ::savedJob
                                )

                                binding.recyclerView.adapter = jobAdapter

                            }
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            Toast.makeText(this@AvailableJobListActivity, response.msg, Toast.LENGTH_SHORT)
                                .show() // Show error message
                        }

                        is BaseResponse.Loading -> {
                            // Show loading indicator if needed
                        }
                    }
                }

                viewModel.getJobList(userid!!,catId,"", stateId.toString(),distId.toString())

            } else {
                Toast.makeText(this@AvailableJobListActivity, "Internet not connected", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("Job list", "Error occurred: ${e.localizedMessage}")

        }
    }

    fun savedJob( job_id: String) {
        try {
            if (isOnline) {

                val token = token
                val userId = userid

                if (token.isNullOrEmpty() || userId.isNullOrEmpty()) {
                    Toast.makeText(
                        this@AvailableJobListActivity,
                        "User details are missing. Please Log in.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                viewModel.savedJobResult.observe(this) { response ->
                    pd?.show() // Show loading indicator
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            /*Toast.makeText(this@AvailableJobListActivity, response.data!!.message, Toast.LENGTH_SHORT)
                                .show()*/
                            getJobList()
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator

                            Toast.makeText(this@AvailableJobListActivity, response.msg, Toast.LENGTH_SHORT)
                                .show() // Show error message
                        }

                        is BaseResponse.Loading -> {
                            // Show loading indicator if needed
                        }
                    }
                }

                viewModel.saveJob(
                    token = token!!,
                    userId = userId!!,
                    jobId = job_id!!
                )

            } else {
                Toast.makeText(this, "Internet not connected", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("LikePostError", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(this@AvailableJobListActivity, "An error occurred: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT)
                .show()
        }

    }
}