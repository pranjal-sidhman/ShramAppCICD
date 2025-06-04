package com.uvk.shramapplication.ui.worklist.list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mahindra.serviceengineer.savedata.isuserlgin
import com.mahindra.serviceengineer.savedata.languageName
import com.uvk.shramapplication.R
import com.uvk.shramapplication.helper.TranslationHelper
import com.uvk.shramapplication.ui.joblist.Job
import com.uvk.shramapplication.ui.login.LoginActivity
import com.uvk.shramapplication.ui.worklist.WorkDetailsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppliedJobListAdapter(
    val context: Context,
    private val jobList: List<Job>
) : RecyclerView.Adapter<AppliedJobListAdapter.ApplyJobListViewHolder>() {

    class ApplyJobListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imageView: ImageView = view.findViewById(R.id.imageView)
        val btnDetails: ImageView = view.findViewById(R.id.btnDetails)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val llLoc: LinearLayoutCompat = view.findViewById(R.id.llLock)
        val tvLoc: TextView = view.findViewById(R.id.tvLoc)
        val tvDesc: TextView = view.findViewById(R.id.tvDesc)
        val tvSalary: TextView = view.findViewById(R.id.tvSalary)
        val tvDate: TextView = view.findViewById(R.id.tvDate)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplyJobListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_applied_job, parent, false)
        return ApplyJobListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApplyJobListViewHolder, position: Int) {
        val job = jobList[position]
        TranslationHelper.initialize(context)
        CoroutineScope(Dispatchers.Main).launch {
            val targetLan = context.languageName
            holder.tvTitle.text = TranslationHelper.translateText(job.title ?: "", targetLan)
            holder.tvDesc.text = TranslationHelper.translateText(job.description ?: "", targetLan)
            holder.llLoc.visibility = View.GONE
            holder.tvLoc.text = TranslationHelper.translateText(job.location ?: "", targetLan)
            holder.tvDate.text =  TranslationHelper.translateText("Applied On "+job.apply_date  ?: "", targetLan)
            holder.tvSalary.text = formatSalary(job.salary, job.salary_amount) + " " +  TranslationHelper.translateText(job.salary_type ?: "", targetLan)
        }


        Glide.with(holder.imageView)
            .load(job.image)
            .placeholder(R.drawable.no_data_found)
            .into(holder.imageView)

        holder.btnDetails.setOnClickListener {
            if (context.isuserlgin) {
                val intent = Intent(context, WorkDetailsActivity::class.java).apply {
                    putExtra("apply_job_id", job.id)
                    putExtra("Job_ids", job.job_id)
                    Log.e("apply Adapter","apply_job_id : ${job.id} ")
                }
                context.startActivity(intent)
            }
        }
    }


    override fun getItemCount(): Int = jobList.size

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
}