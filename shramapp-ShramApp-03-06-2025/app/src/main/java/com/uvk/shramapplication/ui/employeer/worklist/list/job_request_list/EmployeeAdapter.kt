package com.uvk.shramapplication.ui.employeer.worklist.list.job_request_list

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uvk.shramapplication.R
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.roleId
import com.mahindra.serviceengineer.savedata.userid
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.helper.CommenViewModel
import com.uvk.shramapplication.helper.TranslationHelper
import com.uvk.shramapplication.ui.chat.ChatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EmployeeAdapter(
    val context: Context,
    private var empList: List<EmployeeData>,
    val callingData: (List<Int>, Int) -> Unit,
    val acceptJobEmp: (Int, Int) -> Unit,
    val callDeductedAmount: (String, String,String,String) -> Unit,
    val getcallDeductStatus: (String, String,String, String,String) -> Unit,
) :
    RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder>() {


    inner class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
       /* val empName: TextView = itemView.findViewById(R.id.tvEmpName)
        val empMobile: TextView = itemView.findViewById(R.id.tvEmpMobile)
        val empStatus: TextView = itemView.findViewById(R.id.tvEmpStatus)*/

          val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
         val tvLoc: TextView = itemView.findViewById(R.id.tvLoc)
         val tvDesc: TextView = itemView.findViewById(R.id.tvDesc)
         val tvDate: TextView = itemView.findViewById(R.id.tvDate)
         val tvSelectStatus: TextView = itemView.findViewById(R.id.tvSelectStatus)
         val btnCall: TextView = itemView.findViewById(R.id.btnCall)
         val btnSelectEmployee: TextView = itemView.findViewById(R.id.tvSelectEmployee)
         val imageView: ImageView = itemView.findViewById(R.id.imageView)
         val btnDetails: ImageView = itemView.findViewById(R.id.btnDetails)
         val btnChat: TextView = itemView.findViewById(R.id.btnChat)
         val llStatus: LinearLayoutCompat = itemView.findViewById(R.id.llStatus)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_job_request, parent, false)
        return EmployeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employee = empList[position]
        TranslationHelper.initialize(context)
        CoroutineScope(Dispatchers.Main).launch {
            val targetLan = context.languageName
            holder.tvTitle.text = TranslationHelper.translateText(employee.user_name ?: "", targetLan)
            holder.tvDesc.text =  TranslationHelper.translateText(employee.designation ?: "", targetLan)
            holder.tvLoc.text =  TranslationHelper.translateText(employee.address ?: "", targetLan)
            holder.btnSelectEmployee.text =  TranslationHelper.translateText("Select Employee", targetLan)

            val status = employee.employer_select_status
            if (!status.isNullOrEmpty()){
                holder.btnSelectEmployee.visibility = View.GONE
                holder.llStatus.visibility = View.VISIBLE
                holder.tvSelectStatus.text =
                    TranslationHelper.translateText(status ?: "", targetLan)

                // Set color based on status
                if (status.equals("Accepted", ignoreCase = true)) {
                    holder.tvSelectStatus.setTextColor(ContextCompat.getColor(context, R.color.green))
                } else if (status.equals("Rejected", ignoreCase = true)) {
                    holder.tvSelectStatus.setTextColor(ContextCompat.getColor(context, R.color.red))
                }
            }else{
                holder.btnSelectEmployee.visibility = View.VISIBLE
                holder.llStatus.visibility = View.GONE
            }
        }

        Glide.with(holder.imageView)
            .load(employee.profile_image)
            .placeholder(R.drawable.user)
            .into(holder.imageView)


        if(!employee.apply_date.isNullOrEmpty()){

            CoroutineScope(Dispatchers.Main).launch {
                val targetLan = context.languageName
                holder.tvDate.text = TranslationHelper.translateText("Applied On " + employee.apply_date ?: "", targetLan)

            }

        }else{
            CoroutineScope(Dispatchers.Main).launch {
                val targetLan = context.languageName
                holder.tvDate.text = TranslationHelper.translateText("Requested On " + employee.job_request_date ?: "", targetLan)
                holder.tvDate.setTextColor(ContextCompat.getColor(context, R.color.green))
            }

        }

        holder.btnChat.setOnClickListener {
          /*  val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("user_id", employee.user_id.toString())
            intent.putExtra("name", employee.user_name)
            intent.putExtra("profile_img", employee.profile_image)
            context.startActivity(intent)*/
            val nextAction = "chat"

            getcallDeductStatus(context.userid, employee.user_id.toString(),employee.user_name,employee.profile_image,nextAction)
        }

        holder.btnDetails.setOnClickListener {

            val userId = employee.user_id.toString()
            val applyId = employee.apply_id
            val applyStatus = employee.apply_status
            val requestId = employee.id

            Log.e("tag","clickckkk $userId $applyId $applyStatus")

            val intent = Intent(context, JobRequestDetailsActivity::class.java)
            intent.putExtra("user_id",userId )
            intent.putExtra("apply_job_id", applyId)
            intent.putExtra("apply_status", applyStatus )
            intent.putExtra("request_job_id", requestId )
            context.startActivity(intent)

        }

        holder.btnSelectEmployee.setOnClickListener {
            val builder = androidx.appcompat.app.AlertDialog.Builder(context)
            CoroutineScope(Dispatchers.Main).launch {
                val title = TranslationHelper.translateText(
                    "Are you sure you want to accept this employee?",
                    context.languageName
                )

                builder.setMessage(title)

                builder.setPositiveButton(R.string.yes) { dialog, _ ->

                    acceptJobEmp(employee.user_id, employee.job_id!!)

                    dialog.dismiss()
                }

                builder.setNegativeButton(R.string.no) { dialog, _ ->
                    dialog.dismiss()
                }

                val alertDialog = builder.create()
                alertDialog.show()
            }
        }

         holder.btnCall.setOnClickListener {
             TranslationHelper.initialize(context)
             CoroutineScope(Dispatchers.Main).launch {
                 val mobile_no = employee.mobile_no
                 val builder = AlertDialog.Builder(context)
                // builder.setMessage(context.getString(R.string.call_msg))
                 if (context.roleId == "2") {
                     val msg = TranslationHelper.translateText(
                         "You are about to 1st time call the employee. ₹5 will be deducted from your wallet. Please confirm to proceed.",
                         context.languageName
                     )
                     builder.setMessage(msg)
                 }else{
                     builder.setMessage(context.getString(R.string.call_msg))
                 }

                 builder.setPositiveButton(context.getString(R.string.yes)) { dialog, _ ->
                     // jobApplyCall(token!!,jobId!!,userid!!)

                     callingData(listOf(employee.user_id), employee.job_id!!)


                     Log.e("tag", "eeee Emp Id :${employee.user_id} ")
                     Log.e("tag", "job Id :${employee.job_id!!} ")

                     if (context.roleId == "2") {
                        val nextAction = "call"

                         callDeductedAmount(context.userid, employee.user_id.toString(), mobile_no,nextAction)
                     } else {
                         val dialIntent = Intent(Intent.ACTION_DIAL)
                         dialIntent.data = Uri.parse("tel:$mobile_no")
                         context.startActivity(dialIntent)
                     }

                     dialog.dismiss()
                 }

                 builder.setNegativeButton(context.getString(R.string.no)) { dialog, _ ->
                     dialog.dismiss()
                 }

                 val alertDialog = builder.create()
                 alertDialog.show()
             }
         }
    }



    override fun getItemCount(): Int = empList.size

    fun updateList(newList: List<EmployeeData>) {
        empList = newList
        notifyDataSetChanged()
    }
}
