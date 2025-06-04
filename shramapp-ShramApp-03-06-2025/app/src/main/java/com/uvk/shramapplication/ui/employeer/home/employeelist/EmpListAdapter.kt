package com.uvk.shramapplication.ui.employeer.home.employeelist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uvk.shramapplication.R
import com.uvk.shramapplication.ui.employeer.response.EmployeerData
import com.uvk.shramapplication.ui.login.LoginActivity
import com.mahindra.serviceengineer.savedata.isuserlgin
import com.mahindra.serviceengineer.savedata.languageName
import com.uvk.shramapplication.helper.TranslationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EmpListAdapter(
    val context: Context,
    private var empList: List<EmployeerData>,
    private var selectedEmployeeIds: MutableList<Int>, // <-- Use mutable list to retain selections
    private val checkEmp: (List<Int>) -> Unit // Callback function to update selected employees
) : RecyclerView.Adapter<EmpListAdapter.EmpListViewHolder>() {

   // private val selectedEmployeeIds = mutableListOf<Int>() // Store selected IDs

    inner class EmpListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val companyName: TextView = itemView.findViewById(R.id.tvDesc)
        val location: TextView = itemView.findViewById(R.id.tvLoc)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvSelectedStatus: TextView = itemView.findViewById(R.id.tvSelectedStatus)
        val btnCheck: CheckBox = itemView.findViewById(R.id.btnCheck)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_emp_list, parent, false)
        return EmpListViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmpListViewHolder, position: Int) {
        val list = empList[position]
        TranslationHelper.initialize(context)
        CoroutineScope(Dispatchers.Main).launch {
            val targetLan = context.languageName
            holder.title.text =
                TranslationHelper.translateText(list.designation ?: "", targetLan)

            holder.title.text =  TranslationHelper.translateText(list.designation ?: "", targetLan)
            holder.companyName.text =  TranslationHelper.translateText(list.user_name ?: "", targetLan)
            holder.location.text =  TranslationHelper.translateText(list.address ?: "", targetLan)
        }

        if (list.available_status.equals("available")) {
            holder.tvStatus.text = "Available"
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
        } else {
            holder.tvStatus.text = "Not Available"
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
        }

        if (list.selected_status.equals("selected")) {
            holder.btnCheck.visibility = View.GONE
            holder.tvSelectedStatus.text = "Selected "
            holder.tvSelectedStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.md_deep_orange_400))
        } else {
            holder.btnCheck.visibility = View.VISIBLE
        }

        Glide.with(holder.imageView.context)
            .load(list.profile_image)
            .placeholder(R.drawable.worker)
            .into(holder.imageView)

        holder.btnCheck.setOnCheckedChangeListener(null)
        holder.btnCheck.isChecked = selectedEmployeeIds.contains(list.user_id)
        holder.btnCheck.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!selectedEmployeeIds.contains(list.user_id)) {
                    selectedEmployeeIds.add(list.user_id)
                }
            } else {
                selectedEmployeeIds.remove(list.user_id)
            }
            checkEmp(selectedEmployeeIds) // Update selection list
        }


        holder.itemView.setOnClickListener {
            Log.e("tag","EmplistAdapter userId : ${list.user_id}")
            if (context.isuserlgin) {
                val intent = Intent(context, EmployeeDetailsActivity::class.java).apply {
                    putExtra("emp_id", list.user_id.toString())
                }
                context.startActivity(intent)
            }else{
                Toast.makeText(context,"Please Login App", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, LoginActivity::class.java))
                ( context as Activity).finish()
            }
        }


    }

    override fun getItemCount(): Int = empList.size


    fun updateSelectedEmpIds(newSelectedIds: List<Int>) {
        selectedEmployeeIds = newSelectedIds.toMutableList()
        notifyDataSetChanged()
    }

    fun updateList(newList: List<EmployeerData>) {
        empList = newList
        notifyDataSetChanged()
    }


}
