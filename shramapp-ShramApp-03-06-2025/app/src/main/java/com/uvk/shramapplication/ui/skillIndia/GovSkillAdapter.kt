package com.uvk.shramapplication.ui.skillIndia


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mahindra.serviceengineer.savedata.languageName
import com.uvk.shramapplication.R
import com.uvk.shramapplication.helper.TranslationHelper
import com.uvk.shramapplication.response.SkillData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GovSkillAdapter(
    private val context: Context,
    private val itemList: List<SkillData>
) : RecyclerView.Adapter<GovSkillAdapter.ImageViewHolder>() {

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvLoc: TextView = view.findViewById(R.id.tvLoc)
        val tvDesc: TextView = view.findViewById(R.id.tvDesc)
        val tvReadMore: TextView = view.findViewById(R.id.tvReadMore)
       // val tvSalary: TextView = view.findViewById(R.id.tvSalary)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gov_skill, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val job = itemList[position]
      //  holder.imageView.setImageResource(currentItem.image)
        // Load Image with Glide (Ensure you have Glide dependency)
        Glide.with(holder.imageView.context)
            .load(job.skill_image)
            .placeholder(R.drawable.baner)
            .into(holder.imageView)

        TranslationHelper.initialize(context)
        CoroutineScope(Dispatchers.Main).launch {
            val targetLan = context.languageName
            holder.tvTitle.text = TranslationHelper.translateText(job.sector_name ?: "", targetLan)
            holder.tvDesc.text = TranslationHelper.translateText(job.training_partner_name ?: "", targetLan)
            holder.tvLoc.text = TranslationHelper.translateText(job.training_center_address ?: "", targetLan)
            holder.tvReadMore.text = TranslationHelper.translateText("Read More..." ?: "", targetLan)
        }

        holder.tvReadMore.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.skillindiadigital.gov.in/skill-india-map"))
            context.startActivity(intent)
        }

        holder.tvLoc.setOnClickListener {
            openLocationInGoogleMaps(job.training_center_address)
        }



    }

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

    override fun getItemCount(): Int = itemList.size
}


