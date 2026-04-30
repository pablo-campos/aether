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

class ItineraryManager(private val apiKey: String) {

    // The model is created lazily so that a missing API key surfaces as a clear error
    // at call time rather than silently during app startup.
    // The underlying APIController sends the key via the "x-goog-api-key" HTTP header
    // on every request (set by the Ktor client inside the library).
    private val model: GenerativeModel by lazy {
        check(apiKey.isNotBlank()) {
            "GEMINI_API_KEY is missing. " +
                "Android: add GEMINI_API_KEY to local.properties. " +
                "iOS: add GEMINI_API_KEY to iosApp/Configuration/Config.xcconfig."
        }
        GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = apiKey,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
            },
        )
    }

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
            """{"id":"${e.id}","name":"${e.name}","category":"${e.category}","address":"${e.address}","description":"${e.description}","startTime":"${e.startTime ?: "N/A"}"}"""
        }
        return """
I am sending a list of events from a mobile app. You are a local travel expert.

Current time: $currentTime
Current location: lat=${currentLocation.latitude}, lng=${currentLocation.longitude}

Events:
[$eventsJson]

1. Filter out events that have already passed based on the provided current time.
2. Create a logical flow (e.g., Brunch -> Museum -> Live Music).
3. Format strictly as JSON.
4. Include a field routeInstructions which is a list of strings explaining how to get from one event to the next.

Return ONLY a JSON object with this exact schema (no markdown, no extra text):
{
  "itineraryId": "string",
  "totalDuration": "string",
  "activities": [
    {
      "eventId": "string",
      "startTime": "HH:mm",
      "why": "string"
    }
  ],
  "routeInstructions": ["string"]
}
        """.trimIndent()
    }

    private fun parseItinerary(text: String, events: List<Event>, currentTime: LocalDateTime): Itinerary {
        val dto = json.decodeFromString<ItineraryDto>(text)
        val eventById = events.associateBy { it.id }
        val stops = dto.activities.mapNotNull { activity ->
            val event = eventById[activity.eventId] ?: return@mapNotNull null
            ItineraryStop(event = event, startTime = activity.startTime, why = activity.why)
        }
        return Itinerary(
            id = dto.itineraryId,
            totalDuration = dto.totalDuration,
            stops = stops,
            routeInstructions = dto.routeInstructions,
            generatedAt = currentTime.toString()
        )
    }
}

@Serializable
private data class ItineraryDto(
    val itineraryId: String,
    val totalDuration: String,
    val activities: List<ActivityDto>,
    val routeInstructions: List<String> = emptyList()
)

@Serializable
private data class ActivityDto(
    val eventId: String,
    val startTime: String,
    val why: String
)
