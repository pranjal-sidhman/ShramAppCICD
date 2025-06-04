package com.uvk.shramapplication.ui.employeer.home

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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.google.firebase.messaging.FirebaseMessaging
import com.mahindra.serviceengineer.savedata.FCM_TOKENa
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.FragmentEmployeerHomeBinding
import com.uvk.shramapplication.helper.CommenViewModel
import com.uvk.shramapplication.helper.SearchAdapter
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.response.SkillData
import com.uvk.shramapplication.response.SuggestionData
import com.uvk.shramapplication.ui.employeer.response.EmployeerData
import com.uvk.shramapplication.ui.employeer.home.employeelist.EmployeeListActivity
import com.uvk.shramapplication.ui.employeer.home.jobPostList.PostedJobListActivity
import com.uvk.shramapplication.ui.login.LoginActivity
import com.uvk.shramapplication.ui.main_category.MainCategory
import com.uvk.shramapplication.ui.skillIndia.EnhanceSkillActivity
import com.uvk.shramapplication.ui.skillIndia.GovSkillAdapter
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.isuserlgin
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.roleId
import com.mahindra.serviceengineer.savedata.userid
import com.uvk.shramapplication.MainActivity
import com.uvk.shramapplication.api.ApiClient
import com.uvk.shramapplication.ui.wallet.WalletActivity
import com.uvk.shramapplication.ui.notification.NotificationListActivity
import kotlinx.coroutines.launch
import kotlin.math.abs


class EmployeerHomeFragment : Fragment() {

    private var _binding: FragmentEmployeerHomeBinding? = null
    private val binding get() = _binding!!
    private var pd: TransparentProgressDialog? = null
    private val handler = Handler(Looper.getMainLooper())

    private var notificationCount = 0
    private lateinit var textNotificationCount: TextView

    private val viewModel by viewModels<EmployeerViewModel>()
    private val commensviewModel by viewModels<CommenViewModel>()
    private var dataList: List<EmployeerData> = emptyList()

    // private var skillDataList: List<SkillData> = emptyList()
    private var skillDataList = ArrayList<SkillData>() // SkillData is your model class
    private lateinit var avaiEmpList: List<EmployeerData>
    private lateinit var postJobList: List<EmployeerData>
    private lateinit var mainCatList: List<MainCategory>
    private lateinit var suggestionList: List<SuggestionData>
    private lateinit var availableempAdapter: AvailableEmpAdapter
    private lateinit var myPostedJobAdapter: MyPostedJobsAdapter
    private lateinit var govSkillAdapter: GovSkillAdapter
    private val commenViewModel by viewModels<CommenViewModel>()

    var userId: String = ""
    var catId: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentEmployeerHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setHasOptionsMenu(true) // Enable options menu in fragment

        Log.d(
            "goHomeEmplyeer",
            "isuserlgin: ${requireContext().isuserlgin}, roleId: ${requireContext().roleId}, ${requireContext().languageName}"
        )


        pd = TransparentProgressDialog(requireActivity(), R.drawable.progress)

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

        getNotificationCount()
        getFilterCategory()
        getAvailableEmp()
        getPostedJob()
        govSkill()

