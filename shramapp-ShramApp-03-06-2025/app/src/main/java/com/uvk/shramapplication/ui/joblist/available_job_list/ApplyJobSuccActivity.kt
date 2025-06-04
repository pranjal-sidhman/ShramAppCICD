package com.uvk.shramapplication.ui.joblist.available_job_list

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.uvk.shramapplication.MainActivity
import com.uvk.shramapplication.databinding.ActivityApplyJobSuccBinding

class ApplyJobSuccActivity : AppCompatActivity() {

    lateinit var binding : ActivityApplyJobSuccBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityApplyJobSuccBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

    }
}