package com.uvk.shramapplication.ui.worklist.list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.ui.joblist.Job
import com.uvk.shramapplication.ui.joblist.JobListViewModel
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.token
import com.mahindra.serviceengineer.savedata.userid


class JobOffersListFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    private lateinit var nodataImageView: ImageView
    private lateinit var jobAdapter: JobOffersListAdapter
    private lateinit var jobList: List<Job>
    private val viewModel by viewModels<JobListViewModel>()
    private var pd: TransparentProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_job_offers_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pd = TransparentProgressDialog(context, R.drawable.progress)
        recyclerView = view.findViewById(R.id.recyclerView)
        nodataImageView = view.findViewById(R.id.nodataimg)

        getJobList(requireContext()!!.userid)
    }

    private fun getJobList(userid: String) {
        try {
            if (requireContext().isOnline) {
                viewModel.requestedJobListResult.observe(requireActivity()) { response ->
                    pd?.show() // Show loading indicator
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            if (response.data?.data.isNullOrEmpty()) {
                                // If the data is null or empty

                                Toast.makeText(
                                    context,
                                    response.data!!.message,
                                    Toast.LENGTH_SHORT
                                ).show()

                                nodataImageView.visibility = View.VISIBLE
                                recyclerView.visibility = View.GONE

                                Glide.with(this)
                                    .load(R.drawable.no_data_found)
                                    .into(nodataImageView)
                            } else {
                                // If the data is available
                                jobList = response.data?.data
                                    ?: emptyList() // Extract the data, or use an empty list if null

                                recyclerView.layoutManager = LinearLayoutManager(context)

                                jobAdapter = JobOffersListAdapter(
                                    requireContext(),
                                    jobList,
                                    ::acceptRequest,
                                    ::rejectRequest
                                )
                                recyclerView.adapter = jobAdapter
                            }
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            Toast.makeText(context, response.msg?:"", Toast.LENGTH_SHORT)
                                .show() // Show error message
                        }

                        is BaseResponse.Loading -> {
                            // Show loading indicator if needed
                        }
                    }
                }

                viewModel.getRquestedJobList(requireContext().userid!!)

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("Job offer List Fragment", "Error occurred: ${e.localizedMessage}")

        }
    }

    private fun rejectRequest(job_id: String) {
        try {
            if (requireContext().isOnline) {

                val token = requireContext().token
                val userId = requireContext().userid

                if (token.isNullOrEmpty() || userId.isNullOrEmpty()) {
                    Toast.makeText(
                        context,
                        "User details are missing. Please Log in.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                viewModel.requestRejectResult.observe(this) { response ->
                    pd?.show() // Show loading indicator
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            Toast.makeText(context, response.data!!.message, Toast.LENGTH_SHORT)
                                .show()
                            getJobList(requireContext()!!.userid)
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator

                            Toast.makeText(context, response.msg, Toast.LENGTH_SHORT)
                                .show() // Show error message
                        }

                        is BaseResponse.Loading -> {
                            // Show loading indicator if needed
                        }
                    }
                }

                viewModel.requestReject(
                    token = token!!,
                    receiverId = userId!!,
                    jobId = job_id!!
                )

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("LikePostError", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(context, "An error occurred: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun acceptRequest(job_id: String) {
        try {
            if (requireContext().isOnline) {

                val token = requireContext().token
                val userId = requireContext().userid

                if (token.isNullOrEmpty() || userId.isNullOrEmpty()) {
                    Toast.makeText(
                        context,
                        "User details are missing. Please Log in.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                viewModel.requestAcceptResult.observe(this) { response ->
                    pd?.show() // Show loading indicator
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            Toast.makeText(context, response.data!!.message, Toast.LENGTH_SHORT)
                                .show()
                            getJobList(requireContext()!!.userid)
                           // startActivity(Intent(context,MainActivity::class.java))
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator

                            Toast.makeText(context, response.msg, Toast.LENGTH_SHORT)
                                .show() // Show error message
                        }

                        is BaseResponse.Loading -> {
                            // Show loading indicator if needed
                        }
                    }
                }

                viewModel.requestAccept(
                    token = token!!,
                    receiverId = userId!!,
                    jobId = job_id!!
                )

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("LikePostError", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(context, "An error occurred: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT)
                .show()
        }

    }

}