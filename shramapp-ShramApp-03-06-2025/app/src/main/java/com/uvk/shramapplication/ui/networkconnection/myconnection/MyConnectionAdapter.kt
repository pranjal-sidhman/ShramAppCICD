package com.uvk.shramapplication.ui.networkconnection.myconnection

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uvk.shramapplication.R
import com.uvk.shramapplication.ui.chat.ChatActivity
import com.uvk.shramapplication.ui.login.LoginActivity
import com.uvk.shramapplication.ui.networkconnection.NetworkData
import com.uvk.shramapplication.ui.networkconnection.NetworkDetailsActivity
import com.mahindra.serviceengineer.savedata.isuserlgin
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.roleId
import com.mahindra.serviceengineer.savedata.userid
import com.uvk.shramapplication.helper.TranslationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyConnectionAdapter(
    val context: Context,
    private val networkList: List<NetworkData>,
    val removeConnection: (String,String) -> Unit,
    val getChatDeductStatus: (String,String,String) -> Unit,
) : RecyclerView.Adapter<MyConnectionAdapter.MyConnectionViewHolder>() {

    inner class MyConnectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
       /* val imgProfile: ImageView = itemView.findViewById(R.id.imgProfile)
        val tvName: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDesc)
        val btnRemove: ImageView = itemView.findViewById(R.id.btnRemove)*/

        val imgProfile: ImageView = itemView.findViewById(R.id.imgProfile)
        val tvName: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDesc)
        val btnRemove: ImageView = itemView.findViewById(R.id.btnRemove)
        val btnMessage: Button = itemView.findViewById(R.id.btnMessage)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyConnectionViewHolder {
       // val view = LayoutInflater.from(parent.context).inflate(R.layout.item_connection, parent, false)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_followers, parent, false)
        return MyConnectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyConnectionViewHolder, position: Int) {
        val networkData = networkList[position]
        TranslationHelper.initialize(context)
        CoroutineScope(Dispatchers.Main).launch {
            val targetLan = context.languageName
            holder.tvName.text =
                TranslationHelper.translateText(networkData.connected_user_name ?: "", targetLan)

            Log.d("MyConnectionAdapter", "Binding data for: ${networkData.connected_user_name}")

            holder.btnMessage.text = TranslationHelper.translateText("Message", targetLan)

        }

        holder.btnRemove.setOnClickListener {
            removeConnection(networkData.network_connection_id,networkData.connected_user_name)
        }

        if(!networkData.connected_user_designation.isNullOrEmpty()){
            CoroutineScope(Dispatchers.Main).launch {
                val targetLan = context.languageName
                holder.tvDescription.text =
                    TranslationHelper.translateText(
                        networkData.connected_user_designation ?: "",
                        targetLan
                    )

            }
        }else{
                CoroutineScope(Dispatchers.Main).launch {
                    val targetLan = context.languageName
                    holder.tvDescription.text =
                        TranslationHelper.translateText(
                            networkData.connected_user_company_name ?: "", targetLan)
                }

        }


        // Load profile image
        Glide.with(holder.imgProfile.context)
            .load(networkData.profile_image)
            .placeholder(R.drawable.worker)
            .into(holder.imgProfile)


        Log.e("tag","Status :${networkData.request_status}")

        holder.btnMessage.setOnClickListener {
            if(context.roleId == "2"){
                getChatDeductStatus(networkData.connected_user_id, networkData.connected_user_name,networkData.profile_image)
            }else {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("user_id", networkData.connected_user_id)
                intent.putExtra("name", networkData.connected_user_name)
                intent.putExtra("profile_img", networkData.profile_image)
                context.startActivity(intent)
            }
        }

        holder.itemView.setOnClickListener {

            if (context.isuserlgin) {
                val intent = Intent(context, NetworkDetailsActivity::class.java).apply {
                    putExtra("net_id", networkData.connected_user_id)
                    putExtra("connect", "already connect")
                }

                context.startActivity(intent)

            }else{
                Toast.makeText(context,"Please Login App", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, LoginActivity::class.java))
                ( context as Activity).finish()
            }
        }

    }

    override fun getItemCount() = networkList.size
}
