package com.uvk.shramapplication.ui.home.skilllist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.uvk.shramapplication.R
import com.google.android.material.chip.Chip

class SkillAdapter(private val context: Context, private val skills: List<String>) :
    RecyclerView.Adapter<SkillAdapter.ChipViewHolder>() {

    // Track the selected position, initialize it to 0 (default "All" is selected)
    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_skill_chip, parent, false)
        return ChipViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChipViewHolder, position: Int) {
        val skill = skills[position]
        holder.bind(skill, position)
    }

    override fun getItemCount(): Int = skills.size

    inner class ChipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val chip: Chip = itemView.findViewById(R.id.skillChip)

        fun bind(skill: String, position: Int) {
            chip.text = skill

            // Set color based on selection
            if (position == selectedPosition) {
                chip.setTextColor(ContextCompat.getColor(context, R.color.white))
                chip.setChipBackgroundColorResource(R.color.colorPrimary)  // Use setChipBackgroundColorResource
            } else {
                chip.setTextColor(ContextCompat.getColor(context, R.color.heading_text))
                chip.setChipBackgroundColorResource(R.color.white)  // Use setChipBackgroundColorResource
            }

            chip.isClickable = true
            chip.setOnClickListener {
                selectedPosition = position
                notifyDataSetChanged()

                //Toast.makeText(context, "Selected: $skill", Toast.LENGTH_SHORT).show()
            }
        }


    }
}
