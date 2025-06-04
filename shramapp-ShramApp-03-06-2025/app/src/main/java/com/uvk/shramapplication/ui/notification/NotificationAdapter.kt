package com.uvk.shramapplication.ui.notification

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.mahindra.serviceengineer.savedata.languageName
import com.uvk.shramapplication.MainActivity
import com.uvk.shramapplication.R
import com.uvk.shramapplication.helper.TranslationHelper
import com.uvk.shramapplication.response.NotificationData
import com.uvk.shramapplication.ui.chat.ChatActivity
import com.uvk.shramapplication.ui.employeer.home.employeelist.EmployeeDetailsActivity
import com.uvk.shramapplication.ui.map.root_map.RootMapActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationAdapter(val context: Context,
                          private var list: List<NotificationData>,
                          val readNotification: (String,String) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.ListViewHolder>() {


    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        val tvDetails = itemView.findViewById<TextView>(R.id.tvDescription)
        val tvDate = itemView.findViewById<TextView>(R.id.tvDate)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val list = list[position]

        CoroutineScope(Dispatchers.Main).launch {
            val targetLan = context.languageName
            holder.tvTitle.text = TranslationHelper.translateText(list.title ?: "", targetLan)
            holder.tvDetails.text = TranslationHelper.translateText(list.message ?: "", targetLan)
            holder.tvDate.text = TranslationHelper.translateText(list.created_at ?: "", targetLan)

        }

        holder.itemView.setOnClickListener {
           readNotification(list.id,
               list.sender_id)

            Log.e("tag", "NotificationAdapter userId : ${list.receiver_id}")
            Log.e("tag", "NotificationAdapter userId : ${list.message}")
            Log.e("tag", "NotificationAdapter userId : ${list.notification_type}")
            Log.e("tag", "NotificationAdapter id : ${list.id}")

            val notificationType = list.notification_type

            when (notificationType) {
                "send_chat_message" -> {
                    val intent = Intent(context, ChatActivity::class.java).apply {
                        putExtra("user_id", list.receiver_id)
                        putExtra("name", list.name)
                        putExtra("profile_img", list.profile_image)
                    }
                    context.startActivity(intent)
                }
                "apply_job" -> {
                 //   listener.onApplyJobClick() //  Notify activity/fragment
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("notification_type", "apply_job")
                    context.startActivity(intent)

                }
                "add_story_post" -> {
                   // listener.onStoryPostClick()
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("notification_type", "add_story_post")
                    context.startActivity(intent)
                }

                "accept_job_request" -> {
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("notification_type", "accept_job_request")
                    context.startActivity(intent)
                }
                "job_request" -> {
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("notification_type", "job_request")
                    context.startActivity(intent)
                }
                "reject_job_request" -> {
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("notification_type", "reject_job_request")
                    context.startActivity(intent)
                }
                "send_request" -> {
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("notification_type", "send_request")
                    context.startActivity(intent)
                }


                else -> {
                    // Default or error navigation
                }
            }
        }



        /* holder.btnView.setOnClickListener {
             Log.e("tag", "AvailableEmpAdapter userId : ${list.user_id}")

             val intent = Intent(context, EmployeeDetailsActivity::class.java).apply {
                 putExtra("emp_id", list.user_id)
             }
             context.startActivity(intent)
         }*/
    }

    override fun getItemCount(): Int = list.size

}
