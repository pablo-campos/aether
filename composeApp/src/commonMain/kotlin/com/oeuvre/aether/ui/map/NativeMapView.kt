package com.oeuvre.aether.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun NativeMapView(
    modifier: Modifier = Modifier,
    cameraState: MapCameraState,
)
