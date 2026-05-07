package com.oeuvre.aether.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import com.oeuvre.aether.model.Event
import com.oeuvre.aether.model.Itinerary
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.plus
import kotlinx.cinterop.reinterpret
import platform.CoreLocation.CLLocationCoordinate2D
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.CoreGraphics.CGPointMake
import platform.CoreGraphics.CGRectMake
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKAnnotationView
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKMapView
import platform.MapKit.MKMapViewDelegateProtocol
import platform.MapKit.MKOverlayLevelAboveRoads
import platform.MapKit.MKOverlayProtocol
import platform.MapKit.MKOverlayRenderer
import platform.MapKit.MKPointAnnotation
import platform.MapKit.MKPolyline
import platform.MapKit.MKPolylineRenderer
import platform.MapKit.MKUserLocation
import platform.MapKit.addOverlay
import platform.MapKit.removeOverlay
import platform.UIKit.NSTextAlignmentCenter
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UILabel
import platform.UIKit.UIUserInterfaceStyle
import platform.UIKit.UIView
import platform.darwin.NSObject
import kotlin.math.abs
import kotlin.math.pow

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun NativeMapView(
    modifier: Modifier,
    cameraState: MapCameraState,
    itinerary: Itinerary?,
    nearbyEvents: List<Event>,
) {
    val delegate = remember { MapViewDelegate() }
    val mapView = remember {
        MKMapView().also {
            it.overrideUserInterfaceStyle = UIUserInterfaceStyle.UIUserInterfaceStyleDark
            it.showsUserLocation = true
            it.delegate = delegate
        }
    }
    var currentPolyline by remember { mutableStateOf<MKPolyline?>(null) }

    // Follow user location only when no itinerary is active.
    LaunchedEffect(cameraState) {
        if (itinerary == null) {
            val coordinate = CLLocationCoordinate2DMake(
                cameraState.target.latitude,
                cameraState.target.longitude,
            )
            val meters = zoomToMeters(cameraState.zoom)
            val maxSafeMeters = (180.0 - (2.0 * abs(cameraState.target.latitude))) * 111_000.0
            val safeMeters = meters.coerceAtMost(maxSafeMeters.coerceAtLeast(100.0))
            mapView.setRegion(MKCoordinateRegionMakeWithDistance(coordinate, safeMeters, safeMeters), animated = true)
        }
    }

    // Replace annotations and overlay whenever the itinerary or nearbyEvents changes.
    LaunchedEffect(itinerary, nearbyEvents) {
        mapView.removeAnnotations(mapView.annotations)
        currentPolyline?.let { mapView.removeOverlay(it) }
        currentPolyline = null

        val stops = itinerary?.stops.orEmpty()
        if (stops.isNotEmpty()) {
            val annotations = stops.mapIndexed { index, stop ->
                NumberedAnnotation(index + 1).also { ann ->
                    ann.setCoordinate(CLLocationCoordinate2DMake(
                        stop.event.location.latitude,
                        stop.event.location.longitude,
                    ))
                    ann.setTitle(stop.event.name)
                    ann.setSubtitle(stop.startTime)
                }
            }
            mapView.addAnnotations(annotations)

            if (stops.size >= 2) {
                // CLLocationCoordinate2D = {Double latitude; Double longitude} = 16 bytes.
                // Kotlin/Native 2.3+ lacks CPointer.plus for struct types, so we reinterpret
                // as ByteVar (which supports plus) and use CValue.place to write each struct.
                memScoped {
                    val coordsPtr = allocArray<CLLocationCoordinate2D>(stops.size)
                    val bytesPtr = coordsPtr.reinterpret<ByteVar>()
                    stops.forEachIndexed { i, stop ->
                        CLLocationCoordinate2DMake(
                            stop.event.location.latitude,
                            stop.event.location.longitude,
                        ).place(
                            (bytesPtr + (i.toLong() * 16L))!!.reinterpret<CLLocationCoordinate2D>()
                        )
                    }
                    val polyline = MKPolyline.polylineWithCoordinates(coordsPtr, stops.size.toULong())
                    mapView.addOverlay(polyline, MKOverlayLevelAboveRoads)
                    currentPolyline = polyline
                }
            }

            // Zoom to fit all markers.
            mapView.showAnnotations(annotations, animated = true)
        } else if (nearbyEvents.isNotEmpty()) {
            val annotations = nearbyEvents.map { event ->
                MKPointAnnotation().also { ann ->
                    ann.setCoordinate(CLLocationCoordinate2DMake(
                        event.location.latitude,
                        event.location.longitude,
                    ))
                    ann.setTitle(event.name)
                }
            }
            mapView.addAnnotations(annotations)
            mapView.showAnnotations(annotations, animated = true)
        }
    }

    UIKitView(factory = { mapView }, modifier = modifier)
}

