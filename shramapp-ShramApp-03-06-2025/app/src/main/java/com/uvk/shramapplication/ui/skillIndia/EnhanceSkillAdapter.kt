package com.uvk.shramapplication.ui.skillIndia

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mahindra.serviceengineer.savedata.languageName
import com.uvk.shramapplication.R
import com.uvk.shramapplication.helper.TranslationHelper
import com.uvk.shramapplication.response.SkillData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EnhanceSkillAdapter(val context: Context,
                          private val jobList: List<SkillData>
     ):
    RecyclerView.Adapter<EnhanceSkillAdapter.SkillListViewHolder>() {


    inner class SkillListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val partnerName: TextView = itemView.findViewById(R.id.tvPartnerName)
        val centerName: TextView = itemView.findViewById(R.id.tvCenterName)
        val location: TextView = itemView.findViewById(R.id.tvLoc)
        val tvReadMore: TextView = itemView.findViewById(R.id.tvReadMore)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_skill_list, parent, false)
        return SkillListViewHolder(view)
    }

    override fun onBindViewHolder(holder: SkillListViewHolder, position: Int) {
        val job = jobList[position]
        TranslationHelper.initialize(context)
        CoroutineScope(Dispatchers.Main).launch {
            val targetLan = context.languageName
            holder.title.text = TranslationHelper.translateText(job.sector_name ?: "", targetLan)
            holder.partnerName.text = TranslationHelper.translateText(job.training_partner_name ?: "", targetLan)
            holder.centerName.text = TranslationHelper.translateText(job.training_center_name ?: "", targetLan)
            holder.location.text = TranslationHelper.translateText(job.training_center_address ?: "", targetLan)
            holder.tvReadMore.text = TranslationHelper.translateText("Read More..." ?: "", targetLan)

        }

        holder.tvReadMore.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.skillindiadigital.gov.in/skill-india-map"))
            context.startActivity(intent)
        }

        holder.location.setOnClickListener {
            openLocationInGoogleMaps(job.training_center_address)
        }


    }


    override fun getItemCount(): Int = jobList.size


    private fun openLocationInGoogleMaps(trainingCenterAddress: String) {
        val uri = Uri.encode(trainingCenterAddress)
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$uri"))
        mapIntent.setPackage("com.google.android.apps.maps") // Ensures it opens in Google Maps
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            // Fallback if Google Maps is not available
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=$uri"))
            context.startActivity(browserIntent)
        }
    }
}
