package com.uvk.shramapplication.ui.post

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.mahindra.serviceengineer.savedata.isOnline
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.emoji2.widget.EmojiEditText
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mahindra.serviceengineer.savedata.companyName
import com.mahindra.serviceengineer.savedata.designationName
import com.mahindra.serviceengineer.savedata.roleId
import com.mahindra.serviceengineer.savedata.token
import com.mahindra.serviceengineer.savedata.user_profile
import com.mahindra.serviceengineer.savedata.userid
import com.mahindra.serviceengineer.savedata.username
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream

import androidx.emoji2.text.EmojiCompat
import androidx.emoji2.text.EmojiCompat.Config
import androidx.emoji2.text.FontRequestEmojiCompatConfig
import androidx.core.provider.FontRequest
import androidx.lifecycle.lifecycleScope
import com.google.android.material.imageview.ShapeableImageView
import com.mahindra.serviceengineer.savedata.languageName
import com.uvk.shramapplication.MainActivity
import com.uvk.shramapplication.helper.TranslationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PostFragment : Fragment() {
  //  private lateinit var bottomSheetCommentDialog: BottomSheetDialog
    private var bottomSheetDialog: BottomSheetDialog? = null
    private lateinit var etDialogInput: AppCompatEditText
    private lateinit var cameraButton: ImageView
    private lateinit var galleryButton: ImageView
    private lateinit var nodataImageView: ImageView
    private lateinit var dialogPost: Dialog

    // private lateinit var dialogPost: MaterialAlertDialogBuilder
    private lateinit var btnPost: ImageView
    lateinit var ivClear: ImageView
    private lateinit var capturedImageView: ImageView
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerViewComment: RecyclerView
    lateinit var btnCommentSend: ImageView
    lateinit var btnReply: ImageView
    lateinit var et_post_message: EmojiEditText
    lateinit var etSearch: EditText
    private lateinit var postListAdapter: PostListAdapter

    /*  private val CAMERA_REQUEST_CODE = 100
      private val GALLERY_REQUEST_CODE = 200
      private val PERMISSION_REQUEST_CODE = 300*/


    private lateinit var currentImageView: ImageView
    private var imgBase64String: String? = null

    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2

    private val REQUIRED_PERMISSIONS = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.READ_MEDIA_IMAGES // For Android 13+
    )

    var postLikeList: MutableList<StoryPostData> = mutableListOf() // Mutable list ✅


    private var postList: MutableList<StoryPostData> = mutableListOf()

    //private lateinit var postList: List<StoryPostData>
    private lateinit var commentList: List<Comment>
    private val viewModel by viewModels<PostViewModel>()

    private var pd: TransparentProgressDialog? = null

    var keyword : String = " "


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true) // Enable options menu in fragment
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pd = TransparentProgressDialog(context, R.drawable.progress)


        recyclerView = view.findViewById(R.id.recyclerView)
      //  btnPost = view.findViewById(R.id.btnadd)
        nodataImageView = view.findViewById(R.id.nodataimg)
        etSearch = view.findViewById(R.id.etSearch)
        ivClear = view.findViewById(R.id.ivClear)

        observePostList() // Observe only once

        if (requireContext().isOnline) {
            viewModel.getStoryPostList(requireActivity().userid, keyword)
        }

        ivClear.setOnClickListener {
            etSearch.text.clear()
            searchData("") // Empty search
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()

                /* if (query.isEmpty()) {
                     searchData("") // Empty search
                 }
                 if (query.length >= 3) {
                     searchData(query) // Search API call
                 }*/

                if (s.toString().length >= 3) {

                    if (query.isEmpty()) {
                        searchData("") // Empty search
                    }
                    if (query.length >= 3) {
                        searchData(query) // Search API call
                    }
                    ivClear.visibility = View.VISIBLE

                } else {
                    if (query.isEmpty()) {
                        searchData("") // Empty search
                    }
                    ivClear.visibility = View.GONE
                }
            }
        })


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

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(requireContext(), MainActivity::class.java)
              //  intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
             //   requireActivity().finish()
            }
        })

    }

    private fun searchData(searchKey: String) {
        keyword = searchKey.trim()
        if (requireContext().isOnline) {
            viewModel.getStoryPostList(requireActivity().userid, keyword)
        }
    }


    @SuppressLint("MissingInflatedId")
    private fun commentPost(story_id: String) {
        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)

        val dialogView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_comment, null)

        bottomSheetDialog?.setContentView(dialogView)


        // Initialize views
        recyclerViewComment =
            dialogView.findViewById<RecyclerView>(R.id.recyclerViewComment)!!
        btnCommentSend = dialogView.findViewById<ImageView>(R.id.btnCommentSend)!!
        btnReply = dialogView.findViewById<ImageView>(R.id.btnReply)!!
        et_post_message = dialogView.findViewById<EmojiEditText>(R.id.et_post_message)!!
       //val commentBoxContainer = dialogView.findViewById<LinearLayout>(R.id.commentBoxContainer)!!

       // val rootView = dialogView.findViewById<View>(android.R.id.content)
        val commentBox = dialogView.findViewById<View>(R.id.layout_gchat_chatbox)
        val rootView = dialogView.rootView
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)

            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            if (keypadHeight > screenHeight * 0.15) {
                // Keyboard is visible
                commentBox.setPadding(0, 0, 0, keypadHeight)
            } else {
                // Keyboard is hidden
                commentBox.setPadding(0, 0, 0, 0)
            }
        }


        val fontRequest = FontRequest(
            "com.google.android.gms.fonts",
            "com.google.android.gms",
            "Noto Color Emoji Compat",
            emptyList() // No certificates required
        )

        val config = FontRequestEmojiCompatConfig(requireContext(), fontRequest)
        EmojiCompat.init(config)

        EmojiCompat.get().registerInitCallback(object : EmojiCompat.InitCallback() {
            override fun onInitialized() {
                Log.e("EmojiCompat", "EmojiCompat initialized successfully")
            }

            override fun onFailed(throwable: Throwable?) {
                Log.e("EmojiCompat", "EmojiCompat initialization failed", throwable)
            }
        })

        // Load comments initially
        commentList(story_id)


        // Handle send button click
        btnCommentSend.setOnClickListener {

            et_post_message.clearFocus() // Ensure EditText loses focus
            hideKeyboard() // Hide the soft keyboard

            val commentText = et_post_message.text?.trim().toString()

            Log.e("EmojiComment", "Comment: $commentText")

            if (commentText.isNullOrEmpty()) {
                Toast.makeText(context, "Comment cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                sendComment(story_id, commentText)
            }
        }

        // Show the dialog
        bottomSheetDialog?.show()


    }

    // Method to hide the soft keyboard
    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
                            /*Toast.makeText(context, response.data?.message, Toast.LENGTH_SHORT)
                                .show()*/

                            // Clear the EditText
                            et_post_message?.text?.clear()


                            // Refresh the comment list
                            commentList(storyId)
                           // observePostList()
                            if (requireContext().isOnline) {
                                viewModel.getStoryPostList(requireActivity().userid, keyword)
                            }
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

                            // Toast.makeText(context, response.data!!.message, Toast.LENGTH_SHORT).show()
                            commentList(storyPostId)
                          //  observePostList()
                            if (requireContext().isOnline) {
                                viewModel.getStoryPostList(requireActivity().userid, keyword)
                            }
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


    private fun commentReply(comment_id: String, story_id: String) {
        btnCommentSend.visibility = View.GONE
        btnReply.visibility = View.VISIBLE

        // Request focus and show the keyboard for the EditText
        et_post_message.apply {
            requestFocus()
            post {
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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

                            /*  Toast.makeText(context, response.data!!.message, Toast.LENGTH_SHORT)
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

                            /* Toast.makeText(context, response.data!!.message, Toast.LENGTH_SHORT)
                                 .show()*/

                            // Refresh the comment list
                            commentList(story_id)
                            Log.e("Like", "comment like : ${response.data!!.message}")
                            Log.e("Like", "comment like code: ${response.data!!.code}")
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




    private fun observePostList() {
        try {
            viewModel.storyPostListResult.removeObservers(viewLifecycleOwner) // Prevent multiple observers
            viewModel.storyPostListResult.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is BaseResponse.Success -> {
                        postList = response.data?.data?.toMutableList() ?: mutableListOf()

                        if (postList.isEmpty()) {
                            //   Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show()
                            nodataImageView.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        } else {
                            nodataImageView.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE

                            if (::postListAdapter.isInitialized) {
                                postListAdapter.updateList(postList) // Update existing adapter
                            } else {
                                recyclerView.layoutManager = LinearLayoutManager(context)
                                postListAdapter = PostListAdapter(
                                    requireContext(),
                                    postList,
                                    postLikeList,
                                    ::likePost,
                                    ::commentPost,
                                    ::DeletePost,
                                    ::SharePost)
                                recyclerView.adapter = postListAdapter
                            }
                        }
                    }
                    is BaseResponse.Error -> {
                        context?.let {
                            Toast.makeText(it, response.msg?:"", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is BaseResponse.Loading -> {
                        // Show loading UI if needed
                    }
                }
            }
        }catch (e: Exception){

        }

    }


   /* private fun SharePost(postId: String) {
        val token = requireActivity().token
        val userId = requireActivity().userid
        Log.e("POSTFragments", "Post Sahre $postId, token : $token $userId")
        try {
            if (requireContext().isOnline) {

                //viewModel.shareStoryPostResult.observe(viewLifecycleOwner) { response ->
                viewModel.shareStoryPostResult.observe(requireActivity()) { response ->
                    when (response) {
                        is BaseResponse.Success -> {

                            val message = response.data!!.message

                            Log.e("POSTFragment", "Post Share Success: ${response.data?.message}")
                            shareToSocialMedia(response.data?.link!!)


                        }

                        is BaseResponse.Error -> {

                            Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
                        }

                        is BaseResponse.Loading -> {   }
                    }
                }

                viewModel.sharePost(token = requireActivity().token,
                    user_id = requireActivity().userid,
                    post_id = postId)
            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("SharePostError", "Error occurred: ${e.localizedMessage}")

        }

    }*/

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

                            Toast.makeText(context, response.data!!.message, Toast.LENGTH_SHORT)
                                .show()

                          //  observePostList()

                            if (requireContext().isOnline) {
                                viewModel.getStoryPostList(requireActivity().userid, keyword)
                            }
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
            Toast.makeText(context, "An error occurred: ${e.localizedMessage}", Toast.LENGTH_SHORT)
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


    /*private fun likePost(token: String, user_id: String, post_id: String) {
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

                viewModel.likeResult.observe(requireActivity()) { response ->
                    pd?.show() // Show loading indicator
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator


                            val message = response.data!!.message

                            Log.e("Like", "Response message: $message")
                            Log.e("Like", "Response code: ${response.data!!.code}")

                            val position = postList.indexOfFirst { it.id == post_id }
                            if (position != -1) {
                                val currentPost = postList[position]
                                val isLiked: Boolean
                                val likeCount: Int

                                when (message) {
                                    "Post liked successfully!" -> {
                                        isLiked = response.data!!.is_like
                                       // likeCount = currentPost.like_count + 1
                                       // likeCount = postList[0].like_count
                                        likeCount = response.data!!.like_count
                                    }

                                    "Post unliked successfully!" -> {
                                        isLiked = response.data!!.is_like
                                        likeCount = response.data!!.like_count
                                       // likeCount = postList[0].like_count
                                        *//*likeCount = maxOf(
                                            0,
                                            currentPost.like_count - 1
                                        )*//* // Prevent negative count
                                    }

                                    else -> return@observe
                                }

                                val updatedPost = currentPost.copy(
                                    is_like = isLiked,
                                    like_count = likeCount
                                )

                                postList[position] = updatedPost // ✅ Update List
                                postListAdapter.updatePostLike(
                                    position,
                                    updatedPost
                                ) // ✅ Refresh UI

                                Log.e("Like", "Response likeCount: ${postList[0].like_count}")


                            }
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss()
                            Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
                        }

                        is BaseResponse.Loading -> {
                            // Show loading indicator if needed
                        }
                    }
                }

                viewModel.likePost(
                    token = token,
                    user_id = userId,
                    post_id = post_id
                )

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Log.e("LikePostError", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(context, "An error occurred: ${e.localizedMessage}", Toast.LENGTH_SHORT)
                .show()
        }
    }*/


   /*private fun likePost(token: String, user_id: String, post_id: String) {
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

               pd!!.show()

               //viewModel.likeResult.observe(requireActivity()) { response ->
                   viewModel.likeResult.removeObservers(viewLifecycleOwner)
                   viewModel.likeResult.observe(viewLifecycleOwner) { response ->


                   when (response) {
                       is BaseResponse.Success -> {
                           pd!!.dismiss()


                           Log.e("LikeResponse", "Response Body: ${response.data}")
                           val message = response.data!!.message

                           Log.e("Like", "Server message: $message")
                           Log.e("Like", "Response code: ${response.data!!.code}")

                           val position = postList.indexOfFirst { it.id == post_id }
                           if (position != -1) {
                               val currentPost = postList[position]
                               val isLiked: Boolean
                               val likeCount: Int


                               when (message) {
                                   "Post liked successfully!" -> {
                                       isLiked = true
                                       //likeCount = currentPost.like_count!! + 1 // ✅ Increment
                                       likeCount = currentPost.like_count!! + 1
                                       // ✅ Increment
                                   }
                                   "Post unliked successfully!" -> {
                                       isLiked = false
                                       likeCount = maxOf(0, currentPost.like_count!! - 1) // ✅ Prevent negative count

                                   }
                                   else -> return@observe
                               }

                               val updatedPost = currentPost.copy(
                                   is_like = isLiked,
                                   like_count = likeCount
                               )

                               postList[position] = updatedPost // ✅ Update List
                               postListAdapter.notifyDataSetChanged()

                               postListAdapter.updatePostLike(position, updatedPost) // ✅ Refresh UI


                               Log.e("Like", "Updated likeCount: ${updatedPost.like_count}")
                           }
                       }

                       is BaseResponse.Error -> {
                           pd!!.dismiss()
                           Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
                       }

                       is BaseResponse.Loading -> {
                           // Show loading indicator if needed
                       }
                   }
               }

               viewModel.likePost(
                   token = token,
                   user_id = userId,
                   post_id = post_id
               )

           } else {
               pd!!.dismiss()
               Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
           }

       } catch (e: Exception) {
           pd!!.dismiss()
           Log.e("LikePostError", "Error occurred: ${e.localizedMessage}")
           Toast.makeText(context, "An error occurred: ${e.localizedMessage}", Toast.LENGTH_SHORT)
               .show()
       }
   }*/

    /*private fun likePost(token: String, user_id: String, post_id: String) {
        Log.d("LikePost", "Token: $token, UserId: $user_id, PostId: $post_id")

        if (!requireContext().isOnline) {
            Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            return
        }

        if (token.isEmpty() || user_id.isEmpty()) {
            Toast.makeText(context, "User details are missing. Please log in.", Toast.LENGTH_SHORT).show()
            return
        }

        pd?.show()

        viewModel.likeResult.removeObservers(viewLifecycleOwner)
        viewModel.likeResult.observe(viewLifecycleOwner) { response ->
            when (response) {
                is BaseResponse.Success -> {
                    pd?.dismiss()
                    Log.e("LikeResponse", "Response: ${response.data}")

                    val message = response.data!!.message
                    val position = postList.indexOfFirst { it.id == post_id }

                    if (position != -1) {
                        val currentPost = postList[position]
                        val isLiked = message == "Post liked successfully!"
                        val likeCount = if (isLiked) currentPost.like_count!! + 1 else maxOf(0, currentPost.like_count!! - 1)

                        val updatedPost = currentPost.copy(
                            is_like = isLiked,
                            like_count = likeCount
                        )

                        postList[position] = updatedPost
                        postListAdapter.updatePostLike(position, updatedPost)

                        Log.e("Like", "Updated likeCount: ${updatedPost.like_count!!}")
                    }
                }
                is BaseResponse.Error -> {
                    pd?.dismiss()
                    Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
                }
                is BaseResponse.Loading -> {
                    // Show loading if necessary
                }
            }
        }

        viewModel.likePost(token, user_id, post_id)
    }*/



    private fun showCustomDialog() {
        // Creating a MaterialAlertDialogBuilder instance
        val builder = MaterialAlertDialogBuilder(requireContext())

        // Inflate the custom view layout for the dialog
        val customLayout = layoutInflater.inflate(R.layout.post_dialog, null)

        // Find views in the custom layout
        val imgProfile = customLayout.findViewById<CircleImageView>(R.id.imgProfile)
        val tvDialogTitle = customLayout.findViewById<TextView>(R.id.tvUserName)
        val tvCompName = customLayout.findViewById<TextView>(R.id.tvCompName)
        etDialogInput = customLayout.findViewById<AppCompatEditText>(R.id.edtInput)
        capturedImageView = customLayout.findViewById<ImageView>(R.id.ivPostimg)
        val btnSubmit = customLayout.findViewById<Button>(R.id.btnPost)
        cameraButton = customLayout.findViewById<ImageView>(R.id.ivCamera)
        galleryButton = customLayout.findViewById<ImageView>(R.id.ivGallery)

        currentImageView = customLayout.findViewById(R.id.ivPostimg)

        // Setting values dynamically
        lifecycleScope.launch {
            tvDialogTitle.text =  TranslationHelper.translateText(requireActivity().username ?: "", requireContext().languageName)
            tvCompName.text =  TranslationHelper.translateText(requireActivity().designationName ?: "", requireContext().languageName)
        }

        Glide.with(imgProfile.context)
            .load(requireContext().user_profile)
            .placeholder(R.drawable.worker)
            .into(imgProfile)

        capturedImageView.visibility = View.GONE  // Hide initially

        // Set the custom layout in the builder
        builder.setView(customLayout)

        // Handle camera button click
        cameraButton.setOnClickListener {
            Log.e("click", "Camera button clicked")

            currentImageView = customLayout.findViewById<ImageView>(R.id.ivPostimg)

            //showPictureDialog()
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openCamera() // Your method to launch the camera intent
            } else {
                checkAndRequestPermissions()
            }
        }

        galleryButton.setOnClickListener {
            Log.e("click", "Camera button clicked")

            currentImageView = customLayout.findViewById<ImageView>(R.id.ivPostimg)

            openGallery()
           //showPictureDialog()
        }

        // Handle text input change
        etDialogInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val inputText = s.toString()
                btnSubmit.isClickable = inputText.isNotEmpty()
               /* btnSubmit.setTextColor(
                    if (inputText.isNotEmpty()) {
                        ContextCompat.getColor(context!!, R.color.white)
                    } else {
                        ContextCompat.getColor(context!!, R.color.grey)
                    }
                )*/

                btnSubmit.setBackgroundColor(
                    if (inputText.isNotEmpty()) {
                        ContextCompat.getColor(context!!, R.color.colorPrimary)
                    } else {
                        ContextCompat.getColor(context!!, R.color.grey)
                    }
                )
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Handle submit button click
        btnSubmit.setOnClickListener {
            submitPostData()
        }


        // builder.create().show()
        // Create and show the dialog
        dialogPost = builder.create()
        dialogPost.show()
    }


    private fun submitPostData() {
        if (requireActivity().isOnline) {

            Log.e("LoginActivity", "${requireActivity().token}")
            Log.e("LoginActivity", "${requireActivity().userid}")
            Log.e("LoginActivity", "${requireActivity().username}")
            Log.e("LoginActivity", "${requireActivity().roleId}")

            // Retrieve values from views
            //  val location = tvLocation?.text?.toString()
            val description = etDialogInput?.text?.toString()
            val image = imgBase64String
            val token = requireActivity().token
            val userId = requireActivity().userid
            val username = requireActivity().username
            val roleId = requireActivity().roleId
            val companyName = if (!requireActivity().designationName.isNullOrEmpty()) {
                requireActivity().designationName
            } else {
                requireContext().companyName
            }


            // Ensure at least one of the required fields is non-empty
            if (description.isNullOrEmpty() && image.isNullOrEmpty()) {
                Toast.makeText(
                    context,
                    "Please provide at least one Description or Image.",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            // Validate essential user data
            if (token.isNullOrEmpty() || userId.isNullOrEmpty() || username.isNullOrEmpty() || roleId.isNullOrEmpty()) {
                Toast.makeText(
                    context,
                    "User details are missing. Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            pd?.show()
            viewModel.addStoryResult.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is BaseResponse.Success -> {
                        pd?.dismiss()
                        //  Toast.makeText(context, response.data?.message, Toast.LENGTH_SHORT).show()
                       // observePostList()

                        if (requireContext().isOnline) {
                            viewModel.getStoryPostList(requireActivity().userid, keyword)
                        }
                        dialogPost.dismiss()

                    }

                    is BaseResponse.Error -> {
                        pd?.dismiss()
                        Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
                    }

                    is BaseResponse.Loading -> {
                        // Optionally show a progress indicator
                    }
                }
            }

            // Call the ViewModel method
            viewModel.addPost(
                token = token,
                user_id = userId,
                name = username,
                location = "",
                company_name = companyName.orEmpty(),
                description = description.orEmpty(),
                role = roleId,
                postImage = image.orEmpty()
            )
        } else {
            Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showPictureDialog() {
        val pictureDialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        pictureDialog.setTitle("Shram")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems) { dialog, which ->
            when (which) {
                0 -> openGallery()
                1 -> //openCamera()
                {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        openCamera() // Your method to launch the camera intent
                    } else {
                        checkAndRequestPermissions()
                    }

                }
            }
        }
        pictureDialog.show()
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    Glide.with(this).load(bitmap).into(currentImageView)
                    val base64String = convertBitmapToBase64(bitmap)

                    if (currentImageView.id == R.id.ivPostimg) {
                        imgBase64String = base64String
                        Log.e("tag", "Profile Base64 (Camera): $imgBase64String")
                    }


                    if (imgBase64String.isNullOrEmpty()) {
                        capturedImageView.visibility = View.GONE
                    } else {
                        capturedImageView.visibility = View.VISIBLE
                    }
                }

                GALLERY_REQUEST_CODE -> {
                    val uri = data?.data
                    Glide.with(this).load(uri).into(currentImageView)
                    val base64String = convertImageToBase64(uri!!)

                    if (currentImageView.id == R.id.ivPostimg) {
                        imgBase64String = base64String
                        Log.e("tag", "Profile Base64 (Gallery): $imgBase64String")
                    }

                    if (imgBase64String.isNullOrEmpty()) {
                        capturedImageView.visibility = View.GONE
                    } else {
                        capturedImageView.visibility = View.VISIBLE
                    }
                }
            }
        }
    }


    private fun convertImageToBase64(uri: Uri): String {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
        return convertBitmapToBase64(bitmap)
    }

    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun checkAndRequestPermissions() {
        if (REQUIRED_PERMISSIONS.any {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    it
                ) != PackageManager.PERMISSION_GRANTED
            }) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                CAMERA_REQUEST_CODE
            )
        } else {
            // showPictureDialog()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               // showPictureDialog()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Camera permission is required.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        // Find the menu item
        val postItem = menu.findItem(R.id.action_post)
        val notificationItem = menu.findItem(R.id.action_notification)

        // Show or hide based on fragment visibility
        postItem?.isVisible = true
        notificationItem?.isVisible = false

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_post -> {
              //  val intent = Intent(requireContext(), MyConnectionActivity::class.java)
                //startActivity(intent)
                showCustomDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }





}