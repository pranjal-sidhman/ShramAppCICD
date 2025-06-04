package com.uvk.shramapplication.ui.profile.myFollowers

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.userid


class MyFollowersFragment : Fragment() {

    private lateinit var connectionAdapter: MyFollowersAdapter
    private lateinit var connectionList: List<NetworkData>
    private val viewModel by viewModels<NetworkConViewModel>()
    private var pd: TransparentProgressDialog? = null
    private lateinit var bottomSheetCommentDialog: BottomSheetDialog
    private lateinit var llRemoveConn: LinearLayout
    lateinit var tvConnCount : TextView
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerViewComment: RecyclerView
    private lateinit var nodataImageView: ImageView
    var userIds : String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_followers, container, false)

        userIds = requireContext().userid//arguments?.getString("userId")
        Log.e("Tag","USER id : $userIds")

        recyclerView = view.findViewById(R.id.recyclerView)
        nodataImageView = view.findViewById(R.id.nodataimg)
        tvConnCount = view.findViewById(R.id.tvConnCount)

        getConnectionList()

        return view
    }

    private fun getConnectionList() {
        if (requireContext().isOnline) {

            val userId = userIds

            if ( userId.isNullOrEmpty() ) {
                Toast.makeText(requireContext(), "User details are missing. Please Log in.", Toast.LENGTH_SHORT).show()
                return
            }
            viewModel.myConnectionResult.observe(requireActivity()) { response ->
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

                            tvConnCount.text = response.data?.count?:"0"

                            recyclerView.layoutManager = LinearLayoutManager(requireContext())

                            connectionAdapter = MyFollowersAdapter(
                                requireContext(),
                                response.data!!.data,
                                ::removeConnection

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

            viewModel.getMyConnectionList( user_id = userId,"")

        } else {
            Toast.makeText(requireContext(), "Internet not connected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeConnection(network_id: String,name:String) {
        val bottomSheetView =
            LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_remove_user, null)

        // Create the BottomSheetDialog and set its content view
        bottomSheetCommentDialog = BottomSheetDialog(requireContext())
        bottomSheetCommentDialog.setContentView(bottomSheetView)

        llRemoveConn = bottomSheetView.findViewById(R.id.llRemoveConn)

        llRemoveConn.setOnClickListener {
            showDeleteConfirmationDialog(network_id,name)
        }


        // Show the dialog
        bottomSheetCommentDialog.show()
    }

    @SuppressLint("StringFormatInvalid")
    private fun showDeleteConfirmationDialog(networkId: String, name : String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.remove_conn_title)
        builder.setMessage(getString(R.string.remove_conn_message, name))

        builder.setPositiveButton(R.string.remove) { dialog, which ->
            // Perform the removal or action here
            removeUser(networkId)
        }

        builder.setNegativeButton(R.string.cancel) { dialog, which ->
            // Do nothing, just dismiss the dialog
            dialog.dismiss()
            bottomSheetCommentDialog.dismiss()
        }

        // Create and show the dialog
        builder.show()
    }

    private fun removeUser(networkId: String) {
        if (requireContext().isOnline) {

            val userId = userIds

            if (userId.isNullOrEmpty() ) {
                Toast.makeText(requireContext(), "User details are missing. Please Log in.", Toast.LENGTH_SHORT).show()
                return
            }
            viewModel.removeConnectionResult.observe(this) { response ->
                pd?.show() // Show loading indicator
                when (response) {
                    is BaseResponse.Success -> {
                        pd?.dismiss() // Dismiss loading indicator

                        Toast.makeText(requireContext(), response.data!!.message, Toast.LENGTH_SHORT)
                            .show()
                        bottomSheetCommentDialog.dismiss()
                        getConnectionList()

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

            viewModel.removeConnection(
                networkId = networkId,
                user_id = userId)

        } else {
            Toast.makeText(requireContext(), "Internet not connected", Toast.LENGTH_SHORT).show()
        }


    }


}