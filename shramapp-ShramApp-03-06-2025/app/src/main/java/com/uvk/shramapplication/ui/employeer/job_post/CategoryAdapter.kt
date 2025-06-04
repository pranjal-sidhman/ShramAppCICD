package com.uvk.shramapplication.ui.employeer.job_post

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.mahindra.serviceengineer.savedata.languageName
import com.uvk.shramapplication.R
import com.uvk.shramapplication.helper.TranslationHelper
import com.uvk.shramapplication.ui.category.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryAdapter(
    private val context: Context,
    private val categories: List<Category>,
    private val selectedCatIds: MutableList<Int> // Pass selectedCatIds list to the adapter
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkbox: CheckBox = view.findViewById(R.id.cbCategory)
        val vacancyEditText: EditText = view.findViewById(R.id.etVacancy)
        var textWatcher: TextWatcher? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_category_vacancy, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        TranslationHelper.initialize(context)
        CoroutineScope(Dispatchers.Main).launch {
            val targetLan = context.languageName
            holder.checkbox.text  = TranslationHelper.translateText(category.category_name ?: "", targetLan)

        }

        holder.checkbox.isChecked = category.isSelected

        // Prevent triggering TextWatcher during setText
        holder.vacancyEditText.setText(
            if (category.vacancies > 0) category.vacancies.toString() else ""
        )

        // Enable/Disable EditText
        holder.vacancyEditText.isEnabled = category.isSelected

        //  REMOVE old TextWatcher
        holder.textWatcher?.let {
            holder.vacancyEditText.removeTextChangedListener(it)
        }

        // ✅ CREATE and ADD a new TextWatcher
        val newWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                category.vacancies = s?.toString()?.toIntOrNull() ?: 0
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        holder.vacancyEditText.addTextChangedListener(newWatcher)
        holder.textWatcher = newWatcher

        // ✅ Handle checkbox logic
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            category.isSelected = isChecked
            holder.vacancyEditText.isEnabled = isChecked

            if (isChecked) {
                // Add category ID to selectedCatIds
                if (!selectedCatIds.contains(category.id)) {
                    selectedCatIds.add(category.id)
                }
            } else {
                // Remove category ID from selectedCatIds
                selectedCatIds.remove(category.id)
            }

            if (isChecked) {
                holder.vacancyEditText.requestFocus()
                holder.vacancyEditText.postDelayed({
                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(holder.vacancyEditText, InputMethodManager.SHOW_IMPLICIT)
                }, 200)
            } else {
                holder.vacancyEditText.setText("")
                category.vacancies = 0
                holder.vacancyEditText.clearFocus()
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(holder.vacancyEditText.windowToken, 0)
            }
        }

        holder.vacancyEditText.doAfterTextChanged {
            val value = it.toString().toIntOrNull() ?: 0
            category.vacancies = value
            category.isSelected = value > 0
            holder.checkbox.isChecked = value > 0
        }
    }

    override fun getItemCount(): Int = categories.size
}
