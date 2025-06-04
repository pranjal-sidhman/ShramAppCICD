package com.uvk.shramapplication.ui.home.newhome


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uvk.shramapplication.R
import com.google.android.material.chip.ChipGroup
import com.mahindra.serviceengineer.savedata.languageName
import com.uvk.shramapplication.helper.TranslationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GotJobAdapter(
    val context: Context,
    private val itemList: List<Job>
) : RecyclerView.Adapter<GotJobAdapter.ImageViewHolder>() {

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val btnSave: ImageView = view.findViewById(R.id.btnSave)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvLoc: TextView = view.findViewById(R.id.tvLoc)
        val tvDesc: TextView = view.findViewById(R.id.tvDesc)
        val llDate: LinearLayoutCompat = view.findViewById(R.id.llDate)
        val tvSalary: TextView = view.findViewById(R.id.tvSalary)
        //val tvApply: TextView = view.findViewById(R.id.tvApply)
        val chipGroupJobTypes: ChipGroup = view.findViewById(R.id.chipGroupJobTypes)
        val chipGroupCategories: ChipGroup = view.findViewById(R.id.chipGroupCategories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_got_job, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val job = itemList[position]
        TranslationHelper.initialize(context)
      //  holder.imageView.setImageResource(currentItem.image)
        // Load Image with Glide (Ensure you have Glide dependency)
        Glide.with(holder.imageView.context)
            .load(job.profile_image)
            .placeholder(R.drawable.worker)
            .into(holder.imageView)


      //  holder.tvApply.visibility = View.GONE
        holder.llDate.visibility = View.GONE
        holder.chipGroupCategories.visibility = View.GONE
        holder.btnSave.visibility = View.GONE

        CoroutineScope(Dispatchers.Main).launch {
            val targetLan = context.languageName
            holder.tvTitle.text = TranslationHelper.translateText(job.employee_name ?: "", targetLan)
            holder.tvDesc.text = TranslationHelper.translateText(job.designation ?: "", targetLan)
            holder.tvLoc.text = TranslationHelper.translateText(job.address ?: "", targetLan)

            if(!job.salary.isNullOrEmpty()) {
                holder.tvSalary.text =
                    formatSalary(
                        job.salary,
                        job.salary_amount
                    ) + " " + TranslationHelper.translateText(job.salary_type ?: "", targetLan)
            }
        }

        // Clear previous chip views
       /* holder.chipGroupJobTypes.removeAllViews()
        holder.chipGroupCategories.removeAllViews()

        // Add Job Types Chips
        job.job_types.forEach { jobType ->
            val chip = Chip(holder.chipGroupJobTypes.context).apply {
                text = jobType.job_type_names // Set correct text
                isCheckable = false
                isClickable = false
                setChipBackgroundColorResource(R.color.light_sky)
            }
            holder.chipGroupJobTypes.addView(chip)
        }

        // Add Categories Chips
        job.categories.forEach { category ->
            val chip = Chip(holder.chipGroupCategories.context).apply {
                text = category.category_names
                isCheckable = false
                isClickable = false
                setChipBackgroundColorResource(R.color.light_sky)
            }
            holder.chipGroupCategories.addView(chip)
        }*/


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


