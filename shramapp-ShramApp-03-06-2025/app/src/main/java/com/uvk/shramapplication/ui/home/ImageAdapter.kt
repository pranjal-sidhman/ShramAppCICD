package com.uvk.shramapplication.ui.home


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.uvk.shramapplication.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup



class ImageAdapter(
    private val itemList: List<Job>
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val title: TextView = view.findViewById(R.id.tvTitle)
        val location: TextView = view.findViewById(R.id.tvLoc)
        val description: TextView = view.findViewById(R.id.tvDesc)
        val chipGroupJobTypes: ChipGroup = view.findViewById(R.id.chipGroupJobTypes)
        val chipGroupCategories: ChipGroup = view.findViewById(R.id.chipGroupCategories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_job_seeker, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val currentItem = itemList[position]
      //  holder.imageView.setImageResource(currentItem.image)
        holder.title.text = currentItem.title
        holder.location.text = currentItem.location
        holder.description.text = currentItem.description

        // Dynamically add Chips for Job Types
        holder.chipGroupJobTypes.removeAllViews()  // Clear any existing chips
        currentItem.job_types.job_type_names.forEach { jobType ->
            val chip = Chip(holder.chipGroupJobTypes.context).apply {
                text = jobType
                isCheckable = false  // Make it non-checkable if needed
                chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.light_sky)
            }
            holder.chipGroupJobTypes.addView(chip)
        }

        // Dynamically add Chips for Categories
        holder.chipGroupCategories.removeAllViews()  // Clear any existing chips
        currentItem.categories.category_names.forEach { category ->
            val chip = Chip(holder.chipGroupCategories.context).apply {
                text = category
                isCheckable = false  // Make it non-checkable if needed
                chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.light_sky)
            }
            holder.chipGroupCategories.addView(chip)
        }

    }

    override fun getItemCount(): Int = itemList.size
}


