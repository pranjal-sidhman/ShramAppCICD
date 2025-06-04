package com.uvk.shramapplication.ui.networkconnection.invitationlist.sendRequest

import android.annotation.SuppressLint
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
import com.uvk.shramapplication.ui.networkconnection.NetworkConViewModel
import com.uvk.shramapplication.ui.networkconnection.NetworkData
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.userid


class SendRequestFragment : Fragment() {

    private lateinit var connectionAdapter: SendRequestAdapter
    private lateinit var connectionList: List<NetworkData>
    private val viewModel by viewModels<NetworkConViewModel>()
    private var pd: TransparentProgressDialog? = null
    lateinit var recyclerView: RecyclerView
    private lateinit var nodataImageView: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_send_request, container, false)

        recyclerView = view.findViewById(R.id.recyclerViews)
        nodataImageView = view.findViewById(R.id.nodataimg)

        getSendRequestList()

        return view
    }

    private fun getSendRequestList() {
        if (requireContext().isOnline) {

            val userId = requireContext().userid

            if ( userId.isNullOrEmpty() ) {
                Toast.makeText(requireContext(), getString(R.string.login_msg), Toast.LENGTH_SHORT).show()
                return
            }
            viewModel.getSendRequestListResult.observe(requireActivity()) { response ->
                pd?.show() // Show loading indicator
                when (response) {
                    is BaseResponse.Success -> {
                        pd?.dismiss() // Dismiss loading indicator

                        if (response.data?.data.isNullOrEmpty()) {
                            // If the data is null or empty

                            Toast.makeText(requireContext(), response.data!!.message, Toast.LENGTH_SHORT)
                                .show()
                            nodataImageView.visibility = View.VISIBLE
                            Glide.with(requireContext())
                                .load(R.drawable.no_data_found)
                                .into(nodataImageView)
                        } else {
                            // If the data is available
                            connectionList = response.data?.data
                                ?: emptyList() // Extract the data, or use an empty list if null



                            recyclerView.layoutManager = LinearLayoutManager(requireContext())

                            connectionAdapter = SendRequestAdapter(
                                requireContext(),
                                response.data!!.data,
                                ::cancleRequest
                            )

                            recyclerView.adapter = connectionAdapter

                        }
                    }

                    is BaseResponse.Error -> {
                        pd?.dismiss() // Dismiss loading indicator
                        Toast.makeText(requireContext(), response.msg, Toast.LENGTH_SHORT)
                            .show() // Show error message
                    }

                    is BaseResponse.Loading -> {
                        // Show loading indicator if needed
                    }
                }
            }

            viewModel.getSendRequestList( user_id = userId)

        } else {
            Toast.makeText(requireContext(), "Internet not connected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cancleRequest( sender_id: String, network_id: String) {
        try {
            if (requireContext().isOnline) {

                Log.d("cancleRequest", "sender_id: $sender_id, network_id: $network_id")
                val userId = sender_id

                if (userId.isNullOrEmpty()) {
                    Toast.makeText(
                        context,
                        "User details are missing. Please Log in.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                viewModel.requestCancelResult.observe(requireActivity()) { response ->
                    pd?.show() // Show loading indicator
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            Toast.makeText(context, response.data!!.message, Toast.LENGTH_SHORT)
                                .show()

                            Log.e("Network", "Cancel send : ${response.data!!.message}")
                            Log.e("Network", "Cancel request code: ${response!!.data!!.code}")

                            getSendRequestList()

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

                viewModel.requestCancel(
                    sender_id = userId,
                    network_id = network_id
                )

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("cancel request", "Error occurred: ${e.localizedMessage}")
            /*  Toast.makeText(context, "An error occurred: ${e.localizedMessage}",
                  Toast.LENGTH_SHORT)
                  .show()*/
        }
    }
}