@OptIn(ExperimentalForeignApi::class)
private class NumberedAnnotation(val number: Int) : MKPointAnnotation()

private fun zoomToMeters(zoom: Float): Double =
    20_000_000.0 / 2.0.pow(zoom - 2.0)

private class MapViewDelegate : NSObject(), MKMapViewDelegateProtocol {
    @OptIn(ExperimentalForeignApi::class)
    override fun mapView(mapView: MKMapView, viewForAnnotation: MKAnnotationProtocol): MKAnnotationView? {
        if (viewForAnnotation is MKUserLocation) return null

        val identifier = "EventMarker"
        var annotationView = mapView.dequeueReusableAnnotationViewWithIdentifier(identifier)

        if (annotationView == null) {
            annotationView = MKAnnotationView(annotation = viewForAnnotation, reuseIdentifier = identifier)
            annotationView.canShowCallout = true
        } else {
            annotationView.annotation = viewForAnnotation
        }

        // Remove old subviews to avoid doubling up on reuse
        annotationView.subviews.filterIsInstance<UIView>().forEach { 
            it.removeFromSuperview()
        }

        val isNumbered = viewForAnnotation is NumberedAnnotation
        val size = if (isNumbered) 32.0 else 24.0
        val lineWeight = 2.0
        val lineHeight = 6.0
        val totalHeight = size + lineHeight

        annotationView.setFrame(CGRectMake(0.0, 0.0, size, totalHeight))
        annotationView.centerOffset = CGPointMake(0.0, -totalHeight / 2.0)

        val blueColor = UIColor.colorWithRed(
            red = 0x19 / 255.0,
            green = 0x76 / 255.0,
            blue = 0xD2 / 255.0,
            alpha = 1.0,
        )

        val circle = UIView(frame = CGRectMake(0.0, 0.0, size, size)).apply {
            backgroundColor = blueColor
            layer.cornerRadius = size / 2.0
            layer.borderWidth = 1.0
            layer.borderColor = UIColor.whiteColor.CGColor
        }

        val line = UIView(frame = CGRectMake((size - lineWeight) / 2.0, size, lineWeight, lineHeight)).apply {
            backgroundColor = UIColor.whiteColor
        }

        annotationView.addSubview(circle)
        annotationView.addSubview(line)

        if (viewForAnnotation is NumberedAnnotation) {
            val label = UILabel(frame = circle.bounds).apply {
                text = viewForAnnotation.number.toString()
                textColor = UIColor.whiteColor
                textAlignment = NSTextAlignmentCenter
                font = UIFont.boldSystemFontOfSize(14.0)
            }
            circle.addSubview(label)
        }

        return annotationView
    }

    override fun mapView(mapView: MKMapView, rendererForOverlay: MKOverlayProtocol): MKOverlayRenderer {
        val polyline = rendererForOverlay as? MKPolyline
        if (polyline != null) {
            return MKPolylineRenderer(polyline = polyline).also { renderer ->
                renderer.strokeColor = UIColor.colorWithRed(
                    red = 0x7D / 255.0,
                    green = 0xB9 / 255.0,
                    blue = 0xFD / 255.0,
                    alpha = 1.0,
                )
                renderer.lineWidth = 4.0
            }
        }
        return MKOverlayRenderer(overlay = rendererForOverlay)
    }
}
