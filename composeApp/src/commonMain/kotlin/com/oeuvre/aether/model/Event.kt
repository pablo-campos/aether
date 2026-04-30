package com.oeuvre.aether.model

import com.oeuvre.aether.location.LatLng

data class Event(
    val id: String,
    val name: String,
    val description: String,
    val address: String,
    val location: LatLng,
    val category: String,
)
