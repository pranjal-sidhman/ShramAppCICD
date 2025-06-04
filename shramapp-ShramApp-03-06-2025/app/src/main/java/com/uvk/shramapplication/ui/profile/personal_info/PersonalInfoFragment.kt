package com.uvk.shramapplication.ui.profile.personal_info

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AlertDialog.Builder
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.FragmentPersonalInfoBinding
import com.uvk.shramapplication.helper.CommenViewModel
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.response.DistrictData
import com.uvk.shramapplication.response.StateData
import com.uvk.shramapplication.ui.employeer.response.EmployeerData
import com.uvk.shramapplication.ui.employeer.worklist.list.MyPostedJobsAdapter
import com.uvk.shramapplication.ui.main_category.MainCategory
import com.uvk.shramapplication.ui.profile.CategoryData
import com.uvk.shramapplication.ui.profile.ProflieViewModel
import com.uvk.shramapplication.ui.registration.StateDTViewModel
import com.uvk.shramapplication.ui.subcategory.SubcategoryModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.userid
import com.uvk.shramapplication.helper.Education
import com.uvk.shramapplication.helper.GenderType
import com.uvk.shramapplication.helper.TranslationHelper
import com.uvk.shramapplication.ui.category.Category
import com.yalantis.ucrop.UCrop
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


import java.io.File
import java.io.FileInputStream
import java.io.IOException


class PersonalInfoFragment : Fragment() {
    private var _binding: FragmentPersonalInfoBinding? = null
    private val binding get() = _binding!!
    private var bottomSheetDialog: BottomSheetDialog? = null
    private lateinit var profileViewModel: ProfileViewModel // Fix typo in class name

    private var pd: TransparentProgressDialog? = null

    private val viewModel by viewModels<ProflieViewModel>()
    private val commenViewModel by viewModels<StateDTViewModel>()
    private val commensviewModel by viewModels<CommenViewModel>()

    //  private val viewModel by viewModels<EmployeerViewModel>()
    private lateinit var postJobList: List<EmployeerData>
    private lateinit var jobAdapter: MyPostedJobsAdapter

    private lateinit var stateList: List<StateData>
    private lateinit var districtList: List<DistrictData>
    private lateinit var categoryList: List<CategoryData>
    private lateinit var subCategoryList: List<SubcategoryModel>
    private lateinit var mainCatList: List<MainCategory>
    private lateinit var genderList: List<GenderType>
    private lateinit var educationList: List<Education>

    private var selectedCatIds: MutableList<Int> = mutableListOf()
    private var selectedCatName: String = " "
    private var selectedSubCatIds: MutableList<Int> = mutableListOf()
    private var selectedSubCatName: String = " "

    private lateinit var selectedSubCatItems: BooleanArray
    private lateinit var selectedCatItems: BooleanArray

    var stateId: Int = 0
    var stateName: String = " "
    var genderId: Int = 0
    var genderName: String = " "
    var educationId: Int = 0
    var educationName: String = " "

    var getMobileNo: String? = null

    var distId: Int = 0
    var distName: String = " "

    private var previousMainCatId: String? = null
    private var roleId: String? = null

    var mainCatId: String = " "
    var mainCatName: String = " "


    private var api_aadharImg: String? = null
    private var aadharImgUrl: String? = null


    private lateinit var scrollView: NestedScrollView
    private lateinit var etEmail: EditText
    private lateinit var etAddress: EditText
    private lateinit var etSkill: EditText
    private lateinit var etPincode: EditText
    private lateinit var etExperiance: EditText
    private lateinit var tvJobCat: TextView
    private lateinit var tvsubCategory: TextView
    private lateinit var tvFileName: TextView
    private lateinit var cGSubCat: ChipGroup
    private lateinit var cGJobCat: ChipGroup
    private lateinit var spinnerForState: Spinner
    private lateinit var spinnerForDitrict: Spinner
    private lateinit var spinnerForGender: Spinner
    private lateinit var spinnerForEducation: Spinner
    private lateinit var spinnerMainCat: Spinner
    private lateinit var iv_aadharImage_d: ImageView
    private lateinit var tvAaName: TextView
    private lateinit var tvAaNumber: TextView
    private lateinit var lladhrNumber: LinearLayout
    private lateinit var lladhrName: LinearLayout
    private lateinit var iv_aadharPdf: AppCompatImageView
    private lateinit var btnUpload: LinearLayoutCompat
    private lateinit var llText: LinearLayoutCompat


    private lateinit var currentImageView: ImageView
    private var aadharBase64String: String? = null

    private val CAMERA_REQUEST_CODE = 1001
    private val GALLERY_REQUEST_CODE = 1002

