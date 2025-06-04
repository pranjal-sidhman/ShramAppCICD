package com.uvk.shramapplication.ui.home.newhome


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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uvk.shramapplication.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateException
import com.google.cloud.translate.TranslateOptions
import com.uvk.shramapplication.ui.joblist.available_job_list.JobsDetailsActivity
import com.uvk.shramapplication.ui.login.LoginActivity
import com.mahindra.serviceengineer.savedata.isuserlgin
import com.mahindra.serviceengineer.savedata.languageName
import com.uvk.shramapplication.helper.TranslationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AvailableJobAdapter(
    val context: Context,
    private val itemList: List<Job>,
    val savedJob: (String) -> Unit
) : RecyclerView.Adapter<AvailableJobAdapter.ImageViewHolder>() {



    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val btnSave: ImageView = view.findViewById(R.id.btnSave)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvLoc: TextView = view.findViewById(R.id.tvLoc)
        val tvDesc: TextView = view.findViewById(R.id.tvDesc)
        val tvPostDate: TextView = view.findViewById(R.id.tvPostDate)
        val tvSalary: TextView = view.findViewById(R.id.tvSalary)
        val tvRecentAdd: TextView = view.findViewById(R.id.tvRecentAdd)
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
        val job = itemList[position]
        TranslationHelper.initialize(context)
      //  holder.imageView.setImageResource(currentItem.image)


        holder.tvSalary.visibility = View.GONE
        holder.chipGroupCategories.visibility = View.GONE

        if (job.save_status.equals("saved")) {
            holder.btnSave.setImageResource(R.drawable.bookmark)
            holder.btnSave.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary))
        }


        if(job.recently_added) {
            holder.tvRecentAdd.visibility = View.VISIBLE
        }else{
            holder.tvRecentAdd.visibility = View.GONE
        }

        holder.btnSave.setOnClickListener {
            savedJob(job.id)
        }

        holder.itemView.setOnClickListener {
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

        // Load Image with Glide (Ensure you have Glide dependency)
        Glide.with(holder.imageView.context)
            .load(job.image)
            .placeholder(R.drawable.worker)
            .into(holder.imageView)


        CoroutineScope(Dispatchers.Main).launch {
            val targetLan = context.languageName
            holder.tvTitle.text = TranslationHelper.translateText(job.title?: "", targetLan)
            holder.tvDesc.text = TranslationHelper.translateText(job.description?: "", targetLan)
            holder.tvLoc.text = TranslationHelper.translateText(job.location?: "", targetLan)
            holder.tvPostDate.text = TranslationHelper.translateText(job.created_date?: "", targetLan)


            // Clear previous chip views
            holder.chipGroupJobTypes.removeAllViews()
            holder.chipGroupCategories.removeAllViews()

            // Add Job Types Chips
            job.job_types.forEach { jobType ->
                val chip = Chip(holder.chipGroupJobTypes.context).apply {
                    text = TranslationHelper.translateText(jobType.job_type_names?: "", targetLan) // Set correct text
                    isCheckable = false
                    isClickable = false
                    setChipBackgroundColorResource(R.color.light_sky)
                }
                holder.chipGroupJobTypes.addView(chip)
            }

            // Add Categories Chips
            job.categories.forEach { category ->
                val chip = Chip(holder.chipGroupCategories.context).apply {
                    text =  TranslationHelper.translateText(category.category_names?: "", targetLan)
                    isCheckable = false
                    isClickable = false
                    setChipBackgroundColorResource(R.color.light_sky)
                }
                holder.chipGroupCategories.addView(chip)
            }

        }

    }


    override fun getItemCount(): Int = itemList.size
}


