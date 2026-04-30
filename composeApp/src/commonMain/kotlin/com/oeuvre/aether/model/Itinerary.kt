package com.oeuvre.aether.model

data class ItineraryStop(
    val event: Event,
    val startTime: String,
    val why: String,
)

data class Itinerary(
    val id: String,
    val totalDuration: String,
    val stops: List<ItineraryStop>,
    val routeInstructions: List<String>,
    val generatedAt: String,
)