    private val REQUIRED_PERMISSIONS = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.READ_MEDIA_IMAGES // For Android 13+
    )

    private var selectedFileUri: Uri? = null
    private var selectedFile: File? = null

    val TAG = "PersonalInfoFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        _binding = FragmentPersonalInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val rootLayout = binding.root.findViewById<LinearLayoutCompat>(R.id.rootLayout)

        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)

        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        pd = TransparentProgressDialog(requireActivity(), R.drawable.progress)

        lifecycleScope.launch {
            binding.tvExper.text = TranslationHelper.translateText(
                "Work Experience",
                requireContext().languageName
            )
            binding.tvWorkExp.hint = TranslationHelper.translateText(
                "Work Experience",
                requireContext().languageName
            )
            binding.tvGender.hint = TranslationHelper.translateText(
                "Gender",
                requireContext().languageName
            )

            binding.tvEducation.hint = TranslationHelper.translateText(
                "Qualification",
                requireContext().languageName
            )

            binding.tvAadarName.hint = TranslationHelper.translateText(
                "Aadhar Name",
                requireContext().languageName
            )
            binding.tvAaarNumber.hint = TranslationHelper.translateText(
                "Aadhar Number",
                requireContext().languageName
            )


        }

        getProfileData(requireContext().userid)

        binding.cGJobCat.visibility = View.VISIBLE
        binding.llaadhar.visibility = View.VISIBLE

        checkPermissions()


        binding.ivAadharPdf.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(aadharImgUrl), "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }



        observeProfileEditResponse() // Set observer only once


        binding.tvEdit.setOnClickListener {
            showEditDialog()
        }


        return root
    }

    @SuppressLint("MissingInflatedId")
    private fun showEditDialog() {
        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)

        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.edit_profile_personal_info, null)
        bottomSheetDialog?.setContentView(dialogView)


        // Expand full height
        dialogView.viewTreeObserver.addOnGlobalLayoutListener {
            val bottomSheet = bottomSheetDialog?.delegate?.findViewById<FrameLayout>(
                com.google.android.material.R.id.design_bottom_sheet
            )
            bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        val btnSubmit = dialogView.findViewById<TextView>(R.id.btnSubmit)
        val btnCancel = dialogView.findViewById<TextView>(R.id.btnCancel)

        scrollView = dialogView.findViewById<NestedScrollView>(R.id.scrollView)
        etEmail = dialogView.findViewById<EditText>(R.id.etEmail)
        etAddress = dialogView.findViewById<EditText>(R.id.etAddress)
        spinnerForState = dialogView.findViewById<Spinner>(R.id.spinnerForState)
        spinnerForDitrict = dialogView.findViewById<Spinner>(R.id.spinnerForDitrict)
        spinnerForGender = dialogView.findViewById<Spinner>(R.id.spinnerForGender)
        spinnerForEducation = dialogView.findViewById<Spinner>(R.id.spinnerForEducation)
        etExperiance = dialogView.findViewById<EditText>(R.id.etExperiance)
        etSkill = dialogView.findViewById<EditText>(R.id.etSkill)
        etPincode = dialogView.findViewById<EditText>(R.id.etPincode)
        spinnerMainCat = dialogView.findViewById<Spinner>(R.id.spinnerForMain_cat)
        tvJobCat = dialogView.findViewById<TextView>(R.id.tvJobCat)
        tvsubCategory = dialogView.findViewById<TextView>(R.id.tvsubCategory)
        tvFileName = dialogView.findViewById<TextView>(R.id.tvFileName)
        llText = dialogView.findViewById<LinearLayoutCompat>(R.id.llText)
        btnUpload = dialogView.findViewById<LinearLayoutCompat>(R.id.btnUpload)
        cGSubCat = dialogView.findViewById<ChipGroup>(R.id.cGSubCat)
        cGJobCat = dialogView.findViewById<ChipGroup>(R.id.cGJobCat)
        iv_aadharImage_d = dialogView.findViewById<ImageView>(R.id.iv_aadharImage_d)
        iv_aadharPdf = dialogView.findViewById<AppCompatImageView>(R.id.iv_aadharPdf)
        tvAaName = dialogView.findViewById<TextView>(R.id.tvAaName)
        tvAaNumber = dialogView.findViewById<TextView>(R.id.tvAaNumber)
        lladhrName = dialogView.findViewById<LinearLayout>(R.id.lladhrName)
        lladhrNumber = dialogView.findViewById<LinearLayout>(R.id.lladhrNumber)
        btnUpload = dialogView.findViewById<LinearLayoutCompat>(R.id.btnUpload)
       val tvaadharName = dialogView.findViewById<AppCompatTextView>(R.id.tvaadharName)
       val rlaadhar = dialogView.findViewById<RelativeLayout>(R.id.rlaadhar)

        //tvaadharName.visibility = View.GONE
        //rlaadhar.visibility = View.GONE

        tvaadharName.visibility = View.VISIBLE
        rlaadhar.visibility = View.VISIBLE
        lladhrName.visibility = View.GONE
        lladhrNumber.visibility = View.GONE

        getProfileDataDialog(requireContext().userid)

        btnUpload.setOnClickListener {
            currentImageView = iv_aadharImage_d
            showImagePickerDialog()

        }

        tvJobCat.setOnClickListener {
            if (!mainCatId.isNullOrEmpty()) { // Ensures the list is not null and not empty
                viewModel.getProfileCategory(mainCatId)
                observeCategory()
            } else {
                lifecycleScope.launch {
                    val msg = TranslationHelper.translateText(
                        "Please Select Main Category",
                        requireContext().languageName
                    )
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }

        tvsubCategory.setOnClickListener {
            if (!selectedCatIds.isNullOrEmpty()) {
                commensviewModel.fetchsubcategories(selectedCatIds)
                observeSubCategory()
            } else {
                lifecycleScope.launch {
                    val msg = TranslationHelper.translateText(
                        "Please Select Category",
                        requireContext().languageName
                    )
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnSubmit.setOnClickListener {
            lifecycleScope.launch {
                if (validateInputs()) {
                    profileEdit()
                    getProfileData(requireContext().userid)
                    bottomSheetDialog?.dismiss()
                }
            }
        }


      /*  btnSubmit.setOnClickListener {

            if (validateInputs()) {
                profileEdit()
                getProfileData(requireContext().userid)
                bottomSheetDialog?.dismiss()
            }
        }*/

        btnCancel.setOnClickListener {
            bottomSheetDialog?.dismiss()
        }

        bottomSheetDialog?.show()
    }

    private fun getProfileDataDialog(userid: String) {
        try {
            if (requireContext().isOnline) {
                viewModel.getProfileResult.observe(requireActivity()) { response ->

                    when (response) {
                        is BaseResponse.Loading -> {
                            pd?.show()
                        }

                        is BaseResponse.Success -> {
                            pd?.dismiss()

                            api_aadharImg = response.data!!.data[0].aadhar_image
                            Log.e(TAG, "API Aadhar Image URL: $api_aadharImg")

                            aadharImgUrl = response.data!!.data[0].aadhar_image

                            if (!aadharImgUrl.isNullOrEmpty()) {
                                if (aadharImgUrl!!.endsWith(
                                        ".png",
                                        true
                                    ) || aadharImgUrl!!.endsWith(".jpg", true)
                                ) {
                                    iv_aadharImage_d.visibility = View.VISIBLE
                                    llText.visibility = View.GONE
                                    Glide.with(binding.ivAadharImage)
                                        .load(aadharImgUrl)
                                        //.placeholder(R.drawable.no_data_found)
                                        .into(iv_aadharImage_d)
                                } else if (aadharImgUrl!!.endsWith(".pdf", true)) {
                                    iv_aadharImage_d.visibility = View.GONE
                                    llText.visibility = View.VISIBLE
                                    tvFileName.text = aadharImgUrl

                                }
                            }

                            etEmail.setText(response.data!!.data[0].email?:"")
                            etAddress.setText(response.data!!.data[0].address?:"")
                            etSkill.setText(response.data!!.data[0].skill?:"")
                            etPincode.setText(response.data!!.data[0].pincode?:"")
                            etExperiance.setText(response.data!!.data[0].experience?:"")

                            /*  lifecycleScope.launch {
                                  etAddress.setText(  TranslationHelper.translateText(
                                      response.data!!.data[0].address,
                                      requireContext().languageName
                                  ))

                                  etSkill.setText(  TranslationHelper.translateText(
                                      response.data!!.data[0].skill,
                                      requireContext().languageName
                                  ))

                                  etPincode.setText(  TranslationHelper.translateText(
                                      response.data!!.data[0].pincode,
                                      requireContext().languageName
                                  ))

                              }*/



                            roleId = response.data!!.data[0].role_id
                            stateId = response.data!!.data[0].state_id
                            distId = response.data!!.data[0].district_id
                            genderId = response.data!!.data[0].gender_id
                            educationId = response.data!!.data[0].education_id
                            stateName = response.data!!.data[0].state_name?:""
                            distName = response.data!!.data[0].district_name?:""
                            genderName = response.data!!.data[0].gender?:""
                            educationName = response.data!!.data[0].education?:""
                            mainCatName = response.data!!.data[0].main_category_name?:""
                            mainCatId = response.data!!.data[0].main_category_id
                            getMobileNo = response.data!!.data[0].mobile_no

                            previousMainCatId = mainCatId

                            // selectedCatIds = mutableListOf(response.data!!.data[0].categories[0].id)
                            selectedCatIds = response.data!!.data[0].categories
                                .map { it.id.toInt() }.toMutableList()
                            selectedCatName = response.data!!.data[0].categories
                                .joinToString(", ") { it.name?:"" }
                            selectedSubCatIds =
                                response.data!!.data[0].sub_categories
                                    .map { it.id.toInt() }.toMutableList()

                            selectedSubCatName =
                                response.data!!.data[0].sub_categories
                                    .joinToString(", ") { it.name?:"" }


                            cGJobCat.removeAllViews() // Clear previous chips if any
                            response.data!!.data[0].categories.forEach { Category ->
                                val chip = Chip(context).apply {
                                    // text = Category.name
                                    lifecycleScope.launch {
                                        text = TranslationHelper.translateText(
                                            Category.name?:"",
                                            requireContext().languageName
                                        )
                                    }
                                }
                                cGJobCat.addView(chip)

                            }

                            cGSubCat.removeAllViews() // Clear previous chips if any
                            response.data!!.data[0].sub_categories.forEach { SubCategory ->
                                val chip = Chip(context).apply {
                                    // text = SubCategory.name
                                    lifecycleScope.launch {
                                        text = TranslationHelper.translateText(
                                            SubCategory.name?:"",
                                            requireContext().languageName
                                        )
                                    }
                                }
                                cGSubCat.addView(chip)

                            }


                            getGenderList()
                            getEducationList()
                            getStateList()
                            getMainCategory()


                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss()
                            Toast.makeText(context, response.msg?:"", Toast.LENGTH_SHORT).show()
                        }


                    }
                }
                viewModel.getProfile(user_id = userid)
            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Log.e("ProfileErrorEdit", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(
                context,
                "An error occurred: ${e.localizedMessage ?: "Unknown error"}",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }


    private fun observeProfileEditResponse() {
        viewModel.editProfileResult.observe(viewLifecycleOwner) { response ->
            pd?.show() // Show loading indicator
            when (response) {
                is BaseResponse.Loading -> {
                    pd?.show()
                }

                is BaseResponse.Success -> {
                    pd?.dismiss()
                    response.data?.let { data ->
                        if (response.data.code == "200") {
                            Toast.makeText(context, " ${data.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                is BaseResponse.Error -> {
                    pd?.dismiss()
                    Log.e(TAG, "Error: ${response.msg}")
                    Toast.makeText(context, "Error: ${response.msg}", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    pd?.dismiss()
                    Log.e(TAG, "Unexpected response: $response")
                }
            }
        }
    }


    /*private fun validateInputs(): Boolean {


            val spinnerState = spinnerForState.selectedItemPosition
            val spinnerDistrict = spinnerForDitrict.selectedItemPosition
            val spinnerGender = spinnerForGender.selectedItemPosition
            val spinnerEducation = spinnerForEducation.selectedItemPosition

            val Email = etEmail.text.toString().trim()
            val Address = etAddress.text.toString().trim()
            val pincode = etPincode.text.toString().trim()
            val skill = etSkill.text.toString().trim()
            val experience = etExperiance.text.toString().trim()

            val mobileNumberPattern = "^[6-9][0-9]{9}$"
            val emailPattern = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,3}$"
            val usernamePattern = "^[a-zA-Z\\s]+$"


            if (!Email.matches(emailPattern.toRegex())) {
                // binding.etEmail.setError("Invalid Email Address.")
                lifecycleScope.launch {
                    Toast.makeText(
                        context,
                        TranslationHelper.translateText(
                            "Please Enter valid Email",
                            requireContext().languageName
                        ),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

            } else {
                etEmail.setError(null) // Clear error
            }

            if (TextUtils.isEmpty(Address)) {
                //  binding.etAdd.setError(getString(R.string.enter_add))
                Toast.makeText(context, getString(R.string.enter_add), Toast.LENGTH_SHORT)
                    .show()
                return false
            } else {
                etAddress.setError(null) // Clear error
            }

            if (TextUtils.isEmpty(pincode)) {

                Toast.makeText(context, getString(R.string.enter_pincode), Toast.LENGTH_SHORT)
                    .show()
                return false
            } else {
                etPincode.setError(null) // Clear error
            }

            if (spinnerState == 0) {
                // If no item is selected, show error and return false
                Toast.makeText(context, getString(R.string.select_state), Toast.LENGTH_SHORT).show()
                return false
            }

            if (spinnerDistrict == 0) {
                // If no item is selected, show error and return false
                Toast.makeText(context, getString(R.string.select_dist), Toast.LENGTH_SHORT).show()
                return false
            }

            if (spinnerGender == 0) {
                lifecycleScope.launch {
                    Toast.makeText(
                        context,
                        TranslationHelper.translateText(
                            "Please Select Gender",
                            requireContext().languageName
                        ),
                        Toast.LENGTH_SHORT
                    ).show()

                }
                return false
            }
            if (spinnerEducation == 0) {
                lifecycleScope.launch {
                    Toast.makeText(
                        context,
                        TranslationHelper.translateText(
                            "Please Select Education",
                            requireContext().languageName
                        ),
                        Toast.LENGTH_SHORT
                    ).show()

                }
                return false
            }

            if (mainCatId == "0") {

                Toast.makeText(context, getString(R.string.select_main_cat), Toast.LENGTH_SHORT)
                    .show()
                return false
            }

            if (selectedCatIds.size == 0) {
                Toast.makeText(context, getString(R.string.select_cat), Toast.LENGTH_SHORT).show()
                return false
            }

            *//* if (selectedSubCatIds.size == 0) {
             Toast.makeText(context, "Please Select Sub Category", Toast.LENGTH_SHORT)
                 .show()
             return false
         }*//*



        if (TextUtils.isEmpty(skill)) {
            //  binding.etSkill.setError(getString(R.string.enter_skill))
            Toast.makeText(context, getString(R.string.enter_skill), Toast.LENGTH_SHORT).show()
            return false
        } else {
            etSkill.setError(null) // Clear error
        }

        if (TextUtils.isEmpty(experience)) {
            lifecycleScope.launch {
                Toast.makeText(
                    context,
                    TranslationHelper.translateText(
                        "Please Enter Experience",
                        requireContext().languageName
                    ),
                    Toast.LENGTH_SHORT
                ).show()

            }
            return false
        } else {
            etExperiance.setError(null) // Clear error
        }


        *//* if (role.isNullOrEmpty()) {
             Toast.makeText(context, getString(R.string.select_role), Toast.LENGTH_SHORT).show()
             return false
         }

         if (aadharBase64String == null) {
             Toast.makeText(context, getString(R.string.select_aadhar_img), Toast.LENGTH_SHORT).show()
             return false
         }*//*


        return true
    }*/

    private suspend fun validateInputs(): Boolean {
        val email = etEmail.text.toString().trim()
        val address = etAddress.text.toString().trim()
        val skill = etSkill.text.toString().trim()
        val experience = etExperiance.text.toString().trim()

        val emailPattern = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"

        val spinnerState = spinnerForState.selectedItemPosition
        val spinnerDistrict = spinnerForDitrict.selectedItemPosition
        val spinnerGender = spinnerForGender.selectedItemPosition
        val spinnerEducation = spinnerForEducation.selectedItemPosition
        val spinnerMainCategory = spinnerMainCat.selectedItemPosition


        var isValid = true
        var focusView: View? = null

        val emailError = TranslationHelper.translateText("Please enter valid email", requireContext().languageName)
        val addressError = TranslationHelper.translateText("Please enter address", requireContext().languageName)
        val skillError = TranslationHelper.translateText("Please enter skill", requireContext().languageName)
        val expError = TranslationHelper.translateText("Please enter experience", requireContext().languageName)
        val mainCatError = TranslationHelper.translateText("Please select Main Category", requireContext().languageName)

        if (email.isNotEmpty() && !email.matches(emailPattern.toRegex())) {
            etEmail.error = emailError
            if (focusView == null) focusView = etEmail
            isValid = false
        } else {
            etEmail.error = null
        }

        if (address.isEmpty()) {
            etAddress.error = addressError
            if (focusView == null) focusView = etAddress
            isValid = false
        } else {
            etAddress.error = null
        }

        if (spinnerState == 0) {
            if (focusView == null) focusView = spinnerForState
            isValid = false
        }
        if (spinnerDistrict == 0) {
            if (focusView == null) focusView = spinnerForDitrict
            isValid = false
        }
        if (spinnerGender == 0) {
            if (focusView == null) focusView = spinnerForGender
            isValid = false
        }
        if (spinnerEducation == 0) {
            if (focusView == null) focusView = spinnerForEducation
            isValid = false
        }

        if (skill.isEmpty()) {
            etSkill.error = skillError
            if (focusView == null) focusView = etSkill
            isValid = false
        } else {
            etSkill.error = null
        }

        if (experience.isEmpty()) {
            etExperiance.error = expError
            if (focusView == null) focusView = etExperiance
            isValid = false
        } else {
            etExperiance.error = null
        }

        if (spinnerMainCategory == 0) {
            Toast.makeText(context, mainCatError, Toast.LENGTH_SHORT).show()
            if (focusView == null) focusView = spinnerMainCat
            isValid = false
        }

        if (mainCatId == "0") {
            Toast.makeText(context, mainCatError, Toast.LENGTH_SHORT).show()
            if (focusView == null) focusView = spinnerMainCat
            isValid = false
        }

        focusView?.let {
            scrollView.post {
                scrollView.smoothScrollTo(0, it.top)
                it.requestFocus()
            }
        }

        return isValid
    }



    private fun profileEdit() {
        try {
            if (requireContext().isOnline) {
                val mobileNo = getMobileNo
                val email = etEmail.text.toString().trim()
                val address = etAddress.text.toString().trim()
                val state = stateId
                val district = distId
                val gender = genderId
                val education = educationId
                val pincode = etPincode.text.toString().trim()
                val mainCatId = mainCatId
                val selectedCatIds = selectedCatIds
                val selectedSubCatIds = selectedSubCatIds
                val skill = etSkill.text.toString().trim()
                val experiance = etExperiance.text.toString().trim()
                val aadhar_name = tvAaName.text.toString().trim()
                val aadhar_no = tvAaNumber.text.toString().trim()
                val role = roleId

                val aadharsImg: String? =
                    if (!aadharBase64String.isNullOrEmpty()) aadharBase64String else ""

                viewModel.editProfile(
                    user_id = requireContext().userid,
                    mobile_no = mobileNo!!,
                    email = email,
                    address = address,
                    state = state,
                    district = district,
                    gender_id = gender,
                    education_id = education,
                    pincode = pincode,
                    main_category_id = mainCatId,
                    category_ids = selectedCatIds,
                    sub_category_ids = selectedSubCatIds,
                    skill = skill,
                    experience = experiance,
                    role = role!!,
                    aadhar_image = aadharsImg!!,
                    aadhar_name = aadhar_name,
                    aadhar_no = aadhar_no
                )

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error occurred: ${e.localizedMessage}")
        }
    }


    private fun showImagePickerDialog() {
        val options = arrayOf("Camera", "Gallery")
      //  val options = arrayOf("Camera", "Gallery", "Select PDF")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose an option")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    openCamera() // Your method to launch the camera intent
                } else {
                    checkAndRequestPermissions()
                }// Camera
                1 -> openGallery() // Gallery
               // 2 -> openPdfPicker() // PDF
            }
        }
        builder.show()
    }

    /*  private fun openCamera() {
          ImagePicker.with(requireActivity())
              .cameraOnly()
              .crop() // triggers crop
              .compress(1024)
              .createIntent { intent ->
                  imagePickerLauncher.launch(intent)
              }
      }

      private fun openGallery() {
          ImagePicker.with(requireActivity())
              .galleryOnly()
              .crop() // or your preferred aspect ratio
              .compress(1024) // 1MB max size
              .createIntent { intent ->
                  imagePickerLauncher.launch(intent)
              }
      }*/
    private fun openCamera() {
        ImagePicker.with(this)
            .cameraOnly()
            .compress(1024)
            .createIntent { intent -> imagePickerLauncher.launch(intent) }
    }

    private fun openGallery() {
        ImagePicker.with(this)
            .galleryOnly()
            .compress(1024)
            .createIntent { intent -> imagePickerLauncher.launch(intent) }
    }


    private fun openPdfPicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        pdfPickerLauncher.launch(intent)
    }

    /* private val imagePickerLauncher = registerForActivityResult(
         ActivityResultContracts.StartActivityForResult()
     ) { result ->
         if (result.resultCode == Activity.RESULT_OK) {
             val data: Intent? = result.data
             selectedFileUri = data?.data

             if (selectedFileUri != null) {
                 binding.llText.visibility = View.GONE
                 binding.ivAadharImage.visibility = View.VISIBLE
                 binding.ivAadharPdf.visibility = View.GONE

                 Glide.with(this).load(selectedFileUri).into(binding.ivAadharImage)
                 aadharBase64String = uriToBase64(selectedFileUri!!) // Convert to Base64
                 binding.tvFileName.text = getFileName(selectedFileUri!!)
             }
         }
     }*/

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            selectedFileUri = data?.data

            if (selectedFileUri != null) {
                startUCrop(selectedFileUri!!)
            }
        }
    }

    private val uCropLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            if (resultUri != null) {
                llText.visibility = View.GONE
                iv_aadharImage_d.visibility = View.VISIBLE
                iv_aadharPdf.visibility = View.GONE

                val bitmap = getBitmapFromUri(resultUri)
                if (bitmap != null) {
                    runOCR(bitmap, resultUri)
                } else {
                    Log.e("OCR", "Failed to convert URI to Bitmap")
                    Toast.makeText(requireContext(), "Failed to read image", Toast.LENGTH_SHORT).show()
                }

                // Convert URI to Bitmap
              /*  val bitmap = getBitmapFromUri(resultUri!!)
                Log.e("OCR", "Failed to $bitmap")
                if (bitmap != null) {
                    runOCR(bitmap) // Pass Bitmap for OCR
                    aadharBase64String = bitmapToBase64(bitmap) // If you still need Base64
                    Glide.with(this).load(resultUri).into(iv_aadharImage_d)

                    aadharBase64String = uriToBase64(resultUri)
                    tvFileName.text = getFileName(resultUri)
                    selectedFileUri = resultUri

                } else {
                    Log.e("OCR", "Failed to convert URI to Bitmap")
                }*/
            }
        }
    }
    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }


    private fun runOCR(bitmap: Bitmap, uri: Uri) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val fullText = visionText.text
                Log.e("OCR", "Extracted Text: $fullText")


                val aadhaarRegex = Regex("\\b\\d{4}\\s\\d{4}\\s\\d{4}\\b")
                val nameRegex = Regex("[A-Z][a-zA-Z]+(\\s[A-Z][a-zA-Z]+)+") // Basic name pattern

                val aadhaarNumber = aadhaarRegex.find(fullText)?.value
                val name = nameRegex.find(fullText)?.value

                if (!aadhaarNumber.isNullOrBlank() && !name.isNullOrBlank()) {
                    Log.e("OCR", "Valid Aadhaar: $aadhaarNumber, Name: $name")

                    lladhrName.visibility = View.VISIBLE
                    lladhrNumber.visibility = View.VISIBLE

                    tvAaName.text = name
                    tvAaNumber.text = aadhaarNumber

                    aadharBase64String = bitmapToBase64(bitmap)

                    Glide.with(requireContext()).load(uri).into(iv_aadharImage_d)
                    tvFileName.text = "Aadhaar: $aadhaarNumber\nName: $name"
                    selectedFileUri = uri
                } else {
                    Log.e("OCR", "Invalid OCR result. Aadhaar or Name not found.")
                    Toast.makeText(requireContext(), "Please upload a clear Aadhaar image showing name and number.", Toast.LENGTH_LONG).show()

                    aadharBase64String = "" // Reset invalid
                    tvFileName.text = "Invalid Aadhaar file"
                }
            }
            .addOnFailureListener {
                Log.e("OCR", "OCR Failed: ${it.message}")
                Toast.makeText(requireContext(), "OCR failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }


   /* private fun runOCR(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val fullText = visionText.text
                //    textViewResult.text = fullText
                Log.e("text","Name : $fullText")

                // Aadhaar number format: 1234 5678 9012
                val aadhaarRegex = Regex("\\b\\d{4}\\s\\d{4}\\s\\d{4}\\b")
                val nameRegex = Regex("[A-Z][a-z]+(\\s[A-Z][a-z]+)+") // basic name pattern

                val aadhaarNumber = aadhaarRegex.find(fullText)?.value
                val name = nameRegex.find(fullText)?.value
                Log.e("text","Name: ${name ?: "Not Found"}\nAadhaar: ${aadhaarNumber ?: "Not Found"}")
                //textViewResult.text = "Name: ${name ?: "Not Found"}\nAadhaar: ${aadhaarNumber ?: "Not Found"}"
            }
            .addOnFailureListener {
                // textViewResult.text = "OCR Failed: ${it.message}"
            }
    }*/


    /* private fun startUCrop(sourceUri: Uri) {
         val destinationUri = Uri.fromFile(
             File(requireActivity().cacheDir, "cropped_image_${System.currentTimeMillis()}.jpg")
         )

         val options = UCrop.Options().apply {
             setFreeStyleCropEnabled(true)
             setCompressionQuality(90)
             setHideBottomControls(false)
             setToolbarTitle("Crop Image")
             setToolbarColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
             setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.purple_700))
         }

         val uCrop = UCrop.of(sourceUri, destinationUri)
             .withOptions(options)
             .withAspectRatio(0f, 0f)

         uCropLauncher.launch(uCrop.getIntent(requireContext()))
     }*/
    private fun startUCrop(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(
            File(
                requireContext().cacheDir,
                "cropped_image_${System.currentTimeMillis()}.jpg"
            )
        )

        val options = UCrop.Options().apply {
            setToolbarColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.white))
            setActiveControlsWidgetColor(ContextCompat.getColor(requireContext(), R.color.orange))
            setCompressionQuality(90)
            setFreeStyleCropEnabled(true) // If you want freestyle cropping
        }

        /* UCrop.of(sourceUri, destinationUri)
             .withOptions(options)
             .withAspectRatio(1f, 1f) // Optional
             .withMaxResultSize(1000, 1000) // Optional
             .start(requireContext(), this) // Use `this` if inside Fragment*/
        val uCrop = UCrop.of(sourceUri, destinationUri)
            .withOptions(options)
            .withAspectRatio(0f, 0f)

        uCropLauncher.launch(uCrop.getIntent(requireContext()))
    }


    private val pdfPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            val data: Intent? = result.data
            selectedFileUri = data?.data

            if (selectedFileUri != null) {
                llText.visibility = View.VISIBLE
                iv_aadharImage_d.visibility = View.GONE
                iv_aadharPdf.visibility = View.VISIBLE

                iv_aadharPdf.setOnClickListener {
                    openPdf(selectedFileUri)
                }
                aadharBase64String = uriToBase64(selectedFileUri!!) // Convert to Base64
                tvFileName.text = getFileName(selectedFileUri!!)
            }
        }
    }


    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
            }
        }
    }


    private fun uriToBase64(uri: Uri): String {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun openPdf(pdfUri: Uri?) {
        if (pdfUri == null) {
            Toast.makeText(context, "Invalid PDF file", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(context, "No PDF viewer found", Toast.LENGTH_SHORT).show()
        }
    }

    /*   private val pickFileLauncher =
           registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
               uri?.let {
                   selectedFileUri = it
                   binding.tvFileName.text = getFileName(it)
               }
           }*/

    private fun getFileName(uri: Uri): String {
        var fileName = "Unknown File"

        // Try querying the content resolver
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                fileName = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            }
        }

        // If cursor doesn't return a file name, extract from URI path
        if (fileName == "Unknown File") {
            fileName = uri.path?.substringAfterLast('/') ?: "Unknown File"
        }

        return fileName
    }


    /* private fun uriToFile(uri: Uri): File {
         val fileName = getFileName(uri) // Get the actual file name
         val file = File(requireContext().cacheDir, fileName) // Use the actual file name

         requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
             file.outputStream().use { outputStream ->
                 inputStream.copyTo(outputStream)
             }
         }

         return file
     }


     private fun encodeFileToBase64(file: File): String {
         return try {
             val bytes = FileInputStream(file).readBytes()
             Base64.encodeToString(bytes, Base64.NO_WRAP)
         } catch (e: IOException) {
             e.printStackTrace()
             ""
         }
     }

     private fun getFileType(fileName: String): String {
         return when {
             fileName.endsWith(".png", true) -> "image/png"
             fileName.endsWith(".jpg", true) || fileName.endsWith(".jpeg", true) -> "image/jpeg"
             fileName.endsWith(".pdf", true) -> "application/pdf"
             else -> "application/octet-stream"
         }
     }*/

    private fun getProfileData(userid: String) {
        try {
            if (requireContext().isOnline) {
                viewModel.getProfileResult.observe(requireActivity()) { response ->

                    when (response) {
                        is BaseResponse.Loading -> {
                            pd?.show()
                        }

                        is BaseResponse.Success -> {
                            pd?.dismiss()

                            api_aadharImg = response.data!!.data[0].aadhar_image
                            Log.e(TAG, "API Aadhar Image URL: $api_aadharImg")

                            aadharImgUrl = response.data!!.data[0].aadhar_image

                            if (!aadharImgUrl.isNullOrEmpty()) {
                                if (aadharImgUrl!!.endsWith(
                                        ".png",
                                        true
                                    ) || aadharImgUrl!!.endsWith(".jpg", true)
                                ) {
                                    binding.ivAadharImage.visibility = View.VISIBLE
                                    binding.llText.visibility = View.GONE
                                    Glide.with(binding.ivAadharImage)
                                        .load(aadharImgUrl)
                                        //.placeholder(R.drawable.no_data_found)
                                        .into(binding.ivAadharImage)
                                } else if (aadharImgUrl!!.endsWith(".pdf", true)) {
                                    binding.ivAadharImage.visibility = View.GONE
                                    binding.llText.visibility = View.VISIBLE
                                    binding.tvFileName.text = aadharImgUrl

                                }
                            }

                            lifecycleScope.launch {


                                binding.tvMobile.text = TranslationHelper.translateText(
                                    response.data!!.data[0].mobile_no?:"",
                                    requireContext().languageName
                                )
                                binding.etEmail.text = response.data!!.data[0].email?:""

                                binding.etAdd.text =
                                    TranslationHelper.translateText(
                                        response.data!!.data[0].address?:"",
                                        requireContext().languageName
                                    )

                                binding.etSkill.text =
                                    TranslationHelper.translateText(
                                        response.data!!.data[0].skill?:"",
                                        requireContext().languageName
                                    )

                                binding.etPincode.text =
                                    TranslationHelper.translateText(
                                        response.data!!.data[0].pincode?:"",
                                        requireContext().languageName
                                    )
                                binding.tvGender.text =
                                    TranslationHelper.translateText(
                                        response.data!!.data[0].gender?:"",
                                        requireContext().languageName
                                    )
                                binding.tvEducation.text =
                                    TranslationHelper.translateText(
                                        response.data!!.data[0].education?:"",
                                        requireContext().languageName
                                    )
                                binding.tvWorkExp.text =
                                    TranslationHelper.translateText(
                                        response.data!!.data[0].experience?:"",
                                        requireContext().languageName
                                    )
                                binding.tvAadarName.text =
                                    TranslationHelper.translateText(
                                        response.data!!.data[0].aadhar_name?:"",
                                        requireContext().languageName
                                    )
                                binding.tvAaarNumber.text =
                                    TranslationHelper.translateText(
                                        response.data!!.data[0].aadhar_no?:"",
                                        requireContext().languageName
                                    )


                            }

                            roleId = response.data!!.data[0].role_id

                            /* binding.tvDesignation.visibility = View.GONE
                             binding.llDesig.visibility = View.GONE
                             binding.llCompName.visibility = View.GONE
                             binding.tvCompanyName.visibility = View.GONE

                             if (!response.data!!.data[0].designation.isNullOrEmpty()) {
                                 binding.tvDesignation.visibility = View.VISIBLE
                                 binding.llDesig.visibility = View.VISIBLE
                                 //  binding.tvDesignation.setText(response.data!!.data[0].designation)
                                 lifecycleScope.launch {
                                     binding.tvDesignation.text = TranslationHelper.translateText(
                                         response.data!!.data[0].designation,
                                         requireContext().languageName
                                     )
                                 }
                             } else {
                                 binding.llCompName.visibility = View.VISIBLE
                                 binding.tvCompanyName.visibility = View.VISIBLE
                                 // binding.tvCompanyName.setText(response.data!!.data[0].company_name)
                                 lifecycleScope.launch {
                                     binding.tvCompanyName.text = TranslationHelper.translateText(
                                         response.data!!.data[0].company_name,
                                         requireContext().languageName
                                     )
                                 }
                             }*/

                            stateId = response.data!!.data[0].state_id
                            distId = response.data!!.data[0].district_id
                            stateName = response.data!!.data[0].state_name?:""
                            distName = response.data!!.data[0].district_name?:""
                            mainCatName = response.data!!.data[0].main_category_name?:""
                            mainCatId = response.data!!.data[0].main_category_id

                            lifecycleScope.launch {

                                binding.tvState.text = TranslationHelper.translateText(
                                    stateName?:"",
                                    requireContext().languageName
                                )
                                binding.tvDistrict.text = TranslationHelper.translateText(
                                    distName?:"",
                                    requireContext().languageName
                                )
                                binding.tvMainCat.text = TranslationHelper.translateText(
                                    mainCatName?:"",
                                    requireContext().languageName
                                )
                            }



                            previousMainCatId = mainCatId

                            // selectedCatIds = mutableListOf(response.data!!.data[0].categories[0].id)
                            selectedCatIds =
                                response.data!!.data[0].categories
                                    .map { it.id.toInt() }.toMutableList()
                            selectedCatName = response.data!!.data[0].categories
                                .joinToString(", ") { it.name?:"" }
                            selectedSubCatIds =
                                response.data!!.data[0].sub_categories
                                    .map { it.id.toInt() }.toMutableList()

                            selectedSubCatName =
                                response.data!!.data[0].sub_categories
                                    .joinToString(", ") { it.name?:"" }

                            //--------Job cat

                            /*if(!response.data!!.data[0].categories.isNullOrEmpty()){
                                tvJobCat.text = ""
                            }*/

                            binding.cGJobCat.removeAllViews() // Clear previous chips if any
                            response.data!!.data[0].categories.forEach { Category ->
                                val chip = Chip(context).apply {
                                    // text = Category.name
                                    lifecycleScope.launch {
                                        text = TranslationHelper.translateText(
                                            Category.name?:"",
                                            requireContext().languageName
                                        )
                                    }
                                }
                                binding.cGJobCat.addView(chip)

                            }

                            binding.cGSubCat.removeAllViews() // Clear previous chips if any
                            response.data!!.data[0].sub_categories.forEach { SubCategory ->
                                val chip = Chip(context).apply {
                                    // text = SubCategory.name
                                    lifecycleScope.launch {
                                        text = TranslationHelper.translateText(
                                            SubCategory.name?:"",
                                            requireContext().languageName
                                        )
                                    }
                                }
                                binding.cGSubCat.addView(chip)

                            }


                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss()
                            Toast.makeText(context, response.msg?:"", Toast.LENGTH_SHORT).show()
                        }


                    }
                }
                viewModel.getProfile(user_id = userid)
            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Log.e("getProfileData", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(
                context,
                "An error occurred: ${e.localizedMessage ?: "Unknown error"}",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun getEducationList() {
        try {
            if (requireActivity().isOnline) {
                commensviewModel.educationResult.observe(requireActivity()) { response ->
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
                                                requireContext().languageName
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
                                                                requireContext().languageName
                                                            )

                                                        if (item.education == educationName) {
                                                            selectedIndex = index + 1
                                                        }
                                                        translatedName
                                                    }
                                                }

                                            arrayList.addAll(translations.awaitAll()) // Wait for all translations

                                          //  Log.d("Main Cat", "ArrayList: $arrayList")

                                            val arrayAdapter = ArrayAdapter(
                                                requireActivity(),
                                                android.R.layout.simple_spinner_item,
                                                arrayList
                                            )
                                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                            spinnerForEducation.adapter = arrayAdapter



                                            // Set the spinner to the desired state by name
                                            spinnerForEducation.setSelection(selectedIndex)

                                            spinnerForEducation.onItemSelectedListener =
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
                                                            educationId = educationList[position - 1].id
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
                            Log.e("spinnerForEducation", "gender Error: ${response.msg!!}")
                        }

                        else -> {
                            Log.e("spinnerForEducation", "Unhandled case")
                        }
                    }
                }

                commensviewModel.fetchEducation()

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("pers info education", "Error occurred: ${e.localizedMessage}")
        }
    }
    private fun getGenderList() {
        try {
            if (requireActivity().isOnline) {
                commensviewModel.genderResult.observe(requireActivity()) { response ->
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
                                                requireContext().languageName
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
                                                        val genderText = item.gender ?: ""
                                                        val translatedName = TranslationHelper.translateText(
                                                            genderText,
                                                            requireContext().languageName
                                                        )

                                                        if (item.gender == genderName?:"") {
                                                            selectedIndex = index + 1
                                                        }
                                                        translatedName
                                                    }
                                                }

                                            arrayList.addAll(translations.awaitAll()) // Wait for all translations



                                            val arrayAdapter = ArrayAdapter(
                                                requireActivity(),
                                                android.R.layout.simple_spinner_item,
                                                arrayList
                                            )
                                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                            spinnerForGender.adapter = arrayAdapter



                                            // Set the spinner to the desired state by name
                                            spinnerForGender.setSelection(selectedIndex)

                                            spinnerForGender.onItemSelectedListener =
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
                            Log.e("spinnerMainCat", "gender Error: ${response.msg!!}")
                        }

                        else -> {
                            Log.e("spinnerMainCat", "Unhandled case")
                        }
                    }
                }

                commensviewModel.fetchGender()

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("pers info gender", "Error occurred: ${e.localizedMessage}")
        }
    }

    private fun getMainCategory() {
        try {
            if (requireActivity().isOnline) {
                commensviewModel.mainCategoryResult.observe(requireActivity()) { response ->
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

                                        mainCatList = response.data


                                        val arrayList = ArrayList<String>()
                                        var selectedIndex = 0

                                        val translatedTitle = async {
                                            TranslationHelper.translateText(
                                                "Select Main Category",
                                                requireContext().languageName
                                            )
                                        }

                                        arrayList.add(
                                            0,
                                            translatedTitle.await()
                                        ) // Add translated title


                                        if (mainCatList != null && mainCatList.isNotEmpty()) {


                                            // Translate district names
                                            val translations =
                                                mainCatList.mapIndexed { index, item ->
                                                    async {
                                                        val translatedName =
                                                            TranslationHelper.translateText(
                                                                item.name,
                                                                requireContext().languageName
                                                            )

                                                        if (item.name == mainCatName) {
                                                            selectedIndex = index + 1
                                                        }
                                                        translatedName
                                                    }
                                                }

                                            arrayList.addAll(translations.awaitAll()) // Wait for all translations


                                            val arrayAdapter = ArrayAdapter(
                                                requireActivity(),
                                                android.R.layout.simple_spinner_item,
                                                arrayList
                                            )
                                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                            spinnerMainCat.adapter = arrayAdapter



                                            spinnerMainCat.setSelection(selectedIndex)

                                            spinnerMainCat.onItemSelectedListener =
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
                                                            val newMainCatId =
                                                                mainCatList[position - 1].id.toString()

                                                            Log.e("mainCatList", "mainCatId: 1 ${mainCatId} $selectedCatIds $selectedSubCatIds")
                                                            Log.e("mainCatList", "newMainCatId: ${newMainCatId}")
                                                            Log.e("mainCatList", "previousMainCatId: ${previousMainCatId}")


                                                            // Only clear selections and views if the mainCatId changes
                                                            if (newMainCatId != previousMainCatId) {
                                                                // Clear category & subcategory selections and views
                                                                selectedCatIds.clear()
                                                                selectedSubCatIds.clear()
                                                                cGJobCat.removeAllViews()
                                                                cGSubCat.removeAllViews()
                                                            }


                                                            // Update the current mainCatId and the previous one
                                                            mainCatId = newMainCatId
                                                            previousMainCatId = mainCatId

                                                            Log.e("mainCatList", "mainCatId: ${mainCatId} $selectedCatIds $selectedSubCatIds")
                                                            Log.e("mainCatList", "newMainCatId: ${newMainCatId}")
                                                            Log.e("mainCatList", "previousMainCatId: ${previousMainCatId}")


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
                            Log.e("spinnerMainCat", "Error: ${response.msg}")
                        }

                        else -> {
                            Log.e("spinnerMainCat", "Unhandled case")
                        }
                    }
                }

                commensviewModel.fetchMainCategories()

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("pers info main cat", "Error occurred: ${e.localizedMessage}")
        }
    }


   private fun observeCategory() {
        if (requireContext().isOnline) {
            viewModel.getProfileCategoryResult.observe(requireActivity()) { response ->
                pd?.show() // Show loading indicator
                when (response) {
                    is BaseResponse.Success -> {
                        pd?.dismiss() // Dismiss loading indicator
                        Log.d(
                            "Job Post",
                            "Response data: ${response.data}"
                        ) // Log the raw response data

                        // Ensure the data is being correctly mapped into the list
                        categoryList = response.data!!.data
                        // Clear subcategory selection when category changes
                        selectedSubCatIds.clear()
                        cGSubCat.removeAllViews()

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
                            chipGroup = cGJobCat
                        )
                    }

                    is BaseResponse.Error -> {
                        pd?.dismiss() // Dismiss loading indicator
                        Toast.makeText(context, response.msg, Toast.LENGTH_SHORT)
                            .show() // Show error message
                    }

                    is BaseResponse.Loading -> {
                        // Show loading indicator if needed
                    }
                }
            }
        } else {
            Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showcategoryListDialog(
        title: String,
        list: List<CategoryData>,
        selectedItems: BooleanArray,
        selectedIds: MutableList<Int>,
        chipGroup: ChipGroup
    ) {
        Log.e(
            "Cat",
            "call showcategoryListDialog"
        )
        // Ensure selectedItems reflects selectedIds
        list.forEachIndexed { index, experience ->
            selectedItems[index] = selectedIds.contains(experience.id)
        }

        val dialog = Builder(requireContext())
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
                            context,
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
                    val chip = Chip(context)
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

    }

   /* private fun observeCategory() {
        if (requireActivity().isOnline) {
            viewModel.getProfileCategoryResult.observe(requireActivity()) { response ->
                pd?.show()
                when (response) {
                    is BaseResponse.Success -> {
                        pd?.dismiss()
                        categoryList = response.data!!.data

                        // Launch coroutine to translate names
                        lifecycleScope.launch {
                            val translatedNames = categoryList.map {
                                TranslationHelper.translateText(it.category_name, requireContext().languageName)
                            }
                            val title = TranslationHelper.translateText("Select Job Role", requireContext().languageName)

                            selectedCatItems = BooleanArray(categoryList.size)
                            showcategoryListDialog(
                                title = title,
                                list = categoryList,
                                translatedNames = translatedNames,
                                selectedItems = selectedCatItems,
                                selectedIds = selectedCatIds,
                                chipGroup = cGJobCat
                            )
                        }
                    }

                    is BaseResponse.Error -> {
                        pd?.dismiss()
                        Toast.makeText(context, response.msg ?: "", Toast.LENGTH_SHORT)
                            .show()
                    }

                    is BaseResponse.Loading -> { *//* optional *//* }
                }
            }
        } else {
            Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
        }
    }
    private fun showcategoryListDialog(
        title: String,
        list: List<CategoryData>,
        translatedNames: List<String>,
        selectedItems: BooleanArray,
        selectedIds: MutableList<Int>,
        chipGroup: ChipGroup
    ) {
        list.forEachIndexed { index, experience ->
            selectedItems[index] = selectedIds.contains(experience.id)
        }

        val dialog = AlertDialog.Builder(requireContext())
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
                        Toast.makeText(context, "${list[which].category_name} is already selected", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    selectedIds.remove(list[which].id)
                }
            }
            .setPositiveButton("OK") { _, _ ->
                chipGroup.removeAllViews()
                selectedIds.forEach { id ->
                    val chip = Chip(context)

                    lifecycleScope.launch {
                        val translatedText = TranslationHelper.translateText(
                            list.find { it.id == id }?.category_name ?: "",
                            requireContext().languageName
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
    }*/

  /*  private fun observeCategory() {
        if (requireContext().isOnline) {
            viewModel.getProfileCategoryResult.observe(requireActivity()) { response ->
                pd?.show() // Show loading indicator
                when (response) {
                    is BaseResponse.Success -> {
                        pd?.dismiss()
                        Log.d("Job Post", "Response data: ${response.data}")

                        // Store original category list
                        categoryList = response.data!!.data
                        selectedSubCatIds.clear()
                        cGSubCat.removeAllViews()

                        // Translate category names before showing dialog
                        lifecycleScope.launch {
                            val translatedCategoryList = categoryList.map {
                                val translatedName = TranslationHelper.translateText(
                                    it.category_name,
                                    requireContext().languageName
                                )
                                it.copy(category_name = translatedName)
                            }

                            val translatedTitle = TranslationHelper.translateText(
                                "Select Job Category",
                                requireContext().languageName
                            )

                            selectedCatItems = BooleanArray(translatedCategoryList.size)
                            showcategoryListDialog(
                                title = translatedTitle,
                                list = translatedCategoryList,
                                selectedItems = selectedCatItems,
                                selectedIds = selectedCatIds,
                                chipGroup = cGJobCat
                            )
                        }
                    }

                    is BaseResponse.Error -> {
                        pd?.dismiss()
                        Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
                    }

                    is BaseResponse.Loading -> {
                        // Show loading indicator if needed
                    }
                }
            }
        } else {
            Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showcategoryListDialog(
        title: String,
        list: List<CategoryData>,
        selectedItems: BooleanArray,
        selectedIds: MutableList<Int>,
        chipGroup: ChipGroup
    ) {
        Log.e("Cat", "call showcategoryListDialog")

        // Reflect preselected items
        list.forEachIndexed { index, category ->
            selectedItems[index] = selectedIds.contains(category.id)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMultiChoiceItems(
                list.map { it.category_name }.toTypedArray(), // Translated names
                selectedItems
            ) { _, which, isChecked ->
                selectedItems[which] = isChecked
                val id = list[which].id
                if (isChecked) {
                    if (!selectedIds.contains(id)) {
                        selectedIds.add(id)
                    } else {
                        Toast.makeText(context, "${list[which].category_name} is already selected", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    selectedIds.remove(id)
                }
            }
            .setPositiveButton("OK") { _, _ ->
                chipGroup.removeAllViews()
                selectedIds.forEach { id ->
                    val chip = Chip(context)
                    chip.text = list.find { it.id == id }?.category_name
                    chip.isCloseIconVisible = true
                    chip.setOnCloseIconClickListener {
                        selectedIds.remove(id)
                        selectedItems[list.indexOfFirst { it.id == id }] = false
                        showcategoryListDialog(title, list, selectedItems, selectedIds, chipGroup)
                    }
                    chipGroup.addView(chip)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }*/


    private fun observeSubCategory() {
        if (requireContext().isOnline) {
            commensviewModel.subcategoryResult.observe(requireActivity()) { response ->
                pd?.show() // Show loading indicator
                when (response) {
                    is BaseResponse.Success -> {
                        pd?.dismiss() // Dismiss loading indicator
                        Log.d("Job Post", "Response data: ${response.data}") // Log response data

                        if (response.data.isNullOrEmpty()) {
                            Log.e("Job Post", "Subcategory list is empty!")
                            Toast.makeText(context, "No subcategories found.", Toast.LENGTH_SHORT)
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
                            chipGroup = cGSubCat
                        )

                    }

                    is BaseResponse.Error -> {
                        pd?.dismiss() // Dismiss loading indicator
                        Toast.makeText(context, response.msg, Toast.LENGTH_SHORT)
                            .show() // Show error message
                    }

                    is BaseResponse.Loading -> {
                        // Show loading indicator if needed
                    }
                }
            }
        } else {
            Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
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

        val dialog = AlertDialog.Builder(requireContext())
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
                            context,
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
                    val chip = Chip(context)

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


    private fun getStateList() {

        commenViewModel.stateresult.observe(requireActivity()) { response ->
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

                                // Translate "Select Main Category"
                                val translatedTitle = async {
                                    TranslationHelper.translateText(
                                        "Select State",
                                        requireContext().languageName
                                    )
                                }

                                arrayList.add(0, translatedTitle.await()) // Add translated title


                                if (stateList != null && stateList.isNotEmpty()) {
                                    // Translate state names
                                    val translations = stateList.mapIndexed { index, item ->
                                        async {
                                            val translatedName = TranslationHelper.translateText(
                                                item.state_name, requireContext().languageName
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
                                        requireContext(),
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
        commenViewModel.State()
    }

    private fun getDistrictList(stateId: Int) {

        commenViewModel.districtresult.observe(this) { response ->
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

                                // Translate "Select Main Category"
                                val translatedTitle = async {
                                    TranslationHelper.translateText(
                                        "Select District",
                                        requireContext().languageName
                                    )
                                }

                                arrayList.add(0, translatedTitle.await()) // Add translated title

                                if (districtList != null && districtList.isNotEmpty()) {


                                    // Translate state names
                                    val translations = districtList.mapIndexed { index, item ->
                                        async {
                                            val translatedName = TranslationHelper.translateText(
                                                item.district_name, requireContext().languageName
                                            )

                                            if (item.district_name == distName) {
                                                selectedIndex =
                                                    index + 1 // Offset due to "Select State"
                                            }
                                            translatedName
                                        }
                                    }

                                    arrayList.addAll(translations.awaitAll()) // Wait for all translations

                                    val arrayAdapter = ArrayAdapter(
                                        requireContext(),
                                        android.R.layout.simple_spinner_item,
                                        arrayList
                                    )
                                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    spinnerForDitrict.adapter = arrayAdapter

                                    // Set the spinner to the desired state by name
                                    spinnerForDitrict.setSelection(selectedIndex)

                                    spinnerForDitrict.onItemSelectedListener =
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
        commenViewModel.district(stateId!!)
    }

    private fun checkAndRequestPermissions() {
        if (REQUIRED_PERMISSIONS.any {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    it
                ) != PackageManager.PERMISSION_GRANTED
            }) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                CAMERA_REQUEST_CODE
            )
        } else {
            // showPictureDialog()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // showPictureDialog()
            } else {
                Toast.makeText(context, "Camera permission is required.", Toast.LENGTH_SHORT).show()
            }
        }
    }


}