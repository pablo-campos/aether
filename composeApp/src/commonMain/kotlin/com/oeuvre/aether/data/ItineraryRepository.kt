package com.oeuvre.aether.data

import com.oeuvre.aether.model.Itinerary
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ItineraryRepository {
    private val settings: Settings = Settings()
    private val json = Json { ignoreUnknownKeys = true }
    private val KEY_ITINERARIES = "itineraries"

    fun saveItineraries(itineraries: List<Itinerary>) {
        val data = json.encodeToString(itineraries)
        settings[KEY_ITINERARIES] = data
    }

    fun getItineraries(): List<Itinerary> {
        val data = settings.getStringOrNull(KEY_ITINERARIES) ?: return emptyList()
        return try {
            json.decodeFromString(data)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addItinerary(itinerary: Itinerary) {
        val current = getItineraries().toMutableList()
        current.add(itinerary)
        saveItineraries(current)
    }

    fun deleteItinerary(id: String) {
        val current = getItineraries().filter { it.id != id }
        saveItineraries(current)
    }
}
