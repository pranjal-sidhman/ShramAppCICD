package com.uvk.shramapplication.ui.employeer.worklist

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.uvk.shramapplication.R
import com.uvk.shramapplication.databinding.ActivityWorkListEmployeerBinding
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.ui.employeer.worklist.list.AcceptedJobFragment
import com.uvk.shramapplication.ui.employeer.worklist.list.job_request_list.JobRequestFragment
import com.uvk.shramapplication.ui.employeer.worklist.list.MyPostedJobsFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.mahindra.serviceengineer.savedata.languageName
import com.uvk.shramapplication.MainActivity
import com.uvk.shramapplication.helper.TranslationHelper
import kotlinx.coroutines.launch

class WorkListEmployeerActivity : AppCompatActivity() {
    lateinit var binding : ActivityWorkListEmployeerBinding
    private var pd: TransparentProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkListEmployeerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pd = TransparentProgressDialog(this, R.drawable.progress)

        binding.backicon.setOnClickListener { finish() }
        // Setup ViewPager2 with the adapter
        val adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // Connect TabLayout with ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            val defaultText = when (position) {
                0 -> "My Posted Job"
                1 -> "Job Requested List"
                2 -> "Accepted Job"
                else -> "My Posted Job"
            }

            tab.text = defaultText  // Set default text immediately

            // Launch coroutine to update text asynchronously
            lifecycleScope.launch {
                val translatedText = TranslationHelper.translateText(defaultText, languageName)
                tab.text = translatedText
            }
        }.attach()


        //  Set selected tab if passed from intent
        val tabIndex = intent.getIntExtra("tab_index", 0)
        binding.viewPager.currentItem = tabIndex

    }

    class ViewPagerAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> MyPostedJobsFragment()
                1 -> JobRequestFragment()
                2 -> AcceptedJobFragment()
                else -> MyPostedJobsFragment()
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this@WorkListEmployeerActivity, MainActivity::class.java)
        startActivity(intent)


        // Optional: Call super if you still want the default behavior
        super.onBackPressed()
    }
}