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
import platform.UIKit.UIUserInterfaceStyle
import kotlin.math.abs
import kotlin.math.pow

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun NativeMapView(modifier: Modifier, cameraState: MapCameraState) {
    val mapView = remember {
        MKMapView().also {
            it.overrideUserInterfaceStyle = UIUserInterfaceStyle.UIUserInterfaceStyleDark
            it.showsUserLocation = true
        }
    }

    LaunchedEffect(cameraState) {
        val coordinate = CLLocationCoordinate2DMake(
            cameraState.target.latitude,
            cameraState.target.longitude,
        )
        val meters = zoomToMeters(cameraState.zoom)
        
        // Ensure the region is valid for MapKit to avoid crashes.
        // MapKit throws an exception if the span is too large for the given center.
        val maxSafeMeters = (180.0 - 2.0 * abs(cameraState.target.latitude)) * 111_000.0
        val safeMeters = meters.coerceAtMost(maxSafeMeters.coerceAtLeast(100.0))
        
        val region = MKCoordinateRegionMakeWithDistance(coordinate, safeMeters, safeMeters)
        mapView.setRegion(region, animated = true)
    }

    UIKitView(factory = { mapView }, modifier = modifier)
}

private fun zoomToMeters(zoom: Float): Double =
    20_000_000.0 / 2.0.pow((zoom - 2.0).toDouble())
