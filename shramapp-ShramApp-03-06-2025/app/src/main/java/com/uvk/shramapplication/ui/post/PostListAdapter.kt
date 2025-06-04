package com.uvk.shramapplication.ui.post

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uvk.shramapplication.R
import com.uvk.shramapplication.helper.ZoomImageUtils
import com.uvk.shramapplication.helper.setBlueHashtagText
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.token
import com.mahindra.serviceengineer.savedata.userid
import com.uvk.shramapplication.helper.TranslationHelper
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostListAdapter(
    val context: Context,
    //private val postList: List<StoryPostData>,
    private val postList: MutableList<StoryPostData>,
    private var postLikeList: MutableList<StoryPostData>,
    val likePost: (String, String, String) -> Unit, // Accept a single post instead of full list
    val commentPost: (String) -> Unit,
    val deletePost: (String) -> Unit,
    val sharePost: (String) -> Unit
) : RecyclerView.Adapter<PostListAdapter.PostViewHolder>() {

    private var bottomSheetDialog: BottomSheetDialog? = null


    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile: CircleImageView = itemView.findViewById(R.id.imgProfile)
        val imgPost: ImageView = itemView.findViewById(R.id.imgPost)
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        val tvCompName: TextView = itemView.findViewById(R.id.tvCompName)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val btnDots: ImageView = itemView.findViewById(R.id.btnDots)
        val llLike: LinearLayout = itemView.findViewById(R.id.llLike)
        val llComment: LinearLayout = itemView.findViewById(R.id.llComment)
        val llRepost: LinearLayout = itemView.findViewById(R.id.llRepost)
        val ivLike: ImageView = itemView.findViewById(R.id.ivLike)
        val tvLikeText: TextView = itemView.findViewById(R.id.tvLikeText)
        val tvLikeCount: TextView = itemView.findViewById(R.id.tvLikeCount)
        val tvComm: TextView = itemView.findViewById(R.id.tvComm)
        val tvrePost: TextView = itemView.findViewById(R.id.tvrePost)
        val tvCom: TextView = itemView.findViewById(R.id.tvCom)
        val tvsend: TextView = itemView.findViewById(R.id.tvsend)
        val tvCommentCount: TextView = itemView.findViewById(R.id.tvCommentCount)
        val tvReadMore: TextView = itemView.findViewById(R.id.tvReadMore)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val postModel = postList[position]
        TranslationHelper.initialize(context)
        CoroutineScope(Dispatchers.Main).launch {
            val targetLan = context.languageName
            holder.tvUserName.text =
                TranslationHelper.translateText(postModel.name ?: "", targetLan)
            holder.tvCompName.text =
                TranslationHelper.translateText(postModel.company_name ?: "", targetLan)
            CoroutineScope(Dispatchers.Main).launch {
                holder.tvDescription.text =
                    TranslationHelper.translateText(
                        postModel.description ?: "",
                        context.languageName
                    )
            }
            holder.tvCommentCount.text =
                TranslationHelper.translateText(postModel.comment_count ?: "", targetLan)
            holder.tvTime.text =
                TranslationHelper.translateText(postModel.created_at ?: "", targetLan)


            setBlueHashtagText(context, holder.tvDescription, postModel.description)

            // Post a layout to ensure the text has been measured before checking the line count
            holder.tvReadMore.visibility = View.GONE
            holder.tvDescription.post {
                if (holder.tvDescription.lineCount >= 4) {
                    holder.tvReadMore.visibility = View.VISIBLE
                    holder.tvDescription.maxLines = 4
                } else {
                    holder.tvReadMore.visibility = View.GONE
                }
            }

            holder.tvReadMore.setOnClickListener {
                if (holder.tvDescription.maxLines == 4) {
                    // Expand to show full text
                    holder.tvDescription.maxLines = Integer.MAX_VALUE
                    CoroutineScope(Dispatchers.Main).launch {
                        holder.tvReadMore.text =
                            TranslationHelper.translateText("Read Less", context.languageName)
                    }
                } else {
                    // Collapse to 4 lines
                    holder.tvDescription.maxLines = 4
                    CoroutineScope(Dispatchers.Main).launch {
                        holder.tvReadMore.text =
                            TranslationHelper.translateText("Read More", context.languageName)
                    }
                }
            }


            // Load profile image
            if (!postModel.story_post_image.isNullOrEmpty()) {
                holder.imgPost.visibility = View.VISIBLE

                Glide.with(holder.itemView.context)
                    .load(postModel.story_post_image)
                    .placeholder(R.drawable.no_data_found)
                    .into(holder.imgPost)
            } else {
                holder.imgPost.visibility = View.GONE
            }

            holder.imgPost.setOnClickListener {
                ZoomImageUtils.showZoomableImage(context, postModel.story_post_image!!)
            }

            Glide.with(holder.imgProfile)
                .load(postModel.profile_image)
                .placeholder(R.drawable.worker)
                .into(holder.imgProfile)

            holder.btnDots.setOnClickListener {
                showBottomSheetShareDialog(context, postModel.id!!, postModel.user_id!!)
            }

            // Set like count
            CoroutineScope(Dispatchers.Main).launch {
                holder.tvLikeText.text =
                    TranslationHelper.translateText("Like", context.languageName)
                holder.tvComm.text =
                    TranslationHelper.translateText("Comment", context.languageName)
                holder.tvCom.text = TranslationHelper.translateText("Comment", context.languageName)
                holder.tvrePost.text =
                    TranslationHelper.translateText("Repost", context.languageName)
                holder.tvsend.text = TranslationHelper.translateText("Send", context.languageName)
            }
            holder.tvLikeCount.text = TranslationHelper.translateText(
                postModel.like_count.toString() ?: "",
                context.languageName
            )

            // Update Like Button UI
            if (postModel.is_like!! == true) {
                holder.ivLike.setImageResource(R.drawable.like)
                holder.ivLike.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary))
                holder.tvLikeText.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPrimary
                    )
                )
            } else {
                holder.ivLike.setImageResource(R.drawable.like)
                holder.ivLike.setColorFilter(ContextCompat.getColor(context, R.color.sub_text))
                holder.tvLikeText.setTextColor(ContextCompat.getColor(context, R.color.sub_text))
            }

            // Like Button Click
            holder.llLike.setOnClickListener {
                likePost(context.token, context.userid, postModel.id!!)
                Log.e("Post", "Like clicked for post ID: ${postModel.id!!}")
            }


            holder.llComment.setOnClickListener {
                // showBottomCommentDialog(context)
                commentPost(postModel.id!!)
            }

            holder.llRepost.setOnClickListener {
                showBottomRepostDialog(context)
            }
        }
    }

    fun updatePostLike(position: Int, updatedPost: StoryPostData) {
        postList[position] = updatedPost
        Handler(Looper.getMainLooper()).post { notifyItemChanged(position) }
    }


    fun updateList(newList: MutableList<StoryPostData>) {
        postList.clear()
        postList.addAll(newList)
        notifyDataSetChanged()
    }


    private fun showBottomRepostDialog(context: Context) {
        // Inflate the bottom sheet layout

        bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)

        val dialogView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_repost, null)

        bottomSheetDialog?.setContentView(dialogView)


        // Show the bottom sheet dialog
        bottomSheetDialog?.show()
    }


    // Function to show BottomSheetDialog
    @SuppressLint("MissingInflatedId")
    private fun showBottomSheetShareDialog(context: Context, postId: String, userId: String) {

        bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)

        val dialogView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_share, null)

        bottomSheetDialog?.setContentView(dialogView)

        val deletePost = dialogView.findViewById<LinearLayout>(R.id.option_delete_post)
        val tvSave = dialogView.findViewById<TextView>(R.id.tvSave)
        val tvShare = dialogView.findViewById<TextView>(R.id.tvShare)
        val option_share = dialogView.findViewById<LinearLayout>(R.id.option_share)
        val tvDelPost = dialogView.findViewById<TextView>(R.id.tvDelPost)
        val tvNoInt = dialogView.findViewById<TextView>(R.id.tvNoInt)
        val tvReport = dialogView.findViewById<TextView>(R.id.tvReport)

        CoroutineScope(Dispatchers.Main).launch {
            val targetLan = context.languageName
            tvSave.text = TranslationHelper.translateText("Save", targetLan)
            tvShare.text = TranslationHelper.translateText("Share", targetLan)
            tvDelPost.text = TranslationHelper.translateText("Delete Post", targetLan)
            tvNoInt.text = TranslationHelper.translateText("Not Interested", targetLan)
            tvReport.text = TranslationHelper.translateText("Report", targetLan)
        }

        option_share.setOnClickListener {
            sharePost(postId)

            bottomSheetDialog?.dismiss()
        }


        deletePost.visibility = View.GONE

        if (context.userid == userId) {
            deletePost.visibility = View.VISIBLE
            deletePost.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                // builder.setTitle("Call Confirmation")
                builder.setMessage(context.getString(R.string.delete_post_msg))

                builder.setPositiveButton(context.getString(R.string.yes)) { dialog, _ ->
                    Log.e("PostAdapter", "postId : $postId")
                    deletePost(postId)
                    dialog.dismiss()
                    bottomSheetDialog?.dismiss()
                }

                builder.setNegativeButton(context.getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                    bottomSheetDialog?.dismiss()
                }

                val alertDialog = builder.create()
                alertDialog.show()
            }
        }

        // Show the bottom sheet dialog
        bottomSheetDialog?.show()
    }


    override fun getItemCount() = postList.size


}



