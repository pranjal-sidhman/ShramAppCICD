package com.uvk.shramapplication.ui.employeer.worklist.list.job_request_list

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.ActivityJobRequestDetailsBinding
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.ui.chat.ChatActivity
import com.uvk.shramapplication.ui.employeer.home.EmployeerViewModel
import com.google.android.material.chip.Chip
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.roleId
import com.mahindra.serviceengineer.savedata.token
import com.mahindra.serviceengineer.savedata.userid
import com.uvk.shramapplication.helper.CommenViewModel
import com.uvk.shramapplication.helper.GlobalFunctions
import com.uvk.shramapplication.helper.TranslationHelper
import com.uvk.shramapplication.ui.wallet.WalletActivity
import kotlinx.coroutines.launch

class JobRequestDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJobRequestDetailsBinding
    private var pd: TransparentProgressDialog? = null

    private val viewModel by viewModels<EmployeerViewModel>()
    private val commenViewModel by viewModels<CommenViewModel>()
    private lateinit var postJobList: List<JobItem>

    private var userIds: String? = null
    private var applyJobId: String? = null
    private var requestJobId: String? = null
    private var applyStatus: String? = null
    private var profile_img: String? = null
    private var user_name: String? = null
    private var emp_id: Int? = null
    private var job_id: String? = null
    private var mobile_no: String? = null
    private var nextAction: String? = null


    val TAG = " JobRequestDetailsActivity "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJobRequestDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backicon.setOnClickListener { finish() }
        pd = TransparentProgressDialog(this, R.drawable.progress)

        userIds = intent.getStringExtra("user_id") ?: ""
        applyJobId = intent.getStringExtra("apply_job_id") ?: ""
        requestJobId = intent.getStringExtra("request_job_id") ?: ""
        applyStatus = intent.getStringExtra("apply_status") ?: ""

        Log.e(TAG, "userid : $userIds appjobid : $applyJobId appStatus : $applyStatus")

        Log.e(TAG, "userid : $userIds appjobid : $applyJobId appStatus : $applyStatus")

        getJobRequestDetails()
        observeViewModels()


        /* if (!userIds.isNullOrEmpty()) {
             binding.btnChat.setOnClickListener {
                // getcallDeductStatus(userid, userIds!!)
                 commenViewModel.callDeductStatus(
                     userid,
                     userIds!!
                 )
             }
         }*/

        binding.btnChat.setOnClickListener {
            if (!userIds.isNullOrEmpty()) {
                nextAction = "chat"
                commenViewModel.callDeductStatus(userid, userIds!!)
            }
        }

        binding.btnCall.setOnClickListener {
            lifecycleScope.launch {
                mobile_no = postJobList[0].mobile_no

                if (roleId == "2") {
                    val msg = TranslationHelper.translateText(
                        "You are about to 1st time call the employee. ₹5 will be deducted from your wallet. Please confirm to proceed.",
                        languageName
                    )

                    AlertDialog.Builder(this@JobRequestDetailsActivity)
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


        binding.btnReject.setOnClickListener {
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            lifecycleScope.launch {
                val title = TranslationHelper.translateText(
                    "Are you sure you want to reject this employee?",
                    languageName
                )

                builder.setMessage(title)

                builder.setPositiveButton(R.string.yes) { dialog, _ ->

                    rejectEmp()

                    dialog.dismiss()
                }

                builder.setNegativeButton(R.string.no) { dialog, _ ->
                    dialog.dismiss()
                }

                val alertDialog = builder.create()
                alertDialog.show()
            }
        }

        binding.btnAccept.setOnClickListener {
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            lifecycleScope.launch {
                val title = TranslationHelper.translateText(
                    "Are you sure you want to accept this employee?",
                    languageName
                )

                builder.setMessage(title)

                builder.setPositiveButton(R.string.yes) { dialog, _ ->

                    acceptJobEmp()

                    dialog.dismiss()
                }

                builder.setNegativeButton(R.string.no) { dialog, _ ->
                    dialog.dismiss()
                }

                val alertDialog = builder.create()
                alertDialog.show()
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
                        Toast.makeText(this@JobRequestDetailsActivity, msg, Toast.LENGTH_SHORT).show()

                        if (response.data?.status == true) {
                            proceedToNextAction()
                        } else if (response.data?.status_code == 402) {
                            val intent = Intent(this@JobRequestDetailsActivity, WalletActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }else if (response.data?.status_code == 404) {
                            val intent = Intent(this@JobRequestDetailsActivity, WalletActivity::class.java)
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

        /*commenViewModel.callDeductStatusResult.observe(this) { response ->
            pd?.show()
            when (response) {
                is BaseResponse.Success -> {
                    pd?.dismiss()
                    if (response.data?.status == true) {
                        startChatActivity()
                    } else {
                        askForDeductionConfirmation()
                    }
                    Log.e("Job request details","chat onCreate : ${response}")
                }
                is BaseResponse.Error -> {
                    pd?.dismiss()
                    Toast.makeText(this, response.msg, Toast.LENGTH_SHORT).show()
                    Log.e("Job request details","chat onCreate :error ${response}")
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
                        if (response.data?.status == true) {
                            val msg = TranslationHelper.translateText(
                                response.data.message ?: "", languageName
                            )
                            Toast.makeText(this@JobRequestDetailsActivity, msg, Toast.LENGTH_SHORT).show()

                            val dialIntent = Intent(Intent.ACTION_DIAL)
                            dialIntent.data = Uri.parse("tel:$mobile_no")
                            startActivity(dialIntent)
                            //   startChatActivity()
                        } else {
                            val msg = TranslationHelper.translateText(
                                response.data?.message ?: "", languageName
                            )
                            Toast.makeText(this@JobRequestDetailsActivity, msg, Toast.LENGTH_SHORT).show()
                        }

                        if (response.data?.status_code == 402) {
                            val intent = Intent(this@JobRequestDetailsActivity, WalletActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)

                        }

                        Log.e("Job request details","call onCreate : ${response}")
                    }
                }
                is BaseResponse.Error -> {
                    pd?.dismiss()
                    Toast.makeText(this, response.msg, Toast.LENGTH_SHORT).show()
                    Log.e("Job request details details","call onCreate : error ${response}")
                }
                is BaseResponse.Loading -> pd?.show()
            }
        }*/
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
            val builder = AlertDialog.Builder(this@JobRequestDetailsActivity)
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



    /*private fun getcallDeductStatus(employer_id: String,
                                    employee_id: String) {
        try {
            if (isOnline) {

                commenViewModel.callDeductStatusResult.observe(this) { response ->
                    pd?.show() // Show loading indicator

                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator


                            if(response.data?.status == true){
                                if (!userIds.isNullOrEmpty()) {
                                    binding.btnChat.setOnClickListener {

                                        val intent = Intent(this, ChatActivity::class.java)
                                        intent.putExtra("user_id", userIds)
                                        intent.putExtra("name", user_name)
                                        intent.putExtra("profile_img", profile_img)
                                        startActivity(intent)

                                    }
                                }
                            }else{
                                lifecycleScope.launch {

                                    val builder =
                                        AlertDialog.Builder(this@JobRequestDetailsActivity)
                                    // builder.setMessage(getString(R.string.call_msg))
                                    if (roleId == "2") {
                                        val msg = TranslationHelper.translateText(
                                            "You are about to 1st time chat the employee. ₹5 will be deducted from your wallet. Please confirm to proceed.",
                                            languageName
                                        )
                                        builder.setMessage(msg)
                                    }else{
                                        builder.setMessage(getString(R.string.call_msg))
                                    }

                                    builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->

                                        if (roleId == "2") {
                                            chatDeductedAmount(
                                                userid,
                                                postJobList[0].id.toString()
                                            )
                                        } else {
                                            val intent = Intent(this@JobRequestDetailsActivity, ChatActivity::class.java)
                                            intent.putExtra("user_id", userIds)
                                            intent.putExtra("name", user_name)
                                            intent.putExtra("profile_img", profile_img)
                                            startActivity(intent)
                                        }

                                        dialog.dismiss()
                                    }

                                    builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
                                        dialog.dismiss()
                                    }

                                    val alertDialog = builder.create()
                                    alertDialog.show()
                                }
                            }


                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            Toast.makeText(
                                this@JobRequestDetailsActivity,
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

                commenViewModel.callDeductStatus(
                    employer_id,
                    employee_id
                )

            } else {
                Toast.makeText(
                    this@JobRequestDetailsActivity,
                    "Internet not connected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e(TAG, "Error occurred: ${e.localizedMessage}")

        }

    }

    private fun chatDeductedAmount(employer_id: String, employee_id: String) {
        try {
            if (isOnline) {
                commenViewModel.callDeductStatusResult.observe(this) { response ->
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


                                        Log.e("Call", "Call res: ${response.data}")
                                        Log.e("Call", "Call msg: ${response.data.message}")
                                        Log.e("Call", "Call status: ${response.data.status}")

                                        if(response.data.status== true) {
                                            val msg = TranslationHelper.translateText(response.data.message ?: "", languageName)
                                            Toast.makeText(this@JobRequestDetailsActivity,msg,Toast.LENGTH_SHORT).show()

                                            val intent = Intent(this@JobRequestDetailsActivity, ChatActivity::class.java)
                                            intent.putExtra("user_id", userIds)
                                            intent.putExtra("name", user_name)
                                            intent.putExtra("profile_img", profile_img)
                                            startActivity(intent)



                                        }else{
                                            //val msg = TranslationHelper.translateText(response.data.message ?: "", languageName)
                                           // Toast.makeText(this@JobRequestDetailsActivity,msg,Toast.LENGTH_SHORT).show()
                                        }

                                        commenViewModel.callDeductStatus(
                                            employer_id,
                                            employee_id
                                        )

                                    } catch (e: Exception) {
                                        Log.e(
                                            "Translation Chat Error",
                                            "Error translating Chat: ${e.message}"
                                        )
                                    }
                                }
                            }
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss()

                        }

                        else -> {
                            Log.e("JobRequestDetails Chat", "Unhandled case")
                        }
                    }
                }

                commenViewModel.callEmployee(employer_id,employee_id)

            } else {
                Toast.makeText(this, "Internet not connected", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("EmpDetails", "Error occurred: chat ${e.localizedMessage}")
        }
    }*/

    private fun rejectEmp() {
        try {
            if (isOnline) {

                viewModel.rejectSelectEmpResult.observe(this) { response ->
                    pd?.show() // Show loading indicator

                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            Toast.makeText(
                                this@JobRequestDetailsActivity,
                                response.data!!.message,
                                Toast.LENGTH_SHORT
                            ).show()


                            getJobRequestDetails()



                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            Toast.makeText(
                                this@JobRequestDetailsActivity,
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

                viewModel.rejectEmp(
                    token!!,
                    userid!!,
                    emp_id!!,
                    job_id!!,
                )

            } else {
                Toast.makeText(
                    this@JobRequestDetailsActivity,
                    "Internet not connected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e(TAG, "Error occurred: ${e.localizedMessage}")

        }
    }

    private fun acceptJobEmp() {
        try {
            if (isOnline) {

                viewModel.acceptSelectEmpResult.observe(this) { response ->
                    pd?.show() // Show loading indicator

                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            Toast.makeText(
                                this@JobRequestDetailsActivity,
                                response.data!!.message,
                                Toast.LENGTH_SHORT
                            ).show()

                            getJobRequestDetails()



                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            Toast.makeText(
                                this@JobRequestDetailsActivity,
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

                viewModel.acceptSelectEmp(
                    token!!,
                    userid!!,
                    emp_id!!,
                    job_id!!,
                )

            } else {
                Toast.makeText(
                    this@JobRequestDetailsActivity,
                    "Internet not connected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e(TAG, "Error occurred: ${e.localizedMessage}")

        }

    }

    private fun getJobRequestDetails() {
        try {
            if (isOnline) {

                viewModel.getJobRequestDetailsResult.observe(this) { response ->
                    pd?.show() // Show loading indicator

                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            if (response.data?.data.isNullOrEmpty()) {

                                Toast.makeText(
                                    this@JobRequestDetailsActivity,
                                    response.data!!.message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()

                            } else {
                                postJobList = (response.data?.data ?: emptyList()) as List<JobItem>
                                //dataList = avaiEmpList // Initialize imageList here

                                lifecycleScope.launch {
                                    binding.tvName.text = TranslationHelper.translateText(
                                        postJobList[0].name ?: "",
                                        languageName
                                    )

                                    binding.tvDesignation.text = TranslationHelper.translateText(
                                        postJobList[0].designation ?: "",
                                        languageName
                                    )
                                    binding.tvLoc.text =
                                        TranslationHelper.translateText(
                                            postJobList[0].address ?: "",
                                            languageName
                                        )
                                    // binding.tvMobile.text = postJobList[0].mobile_no
                                    val emailId = postJobList[0].email
                                    binding.tvEmail.text = GlobalFunctions.smartMaskEmail(emailId)
                                    binding.tvSkill.text =
                                        TranslationHelper.translateText(
                                            postJobList[0].skill ?: "",
                                            languageName
                                        )
                                }
                                user_name = postJobList[0].name
                                profile_img = postJobList[0].profile_image
                                job_id = postJobList[0].job_id
                                emp_id = postJobList[0].id
                                mobile_no = postJobList[0].mobile_no

                                val status = postJobList[0].select_status
                                if (!status.isNullOrEmpty()){
                                    binding.llStatus.visibility = View.VISIBLE
                                    binding.llAccRejBtn.visibility = View.GONE
                                    binding.tvStatus.text = status

                                    // Set color based on status
                                    if (status.equals("Accepted", ignoreCase = true)) {
                                        binding.tvStatus.setTextColor(ContextCompat.getColor(this, R.color.green))
                                    } else if (status.equals("Rejected", ignoreCase = true)) {
                                        binding.tvStatus.setTextColor(ContextCompat.getColor(this, R.color.red))
                                    }
                                }else{
                                    binding.llAccRejBtn.visibility = View.VISIBLE
                                }

                                if (!postJobList[0].job_request_date.isNullOrEmpty()) {

                                    lifecycleScope.launch {
                                        binding.tvDate.text = TranslationHelper.translateText(
                                            "Requested On " + postJobList[0].job_request_date ?: "",
                                            languageName
                                        )
                                    }
                                    binding.tvDate.setTextColor(
                                        ContextCompat.getColor(
                                            this,
                                            R.color.green
                                        )
                                    )
                                } else {
                                    lifecycleScope.launch {
                                        binding.tvDate.text = TranslationHelper.translateText(
                                            "Applied On " + postJobList[0].apply_date ?: "",
                                            languageName
                                        )
                                    }

                                }

                                Glide.with(binding.imageView)
                                    .load(postJobList[0].profile_image)
                                    .placeholder(R.drawable.user)
                                    .into(binding.imageView)

                                // Add JobType chips
                                binding.chipGroupJobTypes.removeAllViews() // Clear previous chips if any

                                // Add Category chips
                                binding.chipGroupCategories.removeAllViews()
                                postJobList[0].categories.forEach { category ->
                                    val chip = Chip(this).apply {
                                        lifecycleScope.launch {
                                            text = TranslationHelper.translateText(
                                                category.name ?: "",
                                                languageName
                                            )
                                        }
                                    }
                                    // chip.setChipBackgroundColorResource(R.color.light_blue)
                                    chip.setChipBackgroundColorResource(R.color.light_blue_chip)
                                    binding.chipGroupCategories.addView(chip)
                                }

                                // Add SubCategory chips
                                binding.chipGroupSubCategories.visibility = View.GONE
                                if (!postJobList[0].sub_categories[0].sub_category_names.isNullOrEmpty()) {
                                    binding.chipGroupSubCategories.visibility = View.VISIBLE
                                    // Add SubCategory chips
                                    binding.chipGroupSubCategories.removeAllViews()
                                    postJobList[0].sub_categories.forEach { subCategory ->
                                        val chip = Chip(this).apply {
                                            lifecycleScope.launch {
                                                text = TranslationHelper.translateText(
                                                    subCategory.sub_category_names ?: "",
                                                    languageName
                                                )
                                            }
                                        }

                                        binding.chipGroupSubCategories.addView(chip)
                                    }
                                }


                                /* binding.btnCall.setOnClickListener {
                                     lifecycleScope.launch {
                                          mobile_no = postJobList[0].mobile_no
                                         val builder =
                                             AlertDialog.Builder(this@JobRequestDetailsActivity)
                                         // builder.setMessage(getString(R.string.call_msg))
                                         if (roleId == "2") {
                                             val msg = TranslationHelper.translateText(
                                                 "You are about to 1st time call the employee. ₹5 will be deducted from your wallet. Please confirm to proceed.",
                                                 languageName
                                             )
                                             builder.setMessage(msg)
                                         }else{
                                             builder.setMessage(getString(R.string.call_msg))
                                         }

                                         builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->

                                             //  callingData(listOf(employee.user_id), employee.job_id!!)

                                             if (roleId == "2") {
                                                *//* deductedAmount(
                                                    userid,
                                                    postJobList[0].id.toString(),
                                                    mobile_no!!
                                                )*//*
                                                commenViewModel.callEmployee(userid, userIds!!)

                                            } else {
                                                val dialIntent = Intent(Intent.ACTION_DIAL)
                                                dialIntent.data = Uri.parse("tel:$mobile_no")
                                                startActivity(dialIntent)
                                            }

                                            dialog.dismiss()
                                        }

                                        builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
                                            dialog.dismiss()
                                        }

                                        val alertDialog = builder.create()
                                        alertDialog.show()
                                    }
                                }*/
                            }

                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            Toast.makeText(
                                this@JobRequestDetailsActivity,
                                response.msg?:"",
                                Toast.LENGTH_SHORT
                            ).show() // Show error message
                        }

                        is BaseResponse.Loading -> {
                            // Show loading indicator if needed
                        }
                    }
                }

                viewModel.getJobRequestDetails(
                    userIds!!,
                    applyJobId!!,
                    applyStatus!!,
                    requestJobId!!
                )

            } else {
                Toast.makeText(
                    this@JobRequestDetailsActivity,
                    "Internet not connected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e(TAG, "Error occurred: ${e.localizedMessage}")

        }
    }

    private fun deductedAmount(employer_id: String, employee_id: String, mobile_no: String) {
        try {
            if (isOnline) {
                commenViewModel.walletCallResult.observe(this) { response ->
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


                                        Log.e("Call", "Call res: ${response.data}")
                                        Log.e("Call", "Call msg: ${response.data.message}")
                                        Log.e("Call", "Call status: ${response.data.status}")

                                        if(response.data.status== true) {
                                            val msg = TranslationHelper.translateText(response.data.message ?: "", languageName)
                                            Toast.makeText(this@JobRequestDetailsActivity,msg,Toast.LENGTH_SHORT).show()

                                            val dialIntent = Intent(Intent.ACTION_DIAL)
                                            dialIntent.data = Uri.parse("tel:$mobile_no")
                                            startActivity(dialIntent)

                                        }else{
                                            // val msg = TranslationHelper.translateText(response.data.message ?: "", languageName)
                                            //Toast.makeText(this@JobRequestDetailsActivity,msg,Toast.LENGTH_SHORT).show()
                                        }



                                    } catch (e: Exception) {
                                        Log.e(
                                            "Translation Call Error",
                                            "Error translating Call: ${e.message}"
                                        )
                                    }
                                }
                            }
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss()

                        }

                        else -> {
                            Log.e("JobRequestDetails Call", "Unhandled case")
                        }
                    }
                }

                commenViewModel.callEmployee(employer_id,employee_id)

            } else {
                Toast.makeText(this, "Internet not connected", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("JobRequsetDetails", "Error occurred: call ${e.localizedMessage}")
        }
    }

}