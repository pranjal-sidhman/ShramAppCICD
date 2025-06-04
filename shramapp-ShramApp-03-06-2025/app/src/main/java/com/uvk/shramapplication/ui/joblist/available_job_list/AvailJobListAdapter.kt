package com.uvk.shramapplication.ui.joblist.available_job_list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.uvk.shramapplication.R
import com.uvk.shramapplication.ui.joblist.Job
import com.uvk.shramapplication.ui.login.LoginActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.mahindra.serviceengineer.savedata.isuserlgin
import com.mahindra.serviceengineer.savedata.languageName
import com.uvk.shramapplication.helper.TranslationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AvailJobListAdapter(val context: Context,
                          private val jobList: List<Job>,
                          val savedJob: (String) -> Unit ):
    RecyclerView.Adapter<AvailJobListAdapter.AvailableJobListViewHolder>() {


    inner class AvailableJobListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val companyName: TextView = itemView.findViewById(R.id.tvDesc)
        val salary: TextView = itemView.findViewById(R.id.tvSalary)
        val location: TextView = itemView.findViewById(R.id.tvLoc)
        val tvApply: TextView = itemView.findViewById(R.id.tvApply)
        val tvApplyStatus: TextView = itemView.findViewById(R.id.tvApplyStatus)
        val tvPostDates: TextView = itemView.findViewById(R.id.tvPostDates)
        val btnSave: ImageView = itemView.findViewById(R.id.btnSave)
        val chipGroupJobTypes: ChipGroup = itemView.findViewById(R.id.chipGroupJobTypes)
        val chipGroupCategories: ChipGroup = itemView.findViewById(R.id.chipGroupCategories)
        val chipGroupSubCategories: ChipGroup = itemView.findViewById(R.id.chipGroupSubCategories)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableJobListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_job_list, parent, false)
        return AvailableJobListViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvailableJobListViewHolder, position: Int) {
        val job = jobList[position]
        TranslationHelper.initialize(context)
        CoroutineScope(Dispatchers.Main).launch {
            val targetLan = context.languageName
            holder.title.text = TranslationHelper.translateText(job.title ?: "", targetLan)
            holder.companyName.text = TranslationHelper.translateText(job.company_name ?: "", targetLan)
            holder.location.text = TranslationHelper.translateText(job.location ?: "", targetLan)
            holder.tvPostDates.text = TranslationHelper.translateText(formatDate(job.created_date) ?: "", targetLan)


          //  holder.tvPostDates.text = formatDate(job.created_date)
            //holder.salary.text = formatSalary(job.salary)+" "+job.salary_type
            holder.salary.text = formatSalary(job.salary, job.salary_amount) + " " +TranslationHelper.translateText(job.salary_type ?: "", targetLan)

            if (job.save_status.equals("saved")) {
                holder.btnSave.setImageResource(R.drawable.bookmark)
                holder.btnSave.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary))
            }

            if (job.apply_status.equals("applied")) {
                holder.tvApplyStatus.visibility = View.VISIBLE
                holder.tvApplyStatus.text = TranslationHelper.translateText("Applied job on "+job.apply_date ?: "", targetLan)
                holder.tvApply.text = holder.itemView.context.getString(R.string.view)
            }
        }


        holder.btnSave.setOnClickListener {
            savedJob(job.id)
        }


        holder.tvApply.setOnClickListener {
            if (context.isuserlgin) {
                val intent = Intent(context, JobsDetailsActivity::class.java).apply {
                    putExtra("Job_id", job.id)
                }
                context.startActivity(intent)
            }else{
                Toast.makeText(context,"Please Login App", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, LoginActivity::class.java))
                ( context as Activity).finish()
            }
        }



        holder.chipGroupJobTypes.removeAllViews() // Clear previous chips if any
        holder.chipGroupCategories.removeAllViews()
        holder.chipGroupSubCategories.removeAllViews()

        job.job_types?.forEach { jobType ->
            val chip = Chip(context).apply {
                CoroutineScope(Dispatchers.Main).launch {
                    val targetLan = context.languageName
                    text = TranslationHelper.translateText(jobType.job_type_names ?: "", targetLan)
                }
            }
            holder.chipGroupJobTypes.addView(chip)
        }

        job.categories?.forEach { category ->
            val chip = Chip(context).apply {
               /* CoroutineScope(Dispatchers.Main).launch {
                    val targetLan = context.languageName
                    text  = TranslationHelper.translateText("${category.category_names ?: ""}  ${category.vacancies ?: ""}", targetLan)

                }*/
                CoroutineScope(Dispatchers.Main).launch {
                    val targetLan = context.languageName
                    text = TranslationHelper.translateText("${category.category_names ?: ""}  👤 ${category.vacancies ?: ""}", targetLan)

                }

                // Set icon from drawable
               // chipIcon = ContextCompat.getDrawable(context, R.drawable.baseline_person_24_1) // use your user icon here
               // isChipIconVisible = true
                //chipIconSize = 48f  // optional: set icon size
            }
            chip.setChipBackgroundColorResource(R.color.light_blue_chip)
            holder.chipGroupCategories.addView(chip)
        }



        holder.chipGroupSubCategories.visibility = View.GONE
        if(!job.sub_categories[0].sub_category_names.isNullOrEmpty()){
            holder.chipGroupSubCategories.visibility = View.VISIBLE
            // Add SubCategory chips
            holder.chipGroupSubCategories.removeAllViews()
            job.sub_categories.forEach { subCategory ->
                val chip = Chip(context).apply {
                    CoroutineScope(Dispatchers.Main).launch {
                        val targetLan = context.languageName
                        text = TranslationHelper.translateText(subCategory.sub_category_names ?: "", targetLan)
                    }

                }

                holder.chipGroupSubCategories.addView(chip)
            }
        }


    }

    fun hideFirstSixDigits(number: String): String {
        return if (number.length > 6) {
            "******" + number.substring(number.length - 4)
        } else {
            number
        }
    }

    fun formatDate(createdDate: String): String {
        val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("d MMMM", Locale.getDefault())

        val date = inputFormat.parse(createdDate) // Convert string to Date
        return outputFormat.format(date!!) // Convert Date to desired format
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
