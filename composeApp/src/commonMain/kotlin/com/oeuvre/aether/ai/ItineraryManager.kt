package com.oeuvre.aether.ai

import com.oeuvre.aether.location.LatLng
import com.oeuvre.aether.model.Event
import com.oeuvre.aether.model.Itinerary
import com.oeuvre.aether.model.ItineraryStop
import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import dev.shreyaspatil.ai.client.generativeai.type.generationConfig
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class ItineraryManager(apiKey: String) {

    private val model = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = apiKey,
        generationConfig = generationConfig {
            responseMimeType = "application/json"
        },
    )

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun generatePlan(
        events: List<Event>,
        currentLocation: LatLng,
        currentTime: LocalDateTime,
    ): Itinerary {
        val prompt = buildPrompt(events, currentLocation, currentTime)
        val response = model.generateContent(prompt)
        val text = response.text ?: error("Empty response from Gemini")
        return parseItinerary(text, events, currentTime)
    }

    private fun buildPrompt(
        events: List<Event>,
        currentLocation: LatLng,
        currentTime: LocalDateTime,
    ): String {
        val eventsJson = events.joinToString(",\n") { e ->
            """{"id":"${e.id}","name":"${e.name}","category":"${e.category}","address":"${e.address}","description":"${e.description}"}"""
        }
        return """
You are a travel planner. Create a personalized day itinerary from the events below.

Current time: $currentTime
Current location: lat=${currentLocation.latitude}, lng=${currentLocation.longitude}

Events:
[$eventsJson]

Return ONLY a JSON object with this exact schema (no markdown, no extra text):
{
  "title": "string",
  "stops": [
    {
      "eventId": "string",
      "suggestedTime": "HH:mm",
      "notes": "string"
    }
  ]
}
        """.trimIndent()
    }

    private fun parseItinerary(text: String, events: List<Event>, currentTime: LocalDateTime): Itinerary {
        val dto = json.decodeFromString<ItineraryDto>(text)
        val eventById = events.associateBy { it.id }
        val stops = dto.stops.mapNotNull { stop ->
            val event = eventById[stop.eventId] ?: return@mapNotNull null
            ItineraryStop(event = event, suggestedTime = stop.suggestedTime, notes = stop.notes)
        }
        val generatedAt = currentTime.toString()
        val id = generatedAt.filter { it.isLetterOrDigit() }
        return Itinerary(id = id, title = dto.title, stops = stops, generatedAt = generatedAt)
    }
}

@Serializable
private data class ItineraryDto(val title: String, val stops: List<StopDto>)

@Serializable
private data class StopDto(val eventId: String, val suggestedTime: String, val notes: String)
