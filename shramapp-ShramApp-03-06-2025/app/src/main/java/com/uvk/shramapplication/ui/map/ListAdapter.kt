package com.uvk.shramapplication.ui.map

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mahindra.serviceengineer.savedata.languageName
import com.uvk.shramapplication.R
import com.uvk.shramapplication.helper.TranslationHelper
import com.uvk.shramapplication.ui.employeer.home.employeelist.EmployeeDetailsActivity
import com.uvk.shramapplication.ui.map.root_map.RootMapActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListAdapter( val context: Context,
private var list: List<LocationData>
) : RecyclerView.Adapter<ListAdapter.ListViewHolder>() {


    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        val tvDetails = itemView.findViewById<TextView>(R.id.tvDescription)
        val tvLoc = itemView.findViewById<TextView>(R.id.tvLoc)
        val btnView = itemView.findViewById<TextView>(R.id.btnView)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dialog_details, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val list = list[position]

        CoroutineScope(Dispatchers.Main).launch {
            val targetLan = context.languageName
            holder.tvTitle.text = TranslationHelper.translateText(list.user_name ?: "", targetLan)
            holder.tvLoc.text = TranslationHelper.translateText(list.address ?: "", targetLan)

            if(!list.designation.isNullOrEmpty()){
                holder.tvDetails.text = TranslationHelper.translateText(list.designation  ?: "", targetLan)
            }else{
                holder.tvDetails.text = TranslationHelper.translateText(list.company_name  ?: "", targetLan)

            }

        }



        holder.btnView.setOnClickListener {
            Log.e("tag", "AvailableEmpAdapter userId : ${list.user_id}")

            val intent = Intent(context, EmployeeDetailsActivity::class.java).apply {
                putExtra("emp_id", list.user_id)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = list.size

}
