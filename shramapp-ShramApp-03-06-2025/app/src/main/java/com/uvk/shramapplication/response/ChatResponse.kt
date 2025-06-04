package com.uvk.shramapplication.response



data class ChatResponse(
    val code: Int,
    val status: Boolean,
    val message: String,
    val data: List<ChatDateGroup>
)

data class ChatRequest(
    val sender_id: String,
    val receiver_id: String,
    val chat_ids: List<String>
)

data class ChatDeleteRequest(
    val sender_id: String,
    val receiver_id: String,
    val delete_type: String,
    val chat_ids: List<String>
)



data class ChatDateGroup(
    val date: String,
    val messages: List<ChatMessage>
)

data class ChatMessage(
    val id: String,
    val sender_id: String,
    val receiver_id: String,
    val message: String,
    var is_read: String,
    val image: String?,
    val created_at: String
)

sealed class ChatItem {
    data class MessageItem(val message: ChatMessage) : ChatItem()
    data class DateHeader(val date: String) : ChatItem()
}

data class MessageResponse(
    val code: Int,
    val status: Boolean,
    val message: String,
    val data: List<MessageData>
)

data class MessageData(
    val user_id: String,
    val user_name: String,
    val profile_image: String,
    val latest_message: String,
    val message_time: String,
    val unread_count: String
)


