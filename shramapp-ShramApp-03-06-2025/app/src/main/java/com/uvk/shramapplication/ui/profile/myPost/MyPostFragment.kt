package com.uvk.shramapplication.ui.profile.myPost

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.emoji2.widget.EmojiEditText
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.ui.post.Comment
import com.uvk.shramapplication.ui.post.CommentListAdapter
import com.uvk.shramapplication.ui.post.PostListAdapter
import com.uvk.shramapplication.ui.post.PostViewModel
import com.uvk.shramapplication.ui.post.StoryPostData
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.token
import com.mahindra.serviceengineer.savedata.userid


class MyPostFragment : Fragment() {

    var userIds : String? = null
    private lateinit var bottomSheetCommentDialog: BottomSheetDialog
    private lateinit var etDialogInput: AppCompatEditText
    private lateinit var cameraButton: ImageView
    private lateinit var nodataImageView: ImageView
    private lateinit var dialogPost: Dialog

    // private lateinit var dialogPost: MaterialAlertDialogBuilder
    private lateinit var btnPost: ImageView
    private lateinit var capturedImageView: ImageView
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerViewComment: RecyclerView
    lateinit var btnCommentSend: ImageView
    lateinit var btnReply: ImageView
    lateinit var et_post_message: EditText
    private lateinit var postListAdapter: PostListAdapter


    private lateinit var currentImageView: ImageView
    private var imgBase64String: String? = null

    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2

