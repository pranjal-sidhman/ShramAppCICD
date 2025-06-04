package com.uvk.shramapplication.ui.chat.message

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.mahindra.serviceengineer.savedata.languageName
import com.uvk.shramapplication.R
import com.uvk.shramapplication.helper.TranslationHelper
import com.uvk.shramapplication.helper.ZoomImageUtils
import com.uvk.shramapplication.response.MessageData
import com.uvk.shramapplication.ui.chat.ChatActivity
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MessageAdapter(
    val context: Context,
    private val messageList: List<MessageData>
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {


    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile: ShapeableImageView = itemView.findViewById(R.id.imgProfile)
        val tvUserName: TextView = itemView.findViewById(R.id.tvName)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDesc)
        val tvMsgCount: TextView = itemView.findViewById(R.id.tvMsgCount)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val messageModel = messageList[position]
        TranslationHelper.initialize(context)

        CoroutineScope(Dispatchers.Main).launch {
            val targetLan = context.languageName
            holder.tvUserName.text = //messageModel.user_name
                TranslationHelper.translateText(messageModel.user_name ?: "", targetLan)
            holder.tvTime.text =
                TranslationHelper.translateText(messageModel.message_time ?: "", targetLan)
            holder.tvDescription.text =
                TranslationHelper.translateText(messageModel.latest_message ?: "", targetLan)


            holder.tvMsgCount.visibility = View.GONE
            if (messageModel.unread_count > "0") {
                holder.tvMsgCount.visibility = View.VISIBLE
                holder.tvMsgCount.text =   TranslationHelper.translateText(messageModel.unread_count ?: "", targetLan)
            }
        }

        // Load profile image
        Glide.with(holder.itemView.context)
            .load(messageModel.profile_image)
            .placeholder(R.drawable.no_data_found)
            .into(holder.imgProfile)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("user_id", messageModel.user_id)
            intent.putExtra("name", messageModel.user_name)
            intent.putExtra("profile_img", messageModel.profile_image)
            context.startActivity(intent)
        }

        holder.imgProfile.setOnClickListener {
            ZoomImageUtils.showZoomableImage(context, messageModel.profile_image)
        }

    }


    override fun getItemCount() = messageList.size
}