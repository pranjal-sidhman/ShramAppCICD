package com.uvk.shramapplication.ui.joblist

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.FragmentCompanyBinding
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.userid
import com.uvk.shramapplication.helper.TranslationHelper
import kotlinx.coroutines.launch


class CompanyFragment : Fragment() {
    private var jobId: String? = null
    private var jobIds: String? = null
    private val viewModel by viewModels<JobListViewModel>()
    private lateinit var jobList: List<Job>
    lateinit var binding: FragmentCompanyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCompanyBinding.inflate(inflater, container, false)

        jobId = arguments?.getString("jobId")
        jobIds = arguments?.getString("jobIds")
        Log.e("comp","JobId : $jobId")
        Log.e("comp","JobIds : $jobIds")

        binding.tvCompName.visibility = View.GONE

        getDetails()

        return binding.root
    }

    @SuppressLint("StringFormatInvalid")
    private fun getDetails() {
        try {
            if (requireContext().isOnline) {
                viewModel.jobDetailsResult.observe(viewLifecycleOwner) { response ->

                    when (response) {
                        is BaseResponse.Success -> {
                            Log.d("JobDetailsActivity", "API Response: ${response.data}")

                            if (response.data?.data.isNullOrEmpty()) {
                                Toast.makeText(
                                    context,
                                    response.data!!.message,
                                    Toast.LENGTH_SHORT
                                ).show()

                            } else {
                                jobList = response.data?.data ?: emptyList()
                                val job = jobList[0]



                                lifecycleScope.launch {
                                    val targetLan = requireContext().languageName

                                    val aboutComp = getString(R.string.about_comp,
                                        TranslationHelper.translateText(
                                            job.company_name ?: "",
                                            targetLan
                                        )
                                    )
                                    binding.tvCompName.visibility = View.VISIBLE

                                    binding.tvCompName.text = TranslationHelper.translateText(
                                        aboutComp ?: "",
                                        targetLan
                                    )
                                    binding.tvaboutComp.text = TranslationHelper.translateText(
                                        job.company_description ?: "",
                                        targetLan
                                    )

                                }
                            }
                        }

                        is BaseResponse.Error -> {
                            Log.e("CompFragment", "${response.msg}")
                            Toast.makeText(context, response.msg, Toast.LENGTH_SHORT)
                                .show()
                        }

                        is BaseResponse.Loading -> {
                            // Show loading indicator if needed
                        }
                    }
                }

                // Make the API call
                if(!jobId.isNullOrEmpty()) {
                    viewModel.jobDetails(jobId!!, requireContext().userid!!)
                }else{
                    viewModel.jobDetails(jobIds!!, requireContext().userid!!)
                }

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            Log.e("Job list", "Error occurred company: ${e.localizedMessage}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Remove observers when the view is destroyed to prevent memory leaks
        viewModel.jobDetailsResult.removeObservers(viewLifecycleOwner)
    }

}
