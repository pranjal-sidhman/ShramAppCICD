package com.uvk.shramapplication.ui.chat.message

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.ActivityMessengerListBinding
import com.uvk.shramapplication.helper.CommenViewModel
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.response.MessageData
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.userid

class MessengerListActivity : AppCompatActivity() {
    lateinit var binding: ActivityMessengerListBinding
    private var pd: TransparentProgressDialog? = null

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: List<MessageData>
    private val viewModel by viewModels<CommenViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessengerListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backicon.setOnClickListener { finish() }

        pd = TransparentProgressDialog(this, R.drawable.progress)

        binding.btnRefresh.setOnClickListener {
            viewModel.getMessageList(user_id = userid)
        }


        getMessageList()
    }

    private fun getMessageList() {
        if (isOnline) {

            val userId = userid

            if (userId.isNullOrEmpty()) {
                Toast.makeText(
                    this@MessengerListActivity,
                    "User details are missing. Please Log in.",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            viewModel.messageListResult.observe(this) { response ->
                runOnUiThread {
                    pd?.show()
                }

                when (response) {
                    is BaseResponse.Success -> {
                        runOnUiThread {
                            pd?.dismiss()
                        }


                        if (response.data?.data.isNullOrEmpty()) {
                            // If the data is null or empty

                            Toast.makeText(
                                this@MessengerListActivity,
                                response.data!!.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            binding.nodataimg.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE

                            Glide.with(this@MessengerListActivity)
                                .load(R.drawable.no_data_found)
                                .into(binding.nodataimg)
                        } else {

                            binding.nodataimg.visibility = View.GONE
                            binding.recyclerView.visibility = View.VISIBLE
                            // If the data is available
                            messageList = response.data?.data
                                ?: emptyList() // Extract the data, or use an empty list if null

                            binding.recyclerView.layoutManager = LinearLayoutManager(this)

                            messageAdapter = MessageAdapter(
                                this,
                                messageList
                            )

                            binding.recyclerView.adapter = messageAdapter



                        }
                    }

                    is BaseResponse.Error -> {
                        runOnUiThread {
                            pd?.dismiss()
                        }

                        Toast.makeText(this@MessengerListActivity, response.msg?:"", Toast.LENGTH_SHORT).show()
                    }

                    is BaseResponse.Loading -> {
                        runOnUiThread {
                            pd?.show()
                        }

                    }
                }
            }

            viewModel.getMessageList(user_id = userid)


        } else {

            Toast.makeText(this@MessengerListActivity, "Internet not connected", Toast.LENGTH_SHORT).show()
        }
    }
}