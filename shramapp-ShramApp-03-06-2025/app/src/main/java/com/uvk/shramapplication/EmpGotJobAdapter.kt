package com.uvk.shramapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uvk.shramapplication.ui.employeer.worklist.list.job_request_list.JobItem

class EmpGotJobAdapter(
    val context: Context,
    private var empList: List<JobItem>,
    private var selectedJobIds: MutableList<Int>,
    private val checkEmp: (List<Int>) -> Unit,
    private val getEmployerIds: (List<Int>) -> Unit // <-- Updated signature
) : RecyclerView.Adapter<EmpGotJobAdapter.GotJobViewHolder>() {

    private val employerIds = mutableListOf<Int>() // Store employer IDs

    inner class GotJobViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val companyName: TextView = itemView.findViewById(R.id.tvCompanyName)
        val btnCheck: CheckBox = itemView.findViewById(R.id.btnCheck)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GotJobViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_got_job_dialog, parent, false)
        return GotJobViewHolder(view)
    }

    override fun onBindViewHolder(holder: GotJobViewHolder, position: Int) {
        val list = empList[position]
        holder.title.text = list.job_title
        holder.companyName.text = list.company_name

        holder.btnCheck.setOnCheckedChangeListener(null)
        holder.btnCheck.isChecked = selectedJobIds.contains(list.id)

        holder.btnCheck.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!selectedJobIds.contains(list.id)) {
                    selectedJobIds.add(list.id)
                }
                if (!employerIds.contains(list.employer_id)) {
                    employerIds.add(list.employer_id)
                }
            } else {
                selectedJobIds.remove(list.id)
                employerIds.remove(list.employer_id)
            }

            checkEmp(selectedJobIds) // Update selected employee IDs
            getEmployerIds(employerIds) // Update employer IDs
        }
    }

    override fun getItemCount(): Int = empList.size

    fun updateSelectedEmpIds(newSelectedIds: List<Int>) {
        selectedJobIds = newSelectedIds.toMutableList()
        notifyDataSetChanged()
    }

    fun updateList(newList: List<JobItem>) {
        empList = newList
        notifyDataSetChanged()
    }
}
