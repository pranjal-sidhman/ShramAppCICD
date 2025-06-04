package com.uvk.shramapplication.ui.wallet

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mahindra.serviceengineer.savedata.languageName
import com.uvk.shramapplication.R
import com.uvk.shramapplication.helper.TranslationHelper
import com.uvk.shramapplication.response.WalletHistoryData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryWalletAdapter(
    val context: Context,
    private val historyList: List<WalletHistoryData>
) :
    RecyclerView.Adapter<HistoryWalletAdapter.HistoryWalletListViewHolder>() {

    inner class HistoryWalletListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTransactionTitle: TextView = itemView.findViewById(R.id.tvTransactionTitle)
        val tvTransactionDate: TextView = itemView.findViewById(R.id.tvTransactionDate)
        val tvTransactionAmount: TextView = itemView.findViewById(R.id.tvTransactionAmount)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryWalletListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wallet_history_list, parent, false)
        return HistoryWalletListViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: HistoryWalletListViewHolder, position: Int) {
        val data = historyList[position]

        TranslationHelper.initialize(context)
        CoroutineScope(Dispatchers.Main).launch {
            val targetLan = context.languageName

            holder.tvTransactionTitle.text = TranslationHelper.translateText(data.name ?: "", targetLan)
            holder.tvTransactionDate.text = TranslationHelper.translateText(data.created_at ?: "", targetLan)
            holder.tvDescription.text = TranslationHelper.translateText(data.description ?: "", targetLan)

            if (data.type == "debit") {
                holder.tvTransactionAmount.text = TranslationHelper.translateText(" - " + data.amount ?: "", targetLan)
                holder.tvTransactionAmount.setTextColor(Color.parseColor("#D32F2F"))
            } else {
                holder.tvTransactionAmount.text = TranslationHelper.translateText( " + "+data.amount ?: "", targetLan)
                holder.tvTransactionAmount.setTextColor(Color.parseColor("#388E3C")) // Green
            }

        }

    }

    override fun getItemCount(): Int = historyList.size
}