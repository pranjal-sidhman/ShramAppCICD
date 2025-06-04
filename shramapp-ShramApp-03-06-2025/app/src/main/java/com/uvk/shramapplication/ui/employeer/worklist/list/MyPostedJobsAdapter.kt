package com.uvk.shramapplication.ui.employeer.worklist.list

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mahindra.serviceengineer.savedata.languageName
import com.uvk.shramapplication.R
import com.uvk.shramapplication.helper.TranslationHelper
import com.uvk.shramapplication.ui.employeer.home.jobPostList.details.PostedJobDetailsActivity
import com.uvk.shramapplication.ui.employeer.response.EmployeerData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyPostedJobsAdapter (
    val context: Context,
    private val jobList: List<EmployeerData>
) : RecyclerView.Adapter<MyPostedJobsAdapter.PostedJobListViewHolder>() {

    class PostedJobListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        //  val tvLoc: TextView = view.findViewById(R.id.tvLoc)
        val tvDesc: TextView = view.findViewById(R.id.tvDesc)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val btnDetails: ImageView = view.findViewById(R.id.btnDetails)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostedJobListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_posted_job, parent, false)
        return PostedJobListViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostedJobListViewHolder, position: Int) {
        val job = jobList[position]
        TranslationHelper.initialize(context)
        CoroutineScope(Dispatchers.Main).launch {
            val targetLan = context.languageName
            holder.tvTitle.text = TranslationHelper.translateText(job.title ?: "", targetLan)
            holder.tvDesc.text =  TranslationHelper.translateText(job.description ?: "", targetLan)
            // holder.tvLoc.text = job.location
            holder.tvDate.text =  TranslationHelper.translateText("Posted On " + job.created_at ?: "", targetLan)
        }
        holder.btnDetails.setOnClickListener {
            val intent = Intent(context, PostedJobDetailsActivity::class.java).apply {
                putExtra("Job_id", job.id.toString())
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = jobList.size
}