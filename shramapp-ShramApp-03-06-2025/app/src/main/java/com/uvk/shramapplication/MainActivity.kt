package com.uvk.shramapplication

import android.Manifest
import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.os.Build
import android.os.Build.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ExpandableListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.GravityCompat.*
import androidx.core.view.WindowCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.ActivityMainBinding
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.helper.getAndroidId
import com.uvk.shramapplication.ui.employeer.job_post.PostJobActivity
import com.uvk.shramapplication.ui.employeer.worklist.WorkListEmployeerActivity
import com.uvk.shramapplication.ui.employeer.worklist.list.job_request_list.JobItem
import com.uvk.shramapplication.ui.expandNav.ExpandableListAdapter
import com.uvk.shramapplication.ui.login.LoginActivity
import com.uvk.shramapplication.ui.login.LoginViewModel
import com.uvk.shramapplication.ui.map.GoogleMapActivity
import com.uvk.shramapplication.ui.map.EmpGoogleMapActivity
import com.uvk.shramapplication.ui.profile.NewProfileActivity
import com.uvk.shramapplication.ui.registration.SignupViewModel
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.mahindra.serviceengineer.savedata.AppRole
import com.mahindra.serviceengineer.savedata.companyName
import com.mahindra.serviceengineer.savedata.designationName
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.isuserlgin
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.mobile_no
import com.mahindra.serviceengineer.savedata.resenduserotp
import com.mahindra.serviceengineer.savedata.roleId
import com.mahindra.serviceengineer.savedata.token
import com.mahindra.serviceengineer.savedata.user_profile
import com.mahindra.serviceengineer.savedata.userid
import com.mahindra.serviceengineer.savedata.usermobilenumber
import com.mahindra.serviceengineer.savedata.username
import com.mahindra.serviceengineer.savedata.userotp
import com.uvk.shramapplication.helper.TranslationHelper
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.MutableLiveData
import com.mahindra.serviceengineer.savedata.FCM_TOKEN
import com.mahindra.serviceengineer.savedata.FCM_TOKENa
import com.mahindra.serviceengineer.savedata.USER_OTP
import com.mahindra.serviceengineer.savedata.clearPrefs
import com.mahindra.serviceengineer.savedata.is_logged_in
import com.uvk.shramapplication.api.ApiClient
import com.uvk.shramapplication.helper.CommenViewModel
import com.uvk.shramapplication.savedata.language.LanguageHelper
import com.uvk.shramapplication.savedata.language.LanguagePref
import com.uvk.shramapplication.ui.chat.ChatActivity
import com.uvk.shramapplication.ui.chat.message.MessengerListActivity
import com.uvk.shramapplication.ui.notification.NotificationClickListener
import com.uvk.shramapplication.ui.notification.NotificationListActivity
import kotlinx.coroutines.Dispatchers


class MainActivity : AppCompatActivity(), NotificationClickListener {
    private var bottomSheetAvailableDialog: BottomSheetDialog? = null
    private var bottomSheetLangDialog: BottomSheetDialog? = null
    private lateinit var btnYes: AppCompatButton
    private lateinit var btnNo: AppCompatButton
    private lateinit var tvMsg: TextView
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var fabJobPost: TextView
    private lateinit var fabMap: FloatingActionButton
    private lateinit var imageView: CircleImageView
    private lateinit var tvUserName: TextView
    private lateinit var tvMobilno: TextView
    private lateinit var cbHindi: CheckBox
    private lateinit var cbEnglish: CheckBox
    private lateinit var cbMarathi: CheckBox
    private lateinit var btnLangSubmit: Button
    private lateinit var llEnglish: LinearLayoutCompat
    private lateinit var llHindi: LinearLayoutCompat
    private lateinit var llMarathi: LinearLayoutCompat
    private lateinit var navController: NavController
    var buttonClickedFlag = ""
    private lateinit var navView: NavigationView
    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    private var expandableListView: ExpandableListView? = null
    private var expandableListAdapter: ExpandableListAdapter? = null
    private var listGroupTitles: List<String>? = null
    private var listChildData: HashMap<String, List<String>>? = null

    private val viewModel by viewModels<LoginViewModel>()
    private val availableViewModel by viewModels<SignupViewModel>()
    private val commenViewModel by viewModels<CommenViewModel>()
    var pd: TransparentProgressDialog? = null
    var userRole: String? = null


    private lateinit var jobList: List<JobItem>
    private lateinit var gotJobAdapter: EmpGotJobAdapter
    private var jobIds = mutableListOf<Int>()
    private var EmployeerId = mutableListOf<Int>()


    private var REQUEST_LOCATION_CODE = 101
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocation: Location? = null
    private var mLocationRequest: LocationRequest? = null
    private val UPDATE_INTERVAL = (2 * 1000).toLong()  /* 10 secs */
    private val FASTEST_INTERVAL: Long = 2000 /* 2 sec */
    var Latitude: String = " "
    var Longitude: String = " "
    var latitude: Double? = 00.00
    var longitude: Double? = 00.00
    var Currenttime = 0
    var addressLine: String? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private var notificationCount = 0
    private lateinit var textNotificationCount: TextView
    val notificationCountLiveData = MutableLiveData<Int>()

    companion object {
        const val MY_FINE_LOCATION_REQUEST = 99
        private const val MY_BACKGROUND_LOCATION_REQUEST = 100
        private const val STOP_SERVICE_REQUEST_CODE = 123
        private const val REQUEST_SCHEDULE_EXACT_ALARM_PERMISSION = 101
        private const val LOCATION_PERMISSION_REQUEST_CODE = 102
        private val REQUEST_CODE_SCHEDULE_EXACT_ALARM = 1234
    }

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch(Dispatchers.IO) {
            TranslationHelper.initialize(this@MainActivity)
        }

        setSupportActionBar(binding.appBarMain.toolbar)
        pd = TransparentProgressDialog(this, R.drawable.progress)

        // Get the Android ID
        val androidId = getAndroidId(this)

        // Print the Android ID to the Logcat 483b67b003b162b1
        Log.d("DeviceID", "Android ID: $androidId")
        Log.d(TAG, "token :Authorization : $token")

        getNotificationCount()

        val destination = intent?.getStringExtra("notification_type")
        Log.e("MainActivity", "destination : in onCreate $destination")

        /* if (destination != null && isuserlgin) {
             navigateBasedOnNotification(destination)
             intent.replaceExtras(Bundle()) // clear it
         }*/


        userRole = AppRole
        Log.e(TAG, "Role : $userRole")
        Log.e(
            "main",
            "isuserlgin: $isuserlgin, roleId: $roleId, userRole: $userRole,is_logged_in : $is_logged_in, Lang : $languageName $userid"
        )


        languageName = LanguagePref.getLanguage(this)
        roleId = LanguagePref.getRoleId(this)
        isuserlgin = LanguagePref.isUserLoggedIn(this)
        userid = LanguagePref.getUserId(this)
        userRole = LanguagePref.getAppRole(this)
        username = LanguagePref.getUserName(this)
        user_profile = LanguagePref.getUserProfile(this)
        mobile_no = LanguagePref.getMobileNo(this)
        usermobilenumber = LanguagePref.getMobileNo(this)
        designationName = LanguagePref.getDesignationName(this)
        companyName = LanguagePref.getCompanyName(this)
        token = LanguagePref.getUserToken(this)
        FCM_TOKEN = LanguagePref.getFCMToken(this)
        FCM_TOKENa = LanguagePref.getFCMToken(this)
        userotp = LanguagePref.getUserOTP(this)
        resenduserotp = LanguagePref.getUserResendOTP(this)

        Log.e("main2", "Language: $languageName, RoleId: $roleId, isuserlgin: $isuserlgin $userid")
        Log.e("main2", "name: $username,  $mobile_no ,userRole : $userRole")

