package com.oeuvre.aether.places

import com.oeuvre.aether.location.LatLng
import com.oeuvre.aether.model.Event
import com.oeuvre.aether.platform.placesApiKey
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class PlacesRepository {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
            connectTimeoutMillis = 15000
            socketTimeoutMillis = 15000
        }
    }

    suspend fun fetchNearby(location: LatLng): List<Event> {
        val response = client.post("https://places.googleapis.com/v1/places:searchNearby") {
            contentType(ContentType.Application.Json)
            header("X-Goog-Api-Key", placesApiKey())
            header(
                "X-Goog-FieldMask",
                "places.id,places.displayName,places.formattedAddress,places.location,places.primaryType,places.editorialSummary",
            )
            setBody(
                NearbySearchRequest(
                    includedTypes = listOf("tourist_attraction", "museum", "park"),
                    maxResultCount = 20,
                    locationRestriction = LocationRestriction(
                        circle = Circle(
                            center = Center(
                                latitude = location.latitude,
                                longitude = location.longitude,
                            ),
                            radius = 5000.0,
                        ),
                    ),
                ),
            )
        }

        if (!response.status.isSuccess()) {
            val errorBody = response.bodyAsText()
            throw Exception("Places API Error (${response.status.value}): $errorBody")
        }

        val dto: NearbySearchResponse = response.body()
        return dto.places.orEmpty().map { place ->
            Event(
                id = place.id ?: "",
                name = place.displayName?.text ?: "",
                description = place.editorialSummary?.text ?: place.primaryType ?: "",
                address = place.formattedAddress ?: "",
                location = LatLng(
                    latitude = place.location?.latitude ?: 0.0,
                    longitude = place.location?.longitude ?: 0.0,
                ),
                category = place.primaryType ?: "attraction",
            )
        }
    }
}

@Serializable
private data class NearbySearchRequest(
    val includedTypes: List<String>,
    val maxResultCount: Int,
    val locationRestriction: LocationRestriction,
)

@Serializable
private data class LocationRestriction(val circle: Circle)

@Serializable
private data class Circle(val center: Center, val radius: Double)

@Serializable
private data class Center(val latitude: Double, val longitude: Double)

@Serializable
private data class NearbySearchResponse(val places: List<PlaceDto>? = null)

@Serializable
private data class PlaceDto(
    val id: String? = null,
    val displayName: LocalizedText? = null,
    val formattedAddress: String? = null,
    val location: LocationDto? = null,
    val primaryType: String? = null,
    val editorialSummary: LocalizedText? = null,
)

@Serializable
private data class LocalizedText(val text: String? = null)

@Serializable
private data class LocationDto(val latitude: Double? = null, val longitude: Double? = null)
