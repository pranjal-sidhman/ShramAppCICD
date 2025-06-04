package com.uvk.shramapplication.ui.networkconnection

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.ActivityNetworkDetailsBinding
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.ui.chat.ChatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.token
import com.mahindra.serviceengineer.savedata.userid
import com.uvk.shramapplication.helper.GlobalFunctions
import com.uvk.shramapplication.helper.TranslationHelper
import kotlinx.coroutines.launch

class NetworkDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityNetworkDetailsBinding

    private lateinit var connectionList: List<NetworkData>
    private lateinit var invitationList: List<NetworkData>
    private val viewModel by viewModels<NetworkConViewModel>()
    private var pd: TransparentProgressDialog? = null

    var profileImg: String? = null
    var name: String? = null
    var user_id: String? = null
    var networkMessage: String? = null
    var network_id: String? = null
    var details_id: String? = null
    var connect_msg: String? = null

    val TAG = "NetworkDetailsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNetworkDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pd = TransparentProgressDialog(this, R.drawable.progress)
        binding.backicon.setOnClickListener { finish() }

        network_id = intent.getStringExtra("net_id")!!
        networkMessage = intent.getStringExtra("network")
        connect_msg = intent.getStringExtra("connect")
        user_id = userid

        Log.e(TAG, "user_id $user_id")
        Log.e(TAG, "network_id $network_id")

        if(!networkMessage.isNullOrEmpty()){
            binding.btnMessage.visibility = View.GONE
        }

        if(!connect_msg.isNullOrEmpty()){
            binding.btnConnect.visibility = View.GONE
        }
        lifecycleScope.launch {
            binding.btnMessage.text = TranslationHelper.translateText(
                 "Message",
                languageName
            )
        }

        binding.btnMessage.setOnClickListener {
            val intent = Intent(this@NetworkDetailsActivity, ChatActivity::class.java)
            intent.putExtra("user_id", details_id)
            intent.putExtra("name", name)
            intent.putExtra("profile_img", profileImg)
            startActivity(intent)

        }

       /* binding.tvReadMore.setOnClickListener {
            // Toggle maxLines and ellipsize to show full text or truncated
            if (binding.tvabout.maxLines == 4) {
                // Expand to show full text
                binding.tvabout.maxLines = Integer.MAX_VALUE
                binding.tvReadMore.text = "Read Less"
            } else {
                // Collapse to 4 lines and show ellipsis
                binding.tvabout.maxLines = 4
                binding.tvReadMore.text = "Read More"
            }
        }*/


        connectionDetails(user_id!!,network_id!!)

    }

    private fun connectionDetails(userId: String, network_id: String) {
        try {
            if (isOnline) {

                val token = token

                if (token.isNullOrEmpty() || userId.isNullOrEmpty()) {
                    Toast.makeText(
                        this@NetworkDetailsActivity,
                        "User details are missing. Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                viewModel.connDetailsResult.observe(this) { response ->
                    pd?.show() // Show loading indicator
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            Log.e(TAG, "request send : ${response.data!!.message}")
                            Log.e(TAG, "request code: ${response.data!!.code}")

                            connectionList = response.data?.data ?: emptyList()

                            if (connectionList.isNotEmpty()) {
                                val connection = connectionList[0]

                                lifecycleScope.launch {
                                    binding.tvName.text = TranslationHelper.translateText(connection.name ?: "", languageName)
                                    binding.tvLoc.text = TranslationHelper.translateText(connection.address ?: "No address available", languageName)

                                }
                                val emailId = connection.email ?: ""
                                binding.tvEmail.text = GlobalFunctions.smartMaskEmail(emailId)



                                details_id = connection.id
                                name = connection.name
                                profileImg = connection.profile_image

                                if(!connection.designation.isNullOrEmpty()){
                                    lifecycleScope.launch {
                                        binding.tvDesignation.text = TranslationHelper.translateText(
                                            connection.designation ?: "",
                                            languageName
                                        )
                                    }
                                        binding.tvDesignation.text =  connection.designation
                                }else{
                                    lifecycleScope.launch {
                                        binding.tvDesignation.text = TranslationHelper.translateText(
                                            connection.company_name ?: "",
                                            languageName
                                        )
                                    }

                                }

                                // Load profile image
                                Glide.with(binding.imageView)
                                    .load(connection.profile_image)
                                    .placeholder(R.drawable.user)
                                    .into(binding.imageView)

                                if(connection.request_status.equals("pending")){
                                    binding.btnConnect.text = "Pending"
                                    binding.btnConnect.setTextColor(ContextCompat.getColor(this, R.color.black))
                                    // Disable the button so it becomes unclickable
                                    binding.btnConnect.isEnabled = true
                                }else if(connection.request_status.equals("confirm")){
                                    binding.btnConnect.text = "Confirm"
                                    binding.btnConnect.setTextColor(ContextCompat.getColor(this, R.color.black))
                                    // Disable the button so it becomes unclickable
                                    binding.btnConnect.isEnabled = false
                                }
                                else if(connection.request_status.equals("rejected")){
                                    binding.btnConnect.text = "Rejected"
                                    binding.btnConnect.setTextColor(ContextCompat.getColor(this, R.color.black))
                                    // Disable the button so it becomes unclickable
                                    binding.btnConnect.isEnabled = false
                                }else if(connection.request_status.isNullOrEmpty()){
                                    binding.btnConnect.text = "Connect"
                                    binding.btnConnect.setTextColor(ContextCompat.getColor(this, R.color.white))
                                }else if(connection.request_status.equals("not_connected")){
                                    binding.btnConnect.text = "Confirm"
                                    binding.btnConnect.setTextColor(ContextCompat.getColor(this, R.color.black))
                                    // Disable the button so it becomes unclickable
                                    binding.btnConnect.isEnabled = false
                                }else{
                                    binding.btnConnect.visibility = View.GONE
                                }

                                binding.btnConnect.setOnClickListener {

                                    if(connection.request_status.equals("pending")) {
                                        // cancleRequest(context.userid, networkData.id)
                                        Log.e("tag","Cancle request : ${userid} ${network_id}")
                                        showBottomSheetCancleDialog(userid, connection.network_id)
                                    }else{
                                        sendRequest(token, userid, network_id)
                                    }
                                }


                            }


                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            Toast.makeText(
                                this@NetworkDetailsActivity,
                                response.msg,
                                Toast.LENGTH_SHORT
                            )
                                .show() // Show error message
                        }

                        is BaseResponse.Loading -> {
                            // Show loading indicator if needed
                        }
                    }
                }

                viewModel.connectionDetails(
                    token = token,
                    user_id = userId,
                    network_id = network_id
                )

            } else {
                Toast.makeText(this, "Internet not connected", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("NetworkDetails", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(this, "An error occurred: ${e.localizedMessage}", Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun sendRequest(token: String, user_id: String, network_id: String) {

        try {
            if (isOnline) {

                val token = token
                val userId = userid

                if (token.isNullOrEmpty() || userId.isNullOrEmpty()) {
                    Toast.makeText(
                        this@NetworkDetailsActivity,
                        "User details are missing. Please Log in.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                viewModel.requestSendResult.observe(this) { response ->
                    pd?.show() // Show loading indicator
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            /* Toast.makeText(context, response.data!!.message, Toast.LENGTH_SHORT)
                                 .show()*/

                            Log.e("Network", "request send : ${response.data!!.message}")
                            Log.e("Network", "request code: ${response.data!!.code}")

                            connectionDetails(user_id,network_id)

                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            Toast.makeText(this@NetworkDetailsActivity, response.msg, Toast.LENGTH_SHORT)
                                .show() // Show error message
                        }

                        is BaseResponse.Loading -> {
                            // Show loading indicator if needed
                        }
                    }
                }

                viewModel.requestSend(
                    token = token,
                    sender_id = userId,
                    receiver_id = network_id
                )

            } else {
                Toast.makeText(this@NetworkDetailsActivity, "Internet not connected", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("Send request", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(this@NetworkDetailsActivity, "An error occurred: ${e.localizedMessage}", Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun showBottomSheetCancleDialog(userId: String, netId: String) {
        val bottomSheetView =
            LayoutInflater.from(this).inflate(R.layout.bottom_sheet_delete_comment, null)

        // Create the BottomSheetDialog and set its content view
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView)

        val deleteComment = bottomSheetDialog.findViewById<LinearLayout>(R.id.llDeleteComment)
        val tvMsg = bottomSheetDialog.findViewById<TextView>(R.id.tvMsg)
        tvMsg!!.text = "Cancel request"

        deleteComment!!.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            // builder.setTitle("Call Confirmation")
            builder.setMessage(getString(R.string.cancele_request_msg))

            builder.setPositiveButton(R.string.yes) { dialog, _ ->
                Log.e("comment", "id : $netId")
                cancleRequest(userId, netId)
                dialog.dismiss()
                bottomSheetDialog.dismiss()
            }

            builder.setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
                bottomSheetDialog.dismiss()
            }

            val alertDialog = builder.create()
            alertDialog.show()
        }

        // Show the bottom sheet dialog
        bottomSheetDialog.show()
    }

    private fun cancleRequest( sender_id: String, netId: String) {
        try {
            if (isOnline) {

                Log.d("cancleRequest", "sender_id: $sender_id, network_id: $network_id")
                val userId = sender_id

                if (userId.isNullOrEmpty()) {
                    Toast.makeText(
                        this@NetworkDetailsActivity,
                        "User details are missing. Please Log in.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                viewModel.requestCancelResult.observe(this) { response ->
                    pd?.show() // Show loading indicator
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            /*Toast.makeText(context, response.data!!.message, Toast.LENGTH_SHORT)
                                .show()*/

                            Log.e("Network", "Cancel send : ${response.data!!.message}")
                            Log.e("Network", "Cancel request code: ${response!!.data!!.code}")

                            connectionDetails(user_id!!,network_id!!)

                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            Toast.makeText(this, response.msg, Toast.LENGTH_SHORT)
                                .show() // Show error message
                        }

                        is BaseResponse.Loading -> {
                            // Show loading indicator if needed
                        }
                    }
                }

                viewModel.requestCancel(
                    sender_id = userId,
                    network_id = netId
                )

            } else {
                Toast.makeText(this, "Internet not connected", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("cancel request", "Error occurred: ${e.localizedMessage}")

        }
    }




}