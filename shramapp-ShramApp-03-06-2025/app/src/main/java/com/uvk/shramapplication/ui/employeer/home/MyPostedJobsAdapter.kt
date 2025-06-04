package com.uvk.shramapplication.ui.employeer.home


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uvk.shramapplication.R
import com.uvk.shramapplication.ui.employeer.home.jobPostList.details.PostedJobDetailsActivity
import com.uvk.shramapplication.ui.employeer.response.EmployeerData
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.uvk.shramapplication.ui.login.LoginActivity
import com.mahindra.serviceengineer.savedata.isuserlgin
import com.mahindra.serviceengineer.savedata.languageName
import com.uvk.shramapplication.helper.TranslationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MyPostedJobsAdapter(val context: Context,
    private val itemList: List<EmployeerData>
) : RecyclerView.Adapter<MyPostedJobsAdapter.ImageViewHolder>() {

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val btnSave: ImageView = view.findViewById(R.id.btnSave)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvLoc: TextView = view.findViewById(R.id.tvLoc)
        val tvPostDate: TextView = view.findViewById(R.id.tvPostDate)
        val llDate: LinearLayoutCompat = view.findViewById(R.id.llDate)
        val tvDesc: TextView = view.findViewById(R.id.tvDesc)
        val tvSalary: TextView = view.findViewById(R.id.tvSalary)
       // val tvApply: TextView = view.findViewById(R.id.tvApply)
       val chipGroupJobTypes: ChipGroup = view.findViewById(R.id.chipGroupJobTypes)
        val chipGroupCategories: ChipGroup = view.findViewById(R.id.chipGroupCategories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_got_job, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val empData = itemList[position]
        TranslationHelper.initialize(context)

        holder.btnSave.visibility = View.GONE
        holder.llDate.visibility = View.VISIBLE


        // Load Image with Glide (Ensure you have Glide dependency)
        Glide.with(holder.imageView.context)
            .load(empData.image)
            .placeholder(R.drawable.worker)
            .into(holder.imageView)

        CoroutineScope(Dispatchers.Main).launch {
            val targetLan = context.languageName
            holder.tvTitle.text = TranslationHelper.translateText(empData.title ?: "", targetLan)
            holder.tvDesc.text = TranslationHelper.translateText(empData.description  ?: "", targetLan)
            holder.tvLoc.text =  TranslationHelper.translateText(empData.location  ?: "", targetLan)
            holder.tvPostDate.text = "Posted On " +  TranslationHelper.translateText(empData.created_at  ?: "", targetLan)
            holder.tvSalary.text =
                formatSalary(empData.salary, empData.salary_amount) + " " +  TranslationHelper.translateText(empData.salary_type  ?: "", targetLan)
        }


        // Clear previous chip views
        holder.chipGroupJobTypes.removeAllViews()
        holder.chipGroupCategories.removeAllViews()

        // Add Job Types Chips
        empData.job_types.forEach { jobType ->
            val chip = Chip(holder.chipGroupJobTypes.context).apply {
                CoroutineScope(Dispatchers.Main).launch {
                    val targetLan = context.languageName
                    text = TranslationHelper.translateText(
                        jobType.job_type_names ?: "",
                        targetLan
                    ) // Set correct text
                    isCheckable = false
                    isClickable = false
                    setChipBackgroundColorResource(R.color.light_sky)
                }
            }
            holder.chipGroupJobTypes.addView(chip)
        }

        // Add Categories Chips
        empData.categories.forEach { category ->
            val chip = Chip(holder.chipGroupCategories.context).apply {
                CoroutineScope(Dispatchers.Main).launch {
                    val targetLan = context.languageName
                    text = TranslationHelper.translateText(
                        category.category_names ?: "",
                        targetLan
                    )
                    isCheckable = false
                    isClickable = false
                    setChipBackgroundColorResource(R.color.light_sky)
                }
            }
            holder.chipGroupCategories.addView(chip)
        }

        holder.itemView.setOnClickListener {
            if (context.isuserlgin) {
                val intent = Intent(context, PostedJobDetailsActivity::class.java).apply {
                    putExtra("Job_id", empData.id.toString())
                }
                context.startActivity(intent)
            }else{
                Toast.makeText(context,"Please Login App", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, LoginActivity::class.java))
                ( context as Activity).finish()
            }
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