        navView = findViewById(R.id.nav_view)

        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_main2)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_employeer_home,
                R.id.nav_post,
                R.id.nav_network,
                R.id.nav_worklist
            ), drawerLayout
        )// R.id.nav_slideshow
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        // drawerLayout = findViewById(R.id.drawer_layout)
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this, drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // to make the Navigation drawer icon always appear on the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val headerView = navView.getHeaderView(0) // Get the first header view

        imageView = headerView.findViewById(R.id.imageView)
        tvUserName = headerView.findViewById(R.id.tvUser)
        tvMobilno = headerView.findViewById(R.id.tvMobile)
        fabJobPost = findViewById(R.id.fabjobPost)
        fabMap = findViewById(R.id.fabMap)

        if (isuserlgin) {
            lifecycleScope.launch {
                tvUserName.text = TranslationHelper.translateText(username ?: "", languageName)
                tvMobilno.text =
                    TranslationHelper.translateText(usermobilenumber ?: "", languageName)

                Log.e(
                    TAG,
                    "usrnm : ${TranslationHelper.translateText(username ?: "", languageName)}"
                )
            }

            Glide.with(imageView.context)
                .load(user_profile)
                .placeholder(R.drawable.logo)
                .into(imageView)
        }


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        Handler().postDelayed({
            Log.e(TAG, "data loop")
            getLocationData()
        }, 2000)


        fabMap.setOnClickListener {
            goMap()
        }


        // Bottom Navigation
        bottomNavigation = findViewById(R.id.bottom_navigation)



        fabJobPost.setOnClickListener {
            if (isuserlgin) {
                when (roleId) {
                    "2" -> startActivity(Intent(this, PostJobActivity::class.java))
                    "3" -> Toast.makeText(
                        this,
                        "You are not authorized to post a job.",
                        Toast.LENGTH_SHORT
                    ).show()

                    else -> Toast.makeText(this, "Invalid role.", Toast.LENGTH_SHORT).show()
                }
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }





        navView.setNavigationItemSelectedListener { menuItem ->
            handleNavigationSelection(menuItem)
            drawerLayout.closeDrawer(START)
            true
        }

        // Call this function after user login state or role changes
        updateMenuVisibility()


        // Handle Bottom Navigation
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.bottom_nav_home ->

                    if (isuserlgin) {  // Check if the user is logged in
                        Log.e("MainAct", "Home tab isuserlgin")
                        when (roleId) {
                            "2" -> {
                                userRole = "Hire Top Talent"
                                fabJobPost.visibility = View.VISIBLE
                                navController.navigate(R.id.nav_employeer_home) // Employer home
                            }

                            "3" -> {
                                userRole = "Find Job"
                                fabJobPost.visibility = View.GONE
                                navController.navigate(R.id.nav_home) // Job seeker home
                            }
                        }
                    } else {
                        Log.e("MainAct", "Home tab $userRole")
                        Log.e("MainAct", "Home tab $roleId")
                        Log.e("MainAct", "Home tab $AppRole")
                        // If not logged in, follow the original logic based on userRole
                        when (userRole) {
                            "Find Job" -> {
                                fabJobPost.visibility = View.GONE
                                navController.navigate(R.id.nav_home)
                            }

                            "Hire Top Talent" -> {
                                fabJobPost.visibility = View.VISIBLE
                                navController.navigate(R.id.nav_employeer_home)
                            }
                        }
                    }

                R.id.bottom_nav_network -> {
                    if (isuserlgin) {
                        navController.navigate(R.id.nav_network)
                        invalidateOptionsMenu()
                    } else {
                        startActivity(Intent(this, LoginActivity::class.java))
                    }

                }

                R.id.bottom_nav_post ->
                    if (isuserlgin) {
                        navController.navigate(R.id.nav_post)
                    } else {
                        startActivity(Intent(this, LoginActivity::class.java))
                    }

                R.id.bottom_nav_worklist -> {
                    if (isuserlgin) { // Check if the user is logged in
                        when {
                            userRole == "Find Job" && roleId == "3" -> {
                                // replaceFragment(WorkListFragment())  // Replace fragment
                                navController.navigate(R.id.nav_worklist)
                            }

                            userRole == "Hire Top Talent" && roleId == "2" -> {
                                startActivity(
                                    Intent(
                                        this,
                                        WorkListEmployeerActivity::class.java
                                    )
                                )  // Open activity
                            }
                        }
                    } else {
                        startActivity(Intent(this, LoginActivity::class.java))

                    }
                }

                R.id.bottom_nav_profile -> {
                    //startActivity(Intent(this, ProfileActivity::class.java))

                    if (isuserlgin) { // Check if the user is logged in
                        when {
                            userRole == "Find Job" && roleId == "3" -> {

                                startActivity(Intent(this, NewProfileActivity::class.java))
                            }

                            userRole == "Hire Top Talent" && roleId == "2" -> {
                                startActivity(Intent(this, NewProfileActivity::class.java))
                            }
                        }
                    } else {
                        startActivity(Intent(this, LoginActivity::class.java))

                    }

                }

            }
            true
        }


        if (!userid.isNullOrEmpty()) {
            getAvailabilityStatus()
            getJobRequestDialog()
        }
        // Load role-based Home Fragment
        loadHomeFragment()

    }


    private fun getNotificationCount() {
        try {
            if (isOnline) {
                lifecycleScope.launch {
                    val initialized = ApiClient.init()

                    if (initialized) {
                        commenViewModel.notificationCountResult.observe(this@MainActivity) { response ->
                            pd?.show()
                            when (response) {
                                is BaseResponse.Success -> {
                                    pd?.dismiss()

                                    notificationCount = response.data!!.count
                                    notificationCountLiveData.postValue(notificationCount) // <-- UPDATE LIVE DATA

                                    Log.e(
                                        "MainActivity",
                                        "notificationCount : ${response.data!!.count}"
                                    )
                                }

                                is BaseResponse.Error -> {
                                    pd?.dismiss()
                                }

                                is BaseResponse.Loading -> {
                                    // optional
                                }
                            }
                        }

                        commenViewModel.notificationCount(user_id = userid!!)
                    }
                }
            } else {
                Toast.makeText(this, "Internet not connected", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Log.e("Notification", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(
                this,
                "An error occurred: ${e.localizedMessage ?: "Unknown error"}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    // Function to handle menu visibility based on login state
    private fun updateMenuVisibility() {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val menu = navigationView.menu
        if (isuserlgin) {
            menu.findItem(R.id.nav_logIn).isVisible = false
            menu.findItem(R.id.nav_logout).isVisible = true
        } else {
            menu.findItem(R.id.nav_logIn).isVisible = true
            menu.findItem(R.id.nav_logout).isVisible = false
        }
    }

    private fun handleNavigationSelection(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.nav_home -> {
                if (isuserlgin) {
                    // Based on the user's role, navigate accordingly
                    when (roleId) {
                        "2" -> { // Employer
                            userRole = "Hire Top Talent"
                            fabJobPost.visibility = View.VISIBLE
                            navController.navigate(R.id.nav_employeer_home)
                        }

                        "3" -> { // Job Seeker
                            userRole = "Find Job"
                            fabJobPost.visibility = View.GONE
                            navController.navigate(R.id.nav_home)
                        }
                    }
                } else {
                    // Navigate based on stored user role when not logged in
                    when (userRole) {
                        "Find Job" -> {
                            fabJobPost.visibility = View.GONE
                            navController.navigate(R.id.nav_home)
                        }

                        "Hire Top Talent" -> {
                            fabJobPost.visibility = View.VISIBLE
                            navController.navigate(R.id.nav_employeer_home)
                        }
                    }
                }
            }

            R.id.nav_post -> {
                if (isuserlgin) {
                    navController.navigate(R.id.nav_post)
                    bottomNavigation.selectedItemId = R.id.bottom_nav_post
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }

            R.id.nav_network -> {
                if (isuserlgin) {
                    navController.navigate(R.id.nav_network)
                    invalidateOptionsMenu()
                    bottomNavigation.selectedItemId = R.id.bottom_nav_network
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }

            R.id.nav_worklist -> {
                if (isuserlgin) {
                    when {
                        userRole == "Find Job" && roleId == "3" -> {
                            navController.navigate(R.id.nav_worklist)
                            bottomNavigation.selectedItemId = R.id.bottom_nav_worklist
                        }

                        userRole == "Hire Top Talent" && roleId == "2" -> {
                            startActivity(Intent(this, WorkListEmployeerActivity::class.java))
                        }

                        else -> {

                        }
                    }
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }

            R.id.nav_language -> {
                languageSelectionDialog()
            }

            R.id.nav_profile -> {
                if (isuserlgin) {
                    startActivity(Intent(this, NewProfileActivity::class.java))
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }

            R.id.nav_privacy_policy -> {
                val url = "https://callisol.com/Shram/privacypolicy.html"
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            }

            R.id.nav_logIn -> {
                startActivity(Intent(this, LoginActivity::class.java))
            }

            R.id.nav_logout -> {
                logOut()
            }

            else -> false
        }
    }


    override fun onResume() {
        super.onResume()

        getNotificationCount()
        updateNotificationCount(notificationCount)

        val destination = intent.getStringExtra("notification_type")
        Log.e("MainActivity", "destination : resume $destination")

        // Clear extras BEFORE navigation
        intent.replaceExtras(Bundle()) // Clear it before any possible repeated use
        if (isuserlgin) {
            when (destination) {
                "send_chat_message" -> {
                    val intent = Intent(this@MainActivity, MessengerListActivity::class.java)
                    startActivity(intent)
                    intent.replaceExtras(Bundle()) //  clear intent extras properly
                }
                "add_story_post" -> {
                    if (navController.currentDestination?.id != R.id.nav_post) {
                        navController.navigate(R.id.nav_post)
                    }
                    // Manually highlight the tab without triggering nav listener
                    bottomNavigation.menu.findItem(R.id.bottom_nav_post).isChecked = true

                    intent.replaceExtras(Bundle()) // Clear extras
                }

                "send_request" -> {
                    if (navController.currentDestination?.id != R.id.nav_network) {
                        navController.navigate(R.id.nav_network)
                    }
                    // Manually highlight the tab without triggering nav listener
                    bottomNavigation.menu.findItem(R.id.bottom_nav_network).isChecked = true

                    intent.replaceExtras(Bundle()) // Clear extras
                }

                "apply_job" -> {
                    when {
                        userRole == "Find Job" && roleId == "3" -> {
                            //  val navController = findNavController(R.id.nav_host_fragment_content_main2)
                            val bundle = Bundle().apply {
                                putInt("tab_index", 2) // Jump to AppliedJobListFragment
                            }
                            navController.navigate(R.id.nav_worklist, bundle)


                            //  navController.navigate(R.id.nav_worklist)
                        }

                        userRole == "Hire Top Talent" && roleId == "2" -> {
                            val intent = Intent(this, WorkListEmployeerActivity::class.java)
                            intent.putExtra("tab_index", 1) // Jump to AcceptedJobFragment
                            startActivity(intent)
                        }

                    }
                    // Manually highlight the tab without triggering nav listener
                    bottomNavigation.menu.findItem(R.id.bottom_nav_worklist).isChecked = true
                    intent.replaceExtras(Bundle()) //  clear intent extras properly
                }

                "accept_job_request" -> {
                    when {
                        userRole == "Find Job" && roleId == "3" -> {
                            //  val navController = findNavController(R.id.nav_host_fragment_content_main2)
                            val bundle = Bundle().apply {
                                putInt("tab_index", 2) // Jump to AppliedJobListFragment
                            }
                            navController.navigate(R.id.nav_worklist, bundle)

                        }

                        userRole == "Hire Top Talent" && roleId == "2" -> {
                            val intent = Intent(this, WorkListEmployeerActivity::class.java)
                            intent.putExtra("tab_index", 2) // Jump to AcceptedJobFragment
                            startActivity(intent)
                        }
                    }
                    // Manually highlight the tab without triggering nav listener
                    bottomNavigation.menu.findItem(R.id.bottom_nav_worklist).isChecked = true
                    intent.replaceExtras(Bundle()) //  clear intent extras properly
                }

                "job_request" -> {

                    if (navController.currentDestination?.id != R.id.nav_worklist) {
                        navController.navigate(R.id.nav_worklist)
                    }
                    // Manually highlight the tab without triggering nav listener
                    bottomNavigation.menu.findItem(R.id.bottom_nav_worklist).isChecked = true

                    intent.replaceExtras(Bundle()) // Clear extras
                }

                "reject_job_request" -> {

                    val intent = Intent(this@MainActivity, MainActivity::class.java)
                    intent.putExtra("notification_type", "reject_job_request")
                    startActivity(intent)

                    intent.replaceExtras(Bundle()) //  clear intent extras properly
                }
            }

        } else {
            // Toast.makeText(this, "Please logged in.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun languageSelectionDialog() {
        bottomSheetLangDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_language, null)
        bottomSheetLangDialog?.setContentView(dialogView)

        cbEnglish = dialogView.findViewById(R.id.cbEnglish)
        cbHindi = dialogView.findViewById(R.id.cbHindi)
        cbMarathi = dialogView.findViewById(R.id.cbMarathi)
        btnLangSubmit = dialogView.findViewById(R.id.btnSubmit)
        llEnglish = dialogView.findViewById(R.id.llEnglish)
        llHindi = dialogView.findViewById(R.id.llHindi)
        llMarathi = dialogView.findViewById(R.id.llMarathi)

        // Load current language
        selectLanguage(LanguagePref.getLanguage(this) ?: "en")

        Log.e("autoselect", "isuserlgin: ${LanguagePref.getLanguage(this) ?: "en"}")


        // Listeners
        llEnglish.setOnClickListener { selectLanguage("en") }
        llHindi.setOnClickListener { selectLanguage("hi") }
        llMarathi.setOnClickListener { selectLanguage("mr") }

        cbEnglish.setOnCheckedChangeListener { _, isChecked -> if (isChecked) selectLanguage("en") }
        cbHindi.setOnCheckedChangeListener { _, isChecked -> if (isChecked) selectLanguage("hi") }
        cbMarathi.setOnCheckedChangeListener { _, isChecked -> if (isChecked) selectLanguage("mr") }

        Log.e(
            "before",
            "isuserlgin: $isuserlgin, roleId: $roleId, userRole: $userRole,is_logged_in : $is_logged_in, Lang : $languageName"
        )

        btnLangSubmit.setOnClickListener {
            if (!cbEnglish.isChecked && !cbHindi.isChecked && !cbMarathi.isChecked) {
                showToast(getString(R.string.txxtselect_app_language))
                return@setOnClickListener
            }

            // Save and apply language
            LanguagePref.setLanguage(this, selectedLangCode)
            LanguagePref.setRoleId(this, roleId)
            LanguagePref.setUserLogin(this, isuserlgin)
            LanguagePref.setUserId(this, userid)
            LanguagePref.setAppRole(this, AppRole)
            LanguagePref.setUserName(this, username)
            LanguagePref.setUserProfile(this, user_profile)
            LanguagePref.setMobileNo(this, mobile_no)
            LanguagePref.setMobileNo(this, usermobilenumber)
            LanguagePref.setDesignationName(this, designationName)
            LanguagePref.setCompanyName(this, companyName)
            LanguagePref.setUserToken(this, token)
            LanguagePref.setFCMToken(this, FCM_TOKEN)
            LanguagePref.setFCMToken(this, FCM_TOKENa)
            LanguagePref.setUserOTP(this, userotp)
            LanguagePref.setUserResendOTP(this, resenduserotp)


            // Update config
            val updatedContext = LanguageHelper.setLocale(this, selectedLangCode)

            // Restart main activity with updated context
            val intent = Intent(updatedContext, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            bottomSheetLangDialog?.dismiss()
        }





        bottomSheetLangDialog?.show()
    }


    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let {
            LanguageHelper.setLocale(
                it,
                LanguagePref.getLanguage(it)
            )
        })
    }


    private var selectedLangCode: String = ""//"en"

    private fun selectLanguage(languageCode: String) {
        selectedLangCode = languageCode
        //  selectedLangCode = languageName
        languageName = selectedLangCode

        cbEnglish.isChecked = languageCode == "en"
        cbHindi.isChecked = languageCode == "hi"
        cbMarathi.isChecked = languageCode == "mr"

        highlightSelectedLayout(languageCode)

        Log.e(
            "selectLanguage",
            "Lan code: $languageName $isuserlgin, $roleId ,$languageCode $selectedLangCode"
        )
    }

    private fun highlightSelectedLayout(languageCode: String) {
        llEnglish.setBackgroundResource(if (languageCode == "en") R.drawable.ic_green_rectangle else R.drawable.border_box)
        llHindi.setBackgroundResource(if (languageCode == "hi") R.drawable.ic_green_rectangle else R.drawable.border_box)
        llMarathi.setBackgroundResource(if (languageCode == "mr") R.drawable.ic_green_rectangle else R.drawable.border_box)
    }


    /* private fun selectLanguage(languageCode: String) {
         // Update checkbox states based on the selected language
         cbEnglish.isChecked = languageCode == "en"
         cbHindi.isChecked = languageCode == "hi"
         cbMarathi.isChecked = languageCode == "mr"

         // Update background to highlight the selected language
         setSelectedLanguage(
             when (languageCode) {
                 "en" -> llEnglish
                 "hi" -> llHindi
                 "mr" -> llMarathi
                 else -> null
             }
         )

         // Reset other languages
         resetOtherLanguages(llEnglish, languageCode == "en")
         resetOtherLanguages(llHindi, languageCode == "hi")
         resetOtherLanguages(llMarathi, languageCode == "mr")

         // Set the language
         setLanguage(languageCode)
     }*/

    private fun setSelectedLanguage(ll: View?) {
        ll?.background = ContextCompat.getDrawable(this, R.drawable.ic_green_rectangle)
    }

    private fun resetOtherLanguages(ll: View, isSelected: Boolean) {
        if (!isSelected) {
            ll.background = ContextCompat.getDrawable(this, R.drawable.border_box)
        }
    }

    private fun setLanguage(languageCode: String) {
        languageName = languageCode
        Log.e("MainActivity", "Language set : $languageName")
        applyLanguage(languageName)
    }

    private fun applyLanguage(languageCode: String) {
        if (!languageCode.isNullOrEmpty()) {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = Configuration()
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)
        } else {
            val locale = Locale(languageName)
            Locale.setDefault(locale)
            val config = Configuration()
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)
        }

    }

    /*  override fun attachBaseContext(newBase: Context) {
          val lang = newBase.languageName
          val context = ContextWrapper(newBase).apply {
              val locale = Locale(lang)
              Locale.setDefault(locale)
              val config = Configuration()
              config.setLocale(locale)
              applyOverrideConfiguration(config)
          }
          super.attachBaseContext(context)
      }
    override fun attachBaseContext(newBase: Context) {
        val lang = newBase.languageName
        super.attachBaseContext(LocaleHelper.wrap(newBase, lang))
    }*/


    /* private fun goHome() {
         applyLanguage(languageName)
         fabJobPost.text = getString(R.string.job_post)
         updateToolbarTitles()
         updateMenuTitles()
         updateBottomMenuTitles()
         Log.d("goHome", "isuserlgin: $isuserlgin, roleId: $roleId, userRole: $userRole")
         Log.d("goHome", "language: $languageName")


         if (isuserlgin) {  // Check if the user is logged in

             when (roleId) {
                 "2" -> {
                     userRole = "Hire Top Talent"
                     fabJobPost.visibility = View.VISIBLE
                     navController.navigate(R.id.nav_employeer_home) // Employer home
                     // Set the selected item in Bottom Navigation
                     bottomNavigation.selectedItemId = R.id.bottom_nav_home
                 }

                 "3" -> {
                     userRole = "Find Job"
                     fabJobPost.visibility = View.GONE
                     navController.navigate(R.id.nav_home) // Job seeker home
                     // Set the selected item in Bottom Navigation
                     bottomNavigation.selectedItemId = R.id.bottom_nav_home
                 }
             }
         } else {
             // If not logged in, follow the original logic based on userRole
             when (userRole) {
                 "Find Job" -> {
                     fabJobPost.visibility = View.GONE
                     navController.navigate(R.id.nav_home)
                     // Set the selected item in Bottom Navigation
                     bottomNavigation.selectedItemId = R.id.bottom_nav_home
                 }

                 "Hire Top Talent" -> {
                     fabJobPost.visibility = View.VISIBLE
                     navController.navigate(R.id.nav_employeer_home)
                     // Set the selected item in Bottom Navigation
                     bottomNavigation.selectedItemId = R.id.bottom_nav_home
                 }
             }
         }
     }*/

    private fun refreshCurrentFragmentTitle() {
        val currentDestinationId = navController.currentDestination?.id
        currentDestinationId?.let {
            navController.popBackStack(it, true) // Remove current
            navController.navigate(it)           // Navigate again
        }
    }


    /* private fun updateToolbarTitles() {
         val navController = findNavController(R.id.nav_host_fragment_content_main2)
         setupActionBarWithNavController(navController)

         navController.addOnDestinationChangedListener { _, destination, _ ->
             val resId = destination.id
             val label = when (resId) {
                 R.id.nav_home -> getString(R.string.menu_home)
                 R.id.nav_employeer_home -> getString(R.string.menu_home)
                 R.id.nav_post -> getString(R.string.post)
                 R.id.nav_network -> getString(R.string.network)
                 R.id.nav_worklist -> getString(R.string.work_list)
                 else -> getString(R.string.app_name)
             }
             supportActionBar?.title = label
         }
     }*/
    private fun updateToolbarTitles() {
        val navController = findNavController(R.id.nav_host_fragment_content_main2)

        // Define top-level destinations
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_employeer_home,
                R.id.nav_post,
                R.id.nav_network,
                R.id.nav_worklist,
                R.id.nav_profile
            ),
            drawerLayout // <- Make sure your DrawerLayout id is passed here
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val label = when (destination.id) {
                R.id.nav_home -> getString(R.string.menu_home)
                R.id.nav_employeer_home -> getString(R.string.menu_home)
                R.id.nav_post -> getString(R.string.post)
                R.id.nav_network -> getString(R.string.network)
                R.id.nav_worklist -> getString(R.string.work_list)
                else -> getString(R.string.app_name)
            }
            supportActionBar?.title = label
        }
    }

    private fun updateBottomMenuTitles() {
        val menu = bottomNavigation.menu
        menu.findItem(R.id.bottom_nav_home)?.title = getString(R.string.menu_home)
        menu.findItem(R.id.bottom_nav_post)?.title = getString(R.string.post)
        menu.findItem(R.id.bottom_nav_network)?.title = getString(R.string.network)
        menu.findItem(R.id.bottom_nav_worklist)?.title = getString(R.string.work_list)
        menu.findItem(R.id.bottom_nav_profile)?.title = getString(R.string.profile)
    }

    private fun updateMenuTitles() {

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val menu = navigationView.menu
        menu.findItem(R.id.nav_home)?.title = getString(R.string.menu_home)
        menu.findItem(R.id.nav_post)?.title = getString(R.string.post)
        menu.findItem(R.id.nav_network)?.title = getString(R.string.network)
        menu.findItem(R.id.nav_worklist)?.title = getString(R.string.work_list)
        menu.findItem(R.id.nav_language)?.title = getString(R.string.change_lang)
        menu.findItem(R.id.nav_profile)?.title = getString(R.string.profile)
        menu.findItem(R.id.nav_privacy_policy)?.title = getString(R.string.privacy_policy)
        menu.findItem(R.id.nav_logout)?.title = getString(R.string.logout)
        menu.findItem(R.id.nav_logIn)?.title = getString(R.string.login)
    }


    private fun goMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {

                    AlertDialog.Builder(this).apply {
                        setTitle(R.string.background_permission)
                        setMessage(R.string.location_permission)
                        setPositiveButton(R.string.accept,
                            DialogInterface.OnClickListener { dialog, id ->
                                requestBackgroundLocationPermission()
                                startForegroundPermission()

                                Log.e(TAG, "*permission request*")


                            })
                        setNeutralButton(R.string.deny,
                            DialogInterface.OnClickListener { dialog, id ->
                                dialog.dismiss();
                                Log.e(TAG, "*permission deny request*")
                            })
                    }.create().show()

                } else if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    Log.e(TAG, "addressLine $addressLine ")
                    Log.e(TAG, "latitude $latitude ")
                    Log.e(TAG, "longitude $longitude ")
                    //  if (!addressLine.isNullOrEmpty() && latitude != null && longitude != null && latitude != 0.0 && longitude != 0.0) {
                    Log.e(TAG, "*permission granted*")
                    Log.e(TAG, "GO Map 1")

                    if (isuserlgin) { // Check if the user is logged in
                        when {
                            userRole == "Find Job" && roleId == "3" -> {

                                val intent =
                                    Intent(this@MainActivity, EmpGoogleMapActivity::class.java)
                                startActivity(intent)
                            }

                            userRole == "Hire Top Talent" && roleId == "2" -> {
                                val intent =
                                    Intent(this@MainActivity, GoogleMapActivity::class.java)
                                startActivity(intent)
                            }

                            else -> {
                                Toast.makeText(
                                    this,
                                    "You are not authorized to access this section.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this,
                            getString(R.string.login_msg),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {

                Log.e(TAG, "*permission granted*")
                Log.e(TAG, "GO Map 2")

                if (isuserlgin) { // Check if the user is logged in
                    when {
                        userRole == "Find Job" && roleId == "3" -> {

                            val intent = Intent(this@MainActivity, EmpGoogleMapActivity::class.java)
                            startActivity(intent)
                        }

                        userRole == "Hire Top Talent" && roleId == "2" -> {
                            val intent = Intent(this@MainActivity, GoogleMapActivity::class.java)
                            startActivity(intent)
                        }

                        else -> {
                            Toast.makeText(
                                this,
                                getString(R.string.login_msg),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.login_msg),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        } else if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                lifecycleScope.launch {
                    val translatedTitle = TranslationHelper.translateText(
                        "ACCESS FINE LOCATION", languageName
                    )
                    val translatedMessage = TranslationHelper.translateText(
                        "Location permission required", languageName
                    )

                    AlertDialog.Builder(this@MainActivity)
                        .setTitle(translatedTitle)
                        .setMessage(translatedMessage)
                        .setPositiveButton(
                            "OK"
                        ) { _, _ ->
                            requestFineLocationPermission()

                        }
                        .create()
                        .show()
                }
            } else {
                requestFineLocationPermission()
            }

        }

    }

    private fun sendLocation(token: String, userid: String, longitude: Double, latitude: Double) {
        try {
            if (isOnline) {
                viewModel.sendLocationResult.observe(this) { response ->
                    pd?.show() // Show loading indicator
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            Log.e(TAG, "send location : ${response.data!!.message}")

                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator

                            Toast.makeText(
                                this@MainActivity,
                                response.msg ?: "",
                                Toast.LENGTH_SHORT
                            )
                                .show() // Show error message
                        }

                        is BaseResponse.Loading -> {
                            // Show loading indicator if needed
                        }
                    }
                }

                viewModel.sendLocation(
                    token = token!!,
                    userId = userid!!,
                    longitude = longitude,
                    latitude = latitude
                )

            } else {
                Toast.makeText(this@MainActivity, "Internet not connected", Toast.LENGTH_SHORT)
                    .show()
            }

        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e(TAG, "Error occurred: ${e.localizedMessage}")
            Toast.makeText(
                this@MainActivity,
                "An error occurred: ${e.localizedMessage}",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }


    private fun getJobRequestDialog() {
        try {
            if (isOnline) {
                availableViewModel.dialogJobListResult.observe(this) { response ->
                    pd?.show() // Show loading indicator
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            Log.e(TAG, "got job data : ${response.data!!}")
                            if (response.data?.data.isNullOrEmpty()) {
                                // If the data is null or empty


                            } else {
                                // If the data is available
                                jobList = (response.data?.data
                                    ?: emptyList()) as List<JobItem> // Extract the data, or use an empty list if null
                                Log.e(TAG, "got job data : ${jobList}")


                                getShowDialog(jobList)

                            }

                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator

                        }

                        is BaseResponse.Loading -> {
                            // Show loading indicator if needed
                        }
                    }
                }

                availableViewModel.getJobRequestDialog(
                    user_id = userid!!
                )

            } else {
                Toast.makeText(this@MainActivity, "Internet not connected", Toast.LENGTH_SHORT)
                    .show()
            }

        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e(TAG, "Error occurred: ${e.localizedMessage}")
            Toast.makeText(
                this@MainActivity,
                "An error occurred: ${e.localizedMessage}",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    /*private fun getShowDialog(jobList: List<JobItem>) {
        Log.e(TAG, "got job data :*** ${jobList}")

        // Inflate the custom dialog layout
        val dialogView = LayoutInflater.from(this).inflate(R.layout.got_job_dialog, null)

        // Create the AlertDialog
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)

        val alertDialog = builder.create()

        // Initialize RecyclerView from the dialogView, not alertDialog
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerViewJob)

        recyclerView.layoutManager = LinearLayoutManager(this)

        Log.e(TAG, "got job data :111 ${jobList}")

        gotJobAdapter = GotJobAdapter(
            this,
            jobList,
            selectedEmpIds,
            ::selectedEmp
        )
        recyclerView.adapter = gotJobAdapter

        alertDialog.show()
    }*/

    private fun getShowDialog(jobList: List<JobItem>) {
        Log.e(TAG, "got job data :*** ${jobList}")

        // Inflate the custom dialog layout
        val dialogView = LayoutInflater.from(this).inflate(R.layout.got_job_dialog, null)

        // Create the AlertDialog
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false) // Prevents dialog from closing when touching outside

        val alertDialog = builder.create()

        val btnSubmit = dialogView.findViewById<Button>(R.id.btnSubmit)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerViewJob)
        recyclerView.layoutManager = LinearLayoutManager(this)

        gotJobAdapter = EmpGotJobAdapter(
            this,
            jobList,
            jobIds,
            ::selectedJobId,
            ::getEmployeerId
        )
        recyclerView.adapter = gotJobAdapter

        // Handle btnSubmit click to dismiss the dialog

        btnSubmit.setOnClickListener {
            // Perform your submit actions here
            Log.d(TAG, "Submit button clicked $EmployeerId $jobIds $userid")

            if (jobIds.isNullOrEmpty() || EmployeerId.isNullOrEmpty() || userid.isNullOrEmpty()) {
                Toast.makeText(
                    this@MainActivity, "Please Select Job",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                updateJobStatus(jobIds, EmployeerId, userid.toString())
                alertDialog.dismiss() // Dismiss the dialog only on button click
            }


        }

        alertDialog.show()
    }

    private fun updateJobStatus(
        jobIds: MutableList<Int>,
        EmployeerId: MutableList<Int>,
        userid: String
    ) {
        Log.d(TAG, "call  ${EmployeerId} ${jobIds} $userid")

        try {
            if (isOnline) {
                availableViewModel.updateFinalEmployeeStatusResult.observe(this) { response ->
                    pd?.show() // Show loading indicator
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            Log.e(TAG, "final job data : ${response.data!!}")

                            Toast.makeText(this, response.data!!.message, Toast.LENGTH_SHORT)
                                .show()
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



                availableViewModel.updateFinalEmployeeStatus(
                    token = token!!,
                    employer_ids = EmployeerId,
                    employee_id = userid,
                    job_ids = jobIds
                )

            } else {
                Toast.makeText(this@MainActivity, "Internet not connected", Toast.LENGTH_SHORT)
                    .show()
            }

        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e(TAG, "Error occurred: ${e.localizedMessage}")
            Toast.makeText(
                this@MainActivity,
                "An error occurred: ${e.localizedMessage}",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun getEmployeerId(employeerId: List<Int>) {
        EmployeerId = employeerId.toMutableList()
        Log.e("emp list", "EmployeerId: $EmployeerId")
    }


    private fun selectedJobId(jobId: List<Int>) {
        Log.e("emp list", "Selected Employees before update: $jobId")

        jobIds = jobId.toMutableList()


        Log.e("emp list", "Selected Employees after update: $jobIds")
    }

    private fun getAvailabilityStatus() {
        try {
            if (isOnline) {
                availableViewModel.availableStatusResult.observe(this) { response ->
                    pd?.show() // Show loading indicator
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            /* Toast.makeText(this, response.data!!.message, Toast.LENGTH_SHORT)
                                 .show()*/
                            Log.e("tag", "avai status : ${response.data!!.message}")
                            val status = response.data!!.available_status

                            if (status.equals("pending")) {
                                availabilityDialog()
                            }

                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss() // Dismiss loading indicator

                        }

                        is BaseResponse.Loading -> {
                            // Show loading indicator if needed
                        }
                    }
                }

                availableViewModel.getAvailableStatus(
                    user_id = userid!!
                )

            } else {
                Toast.makeText(this@MainActivity, "Internet not connected", Toast.LENGTH_SHORT)
                    .show()
            }

        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("LikePostError", "Error occurred: ${e.localizedMessage}")

            Toast.makeText(
                this@MainActivity,
                "An error occurred: ${e.localizedMessage ?: "Unknown error"}",
                Toast.LENGTH_SHORT
            ).show()

        }
    }


    private fun loadHomeFragment() {
        val navController = findNavController(R.id.nav_host_fragment_content_main2)
        if (isuserlgin) {  // Check if the user is logged in
            when (roleId) {
                "2" -> {
                    userRole = "Hire Top Talent"
                    fabJobPost.visibility = View.VISIBLE
                    navController.navigate(R.id.nav_employeer_home) // Employer home
                }

                "3" -> {
                    userRole = "Find Job"
                    fabJobPost.visibility = View.GONE
                    navController.navigate(R.id.nav_home) // Job seeker home
                }
                /*else -> {
                    userRole = "Find Job"
                    fabJobPost.visibility = View.GONE
                    navController.navigate(R.id.nav_home) // Default case
                }*/
            }
        } else {
            // If not logged in, follow the original logic based on userRole
            when (userRole) {
                "Find Job" -> {
                    fabJobPost.visibility = View.GONE
                    navController.navigate(R.id.nav_home)
                }

                "Hire Top Talent" -> {
                    fabJobPost.visibility = View.VISIBLE
                    navController.navigate(R.id.nav_employeer_home)
                }
                /*else -> {
                    fabJobPost.visibility = View.GONE
                    navController.navigate(R.id.nav_home)
                }*/
            }
        }
    }


    private fun availabilityDialog() {

        bottomSheetAvailableDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)

        val dialogView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_availability, null)

        bottomSheetAvailableDialog?.setContentView(dialogView)

        // Flag to track which button was clicked
        var buttonClickedFlag = ""

        // Find the buttons
        btnYes = dialogView.findViewById<AppCompatButton>(R.id.btnYes)!!
        btnNo = dialogView.findViewById<AppCompatButton>(R.id.btnNo)!!
        tvMsg = dialogView.findViewById<TextView>(R.id.tvMsg)!!

        when (userRole) {
            "Find Job" -> {
                tvMsg.text = getString(R.string.available_msg_emp)
            }

            "Hire Top Talent" -> {
                tvMsg.text = getString(R.string.available_msg_employeer)
            }
        }

        // Set onClickListeners
        btnNo.setOnClickListener {
            buttonClickedFlag = "not available"  // Set the flag when No button is clicked
            bottomSheetAvailableDialog?.dismiss()  // Dismiss the dialog
            addAvailabilityStatus(buttonClickedFlag)
        }

        btnYes.setOnClickListener {
            buttonClickedFlag = "available"  // Set the flag when Yes button is clicked
            bottomSheetAvailableDialog?.dismiss()  // Dismiss the dialog
            addAvailabilityStatus(buttonClickedFlag)
        }

        // Show the dialog
        bottomSheetAvailableDialog?.show()
    }

    private fun addAvailabilityStatus(available_status: String) {
        Log.e("DialogButtonClick", "Button clicked: $available_status")
        try {
            if (isOnline) {

                val token = token
                val userId = userid

                if (token.isNullOrEmpty() || userId.isNullOrEmpty()) {
                    /*  Toast.makeText(
                          this@MainActivity,
                          "User details are missing. Please Log in.",
                          Toast.LENGTH_SHORT
                      ).show()*/
                    Log.e(
                        "Main activity",
                        "DialogButtonClick : User details are missing. Please Log in."
                    )
                    return
                }
                availableViewModel.addAvailabilityResult.observe(this) { response ->
                    pd?.show() // Show loading indicator
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss() // Dismiss loading indicator

                            /*   Toast.makeText(this, response.data!!.message, Toast.LENGTH_SHORT)
                                   .show()*/


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

                availableViewModel.addAvailability(
                    token = token,
                    user_id = userId,
                    available_status = available_status
                )

            } else {
                Toast.makeText(this@MainActivity, "Internet not connected", Toast.LENGTH_SHORT)
                    .show()
            }

        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("LikePostError", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(
                this@MainActivity,
                "An error occurred: ${e.localizedMessage}",
                Toast.LENGTH_SHORT
            )
                .show()
        }

    }


    /* private fun prepareMenuData() {
         listGroupTitles = listOf("Home", "Post", "Profile", "Job Seeker", "Help", "Log Out")
         listChildData = hashMapOf(
             "Home" to listOf("Dashboard"),
             // "Profile" to listOf("Account", "Preferences"),
             "Job Seeker" to listOf("Skilled", "UnSkilled"),
             "Help" to listOf("About Us", "Contact Us")
         )


     }

     private fun populateExpandableList() {
         expandableListAdapter = ExpandableListAdapter(this, listGroupTitles!!, listChildData!!)
         expandableListView?.setAdapter(expandableListAdapter)

         expandableListView?.setOnGroupClickListener { _, _, groupPosition, _ ->

             // Handle clicking the Profile group (assuming you want to open ProfileActivity when selecting the Profile group)
             if (listGroupTitles?.get(groupPosition) == "Profile") {
                 startActivity(Intent(this, ProfileActivity::class.java))
                 drawerLayout.closeDrawers() // Close drawer after navigating to ProfileActivity
                 return@setOnGroupClickListener true // Indicate that the group click has been handled
             }
             if (listGroupTitles?.get(groupPosition) == "Post") {
                 replaceFragment(PostFragment())
                 drawerLayout.closeDrawers() // Close drawer after navigating to ProfileActivity
                 return@setOnGroupClickListener true // Indicate that the group click has been handled
             }
             if (listGroupTitles?.get(groupPosition) == "Log Out") {

                 *//* isuserlgin = false
             username = ""
             userid = ""
             userotp = ""
             Toast.makeText(this, "User Logout Successfully", Toast.LENGTH_SHORT).show()
             startActivity(Intent(this, LoginActivity::class.java))
             finish()*//*
            logOut()
            true
        }

        false
    }

    expandableListView!!.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
        val selectedItem =
            listChildData?.get(listGroupTitles!![groupPosition])?.get(childPosition)

        when (selectedItem) {
            "Dashboard" -> startActivity(
                Intent(
                    this, MainActivity::class.java
                )
            )// openFragment(DashboardFragment())
            //"Profile" -> startActivity(Intent(this, ProfileActivity::class.java))
            // "Post" ->startActivity(Intent(this, PostFragment::class.java))// openFragment(AccountFragment())
            else -> Toast.makeText(this, "Clicked: $selectedItem", Toast.LENGTH_SHORT).show()
        }

        //  drawerLayout.closeDrawers()
        true
    }
}*/


    private fun logOut() {
        if (isOnline) {
            viewModel.logOutResult.observe(this) { response ->
                when (response) {
                    is BaseResponse.Loading -> {
                        pd?.show()

                    }

                    is BaseResponse.Success -> {
                        pd?.dismiss()


                        response.data?.let { data ->
                            // Logging
                            Log.e("TAG", "Response Code: ${data.code}")
                            Log.e("TAG", "Message: ${data.message}")

                            if (response.data.code == "200") {


                                isuserlgin = false
                                // languageName = ""
                                username = ""
                                mobile_no = ""
                                usermobilenumber = ""
                                userid = ""
                                userotp = ""
                                AppRole = ""
                                user_profile = ""
                                companyName = ""
                                designationName = ""
                                token = ""
                                roleId = ""
                                userotp = ""
                                resenduserotp = ""
                                userRole = ""

                                //   LanguagePref.setLanguage(this, "")
                                LanguagePref.setRoleId(this, "")
                                LanguagePref.setUserLogin(this, false)
                                LanguagePref.setUserId(this, "")
                                LanguagePref.setAppRole(this, "")
                                LanguagePref.setUserName(this, "")
                                LanguagePref.setUserProfile(this, "")
                                LanguagePref.setMobileNo(this, "")
                                LanguagePref.setDesignationName(this, "")
                                LanguagePref.setCompanyName(this, "")
                                LanguagePref.setUserToken(this, "")
                                LanguagePref.setFCMToken(this, "")
                                LanguagePref.setUserOTP(this, "")
                                LanguagePref.setUserResendOTP(this, "")

                                clearPrefs() // Clears all saved SharedPreferences

                                Toast.makeText(this@MainActivity, data.message, Toast.LENGTH_SHORT)
                                    .show()

                                val intent = Intent(this, LoginActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()

                            } else {
                                Toast.makeText(this@MainActivity, data.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }

                    is BaseResponse.Error -> {
                        processError(response.msg)
                        Log.e("TAG", "Error: ${response.msg}")
                        Toast.makeText(this@MainActivity, response.msg ?: "", Toast.LENGTH_SHORT)
                            .show()
                        pd!!.dismiss()
                    }

                    else -> {
                        pd!!.dismiss()
                    }
                }
            }


            val userId = userid

            if (token.isNullOrEmpty() || userId.isNullOrEmpty()) {
                Toast.makeText(this@MainActivity, "Failed to logOut as user! ", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            viewModel.logOut(
                token = token!!,
                userId = userid!!,
                device_token = FCM_TOKENa!!
            )

        } else {
            Toast.makeText(this, "Internet not connected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processError(msg: String?) {
        showToast("Error:$msg")
        Log.e(TAG, "processError : $msg")
    }

    private fun showToast(msg: String) {
        Log.e(TAG, "showToast msg:$msg")
        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
    }


    /* override fun onCreateOptionsMenu(menu: Menu): Boolean {
         // Inflate the menu; this adds items to the action bar if it is present.
         menuInflater.inflate(R.menu.main, menu)
         return true
     }*/
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        val menuItem = menu.findItem(R.id.action_notification)
        val actionView = menuItem.actionView

        textNotificationCount = actionView!!.findViewById(R.id.text_notification_count)

        updateNotificationCount(notificationCount)

        actionView.setOnClickListener {
            onOptionsItemSelected(menuItem)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notification -> {
                //Toast.makeText(this, "Notification clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, NotificationListActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
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


    // Example: Call this function when you receive new notifications
    /* private fun simulateNotificationArrival() {
         notificationCount += 1
         updateNotificationCount(notificationCount)
     }*/


    override fun onSupportNavigateUp(): Boolean {
        drawerLayout.openDrawer(navView)
        val navController = findNavController(R.id.nav_host_fragment_content_main2)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

            /*  REQUEST_SCHEDULE_EXACT_ALARM_PERMISSION -> {
                  val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                      if (alarmManager.canScheduleExactAlarms()) {
                          // Schedule your exact alarm here
                          scheduleNotifications(this)
                          Log.e("tag", "permission granted")
                      } else {
                          // Handle the case where the user did not grant the permission
                          Log.e("tag", "permission denied")
                      }
                  } else {
                      // For versions below Android S, you might need different logic
                      // to handle alarm scheduling permissions
                      // Assume permission is granted for simplicity, but add necessary checks if required
                      scheduleNotifications(this)
                      Log.e("tag", "permission granted for SDK < S")
                  }

              }*/

            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    Log.e(TAG, "startLocationService granted")
                    // All requested permissions are granted, start the foreground service
                    // startLocationService()
                } else {
                    Log.e(TAG, "startLocationService denide")
                    startForegroundPermission()
                    // Permission denied, show an appropriate message
                    Toast.makeText(
                        this,
                        "Location and foreground service permissions are required",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            /* REQUEST_CODE_SCHEDULE_EXACT_ALARM -> {

                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {  // Ensure API level is 31 or higher
                     val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                     if (!alarmManager.canScheduleExactAlarms()) {
                         Toast.makeText(
                             this,
                             "Permission not granted to set exact alarms",
                             Toast.LENGTH_SHORT
                         ).show()
                         Log.e("tag", "alarms permission not granted")
                     } else {
                         restartLocation()  // Permission granted, proceed with the location service
                         Log.e("tag", "alarms permission granted")
                     }
                 }
                 return
             }*/

            REQUEST_LOCATION_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Toast.makeText(this, "permission granted", Toast.LENGTH_LONG).show()
                    }
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }


            MY_FINE_LOCATION_REQUEST -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        requestBackgroundLocationPermission()
                    }

                } else {
                    Toast.makeText(
                        this,
                        "ACCESS FINE LOCATION permission denied",
                        Toast.LENGTH_LONG
                    ).show()
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", this.packageName, null)
                            ),
                        )
                    }
                }
                return
            }

            MY_BACKGROUND_LOCATION_REQUEST -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        Toast.makeText(
                            this,
                            "Background location Permission Granted",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(this, "Background location permission denied", Toast.LENGTH_LONG)
                        .show()
                }
                return
            }

        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_content_main2, fragment).commit()
    }


    private fun getLocationData() {
        try {
            if (VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                getLocation()
            } else {
                checkLocationPermission()
            }
        } catch (e: Exception) {
            // Handle the exception or leave it empty based on your requirements
        }
    }

    private fun getLocation() {
        try {
            if (checkGPSEnabled()) {

                /* val fusedLocationProviderApi: FusedLocationProviderApi =
                     LocationServices.FusedLocationApi
                 val googleApiClient: GoogleApiClient = this.mGoogleApiClient!!
                 val lastLocation: Location? =
                     fusedLocationProviderApi.getLastLocation(googleApiClient)
                 this.mLocation = lastLocation

                 if (lastLocation == null) {
                     startLocationUpdates()
                 }

                 val location: Location? = this.mLocation

                 if (location != null) {
                     this.latitude = location.latitude
                     this.longitude = location.longitude

                     Log.e(this.TAG, "onCreate: latitude $latitude")
                     Log.e(this.TAG, "onCreate: longitude $longitude")

                     getAddress(latitude ?: 0.0, longitude ?: 0.0)


                     val cal = Calendar.getInstance()
                     val currentDateTimeString = SimpleDateFormat("HH", Locale.US).format(cal.time)
                     this.Currenttime = currentDateTimeString.toInt()
                     return
                 }*/
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {

                    fusedLocationProviderClient.lastLocation.addOnCompleteListener { task: Task<Location> ->
                        if (task.isSuccessful && task.result != null) {
                            val location: Location = task.result
                            latitude = location.latitude
                            longitude = location.longitude

                            getAddress(latitude ?: 0.0, longitude ?: 0.0)

                            sendLocation(token, userid, longitude!!, latitude!!)

                            // Use latitude and longitude
                            Log.e(TAG, "Latitude: $latitude, Longitude: $longitude")
                        } else {
                            Log.e(TAG, "Failed to get location")
                        }
                    }
                }
                Log.e(TAG, "Location not Detected")
                // Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle exception
        }
    }

    private fun getAddress(latitude: Double, longitude: Double) {
        // Use Geocoder to get the address
        val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocation(latitude!!, longitude!!, 1)!!
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                addressLine = address.getAddressLine(0) // Full address
                Log.e(TAG, "Address: $addressLine")
            } else {
                addressLine = "No address found for the location"
                // Log.e(TAG, "No address found for the location")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(TAG, "Geocoder error: ${e.message}")
            // addressLine = "No address found for the location"
        }
    }

    private fun checkGPSEnabled(): Boolean {
        if (!isLocationEnabled())
            showAlert()
        return isLocationEnabled()
    }

    private fun showAlert() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Enable Location")
            .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " + "use this app")
            .setPositiveButton("Location Settings") { paramDialogInterface, paramInt ->
                val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(myIntent)
            }
            .setNegativeButton("Cancel") { paramDialogInterface, paramInt -> }
        dialog.show()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        Log.e(TAG, "isLocationEnabled: $locationManager")
        Log.e(TAG, "isLocationEnabled: gps")
        Log.e(TAG, "isLocationEnabled: network")
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            AlertDialog.Builder(this)
                .setTitle("Location Permission Needed")
                .setMessage("This app needs the Location permission, please accept to use location functionality")
                .setPositiveButton("OK") { _, _ -> requestLocationPermission() }
                .create()
                .show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_CODE
            )
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_LOCATION_CODE
        )
    }

    /*private fun startLocationUpdates() {
        mLocationRequest =
            LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL.toLong())
                .setFastestInterval(FASTEST_INTERVAL)

        // Check if the app has location permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val fusedLocationProviderApi = LocationServices.FusedLocationApi
            val googleApiClient = mGoogleApiClient ?: return
            val locationRequest = mLocationRequest ?: return

            fusedLocationProviderApi.requestLocationUpdates(
                googleApiClient,
                locationRequest,
                this
            )
        }
    }*/


    private fun requestBackgroundLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(ACCESS_BACKGROUND_LOCATION), MY_BACKGROUND_LOCATION_REQUEST
        )
    }

    private fun requestFineLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            MY_FINE_LOCATION_REQUEST
        )
    }

    private fun startForegroundPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+ (API level 33), check both LOCATION and FOREGROUND_SERVICE_LOCATION permissions
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.FOREGROUND_SERVICE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                // Request the missing permissions
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.FOREGROUND_SERVICE_LOCATION
                    ),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            } else {
                Log.e(TAG, "Permissions are granted, start the foreground service")
                // Permissions are granted, start the foreground service
                // startForegroundServiceWithLocation()
            }
        } else {
            // For Android versions lower than 13, check the normal location permissions
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                // Request the location permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            } else {
                Log.e(TAG, "Permissions are granted, start the foreground service")
                // Permissions are granted, start the foreground service
                //   startForegroundServiceWithLocation()
            }
        }

    }

    /*override fun onBackPressed() {
        super.onBackPressed()
        if (this.drawerLayout.isDrawerOpen(START)) {
            this.drawerLayout.closeDrawer(START)
        } else {
            finishAffinity()
            onBackPressedDispatcher.onBackPressed()
        }
    }*/

    /* override fun onBackPressed() {
         super.onBackPressed()
         if (drawerLayout.isDrawerOpen(START)) {
             drawerLayout.closeDrawer(START)
         } else {
             showExitConfirmationDialog()
         }
     }*/

    /*override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(START)) {
            drawerLayout.closeDrawer(START)
            return
        }

        val currentDestId = navController.currentDestination?.id

        if (currentDestId == R.id.nav_home || currentDestId == R.id.nav_employeer_home) {
            // If the user is already on the landing page (home screen), show exit dialog
            showExitConfirmationDialog()
        } else {
            // Otherwise, perform normal back navigation
            super.onBackPressed()
        }
    }*/

    /* override fun onBackPressed() {
         if (drawerLayout.isDrawerOpen(START)) {
             drawerLayout.closeDrawer(START)
             return
         }

         val currentDestId = navController.currentDestination?.id
         Log.e("BAckPress"," dest id $currentDestId")

         if (currentDestId == R.id.nav_home || currentDestId == R.id.nav_employeer_home) {
             // Already on home screen, show exit confirmation
             showExitConfirmationDialog()
         } else {
             super.onBackPressed()

             // Update BottomNavigationView icon after navigation is complete
             Handler(Looper.getMainLooper()).postDelayed({
                 navController.currentDestination?.id?.let { destinationId ->
                     when (destinationId) {
                         R.id.nav_home -> bottomNavigation.selectedItemId = R.id.bottom_nav_home
                         R.id.nav_employeer_home -> bottomNavigation.selectedItemId = R.id.bottom_nav_home
                         R.id.nav_worklist -> bottomNavigation.selectedItemId = R.id.bottom_nav_worklist
                         R.id.nav_post -> bottomNavigation.selectedItemId = R.id.bottom_nav_post
                         R.id.nav_network -> bottomNavigation.selectedItemId = R.id.bottom_nav_network
                         R.id.nav_profile -> bottomNavigation.selectedItemId = R.id.bottom_nav_profile
                     }
                 }
             }, 100) // Give time for fragment to pop before checking
         }
     }*/

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(START)) {
            drawerLayout.closeDrawer(START)
            return
        }

        val currentDestId = navController.currentDestination?.id
        Log.e("BackPress", "dest id $currentDestId")

        val destName = when (currentDestId) {
            R.id.nav_home -> "HomeFragment"
            R.id.nav_employeer_home -> "EmployeerHomeFragment"
            R.id.nav_worklist -> "WorkListFragment"
            R.id.nav_post -> "PostFragment"
            R.id.nav_network -> "NetworkFragment"
            R.id.nav_profile -> "ProfileFragment"
            else -> "Unknown Destination"
        }

        Log.e("BackPress", "Currently at: $destName (id: $currentDestId)")


        if (currentDestId == R.id.nav_home || currentDestId == R.id.nav_employeer_home) {
            showExitConfirmationDialog()
        } else {
            super.onBackPressedDispatcher.onBackPressed() // Use this for consistent behavior

            // Post a delayed task to ensure fragment pop has completed
            Handler(Looper.getMainLooper()).postDelayed({
                when (navController.currentDestination?.id) {
                    R.id.nav_home,
                    R.id.nav_employeer_home -> bottomNavigation.selectedItemId =
                        R.id.bottom_nav_home

                    R.id.nav_worklist -> bottomNavigation.selectedItemId = R.id.bottom_nav_worklist
                    R.id.nav_post -> bottomNavigation.selectedItemId = R.id.bottom_nav_post
                    R.id.nav_network -> bottomNavigation.selectedItemId = R.id.bottom_nav_network
                    R.id.nav_profile -> bottomNavigation.selectedItemId = R.id.bottom_nav_profile
                }
            }, 150)

            Log.e("BackPress", "Currently at: **$destName (id: $currentDestId)")
        }
    }


    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.app_name)
            .setMessage(R.string.exit_msg)
            .setPositiveButton(R.string.yes) { _, _ ->
                finishAffinity() // Closes all activities and exits the app

            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss() // Dismiss the dialog
            }
            .setCancelable(false) // Prevent dialog from closing on outside touch
            .show()
    }

    override fun onApplyJobClick() {
        if (isuserlgin) {
            when {
                userRole == "Find Job" && roleId == "3" -> {
                    // Navigate to Fragment
                    navController.navigate(R.id.nav_worklist)
                }

                userRole == "Hire Top Talent" && roleId == "2" -> {
                    // Start Activity
                    startActivity(Intent(this, WorkListEmployeerActivity::class.java))
                }

                else -> {
                    Toast.makeText(
                        this,
                        "You are not authorized to access this section.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }


    /*private var backPressedTime: Long = 0
     private val doubleBackPressInterval: Long = 2000 // 2 seconds

     override fun onBackPressed() {
         if (drawerLayout.isDrawerOpen(START)) {
             drawerLayout.closeDrawer(START)
         } else {
             if (backPressedTime + doubleBackPressInterval > System.currentTimeMillis()) {
                 super.onBackPressed() // or finishAffinity() to close the app completely
             } else {
                 Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
                 backPressedTime = System.currentTimeMillis()
             }
         }
     }*/


}




