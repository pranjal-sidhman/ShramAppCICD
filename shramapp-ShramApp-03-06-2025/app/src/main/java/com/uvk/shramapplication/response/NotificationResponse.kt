package com.uvk.shramapplication.response

data class NotificationResponse(
    val status: Boolean,
    val message: String,
    val code: String,
    val count: Int,
    val data: List<NotificationData>
)

data class NotificationData(
    val user_id : String,
    val id : String,
    val title : String,
    val message : String,
    val notification_type : String,
    val is_read : String,
    val latitude : String,
    val created_at : String,
    val receiver_id : String,
    val sender_id : String,
    val name : String,
    val profile_image : String
)