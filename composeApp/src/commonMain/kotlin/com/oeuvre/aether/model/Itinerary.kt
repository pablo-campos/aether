package com.oeuvre.aether.model

import kotlinx.serialization.Serializable

@Serializable
data class ItineraryStop(
    val event: Event,
    val startTime: String,
    val why: String,
)

@Serializable
data class Itinerary(
    val id: String,
    val city: String,
    val totalDuration: String,
    val stops: List<ItineraryStop>,
    val routeInstructions: List<String>,
    val generatedAt: String,
)
