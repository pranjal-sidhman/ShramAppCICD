package com.uvk.shramapplication.ui.map.root_map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.userid
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.ActivityEmployeeDetailsBinding
import com.uvk.shramapplication.databinding.ActivityRootMapBinding
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.ui.login.LoginViewModel
import com.uvk.shramapplication.ui.map.LocationData

class RootMapActivity :  AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityRootMapBinding
    private var pd: TransparentProgressDialog? = null

    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var userList: List<LocationData>
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLatLng: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRootMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backicon.setOnClickListener { finish() }

        pd = TransparentProgressDialog(this, R.drawable.progress)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this@RootMapActivity)

        getCurrentLocation()
    }

    private fun getCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLatLng = LatLng(location.latitude, location.longitude)
               // viewModel.fetchLocations("4") // call API when location is ready
                fetchUserLocations(userid) // call API when location is ready
            } else {
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

   /* override fun onMapReady(map: GoogleMap) {
        googleMap = map
        fetchUserLocations(userid)
    }*/
   @SuppressLint("MissingPermission")
   override fun onMapReady(map: GoogleMap) {
       googleMap = map

       val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

       fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
           if (location != null) {
               currentLatLng = LatLng(location.latitude, location.longitude)

               // Animate camera to current location
             //  googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng!!, 2f))
               googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng!!, 15f))

               // Fetch and draw user locations
               fetchUserLocations(userid)
           } else {
               Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show()
           }
       }
   }



    private fun fetchUserLocations(userid: String) {
        try {
            if (isOnline) {
                viewModel.getLocationResult.observe(this) { response ->
                    pd?.show()
                    when (response) {
                        is BaseResponse.Success -> {
                            pd?.dismiss()
                            userList = response.data!!.data

                            drawRoutes(response.data.data) // Draw routes from user_id 4 (you) to others

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

    private fun drawRoutes(locations: List<LocationData>) {
        googleMap.clear()

        currentLatLng?.let { source ->
            googleMap.addMarker(MarkerOptions().position(source).title("You").icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(source, 10f))

            for (user in locations) {
                val destLatLng = LatLng(user.latitude.toDouble(), user.longitude.toDouble())

                // Destination Marker
                googleMap.addMarker(
                    MarkerOptions().position(destLatLng).title("${user.user_name} (${user.designation})")
                )

                // Route (straight line)
                googleMap.addPolyline(
                    PolylineOptions()
                        .add(source, destLatLng)
                        .width(5f)
                        .color(Color.BLUE)
                )
            }
        } ?: run {
            Toast.makeText(this, "Current location not available", Toast.LENGTH_SHORT).show()
        }
    }


    /*override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // 🔵 Source: User ID 4
        val sourceLatLng = LatLng(19.9975, 73.7898) // Example: Nashik

        // 🟢 Destination 1: User ID 59
        val destination1 = LatLng(19.9940213, 73.7534856)

        // 🔴 Destination 2: User ID 61
        val destination2 = LatLng(20.0815501, 73.9105367)

        // Markers
        googleMap.addMarker(MarkerOptions().position(sourceLatLng).title("User ID 4 (You)"))
        googleMap.addMarker(MarkerOptions().position(destination1).title("User ID 59"))
        googleMap.addMarker(MarkerOptions().position(destination2).title("User ID 61"))

        // Zoom camera
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sourceLatLng, 12f))

        // Draw lines from user_id=4 to others
        drawRoute(sourceLatLng, destination1, Color.BLUE)
        drawRoute(sourceLatLng, destination2, Color.RED)
    }

    private fun drawRoute(start: LatLng, end: LatLng, color: Int) {
        googleMap.addPolyline(
            PolylineOptions()
                .add(start, end)
                .width(6f)
                .color(color)
        )
    }*/
}