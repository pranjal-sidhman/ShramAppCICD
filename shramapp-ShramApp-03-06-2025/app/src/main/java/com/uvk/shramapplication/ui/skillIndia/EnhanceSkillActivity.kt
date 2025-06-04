package com.uvk.shramapplication.ui.skillIndia

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.ActivityEnhanceSkillBinding
import com.uvk.shramapplication.helper.CommenViewModel
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.response.DistrictData
import com.uvk.shramapplication.response.SkillData
import com.uvk.shramapplication.response.StateData
import com.uvk.shramapplication.ui.registration.StateDTViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.userid
import com.uvk.shramapplication.helper.TranslationHelper
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class EnhanceSkillActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEnhanceSkillBinding
    private var pd: TransparentProgressDialog? = null
    private var bottomSheetDialog: BottomSheetDialog? = null

    private val viewModel by viewModels<CommenViewModel>()
    private val viewModelState by viewModels<StateDTViewModel>()
    private lateinit var skillDataList: List<SkillData>
    private lateinit var sectorNameList: List<SkillData>
    private lateinit var stateList: List<StateData>
    private lateinit var districtList: List<DistrictData>

    private lateinit var enhanceSkillAdapter: EnhanceSkillAdapter

    var sectorId: Int = 0
    var sector_name: String = ""

    var stateId: Int = 0
    var stateName: String = ""


    var distId: Int = 0
    var distName: String = ""

    lateinit var btnReset: TextView
    lateinit var spinnerForSectorName: Spinner
    lateinit var spinnerForState: Spinner
    lateinit var spinnerForDistrict: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEnhanceSkillBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pd = TransparentProgressDialog(this, R.drawable.progress)
        binding.backicon.setOnClickListener {
            finish()
        }

        getSkillList()

        binding.btnFilter.setOnClickListener {
            filterDialog()
        }

    }

    private fun filterDialog() {
        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)

        val dialogView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_filter, null)

        bottomSheetDialog?.setContentView(dialogView)

        // Initialize views
        btnReset = dialogView.findViewById<TextView>(R.id.tvReset)!!
        spinnerForState = dialogView.findViewById<Spinner>(R.id.spinnerForState)!!
        spinnerForDistrict = dialogView.findViewById<Spinner>(R.id.spinnerForDistrict)!!
        spinnerForSectorName = dialogView.findViewById<Spinner>(R.id.spinnerForSectorName)!!

        getSectorNameList()
        getStateList()

        // Handle send button click
        btnReset.setOnClickListener {

            val stateIdParam = if (stateId == 0) "" else stateId.toString()
            val distIdParam = if (distId == 0) "" else distId.toString()
            val sectorIdParam = if (sectorId == 0) "" else sectorId.toString()

            sendFilterData(stateIdParam, distIdParam, sectorIdParam)

            bottomSheetDialog?.dismiss()

            stateId = 0
            distId = 0
            sectorId = 0

            stateName = ""
            distName = ""
            sector_name = ""

        }

        // Show the dialog
        bottomSheetDialog?.show()
    }

    private fun getSectorNameList() {
        viewModel.sectorNameListResult.observe(this) { response ->
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
                                sectorNameList = apiResponse.data

                                // val dataList: List<StateData>? = stateApiResponse?.DATA
                                /*  val arrayList: ArrayList<String> = ArrayList()
                                  arrayList.add(0, "Select Sector Name")

                                  var selectedIndex = 0*/

                                val arrayList = ArrayList<String>()
                                var selectedIndex = 0

                                if (sectorNameList != null && sectorNameList.isNotEmpty()) {

                                  /*  for (i in sectorNameList.indices) {
                                        arrayList.add(sectorNameList[i].sector_name)
                                        // Find the index of the state with the name "Delhi"
                                        if (sectorNameList[i].sector_name == sector_name) {
                                            selectedIndex =
                                                i + 1 // +1 because we added "Select State" at index 0
                                        }
                                    }*/

                                    // Translate "Select Main Category"
                                    val translatedTitle = async {
                                        TranslationHelper.translateText(
                                            "Select Sector Name",
                                            languageName
                                        )
                                    }

                                    arrayList.add(0, translatedTitle.await()) // Add translated title

                                    // Translate main category names
                                    val translations = sectorNameList.mapIndexed { index, item ->
                                        async {
                                            val translatedName = TranslationHelper.translateText(
                                                item.sector_name, languageName
                                            )

                                            if (item.sector_name == sector_name) {
                                                selectedIndex = index + 1
                                            }
                                            translatedName
                                        }
                                    }

                                    arrayList.addAll(translations.awaitAll()) // Wait for all translations

                                    val arrayAdapter = ArrayAdapter(
                                        this@EnhanceSkillActivity,
                                        android.R.layout.simple_spinner_item,
                                        arrayList
                                    )
                                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    spinnerForSectorName.adapter = arrayAdapter

                                    // Set the spinner to the desired state by name
                                    spinnerForSectorName.setSelection(selectedIndex)

                                    spinnerForSectorName.onItemSelectedListener =
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
                                                    sectorId = sectorNameList[position - 1].id
                                                    sector_name = sectorNameList[position - 1].sector_name

                                                    Log.e("tag","Sector id :$sectorId")

                                                }
                                            }

                                            override fun onNothingSelected(parent: AdapterView<*>?) {}
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
        viewModel.getSectorNameList()
    }

    private fun getStateList() {

        viewModelState.stateresult.observe(this) { response ->
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
                                                selectedIndex = index + 1 // Offset due to "Select State"
                                            }
                                            translatedName
                                        }
                                    }

                                    arrayList.addAll(translations.awaitAll()) // Wait for all translations

                                   /* for (i in stateList.indices) {
                                        arrayList.add(stateList[i].state_name)
                                        // Find the index of the state with the name "Delhi"
                                        if (stateList[i].state_name == stateName) {
                                            selectedIndex =
                                                i + 1 // +1 because we added "Select State" at index 0
                                        }
                                    }*/

                                    val arrayAdapter = ArrayAdapter(
                                        this@EnhanceSkillActivity,
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
                                                    stateName = stateList[position - 1].state_name
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
                    Log.e("MainActivity", "Error: ${response.msg}")
                }

                else -> {
                    // Handle other cases if any
                }
            }
        }

        // Call the ViewModel's State method to fetch states
        viewModelState.State()
    }


    private fun getDistrictList(stateId: Int) {

        viewModelState.districtresult.observe(this) { response ->
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
                                    /*for (i in districtList.indices) {
                                        arrayList.add(districtList[i].district_name)
                                        // Find the index of the state with the name "Delhi"
                                        if (districtList[i].district_name == distName) {
                                            selectedIndex =
                                                i + 1 // +1 because we added "Select State" at index 0
                                        }
                                    }*/


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
                                        this@EnhanceSkillActivity,
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
                                                    distName = districtList[position - 1].district_name

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
                    Log.e("MainActivity", "Error: ${response.msg}")
                }

                else -> {
                    // Handle other cases if any
                }
            }
        }

        // Call the ViewModel's State method to fetch states
        viewModelState.district(stateId!!)
    }

    private fun sendFilterData(stateIdParam: String, distIdParam: String, sectorIdParam: String) {

        Log.e("MainActivity", "userid: ${userid}")
        Log.e("MainActivity", "distName: ${distName}")
        Log.e("MainActivity", "stateName: ${stateName}")
        Log.e("MainActivity", "sectorName: ${sector_name}")
        viewModel.getSkillList(userid, distName, stateName, sector_name)


    }

    private fun getSkillList() {
        if (isOnline) {
            pd?.show() // Show loading indicator before making the API call

            viewModel.skillListResult.observe(this) { response ->
                when (response) {
                    is BaseResponse.Success -> {
                        pd?.dismiss() // Dismiss loading indicator

                        Log.e("tag", "Posted job resp : $response")

                        if (response.data?.data.isNullOrEmpty()) {
                            // No data available, show 'no data' image
                            Toast.makeText(
                                this@EnhanceSkillActivity,
                                response.data?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.nodataimg.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                        } else {
                            // Data is available, show the RecyclerView
                            skillDataList = response.data?.data ?: emptyList()
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.nodataimg.visibility = View.GONE

                            binding.recyclerView.layoutManager = LinearLayoutManager(this)
                            enhanceSkillAdapter = EnhanceSkillAdapter(this, skillDataList)
                            binding.recyclerView.adapter = enhanceSkillAdapter
                            enhanceSkillAdapter.notifyDataSetChanged()
                        }
                    }

                    is BaseResponse.Error -> {
                        pd?.dismiss() // Dismiss loading indicator
                        Toast.makeText(this@EnhanceSkillActivity, response.msg, Toast.LENGTH_SHORT)
                            .show() // Show error message
                    }

                    is BaseResponse.Loading -> {
                        // Show loading indicator if needed
                        pd?.show()
                    }
                }
            }

            viewModel.getSkillList(userid, distName, stateName, sector_name)

        } else {
            Toast.makeText(this@EnhanceSkillActivity, "Internet not connected", Toast.LENGTH_SHORT)
                .show()
        }
    }
}