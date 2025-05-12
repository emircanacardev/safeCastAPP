package com.example.safecast

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager as AndroidLocationManager
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

class LocationManager(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L)
        .setMinUpdateIntervalMillis(5000L)
        .build()

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    //Checks if the user has given location permission
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    //Request the location permission
    fun requestLocationPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    //Checks if the location services are enabled
    fun isLocationEnabled(): Boolean {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as AndroidLocationManager
        return locationManager.isProviderEnabled(AndroidLocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(AndroidLocationManager.NETWORK_PROVIDER)
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getCurrentLocation(onLocationReceived: (Location?) -> Unit) {
        if (!hasLocationPermission()) {
            Log.e("LocationManager", "Konum izni yok")
            onLocationReceived(null)
            return
        }

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->
            onLocationReceived(location)
        }.addOnFailureListener {
            Log.e("LocationManager", "Konum alınamadı: ${it.message}")
            onLocationReceived(null)
        }
    }

    // Yeni fonksiyon: Gerçek zamanlı konum almak için
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun requestSingleUpdate(onLocationReceived: (Location?) -> Unit) {
        if (!hasLocationPermission()) {
            Log.e("LocationManager", "Konum izni yok (requestSingleUpdate)")
            onLocationReceived(null)
            return
        }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L)
            .setWaitForAccurateLocation(true)
            .setMaxUpdates(1)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                fusedLocationClient.removeLocationUpdates(this)
                val location = result.lastLocation
                Log.d("LocationManager", "Güncel konum: ${location?.latitude}, ${location?.longitude}")
                onLocationReceived(location)
            }
        }

        fusedLocationClient.requestLocationUpdates(
            request,
            callback,
            Looper.getMainLooper()
        )
    }
}
