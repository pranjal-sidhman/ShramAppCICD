package com.uvk.shramapplication.ui.networkconnection.invitationlist


import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.uvk.shramapplication.R
import com.uvk.shramapplication.databinding.ActivityInvitationListBinding
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.ui.networkconnection.NetworkConViewModel
import com.uvk.shramapplication.ui.networkconnection.NetworkData
import com.uvk.shramapplication.ui.networkconnection.invitationlist.sendRequest.SendRequestFragment
import com.uvk.shramapplication.ui.networkconnection.myconnection.MyConnectionAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class InvitationListActivity : AppCompatActivity() {
    lateinit var binding : ActivityInvitationListBinding
    private lateinit var connectionAdapter: MyConnectionAdapter
    private lateinit var connectionList: List<NetworkData>
    private val viewModel by viewModels<NetworkConViewModel>()
    private var pd: TransparentProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvitationListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pd = TransparentProgressDialog(this, R.drawable.progress)

        binding.backicon.setOnClickListener { finish() }

        // Setup ViewPager2 with the adapter
        val adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter


        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            val tabView = LayoutInflater.from(this).inflate(R.layout.tab_item, null)
            val tabText = tabView.findViewById<TextView>(R.id.tabTitle)

            tabText.text = when (position) {
                0 -> "Received Requests"
                else -> "Sent Requests"
            }

            // Set a minimum width to prevent text cutoff
            tabView.minimumWidth = binding.tabLayout.width / 2

            tab.customView = tabView
        }.attach()


        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

           override fun onTabSelected(tab: TabLayout.Tab?) {
               tab?.customView?.setBackgroundResource(R.drawable.bt_round_shape_tab)
               val tabText = tab?.customView?.findViewById<TextView>(R.id.tabTitle)
               tabText?.apply {
                   setTextColor(Color.WHITE)
                   setPadding(12, 4, 12, 4)  // Add padding to prevent text cutoff
               }
           }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.customView?.setBackgroundResource(android.R.color.transparent)
                val tabText = tab?.customView?.findViewById<TextView>(R.id.tabTitle)
                tabText?.apply {
                    setTextColor(ContextCompat.getColor(this@InvitationListActivity, R.color.black))
                   // gravity = Gravity.CENTER
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })


    }

    class ViewPagerAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {
        override fun getItemCount(): Int = 2 // Two tabs (Received and Sent)

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> ReceivedRequestFragment()  // First tab: Received Requests
                else -> SendRequestFragment()  // Second tab: Sent Requests
            }
        }
    }

}