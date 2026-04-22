package com.oeuvre.aether.location

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class IosLocationService : LocationService {

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun getCurrentLocation(): LatLng? = suspendCoroutine { cont ->
        val manager = CLLocationManager()
        val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
                val loc = didUpdateLocations.firstOrNull() as? CLLocation
                manager.stopUpdatingLocation()
                manager.delegate = null
                val result = loc?.coordinate?.useContents { LatLng(latitude, longitude) }
                cont.resume(result)
            }

            override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
                manager.stopUpdatingLocation()
                manager.delegate = null
                cont.resume(null)
            }
        }
        manager.delegate = delegate
        manager.desiredAccuracy = kCLLocationAccuracyBest
        manager.startUpdatingLocation()
    }

    override fun isLocationEnabled(): Boolean = CLLocationManager.locationServicesEnabled()
}

@Composable
actual fun rememberLocationService(): LocationService = remember { IosLocationService() }
