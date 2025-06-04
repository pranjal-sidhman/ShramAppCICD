package com.uvk.shramapplication.ui.networkconnection.myconnection

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.ActivityMyConnectionBinding
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.ui.networkconnection.NetworkConViewModel
import com.uvk.shramapplication.ui.networkconnection.NetworkData
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.roleId
import com.mahindra.serviceengineer.savedata.userid
import com.uvk.shramapplication.helper.CommenViewModel
import com.uvk.shramapplication.helper.TranslationHelper
import com.uvk.shramapplication.ui.chat.ChatActivity
import com.uvk.shramapplication.ui.wallet.WalletActivity
import kotlinx.coroutines.launch

class MyConnectionActivity : AppCompatActivity() {
    lateinit var binding : ActivityMyConnectionBinding
    private lateinit var connectionAdapter: MyConnectionAdapter
    private lateinit var connectionList: List<NetworkData>
    private val viewModel by viewModels<NetworkConViewModel>()
    private val commenViewModel by viewModels<CommenViewModel>()
    private var pd: TransparentProgressDialog? = null
    private  var bottomSheetCommentDialog: BottomSheetDialog? = null
    private lateinit var llRemoveConn: LinearLayout
    private lateinit var tvRemoveConnection: TextView
    var keyword : String = " "
    private var selectedEmployeeId: String? = null
    private var selectedUserName: String? = null
    private var selectedProfileImg: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyConnectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pd = TransparentProgressDialog(this, R.drawable.progress)

        binding.backicon.setOnClickListener { finish() }


        getConnectionList()
        observeViewModels()

