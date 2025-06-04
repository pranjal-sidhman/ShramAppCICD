package com.uvk.shramapplication.ui.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.uvk.shramapplication.MainActivity
import com.uvk.shramapplication.R
import com.uvk.shramapplication.ui.chat.ChatActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title"] ?: "New Message"
            val message = remoteMessage.data["message"] ?: ""
            val sender_id = remoteMessage.data["sender_id"]
            val sender_name = remoteMessage.data["sender_name"]
            val sender_image_full_path = remoteMessage.data["sender_image_full_path"]
            val notificationType = remoteMessage.data["notification_type"]

            Log.e("onMsgRec","chatId :  $sender_name $sender_id $notificationType $sender_image_full_path")


            val intent: Intent = Intent(this, MainActivity::class.java) // default fallback

            when (notificationType) {
                "send_chat_message" -> {
                    intent.setClass(this, ChatActivity::class.java)
                    intent.putExtra("user_id", sender_id)
                    intent.putExtra("name", sender_name)
                    intent.putExtra("profile_img", sender_image_full_path)
                }

                "apply_job" -> {
                    intent.putExtra("notification_type", "apply_job")
                }

                "add_story_post" -> {
                    intent.putExtra("notification_type", "add_story_post")
                }

                "accept_job_request" -> {
                    intent.putExtra("notification_type", "accept_job_request")
                }

                "job_request" -> {
                    intent.putExtra("notification_type", "job_request")
                }

                "reject_job_request" -> {
                    intent.putExtra("notification_type", "reject_job_request")
                }

                "send_request" -> {
                    intent.putExtra("notification_type", "send_request")
                }

                else -> {
                    // Optionally handle unknown types
                }
            }

            val pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val notificationBuilder = NotificationCompat.Builder(this, "chat_channel")
                .setSmallIcon(R.drawable.logo) // Your icon
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    "chat_channel", "Chat Messages", NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }

            notificationManager.notify(101, notificationBuilder.build())
        }
    }



}


