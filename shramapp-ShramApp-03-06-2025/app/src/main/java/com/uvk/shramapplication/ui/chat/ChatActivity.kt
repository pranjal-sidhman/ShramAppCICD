package com.uvk.shramapplication.ui.chat

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.ActivityChatBinding
import com.uvk.shramapplication.helper.CommenViewModel
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.helper.ZoomImageUtils
import com.uvk.shramapplication.response.ChatItem
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.token
import com.mahindra.serviceengineer.savedata.userid
import com.uvk.shramapplication.helper.TranslationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class ChatActivity : AppCompatActivity(), OnSelectionChangeListener {
    lateinit var binding: ActivityChatBinding
    var receiver_id: String? = null
    var name: String? = null
    var profile: String? = null
    var deleteMessageId: String? = null
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatList: List<ChatItem>
    private lateinit var unreadChatIds: List<String>
    private val viewModel by viewModels<CommenViewModel>()
    private var pd: TransparentProgressDialog? = null

    private var bottomSheetDialog: BottomSheetDialog? = null
    private var bottomSheetPDFDialog: BottomSheetDialog? = null
    private lateinit var ivCameraImg: ImageView
    private lateinit var ivPDFImg: ImageView
    private lateinit var tvFileName: TextView
    private lateinit var currentImageView: ImageView
    private lateinit var bitmap: Bitmap
    private lateinit var uri: Uri
    private var imgBase64String: String? = null
    private var selectedFileUri: Uri? = null
    private var count: Int = 0
    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2
    private val PDF_REQUEST_CODE = 3

    private val REQUIRED_PERMISSIONS = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.READ_MEDIA_IMAGES // For Android 13+
    )
    private var pollingJob: Job? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        TranslationHelper.initialize(this)

        binding.ivBack.setOnClickListener { finish() }

        pd = TransparentProgressDialog(this, R.drawable.progress)

        receiver_id = intent.getStringExtra("user_id")
        name = intent.getStringExtra("name")
        profile = intent.getStringExtra("profile_img")

        Log.e("Chat", "ids : $receiver_id")
        Log.e("Chat", "name : $name")
        Log.e("Chat", "profile : $profile")

        val chatId = intent.getStringExtra("chat_id")
        val userId = intent.getStringExtra("user_id")

        Log.e("ChatAct","chatId :  $chatId $userId")

        lifecycleScope.launch {
            binding.tvName.text = TranslationHelper.translateText(name ?: "", languageName)
        }

        Glide.with(binding.ivUserImage.context)
            .load(profile)
            .placeholder(R.drawable.worker)
            .into(binding.ivUserImage)

        binding.ivUserImage.setOnClickListener {
            ZoomImageUtils.showZoomableImage(this, profile!!)
        }


        getChatList()

        binding.ivInfo.setOnClickListener { view ->
            showPopupMenu(view)
        }


        // currentImageView = ivCameraImg
        binding.ivCamera.setOnClickListener {

            showPictureDialog()
        }
        binding.ivAttach.setOnClickListener {

            openPdfPicker()
        }



        binding.ivSend.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (!message.isNullOrEmpty()) {
                sendMessage(message, "")
            }
        }


        binding.ivDelete.setOnClickListener {
            val messageIds = chatAdapter.getSelectedMessageIds()
            Log.e("tag", "Selected Messages id: $messageIds")

            val senderIds = chatAdapter.getSelectedSenderIds()
            Log.e("tag", "Selected senderIds id: $senderIds")

            chatAdapter.onDeleteMessageWithType = { ids, deleteType ->
                Log.e("tag", "Deleting Messages: $ids with Type: $deleteType")
                // Call API or perform deletion action here
                chatDelete(userid, receiver_id, ids, deleteType)
            }

            chatAdapter.showDeleteDialog(this, messageIds, senderIds, userid)

        }


    }

    fun chatDelete(
        senderid: String,
        receiverId: String?,
        chatids: List<String>,
        deleteType: String
    ) {
        Log.e("this", "calling chat delete")

        if (isOnline) {

            val userId = userid

            if (userId.isNullOrEmpty()) {
                Toast.makeText(
                    this@ChatActivity,
                    "User details are missing. Please Log in.",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            viewModel.chatDeleteResult.observe(this) { response ->
                runOnUiThread {
                    pd?.show()
                }

                when (response) {
                    is BaseResponse.Success -> {
                        runOnUiThread {
                            pd?.dismiss()
                        }

                        Log.e("chat", "Chat delete code : ${response.data!!.code}")
                        Log.e("chat", "Chat delete msg : ${response.data!!.message}")
                        if (response.data!!.code == 200) {
                            viewModel.getChatList(sender_id = userid, receiver_id = receiver_id!!)
                            // Clear selection and update UI
                            chatAdapter.clearSelection()
                            count = 0
                            onSelectionChanged(0)
                        }

                    }

                    is BaseResponse.Error -> {
                        runOnUiThread {
                            pd?.dismiss()
                        }

                        Toast.makeText(this@ChatActivity, response.msg?:"", Toast.LENGTH_SHORT).show()
                    }

                    is BaseResponse.Loading -> {
                        runOnUiThread {
                            pd?.show()
                        }

                    }
                }
            }

            viewModel.chatDelete(
                sender_id = userid,
                receiver_id = receiver_id!!,
                delete_type = deleteType,
                chat_ids = chatids
            )


        } else {
            Toast.makeText(this@ChatActivity, "Internet not connected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteMessage(messageIds: List<String>) {
        // Perform API call or other logic to delete messages
        Log.e("this", "Deleting Messages: 34756 $messageIds")
    }

    override fun onSelectionChanged(selectedCount: Int) {
        // Update UI based on selection count (e.g., show/hide delete button)
        if (selectedCount > 0) {
            binding.tvCount.visibility = View.VISIBLE
            binding.ivDelete.visibility = View.VISIBLE
            binding.ivUserImage.visibility = View.GONE
            binding.tvName.visibility = View.GONE
            binding.ivInfo.visibility = View.GONE

            count = chatAdapter.getSelectedMessageCount()
            Log.e("tag", "Selected Messages count: $count")

            lifecycleScope.launch {
                binding.tvCount.text =
                    TranslationHelper.translateText(count.toString() ?: "", languageName)
            }
        } else {
            binding.tvCount.visibility = View.GONE
            binding.ivDelete.visibility = View.GONE
            binding.ivUserImage.visibility = View.VISIBLE
            binding.tvName.visibility = View.VISIBLE
            binding.ivInfo.visibility = View.VISIBLE
        }
    }

    private fun getChatList() {
        if (isOnline) {

            val userId = userid

            if (userId.isNullOrEmpty()) {
                Toast.makeText(
                    this@ChatActivity,
                    "User details are missing. Please Log in.",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            viewModel.chatListResult.observe(this) { response ->
                runOnUiThread {
                    pd?.show()
                }

                when (response) {
                    is BaseResponse.Success -> {
                        runOnUiThread {
                            pd?.dismiss()
                        }


                        if (response.data?.data.isNullOrEmpty()) {
                            binding.rvChat.visibility = View.GONE
                        } else {
                            lifecycleScope.launch(Dispatchers.Default) {
                                val chatItems = mutableListOf<ChatItem>()
                                response.data?.data?.forEach { chatDateGroup ->
                                    chatItems.add(ChatItem.DateHeader(chatDateGroup.date))
                                    chatDateGroup.messages.forEach { message ->
                                        chatItems.add(ChatItem.MessageItem(message))
                                    }
                                }

                                withContext(Dispatchers.Main) {


                                    chatAdapter = ChatAdapter(
                                        this@ChatActivity,
                                        chatItems,
                                        userid!!,
                                        // ::deleteMessage,
                                        this@ChatActivity, // Passing OnSelectionChangeListener
                                        profile!!
                                    )



                                    unreadChatIds = chatAdapter.getUnreadSentMessageIds()

                                    if (!unreadChatIds.isNullOrEmpty()) {

                                        markAsRead()

                                    }


                                    binding.rvChat.adapter = chatAdapter
                                    binding.rvChat.visibility = View.VISIBLE
                                }
                            }
                        }
                    }

                    is BaseResponse.Error -> {
                        runOnUiThread {
                            pd?.dismiss()
                        }

                        Toast.makeText(this@ChatActivity, response.msg?:"", Toast.LENGTH_SHORT).show()
                    }

                    is BaseResponse.Loading -> {
                        runOnUiThread {
                            pd?.show()
                        }

                    }
                }
            }

            viewModel.getChatList(sender_id = userid, receiver_id = receiver_id!!)


        } else {

            Toast.makeText(this@ChatActivity, "Internet not connected", Toast.LENGTH_SHORT).show()
        }
    }


    private fun markAsRead() {
        if (!isOnline) {

            Toast.makeText(this@ChatActivity, "Internet not connected", Toast.LENGTH_SHORT).show()
            return
        }

        if (unreadChatIds.isNullOrEmpty()) {
            Log.e("tag", "No unread messages to mark as read")
            return
        }

        val userId = userid
        if (userId.isNullOrEmpty()) {
            Toast.makeText(
                this@ChatActivity,
                "User details are missing. Please Log in.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Show loading indicator
        runOnUiThread { pd?.show() }

        viewModel.chatRead(
            token = token,
            sender_id = userid,
            receiver_id = receiver_id!!,
            chat_ids = unreadChatIds!!
        )

        viewModel.chatReadResult.observe(this) { response ->
            when (response) {
                is BaseResponse.Success -> {
                    runOnUiThread { pd?.dismiss() }
                    Log.e("tag", "Read API Success: ${response.data?.message}")

                    // Update read status for marked messages
                    chatAdapter.markMessagesAsRead(unreadChatIds)
                }

                is BaseResponse.Error -> {
                    runOnUiThread { pd?.dismiss() }
                    Log.e("tag", "Read API Error: ${response.msg}")
                }

                is BaseResponse.Loading -> {
                    runOnUiThread { pd?.show() }
                }
            }
        }
    }


    private fun showPictureDialog() {
        val pictureDialog = androidx.appcompat.app.AlertDialog.Builder(this)
        pictureDialog.setTitle("Shram")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems) { dialog, which ->
            when (which) {
                0 -> openGallery()
                1 -> //openCamera()
                {
                    if (ContextCompat.checkSelfPermission(
                            this,
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

    private fun openPdfPicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        startActivityForResult(intent,PDF_REQUEST_CODE)
       // pdfPickerLauncher.launch(intent)
    }

    /* private val pdfPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            val data: Intent? = result.data
            selectedFileUri = data?.data

            if (selectedFileUri != null) {
                llText.visibility = View.VISIBLE
                iv_aadharImage_d.visibility = View.GONE
                iv_aadharPdf.visibility = View.VISIBLE

                iv_aadharPdf.setOnClickListener {
                    openPdf(selectedFileUri)
                }
                aadharBase64String = uriToBase64(selectedFileUri!!) // Convert to Base64
                tvFileName.text = getFileName(selectedFileUri!!)
            }
        }
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    bitmap = data?.extras?.get("data") as Bitmap
                    uri = Uri.EMPTY // Add this to avoid uninitialized error
                    //Glide.with(this).load(bitmap).into(iv_ProfileImg)

                    val base64String = convertBitmapToBase64(bitmap)
                    imgBase64String = base64String
                    /* if (currentImageView.id == R.id.ivImage) {
                         imgBase64String = base64String
                         Log.e("tag", " Base64 (Camera): $imgBase64String")
                     }*/

                    if (!imgBase64String.isNullOrEmpty()) {
                        showDialog(imgBase64String!!)
                    }

                }

                GALLERY_REQUEST_CODE -> {
                    uri = data?.data!!
                    // Glide.with(this).load(uri).into(iv_ProfileImg)

                    val base64String = convertImageToBase64(uri!!)
                    imgBase64String = base64String
                    /* if (currentImageView.id == R.id.ivImage) {
                         imgBase64String = base64String
                         Log.e("tag", " Base64 (Gallery): $imgBase64String")
                     }*/

                    if (!imgBase64String.isNullOrEmpty()) {
                        showDialog(imgBase64String!!)
                    }
                }

                PDF_REQUEST_CODE -> {
                  /*  uri = data?.data!!
                    // Glide.with(this).load(uri).into(iv_ProfileImg)

                    val base64String = convertImageToBase64(uri!!)
                    imgBase64String = base64String

                    if (!imgBase64String.isNullOrEmpty()) {
                        showPDFDialog(imgBase64String!!)
                    }*/


                    selectedFileUri = data?.data
                    selectedFileUri?.let {

                        imgBase64String = uriToBase64(it)
                        showPDFDialog(imgBase64String!!)
                    }
                }


            }
        }
    }

    private fun uriToBase64(uri: Uri): String {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun openPdf(pdfUri: Uri?) {
        if (pdfUri == null) {
            Toast.makeText(this@ChatActivity, "Invalid PDF file", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(pdfUri, "application/pdf")
            intent.flags =
                Intent.FLAG_GRANT_READ_URI_PERMISSION  // Grant permission for external apps
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this@ChatActivity, "No PDF viewer found", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showPDFDialog(imgBase64String: String) {
        bottomSheetPDFDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val view = LayoutInflater.from(this).inflate(R.layout.item_chat_pdf_dailog, null)
        bottomSheetPDFDialog?.setContentView(view)

        ivPDFImg = view.findViewById(R.id.ivPdf)
        tvFileName = view.findViewById(R.id.tvFileName)
        val btnSend = view.findViewById<AppCompatImageView>(R.id.ivSend)
        val etMessage = view.findViewById<EditText>(R.id.etMessage)

        tvFileName.text = selectedFileUri.toString()
        // Auto open the PDF immediately when the dialog is shown
      //  openPdf(selectedFileUri)
        /*ivPDFImg.setOnClickListener {
            openPdf(selectedFileUri)
        }*/

        btnSend.setOnClickListener {
            val message = etMessage.text.toString().trim()
            if (message.isNotEmpty() || imgBase64String.isNotEmpty()) {
                sendMessage(message, imgBase64String)
                bottomSheetPDFDialog?.dismiss()
            }
        }

        bottomSheetPDFDialog?.show()
    }



   /* private fun showPDFDialog(imgBase64String: String) {

        bottomSheetPDFDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)

        val dialogView = LayoutInflater.from(this).inflate(R.layout.item_chat_pdf_dailog, null)

        bottomSheetPDFDialog?.setContentView(dialogView)


        ivPDFImg = dialogView.findViewById<ImageView>(R.id.ivPdf)
        val btnSend = dialogView.findViewById<AppCompatImageView>(R.id.ivSend)
        val etMessage = dialogView.findViewById<EditText>(R.id.etMessage)

        etMessage.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.rvChat.postDelayed({
                    binding.rvChat.scrollToPosition(binding.rvChat.adapter?.itemCount?.minus(1) ?: 0)
                }, 300)
            }
        }


        Log.e("tag", "chat Image : $imgBase64String")

        // if (this::uri.isInitialized) {
        if (this::uri.isInitialized && uri != Uri.EMPTY) {
            Glide.with(ivCameraImg)
                .load(uri)
                .into(ivCameraImg)
        } else {
            Glide.with(ivCameraImg)
                .load(bitmap)
                .into(ivCameraImg)
        }

        btnSend.setOnClickListener {

            val message = etMessage.text.toString().trim()
            if (!message.isNullOrEmpty() || !imgBase64String.isNullOrEmpty()) {
                sendMessage(message, imgBase64String)

            }

        }
        bottomSheetPDFDialog?.show()
    }*/

    private fun showDialog(imgBase64String: String) {

            bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)

            val dialogView = LayoutInflater.from(this).inflate(R.layout.item_chat_image_dailog, null)

            bottomSheetDialog?.setContentView(dialogView)


            ivCameraImg = dialogView.findViewById<ImageView>(R.id.ivImage)
            val btnSend = dialogView.findViewById<AppCompatImageView>(R.id.ivSend)
            val etMessage = dialogView.findViewById<EditText>(R.id.etMessage)

            etMessage.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    binding.rvChat.postDelayed({
                        binding.rvChat.scrollToPosition(binding.rvChat.adapter?.itemCount?.minus(1) ?: 0)
                    }, 300)
                }
            }


            Log.e("tag", "chat Image : $imgBase64String")

            // if (this::uri.isInitialized) {
            if (this::uri.isInitialized && uri != Uri.EMPTY) {
                Glide.with(ivCameraImg)
                    .load(uri)
                    .into(ivCameraImg)
            } else {
                Glide.with(ivCameraImg)
                    .load(bitmap)
                    .into(ivCameraImg)
            }

            btnSend.setOnClickListener {

                val message = etMessage.text.toString().trim()
                if (!message.isNullOrEmpty() || !imgBase64String.isNullOrEmpty()) {
                    sendMessage(message, imgBase64String)

                }

            }
            bottomSheetDialog?.show()
        }


    private fun sendMessage(message: String, imgBase64String: String) {
        if (isOnline) {

            val userId = userid

            if (userId.isNullOrEmpty()) {
                Toast.makeText(
                    this@ChatActivity,
                    "User details are missing. Please Log in.",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            viewModel.sendMessageResult.observe(this) { response ->
                pd?.show() // Show loading indicator

                when (response) {
                    is BaseResponse.Success -> {
                        pd?.dismiss()
                        Log.e("chat send", "code : ${response.data!!.code}")
                        Log.e("chat send", "mess : ${response.data!!.message}")

                        if (response.data.code == 200) {
                            binding.etMessage.text.clear()
                            bottomSheetDialog?.dismiss()
                            // Refresh chat list after sending a message
                            viewModel.getChatList(sender_id = userid, receiver_id = receiver_id!!)
                        }

                    }

                    is BaseResponse.Error -> {
                        pd?.dismiss()
                        Log.e("chat", "res : ${response.msg}")
                        Toast.makeText(this@ChatActivity, response.msg?:"", Toast.LENGTH_SHORT).show()
                    }

                    is BaseResponse.Loading -> {
                        pd!!.show()
                    }
                }
            }

            viewModel.sendMessage(
                token = token,
                senderId = userid,
                receiverId = receiver_id!!,
                message = message,
                image = imgBase64String
            )
        } else {
            Toast.makeText(this@ChatActivity, "Internet not connected", Toast.LENGTH_SHORT).show()
        }
    }


    private fun convertImageToBase64(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri)
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
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, CAMERA_REQUEST_CODE)
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
                showPictureDialog()
            } else {
                Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.menu_info, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_refresh -> {
                    // Perform refresh action here
                    viewModel.getChatList(sender_id = userid, receiver_id = receiver_id!!)
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    /*private fun startPollingChat() {
        pollingJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                getChatList() // Your existing function that fetches chat
                delay(5000) // Refresh every 5 seconds
            }
        }
    }

    private fun stopPollingChat() {
        pollingJob?.cancel()
    }


    override fun onResume() {
        super.onResume()
        startPollingChat()
    }

    override fun onPause() {
        super.onPause()
        stopPollingChat()
    }*/


}