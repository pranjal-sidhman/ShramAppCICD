package com.uvk.shramapplication.ui.post

data class PostModel(
    val user_name: String,
    val comp_name: String,
    val description: String,
    val imageUrl: Any

)

data class StoryPostResponse(
    val code:String,
    val status:String,
    val message:String,
    val link:String,
    val type:String,
    val story_post_id:String,
    val like_count:Int,
    val is_like:Boolean,
    val data:List<StoryPostData>
)

data class  StoryPostData (
    val id:String?,
    val user_id:String?,
    val name:String?,
    val story_post_image:String?,
    val location:String?,
    val company_name:String?,
    val description:String,
    val role:String?,
    val user_name:String?,
    val profile_image:String?,
    var is_like:Boolean?,
    var like_count: Int,
    val comment_count: String?,
    val created_at: String


)

data class StoryPostRequestBody(
    val token:String,
    val user_id:String,
    val name:String,
    val location:String,
    val company_name:String,
    val role:String,
    val description:String,
    val story_post_image:String
)

data class CommentResponse(
    val code: Int,
    val status: String,
    val data: List<Comment>,
    val message: String
)

data class Comment(
    val id: String,
    val story_post_user_id: String,
    val story_post_id: String,
    val comment: String,
    val parent_comment_id: String,
    val commented_user_name: String,
    val story_post_name: String,
    val story_post_added_by: String,
    val is_like: Boolean,
    val like_count: String,
    val comment_time: String,
    val reply_count: String,
    val replies: List<Reply>
)

data class Reply(
    val id: String,
    val story_post_user_id: String,
    val story_post_id: String,
    val comment: String,
    val reply_time: String,
    val parent_comment_id: String,
    val commented_user_name: String,
    val story_post_name: String,
    val story_post_added_by: String
)
