package com.uvk.shramapplication.ui.worklist

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import com.uvk.shramapplication.R
import com.uvk.shramapplication.databinding.FragmentWorkListBinding
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.ui.worklist.list.AppliedJobListFragment
import com.uvk.shramapplication.ui.worklist.list.JobOffersListFragment
import com.uvk.shramapplication.ui.worklist.list.SavedJobListFragment
import com.google.android.material.tabs.TabLayoutMediator
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mahindra.serviceengineer.savedata.languageName
import com.uvk.shramapplication.MainActivity
import com.uvk.shramapplication.helper.TranslationHelper
import kotlinx.coroutines.launch

class WorkListFragment : Fragment() {

    private var _binding: FragmentWorkListBinding? = null
    private val binding get() = _binding!!
    private var pd: TransparentProgressDialog? = null

    companion object {
        private const val ARG_TAB_INDEX = "tab_index"

        fun newInstance(tabIndex: Int): WorkListFragment {
            val fragment = WorkListFragment()
            val args = Bundle()
            args.putInt(ARG_TAB_INDEX, tabIndex)
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true) // Enable options menu in fragment
        // Inflate the layout for this fragment
        _binding = FragmentWorkListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        pd = TransparentProgressDialog(requireActivity(), R.drawable.progress)

        // Setup ViewPager2 with the adapter
        val adapter = ViewPagerAdapter(this)  // Pass the Fragment instance
        binding.viewPager.adapter = adapter

        val defaultTabIndex = arguments?.getInt("tab_index", 0) ?: 0
        binding.viewPager.currentItem = defaultTabIndex


        // Connect TabLayout with ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            val defaultText = when (position) {
                0 -> "Job Offers"
                1 -> "Saved Jobs"
                2 -> "Applied Jobs"
                else -> "Job Offers"
            }

            tab.text = defaultText  // Set default text immediately

            // Launch coroutine to update text asynchronously
            lifecycleScope.launch {
                val translatedText = TranslationHelper.translateText(defaultText, requireContext().languageName)
                tab.text = translatedText
            }
        }.attach()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(requireContext(), MainActivity::class.java)
                //  intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                //   requireActivity().finish()
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> JobOffersListFragment()
                1 -> SavedJobListFragment()
                2 -> AppliedJobListFragment()
                else -> throw IllegalArgumentException("Invalid position")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        // Find the menu item
        val notificationItem = menu.findItem(R.id.action_notification)

        // Show or hide based on fragment visibility
        notificationItem?.isVisible = false

    }
}
