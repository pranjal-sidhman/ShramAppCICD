package com.uvk.shramapplication.ui.jobgiver

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.uvk.shramapplication.databinding.ActivityJobGiverListBinding
import com.uvk.shramapplication.ui.joblist.MidWorkListModel

class JobGiverListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobGiverListBinding

    val jobList = listOf(
        MidWorkListModel("Developer", "Ram Patil", "2", "₹ 67000-79000", "Pune", "B.Sc", "2 years", "Full-time", "1234567890"),
        MidWorkListModel("Tester", "Bhushan Pawar", "1", "₹ 37400-67000", "Nashik", "M.Sc", "1 year", "Part-time", "1234567891"),
        MidWorkListModel("Manager", "Dinesh Jadhav C", "3", "₹ 80000 fixed ", "Mumbai", "MBA", "5 years", "Full-time", "1234567892"),
        MidWorkListModel("Designer", "Ganesh Rane", "2", "₹15600-39100", "Chennai", "BFA", "3 years", "Part-time", "1234567893")
    )

    private lateinit var jobAdapter: JobGiverListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobGiverListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        jobAdapter = JobGiverListAdapter(this,jobList)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = jobAdapter
    }
}