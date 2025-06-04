package com.uvk.shramapplication.ui.jobgiver

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
import com.uvk.shramapplication.ui.joblist.JobDetailsActivity
import com.uvk.shramapplication.ui.joblist.MidWorkListModel
import com.uvk.shramapplication.ui.login.LoginActivity
import com.mahindra.serviceengineer.savedata.isuserlgin

class JobGiverListAdapter(val context: Context, private val jobList: List<MidWorkListModel>):
RecyclerView.Adapter<JobGiverListAdapter.JobListViewHolder>() {


    inner class JobListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvDesignation)
        val companyName: TextView = itemView.findViewById(R.id.tvName)
        val salary: TextView = itemView.findViewById(R.id.tvSalary)
        val location: TextView = itemView.findViewById(R.id.tvLoc)
        val job_type: TextView = itemView.findViewById(R.id.tvvaca)
      //  val tvqualification: TextView = itemView.findViewById(R.id.tvqualification)
       // val tvCall: TextView = itemView.findViewById(R.id.tvCall)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_job_giver, parent, false)
        return JobListViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobListViewHolder, position: Int) {
        val item = jobList[position]
        holder.title.text = item.title
        holder.companyName.text = item.companyName
        holder.salary.text = item.salary
        holder.location.text = item.location
        holder.job_type.text = item.time
       // holder.tvqualification.text = hideFirstSixDigits(item.phoneNumber)


       /* holder.tvCall.setOnClickListener {
            context.startActivity(Intent(context, SignUpActivity::class.java))
        }*/

        holder.itemView.setOnClickListener {
           // context.startActivity(Intent(context, JobDetailsActivity::class.java))

            if (context.isuserlgin) {
                context.startActivity(Intent(context, JobDetailsActivity::class.java))
            }else{
                Toast.makeText(context,"Please Login App", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, LoginActivity::class.java))
                ( context as Activity).finish()
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

    override fun getItemCount(): Int = jobList.size
}
