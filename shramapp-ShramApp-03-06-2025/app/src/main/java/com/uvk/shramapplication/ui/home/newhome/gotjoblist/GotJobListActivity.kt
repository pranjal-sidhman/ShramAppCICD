package com.uvk.shramapplication.ui.home.newhome.gotjoblist

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
import com.uvk.shramapplication.databinding.ActivityGotJobListBinding
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.ui.home.newhome.Job
import com.uvk.shramapplication.ui.home.job.JobListViewModel
import com.mahindra.serviceengineer.savedata.isOnline

class GotJobListActivity : AppCompatActivity() {

    lateinit var binding : ActivityGotJobListBinding
    private lateinit var nodataImageView: ImageView
    private lateinit var gotJobAdapter: EmployeeGotJobsAdapter
    private lateinit var jobList: List<Job>
    private val viewModel by viewModels<JobListViewModel>()
    private var pd: TransparentProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding  = ActivityGotJobListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pd = TransparentProgressDialog(this, R.drawable.progress)
        nodataImageView = findViewById(R.id.nodataimg)

        binding.backicon.setOnClickListener {
            finish()
        }

        getGotJobList()

    }

    private fun getGotJobList() {
        try {
            if (isOnline) {
                viewModel.gotJobListResult.observe(this) { response ->
                    pd?.show() // Show loading indicator
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            if (response.data?.data.isNullOrEmpty()) {
                                // If the data is null or empty

                                Toast.makeText(
                                    this@GotJobListActivity,
                                    response.data!!.message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                nodataImageView.visibility = View.VISIBLE
                                binding.recyclerView.visibility = View.GONE

                                Glide.with(this@GotJobListActivity)
                                    .load(R.drawable.no_data_found)
                                    .into(nodataImageView)
                            } else {
                                // If the data is available
                                jobList  = (response.data?.data
                                    ?: emptyList()) as List<Job>

                                binding.recyclerView.layoutManager = LinearLayoutManager(this)

                                gotJobAdapter = EmployeeGotJobsAdapter(
                                    this,
                                    jobList
                                )

                                binding.recyclerView.adapter = gotJobAdapter

                            }
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            Toast.makeText(this@GotJobListActivity, response.msg, Toast.LENGTH_SHORT)
                                .show() // Show error message
                        }

                        is BaseResponse.Loading -> {
                            // Show loading indicator if needed
                        }
                    }
                }

                viewModel.getGotJobList()

            } else {
                Toast.makeText(this@GotJobListActivity, "Internet not connected", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("Job list", "Error occurred: ${e.localizedMessage}")

        }
    }


}