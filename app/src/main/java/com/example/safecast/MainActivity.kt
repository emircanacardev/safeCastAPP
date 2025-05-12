package com.example.safecast

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.safecast.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var locationManager: LocationManager

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        locationManager = LocationManager(this)

        if (locationManager.hasLocationPermission()) {
            if (locationManager.isLocationEnabled()) {
                Log.d("LM", "Location services enabled!")
                locationManager.getCurrentLocation{ location ->
                    location?.let {
                        Log.d("Location", "Lat: ${it.latitude}, Lng: ${it.longitude}")
                    }
                }
            } else {
                Toast.makeText(this, "Location services off ", Toast.LENGTH_SHORT).show()
            }
        } else {
            locationManager.requestLocationPermission(this)
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        val currentUser = auth.currentUser
        if (currentUser != null) {
            navController.navigate(R.id.mainPageFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
