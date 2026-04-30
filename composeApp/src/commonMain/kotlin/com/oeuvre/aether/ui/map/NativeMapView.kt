package com.oeuvre.aether.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.oeuvre.aether.model.Itinerary

@Composable
expect fun NativeMapView(
    modifier: Modifier = Modifier,
    cameraState: MapCameraState,
    itinerary: Itinerary? = null,
)
