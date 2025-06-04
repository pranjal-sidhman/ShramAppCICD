package com.uvk.shramapplication.ui.map

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.userid
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.ActivityEmpListBinding
import com.uvk.shramapplication.databinding.ActivityEmployeeListBinding
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.ui.employeer.home.employeelist.EmpListAdapter
import com.uvk.shramapplication.ui.login.LoginViewModel
import com.uvk.shramapplication.ui.map.root_map.RootMapActivity

class EmpListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmpListBinding
    private var pd: TransparentProgressDialog? = null
    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var userList: List<LocationData>
    private lateinit var empListAdapter: ListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmpListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backicon.setOnClickListener {
            finish()
        }

        binding.btnMap.setOnClickListener {

            val intent = Intent(this@EmpListActivity, RootMapActivity::class.java)
            startActivity(intent)

        }


        pd = TransparentProgressDialog(this, R.drawable.progress)


        getEmpList(userid)

    }


    private fun getEmpList(userid: String) {
        try {
            if (isOnline) {
                viewModel.getLocationResult.observe(this) { response ->
                    pd?.show()
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss()
                            userList = response.data!!.data
                            if (response.data?.data.isNullOrEmpty()) {
                                // If the data is null or empty

                                Toast.makeText(
                                    this@EmpListActivity,
                                    response.data!!.message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                binding.nodataimg.visibility = View.VISIBLE
                                binding.recyclerView.visibility = View.GONE

                                Glide.with(this@EmpListActivity)
                                    .load(R.drawable.no_data_found)
                                    .into(binding.nodataimg)
                            } else {
                                // If the data is available
                                userList = response.data?.data
                                    ?: emptyList() // Extract the data, or use an empty list if null

                                binding.recyclerView.layoutManager = LinearLayoutManager(this)

                                empListAdapter = ListAdapter(
                                    this,
                                    userList
                                )
                                binding.recyclerView.adapter = empListAdapter
                            }


                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss()
                            Toast.makeText(this, response.msg, Toast.LENGTH_SHORT).show()
                        }

                        is BaseResponse.Loading -> {}
                    }
                }
                viewModel.getLocation(userId = userid)
            } else {
                Toast.makeText(this, "Internet not connected", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Log.e("EmpGoogleMapActivity", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(this, "An error occurred: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT)
                .show()
        }
    }
}