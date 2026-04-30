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
import com.oeuvre.aether.model.Itinerary

private val DEFAULT_POSITION = LatLng(latitude = 30.2672, longitude = -97.7431)
private const val DEFAULT_ZOOM = 4f
private const val USER_ZOOM = 14f

@Composable
fun MapScreen(locationService: LocationService, itinerary: Itinerary? = null, modifier: Modifier = Modifier) {
    var cameraState by remember {
        mutableStateOf(MapCameraState(target = DEFAULT_POSITION, zoom = DEFAULT_ZOOM))
    }

    LaunchedEffect(Unit) {
        locationService.getCurrentLocation()?.let { userLocation ->
            cameraState = MapCameraState(target = userLocation, zoom = USER_ZOOM)
        }
    }

    NativeMapView(modifier = modifier, cameraState = cameraState, itinerary = itinerary)
}
