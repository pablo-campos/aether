package com.oeuvre.aether.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng as GoogleLatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.CameraPosition

@Composable
actual fun NativeMapView(modifier: Modifier, cameraState: MapCameraState) {
    val googleLatLng = GoogleLatLng(cameraState.target.latitude, cameraState.target.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(googleLatLng, cameraState.zoom)
    }

    LaunchedEffect(cameraState) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(
                GoogleLatLng(cameraState.target.latitude, cameraState.target.longitude),
                cameraState.zoom,
            ),
            durationMs = 800,
        )
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(zoomControlsEnabled = false),
    )
}
