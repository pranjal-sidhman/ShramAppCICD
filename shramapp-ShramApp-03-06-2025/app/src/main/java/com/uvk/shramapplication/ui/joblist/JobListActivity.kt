package com.uvk.shramapplication.ui.joblist

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.ActivityJobListBinding
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.userid

class JobListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobListBinding

    private lateinit var nodataImageView: ImageView
    private lateinit var jobAdapter: JobListAdapter
    private lateinit var jobList: List<Job>
    private val viewModel by viewModels<JobListViewModel>()
    private var pd: TransparentProgressDialog? = null
    var catId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pd = TransparentProgressDialog(this, R.drawable.progress)
        nodataImageView = findViewById(R.id.nodataimg)

        binding.backicon.setOnClickListener {
            finish()
        }

        getJobList()


    }

    private fun getJobList() {
        try {
            if (isOnline) {
                viewModel.jobListResult.observe(this) { response ->
                    pd?.show() // Show loading indicator
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            if (response.data?.data.isNullOrEmpty()) {
                                // If the data is null or empty

                                Toast.makeText(
                                    this@JobListActivity,
                                    response.data!!.message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                nodataImageView.visibility = View.VISIBLE
                                binding.recyclerView.visibility = View.GONE

                                Glide.with(this@JobListActivity)
                                    .load(R.drawable.no_data_found)
                                    .into(nodataImageView)
                            } else {
                                // If the data is available
                                jobList = response.data?.data
                                    ?: emptyList() // Extract the data, or use an empty list if null

                                binding.recyclerView.layoutManager = LinearLayoutManager(this)

                                jobAdapter = JobListAdapter(
                                    this,
                                    jobList
                                )

                                binding.recyclerView.adapter = jobAdapter

                            }
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            Toast.makeText(this@JobListActivity, response.msg, Toast.LENGTH_SHORT)
                                .show() // Show error message
                        }

                        is BaseResponse.Loading -> {
                            // Show loading indicator if needed
                        }
                    }
                }

                viewModel.getJobList(userid!!,catId,"","","")

            } else {
                Toast.makeText(this@JobListActivity, "Internet not connected", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("Job list", "Error occurred: ${e.localizedMessage}")

        }
    }


}


