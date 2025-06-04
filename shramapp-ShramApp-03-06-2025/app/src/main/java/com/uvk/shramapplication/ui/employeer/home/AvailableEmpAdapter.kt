package com.uvk.shramapplication.ui.employeer.home


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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uvk.shramapplication.R
import com.uvk.shramapplication.ui.employeer.home.employeelist.EmployeeDetailsActivity
import com.uvk.shramapplication.ui.employeer.response.EmployeerData
import com.uvk.shramapplication.ui.login.LoginActivity
import com.mahindra.serviceengineer.savedata.isuserlgin
import com.mahindra.serviceengineer.savedata.languageName
import com.uvk.shramapplication.helper.TranslationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AvailableEmpAdapter(val context:Context,
    private val itemList: List<EmployeerData>
) : RecyclerView.Adapter<AvailableEmpAdapter.ImageViewHolder>() {

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val btnSave: ImageView = view.findViewById(R.id.btnSave)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvLoc: TextView = view.findViewById(R.id.tvLoc)
        val tvDesc: TextView = view.findViewById(R.id.tvDesc)
       // val tvApply: TextView = view.findViewById(R.id.tvApply)
       // val chipGroupJobTypes: ChipGroup = view.findViewById(R.id.chipGroupJobTypes)
       // val chipGroupCategories: ChipGroup = view.findViewById(R.id.chipGroupCategories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_available_emp, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val empData = itemList[position]
        TranslationHelper.initialize(context)
        CoroutineScope(Dispatchers.Main).launch {
            val targetLan = context.languageName
            holder.tvTitle.text = TranslationHelper.translateText(empData.designation ?: "", targetLan)
            holder.tvDesc.text =  TranslationHelper.translateText(empData.user_name ?: "", targetLan)
            holder.tvLoc.text =  TranslationHelper.translateText(empData.address ?: "", targetLan)
        }

        holder.btnSave.visibility = View.GONE

        Glide.with(holder.imageView.context)
            .load(empData.profile_image)
            .placeholder(R.drawable.worker)
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            Log.e("tag","AvailableEmpAdapter userId : ${empData.user_id}")
            if (context.isuserlgin) {
                val intent = Intent(context, EmployeeDetailsActivity::class.java).apply {
                    putExtra("emp_id", empData.user_id.toString())
                }
                context.startActivity(intent)
            }else{
                Toast.makeText(context,"Please Login App", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, LoginActivity::class.java))
                ( context as Activity).finish()
            }
        }
    }

    override fun getItemCount(): Int = itemList.size
}


