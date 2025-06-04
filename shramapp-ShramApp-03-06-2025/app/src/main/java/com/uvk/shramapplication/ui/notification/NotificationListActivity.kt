package com.uvk.shramapplication.ui.notification

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.isuserlgin
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.roleId
import com.mahindra.serviceengineer.savedata.userid
import com.mahindra.serviceengineer.savedata.username
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.ActivityNotificationListBinding
import com.uvk.shramapplication.helper.CommenViewModel
import com.uvk.shramapplication.helper.TranslationHelper
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.response.NotificationData
import com.uvk.shramapplication.ui.employeer.worklist.WorkListEmployeerActivity
import com.uvk.shramapplication.ui.map.ListAdapter
import com.uvk.shramapplication.ui.map.LocationData
import kotlinx.coroutines.launch

class NotificationListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationListBinding
    private var pd: TransparentProgressDialog? = null
    private val viewModel by viewModels<CommenViewModel>()
    private lateinit var notifList: List<NotificationData>
    private lateinit var notifListAdapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backicon.setOnClickListener {
            finish()
        }
        lifecycleScope.launch {
            binding.textHeading.text =TranslationHelper.translateText(
                "Notification",
                languageName
            )

        }

        getNotifList(userid)

    }

    private fun getNotifList(userid: String) {
        try {
            if (isOnline) {
                viewModel.notificationListResult.observe(this) { response ->
                    pd?.show()
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss()
                            notifList = response.data!!.data
                            if (response.data?.data.isNullOrEmpty()) {
                                // If the data is null or empty

                               /* Toast.makeText(
                                    this@NotificationListActivity,
                                    response.data!!.message,
                                    Toast.LENGTH_SHORT
                                ).show()*/
                                binding.nodataimg.visibility = View.VISIBLE
                                binding.recyclerView.visibility = View.GONE

                                Glide.with(this@NotificationListActivity)
                                    .load(R.drawable.no_data_found)
                                    .into(binding.nodataimg)
                            } else {
                                // If the data is available
                                notifList = (response.data?.data
                                    ?: emptyList()) as List<NotificationData> // Extract the data, or use an empty list if null

                                binding.recyclerView.layoutManager = LinearLayoutManager(this)

                                notifListAdapter = NotificationAdapter(
                                    this,
                                    notifList,
                                    ::readNotification
                                )

                                binding.recyclerView.adapter = notifListAdapter
                            }


                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss()
                            Toast.makeText(this, response.msg, Toast.LENGTH_SHORT).show()
                        }

                        is BaseResponse.Loading -> {}
                    }
                }
                viewModel.notificationList(user_id = userid)
            } else {
                Toast.makeText(this, "Internet not connected", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Log.e("EmpGoogleMapActivity", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(this, "An error occurred: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun readNotification(notificationId: String,sender_id : String) {
        Log.e("NotiActivity", "NotificationAdapter id : ${notificationId}")

        try {
            if (isOnline) {
                viewModel.notificationReadResult.observe(this) { response ->
                    pd?.show()
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss()
                            viewModel.notificationList(user_id = userid)
                            Log.e("NotiActivity", "NotificationAdapter id : ${response.data}")
                            Log.e("NotiActivity", "NotificationAdapter msg : ${response.data?.message}")
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss()
                            Toast.makeText(this, response.msg, Toast.LENGTH_SHORT).show()
                        }

                        is BaseResponse.Loading -> {}
                    }
                }
                viewModel.notificationRead(user_id = userid,
                    notification_id = notificationId,
                    sender_id = sender_id)
            } else {
                Toast.makeText(this, "Internet not connected", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Log.e("EmpGoogleMapActivity", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(this, "An error occurred: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT)
                .show()
        }
    }


}


