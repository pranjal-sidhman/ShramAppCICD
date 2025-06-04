package com.uvk.shramapplication.ui.chat


import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.uvk.shramapplication.R
import com.uvk.shramapplication.helper.ZoomImageUtils
import com.uvk.shramapplication.response.ChatItem
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(
    val context: Context,
    private val chatList: MutableList<ChatItem>,
    private val currentUserId: String,
    // private val onDeleteMessage: (List<String>) -> Unit, // Pass List of IDs
    private val onSelectionChangeListener: OnSelectionChangeListener,
    val profile: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_DATE_HEADER = 0
    private val TYPE_MESSAGE_SENT = 1
    private val TYPE_MESSAGE_RECEIVED = 2
    private val selectedMessages = mutableSetOf<String>()
    private val selectedSender = mutableSetOf<String>()
    var isSelectionMode = false

    // Define a callback to pass the delete type
    var onDeleteMessageWithType: ((List<String>, String) -> Unit)? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_DATE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_date, parent, false)
                DateHeaderViewHolder(view,context)
            }


            TYPE_MESSAGE_SENT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_send_message, parent, false)
                SentMessageViewHolder(view, context)


            }

            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_received_message, parent, false)
                ReceivedMessageViewHolder(view, context, profile)
            }
        }
    }

    /*override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty() && payloads.contains("SELECTION_CHANGED")) {
         //   val item = chatList[position] as ChatItem.MessageItem
          //  updateItemSelectionState(holder, item)

            val item = chatList[position]
            if (item is ChatItem.MessageItem) {
                updateItemSelectionState(holder, item)
            }

        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }*/
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.contains("SELECTION_CHANGED")) {
            val item = chatList[position]
            if (item is ChatItem.MessageItem) {
                updateItemSelectionState(holder, item)
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = chatList[position]) {
            is ChatItem.DateHeader -> (holder as DateHeaderViewHolder).bind(item)
            is ChatItem.MessageItem -> {
                if (holder is SentMessageViewHolder) {
                    holder.bind(item)
                } else if (holder is ReceivedMessageViewHolder) {
                    holder.bind(item)
                }

                updateItemSelectionState(holder, item)

                // Long Click for Selection
                holder.itemView.setOnLongClickListener {
                    if (!isSelectionMode) {
                        isSelectionMode = true
                        toggleSelection(item.message.id, item.message.sender_id)
                    }
                    true
                }

                // Single Click for Selection
                holder.itemView.setOnClickListener {
                    if (isSelectionMode) {
                        toggleSelection(item.message.id, item.message.sender_id)
                    }
                }
            }
        }
    }

   /* private fun updateItemSelectionState(
        holder: RecyclerView.ViewHolder,
        item: ChatItem.MessageItem
    ) {
        val isSelected =
            selectedMessages.contains(item.message.id) || selectedSender.contains(item.message.sender_id)
        val context = holder.itemView.context

        val color = if (isSelected) ContextCompat.getColor(
            context,
            R.color.light_blue
        ) else Color.TRANSPARENT

        // Animate smoothly for better selection effect
        holder.itemView.animate()
            .alpha(if (isSelected) 0.8f else 1.0f)
            .setDuration(150)
            .start()

        holder.itemView.setBackgroundColor(color)
    }*/
   private fun updateItemSelectionState(
       holder: RecyclerView.ViewHolder,
       item: ChatItem.MessageItem
   ) {
       val isSelected = selectedMessages.contains(item.message.id) ||
               selectedSender.contains(item.message.sender_id)

       val context = holder.itemView.context
       val targetColor = if (isSelected)
           ContextCompat.getColor(context, R.color.light_blue)
       else
           Color.TRANSPARENT

       holder.itemView.apply {
           // Only animate if background color is actually changing
           if (background !is ColorDrawable || (background as ColorDrawable).color != targetColor) {
               animate().alpha(if (isSelected) 0.8f else 1.0f).setDuration(150).start()
           }

           setBackgroundColor(targetColor)
       }
   }



    private fun toggleSelection(messageId: String, senderId: String) {
        val wasSelected = selectedMessages.contains(messageId) || selectedSender.contains(senderId)

        if (wasSelected) {
            selectedMessages.remove(messageId)
            selectedSender.remove(senderId)
        } else {
            selectedMessages.add(messageId)
            selectedSender.add(senderId)
        }

        if (selectedMessages.isEmpty()) {
            isSelectionMode = false
        }

        onSelectionChangeListener.onSelectionChanged(selectedMessages.size)

        // Refresh only the changed item using payload for better performance
        val position =
            chatList.indexOfFirst { it is ChatItem.MessageItem && it.message.id == messageId }

        if (position != -1) {
            notifyItemChanged(position, "SELECTION_CHANGED")
        }

    }


    override fun getItemViewType(position: Int): Int {
        return when (val item = chatList[position]) {
            is ChatItem.DateHeader -> TYPE_DATE_HEADER
            is ChatItem.MessageItem -> {
                if (item.message.sender_id.toString() == currentUserId)
                    TYPE_MESSAGE_SENT
                else
                    TYPE_MESSAGE_RECEIVED
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return when (val item = chatList[position]) {
            is ChatItem.MessageItem -> item.message.id.hashCode().toLong()
            is ChatItem.DateHeader -> item.date.hashCode().toLong()
        }
    }


    fun getSelectedMessageCount(): Int {
        return selectedMessages.size
    }

    fun getSelectedMessageIds(): List<String> {
        return selectedMessages.toList()
    }

    fun getSelectedSenderIds(): List<String> {
        return selectedSender.toList()
    }


    override fun getItemCount(): Int = chatList.size

    class DateHeaderViewHolder(view: View,private val context: Context) : RecyclerView.ViewHolder(view) {
        private val tvDate: TextView = view.findViewById(R.id.tvDate)
        fun bind(item: ChatItem.DateHeader) {
           // TranslationHelper.initialize(context)

            tvDate.text = item.date

            /*CoroutineScope(Dispatchers.Main).launch {
                val targetLan = context.languageName
                tvDate.text = TranslationHelper.translateText(item.date ?: "", targetLan)

            }*/
        }
    }

    class SentMessageViewHolder(view: View, private val context: Context) :
        RecyclerView.ViewHolder(view) {
        private val tvMessage: TextView = view.findViewById(R.id.tvMessage)
        private val tvTime: TextView = view.findViewById(R.id.tvDateTime)
        private val ivRead: ImageView = view.findViewById(R.id.ivRead)
        private val ivReadImg: ImageView = view.findViewById(R.id.ivReadImg)
        private val ivImage: ImageView = view.findViewById(R.id.ivImage)
        private val FLImgIcon: FrameLayout = view.findViewById(R.id.FLImgIcon)

        fun bind(item: ChatItem.MessageItem) {
            tvTime.text = item.message.created_at ?: ""
            // Hide ivRead if the message is null or empty, even if is_read is "1"
            if (item.message.is_read == "1" && !item.message.message.isNullOrEmpty()) {
                ivRead.visibility = View.VISIBLE
            } else {
                ivRead.visibility = View.GONE
            }

            if (!item.message.message.isNullOrEmpty()) {
                tvMessage.text =item.message.message
                tvMessage.visibility = View.VISIBLE
            } else {
                tvMessage.visibility = View.GONE
            }

            if (item.message.is_read == "1" && !item.message.image.isNullOrEmpty()) {

                ivReadImg.visibility = View.VISIBLE
            } else {
                ivReadImg.visibility = View.GONE

            }



            if (!item.message.image.isNullOrEmpty()) {
                val imgUrl = item.message.image
                if (imgUrl.endsWith(".pdf", true)) {
                    ivImage.setImageResource(R.drawable.pdf_img) // your PDF icon
                } else {
                    Glide.with(ivImage).load(imgUrl).into(ivImage)
                }
                ivImage.visibility = View.VISIBLE
                FLImgIcon.visibility = View.VISIBLE
            } else {
                ivImage.visibility = View.GONE
                FLImgIcon.visibility = View.GONE
            }


            item.message.image?.let { fileUrl ->
                ivImage.setOnClickListener {
                    if (fileUrl.endsWith(".pdf", ignoreCase = true)) {
                        // Open PDF file
                        ZoomImageUtils.openPdfFile(context, fileUrl)
                    } else {
                        // Assume it's an image
                        ZoomImageUtils.showZoomableImage(context, fileUrl)
                    }
                }
            }

        }




    }

   /* fun openPdfFile(context: Context, pdfUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.parse(pdfUrl), "application/pdf")
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No PDF viewer found", Toast.LENGTH_SHORT).show()
        }
    }*/
   fun openPdfFile(context: Context, pdfUrl: String) {
       val request = DownloadManager.Request(Uri.parse(pdfUrl))
       request.setTitle("Downloading PDF...")
       request.setDescription("Please wait")
       request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
       request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "downloaded_file.pdf")

       val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
       dm.enqueue(request)

       Toast.makeText(context, "Downloading PDF...", Toast.LENGTH_SHORT).show()
   }



    fun getUnreadSentMessageIds(): List<String> {
        return chatList.filterIsInstance<ChatItem.MessageItem>()
            .filter { it.message.receiver_id.toString() == currentUserId && it.message.is_read == "0" }
            .map { it.message.id }
    }

    fun markMessagesAsRead(readMessageIds: List<String>) {
        chatList.filterIsInstance<ChatItem.MessageItem>().forEach { item ->
            if (readMessageIds.contains(item.message.id)) {
                item.message.is_read = "1"
            }
        }
        notifyDataSetChanged()
    }


    fun showDeleteDialog(
        context: Context,
        messageIds: List<String>,
        senderIds: List<String>,
        currentUserId: String
    ) {
        val allMessagesFromCurrentUser = senderIds.all { it == currentUserId }

        val options = if (allMessagesFromCurrentUser) {
            arrayOf("Delete for Me", "Delete for Everyone")
        } else {
            arrayOf("Delete for Me")
        }

        AlertDialog.Builder(context)
            .setTitle("Delete Message")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> onDeleteMessageWithType?.invoke(messageIds, "for_me") // Delete for Me
                    1 -> if (allMessagesFromCurrentUser) onDeleteMessageWithType?.invoke(
                        messageIds,
                        "for_everyone"
                    ) // Delete for Everyone
                }
            }
            .show()
    }

    fun clearSelection() {
        selectedMessages.clear()
        selectedSender.clear()
        isSelectionMode = false
        notifyDataSetChanged() // Refresh UI to remove selection states
    }


    class ReceivedMessageViewHolder(
        view: View,
        private val context: Context,
        private val profile: String
    ) :
        RecyclerView.ViewHolder(view) {
        private val tvMessage: TextView = view.findViewById(R.id.tvMessageR)
        private val tvTime: TextView = view.findViewById(R.id.tvDateTimeR)
        private val ivProfile: ShapeableImageView = view.findViewById(R.id.ivProfileR)
        private val ivImage: ImageView = view.findViewById(R.id.ivImageR)


        fun bind(item: ChatItem.MessageItem) {
          //  TranslationHelper.initialize(context)

           tvTime.text = item.message.created_at

                if (!item.message.message.isNullOrEmpty()) {
                    tvMessage.text = item.message.message
                    tvMessage.visibility = View.VISIBLE
                } else {
                    tvMessage.visibility = View.GONE
                }


            Glide.with(ivProfile.context)  // Use context from ImageView
                .load(profile)
                .placeholder(R.drawable.worker)
                .into(ivProfile)

           /* if (!item.message.image.isNullOrEmpty()) {
                val imgUrl = item.message.image
                Glide.with(ivImage)
                    .load(imgUrl)
                    //  .placeholder(R.drawable.baby2)
                    .into(ivImage)
            } else {
                ivImage.visibility = View.GONE
            }*/

            if (!item.message.image.isNullOrEmpty()) {
                val imgUrl = item.message.image
                if (imgUrl.endsWith(".pdf", true)) {
                    ivImage.setImageResource(R.drawable.pdf_img) // your PDF icon
                } else {
                    Glide.with(ivImage).load(imgUrl).into(ivImage)
                }
                ivImage.visibility = View.VISIBLE
            } else {
                ivImage.visibility = View.GONE
            }



            /* ivImage.setOnClickListener {
                 ZoomImageUtils.showZoomableImage(context, item.message.image!!)
             }*/

            item.message.image?.let { fileUrl ->
                ivImage.setOnClickListener {
                    if (fileUrl.endsWith(".pdf", ignoreCase = true)) {
                        // Open PDF file
                        ZoomImageUtils.openPdfFile(context, fileUrl)
                    } else {
                        // Assume it's an image
                        ZoomImageUtils.showZoomableImage(context, fileUrl)
                    }
                }
            }
        }
    }
}


interface OnSelectionChangeListener {
    fun onSelectionChanged(selectedCount: Int)

}