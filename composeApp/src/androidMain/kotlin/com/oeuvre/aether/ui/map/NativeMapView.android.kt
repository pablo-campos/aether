package com.oeuvre.aether.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng as GoogleLatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.oeuvre.aether.R

@Composable
actual fun NativeMapView(modifier: Modifier, cameraState: MapCameraState) {
    val context = LocalContext.current
    val mapStyleOptions = remember {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.map_dark_style)
    }
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
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false
        ),
        properties = MapProperties(
            mapStyleOptions = mapStyleOptions,
            isMyLocationEnabled = true
        ),
    )
}
