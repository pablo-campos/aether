package com.oeuvre.aether.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng as GoogleLatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.oeuvre.aether.R
import com.oeuvre.aether.model.Event
import com.oeuvre.aether.model.Itinerary
import androidx.compose.material3.Text

@Composable
actual fun NativeMapView(
    modifier: Modifier,
    cameraState: MapCameraState,
    itinerary: Itinerary?,
    nearbyEvents: List<Event>,
) {
    val context = LocalContext.current
    val mapStyleOptions = remember {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.map_dark_style)
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            GoogleLatLng(cameraState.target.latitude, cameraState.target.longitude),
            cameraState.zoom,
        )
    }

    // Follow user location only when no itinerary is active.
    LaunchedEffect(cameraState) {
        if (itinerary == null) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(
                    GoogleLatLng(cameraState.target.latitude, cameraState.target.longitude),
                    cameraState.zoom,
                ),
                durationMs = 800,
            )
        }
    }

    // Fit the camera to the itinerary bounds whenever the itinerary changes.
    LaunchedEffect(itinerary) {
        val stops = itinerary?.stops.orEmpty()
        if (stops.isNotEmpty()) {
            val builder = LatLngBounds.Builder()
            stops.forEach { stop ->
                builder.include(GoogleLatLng(stop.event.location.latitude, stop.event.location.longitude))
            }
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngBounds(builder.build(), 160),
                durationMs = 900,
            )
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false,
        ),
        properties = MapProperties(
            mapStyleOptions = mapStyleOptions,
            isMyLocationEnabled = true,
        ),
    ) {
        val stops = itinerary?.stops.orEmpty()

        if (stops.isNotEmpty()) {
            stops.forEachIndexed { index, stop ->
                key(stop.event.id) {
                    MarkerComposable(
                        state = rememberMarkerState(
                            position = GoogleLatLng(stop.event.location.latitude, stop.event.location.longitude),
                        ),
                        anchor = Offset(0.5f, 1f),
                        title = stop.event.name,
                        snippet = stop.startTime,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFF1976D2), shape = CircleShape)
                                    .border(1.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = (index + 1).toString(),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(6.dp)
                                    .background(Color.White)
                            )
                        }
                    }
                }
            }

            if (stops.size >= 2) {
                key(itinerary?.id) {
                    Polyline(
                        points = stops.map { GoogleLatLng(it.event.location.latitude, it.event.location.longitude) },
                        color = Color(0xFF7DB9FD),
                        width = 8f,
                    )
                }
            }
        } else {
            nearbyEvents.forEach { event ->
                key(event.id) {
                    MarkerComposable(
                        state = rememberMarkerState(
                            position = GoogleLatLng(event.location.latitude, event.location.longitude),
                        ),
                        anchor = Offset(0.5f, 1f),
                        title = event.name,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(0xFF1976D2), shape = CircleShape)
                                    .border(1.dp, Color.White, CircleShape),
                            )
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(6.dp)
                                    .background(Color.White)
                            )
                        }
                    }
                }
            }
        }
    }
}
