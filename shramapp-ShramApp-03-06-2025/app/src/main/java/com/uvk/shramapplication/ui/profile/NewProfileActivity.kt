package com.uvk.shramapplication.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.ActivityNewProfileBinding
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.ui.profile.myFollowers.MyFollowersFragment
import com.uvk.shramapplication.ui.profile.myPost.MyPostFragment
import com.uvk.shramapplication.ui.profile.personal_info.PersonalInfoFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.token
import com.mahindra.serviceengineer.savedata.userid
import com.uvk.shramapplication.MainActivity
import com.uvk.shramapplication.helper.TranslationHelper
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class NewProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityNewProfileBinding
    private var pd: TransparentProgressDialog? = null
    lateinit var profileViewModel: ProflieViewModel

    private var bottomSheetDialog: BottomSheetDialog? = null
    private lateinit var iv_ProfileImg: CircleImageView
    private lateinit var currentImageView: ImageView
    private var imgBase64String: String? = null
    private var api_profileImg: String? = null
    private var designation: String? = null
    private var companyName: String? = null
    private var userName: String? = null
    private var roleId: String? = null

    private var profileBase64String: String? = null

    private val viewModel by viewModels<ProflieViewModel>()

    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2

    private val REQUIRED_PERMISSIONS = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.READ_MEDIA_IMAGES // For Android 13+
    )

    val TAG = "NewProfileActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_new_profile)
        binding = ActivityNewProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pd = TransparentProgressDialog(this, R.drawable.progress)

        binding.backicon.setOnClickListener { finish() }
        // Setup ViewPager2 with the adapter
        val adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // Connect TabLayout with ViewPager2
       /* TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Personal Info"
                1 -> tab.text = "My Post"
                2 -> tab.text = "Connections"
            }
        }.attach()*/

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            val defaultText = when (position) {
                0 -> "Personal Info"
                1 -> "My Post"
                2 -> "Connections"
                else -> "Personal Info"
            }

            tab.text = defaultText  // Set default text immediately

            // Launch coroutine to update text asynchronously
            lifecycleScope.launch {
                val translatedText = TranslationHelper.translateText(defaultText, languageName)
                tab.text = translatedText
            }
        }.attach()

        profileViewModel = ViewModelProvider(this)[ProflieViewModel::class.java]

        // Update ViewModel when the name is changed
        binding.tvUserName.doAfterTextChanged {
            profileViewModel.profileName.value = it.toString()
        }

        getProfileData(userid)

        currentImageView = binding.imgProfiles
        binding.llEdit.setOnClickListener {
            getProfileData(userid)
            showDialog()
        }



    }

    private fun getProfileData(userid: String) {
        try {
            if (isOnline) {
                viewModel.getProfileResult.observe(this) { response ->

                    when (response) {
                        is BaseResponse.Loading -> {
                            pd?.show()
                        }

                        is BaseResponse.Success -> {
                            pd?.dismiss()

                            userName = response.data!!.data[0].name
                            companyName = response.data!!.data[0].company_name
                            designation = response.data!!.data[0].designation
                            api_profileImg = response.data!!.data[0].profile_image
                            roleId = response.data!!.data[0].role_id

                           // binding.tvUserName.setText(userName)

                            lifecycleScope.launch {
                                binding.tvUserName.text = TranslationHelper.translateText(
                                    userName ?: "",
                                    languageName
                                )
                            }

                            /*if (!designation.isNullOrEmpty()) {
                               // binding.tvCompDesignationName.setText(designation)
                                lifecycleScope.launch {
                                    binding.tvCompDesignationName.text = TranslationHelper.translateText(
                                        designation ?: "",
                                        languageName
                                    )
                                }
                            } else {
                                lifecycleScope.launch {
                                    binding.tvCompDesignationName.text = TranslationHelper.translateText(
                                        companyName ?: "",
                                        languageName
                                    )
                                }
                               // binding.tvCompDesignationName.setText(companyName)
                            }*/
                            if(roleId == "2"){
                                lifecycleScope.launch {
                                    binding.tvCompDesignationName.text = TranslationHelper.translateText(
                                        companyName ?: "",
                                        languageName
                                    )
                                }
                            }else{
                                lifecycleScope.launch {
                                    binding.tvCompDesignationName.text = TranslationHelper.translateText(
                                        designation ?: "",
                                        languageName
                                    )
                                }
                            }

                            Glide.with(binding.imgProfiles)
                                .load(api_profileImg)
                                .placeholder(R.drawable.worker)
                                .into(binding.imgProfiles)

                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss()
                            Toast.makeText(
                                this@NewProfileActivity,
                                response.msg?:"",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    }
                }
                viewModel.getProfile(user_id = userid)
            } else {
                Toast.makeText(
                    this@NewProfileActivity,
                    "Internet not connected",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } catch (e: Exception) {
            Log.e("ProfileError", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(
                this@NewProfileActivity,
                "An error occurred: ${e.localizedMessage}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showDialog() {
        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)

        val dialogView = LayoutInflater.from(this).inflate(R.layout.edit_profile, null)

        bottomSheetDialog?.setContentView(dialogView)

        iv_ProfileImg = dialogView.findViewById<CircleImageView>(R.id.iv_ProfileImg)
        val iv_Camedit = dialogView.findViewById<ImageView>(R.id.iv_Camedit)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etDesignation = dialogView.findViewById<EditText>(R.id.etDesignation)
        val etCompanyName = dialogView.findViewById<EditText>(R.id.etCompanyName)
        val btnSubmit = dialogView.findViewById<TextView>(R.id.btnSubmit)
        val btnCancel = dialogView.findViewById<TextView>(R.id.btnCancel)

        iv_Camedit.setOnClickListener {
            Log.e("click", "Camera button clicked")

            currentImageView = iv_ProfileImg

            showPictureDialog()

        }

        etDesignation.visibility = View.GONE
        etCompanyName.visibility = View.GONE

        Glide.with(iv_ProfileImg)
            .load(api_profileImg)
            .placeholder(R.drawable.worker)
            .into(iv_ProfileImg)

       // etName.setText(userName)
        lifecycleScope.launch {
            etName.setText(TranslationHelper.translateText(
                userName ?: "",
                languageName
            ))
        }

        /*if (!designation.isNullOrEmpty()) {
            etDesignation.visibility = View.GONE
            //etDesignation.setText(designation)
            lifecycleScope.launch {
                etDesignation.setText(TranslationHelper.translateText(
                    designation ?: "",
                    languageName
                ))
            }
        } else {
            etCompanyName.visibility = View.VISIBLE
           // etCompanyName.setText(companyName)
            lifecycleScope.launch {
                etCompanyName.setText(TranslationHelper.translateText(
                    companyName ?: "",
                    languageName
                ))
            }

        }*/

        Log.e("profileActivity","RoleId : $roleId")

        if(roleId == "2"){
            etCompanyName.visibility = View.VISIBLE
            etDesignation.visibility = View.GONE
            // etCompanyName.setText(companyName)
            lifecycleScope.launch {
                etCompanyName.setText(TranslationHelper.translateText(
                    companyName ?: "",
                    languageName
                ))
            }
        }else{
           // etDesignation.visibility = View.VISIBLE
            etCompanyName.visibility = View.GONE
            //etDesignation.setText(designation)
            lifecycleScope.launch {
                etDesignation.setText(TranslationHelper.translateText(
                    designation ?: "",
                    languageName
                ))
            }
        }

        btnSubmit.setOnClickListener {

            when {
                etName.text.isNullOrEmpty() -> {
                    etName.error = getString(R.string.enter_name)
                    etName.requestFocus()
                }

                /*!designation.isNullOrEmpty() && etDesignation.text.isNullOrEmpty() -> {
                    etDesignation.error = getString(R.string.enter_designation)
                    etDesignation.requestFocus()
                }*/

                !companyName.isNullOrEmpty() && etCompanyName.text.isNullOrEmpty() -> {
                    etCompanyName.error = getString(R.string.enter_comp_name)
                    etCompanyName.requestFocus()
                }

                else -> {
                    val name = etName.text.toString().trim()
                    val compName = etCompanyName.text.toString().trim()
                    val designation = etDesignation.text.toString().trim()

                    submitApi(name, compName, designation)
                }
            }

        }

        btnCancel.setOnClickListener {
            bottomSheetDialog?.dismiss()
        }

        /*  // Set margins programmatically
          dialogView.layoutParams = (dialogView.layoutParams as ViewGroup.MarginLayoutParams).apply {
              setMargins(30, 30, 30, 30) // Set left, top, right, bottom margins in pixels
          }*/

        bottomSheetDialog?.show()

    }

    private fun submitApi(name: String, companyName: String, designation: String) {
        try {
            if (isOnline) {

                val profilesImg: String? =
                    if (!profileBase64String.isNullOrEmpty()) profileBase64String else ""

                Log.e("tag", "Profile Image: $profilesImg")


                viewModel.editPersonalDetailsResult.observe(this) { response ->
                    pd?.show() // Show loading indicator
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

                                Toast.makeText(
                                    this@NewProfileActivity,
                                    " ${data.message}",
                                    Toast.LENGTH_SHORT
                                ).show()


                                binding.tvUserName.text = name



                                if (!companyName.isNullOrEmpty()) {
                                    binding.tvCompDesignationName.text = companyName
                                } else {
                                    binding.tvCompDesignationName.text = designation
                                }
                                bottomSheetDialog?.dismiss()


                            }
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss()
                            Log.e(TAG, "Error: ${response.msg}")
                            Toast.makeText(
                                this@NewProfileActivity,
                                "Error: ${response.msg}",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                        else -> {
                            pd?.dismiss()
                            Log.e(TAG, "Unexpected response: $response")
                        }
                    }
                }

                viewModel.editPersonalDetails(
                    token = token,
                    name = name,
                    profile_image = profilesImg!!,
                    role_id = roleId!!,
                    designation = designation,
                    company_name = companyName,
                    userId = userid
                )

            } else {
                Toast.makeText(
                    this@NewProfileActivity,
                    "Internet not connected",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e(TAG, "Error occurred: ${e.localizedMessage}")

        }
    }


    class ViewPagerAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> PersonalInfoFragment()
                1 -> MyPostFragment()
                2 -> MyFollowersFragment()
                else -> PersonalInfoFragment()
            }
        }


    }

    private fun showPictureDialog() {
        val pictureDialog = androidx.appcompat.app.AlertDialog.Builder(this)
        pictureDialog.setTitle("Shram")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems) { dialog, which ->
            when (which) {
                0 -> openGallery()
                1 -> //openCamera()
                {
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
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
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
                    val bitmap = data?.extras?.get("data") as Bitmap
                    Glide.with(this).load(bitmap).into(iv_ProfileImg)


                    val base64String = convertBitmapToBase64(bitmap)

                    if (currentImageView.id == R.id.iv_ProfileImg) {
                        profileBase64String = base64String
                        Log.e("tag", "Profile Base64 (Camera): $profileBase64String")
                    }

                    if (!profileBase64String.isNullOrEmpty()) {
                        Glide.with(binding.imgProfiles)
                            .load(bitmap)
                            .placeholder(R.drawable.worker)
                            .into(binding.imgProfiles)
                    }

                }

                GALLERY_REQUEST_CODE -> {
                    val uri = data?.data
                    Glide.with(this).load(uri).into(iv_ProfileImg)

                    val base64String = convertImageToBase64(uri!!)

                    if (currentImageView.id == R.id.iv_ProfileImg) {
                        profileBase64String = base64String
                        Log.e("tag", "Profile Base64 (Gallery): $profileBase64String")
                    }

                    if (!profileBase64String.isNullOrEmpty()) {
                        Glide.with(binding.imgProfiles)
                            .load(uri)
                            .placeholder(R.drawable.worker)
                            .into(binding.imgProfiles)
                    }
                }
            }
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

    private fun checkAndRequestPermissions() {
        if (REQUIRED_PERMISSIONS.any {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, CAMERA_REQUEST_CODE)
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
                showPictureDialog()
            } else {
                Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this@NewProfileActivity, MainActivity::class.java)
        startActivity(intent)


        // Optional: Call super if you still want the default behavior
         super.onBackPressed()
    }


}