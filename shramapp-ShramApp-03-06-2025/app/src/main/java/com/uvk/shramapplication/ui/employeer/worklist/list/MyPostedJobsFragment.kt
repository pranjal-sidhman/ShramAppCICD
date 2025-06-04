package com.uvk.shramapplication.ui.employeer.worklist.list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.FragmentMyPostedJobsBinding
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.ui.employeer.home.EmployeerViewModel
import com.uvk.shramapplication.ui.employeer.response.EmployeerData
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.userid


class MyPostedJobsFragment : Fragment() {
    private var _binding: FragmentMyPostedJobsBinding? = null
    private val binding get() = _binding!!
    private var pd: TransparentProgressDialog? = null

    private val viewModel by viewModels<EmployeerViewModel>()
    private lateinit var postJobList: List<EmployeerData>
    private lateinit var jobAdapter: MyPostedJobsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMyPostedJobsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        pd = TransparentProgressDialog(requireActivity(), R.drawable.progress)

        getPostedJob()

        return root
    }


        private fun getPostedJob() {
            try {
            if (requireActivity().isOnline) {

                viewModel.postedJobListResult.observe(requireActivity()) { response ->
                    pd?.show() // Show loading indicator

                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            if (response.data?.data.isNullOrEmpty()) {

                                Toast.makeText(
                                    context,
                                    response.data!!.message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                binding.nodataimg.visibility = View.VISIBLE
                                binding.recyclerView.visibility = View.GONE

                                Glide.with(this)
                                    .load(R.drawable.no_data_found)
                                    .into(binding.nodataimg)
                            } else {
                                postJobList = (response.data?.data ?: emptyList()) as List<EmployeerData>
                                //dataList = avaiEmpList // Initialize imageList here

                                binding.recyclerView.layoutManager = LinearLayoutManager(context)

                                jobAdapter = MyPostedJobsAdapter(
                                    requireContext(),
                                    postJobList
                                )

                                binding.recyclerView.adapter = jobAdapter
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

                viewModel.getpostedJobList(requireActivity().userid)

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }

            } catch (e: Exception) {
                // Handle any exception that might occur during the process
                Log.e("TAG", "Error occurred: ${e.localizedMessage}")

            }
        }



}