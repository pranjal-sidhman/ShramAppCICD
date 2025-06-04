package com.uvk.shramapplication.ui.employeer.worklist.list.job_request_list

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.FragmentJobRequestBinding
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.ui.employeer.home.EmployeerViewModel
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.roleId
import com.mahindra.serviceengineer.savedata.token
import com.mahindra.serviceengineer.savedata.userid
import com.uvk.shramapplication.helper.CommenViewModel
import com.uvk.shramapplication.helper.TranslationHelper
import com.uvk.shramapplication.ui.chat.ChatActivity
import com.uvk.shramapplication.ui.wallet.WalletActivity
import kotlinx.coroutines.launch


class JobRequestFragment : Fragment() {
    private var _binding: FragmentJobRequestBinding? = null
    private val binding get() = _binding!!
    private var pd: TransparentProgressDialog? = null

    private val viewModel by viewModels<EmployeerViewModel>()
    private val commenViewModel by viewModels<CommenViewModel>()
    private lateinit var postJobList: List<JobItem>
    private lateinit var jobAdapter: JobRequestAdapter

    private var selectedEmployeeId: String? = null
    private var selectedUserName: String? = null
    private var selectedProfileImg: String? = null
    private var selectedEmployerId: String? = null
    private var selectedJobId: String? = null
    private var nextAction: String? = null
    private var mobile_no: String? = null


    val TAG = "JobRequestFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentJobRequestBinding.inflate(inflater, container, false)
        val root: View = binding.root

        pd = TransparentProgressDialog(requireActivity(), R.drawable.progress)

        getJobRequestList()
        observeViewModels()

        /* commenViewModel.callDeductStatusResult.observe(viewLifecycleOwner) { response ->
             pd?.show()

             when (response) {
                 is BaseResponse.Success -> {
                     pd?.dismiss()
                     if (response.data?.status == true) {
                         startChatActivity()
                     } else {
                         askForDeductionConfirmation()
                     }
                 }

                 is BaseResponse.Error -> {
                     pd?.dismiss()
                     Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
                 }

                 is BaseResponse.Loading -> pd?.show()
             }
         }

         commenViewModel.walletCallResult.observe(viewLifecycleOwner) { response ->
             pd?.show()

             when (response) {
                 is BaseResponse.Success -> {
                     pd?.dismiss()
                     lifecycleScope.launch {
                         if (response.data?.status == true) {
                             val msg = TranslationHelper.translateText(
                                 response.data.message ?: "",
                                 requireContext().languageName
                             )
                             Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                             startChatActivity()
                         } else {
                             Toast.makeText(context, "Deduction failed", Toast.LENGTH_SHORT).show()
                         }
                     }
                 }

                 is BaseResponse.Error -> {
                     pd?.dismiss()
                     Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
                 }

                 is BaseResponse.Loading -> pd?.show()
             }
         }*/


