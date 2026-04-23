package com.oeuvre.aether.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKMapView
import kotlin.math.pow

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun NativeMapView(modifier: Modifier, cameraState: MapCameraState) {
    val mapView = remember { MKMapView() }

    LaunchedEffect(cameraState) {
        val coordinate = CLLocationCoordinate2DMake(
            cameraState.target.latitude,
            cameraState.target.longitude,
        )
        val meters = zoomToMeters(cameraState.zoom)
        val region = MKCoordinateRegionMakeWithDistance(coordinate, meters, meters)
        mapView.setRegion(region, animated = true)
    }

    UIKitView(factory = { mapView }, modifier = modifier)
}

private fun zoomToMeters(zoom: Float): Double =
    20_000_000.0 / 2.0.pow((zoom - 2.0).toDouble())
