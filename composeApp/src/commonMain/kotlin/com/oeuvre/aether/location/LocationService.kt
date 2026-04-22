package com.oeuvre.aether.location

import androidx.compose.runtime.Composable

interface LocationService {
    suspend fun getCurrentLocation(): LatLng?
    fun isLocationEnabled(): Boolean
}

@Composable
expect fun rememberLocationService(): LocationService
