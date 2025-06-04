package com.uvk.shramapplication.ui.employeer.home.jobPostList

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.ActivityPostedJobListBinding
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.ui.employeer.home.EmployeerViewModel
import com.uvk.shramapplication.ui.employeer.response.EmployeerData
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.token
import com.mahindra.serviceengineer.savedata.userid

class PostedJobListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostedJobListBinding
    private var pd: TransparentProgressDialog? = null

    private val viewModel by viewModels<EmployeerViewModel>()
    private lateinit var postJobList: List<EmployeerData>
    private lateinit var postedJobListAdapter: PostedJobListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityPostedJobListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pd = TransparentProgressDialog(this, R.drawable.progress)
        binding.backicon.setOnClickListener {
            finish()
        }


        getPostedJob()

    }

    private fun getPostedJob() {
        if (isOnline) {
            pd?.show() // Show loading indicator before making the API call

            viewModel.postedJobListResult.observe(this) { response ->
                when (response) {
                    is BaseResponse.Success -> {
                        pd?.dismiss() // Dismiss loading indicator

                        Log.e("tag", "Posted job resp : $response")

                        if (response.data?.data.isNullOrEmpty()) {
                            // No data available, show 'no data' image
                            Toast.makeText(
                                this@PostedJobListActivity,
                                response.data?.message ,
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.nodataimg.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                        } else {
                            // Data is available, show the RecyclerView
                            postJobList = response.data?.data ?: emptyList()
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.nodataimg.visibility = View.GONE

                            binding.recyclerView.layoutManager = LinearLayoutManager(this)
                            postedJobListAdapter = PostedJobListAdapter(this, postJobList,::deletePost)
                            binding.recyclerView.adapter = postedJobListAdapter
                            postedJobListAdapter.notifyDataSetChanged()
                        }
                    }

                    is BaseResponse.Error -> {
                        pd?.dismiss() // Dismiss loading indicator
                        Toast.makeText(this@PostedJobListActivity, response.msg, Toast.LENGTH_SHORT)
                            .show() // Show error message
                    }

                    is BaseResponse.Loading -> {
                        // Show loading indicator if needed
                        pd?.show()
                    }
                }
            }

            viewModel.getpostedJobList(userid)

        } else {
            Toast.makeText(this@PostedJobListActivity, "Internet not connected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deletePost(userId: String, jobId: String) {

        Log.e("tag","Job Id : $jobId $userId")
        try {
            if (isOnline) {

                val userId = userId

                if (userId.isNullOrEmpty()) {
                    Toast.makeText(
                        this@PostedJobListActivity,
                        "User details are missing. Please Log in.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                viewModel.deleteJobListResult.observe(this) { response ->
                    pd?.show() // Show loading indicator
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            Toast.makeText(this, response.data!!.message, Toast.LENGTH_SHORT)
                                .show()

                            Log.e("Network", "delete: ${response.data!!.message}")
                            Log.e("Network", "delete post code: ${response!!.data!!.code}")


                            viewModel.getpostedJobList(userid)
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            Toast.makeText(this, response.msg, Toast.LENGTH_SHORT)
                                .show() // Show error message
                        }

                        is BaseResponse.Loading -> {
                            // Show loading indicator if needed
                            pd?.show()
                        }
                    }
                }

                viewModel.getDeleteJobList(
                    token = token,
                    userId = userId,
                    jobId = jobId
                )

            } else {
                Toast.makeText(this@PostedJobListActivity, "Internet not connected", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("cancel request", "Error occurred: ${e.localizedMessage}")

        }

    }

}