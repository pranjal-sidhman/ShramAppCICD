package com.uvk.shramapplication.ui.worklist.list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uvk.shramapplication.R
import com.uvk.shramapplication.ui.joblist.Job
import com.uvk.shramapplication.ui.login.LoginActivity
import com.uvk.shramapplication.ui.worklist.WorkDetailsActivity
import com.mahindra.serviceengineer.savedata.isuserlgin
import com.mahindra.serviceengineer.savedata.languageName
import com.uvk.shramapplication.helper.TranslationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JobOffersListAdapter(
    val context: Context,
    private val jobList: List<Job>,
    val acceptRequest :(String) -> Unit,
    val rejectRequest :(String) -> Unit
) : RecyclerView.Adapter<JobOffersListAdapter.RequestJobListViewHolder>() {

    class RequestJobListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvLoc: TextView = view.findViewById(R.id.tvLoc)
        val tvDesc: TextView = view.findViewById(R.id.tvDesc)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val btnDetails: ImageView = view.findViewById(R.id.btnDetails)
        val btnAccept: TextView = view.findViewById(R.id.btnAccept)
        val btnReject: TextView = view.findViewById(R.id.btnReject)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestJobListViewHolder {
        val view = LayoutInflater.from(parent.context)
          //  .inflate(R.layout.item_job_offers, parent, false)
            .inflate(R.layout.item_job_offers_new, parent, false)
        return RequestJobListViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestJobListViewHolder, position: Int) {
        val job = jobList[position]

        TranslationHelper.initialize(context)
        CoroutineScope(Dispatchers.Main).launch {
            val targetLan = context.languageName
            holder.tvTitle.text = TranslationHelper.translateText(job.title ?: "", targetLan)
            holder.tvDesc.text = TranslationHelper.translateText(job.description ?: "", targetLan)
            holder.tvLoc.text = TranslationHelper.translateText(job.location ?: "", targetLan)
            holder.tvDate.text =    TranslationHelper.translateText("Requested On "+job.job_request_date  ?: "", targetLan)

        }
        holder.btnAccept.setOnClickListener {
            acceptRequest(job.job_id)
        }

        Glide.with(holder.imageView)
            .load(job.image)
            .placeholder(R.drawable.no_data_found)
            .into(holder.imageView)

        holder.btnDetails.setOnClickListener {
            if (context.isuserlgin) {
                val intent = Intent(context, WorkDetailsActivity::class.java).apply {
                    putExtra("Job_id", job.job_id)
                }
                context.startActivity(intent)
            }else{
                Toast.makeText(context,"Please Login App", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, LoginActivity::class.java))
                ( context as Activity).finish()
            }
        }

        holder.btnReject.setOnClickListener {

            val builder = AlertDialog.Builder(context)
           //  builder.setTitle(context.getString(R.string.reject_offer_title))
            builder.setMessage(context.getString(R.string.reject_offer_msg))

            builder.setPositiveButton(R.string.yes) { dialog, _ ->

                rejectRequest(job.job_id)

                dialog.dismiss()
            }

            builder.setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog = builder.create()
            alertDialog.show()
        }

        if(!job.job_request_status.isNullOrEmpty()){
            holder.tvStatus.visibility = View.VISIBLE
        }

        if(job.job_request_status.equals("accepted") ){
            holder.tvStatus.text = "Accepted Request"
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.green))

        }

        if( job.job_request_status.equals("rejected")){
            holder.tvStatus.text = "Rejected Request"
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.red))
        }



        if(job.job_request_status.equals("accepted") || job.job_request_status.equals("rejected")){
            holder.btnAccept.visibility = View.GONE
            holder.btnReject.visibility = View.GONE
        }




    }

    override fun getItemCount(): Int = jobList.size
}