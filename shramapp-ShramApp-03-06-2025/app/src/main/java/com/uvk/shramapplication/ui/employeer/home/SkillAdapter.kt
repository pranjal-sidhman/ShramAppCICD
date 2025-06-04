package com.uvk.shramapplication.ui.employeer.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.uvk.shramapplication.R
import com.google.android.material.chip.Chip
import android.content.res.ColorStateList
import com.mahindra.serviceengineer.savedata.languageName
import com.uvk.shramapplication.helper.TranslationHelper
import com.uvk.shramapplication.ui.main_category.MainCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SkillAdapter(
    private val context: Context,
    private val skills: List<MainCategory>,
    val catId: (String) -> Unit // Callback function to pass selected category ID
) : RecyclerView.Adapter<SkillAdapter.ChipViewHolder>() {

    private var selectedPosition: Int? = null  // No default selection

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_skill_chip, parent, false)
        return ChipViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChipViewHolder, position: Int) {
        val skill = skills[position]
        holder.bind(skill.name, skill.id.toString(), position)  // Pass category ID
    }

    override fun getItemCount(): Int = skills.size

    inner class ChipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val chip: Chip = itemView.findViewById(R.id.skillChip)

        fun bind(skillName: String, skillId: String, position: Int) {
            TranslationHelper.initialize(context)
            CoroutineScope(Dispatchers.Main).launch {
                val targetLan = context.languageName
                chip.text = TranslationHelper.translateText(skillName ?: "", targetLan)
            }


            // Set color only if selected
            if (selectedPosition == position) {
                chip.setTextColor(ContextCompat.getColor(context, R.color.white))
                chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.orange))
            } else {
                chip.setTextColor(ContextCompat.getColor(context, R.color.heading_text))
                chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.light_orange))
            }

            chip.isClickable = true
            chip.setOnClickListener {
                selectedPosition = position
                notifyDataSetChanged()

                // Pass the selected category ID
                catId(skillId)
            }
        }
    }
}


