package com.oeuvre.aether.model

data class ItineraryStop(
    val event: Event,
    val suggestedTime: String,
    val notes: String,
)

data class Itinerary(
    val id: String,
    val title: String,
    val stops: List<ItineraryStop>,
    val generatedAt: String,
)