        binding.ivClear.setOnClickListener {
            binding.etSearch.text.clear()
            keyword = ""
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()

                if (query.isEmpty()) {
                    searchData("") // Empty search
                }
                if (query.length >= 3) {
                    searchData(query) // Search API call
                }
            }
        })

    }

    private fun observeViewModels() {
        commenViewModel.callDeductStatusResult.observe(this) { response ->
            pd?.show()
            when (response) {
                is BaseResponse.Success -> {
                    pd?.dismiss()
                    if (response.data?.status == true) {
                        // Already deducted before, proceed directly
                        val intent = Intent(this, ChatActivity::class.java).apply {
                            putExtra("user_id", selectedEmployeeId)
                            putExtra("name", selectedUserName)
                            putExtra("profile_img", selectedProfileImg)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

                        }
                        startActivity(intent)
                    } else {
                        askForDeductionConfirmation()
                    }
                    Log.e("My Connection","chat onCreate : ${response}")
                }
                is BaseResponse.Error -> {
                    pd?.dismiss()
                    Toast.makeText(this, response.msg, Toast.LENGTH_SHORT).show()
                    Log.e("My Connection","chat onCreate : ${response}")
                }
                is BaseResponse.Loading -> pd?.show()
            }
        }

        commenViewModel.walletCallResult.observe(this) { response ->
            pd?.show()
            when (response) {
                is BaseResponse.Success -> {
                    pd?.dismiss()
                    lifecycleScope.launch {
                        val msg = TranslationHelper.translateText(
                            response.data?.message ?: "", languageName
                        )
                        Toast.makeText(this@MyConnectionActivity, msg, Toast.LENGTH_SHORT).show()

                        if (response.data?.status == true) {
                           // proceedToNextAction()
                            val intent = Intent(this@MyConnectionActivity, ChatActivity::class.java).apply {
                                putExtra("user_id", selectedEmployeeId)
                                putExtra("name", selectedUserName)
                                putExtra("profile_img", selectedProfileImg)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

                            }
                            startActivity(intent)
                        } else if (response.data?.status_code == 402) {
                            val intent = Intent(this@MyConnectionActivity, WalletActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }

                        Log.e("My Connection","call onCreate : ${response}")
                    }
                }
                is BaseResponse.Error -> {
                    pd?.dismiss()
                    Toast.makeText(this, response.msg, Toast.LENGTH_SHORT).show()
                    Log.e("My Connection","call onCreate : error${response}")
                }
                is BaseResponse.Loading -> pd?.show()
            }
        }
    }

    private fun askForDeductionConfirmation() {
        lifecycleScope.launch {
            val builder = AlertDialog.Builder(this@MyConnectionActivity)
            val msg = if (roleId == "2") {
                TranslationHelper.translateText(
                    "₹5 will be deducted from your wallet to proceed. Do you want to continue?",
                    languageName
                )
            } else {
                "Proceed?"
            }

            builder.setMessage(msg)
            builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                if (roleId == "2") {
                    commenViewModel.callEmployee(userid, selectedEmployeeId!!)
                } else {
                   // proceedToNextAction()
                    val intent = Intent(this@MyConnectionActivity, ChatActivity::class.java).apply {
                        putExtra("user_id", selectedEmployeeId)
                        putExtra("name", selectedUserName)
                        putExtra("profile_img", selectedProfileImg)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

                    }
                    startActivity(intent)
                }
                dialog.dismiss()
            }

            builder.setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }

            builder.create().show()
        }
    }

    private fun searchData(searchKey: String) {
        keyword = searchKey.trim()
        if (isOnline) {
            viewModel.getMyConnectionList( userid, keyword)
        }
    }

    private fun getConnectionList() {
        if (isOnline) {

            val userId = userid

            if ( userId.isNullOrEmpty() ) {
                Toast.makeText(this, "User details are missing. Please Log in.", Toast.LENGTH_SHORT).show()
                return
            }
            viewModel.myConnectionResult.observe(this) { response ->

                when (response) {
                    is BaseResponse.Success -> {


                        if (response.data?.data.isNullOrEmpty()) {

                            binding.nodataimg.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                            Glide.with(this)
                                .load(R.drawable.no_data_found)
                                .into(binding.nodataimg)
                        } else {
                            // If the data is available
                            connectionList = response.data?.data
                                ?: emptyList() // Extract the data, or use an empty list if null

                            lifecycleScope.launch {
                                binding.tvConnCount.text = TranslationHelper.translateText(
                                    response.data?.count?:"0",
                                    languageName
                                )
                            }

                          //  binding.tvConnCount.text = response.data?.count?:"0"

                            binding.recyclerView.layoutManager = LinearLayoutManager(this)

                            connectionAdapter = MyConnectionAdapter(
                                this,
                                response.data!!.data,
                                ::removeConnection,
                                ::getChatDeductStatus
                            )

                            binding.recyclerView.adapter = connectionAdapter

                        }
                    }

                    is BaseResponse.Error -> {

                    }

                    is BaseResponse.Loading -> {
                        // Show loading indicator if needed
                    }
                }
            }

            viewModel.getMyConnectionList( user_id = userId, keyword = keyword)

        } else {
            Toast.makeText(this, "Internet not connected", Toast.LENGTH_SHORT).show()
        }
    }



    private fun getChatDeductStatus( employeeId: String, userName: String, profileImg: String) {
        try {
            if (isOnline) {
                selectedEmployeeId = employeeId
                selectedUserName = userName
                selectedProfileImg = profileImg

                commenViewModel.callDeductStatus(userid, employeeId)

            } else {
                Toast.makeText(this@MyConnectionActivity, "Internet not connected", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("Job Req fragment", "Error occurred: call ${e.localizedMessage}")
        }
    }
    private fun removeConnection(network_id: String,name:String) {

        bottomSheetCommentDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)

        val dialogView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_remove_user, null)
        bottomSheetCommentDialog?.setContentView(dialogView)


        llRemoveConn = dialogView.findViewById(R.id.llRemoveConn)
        tvRemoveConnection = dialogView.findViewById(R.id.tvRemoveConnection)

        lifecycleScope.launch {
            tvRemoveConnection.text =
                TranslationHelper.translateText("Remove Connection", languageName)
        }

        llRemoveConn.setOnClickListener {
            showDeleteConfirmationDialog(network_id,name)
        }

        // Show the dialog
        bottomSheetCommentDialog?.show()
    }

    @SuppressLint("StringFormatInvalid")
    private fun showDeleteConfirmationDialog(networkId: String, name : String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.remove_conn_title)
        builder.setMessage(getString(R.string.remove_conn_message, name))

        builder.setPositiveButton(R.string.remove) { dialog, which ->
            // Perform the removal or action here
            removeUser(networkId)
        }

        builder.setNegativeButton(R.string.cancel) { dialog, which ->
            // Do nothing, just dismiss the dialog
            dialog.dismiss()
            bottomSheetCommentDialog?.dismiss()
        }

        // Create and show the dialog
        builder.show()
    }

    private fun removeUser(networkId: String) {
        if (isOnline) {

            val userId = userid

            if (userId.isNullOrEmpty() ) {
                Toast.makeText(this, "User details are missing. Please Log in.", Toast.LENGTH_SHORT).show()
                return
            }
            viewModel.removeConnectionResult.observe(this) { response ->
                pd?.show() // Show loading indicator
                when (response) {
                    is BaseResponse.Success -> {
                        pd?.dismiss() // Dismiss loading indicator

                        Toast.makeText(this@MyConnectionActivity, response.data!!.message, Toast.LENGTH_SHORT)
                            .show()
                        bottomSheetCommentDialog?.dismiss()
                        getConnectionList()

                    }

                    is BaseResponse.Error -> {
                        pd?.dismiss() // Dismiss loading indicator
                        Toast.makeText(this, response.msg, Toast.LENGTH_SHORT)
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
            Toast.makeText(this, "Internet not connected", Toast.LENGTH_SHORT).show()
        }


    }


}