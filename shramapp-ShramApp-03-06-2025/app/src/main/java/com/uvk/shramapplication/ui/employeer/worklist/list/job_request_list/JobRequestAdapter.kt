package com.uvk.shramapplication.ui.employeer.worklist.list.job_request_list

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.userid
import com.uvk.shramapplication.R
import com.uvk.shramapplication.helper.TranslationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JobRequestAdapter (
    val context: Context,
    private var jobList: List<JobItem>,
    val callingsData: (List<Int>, Int) -> Unit,
    val selectedEmp: (Int, Int) -> Unit,
    val deductedAmount: (String, String,String,String) -> Unit,
    val deductionStatus: (String,String,String,String,String) -> Unit
) : RecyclerView.Adapter<JobRequestAdapter.PostedJobListViewHolder>() {

    class PostedJobListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvJobTitle: TextView = view.findViewById(R.id.tvJobTitle)
        val empRecyclerView: RecyclerView = itemView.findViewById(R.id.rvEmpList)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostedJobListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_job_request1, parent, false)
        return PostedJobListViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostedJobListViewHolder, position: Int) {
        val job = jobList[position]
        TranslationHelper.initialize(context)
        CoroutineScope(Dispatchers.Main).launch {
            val targetLan = context.languageName
            holder.tvJobTitle.text = TranslationHelper.translateText(job.job_title ?: "", targetLan)
        }


        // Setup Employee RecyclerView
        val employeeAdapter = EmployeeAdapter(context,job.empdata,::callingData,::selectEmp ,::callDeductedAmount,::callDeductionStatus)
        holder.empRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = employeeAdapter
        }


    }

    override fun getItemCount(): Int = jobList.size

    fun updateList(newList: List<JobItem>) {
        jobList = newList
        notifyDataSetChanged()
    }

    fun callingData(employeeId: List<Int>, jobId: Int) {
        Log.e("tag","jjj Emp Id :${employeeId} ")
        Log.e("tag","job Id :${jobId} ")

        callingsData( employeeId, jobId)
    }

    fun selectEmp(employeeId: Int, jobId: Int) {
        Log.e("tag","Select Emp Id :${employeeId} ")
        Log.e("tag","Select job  Id :${jobId} ")

        selectedEmp( employeeId, jobId)
    }
    fun callDeductedAmount(employer_id: String, employee_id: String, mobile_no: String,nextAction : String) {

        deductedAmount(employer_id,employee_id,mobile_no,nextAction)
    }

    fun callDeductionStatus(employer_id: String, employee_id: String,userName : String,profileImg : String,nextAction : String) {

        deductionStatus(employer_id,employee_id,userName,profileImg,nextAction)
    }

}