        binding.ivClear.setOnClickListener {
            binding.etSearch.text.clear()
            searchData("") // Clear search results
        }


        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length >= 3) {
                    // searchViewModel.searchKeyword(s.toString())
                    searchData(s.toString())
                    binding.rvSearch.visibility = View.VISIBLE
                    binding.ivClear.visibility = View.VISIBLE
                } else {
                    searchData(s.toString())
                    binding.rvSearch.visibility = View.GONE
                    binding.ivClear.visibility = View.GONE
                }
            }
        })


        binding.tvSeeAll.setOnClickListener {
            if (requireContext().isuserlgin) {
                requireContext().startActivity(Intent(context, EmployeeListActivity::class.java))
            } else {
                Toast.makeText(context, "Please Login App", Toast.LENGTH_SHORT).show()
                requireContext().startActivity(Intent(context, LoginActivity::class.java))
                (context as Activity).finish()
            }
        }

        binding.tvSeeAlljobPost.setOnClickListener {

            if (requireContext().isuserlgin) {
                requireContext().startActivity(Intent(context, PostedJobListActivity::class.java))
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

        /* binding.refresh.setOnRefreshListener {
             getFilterCategory()
             getAvailableEmp()
             getPostedJob()
             govSkill()
         }*/

        return root
    }

    private fun sendToken(token: String?) {
        try {
            if (requireActivity().isOnline) {
                commensviewModel.saveTokenResult.observe(requireActivity()) { response ->
                    when (response) {
                        is BaseResponse.Loading -> {
                        }

                        is BaseResponse.Success -> {
                            // pd?.dismiss()
                            response.data?.let { apiResponse ->
                                Log.e("save token", "save token res: ${response.data.message}")
                            }
                        }

                        is BaseResponse.Error -> {

                        }
                    }
                }

                commensviewModel.saveToken(requireContext().userid, token!!)

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("Employer home", "Error occurred: ${e.localizedMessage}")
        }
    }

    private fun searchData(keyword: String) {
        try {
            if (requireActivity().isOnline) {

                commensviewModel.suggestionResult.observe(requireActivity()) { response ->
                    // pd?.show()
                    when (response) {
                        is BaseResponse.Loading -> {
                            //  pd?.show()
                        }

                        is BaseResponse.Success -> {
                            // pd?.dismiss()
                            response.data?.let { apiResponse ->
                                suggestionList = response.data?.data ?: emptyList()

                                Log.d("mainCatList", "Size: ${mainCatList?.size}")
                                if (suggestionList.isNullOrEmpty()) {
                                    // If the data is null or empty
                                } else {

                                    binding.rvSearch.visibility = View.VISIBLE
                                    // Set the layout manager to horizontal
                                    binding.rvSearch.layoutManager = LinearLayoutManager(
                                        context,
                                        LinearLayoutManager.VERTICAL,
                                        false
                                    )

                                    // Set the adapter
                                    val adapter = SearchAdapter(emptyList()) { suggestion ->
                                        binding.etSearch.setText(suggestion.name)  // Set selected suggestion to search box
                                        binding.rvSearch.visibility =
                                            View.GONE    // Hide RecyclerView on selection
                                        val intent = Intent(
                                            context,
                                            EmployeeListActivity::class.java
                                        ).apply {
                                            putExtra("keyword", suggestion.name)
                                        }
                                        context?.startActivity(intent)
                                    }
                                    adapter.updateList(suggestionList)
                                    binding.rvSearch.adapter = adapter
                                }
                            }
                        }

                        is BaseResponse.Error -> {
                            //pd?.dismiss()

                        }

                        else -> {
                            Log.e("spinnerMainCat", "Unhandled case")
                        }
                    }
                }

                commensviewModel.getSuggestionList(keyword)

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("Employer home", "Error occurred: ${e.localizedMessage}")
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
                                    //  binding.refresh.isRefreshing = false // Stop the refresh animation
                                    response.data?.let { apiResponse ->
                                        mainCatList = response.data
                                        Log.d("mainCatList", "Size: ${mainCatList?.size}")
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
                                            val adapter = SkillAdapter(
                                                requireContext(),
                                                mainCatList
                                            ) { selectedId ->

                                                // Toast.makeText(context, "Selected ID: $selectedId", Toast.LENGTH_SHORT).show()
                                                catId = selectedId
                                                getAvailableEmp()
                                                Log.e("Selected Category ID", catId)
                                            }
                                            binding.rvSkill.adapter = adapter
                                        }
                                    }
                                }

                                is BaseResponse.Error -> {
                                    pd?.dismiss()
                                    //   binding.refresh.isRefreshing = false // Stop the refresh animation
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


    private fun govSkill() {

        try {
            if (requireActivity().isOnline) {
                lifecycleScope.launch {
                    val initialized = ApiClient.init() // Initialize Retrofit with BASE_URL

                    if (initialized) {
                        commensviewModel.randomSkillListResult.observe(requireActivity()) { response ->
                            pd?.show() // Show loading indicator
                            when (response) {
                                is BaseResponse.Success -> {
                                    pd?.dismiss() // Dismiss loading indicator

                                    if (response.data?.data.isNullOrEmpty()) {
                                        binding.nodataimg2.visibility = View.VISIBLE
                                        binding.vpEnSkill.visibility = View.GONE
                                    } else {
                                        binding.nodataimg2.visibility = View.GONE
                                        binding.vpEnSkill.visibility = View.VISIBLE

                                        skillDataList.clear()
                                        skillDataList.addAll(response.data!!.data!!) // <-- Fill your list

                                        govSkillAdapter =
                                            GovSkillAdapter(requireContext(), skillDataList)
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


                                    /*if (response.data?.data.isNullOrEmpty()) {
                                // Toast.makeText(context, response.data!!.message, Toast.LENGTH_SHORT).show()

                                binding.nodataimg2.visibility = View.VISIBLE
                                binding.vpEnSkill.visibility = View.GONE
                            } else {

                                binding.nodataimg2.visibility = View.GONE
                                binding.vpEnSkill.visibility = View.VISIBLE

                                skillDataList = response.data!!.data!!
                                govSkillAdapter = GovSkillAdapter(requireContext(),skillDataList)
                                binding.vpEnSkill.adapter = govSkillAdapter
                                binding.vpEnSkill.offscreenPageLimit = 3
                                binding.vpEnSkill.clipToPadding = false
                                binding.vpEnSkill.clipChildren = false
                                binding.vpEnSkill.getChildAt(0).overScrollMode =
                                    RecyclerView.OVER_SCROLL_NEVER

                                setUpTransformer()

                                binding.vpEnSkill.registerOnPageChangeCallback(object :
                                    ViewPager2.OnPageChangeCallback() {
                                    override fun onPageSelected(position: Int) {
                                        super.onPageSelected(position)
                                        handler.removeCallbacks(runnable)
                                        handler.postDelayed(runnable, 2000)
                                    }
                                })
                            }*/


                                }

                                is BaseResponse.Error -> {
                                    pd?.dismiss() // Dismiss loading indicator

                                    val message = response.msg
                                    if (!message.isNullOrBlank()) {
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    } else {
                                        //  Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                                    }
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
            Log.e("Job list", "Error occurred: ${e.localizedMessage}")
        }

    }

    private fun getPostedJob() {

        try {
            if (requireActivity().isOnline) {
                lifecycleScope.launch {
                    val initialized = ApiClient.init() // Initialize Retrofit with BASE_URL

                    if (initialized) {

                        viewModel.postJobListResult.observe(requireActivity()) { response ->
                            pd?.show() // Show loading indicator

                            when (response) {
                                is BaseResponse.Success -> {
                                    pd?.dismiss() // Dismiss loading indicator
                                    //   binding.refresh.isRefreshing = false // Stop the refresh animation
                                    if (response.data?.data.isNullOrEmpty()) {
                                        userId = ""
                                        //Toast.makeText(context, response.data!!.message, Toast.LENGTH_SHORT).show()
                                        binding.nodataimg1.visibility = View.VISIBLE
                                        binding.vpPostedJobs.visibility = View.GONE
                                    } else {
                                        postJobList =
                                            (response.data?.data
                                                ?: emptyList()) as List<EmployeerData>
                                        //dataList = avaiEmpList // Initialize imageList here

                                        userId = requireActivity().userid

                                        binding.nodataimg1.visibility = View.GONE
                                        binding.vpPostedJobs.visibility = View.VISIBLE

                                        myPostedJobAdapter =
                                            MyPostedJobsAdapter(requireContext(), postJobList)
                                        binding.vpPostedJobs.adapter = myPostedJobAdapter
                                        binding.vpPostedJobs.offscreenPageLimit = 3
                                        binding.vpPostedJobs.clipToPadding = false
                                        binding.vpPostedJobs.clipChildren = false
                                        binding.vpPostedJobs.getChildAt(0).overScrollMode =
                                            RecyclerView.OVER_SCROLL_NEVER

                                        setUpTransformer()

                                        /*  binding.vpPostedJobs.registerOnPageChangeCallback(object :
                                    ViewPager2.OnPageChangeCallback() {
                                    override fun onPageSelected(position: Int) {
                                        super.onPageSelected(position)
                                        handler.removeCallbacks(runnable)
                                        handler.postDelayed(runnable, 2000)
                                    }
                                })*/
                                    }
                                }

                                is BaseResponse.Error -> {
                                    pd?.dismiss() // Dismiss loading indicator
                                    //  binding.refresh.isRefreshing = false // Stop the refresh animation
                                    Log.e("Job list", "Error: ${response.msg}")
                                    val message = response.msg
                                    if (!message.isNullOrBlank()) {
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    } else {
                                        //  Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                is BaseResponse.Loading -> {
                                    // Show loading indicator if needed
                                }
                            }
                        }

                        viewModel.getPostJobList(requireContext().userid ?: "")
                    }
                }

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("Job list", "Error occurred: ${e.localizedMessage}")
        }
    }

    private fun getAvailableEmp() {
        try {
            if (requireActivity().isOnline) {
                lifecycleScope.launch {
                    val initialized = ApiClient.init() // Initialize Retrofit with BASE_URL

                    if (initialized) {
                        viewModel.availEmpListResult.observe(requireActivity()) { response ->

                            when (response) {
                                is BaseResponse.Success -> {
                                    //  binding.refresh.isRefreshing = false // Stop the refresh animation

                                    if (response.data?.data.isNullOrEmpty()) {

                                        //  Toast.makeText(context, response.data!!.message, Toast.LENGTH_SHORT).show()
                                        binding.nodataimg.visibility = View.VISIBLE
                                        binding.vpAvailableEmp.visibility = View.GONE
                                    } else {
                                        avaiEmpList =
                                            (response.data?.data
                                                ?: emptyList()) as List<EmployeerData>
                                        dataList = avaiEmpList // Initialize imageList here

                                        binding.nodataimg.visibility = View.GONE
                                        binding.vpAvailableEmp.visibility = View.VISIBLE

                                        availableempAdapter =
                                            AvailableEmpAdapter(requireContext(), dataList)
                                        binding.vpAvailableEmp.adapter = availableempAdapter
                                        binding.vpAvailableEmp.offscreenPageLimit = 3
                                        binding.vpAvailableEmp.clipToPadding = false
                                        binding.vpAvailableEmp.clipChildren = false
                                        binding.vpAvailableEmp.getChildAt(0).overScrollMode =
                                            RecyclerView.OVER_SCROLL_NEVER

                                        setUpTransformer()


                                    }
                                }

                                is BaseResponse.Error -> {
                                    // binding.refresh.isRefreshing = false // Stop the refresh animation
                                    Log.e("Job list", "Error: ${response.msg}")
                                    val message = response.msg
                                    if (!message.isNullOrBlank()) {
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    } else {
                                        //  Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                                    }

                                }

                                is BaseResponse.Loading -> {
                                    // Show loading indicator if needed
                                }
                            }
                        }

                        viewModel.getAvailEmp(requireContext().userid, catId)
                    }
                }

            } else {
                Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("Job list", "Error occurred: ${e.localizedMessage}")
        }
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

        binding.vpAvailableEmp.setPageTransformer(transformer)
        binding.vpPostedJobs.setPageTransformer(transformer)
        binding.vpEnSkill.setPageTransformer(transformer)

    }


    private fun getNotificationCount() {
        try {
            if (requireActivity().isOnline) {
                lifecycleScope.launch {
                    val initialized = ApiClient.init() // Initialize Retrofit with BASE_URL

                    if (initialized) {
                        commenViewModel.notificationCountResult.observe(requireActivity()) { response ->
                            when (response) {
                                is BaseResponse.Success -> {
                                    pd?.dismiss()

                                    notificationCount = response.data!!.count
                                    Log.e("empr Home", "notificationCount : employer ${response.data!!.count}")

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

                        commenViewModel.notificationCount(
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

       /* val refreshItem = menu.findItem(R.id.action_refreshs)
        refreshItem?.isVisible = true*/

        if(requireActivity().isuserlgin) {
            val walletItem = menu.findItem(R.id.action_wallet)
            walletItem?.isVisible = true
        }


    }




    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notification -> {
                //Toast.makeText(this, "Notification clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, NotificationListActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_wallet -> {
                val intent = Intent(context, WalletActivity::class.java)
                startActivity(intent)
               // walletDailog()
                true
            }
            /*R.id.action_refreshs -> {
                getFilterCategory()
                getAvailableEmp()
                getPostedJob()
                govSkill()
                true
            }*/


            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun walletDailog() {

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


}