package com.oeuvre.aether.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oeuvre.aether.ai.ItineraryManager
import com.oeuvre.aether.location.LatLng
import com.oeuvre.aether.model.Event
import com.oeuvre.aether.model.Itinerary
import com.oeuvre.aether.model.SampleEvents
import com.oeuvre.aether.platform.geminiApiKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

class MainViewModel : ViewModel() {

    private val manager = ItineraryManager(geminiApiKey())

    val events: StateFlow<List<Event>> = MutableStateFlow(SampleEvents.list)

    private val _savedItineraries = MutableStateFlow<List<Itinerary>>(emptyList())
    val savedItineraries: StateFlow<List<Itinerary>> = _savedItineraries.asStateFlow()

    sealed interface GenState {
        data object Idle : GenState
        data object Loading : GenState
        data class Error(val msg: String) : GenState
    }

    private val _genState = MutableStateFlow<GenState>(GenState.Idle)
    val genState: StateFlow<GenState> = _genState.asStateFlow()

    fun generateItinerary(currentLocation: LatLng, currentTime: LocalDateTime) {
        viewModelScope.launch {
            _genState.value = GenState.Loading
            try {
                val itinerary = manager.generatePlan(events.value, currentLocation, currentTime)
                _savedItineraries.value = _savedItineraries.value + itinerary
                _genState.value = GenState.Idle
            } catch (e: Exception) {
                _genState.value = GenState.Error(e.message ?: "Generation failed")
            }
        }
    }

    fun clearError() {
        _genState.value = GenState.Idle
    }
}
