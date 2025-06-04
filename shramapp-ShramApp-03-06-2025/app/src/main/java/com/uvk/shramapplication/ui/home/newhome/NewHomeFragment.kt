package com.uvk.shramapplication.ui.home.newhome

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.messaging.FirebaseMessaging
import com.mahindra.serviceengineer.savedata.FCM_TOKENa
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.FragmentNewHomeBinding
import com.uvk.shramapplication.helper.CommenViewModel
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.response.SkillData
import com.uvk.shramapplication.ui.home.HomeViewModel
import com.uvk.shramapplication.ui.home.job.JobListViewModel
import com.uvk.shramapplication.ui.home.newhome.gotjoblist.GotJobListActivity
import com.uvk.shramapplication.ui.joblist.available_job_list.AvailableJobListActivity
import com.uvk.shramapplication.ui.login.LoginActivity
import com.uvk.shramapplication.ui.main_category.MainCategory
import com.uvk.shramapplication.ui.skillIndia.EnhanceSkillActivity
import com.uvk.shramapplication.ui.skillIndia.GovSkillAdapter
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.isuserlgin
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.roleId
import com.mahindra.serviceengineer.savedata.token
import com.mahindra.serviceengineer.savedata.userid
import com.uvk.shramapplication.MainActivity
import com.uvk.shramapplication.api.ApiClient
import com.uvk.shramapplication.ui.chat.message.MessengerListActivity
import com.uvk.shramapplication.ui.notification.NotificationListActivity
import kotlinx.coroutines.launch
import kotlin.math.abs


class NewHomeFragment : Fragment() {
    private var _binding: FragmentNewHomeBinding? = null
    private var pd: TransparentProgressDialog? = null

    private val binding get() = _binding!!

    private lateinit var gotJobAdapter: GotJobAdapter
    private lateinit var availableobAdapter: AvailableJobAdapter
    private lateinit var govSkillAdapter: GovSkillAdapter
    private val handler = Handler(Looper.getMainLooper())
    private var dataList: List<Job> = emptyList()
    private var skillDataList = ArrayList<SkillData>() // SkillData is your model class
    private lateinit var jobList: List<Job>
    private lateinit var mainCatList: List<MainCategory>
    private val viewModel by viewModels<JobListViewModel>()
    private val commensviewModel by viewModels<CommenViewModel>()
    private val viewModelSave by viewModels<com.uvk.shramapplication.ui.joblist.JobListViewModel>()
    var catId: String = ""
    var keyword: String = ""
    private var notificationCount = 0
    private lateinit var textNotificationCount: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        setHasOptionsMenu(true) // Enable options menu in fragment
        _binding = FragmentNewHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        pd = TransparentProgressDialog(requireActivity(), R.drawable.progress)

