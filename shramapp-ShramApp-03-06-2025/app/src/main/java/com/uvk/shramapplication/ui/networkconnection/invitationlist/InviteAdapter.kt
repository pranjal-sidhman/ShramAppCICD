package com.uvk.shramapplication.ui.networkconnection.invitationlist

import android.content.Context
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
import com.uvk.shramapplication.ui.networkconnection.NetworkData
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InviteAdapter(
    val context: Context,
    private val networkList: List<NetworkData>,
    val acceptRequest: (String) -> Unit,
    val rejectRequest: (String) -> Unit
) : RecyclerView.Adapter<InviteAdapter.InviteViewHolder>() {

    inner class InviteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile: CircleImageView = itemView.findViewById(R.id.imgProfile)
        val tvName: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDesc)
        val btnReject: ImageView = itemView.findViewById(R.id.btnReject)
        val btnAccept: ImageView = itemView.findViewById(R.id.btnAccept)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InviteViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_invitation, parent, false)
        return InviteViewHolder(view)
    }

    override fun onBindViewHolder(holder: InviteViewHolder, position: Int) {
        val networkData = networkList[position]
        TranslationHelper.initialize(context)
        CoroutineScope(Dispatchers.Main).launch {
            val targetLan = context.languageName
            holder.tvName.text = TranslationHelper.translateText(networkData.name ?: "", targetLan)
           // holder.tvDescription.text = TranslationHelper.translateText(networkData.designation ?: "", targetLan)
        }

        if(!networkData.designation.isNullOrEmpty()){

            CoroutineScope(Dispatchers.Main).launch {
                val targetLan = context.languageName
                holder.tvDescription.text =
                    TranslationHelper.translateText(networkData.designation ?: "", targetLan)
            }
        }else{

            CoroutineScope(Dispatchers.Main).launch {
                val targetLan = context.languageName
                holder.tvDescription.text =
                    TranslationHelper.translateText(networkData.company_name ?: "", targetLan)
            }
        }

        // Load profile image
        Glide.with(holder.itemView.context)
            .load(networkData.profile_image)
            .placeholder(R.drawable.user)
            .into(holder.imgProfile)

        holder.btnAccept.setOnClickListener {
            acceptRequest(networkData.network_id)
        }

        holder.btnReject.setOnClickListener {
            rejectRequest(networkData.network_id)
        }


    }

    override fun getItemCount() = networkList.size
}
