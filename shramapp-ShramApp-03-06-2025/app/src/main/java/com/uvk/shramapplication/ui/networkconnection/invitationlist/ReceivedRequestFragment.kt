package com.uvk.shramapplication.ui.networkconnection.invitationlist


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.ui.networkconnection.NetworkConViewModel
import com.uvk.shramapplication.ui.networkconnection.NetworkData
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.token
import com.mahindra.serviceengineer.savedata.userid


class ReceivedRequestFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    private lateinit var nodataImageView: ImageView
    private lateinit var inviteAdapter: InviteAdapter
    private lateinit var invitationList: List<NetworkData>
    private val viewModel by viewModels<NetworkConViewModel>()
    private var pd: TransparentProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_received_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pd = TransparentProgressDialog(context, R.drawable.progress)
        recyclerView = view.findViewById(R.id.recyclerView)
        nodataImageView = view.findViewById(R.id.nodataimg)


        invitationsList()

    }

    private fun invitationsList() {
        try {
            if (requireContext().isOnline) {

                val userId = requireActivity().userid

                if (userId.isNullOrEmpty()) {
                    Toast.makeText(
                        context,
                        "User details are missing. Please Log in.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                viewModel.invitationResult.observe(requireActivity()) { response ->
                    pd?.show() // Show loading indicator
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            if (response.data?.data.isNullOrEmpty()) {
                                // If the data is null or empty
                                Toast.makeText(context, response.data!!.message, Toast.LENGTH_SHORT)
                                    .show()
                                nodataImageView.visibility = View.VISIBLE
                                recyclerView.visibility = View.GONE
                                Glide.with(requireContext())
                                    .load(R.drawable.no_data_found)
                                    .into(nodataImageView)

                            } else {
                                // If the data is available
                                invitationList = response.data?.data
                                    ?: emptyList() // Extract the data, or use an empty list if null


                                inviteAdapter = InviteAdapter(
                                    requireContext(),
                                    invitationList,
                                    ::acceptRequest,
                                    ::rejectRequest

                                )

                                recyclerView.adapter = inviteAdapter

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

                viewModel.getinvitationList(user_id = userId)

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("Received request", "Error occurred: ${e.localizedMessage}")

        }
    }

    private fun acceptRequest(network_id: String) {
        try {
            if (requireContext().isOnline) {
                val token = requireActivity().token
                val userId = requireActivity().userid

                if (token.isNullOrEmpty() || userId.isNullOrEmpty()) {
                    Toast.makeText(
                        context,
                        "User details are missing. Please Log in.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                viewModel.acceptRequestResult.observe(requireActivity()) { response ->
                    when (response) {
                        is BaseResponse.Loading -> {
                            pd?.show() // Show loading indicator
                        }

                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            Toast.makeText(context, response.data!!.message, Toast.LENGTH_SHORT)
                                .show()

                            // Refresh the comment list
                            invitationsList()
                            Log.e("accept req ", "accept msg : ${response.data.message}")
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // Trigger the like action
                viewModel.requestAccept(
                    token = token,
                    networkId = network_id,
                    receiver_id = userId
                )

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("Received request", "Error occurred: ${e.localizedMessage}")

        }
    }

    private fun rejectRequest(network_id: String) {
        try {
            if (requireContext().isOnline) {
                val token = requireActivity().token
                val userId = requireActivity().userid

                if (token.isNullOrEmpty() || userId.isNullOrEmpty()) {
                    Toast.makeText(
                        context,
                        "User details are missing. Please Log in.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                viewModel.rejectRequestResult.observe(requireActivity()) { response ->
                    when (response) {
                        is BaseResponse.Loading -> {
                            pd?.show() // Show loading indicator
                        }

                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            Toast.makeText(context, response.data!!.message, Toast.LENGTH_SHORT)
                                .show()

                            // Refresh the comment list
                            invitationsList()
                            Log.e("accept req ", "accept msg : ${response.data.message}")
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // Trigger the like action
                viewModel.requestReject(
                    token = token,
                    networkId = network_id,
                    receiver_id = userId
                )

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("Received request", "Error occurred: ${e.localizedMessage}")

        }
    }


}