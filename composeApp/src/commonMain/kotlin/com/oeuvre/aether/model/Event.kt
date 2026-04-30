package com.oeuvre.aether.model

import com.oeuvre.aether.location.LatLng

data class Event(
    val id: String,
    val name: String,
    val description: String,
    val address: String,
    val location: LatLng,
    val category: String,
    val startTime: String? = null, // e.g. "2023-10-27T10:00:00" or just "10:00"
)