        Log.e(
            "goHomeEmp",
            "isuserlgin: ${requireContext().isuserlgin}, roleId: ${requireContext().roleId}, ${requireContext().languageName} "
        )
        (activity as? MainActivity)?.notificationCountLiveData?.observe(viewLifecycleOwner) { count ->
            updateNotificationCount(count) // <-- UPDATE THE BADGE EVERY TIME
        }
        getNotificationCount()
        getFilterCategory()
        getGotJobList()
        getAvailablejob()
        getGovSkillData()

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val token = task.result
                    Log.d("mytag", "FCM Token: $token")
                    // Here you can send the token to your server or perform any other action
                    context?.FCM_TOKENa = token
                    sendToken(token)
                } else {
                    Log.e("mytag", "Failed to get FCM token: ${task.exception}")
                }
            }

        binding.tvGotSeeAll.setOnClickListener {

            if (requireContext().isuserlgin) {
                requireContext().startActivity(Intent(context, GotJobListActivity::class.java))
            } else {
                Toast.makeText(context, "Please Login App", Toast.LENGTH_SHORT).show()
                requireContext().startActivity(Intent(context, LoginActivity::class.java))
                (context as Activity).finish()
            }
        }

        binding.tvSeeAllAvaijob.setOnClickListener {

            if (requireContext().isuserlgin) {
                requireContext().startActivity(
                    Intent(
                        context,
                        AvailableJobListActivity::class.java
                    )
                )
            } else {
                Toast.makeText(context, "Please Login App", Toast.LENGTH_SHORT).show()
                requireContext().startActivity(Intent(context, LoginActivity::class.java))
                (context as Activity).finish()
            }
        }

        binding.tvSeeAllEnSkill.setOnClickListener {
            if (requireContext().isuserlgin) {
                requireContext().startActivity(Intent(context, EnhanceSkillActivity::class.java))
            } else {
                Toast.makeText(context, "Please Login App", Toast.LENGTH_SHORT).show()
                requireContext().startActivity(Intent(context, LoginActivity::class.java))
                (context as Activity).finish()
            }
        }

        binding.ivClear.setOnClickListener {
            binding.etSearch.text.clear()
            searchData("") // Empty search
        }


        binding.etSearch.addTextChangedListener(object : TextWatcher {
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
                    binding.ivClear.visibility = View.VISIBLE

                } else {
                    if (query.isEmpty()) {
                        searchData("") // Empty search
                    }
                    binding.ivClear.visibility = View.GONE
                }
            }
        })


        return root
    }


    private fun sendToken(token: String?) {
        try {
            if (requireActivity().isOnline) {
                lifecycleScope.launch {
                    val initialized = ApiClient.init() // Initialize Retrofit with BASE_URL

                    if (initialized) {

                commensviewModel.saveTokenResult.observe(requireActivity()) { response ->

                    when (response) {
                        is BaseResponse.Loading -> {

                        }

                        is BaseResponse.Success -> {
                            // pd?.dismiss()
                            response.data?.let { apiResponse ->
                                Log.e("Employe home", "save token res: ${response.data.message}")
                            }
                        }

                        is BaseResponse.Error -> {
                            Log.e("Employe home", "Error token: ${response.msg}")
                        }

                        else -> {
                            Log.e("Employe home", "Unhandled case")
                        }
                    }
                }

                commensviewModel.saveToken(requireContext().userid, token!!)

                    }
                }


            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("Employer home", "Error token: ${e.localizedMessage}")
        }
    }


    private fun searchData(searchKey: String) {
        keyword = searchKey.trim()
        if (requireContext().isOnline) {
            viewModel.getAvailableJobList(requireContext().userid!!, catId, keyword, "", "")
        }
    }

    private fun getFilterCategory() {
        try {
            if (requireActivity().isOnline) {
                lifecycleScope.launch {
                    val initialized = ApiClient.init() // Initialize Retrofit with BASE_URL

                    if (initialized) {

                        commensviewModel.mainCategoryResult.observe(requireActivity()) { response ->
                            pd?.show()
                            when (response) {
                                is BaseResponse.Loading -> {
                                    pd?.show()
                                }

                                is BaseResponse.Success -> {
                                    pd?.dismiss()
                                    response.data?.let { apiResponse ->
                                        mainCatList = response.data
                                        if (response.data!!.isNullOrEmpty()) {
                                            // If the data is null or empty
                                        } else {
                                            // If the data is available
                                            mainCatList = response.data
                                            // Set the layout manager to horizontal
                                            binding.rvSkill.layoutManager = LinearLayoutManager(
                                                context,
                                                LinearLayoutManager.HORIZONTAL,
                                                false
                                            )

                                            // Set the adapter
                                            val adapter =
                                                com.uvk.shramapplication.ui.employeer.home.SkillAdapter(
                                                    requireContext(),
                                                    mainCatList
                                                ) { selectedId ->

                                                    catId = selectedId
                                                    getAvailablejob()

                                                    Log.e("Selected Category ID", catId)
                                                }
                                            binding.rvSkill.adapter = adapter
                                        }

                                    }


                                }

                                is BaseResponse.Error -> {
                                    pd?.dismiss()
                                    Log.e("spinnerMainCat", "Error maincat: ${response.msg}")
                                }

                                else -> {
                                    Log.e("spinnerMainCat", "Unhandled case")
                                }
                            }
                        }

                        commensviewModel.fetchMainCategories()
                    }
                }


            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("Employer home", "Error occurred: ${e.localizedMessage}")
        }
    }

    private fun getGovSkillData() {
        try {
            if (requireActivity().isOnline) {
                lifecycleScope.launch {
                    val initialized = ApiClient.init() // Initialize Retrofit with BASE_URL

                    if (initialized) {
            commensviewModel.randomSkillListResult.observe(requireActivity()) { response ->
                // pd?.show() // Show loading indicator
                when (response) {
                    is BaseResponse.Success -> {
                        //   pd?.dismiss() // Dismiss loading indicator


                        if (response.data?.data.isNullOrEmpty()) {
                            binding.nodataimg2.visibility = View.VISIBLE
                            binding.vpEnSkill.visibility = View.GONE
                        } else {
                            binding.nodataimg2.visibility = View.GONE
                            binding.vpEnSkill.visibility = View.VISIBLE

                            skillDataList.clear()
                            skillDataList.addAll(response.data!!.data!!) // <-- Fill your list

                            govSkillAdapter = GovSkillAdapter(requireContext(), skillDataList)
                            binding.vpEnSkill.adapter = govSkillAdapter
                            binding.vpEnSkill.offscreenPageLimit = 3
                            binding.vpEnSkill.clipToPadding = false
                            binding.vpEnSkill.clipChildren = false
                            binding.vpEnSkill.getChildAt(0).overScrollMode =
                                RecyclerView.OVER_SCROLL_NEVER

                            setUpTransformer()

                            // Important: Restart auto-scroll once new data loaded
                            startAutoScroll()
                        }
                    }

                    is BaseResponse.Error -> {
                        // pd?.dismiss() // Dismiss loading indicator
                        val message = response.msg ?: ""
                        if (!message.isNullOrBlank()) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        } else {
                            //  Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                        Log.e("emp Home", "Gov skill : ${response.msg ?: ""}")
                    }

                    is BaseResponse.Loading -> {
                        // Show loading indicator if needed
                    }
                }
            }

            commensviewModel.getRandomSkillList(requireContext().userid!!)

                    }
                }


            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("Employer home", "Error occurred: ${e.localizedMessage}")
        }

    }

    private fun getAvailablejob() {
        try {
            if (requireActivity().isOnline) {
                lifecycleScope.launch {
                    val initialized = ApiClient.init() // Initialize Retrofit with BASE_URL

                    if (initialized) {
                        viewModel.getAvailableJobListResult.observe(requireActivity()) { response ->

                            //  pd?.show() // Show loading indicator
                            when (response) {
                                is BaseResponse.Success -> {
                                    //    pd?.dismiss() // Dismiss loading indicator
                                    if (response.data?.data.isNullOrEmpty()) {

                                        // Toast.makeText(context, response.data!!.message, Toast.LENGTH_SHORT).show()

                                        binding.nodataimg.visibility = View.VISIBLE
                                        binding.vpAvailablejobs.visibility = View.GONE
                                    } else {

                                        binding.nodataimg.visibility = View.GONE
                                        binding.vpAvailablejobs.visibility = View.VISIBLE

                                        jobList = (response.data?.data ?: emptyList()) as List<Job>
                                        dataList = jobList // Initialize imageList here
                                        availableobAdapter = AvailableJobAdapter(
                                            requireContext(),
                                            dataList,
                                            ::savedJob
                                        )
                                        binding.vpAvailablejobs.adapter = availableobAdapter
                                        binding.vpAvailablejobs.offscreenPageLimit = 3
                                        binding.vpAvailablejobs.clipToPadding = false
                                        binding.vpAvailablejobs.clipChildren = false
                                        binding.vpAvailablejobs.getChildAt(0).overScrollMode =
                                            RecyclerView.OVER_SCROLL_NEVER

                                        setUpTransformer()


                                    }
                                }

                                is BaseResponse.Error -> {
                                    //   pd?.dismiss() // Dismiss loading indicator
                                    val message = response.msg
                                    if (!message.isNullOrBlank()) {
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    } else {
                                        //  Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                                    }
                                    Log.e("emp Home", "Available job : ${response.msg ?: ""}")
                                }

                                is BaseResponse.Loading -> {
                                    // Show loading indicator if needed
                                }
                            }
                        }

                        viewModel.getAvailableJobList(
                            requireContext().userid!!,
                            catId,
                            keyword,
                            "",
                            ""
                        )
                    }
                }
            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("Employer home", "Error occurred: ${e.localizedMessage}")
        }
    }


    fun savedJob(job_id: String) {
        try {
            if (requireActivity().isOnline) {
                lifecycleScope.launch {
                    val initialized = ApiClient.init() // Initialize Retrofit with BASE_URL

                    if (initialized) {

                val token = requireContext().token
                val userId = requireContext().userid

                if (token.isNullOrEmpty() || userId.isNullOrEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "User details are missing. Please Log in.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }
                viewModelSave.savedJobResult.observe(requireActivity()) { response ->
                    pd?.show() // Show loading indicator
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            /*Toast.makeText(requireContext(), response.data!!.message, Toast.LENGTH_SHORT)
                                .show()*/
                            getAvailablejob()
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

                viewModelSave.saveJob(
                    token = token!!,
                    userId = userId!!,
                    jobId = job_id!!
                )

                    }
                }


            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("Employer home", "Error occurred: ${e.localizedMessage}")
        }


    }

    private fun getGotJobList() {
        try {
            if (requireActivity().isOnline) {
                lifecycleScope.launch {
                    val initialized = ApiClient.init() // Initialize Retrofit with BASE_URL

                    if (initialized) {

                        viewModel.homeGotJobListResult.observe(requireActivity()) { response ->
                // pd?.show() // Show loading indicator
                when (response) {
                    is BaseResponse.Success -> {
                        //   pd?.dismiss() // Dismiss loading indicator

                        if (response.data?.data.isNullOrEmpty()) {
                            // Toast.makeText(context, response.data!!.message, Toast.LENGTH_SHORT).show()
                            binding.nodataimg1.visibility = View.VISIBLE
                            binding.vpGotjobs.visibility = View.GONE
                        } else {

                            binding.nodataimg1.visibility = View.GONE
                            binding.vpGotjobs.visibility = View.VISIBLE

                            jobList = (response.data?.data ?: emptyList()) as List<Job>
                            dataList = jobList // Initialize imageList here
                            gotJobAdapter = GotJobAdapter(requireContext(), dataList)
                            binding.vpGotjobs.adapter = gotJobAdapter
                            binding.vpGotjobs.offscreenPageLimit = 3
                            binding.vpGotjobs.clipToPadding = false
                            binding.vpGotjobs.clipChildren = false
                            binding.vpGotjobs.getChildAt(0).overScrollMode =
                                RecyclerView.OVER_SCROLL_NEVER

                            setUpTransformer()


                        }


                    }

                    is BaseResponse.Error -> {
                        //   pd?.dismiss() // Dismiss loading indicator
                        val message = response.msg
                        if (!message.isNullOrBlank()) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        } else {
                            //  Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                        Log.e("emp Home", "Got job : ${response.msg ?: ""}")
                    }

                    is BaseResponse.Loading -> {
                        // Show loading indicator if needed
                    }
                }
            }

            viewModel.getHomeGotJobList()

                    }
                }


            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("Employer home", "Error occurred: ${e.localizedMessage}")
        }

    }


    override fun onStop() {
        super.onStop()
        pd?.dismiss() // Dismiss the dialog when the fragment is stopped
    }

    // Or use onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        pd?.dismiss() // Dismiss the dialog when the fragment view is destroyed
    }


    override fun onResume() {
        super.onResume()
        startAutoScroll()
        getNotificationCount()
    }

    override fun onPause() {
        super.onPause()
        stopAutoScroll()
    }

    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            if (skillDataList.isNotEmpty()) {
                val nextItem = (binding.vpEnSkill.currentItem + 1) % skillDataList.size
                binding.vpEnSkill.currentItem = nextItem
                handler.postDelayed(this, 2000) // 2 sec delay for next auto-scroll
            }
        }
    }

    private fun startAutoScroll() {
        handler.removeCallbacks(autoScrollRunnable)
        handler.postDelayed(autoScrollRunnable, 2000)
    }

    private fun stopAutoScroll() {
        handler.removeCallbacks(autoScrollRunnable)
    }

    private fun setUpTransformer() {
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.14f
        }

        binding.vpAvailablejobs.setPageTransformer(transformer)
        binding.vpGotjobs.setPageTransformer(transformer)
        binding.vpEnSkill.setPageTransformer(transformer)

    }

    private fun getNotificationCount() {
        try {
            if (requireActivity().isOnline) {
                lifecycleScope.launch {
                    val initialized = ApiClient.init()

                    if (initialized) {
                        commensviewModel.notificationCountResult.observe(requireActivity()) { response ->
                            when (response) {
                                is BaseResponse.Success -> {
                                    pd?.dismiss()

                                    notificationCount = response.data!!.count
                                    Log.e("emp Home", "notificationCount : ${response.data!!.count}")

                                    //  Update UI when API gives count
                                    updateNotificationCount(notificationCount)
                                }

                                is BaseResponse.Error -> {
                                    pd?.dismiss()
                                }

                                is BaseResponse.Loading -> {
                                    pd?.show()
                                }
                            }
                        }

                        commensviewModel.notificationCount(
                            user_id = requireContext().userid!!
                        )
                    }
                }
            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("Notification", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(
                context,
                "An error occurred: ${e.localizedMessage ?: "Unknown error"}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear() // <--- CLEAR OLD MENU FIRST
        inflater.inflate(R.menu.main, menu)

        val menuItem = menu.findItem(R.id.action_notification)
        val actionView = menuItem?.actionView

        if (actionView != null) {
            textNotificationCount = actionView.findViewById(R.id.text_notification_count)

            // Initially update
            updateNotificationCount((activity as? MainActivity)?.notificationCountLiveData?.value ?: 0)

            actionView.setOnClickListener {
                onOptionsItemSelected(menuItem)
            }
        }

        val refreshItem = menu.findItem(R.id.action_refreshs)
        refreshItem?.isVisible = true
    }

    private fun updateNotificationCount(count: Int) {
        if (::textNotificationCount.isInitialized) {
            if (count > 0) {
                textNotificationCount.text = count.toString()
                textNotificationCount.visibility = View.VISIBLE
            } else {
                textNotificationCount.visibility = View.GONE
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notification -> {
                val intent = Intent(context, NotificationListActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_refreshs -> {
                getFilterCategory()
                getGotJobList()
                getAvailablejob()
                getGovSkillData()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}