        return root
    }

    private fun observeViewModels() {
        commenViewModel.callDeductStatusResult.observe(requireActivity()) { response ->
            pd?.show()
            when (response) {
                is BaseResponse.Success -> {
                    pd?.dismiss()
                    if (response.data?.status == true) {
                        // Already deducted before, proceed directly
                        proceedToNextAction()
                    } else {
                        askForDeductionConfirmation()
                    }
                    Log.e("Job request Fragment", "chat onCreate : ${response}")
                }

                is BaseResponse.Error -> {
                    pd?.dismiss()
                    Toast.makeText(requireContext(), response.msg, Toast.LENGTH_SHORT).show()
                    Log.e("Job request Fragment", "chat onCreate : ${response}")
                }

                is BaseResponse.Loading -> pd?.show()
            }
        }

        commenViewModel.walletCallResult.observe(requireActivity()) { response ->
            pd?.show()
            when (response) {
                is BaseResponse.Success -> {
                    pd?.dismiss()
                    lifecycleScope.launch {
                        val msg = TranslationHelper.translateText(
                            response.data?.message ?: "", requireContext().languageName
                        )
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT)
                            .show()

                        if (response.data?.status == true) {
                            proceedToNextAction()
                        } else if (response.data?.status_code == 402) {
                            val intent =
                                Intent(requireContext(), WalletActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            requireContext().startActivity(intent)
                        }else if (response.data?.status_code == 404) {
                            val intent = Intent(requireContext(), WalletActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }

                        Log.e("Job request Fragment", "call onCreate : ${response}")
                    }
                }

                is BaseResponse.Error -> {
                    pd?.dismiss()
                    Toast.makeText(requireContext(), response.msg, Toast.LENGTH_SHORT).show()
                    Log.e("Job request Fragment", "call onCreate : error${response}")
                }

                is BaseResponse.Loading -> pd?.show()
            }
        }
    }


    private fun startChatActivity() {
        val intent = Intent(requireContext(), ChatActivity::class.java).apply {
            putExtra("user_id", selectedEmployeeId)
            putExtra("name", selectedUserName)
            putExtra("profile_img", selectedProfileImg)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        }
        startActivity(intent)
    }

    private fun askForDeductionConfirmation() {
        lifecycleScope.launch {
            val builder = AlertDialog.Builder(requireContext())
            val msg = if (requireContext().roleId == "2") {
                TranslationHelper.translateText(
                    "₹5 will be deducted from your wallet to proceed. Do you want to continue?",
                    requireContext().languageName
                )
            } else {
                "Proceed?"
            }

            builder.setMessage(msg)
            builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                if (requireContext().roleId == "2") {
                    commenViewModel.callEmployee(requireContext().userid, selectedEmployeeId!!)
                } else {
                    proceedToNextAction()
                }
                dialog.dismiss()
            }

            builder.setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }

            builder.create().show()
        }
    }

    private fun proceedToNextAction() {
        when (nextAction) {
            "chat" -> startChatActivity()
            "call" -> {
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:$mobile_no")
                startActivity(dialIntent)
            }
        }
    }

    private fun getJobRequestList() {
        try {
            if (requireActivity().isOnline) {

                viewModel.getJobRequestListResult.observe(requireActivity()) { response ->
                    pd?.show() // Show loading indicator

                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            if (response.data?.data.isNullOrEmpty()) {

                                binding.nodataimg.visibility = View.VISIBLE
                                binding.recyclerView.visibility = View.GONE

                                Glide.with(this)
                                    .load(R.drawable.no_data_found)
                                    .into(binding.nodataimg)
                            } else {
                                postJobList = (response.data?.data ?: emptyList()) as List<JobItem>
                                //dataList = avaiEmpList // Initialize imageList here

                                binding.recyclerView.layoutManager = LinearLayoutManager(context)

                                jobAdapter = JobRequestAdapter(
                                    requireContext(),
                                    postJobList,
                                    ::callingData,
                                    ::selectEmp,
                                    ::callDeductedAmount,
                                    ::deductionStatus
                                )

                                binding.recyclerView.adapter = jobAdapter
                            }



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

                viewModel.getJobRequestList(requireActivity().userid)

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e(TAG, "Error occurred: ${e.localizedMessage}")

        }
    }



    private fun deductionStatus(employerId: String, employeeId: String, userName: String, profileImg: String,nextaction :String) {
        try {
            if (requireContext().isOnline) {
                selectedEmployeeId = employeeId
                selectedUserName = userName
                selectedProfileImg = profileImg
                selectedEmployerId = employerId
                nextAction = nextaction
                commenViewModel.callDeductStatus(employerId, employeeId)
            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("Job Req fragment", "Error occurred: deduct ${e.localizedMessage}")
        }
    }


    private fun callDeductedAmount(employer_id: String, employee_id: String, mobileNo: String,nextaction: String) {
        try {
            if (requireContext().isOnline) {
                selectedEmployeeId = employee_id
                selectedEmployerId = employer_id
                nextAction = nextaction
                mobile_no = mobileNo
                commenViewModel.callEmployee(employer_id,employee_id)

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("Job Req fragment", "Error occurred: call ${e.localizedMessage}")
        }
    }



    fun selectEmp(employeeId: Int, jobId: Int) {
        try {
            if (requireActivity().isOnline) {

                viewModel.acceptSelectEmpResult.observe(this) { response ->
                    pd?.show() // Show loading indicator

                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            viewModel.getJobRequestList(requireActivity().userid)

                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            Toast.makeText(
                                context,
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

                viewModel.acceptSelectEmp(
                    requireContext().token!!,
                    requireContext().userid!!,
                    employeeId!!,
                    jobId.toString(),
                )

            } else {
                Toast.makeText(
                    context,
                    "Internet not connected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e(TAG, "Error occurred: ${e.localizedMessage}")

        }
    }

    fun callingData(employeeId: List<Int>, jobId: Int) {
        Log.e("tag","MAin Emp Id :${employeeId} ")
        Log.e("tag","job Id :${jobId} ")
        try {
            if (requireActivity().isOnline) {


                pd?.show()
                viewModel.addSelectedEmpResult.observe(this) { response ->

                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss()
                            // Toast.makeText(context, response.data?.message, Toast.LENGTH_SHORT).show()

                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss()
                            // Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
                            Log.e("API Response", "Error: ${response.msg}")
                        }

                        is BaseResponse.Loading -> { pd?.show() }
                    }
                }


                viewModel.addSelectedEmployees(requireContext().token!!, requireContext().userid!!,jobId!!, employeeId!!)

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e(TAG, "Error occurred: ${e.localizedMessage}")

        }
    }
}


