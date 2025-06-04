package com.uvk.shramapplication.ui.home.newhome.gotjoblist


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mahindra.serviceengineer.savedata.languageName
import com.uvk.shramapplication.R
import com.uvk.shramapplication.helper.TranslationHelper
import com.uvk.shramapplication.ui.home.newhome.Job
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class EmployeeGotJobsAdapter(
    val context : Context,
    private val itemList: List<Job>
) : RecyclerView.Adapter<EmployeeGotJobsAdapter.ImageViewHolder>() {

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvLoc: TextView = view.findViewById(R.id.tvLoc)
        val tvCompanyName: TextView = view.findViewById(R.id.tvCompanyName)
        val tvDesc: TextView = view.findViewById(R.id.tvDesc)
        val tvSalary: TextView = view.findViewById(R.id.tvSalary)
        //val tvApply: TextView = view.findViewById(R.id.tvApply)
    //    val chipGroupJobTypes: ChipGroup = view.findViewById(R.id.chipGroupJobTypes)
      //  val chipGroupCategories: ChipGroup = view.findViewById(R.id.chipGroupCategories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_employee_got_job, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val job = itemList[position]

        // Load Image with Glide (Ensure you have Glide dependency)
        Glide.with(holder.imageView.context)
            .load(job.profile_image)
            .placeholder(R.drawable.worker)
            .into(holder.imageView)


        TranslationHelper.initialize(context)
        CoroutineScope(Dispatchers.Main).launch {
            val targetLan = context.languageName
            holder.tvTitle.text = TranslationHelper.translateText(job.employee_name ?: "", targetLan)
            holder.tvDesc.text = TranslationHelper.translateText(job.designation ?: "", targetLan)
            holder.tvCompanyName.text = TranslationHelper.translateText(job.company_name ?: "", targetLan)
            holder.tvLoc.text = TranslationHelper.translateText(job.address ?: "", targetLan)

            holder.tvSalary.text =
                formatSalary(job.salary, job.salary_amount) + " " +TranslationHelper.translateText(job.salary_type ?: "", targetLan)
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

    override fun getItemCount(): Int = itemList.size
}


