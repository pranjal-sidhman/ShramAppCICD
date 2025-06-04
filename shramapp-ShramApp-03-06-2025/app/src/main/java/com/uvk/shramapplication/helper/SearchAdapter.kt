package com.uvk.shramapplication.helper

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uvk.shramapplication.R
import com.uvk.shramapplication.response.SuggestionData

class SearchAdapter(
    private var list: List<SuggestionData>,
    private val onItemClick: (SuggestionData) -> Unit
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    fun updateList(newList: List<SuggestionData>) {
        list = newList
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.suggestionName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_suggestion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.name.text = item.name
        holder.itemView.setOnClickListener { onItemClick(item) }

        Log.e("tag", "Search Suggestion: ${item.name}") // Ensure logging works
    }

    override fun getItemCount() = list.size
}

