package com.sanomush.tari.helper

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices

object LocationUtils {
    fun getLastKnownLocation(context: Context, onLocationResult: (Location?) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        // Cek izin sekali lagi untuk jaga-jaga
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            onLocationResult(null)
            return
        }

        // Ambil lokasi terakhir yang diketahui (paling cepat dan irit baterai/offline)
        fusedLocationClient.lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                onLocationResult(task.result)
            } else {
                onLocationResult(null)
            }
        }
    }
}