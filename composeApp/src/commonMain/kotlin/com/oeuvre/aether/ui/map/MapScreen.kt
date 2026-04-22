package com.oeuvre.aether.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.oeuvre.aether.location.LocationService
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position

private val DEFAULT_POSITION = Position(longitude = -74.0060, latitude = 40.7128) // New York City
private const val DEFAULT_ZOOM = 2.0
private const val USER_ZOOM = 14.0

@Composable
fun MapScreen(locationService: LocationService, modifier: Modifier = Modifier) {
    val cameraState = rememberCameraState(
        firstPosition = CameraPosition(
            target = DEFAULT_POSITION,
            zoom = DEFAULT_ZOOM,
        )
    )

    LaunchedEffect(Unit) {
        val userLocation = locationService.getCurrentLocation()
        if (userLocation != null) {
            cameraState.animateTo(
                finalPosition = CameraPosition(
                    target = Position(
                        longitude = userLocation.longitude,
                        latitude = userLocation.latitude,
                    ),
                    zoom = USER_ZOOM,
                )
            )
        }
    }

    MaplibreMap(
        modifier = modifier,
        baseStyle = BaseStyle.Demo,
        cameraState = cameraState,
    )
}
