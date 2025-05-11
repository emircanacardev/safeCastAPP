package com.example.safecast

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

class LocationManager(private val context: Context, private val callback: (Location?) -> Unit) {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    init {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    // Konum isteme işlemi
    fun requestCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Konum izni verilmemişse, izin istenmesi sağlanmalı
            Log.e("LocationManager", "Permission not granted for location")
            return
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10000L // Konum güncellemeleri 10 saniyede bir yapılacak
        ).apply {
            setWaitForAccurateLocation(true)
            setMinUpdateIntervalMillis(5000L) // 5 saniye minimum güncelleme aralığı
        }.build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location: Location? = locationResult.lastLocation
                if (location != null) {
                    Log.d("LocationManager", "Real-time Lat: ${location.latitude}, Lng: ${location.longitude}")
                    callback(location)  // Konum verisini callback ile geri gönder
                } else {
                    Log.d("LocationManager", "Failed to get real-time location.")
                    callback(null)  // Konum alınamadığında null döndür
                }

                fusedLocationClient.removeLocationUpdates(this) // Konum güncellemelerini durdur
            }
        }

        // Konum güncellemelerini başlat
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()  // Ana iş parçacığında çalışacak
        )
    }

    // Son bilinen konumu almak için
    fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Konum izni verilmemişse, izin istenmesi sağlanmalı
            Log.e("LocationManager", "Permission not granted for location")
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                Log.d("LocationManager", "Last known location - Lat: ${location.latitude}, Lng: ${location.longitude}")
                callback(location)  // Son bilinen konumu callback ile geri gönder
            } else {
                Log.d("LocationManager", "No last known location available")
                callback(null)  // Konum alınamadıysa null döndür
            }
        }
    }
}
