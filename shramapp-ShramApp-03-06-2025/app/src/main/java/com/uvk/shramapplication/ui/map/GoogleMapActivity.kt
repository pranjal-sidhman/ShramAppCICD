package com.uvk.shramapplication.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.ui.employeer.home.employeelist.EmployeeDetailsActivity
import com.uvk.shramapplication.ui.login.LoginViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.userid
import com.google.maps.android.SphericalUtil
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.username
import com.uvk.shramapplication.helper.TranslationHelper
import kotlinx.coroutines.launch


class GoogleMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private var lastClickedMarker: Marker? = null
    private val viewModel by viewModels<LoginViewModel>()
    var pd: TransparentProgressDialog? = null
    private lateinit var userList: List<LocationData>
    private var currentLatLng: LatLng? = null
    private var circle: Circle? = null

    private lateinit var seekBar: SeekBar
    private lateinit var tvRadius: TextView
    private lateinit var tvAvailableEmp: TextView
    private lateinit var tvViewList: TextView

    private val radiusOptions =
        listOf(0,5000, 10000, 15000, 20000, 25000) // 5 km, 10 km, 15 km, 20 km, 25 km

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_map)
        seekBar = findViewById(R.id.seekBar)
        tvRadius = findViewById(R.id.tvRadius)
        tvAvailableEmp = findViewById(R.id.tvAvailableEmp)
        tvViewList = findViewById(R.id.tvViewList)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        pd = TransparentProgressDialog(this, R.drawable.progress)

        tvViewList.setOnClickListener {
            val intent = Intent(this@GoogleMapActivity, EmpListActivity::class.java)
                startActivity(intent)
        }

        setupSeekBar()
    }


    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map
        // Enable zoom controls (UI buttons)
        googleMap.uiSettings.isZoomControlsEnabled = true

        // Enable zoom gestures (pinch to zoom)
        googleMap.uiSettings.isZoomGesturesEnabled = true

        // Enable My Location Button
        googleMap.uiSettings.isMyLocationButtonEnabled = true

        getCurrentLocation()
    }


    private fun getCurrentLocation() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1001
            )
            return
        }

        googleMap.isMyLocationEnabled = true


        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLatLng = LatLng(location.latitude, location.longitude)

                // Move camera to current location with zoom level 15 (adjustable)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng!!, 15f))

                updateMapRadius(radiusOptions[0]) // Start with 5 km
                getLocations(userid!!)
            } else {
                Toast.makeText(this, "Unable to fetch current location", Toast.LENGTH_SHORT).show()
            }
        }

    }



    private fun updateMapRadius(radius: Int) {
        currentLatLng?.let {
            circle?.remove() // Remove previous circle

            if (radius > 0) {
                circle = googleMap.addCircle(
                    CircleOptions()
                        .center(it)
                        .radius(radius.toDouble())
                        .strokeColor(Color.RED)
                        .fillColor(Color.argb(50, 255, 0, 0))
                        .strokeWidth(2f)
                )

                val bounds = getBounds(it, radius.toDouble())
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            } else {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 15f)) // fallback to default zoom
            }
        }
    }


    private fun getBounds(center: LatLng, radius: Double): LatLngBounds {
        val southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225.0)
        val northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45.0)
        return LatLngBounds(southwest, northeast)
    }

    private fun setupSeekBar() {
        seekBar.max = radiusOptions.size - 1
        seekBar.progress = 0
        tvRadius.text = "Radius: ${radiusOptions[0] / 1000} km"

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val selectedRadius = radiusOptions[progress]
                tvRadius.text = "Radius: ${selectedRadius / 1000} km"
                updateMapRadius(selectedRadius)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun getLocations(userid: String) {
        try {
            if (isOnline) {
                viewModel.getLocationResult.observe(this) { response ->
                    pd?.show()
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss()
                            userList = response.data!!.data
                            tvAvailableEmp.text = getString(R.string.available_emp) + " : ${response.data!!.count}"
                            displayMarkers(userList)
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


    private fun displayMarkers(userList: List<LocationData>) {
        googleMap.clear()

        /* currentLatLng?.let {
             updateMapRadius(radiusOptions[0]) // Keep the circle when markers refresh
         }*/

        userList.forEach { user ->
            val latitude = user.latitude.toDoubleOrNull() ?: 0.0
            val longitude = user.longitude.toDoubleOrNull() ?: 0.0
            val userLocation = LatLng(latitude, longitude)

            val markerOptions = MarkerOptions()
                .position(userLocation)
                .title(user.user_name)
                .snippet("Designation: ${user.designation}\nMobile: ${user.mobile_no}\nAddress: ${user.address}")
                // .icon(BitmapDescriptorFactory.fromResource(R.drawable.emp_map))
                .icon(
                    resizeMapIcon(
                        R.drawable.user_map_white,
                        200,
                        200,
                        this
                    )
                ) // Adjust width & height as needed

            googleMap.addMarker(markerOptions)?.apply {
                tag = user
            }

            // Handle click on the blue dot (current location)
            googleMap.setOnMyLocationClickListener { location ->
                val currentLocationInfo =
                    "Latitude: ${location.latitude}\nLongitude: ${location.longitude}"
                Toast.makeText(this, username, Toast.LENGTH_SHORT).show()
            }

            // Set a marker click listener to change the icon to user_map_blue
            googleMap.setOnMarkerClickListener { clickedMarker ->
                val user = clickedMarker.tag as? LocationData
                user?.let {
                    showBottomSheetDialog(it, clickedMarker)

                    // Reset the last clicked marker to white if it exists and is not the same as the clicked marker
                    if (lastClickedMarker != null && lastClickedMarker != clickedMarker) {
                        val bitmap =
                            BitmapFactory.decodeResource(resources, R.drawable.user_map_white)
                        val smallMarker = Bitmap.createScaledBitmap(bitmap, 200, 200, false)
                        lastClickedMarker?.setIcon(
                            BitmapDescriptorFactory.fromBitmap(
                                smallMarker
                            )
                        )
                    }

                    // Change the clicked marker icon to blue
                    val bitmap =
                        BitmapFactory.decodeResource(resources, R.drawable.user_map_blue)
                    val smallMarker = Bitmap.createScaledBitmap(bitmap, 200, 200, false)
                    clickedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker))

                    // Update the last clicked marker reference
                    lastClickedMarker = clickedMarker
                }
                true
            }
        }
    }


    @SuppressLint("MissingInflatedId")
    private fun showBottomSheetDialog(user: LocationData, clickedMarker: Marker) {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_details, null)

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
        val tvDetails = dialogView.findViewById<TextView>(R.id.tvDescription)
        val tvLoc = dialogView.findViewById<TextView>(R.id.tvLoc)
        val btnView = dialogView.findViewById<TextView>(R.id.btnView)

        lifecycleScope.launch {
            tvTitle.text = TranslationHelper.translateText(user.user_name ?: "", languageName)
            tvLoc.text =  TranslationHelper.translateText(user.address ?: "", languageName)
            if(!user.designation.isNullOrEmpty()){
                tvDetails.text =  TranslationHelper.translateText(user.designation ?: "", languageName)
            }else{
                tvDetails.text =  TranslationHelper.translateText(user.company_name ?: "", languageName)
            }

        }

        btnView.setOnClickListener {
            Log.e("tag", "AvailableEmpAdapter userId : ${user.user_id}")

            val intent = Intent(this@GoogleMapActivity, EmployeeDetailsActivity::class.java).apply {
                putExtra("emp_id", user.user_id)
            }
            startActivity(intent)
        }

        bottomSheetDialog.setContentView(dialogView)

        // Set margins programmatically
        dialogView.layoutParams = (dialogView.layoutParams as ViewGroup.MarginLayoutParams).apply {
            setMargins(30, 30, 30, 30) // Set left, top, right, bottom margins in pixels
        }

        bottomSheetDialog.show()

        // Handle dialog dismiss to reset the marker icon to white
        bottomSheetDialog.setOnDismissListener {
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.user_map_white)
            val smallMarker = Bitmap.createScaledBitmap(bitmap, 200, 200, false)
            clickedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker))
            lastClickedMarker = null // Reset the last clicked marker
        }
    }

    private fun resizeMapIcon(
        resourceId: Int,
        width: Int,
        height: Int,
        context: Context
    ): BitmapDescriptor {
        val imageBitmap = BitmapFactory.decodeResource(context.resources, resourceId)
        val resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false)
        return BitmapDescriptorFactory.fromBitmap(resizedBitmap)
    }
}