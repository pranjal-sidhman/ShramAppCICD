package com.uvk.shramapplication.ui.post

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uvk.shramapplication.R
import com.uvk.shramapplication.ui.post.CommentListAdapter.CommentViewHolder
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.imageview.ShapeableImageView
import com.mahindra.serviceengineer.savedata.userid
import de.hdodenhof.circleimageview.CircleImageView


class CommentListAdapter(
    val context: Context,
    private var commentList: List<Comment>,
    val commentLike: (String, String) -> Unit,
    val commentReply: (String, String) -> Unit,
    val commentDelete: (String,String) -> Unit
) : RecyclerView.Adapter<CommentViewHolder>() {

    private var selectedPosition: Int = -1 // Initialize with no selection
    private var selectedReplyPosition: Int = -1 // Initialize with no selection

    // Hold the state of whether all replies should be shown
    private val showAllReplies = mutableMapOf<String, Boolean>()


    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile: ShapeableImageView = itemView.findViewById(R.id.imgProfile)
        val tvUserName: TextView = itemView.findViewById(R.id.tvName)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDesc)
        val btnLike: ImageView = itemView.findViewById(R.id.btnLikes)
        val repliesRecyclerView: RecyclerView = itemView.findViewById(R.id.repliesRecyclerView)
        val tvRelpyCount: TextView = itemView.findViewById(R.id.tvRelpyCount)
        val tvLikeCount: TextView = itemView.findViewById(R.id.tvLikeCount)
        val btnReply: TextView = itemView.findViewById(R.id.tvReply)
        val btnSeeMoreReplies: Button = itemView.findViewById(R.id.btnSeeMoreReplies)
        val llcomm: LinearLayoutCompat = itemView.findViewById(R.id.llcomm)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }


    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val commentModel = commentList[position]

        holder.tvUserName.text = commentModel.commented_user_name
        holder.tvDescription.text = commentModel.comment
        holder.tvLikeCount.text = commentModel.like_count
        holder.tvRelpyCount.text = commentModel.reply_count
        holder.tvTime.text = commentModel.comment_time

        Log.e("tag", "resp comment: ${commentModel.comment}  ${commentModel.is_like}")


        if (commentModel.is_like == true) {
            holder.btnLike.setImageResource(R.drawable.like)
            holder.btnLike.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary))
        } else {
            holder.btnLike.setImageResource(R.drawable.like)
            holder.btnLike.setColorFilter(ContextCompat.getColor(context, R.color.black))
        }


        // Change the background color based on selection
        if (position == selectedPosition) {
            holder.llcomm.setBackgroundColor(
                ContextCompat.getColor(context, R.color.light_blue)
            )
        } else {
            holder.llcomm.setBackgroundColor(
                ContextCompat.getColor(context, R.color.light_orange)
            )
        }




        holder.llcomm.setOnClickListener {
            if (context.userid == commentModel.story_post_user_id) {
                val previousPosition = selectedPosition
                selectedPosition = position // Update the selected position

                // Notify adapter to update the UI
                notifyItemChanged(previousPosition) // Deselect previous item
                notifyItemChanged(selectedPosition) // Highlight the new item
                showCommentDeleteDialog(context, commentModel.id,commentModel.story_post_id)
            }
        }


        if (position == selectedReplyPosition) {
            holder.btnReply.apply {
                typeface = ResourcesCompat.getFont(context, R.font.montserrat_semi_bold) // Set your custom font
                textSize = 16f // Optional: Set text size
                setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)) // Optional: Set text color
            }
        } else {
            holder.btnReply.apply {
                typeface = ResourcesCompat.getFont(context, R.font.montserrat_semi_bold) // Set your custom font
                //textSize = 16f // Optional: Set text size
                setTextColor(ContextCompat.getColor(context, R.color.sub_text)) // Optional: Set text color
            }
        }

        holder.btnReply.setOnClickListener {

            val previousPosition = selectedReplyPosition
            selectedReplyPosition = position // Update the selected position

            // Notify adapter to update the UI
            notifyItemChanged(previousPosition) // Deselect previous item
            notifyItemChanged(selectedReplyPosition) // Highlight the new item

            commentReply(commentModel.id, commentModel.story_post_id)

        }

        holder.btnLike.setOnClickListener {

            commentLike(commentModel.id, commentModel.story_post_id)

        }

        // Determine if all replies should be shown for this comment
        val isShowAllReplies = showAllReplies[commentModel.id] ?: false

        if (commentModel.replies.isNotEmpty()) {
            holder.repliesRecyclerView.layoutManager = LinearLayoutManager(context)
            holder.repliesRecyclerView.adapter = SubCommentAdapter(
                context,
                commentModel.replies,
                showAllReplies = isShowAllReplies
            )

            holder.repliesRecyclerView.visibility = View.VISIBLE
            holder.btnSeeMoreReplies.visibility = View.VISIBLE

            // Update button text based on the state
            holder.btnSeeMoreReplies.text =
                if (isShowAllReplies) "Collapse All Replies" else "See More Replies"

            // Handle the button click
            holder.btnSeeMoreReplies.setOnClickListener {
                val currentState = showAllReplies[commentModel.id] ?: false
                showAllReplies[commentModel.id] = !currentState // Toggle the state
                notifyItemChanged(position) // Update the view to reflect changes
            }
        } else {
            holder.repliesRecyclerView.visibility = View.GONE
            holder.btnSeeMoreReplies.visibility = View.GONE
        }
    }

    private fun showCommentDeleteDialog(context: Context, id: String, storyPostId: String) {
        val bottomSheetView =
            LayoutInflater.from(context).inflate(R.layout.bottom_sheet_delete_comment, null)

        // Create the BottomSheetDialog and set its content view
        val bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog.setContentView(bottomSheetView)

        val deleteComment = bottomSheetDialog.findViewById<LinearLayout>(R.id.llDeleteComment)

        deleteComment!!.setOnClickListener {

            val builder = AlertDialog.Builder(context)
            // builder.setTitle("Call Confirmation")
            builder.setMessage(context.getString(R.string.delete_comment_msg))

            builder.setPositiveButton("Delete") { dialog, _ ->
                Log.e("comment", "id : $id")
                commentDelete(id,storyPostId)
                dialog.dismiss()
                bottomSheetDialog.dismiss()
            }

            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                bottomSheetDialog.dismiss()
            }

            val alertDialog = builder.create()
            alertDialog.show()
        }

        // Show the bottom sheet dialog
        bottomSheetDialog.show()
    }


    override fun getItemCount() = commentList.size


    fun updateComments(newComments: List<Comment>) {
        commentList = newComments
        notifyDataSetChanged() // Notify RecyclerView of the updated data
    }

    fun resetSelection() {
        val previousPosition = selectedPosition
        selectedPosition = -1
        notifyItemChanged(previousPosition)
    }
}


class SubCommentAdapter(
    private val context: Context,
    private val subComments: List<Reply>,
    private val showAllReplies: Boolean = false // New parameter to track if all replies should be shown
) :
    RecyclerView.Adapter<SubCommentAdapter.SubCommentViewHolder>() {

    inner class SubCommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.tvName)
        val message: TextView = view.findViewById(R.id.tvDesc)
        val time: TextView = view.findViewById(R.id.tvTime)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCommentViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_sub_comment, parent, false)
        return SubCommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubCommentViewHolder, position: Int) {
        val subComment = subComments[position]

        // Only show the first comment if 'showAllReplies' is false
        if (showAllReplies || position == 0) {
            val subComment = subComments[position]
            holder.username.text = subComment.commented_user_name
            holder.message.text = subComment.comment
            holder.time.text = subComment.reply_time
        }

        //  holder.time.text = subComment.time
    }


    override fun getItemCount(): Int {
        // Show all sub-comments if showAllReplies is true, else show only the first one
        return if (showAllReplies) subComments.size else 1
    }


}
