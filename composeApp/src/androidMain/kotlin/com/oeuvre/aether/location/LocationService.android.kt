package com.oeuvre.aether.location

import android.content.Context
import android.location.LocationManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AndroidLocationService(private val context: Context) : LocationService {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    override suspend fun getCurrentLocation(): LatLng? = suspendCoroutine { cont ->
        val request = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()
        try {
            fusedClient.getCurrentLocation(request, null)
                .addOnSuccessListener { loc ->
                    cont.resume(loc?.let { LatLng(it.latitude, it.longitude) })
                }
                .addOnFailureListener {
                    cont.resume(null)
                }
        } catch (_: SecurityException) {
            cont.resume(null)
        }
    }

    override fun isLocationEnabled(): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(lm)
    }
}

@Composable
actual fun rememberLocationService(): LocationService {
    val context = LocalContext.current.applicationContext
    return remember(context) { AndroidLocationService(context) }
}
