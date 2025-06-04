package com.uvk.shramapplication.ui.joblist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.uvk.shramapplication.R
import com.uvk.shramapplication.ui.login.LoginActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.mahindra.serviceengineer.savedata.isuserlgin

class JobListAdapter(val context: Context,
                     private val jobList: List<Job>):
RecyclerView.Adapter<JobListAdapter.JobListViewHolder>() {


    inner class JobListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val companyName: TextView = itemView.findViewById(R.id.tvDesc)
        val salary: TextView = itemView.findViewById(R.id.tvSalary)
        val location: TextView = itemView.findViewById(R.id.tvLoc)
        val tvApply: TextView = itemView.findViewById(R.id.tvApply)
        val chipGroupJobTypes: ChipGroup = itemView.findViewById(R.id.chipGroupJobTypes)
        val chipGroupCategories: ChipGroup = itemView.findViewById(R.id.chipGroupCategories)
        val chipGroupSubCategories: ChipGroup = itemView.findViewById(R.id.chipGroupSubCategories)

        // val job_type: TextView = itemView.findViewById(R.id.tvvaca)
      //  val tvqualification: TextView = itemView.findViewById(R.id.tvqualification)
       // val tvCall: TextView = itemView.findViewById(R.id.tvCall)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_job_list, parent, false)
        return JobListViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobListViewHolder, position: Int) {
        val job = jobList[position]
        holder.title.text = job.title
        holder.companyName.text = job.company_name
        holder.location.text = job.location
        //holder.salary.text = formatSalary(job.salary)
        holder.salary.text = formatSalary(job.salary, job.salary_amount) + " " + job.salary_type

        holder.itemView.setOnClickListener {
            if (context.isuserlgin) {
               // context.startActivity(Intent(context, JobDetailsActivity::class.java))
                val intent = Intent(context, JobDetailsActivity::class.java).apply {
                    putExtra("Job_id", job.id)
                }
                context.startActivity(intent)
            }else{
                Toast.makeText(context,"Please Login App",Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, LoginActivity::class.java))
                ( context as Activity).finish()
            }
        }

        // Add JobType chips
        holder.chipGroupJobTypes.removeAllViews() // Clear previous chips if any
        job.job_types.forEach { jobType ->
            val chip = Chip(context).apply {
                text = jobType.job_type_names
                //isClickable = true
                //isCheckable = true
            }
            holder.chipGroupJobTypes.addView(chip)
        }

        // Add Category chips
        holder.chipGroupCategories.removeAllViews()
        job.categories.forEach { category ->
            val chip = Chip(context).apply {
                text = category.category_names
               // isClickable = true
               // isCheckable = true
            }
            chip.setChipBackgroundColorResource(R.color.light_blue_chip)
            holder.chipGroupCategories.addView(chip)
        }

        // Add SubCategory chips
        holder.chipGroupSubCategories.removeAllViews()
        job.sub_categories.forEach { subCategory ->
            val chip = Chip(context).apply {
                text = subCategory.sub_category_names
               // isClickable = true
               // isCheckable = true
            }
            holder.chipGroupSubCategories.addView(chip)
        }

    }

    fun hideFirstSixDigits(number: String): String {
        return if (number.length > 6) {
            "******" + number.substring(number.length - 4)
        } else {
            number
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

    override fun getItemCount(): Int = jobList.size
}
