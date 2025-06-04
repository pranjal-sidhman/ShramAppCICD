package com.uvk.shramapplication.ui.post

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.mahindra.serviceengineer.savedata.isOnline
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.ActivitySignUpBinding
import com.uvk.shramapplication.databinding.ActivityStoryDetailBinding
import com.uvk.shramapplication.databinding.ActivityWorkDetailsBinding
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.ui.joblist.Job
import com.uvk.shramapplication.ui.registration.StateDTViewModel
import kotlinx.coroutines.launch

class StoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryDetailBinding

    private val viewModel by viewModels<PostViewModel>()

    private var pd: TransparentProgressDialog? = null
    private lateinit var storyList: List<StoryPostData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pd = TransparentProgressDialog(this, R.drawable.progress)

        binding.backicon.setOnClickListener { finish() }

        val storyPostId = intent?.getStringExtra("story_post_id")

        if (!storyPostId.isNullOrEmpty()) {
            fetchStoryDetail(storyPostId)
        } else {
            Toast.makeText(this, "No story ID found", Toast.LENGTH_SHORT).show()
        }


        Log.e("StoryDetailActivity","storyPostId : $storyPostId")
    }

    private fun fetchStoryDetail(storyPostId: String) {
        try {
                if (isOnline) {
                    viewModel.storyPostDetailsResult.observe(this) { response ->
                        pd?.show() // Show loading indicator
                        when (response) {
                            is BaseResponse.Success -> {
                                pd?.dismiss() // Dismiss loading indicator

                                // Debugging the response
                                Log.d("JobDetailsActivity", "apply API Response: ${response.data}")

                                if (response.data?.data.isNullOrEmpty()) {
                                    // If the data is null or empty
                                    Toast.makeText(
                                        this@StoryDetailActivity,
                                        response.data!!.message,
                                        Toast.LENGTH_SHORT
                                    ).show()

                                } else {
                                    // If the data is available
                                    storyList = response.data?.data
                                        ?: emptyList() // Extract the data, or use an empty list if null
                                    val story = storyList[0]

                                    // Debugging the job data
                                    Log.d("workDetailsActivity", "story: $story")
                                    lifecycleScope.launch {
                                        /* binding.tvTitle.text = TranslationHelper.translateText(
                                            job.title ?: "",
                                            languageName
                                        )*/


                                    }

                                }
                            }

                            is BaseResponse.Error -> {
                                pd?.dismiss() // Dismiss loading indicator
                                Toast.makeText(
                                    this@StoryDetailActivity,
                                    response.msg?:"",
                                    Toast.LENGTH_SHORT
                                )
                                    .show() // Show error message
                            }

                            is BaseResponse.Loading -> {
                                // Show loading indicator if needed
                            }
                        }
                    }

                    // Make the API call
                    viewModel.storyPostDetails(storyPostId!!)

                } else {
                    Toast.makeText(
                        this@StoryDetailActivity,
                        "Internet not connected",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } catch (e: Exception) {
                // Handle any exception that might occur during the process
                Log.e("Job list", "Error occurred saved Job: ${e.localizedMessage}")
            }
        }
}