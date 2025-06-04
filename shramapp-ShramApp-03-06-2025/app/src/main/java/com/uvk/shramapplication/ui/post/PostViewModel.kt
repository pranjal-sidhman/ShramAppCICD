package com.uvk.shramapplication.ui.post

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.repository.UserAuthRepository
import kotlinx.coroutines.launch

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepo = UserAuthRepository()
    val storyPostListResult: MutableLiveData<BaseResponse<StoryPostResponse>> = MutableLiveData()
    val myStoryPostListResult: MutableLiveData<BaseResponse<StoryPostResponse>> = MutableLiveData()
    val deleteStoryPostResult: MutableLiveData<BaseResponse<StoryPostResponse>> = MutableLiveData()

   // val likeResult: MutableLiveData<BaseResponse<StoryPostResponse>> = MutableLiveData()
    val storyPostDetailsResult: MutableLiveData<BaseResponse<StoryPostResponse>> = MutableLiveData()
    val shareStoryPostResult: MutableLiveData<BaseResponse<StoryPostResponse>> = MutableLiveData()
    val likeResult: MutableLiveData<BaseResponse<StoryPostResponse>> = MutableLiveData()
    val addStoryResult: MutableLiveData<BaseResponse<StoryPostResponse>> = MutableLiveData()
    val commentListResult: MutableLiveData<BaseResponse<CommentResponse>> = MutableLiveData()
    val commentDeleteResult: MutableLiveData<BaseResponse<CommentResponse>> = MutableLiveData()
    val commentSendResult: MutableLiveData<BaseResponse<CommentResponse>> = MutableLiveData()
    val commentLikeResult: MutableLiveData<BaseResponse<StoryPostResponse>> = MutableLiveData()
    val commentReplyResult: MutableLiveData<BaseResponse<StoryPostResponse>> = MutableLiveData()


    fun getStoryPostList(user_id: String,keyword : String) {

        storyPostListResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getStoryPostList(
                    userId = user_id,
                    keyword = keyword
                )

                if (response?.code() == 200) {
                    storyPostListResult.value = BaseResponse.Success(response.body())
                } else {
                    storyPostListResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                storyPostListResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun getMyStoryPostList(user_id: String) {

        myStoryPostListResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getMyStoryPostList(
                    userId = user_id
                )

                if (response?.code() == 200) {
                    myStoryPostListResult.value = BaseResponse.Success(response.body())
                } else {
                    myStoryPostListResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                myStoryPostListResult.value = BaseResponse.Error(ex.message)
            }
        }
    }




    fun deleteStoryPost(story_post_id: String,user_id: String) {

        deleteStoryPostResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.deleteStoryPost(
                    story_post_id =story_post_id,
                    userId = user_id
                )

                if (response?.code() == 200) {
                    deleteStoryPostResult.value = BaseResponse.Success(response.body())
                } else {
                    deleteStoryPostResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                deleteStoryPostResult.value = BaseResponse.Error(ex.message)
            }
        }
    }





    fun likePost(token: String, user_id: String, post_id: String) {
        likeResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val response = userRepo.like(token, user_id, post_id)

                if (response?.isSuccessful == true) {
                    response.body()?.let { body ->
                        likeResult.postValue(BaseResponse.Success(body))
                    } ?: run {
                        likeResult.postValue(BaseResponse.Error("Empty response body"))
                    }
                } else {
                    likeResult.postValue(BaseResponse.Error(response?.message() ?: "Unknown error"))
                }

            } catch (ex: Exception) {
                likeResult.postValue(BaseResponse.Error(ex.localizedMessage ?: "Unexpected error"))
            }
        }
    }


    fun sharePost(token: String, user_id: String, post_id: String) {
        shareStoryPostResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val response = userRepo.sharePost(token, user_id, post_id)

                if (response?.code() == 200) {
                    shareStoryPostResult.value = BaseResponse.Success(response.body())
                } else {
                    shareStoryPostResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {

                shareStoryPostResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

fun storyPostDetails(story_post_id: String) {
        storyPostDetailsResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val response = userRepo.getStoryPostDetails(story_post_id)

                if (response?.code() == 200) {
                    storyPostDetailsResult.value = BaseResponse.Success(response.body())
                } else {
                    storyPostDetailsResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {

                storyPostDetailsResult.value = BaseResponse.Error(ex.message)
            }
        }
    }





    fun addPost(
        token: String,
        user_id: String,
        name: String,
        location: String,
        company_name: String,
        role: String,
        description: String,
        postImage: String
    ) {

        addStoryResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val storyPostRequest = StoryPostRequestBody(
                    token = token,
                    user_id = user_id,
                    name = name,
                    location = location,
                    company_name = company_name,
                    role = role,
                    description = description,
                    story_post_image = postImage
                )


                val response = userRepo.addStory(
                    token = token,
                    body = storyPostRequest
                )



                if (response?.code() == 200) {
                    addStoryResult.value = BaseResponse.Success(response.body())
                } else {
                    addStoryResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                addStoryResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun getCommentList(story_post_id: String,userId : String) {

        commentListResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.commentList(
                    storyPostId = story_post_id,
                    userId = userId
                )

                if (response?.code() == 200) {
                    commentListResult.value = BaseResponse.Success(response.body())
                } else {
                    commentListResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                commentListResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun commentDelete(commentId: String,userId : String) {

        commentDeleteResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.commentDelete(
                    commentId = commentId,
                    userId = userId
                )

                if (response?.code() == 200) {
                    commentDeleteResult.value = BaseResponse.Success(response.body())
                } else {
                    commentDeleteResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                commentDeleteResult.value = BaseResponse.Error(ex.message)
            }
        }
    }


    fun commentSend(token: String, user_id: String, post_id: String,comment : String) {

        commentSendResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.storyCommentsend(
                    token = token,
                    userId = user_id,
                    postId = post_id,
                    comment = comment
                )

                if (response?.code() == 200) {
                    commentSendResult.value = BaseResponse.Success(response.body())
                } else {
                    commentSendResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                commentSendResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun commentLike(token: String, user_id: String,commentId : String) {

        commentLikeResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.storyCommentLike(
                    token = token,
                    userId = user_id,
                    commentId = commentId
                )

                if (response?.code() == 200) {
                    commentLikeResult.value = BaseResponse.Success(response.body())
                } else {
                    commentLikeResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                commentLikeResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun commentReply(token: String, user_id: String, story_post_id: String,commentId: String,comment : String) {

        commentReplyResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.storyCommentReply(
                    token = token,
                    userId = user_id,
                    storypostId = story_post_id,
                    commentId = commentId,
                    comment = comment
                )

                if (response?.code() == 200) {
                    commentReplyResult.value = BaseResponse.Success(response.body())
                } else {
                    commentReplyResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                commentReplyResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

}