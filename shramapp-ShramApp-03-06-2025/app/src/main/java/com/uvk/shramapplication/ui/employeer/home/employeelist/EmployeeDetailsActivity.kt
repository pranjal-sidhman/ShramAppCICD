package com.uvk.shramapplication.ui.employeer.home.employeelist

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.ActivityEmployeeDetailsBinding
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.ui.chat.ChatActivity
import com.uvk.shramapplication.ui.employeer.home.EmployeerViewModel
import com.uvk.shramapplication.ui.employeer.response.EmployeerData
import com.google.android.material.chip.Chip
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.roleId
import com.mahindra.serviceengineer.savedata.userid
import com.mahindra.serviceengineer.savedata.username
import com.uvk.shramapplication.helper.CommenViewModel
import com.uvk.shramapplication.helper.GlobalFunctions
import com.uvk.shramapplication.helper.TranslationHelper
import com.uvk.shramapplication.ui.wallet.WalletActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EmployeeDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmployeeDetailsBinding

    private lateinit var empList: List<EmployeerData>
    private val viewModel by viewModels<EmployeerViewModel>()
    private val commenViewModel by viewModels<CommenViewModel>()
    private var pd: TransparentProgressDialog? = null

    var userId: String? = null
    var mobile_no: String? = null
    private var userIds: String? = null
    private var profile_img: String? = null
    private var user_name: String? = null
    private var nextAction: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEmployeeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pd = TransparentProgressDialog(this, R.drawable.progress)

        binding.backicon.setOnClickListener {
            finish()
        }
        userId = intent.getStringExtra("emp_id")

        getEmpDetails(userId!!)
        observeViewModels()

        binding.btnChat.setOnClickListener {
            if (!userIds.isNullOrEmpty()) {
                nextAction = "chat"
                commenViewModel.callDeductStatus(userid, userIds!!)
            }
        }

        binding.btnCall.setOnClickListener {
            lifecycleScope.launch {
                mobile_no = empList[0].mobile_no

                if (roleId == "2") {
                    val msg = TranslationHelper.translateText(
                        "You are about to 1st time call the employee. ₹5 will be deducted from your wallet. Please confirm to proceed.",
                        languageName
                    )

                    AlertDialog.Builder(this@EmployeeDetailsActivity)
                        .setMessage(msg)
                        .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                            nextAction = "call"
                            commenViewModel.callDeductStatus(userid, userIds!!)
                            dialog.dismiss()
                        }
                        .setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                } else {
                    val dialIntent = Intent(Intent.ACTION_DIAL)
                    dialIntent.data = Uri.parse("tel:$mobile_no")
                    startActivity(dialIntent)
                }
            }
        }
    }

    private fun observeViewModels() {
        commenViewModel.callDeductStatusResult.observe(this) { response ->
            pd?.show()
            when (response) {
                is BaseResponse.Success -> {
                    pd?.dismiss()
                    if (response.data?.status == true) {
                        // Already deducted before, proceed directly
                        proceedToNextAction()
                    } else {
                        askForDeductionConfirmation()
                    }
                    Log.e("Job request details","chat onCreate : ${response}")
                }
                is BaseResponse.Error -> {
                    pd?.dismiss()
                    Toast.makeText(this, response.msg, Toast.LENGTH_SHORT).show()
                    Log.e("Job request details","chat onCreate : ${response}")
                }
                is BaseResponse.Loading -> pd?.show()
            }
        }

        commenViewModel.walletCallResult.observe(this) { response ->
            pd?.show()
            when (response) {
                is BaseResponse.Success -> {
                    pd?.dismiss()
                    lifecycleScope.launch {
                        val msg = TranslationHelper.translateText(
                            response.data?.message ?: "", languageName
                        )
                        Toast.makeText(this@EmployeeDetailsActivity, msg, Toast.LENGTH_SHORT).show()

                        if (response.data?.status == true) {
                            proceedToNextAction()
                        } else if (response.data?.status_code == 402) {
                            val intent = Intent(this@EmployeeDetailsActivity, WalletActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }else if (response.data?.status_code == 404) {
                            val intent = Intent(this@EmployeeDetailsActivity, WalletActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }

                        Log.e("Job request details","call onCreate : ${response}")
                    }
                }
                is BaseResponse.Error -> {
                    pd?.dismiss()
                    Toast.makeText(this, response.msg, Toast.LENGTH_SHORT).show()
                    Log.e("Job request details","call onCreate : error${response}")
                }
                is BaseResponse.Loading -> pd?.show()
            }
        }
    }


    private fun startChatActivity() {
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra("user_id", userIds)
            putExtra("name", user_name)
            putExtra("profile_img", profile_img)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        }
        startActivity(intent)
    }

    private fun askForDeductionConfirmation() {
        lifecycleScope.launch {
            val builder = AlertDialog.Builder(this@EmployeeDetailsActivity)
            val msg = if (roleId == "2") {
                TranslationHelper.translateText(
                    "₹5 will be deducted from your wallet to proceed. Do you want to continue?",
                    languageName
                )
            } else {
                "Proceed?"
            }

            builder.setMessage(msg)
            builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                if (roleId == "2") {
                    commenViewModel.callEmployee(userid, userIds!!)
                } else {
                    proceedToNextAction()
                }
                dialog.dismiss()
            }

            builder.setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }

            builder.create().show()
        }
    }

    private fun proceedToNextAction() {
        when (nextAction) {
            "chat" -> startChatActivity()
            "call" -> {
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:$mobile_no")
                startActivity(dialIntent)
            }
        }
    }

    private fun getEmpDetails(userId: String) {
        try {

            if (isOnline) {
                viewModel.empDetailsResult.observe(this) { response ->
                    pd?.show() // Show loading indicator
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            // Debugging the response
                            Log.e(
                                "JobDetailsActivity",
                                "API Response Employee: ${response.data!!.data}"
                            )

                            if (response.data?.data.isNullOrEmpty()) {
                                // If the data is null or empty
                                Toast.makeText(
                                    this@EmployeeDetailsActivity,
                                    response.data!!.message,
                                    Toast.LENGTH_SHORT
                                ).show()

                            } else {
                                // If the data is available
                                empList = response.data?.data
                                    ?: emptyList() // Extract the data, or use an empty list if null
                                val emp = empList[0]


                                lifecycleScope.launch {
                                    val name = TranslationHelper.translateText(
                                        emp.name ?: "",
                                        languageName
                                    )
                                    val designation = if (!emp.designation.isNullOrEmpty()) {
                                        TranslationHelper.translateText(
                                            emp.designation ?: "",
                                            languageName
                                        )
                                    } else {
                                        TranslationHelper.translateText(
                                            emp.company_name ?: "",
                                            languageName
                                        )
                                    }
                                    val location = TranslationHelper.translateText(
                                        emp.address ?: "",
                                        languageName
                                    )
                                    val skill = TranslationHelper.translateText(
                                        emp.skill ?: "",
                                        languageName
                                    )

                                    withContext(Dispatchers.Main) {
                                        binding.tvName.text = name
                                        binding.tvDesignation.text = designation
                                        binding.tvLoc.text = location
                                        binding.tvEmail.text =
                                            GlobalFunctions.smartMaskEmail(emp.email)
                                        binding.tvSkill.text = skill
                                    }
                                }
                                user_name = emp.name
                                userIds = emp.id.toString()
                                profile_img = emp.profile_image

                                // Load profile image using Glide
                                Glide.with(binding.imgProfile)
                                    .load(emp.profile_image)
                                    .placeholder(R.drawable.user)
                                    .into(binding.imgProfile)

                                // Add JobType chips
                                binding.chipGroupJobTypes.removeAllViews() // Clear previous chips if any

                                // Add Category chips
                                binding.chipGroupCategories.removeAllViews()
                                emp.categories.forEach { category ->
                                    val chip = Chip(this)
                                    lifecycleScope.launch {
                                        val translatedCategory = TranslationHelper.translateText(
                                            category.name ?: "",
                                            languageName
                                        )
                                        withContext(Dispatchers.Main) {
                                            chip.text = translatedCategory
                                        }
                                    }
                                    chip.setChipBackgroundColorResource(R.color.light_blue_chip)
                                    binding.chipGroupCategories.addView(chip)
                                }


                                // Add SubCategory chips
                                binding.chipGroupSubCategories.visibility = View.GONE
                                if (!emp.sub_categories[0].sub_category_names.isNullOrEmpty()) {
                                    binding.chipGroupSubCategories.visibility = View.VISIBLE
                                    // Add SubCategory chips
                                    binding.chipGroupSubCategories.removeAllViews()
                                    emp.sub_categories.forEach { subCategory ->
                                        val chip = Chip(this).apply {


                                            lifecycleScope.launch {
                                                val translatedSubCategory =
                                                    TranslationHelper.translateText(
                                                        subCategory.sub_category_names ?: "",
                                                        languageName
                                                    )
                                                withContext(Dispatchers.Main) {
                                                    text = translatedSubCategory
                                                }
                                            }
                                        }

                                        binding.chipGroupSubCategories.addView(chip)
                                    }
                                }

                            }
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            Toast.makeText(
                                this@EmployeeDetailsActivity,
                                response.msg,
                                Toast.LENGTH_SHORT
                            )
                                .show() // Show error message
                        }

                        is BaseResponse.Loading -> {
                            // Show loading indicator if needed
                        }
                    }
                }
                // Make the API call
                viewModel.getEmpDetails(userId!!)

            } else {
                Toast.makeText(
                    this@EmployeeDetailsActivity,
                    "Internet not connected",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("Job list", "Error occurred: ${e.localizedMessage}")
        }
    }

}