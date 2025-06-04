package com.uvk.shramapplication.ui.joblist

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.ActivityJobDetailsBinding
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.google.android.material.chip.Chip
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.userid

class JobDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJobDetailsBinding
    private val viewModel by viewModels<JobListViewModel>()
    private var pd: TransparentProgressDialog? = null
    private lateinit var jobList: List<Job>
     var jobId: String? = null


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pd = TransparentProgressDialog(this, R.drawable.progress)

        binding.backicon.setOnClickListener { finish() }

        jobId = intent.getStringExtra("Job_id")

        val switchBackground: ConstraintLayout = findViewById(R.id.switchBackground)
        val activeIndicator: View = findViewById(R.id.activeIndicator)
        val descriptionText: TextView = findViewById(R.id.descriptionText)
        val companyText: TextView = findViewById(R.id.companyText)

        // Initial state
        var isDescriptionSelected = true
        updateSwitchState(activeIndicator, descriptionText, companyText, isDescriptionSelected)

        // Handle "Description" click
        openFragment(DescriptionFragment())

        // Handle "Description" click
        descriptionText.setOnClickListener {
            isDescriptionSelected = true
            updateSwitchState(activeIndicator, descriptionText, companyText, isDescriptionSelected)
            openFragment(DescriptionFragment())
        }

        // Handle "Company" click
        companyText.setOnClickListener {
            isDescriptionSelected = false
            updateSwitchState(activeIndicator, descriptionText, companyText, isDescriptionSelected)
            openFragment(CompanyFragment())
        }

       /* binding.btnApply.setOnClickListener {
            startActivity(Intent(this,SubmitApplActivity::class.java))
        }*/

        getDetails()

    }

    private fun getDetails() {
        try {
            if (isOnline) {
                viewModel.jobDetailsResult.observe(this) { response ->
                    pd?.show() // Show loading indicator
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            // Debugging the response
                            Log.d("JobDetailsActivity", "API Response: ${response.data}")

                            if (response.data?.data.isNullOrEmpty()) {
                                // If the data is null or empty
                                Toast.makeText(
                                    this@JobDetailsActivity,
                                    response.data!!.message,
                                    Toast.LENGTH_SHORT
                                ).show()

                            } else {
                                // If the data is available
                                jobList = response.data?.data ?: emptyList() // Extract the data, or use an empty list if null
                                val job = jobList[0]

                                // Debugging the job data
                                Log.d("JobDetailsActivity", "Job: $job")

                                binding.tvTitle.text = job.title
                                binding.tvTitles.text = job.company_name
                                binding.tvLoc.text = job.location
                              //  binding.tvSalary.text = formatSalary(job.salary)
                                binding.tvSalary.text = formatSalary(job.salary, job.salary_amount) + " " + job.salary_type


                                // Load profile image using Glide
                                Glide.with(binding.imgProfile)
                                    .load(job.image)
                                    .placeholder(R.drawable.user)
                                    .into(binding.imgProfile)

                                // Add JobType chips
                                binding.chipGroupJobTypes.removeAllViews() // Clear previous chips if any

                                // Add JobType chips from job_types list
                                job.job_types.forEach { jobType ->
                                    val chip = Chip(this).apply {
                                        text = jobType.job_type_names
                                    }
                                    binding.chipGroupJobTypes.addView(chip)
                                }

                                // Add Category chips
                                binding.chipGroupCategories.removeAllViews()
                                job.categories.forEach { category ->
                                    val chip = Chip(this).apply {
                                        text = category.category_names
                                        // isClickable = true
                                        // isCheckable = true
                                    }
                                    chip.setChipBackgroundColorResource(R.color.light_blue_chip)
                                    binding.chipGroupCategories.addView(chip)
                                }

                                // Add SubCategory chips
                                binding.chipGroupSubCategories.visibility = View.GONE
                                if(!job.sub_categories[0].sub_category_names.isNullOrEmpty()){
                                    binding.chipGroupSubCategories.visibility = View.VISIBLE
                                    // Add SubCategory chips
                                    binding.chipGroupSubCategories.removeAllViews()
                                    job.sub_categories.forEach { subCategory ->
                                        val chip = Chip(this).apply {
                                            text = subCategory.sub_category_names
                                            // isClickable = true
                                            // isCheckable = true
                                        }

                                        binding.chipGroupSubCategories.addView(chip)
                                    }
                                }
                               /* binding.chipGroupSubCategories.removeAllViews()
                                job.sub_categories.forEach { subCategory ->
                                    val chip = Chip(this).apply {
                                        text = subCategory.sub_category_names
                                        // isClickable = true
                                        // isCheckable = true
                                    }

                                    binding.chipGroupSubCategories.addView(chip)
                                }*/


                            }
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            Toast.makeText(this@JobDetailsActivity, response.msg, Toast.LENGTH_SHORT)
                                .show() // Show error message
                        }

                        is BaseResponse.Loading -> {
                            // Show loading indicator if needed
                        }
                    }
                }

                // Debugging jobId before API call
                Log.d("JobDetailsActivity", "JobId: $jobId")

                // Make the API call
                viewModel.jobDetails(jobId!!, userid!!)

            } else {
                Toast.makeText(this@JobDetailsActivity, "Internet not connected", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("Job list", "Error occurred: ${e.localizedMessage}")
        }
    }

    fun formatSalary(salary: String, salaryAmount: String?): String {
        return when {
            salary.contains("-") -> {
                // Case: Salary range (e.g., "1500-2000")
                val parts = salary.split("-")
                "₹${parts[0].trim()} - ₹${parts[1].trim()}"
            }
            salaryAmount!!.contains("-") -> {
                // Case: Salary range (e.g., "1500-2000")
                val parts = salaryAmount.split("-")
                "₹${parts[0].trim()} - ₹${parts[1].trim()}"
            }
            salary.equals("Other", ignoreCase = true) && !salaryAmount.isNullOrEmpty() -> {
                // Case: Salary is "Other" and salaryAmount is available
                "₹$salaryAmount"
            }
            salary.contains("Below") -> {
                // Case: Single value with "Below"
                salary.replace("Below", "Below ₹")
            }
            else -> {
                // Fallback case
                salary
            }
        }
    }




    private fun openFragment(fragment: Fragment) {
        val bundle = Bundle()
        bundle.putString("jobId", jobId) // Pass the jobId to the fragment
        fragment.arguments = bundle // Set the arguments for the fragment

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.addToBackStack(null) // Add fragment to the back stack
        fragmentTransaction.commit()
    }



    private fun updateSwitchState(
        activeIndicator: View,
        descriptionText: TextView,
        companyText: TextView,
        isDescriptionSelected: Boolean
    ) {
        // Update active indicator position
        val constraintSet = ConstraintSet()
        constraintSet.clone(activeIndicator.parent as ConstraintLayout)
        if (isDescriptionSelected) {
            constraintSet.connect(
                activeIndicator.id,
                ConstraintSet.START,
                descriptionText.id,
                ConstraintSet.START
            )
            constraintSet.connect(
                activeIndicator.id,
                ConstraintSet.END,
                descriptionText.id,
                ConstraintSet.END
            )
        } else {
            constraintSet.connect(
                activeIndicator.id,
                ConstraintSet.START,
                companyText.id,
                ConstraintSet.START
            )
            constraintSet.connect(
                activeIndicator.id,
                ConstraintSet.END,
                companyText.id,
                ConstraintSet.END
            )
        }
        constraintSet.applyTo(activeIndicator.parent as ConstraintLayout)

        // Update background color
        if (isDescriptionSelected) {
            descriptionText.setBackgroundResource(R.drawable.switch_active_indicator)//setBackgroundColor(getColor(android.R.color.holo_blue_light)) // Active: Blue
            companyText.setBackgroundResource(R.drawable.switch_background)//setBackgroundColor(getColor(android.R.color.white))             // Inactive: White switch_background
        } else {
            descriptionText.setBackgroundResource(R.drawable.switch_background)//  setBackgroundColor(getColor(android.R.color.white))         // Inactive: White
            companyText.setBackgroundResource(R.drawable.switch_active_indicator)    //  setBackgroundColor(getColor(android.R.color.holo_blue_light))   // Active: Blue
        }

        // Update text color
        descriptionText.setTextColor(
            if (isDescriptionSelected) getColor(android.R.color.white) else getColor(android.R.color.black)
        )
        companyText.setTextColor(
            if (isDescriptionSelected) getColor(android.R.color.black) else getColor(android.R.color.white)
        )
    }




    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
          //  supportFragmentManager.popBackStack()
            finish()
        } else {
            super.onBackPressed()

        }
    }

}