    private val REQUIRED_PERMISSIONS = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.READ_MEDIA_IMAGES // For Android 13+
    )

    var postLikeList: MutableList<StoryPostData> = mutableListOf()

    private var postList: MutableList<StoryPostData> = mutableListOf()
  //  private lateinit var postList: List<StoryPostData>
    private lateinit var commentList: List<Comment>
    private val viewModel by viewModels<PostViewModel>()

    private var pd: TransparentProgressDialog? = null


    /*override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_post, container, false)

        userIds = requireContext().userid //arguments?.getString("userId")
        Log.e("Tag","USER id : $userIds")

        recyclerView = view.findViewById(R.id.recyclerView)
        nodataImageView = view.findViewById(R.id.nodataimg)

        myPostList()

        return view
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true) // Enable options menu in fragment
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pd = TransparentProgressDialog(context, R.drawable.progress)

        userIds = requireContext().userid
        Log.e("Tag","USER id : $userIds")

        recyclerView = view.findViewById(R.id.recyclerView)
        nodataImageView = view.findViewById(R.id.nodataimg)

        myPostList()

        viewModel.shareStoryPostResult.observe(viewLifecycleOwner) { response ->
            when (response) {
                is BaseResponse.Success -> {
                    Log.e("POSTFragment", "Post Share Success: ${response.data?.message}")
                    shareToSocialMedia(response.data?.link!!)
                }

                is BaseResponse.Error -> {
                    Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
                }

                is BaseResponse.Loading -> { }
            }
        }
    }

    private fun myPostList() {
        try {
            if (requireContext().isOnline) {
                viewModel.myStoryPostListResult.observe(requireActivity()) { response ->
                    pd?.show() // Show loading indicator
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            if (response.data?.data.isNullOrEmpty()) {
                                // If the data is null or empty

                              /*  Toast.makeText(context, response.data!!.message, Toast.LENGTH_SHORT)
                                    .show()*/
                                nodataImageView.visibility = View.VISIBLE
                                recyclerView.visibility = View.GONE
                                Glide.with(requireContext())
                                    .load(R.drawable.no_data_found)
                                    .into(nodataImageView)
                            } else {
                                // If the data is available
                                postList = (response.data?.data
                                    ?: emptyList()).toMutableList() // Extract the data, or use an empty list if null

                                recyclerView.layoutManager = LinearLayoutManager(context)

                                postListAdapter = PostListAdapter(
                                    requireContext(),
                                    postList,
                                    postLikeList ,
                                    ::likePost,
                                    ::commentPost,
                                    ::DeletePost,
                                    ::SharePost
                                )


                                Log.e("Post", "token :${requireContext().token}")

                                recyclerView.adapter = postListAdapter


                            }


                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            Toast.makeText(context, response.msg, Toast.LENGTH_SHORT)
                                .show() // Show error message
                        }

                        is BaseResponse.Loading -> {
                            // Show loading indicator if needed
                        }
                    }
                }

                viewModel.getMyStoryPostList(
                    user_id = userIds.toString()
                )

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("LikePostError", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(context, "An error occurred: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun SharePost(postId: String) {

        val token = requireActivity().token
        val userId = requireActivity().userid
        Log.e("POSTFragments", "Post Sahre $postId, token : $token $userId")
        if (requireContext().isOnline) {
            viewModel.sharePost(
                token = requireActivity().token,
                user_id = requireActivity().userid,
                post_id = postId
            )
        } else {
            Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareToSocialMedia(link: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Check this out: $link")
        }
        requireContext().startActivity(Intent.createChooser(intent, "Share via"))
    }


    private fun DeletePost(postId: String) {
        Log.e("POSTFragment", "Post delete $postId")

        try {
            if (requireContext().isOnline) {
                viewModel.deleteStoryPostResult.observe(requireActivity()) { response ->
                    when (response) {
                        is BaseResponse.Loading -> {
                            pd?.show() // Show loading indicator
                        }

                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            Toast.makeText(context, response.data!!.message, Toast.LENGTH_SHORT).show()

                            myPostList()
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // Trigger the API to fetch comments
                viewModel.deleteStoryPost(
                    story_post_id = postId,
                    user_id = requireActivity().userid!!
                )

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("LikePostError", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(context, "An error occurred: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun likePost(token: String, user_id: String, post_id: String) {
        Log.d("LikePost", "Token: $token, UserId: $user_id, PostId: $post_id")

        try {
            if (requireContext().isOnline) {
                val token = requireActivity().token
                val userId = requireActivity().userid

                if (token.isNullOrEmpty() || userId.isNullOrEmpty()) {
                    Toast.makeText(
                        context,
                        "User details are missing. Please Log in.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                viewModel.likeResult.observe(viewLifecycleOwner) { response ->
                    when (response) {
                        is BaseResponse.Success -> {
                            pd!!.dismiss()
                            val message = response.data!!.message


                            val position = postList.indexOfFirst { it.id == post_id }
                            if (position != -1) {
                                Log.e("Like", "Response message: $message")
                                val currentPost = postList[position]
                                val isLiked = response.data.is_like
                                val likeCount = response.data.like_count

                                val updatedPost = currentPost.copy(
                                    is_like = isLiked,
                                    like_count = likeCount
                                )

                                postList[position] = updatedPost // Update the list
                                postListAdapter.updatePostLike(position, updatedPost) // Update UI
                            }
                        }

                        is BaseResponse.Error -> {
                            pd!!.dismiss()
                            Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
                        }

                        is BaseResponse.Loading -> {  pd!!.show() }
                    }
                }

                viewModel.likePost(token, userId, post_id)
            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("LikePostError", "Error occurred: ${e.localizedMessage}")
            //Toast.makeText(context, "An error occurred: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun commentPost(story_id: String) {
       /* val bottomSheetView =
            LayoutInflater.from(context).inflate(R.layout.bottom_sheet_comment, null)

        // Create the BottomSheetDialog and set its content view
        bottomSheetCommentDialog = BottomSheetDialog(requireContext())
        bottomSheetCommentDialog.setContentView(bottomSheetView)

        // Initialize views
        recyclerViewComment =
            bottomSheetCommentDialog.findViewById<RecyclerView>(R.id.recyclerViewComment)!!
        btnCommentSend = bottomSheetCommentDialog.findViewById<ImageView>(R.id.btnCommentSend)!!
        btnReply = bottomSheetCommentDialog.findViewById<ImageView>(R.id.btnReply)!!
        et_post_message = bottomSheetCommentDialog.findViewById<EditText>(R.id.et_post_message)!!*/

        bottomSheetCommentDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)

        val dialogView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_comment, null)

        bottomSheetCommentDialog?.setContentView(dialogView)


        // Initialize views
        recyclerViewComment =
            dialogView.findViewById<RecyclerView>(R.id.recyclerViewComment)!!
        btnCommentSend = dialogView.findViewById<ImageView>(R.id.btnCommentSend)!!
        btnReply = dialogView.findViewById<ImageView>(R.id.btnReply)!!
        et_post_message = dialogView.findViewById<EmojiEditText>(R.id.et_post_message)!!

        // Load comments initially
        commentList(story_id)


        // Handle send button click
        btnCommentSend.setOnClickListener {

            et_post_message.clearFocus() // Ensure EditText loses focus
            hideKeyboard() // Hide the soft keyboard

            val commentText = et_post_message.text?.trim().toString()

            if (commentText.isNullOrEmpty()) {
                Toast.makeText(context, "Comment cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                sendComment(story_id, commentText)
            }
        }

        // Show the dialog
        bottomSheetCommentDialog.show()


    }


    // Method to hide the soft keyboard
    private fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }


    private fun sendComment(storyId: String, commentText: String) {
        try {
            if (requireContext().isOnline) {
                // Check if user_id is null
                val token = requireActivity().token
                val userId = requireActivity().userid

                if (token.isNullOrEmpty() || userId.isNullOrEmpty()) {
                    Toast.makeText(
                        context,
                        "User details are missing. Please Log in.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                // Observe the API response
                viewModel.commentSendResult.observe(requireActivity()) { response ->
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator
                            Toast.makeText(context, response.data?.message, Toast.LENGTH_SHORT)
                                .show()

                            // Clear the EditText
                            et_post_message.text.clear()

                            // Refresh the comment list
                            commentList(storyId)
                            myPostList()
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
                        }

                        is BaseResponse.Loading -> {
                            pd?.show() // Show loading indicator if needed
                        }
                    }
                }

                // Make the API call
                viewModel.commentSend(
                    token = token,
                    user_id = userId,
                    post_id = storyId,
                    comment = commentText
                )
            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("LikePostError", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(context, "An error occurred: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT)
                .show()
        }
    }


    private fun commentList(storyId: String) {
        try {
            if (requireContext().isOnline) {


                viewModel.commentListResult.observe(requireActivity()) { response ->
                    when (response) {
                        is BaseResponse.Loading -> {
                            pd?.show() // Show loading indicator
                        }

                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            if (response.data?.data.isNullOrEmpty()) {
                                recyclerViewComment.visibility = View.GONE
                            } else {
                                // Refresh comment list data
                                commentList = response.data?.data ?: emptyList()

                                recyclerViewComment.visibility = View.VISIBLE

                                val adapter = recyclerViewComment.adapter as? CommentListAdapter
                                if (adapter == null) {
                                    // Set a new adapter
                                    recyclerViewComment.adapter = CommentListAdapter(
                                        requireContext(),
                                        commentList,
                                        ::commentLike,
                                        ::commentReply,
                                        ::commentDelete
                                    )

                                } else {
                                    // Update existing adapter's data and refresh it
                                    adapter.updateComments(commentList)
                                    adapter.resetSelection()
                                }
                            }
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // Trigger the API to fetch comments
                viewModel.getCommentList(
                    story_post_id = storyId,
                    userId = requireActivity().userid!!
                )

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("LikePostError", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(context, "An error occurred: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun commentDelete(comment_id: String, storyPostId: String) {

        Log.e("POSTFragment", "fragement comment delete $comment_id $storyPostId")

        commentDeleteAPI(comment_id, storyPostId)

    }

    private fun commentDeleteAPI(commentId: String, storyPostId: String) {
        try {
            if (requireContext().isOnline) {


                viewModel.commentDeleteResult.observe(requireActivity()) { response ->
                    when (response) {
                        is BaseResponse.Loading -> {
                            pd?.show() // Show loading indicator
                        }

                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                          //  Toast.makeText(context, response.data!!.message, Toast.LENGTH_SHORT).show()
                            commentList(storyPostId)
                            myPostList()
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // Trigger the API to fetch comments
                viewModel.commentDelete(
                    commentId = commentId,
                    userId = requireActivity().userid!!
                )

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("LikePostError", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(context, "An error occurred: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT)
                .show()
        }
    }


    private fun commentLike(comment_id: String, story_id: String) {
        try {
            if (requireContext().isOnline) {
                val token = requireActivity().token
                val userId = requireActivity().userid

                if (token.isNullOrEmpty() || userId.isNullOrEmpty()) {
                    Toast.makeText(
                        context,
                        "User details are missing. Please Log in.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                viewModel.commentLikeResult.observe(requireActivity()) { response ->
                    when (response) {
                        is BaseResponse.Loading -> {
                            pd?.show() // Show loading indicator
                        }

                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            /*Toast.makeText(context, response.data!!.message, Toast.LENGTH_SHORT)
                                .show()*/

                            // Refresh the comment list
                            commentList(story_id)

                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // Trigger the like action
                viewModel.commentLike(
                    token = token,
                    user_id = userId,
                    commentId = comment_id
                )

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("LikePostError", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(context, "An error occurred: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun commentReply(comment_id: String, story_id: String) {
        btnCommentSend.visibility = View.GONE
        btnReply.visibility = View.VISIBLE

        // Request focus and show the keyboard for the EditText
        et_post_message.apply {
            requestFocus()
            post {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        btnReply.setOnClickListener {

            val replyText = et_post_message.text.toString().trim()

            btnCommentSend.visibility = View.GONE

            if (!replyText.isNullOrEmpty()) {
                // val processedCommentText = convertTextToEmoji(replyText)
                btnCommentSend.visibility = View.GONE
                sendReply(comment_id, story_id, replyText)

            } else {
                btnCommentSend.visibility = View.GONE
                Toast.makeText(context, "Reply cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendReply(commentId: String, storyId: String, replyText: String) {
        try {
            if (requireContext().isOnline) {
                val token = requireActivity().token
                val userId = requireActivity().userid

                if (token.isNullOrEmpty() || userId.isNullOrEmpty()) {
                    Toast.makeText(
                        context,
                        "User details are missing. Please Log in.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                viewModel.commentReplyResult.observe(requireActivity()) { response ->
                    when (response) {
                        is BaseResponse.Loading -> {
                            pd?.show() // Show loading indicator
                        }

                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            /*Toast.makeText(context, response.data!!.message, Toast.LENGTH_SHORT)
                                .show()*/

                            // Clear the EditText
                            et_post_message.text?.clear()
                            btnReply.visibility = View.GONE
                            btnCommentSend.visibility = View.VISIBLE

                            // Refresh the comment list
                            commentList(storyId)
                            Log.e("Reply", "comment Reply : ${response.data!!.message}")
                            Log.e("Reply", "comment Reply code: ${response.data!!.code}")
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // Trigger the like action
                viewModel.commentReply(
                    token = token,
                    user_id = userId,
                    story_post_id = storyId,
                    commentId = commentId,
                    comment = replyText
                )

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("LikePostError", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(context, "An error occurred: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT)
                .show()
        }
    }

}

