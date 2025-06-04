package com.uvk.shramapplication.ui.joblist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.FragmentDescriptionBinding
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.userid
import com.uvk.shramapplication.helper.TranslationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DescriptionFragment : Fragment() {

    private var jobId: String? = null
    private var jobIds: String? = null
    private val viewModel by viewModels<JobListViewModel>()
    private lateinit var jobList: List<Job>

    private lateinit var binding: FragmentDescriptionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for the fragment
         binding = FragmentDescriptionBinding.inflate(inflater, container, false)

        // Retrieve the jobId from arguments
        jobId = arguments?.getString("jobId")
        jobIds = arguments?.getString("jobIds")

        Log.e("descr","JobId : $jobId")
        Log.e("descr","JobIds : $jobIds")

        binding.tvAbjob.visibility = View.GONE
        binding.tvkeResp.visibility = View.GONE
        binding.tvQual.visibility = View.GONE

        getDetails()


        return binding.root
    }

    private fun getDetails() {

        try {
            if (requireContext().isOnline) {
                viewModel.jobDetailsResult.observe(viewLifecycleOwner) { response ->

                    when (response) {
                        is BaseResponse.Success -> {
                            Log.d("DescFagment", "API Response: ${response.data}")

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

                                    binding.tvAbjob.visibility = View.VISIBLE
                                    binding.tvkeResp.visibility = View.VISIBLE
                                    binding.tvQual.visibility = View.VISIBLE

                                    binding.tvAboutJob.text = TranslationHelper.translateText(
                                        job.description ?: "",
                                        targetLan
                                    )

                                    binding.tvKeyResp.text = TranslationHelper.translateText(
                                        job.key_responsibilities ?: "",
                                        targetLan
                                    )
                                    binding.tvqualification.text = TranslationHelper.translateText(
                                        job.qualification ?: "",
                                        targetLan
                                    )
                                }
                            }
                        }

                        is BaseResponse.Error -> {
                            Log.e("DescFragment", "${response.msg}")
                            Toast.makeText(context, response.msg?:"", Toast.LENGTH_SHORT)
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
            Log.e("Job list", "Error occurred job desription: ${e.localizedMessage}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Remove observers when the view is destroyed to prevent memory leaks
        viewModel.jobDetailsResult.removeObservers(viewLifecycleOwner)
    }


}
