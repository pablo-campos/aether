package com.oeuvre.aether.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.oeuvre.aether.location.LatLng
import com.oeuvre.aether.location.LocationService

private val DEFAULT_POSITION = LatLng(latitude = 40.7128, longitude = -74.0060)
private const val DEFAULT_ZOOM = 2f
private const val USER_ZOOM = 14f

@Composable
fun MapScreen(locationService: LocationService, modifier: Modifier = Modifier) {
    var cameraState by remember {
        mutableStateOf(MapCameraState(target = DEFAULT_POSITION, zoom = DEFAULT_ZOOM))
    }

    LaunchedEffect(Unit) {
        val userLocation = locationService.getCurrentLocation()
        if (userLocation != null) {
            cameraState = MapCameraState(target = userLocation, zoom = USER_ZOOM)
        }
    }

    NativeMapView(modifier = modifier, cameraState = cameraState)
}
