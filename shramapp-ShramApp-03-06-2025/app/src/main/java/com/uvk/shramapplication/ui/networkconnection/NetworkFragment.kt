package com.uvk.shramapplication.ui.networkconnection

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.ui.chat.message.MessengerListActivity
import com.uvk.shramapplication.ui.networkconnection.invitationlist.InvitationListActivity
import com.uvk.shramapplication.ui.networkconnection.myconnection.MyConnectionActivity
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.token
import com.mahindra.serviceengineer.savedata.userid
import com.uvk.shramapplication.MainActivity


class NetworkFragment : Fragment() {
    private var isBottomNavVisible = true   // Track if the bottom navigation is visible
    lateinit var recyclerView: RecyclerView
    lateinit var ivClear: ImageView
    lateinit var tvMyConnection: TextView
    lateinit var tvInviteCount: TextView
    lateinit var etSearch: EditText
    lateinit var llInvitation: LinearLayoutCompat
    private lateinit var nodataImageView: ImageView
    private lateinit var connectionAdapter: NetworkAdapter
    private lateinit var connectionList: List<NetworkData>
    private lateinit var invitationList: List<NetworkData>
    private val viewModel by viewModels<NetworkConViewModel>()
    private var pd: TransparentProgressDialog? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    var keyword : String = " "

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true) // Enable options menu in fragment
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_network, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pd = TransparentProgressDialog(context, R.drawable.progress)
        recyclerView = view.findViewById(R.id.recyclerView)
        ivClear = view.findViewById(R.id.ivClear)
        nodataImageView = view.findViewById(R.id.nodataimg)
        tvMyConnection = view.findViewById(R.id.tvMyConnection)
        llInvitation = view.findViewById(R.id.llInvitation)
        tvInviteCount = view.findViewById(R.id.tvInviteCount)
        etSearch = view.findViewById(R.id.etSearch)


        tvMyConnection.setOnClickListener {

            startActivity(Intent(context, MyConnectionActivity::class.java))

        }

        llInvitation.setOnClickListener {
            startActivity(Intent(context, InvitationListActivity::class.java))
        }


        try {
            swipeRefreshLayout = requireView().findViewById(R.id.swipeRefreshLayout)

            swipeRefreshLayout.setOnRefreshListener {
                fetchConnectionList() // Call the function to refresh data
                invitationList()
            }

            fetchConnectionList() // Initial data load
            invitationList()

        } catch (e: Exception) {
            Log.e("Network list", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(context, "An error occurred: ${e.localizedMessage}", Toast.LENGTH_SHORT)
                .show()
        }

        ivClear.setOnClickListener {
            etSearch.text.clear()
            searchData("") // Empty search
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()


                if (s.toString().length >= 3) {

                    if (query.isEmpty()) {
                        searchData("") // Empty search
                    }
                    if (query.length >= 3) {
                        searchData(query) // Search API call
                    }
                    ivClear.visibility = View.VISIBLE

                } else {
                    if (query.isEmpty()) {
                        searchData("") // Empty search
                    }
                    ivClear.visibility = View.GONE
                }
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(requireContext(), MainActivity::class.java)
                //  intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                //   requireActivity().finish()
            }
        })

    }

    private fun searchData(searchKey: String) {
        keyword = searchKey.trim()
        if (requireContext().isOnline) {
            viewModel.getConnectionList(requireActivity().userid, keyword)
        }
    }

    private fun invitationList() {
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
                               // llInvitation.visibility = View.GONE

                            } else {
                                // If the data is available
                                invitationList = response.data?.data
                                    ?: emptyList() // Extract the data, or use an empty list if null

                                llInvitation.visibility = View.VISIBLE
                                tvInviteCount.text = response.data?.count

                            }
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            context?.let {
                                Toast.makeText(it, response.msg ?: "Something went wrong", Toast.LENGTH_SHORT).show()
                            }
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
            Log.e("Invite list", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(context, "An error occurred: ${e.localizedMessage}", Toast.LENGTH_SHORT)
                .show()
        }
    }


    private fun observeConnectionList() {
        try {
            swipeRefreshLayout = requireView().findViewById(R.id.swipeRefreshLayout)

            swipeRefreshLayout.setOnRefreshListener {
                fetchConnectionList() // Call the function to refresh data
            }

            fetchConnectionList() // Initial data load

        } catch (e: Exception) {
            Log.e("Network list", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(context, "An error occurred: ${e.localizedMessage}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun fetchConnectionList() {
        if (requireContext().isOnline) {

            val userId = requireActivity().userid

            if (userId.isNullOrEmpty()) {
                Toast.makeText(context, "User details are missing. Please Log in.", Toast.LENGTH_SHORT).show()
                return
            }

            viewModel.networkConListResult.observe(requireActivity()) { response ->
               // pd?.show() // Show loading indicator

                when (response) {
                    is BaseResponse.Success -> {
                       // pd?.dismiss()
                        swipeRefreshLayout.isRefreshing = false // Stop the refresh animation

                        if (response.data?.data.isNullOrEmpty()) {
                           // Toast.makeText(context, response.data!!.message, Toast.LENGTH_SHORT).show()
                            nodataImageView.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                            Glide.with(requireContext())
                                .load(R.drawable.no_data_found)
                                .into(nodataImageView)
                        } else {
                            connectionList = response.data?.data ?: emptyList()

                            recyclerView.visibility = View.VISIBLE

                            recyclerView.layoutManager = GridLayoutManager(context, 2)

                            connectionAdapter = NetworkAdapter(
                                requireContext(),
                                response.data!!.data,
                                ::sendRequest,
                                ::cancleRequest
                            )

                            recyclerView.adapter = connectionAdapter
                            nodataImageView.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                        }
                    }

                    is BaseResponse.Error -> {
                       // pd?.dismiss()
                        swipeRefreshLayout.isRefreshing = false
                        context?.let {
                            Toast.makeText(it, response.msg ?: "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    }

                    is BaseResponse.Loading -> {
                        // Optional: Show loading indicator
                    }
                }
            }

            viewModel.getConnectionList(user_id = userId,keyword)

        } else {
            swipeRefreshLayout.isRefreshing = false
            Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
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

                            /*Toast.makeText(context, response.data!!.message, Toast.LENGTH_SHORT)
                                .show()*/

                            Log.e("Network", "Cancel send : ${response.data!!.message}")
                            Log.e("Network", "Cancel request code: ${response!!.data!!.code}")

                            observeConnectionList()

                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            context?.let {
                                Toast.makeText(it, response.msg ?: "Something went wrong", Toast.LENGTH_SHORT).show()
                            }
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

    private fun sendRequest(token: String, sender_id: String, receiver_id: String, position: Int) {
        try {
            if (requireContext().isOnline) {
                val token = requireActivity().token
                val userId = requireActivity().userid

                if (token.isNullOrEmpty() || userId.isNullOrEmpty()) {
                    Toast.makeText(context, "User details are missing. Please Log in.", Toast.LENGTH_SHORT).show()
                    return
                }

                viewModel.requestSendResult.observe(viewLifecycleOwner) { response ->  // <- better to use viewLifecycleOwner
                    pd?.show()
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss()

                            Log.e("Network", "request send : ${response.data!!.message}")
                            Log.e("Network", "request code: ${response.data!!.code}")

                            // ✅ Here: Update the adapter's list directly
                            if (position >= 0 && position < connectionList.size) {
                                connectionList[position].request_status = "pending" // Or "confirm" as per your logic
                                connectionAdapter.notifyItemChanged(position) //  Only refresh that item
                            }

                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss()
                            context?.let {
                                Toast.makeText(it, response.msg ?: "Something went wrong", Toast.LENGTH_SHORT).show()
                            }
                        }

                        is BaseResponse.Loading -> {
                            // Optional
                        }
                    }
                }

                viewModel.requestSend(
                    token = token,
                    sender_id = userId,
                    receiver_id = receiver_id
                )

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Log.e("Send request", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(context, "An error occurred: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }


    /* private fun sendRequest(token: String, sender_id: String, receiver_id: String) {

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
                 viewModel.requestSendResult.observe(requireActivity()) { response ->
                     pd?.show() // Show loading indicator
                     when (response) {
                         is BaseResponse.Success -> {
                             pd?.dismiss() // Dismiss loading indicator

                            *//* Toast.makeText(context, response.data!!.message, Toast.LENGTH_SHORT)
                                .show()*//*

                            Log.e("Network", "request send : ${response.data!!.message}")
                            Log.e("Network", "request code: ${response.data!!.code}")

                            observeConnectionList()

                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator
                            context?.let {
                                Toast.makeText(it, response.msg ?: "Something went wrong", Toast.LENGTH_SHORT).show()
                            }
                        }

                        is BaseResponse.Loading -> {
                            // Show loading indicator if needed
                        }
                    }
                }

                viewModel.requestSend(
                    token = token,
                    sender_id = userId,
                    receiver_id = receiver_id
                )

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("Send request", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(context, "An error occurred: ${e.localizedMessage}", Toast.LENGTH_SHORT)
                .show()
        }

    }*/


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        val notification = menu.findItem(R.id.action_notification)
        notification?.isVisible = false

        // Find the menu item
        val chatItem = menu.findItem(R.id.action_chat)

        // Show or hide based on fragment visibility
        chatItem?.isVisible = true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_chat -> {
                val intent = Intent(requireContext(), MessengerListActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



}