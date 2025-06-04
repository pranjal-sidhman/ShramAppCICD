package com.uvk.shramapplication.ui.employeer.home.jobPostList

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.uvk.shramapplication.R
import com.uvk.shramapplication.ui.employeer.home.jobPostList.details.PostedJobDetailsActivity
import com.uvk.shramapplication.ui.employeer.response.EmployeerData
import com.uvk.shramapplication.ui.login.LoginActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.mahindra.serviceengineer.savedata.isuserlgin
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.userid
import com.uvk.shramapplication.helper.TranslationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostedJobListAdapter(
    val context: Context,
    private val jobList: List<EmployeerData>,
    val deletePost: (String, String) -> Unit,
) :
    RecyclerView.Adapter<PostedJobListAdapter.PostedJobListViewHolder>() {

    private var bottomSheetDialog: BottomSheetDialog? = null

    inner class PostedJobListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val companyName: TextView = itemView.findViewById(R.id.tvDesc)
        val salary: TextView = itemView.findViewById(R.id.tvSalary)
        val location: TextView = itemView.findViewById(R.id.tvLoc)
        val tvPostDate: TextView = itemView.findViewById(R.id.tvPostDate)
        val btnData: ImageView = itemView.findViewById(R.id.btnData)
        val chipGroupJobTypes: ChipGroup = itemView.findViewById(R.id.chipGroupJobTypes)
        val chipGroupCategories: ChipGroup = itemView.findViewById(R.id.chipGroupCategories)
        val chipGroupSubCategories: ChipGroup = itemView.findViewById(R.id.chipGroupSubCategories)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostedJobListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_posted_job_list, parent, false)
        return PostedJobListViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostedJobListViewHolder, position: Int) {
        val job = jobList[position]
        TranslationHelper.initialize(context)
        CoroutineScope(Dispatchers.Main).launch {
            val targetLan = context.languageName
            holder.title.text =
                TranslationHelper.translateText(job.title ?: "", targetLan)


            holder.companyName.text =
                TranslationHelper.translateText(job.company_name ?: "", targetLan)
            holder.location.text = TranslationHelper.translateText(job.location ?: "", targetLan)
            holder.salary.text =
                formatSalary(job.salary, job.salary_amount) + " " + TranslationHelper.translateText(
                    job.salary_type ?: "",
                    targetLan
                )

            holder.tvPostDate.text =
                "Posted On " + TranslationHelper.translateText(job.created_at ?: "", targetLan)

        }

        Log.e("tag", "Posted job title : ${job.title}")

        holder.btnData.setOnClickListener {
            showBottomSheetDialog(context.userid, job.id.toString())
        }

        // Add JobType chips
        holder.chipGroupJobTypes.removeAllViews() // Clear previous chips if any
        job.job_types.forEach { jobType ->
            val chip = Chip(context).apply {
                CoroutineScope(Dispatchers.Main).launch {
                    val targetLan = context.languageName
                    text = TranslationHelper.translateText(jobType.job_type_names ?: "", targetLan)
                }

            }
            holder.chipGroupJobTypes.addView(chip)
        }

        // Add Category chips
        holder.chipGroupCategories.removeAllViews()
        job.categories.forEach { category ->
            val chip = Chip(context).apply {
                CoroutineScope(Dispatchers.Main).launch {
                    val targetLan = context.languageName
                    text =
                        TranslationHelper.translateText(category.category_names ?: "", targetLan)
                }
            }
            chip.setChipBackgroundColorResource(R.color.light_blue_chip)
            holder.chipGroupCategories.addView(chip)
        }

        // Add SubCategory chips
        holder.chipGroupSubCategories.removeAllViews()
        job.sub_categories.forEach { subCategory ->
            val chip = Chip(context).apply {
                CoroutineScope(Dispatchers.Main).launch {
                    val targetLan = context.languageName
                    text = TranslationHelper.translateText(
                        subCategory.sub_category_names ?: "",
                        targetLan
                    )
                }
            }
            if (subCategory.sub_category_names.isNullOrEmpty()) {
                holder.chipGroupSubCategories.visibility = View.GONE
            } else {
                holder.chipGroupSubCategories.addView(chip)
            }

        }

        holder.itemView.setOnClickListener {
            if (context.isuserlgin) {
                val intent = Intent(context, PostedJobDetailsActivity::class.java).apply {
                    putExtra("Job_id", job.id.toString())
                }
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Please Login App", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, LoginActivity::class.java))
                (context as Activity).finish()
            }
        }

    }

    private fun showBottomSheetDialog(userid: String, jobId: String) {
        bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)

        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.bottom_sheet_delete_comment, null)

        bottomSheetDialog?.setContentView(dialogView)


        val deleteComment = dialogView.findViewById<LinearLayout>(R.id.llDeleteComment)
        val tvMsg = dialogView.findViewById<TextView>(R.id.tvMsg)
        tvMsg!!.text = context.getString(R.string.delete_post)

        deleteComment!!.setOnClickListener {

            val builder = AlertDialog.Builder(context)
            // builder.setTitle("Call Confirmation")
            builder.setMessage(context.getString(R.string.delete_job_post_msg))

            builder.setPositiveButton(R.string.yes) { dialog, _ ->

                deletePost(userid, jobId)
                dialog.dismiss()
                bottomSheetDialog?.dismiss()
            }

            builder.setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
                bottomSheetDialog?.dismiss()
            }

            val alertDialog = builder.create()
            alertDialog.show()
        }

        // Show the bottom sheet dialog
        bottomSheetDialog?.show()
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
