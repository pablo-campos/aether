package com.oeuvre.aether.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oeuvre.aether.ai.ItineraryManager
import com.oeuvre.aether.data.ItineraryRepository
import com.oeuvre.aether.location.LatLng
import com.oeuvre.aether.model.Event
import com.oeuvre.aether.model.Itinerary
import com.oeuvre.aether.places.PlacesRepository
import com.oeuvre.aether.platform.geminiApiKey
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

class MainViewModel : ViewModel() {

    private val itineraryManager = ItineraryManager(geminiApiKey())
    private val placesRepository = PlacesRepository()
    private val itineraryRepository = ItineraryRepository()

    sealed interface SeekUiState {
        data object Loading : SeekUiState
        data class Success(val events: List<Event>) : SeekUiState
        data class Error(val msg: String) : SeekUiState
    }

    private val _seekUiState = MutableStateFlow<SeekUiState>(SeekUiState.Loading)
    val seekUiState: StateFlow<SeekUiState> = _seekUiState.asStateFlow()

    private val _savedItineraries = MutableStateFlow<List<Itinerary>>(itineraryRepository.getItineraries())
    val savedItineraries: StateFlow<List<Itinerary>> = _savedItineraries.asStateFlow()

    private val _selectedItinerary = MutableStateFlow<Itinerary?>(itineraryRepository.getItineraries().lastOrNull())
    val selectedItinerary: StateFlow<Itinerary?> = _selectedItinerary.asStateFlow()

    sealed interface GenState {
        data object Idle : GenState
        data object Loading : GenState
        data class Error(val msg: String) : GenState
    }

    private val _genState = MutableStateFlow<GenState>(GenState.Idle)
    val genState: StateFlow<GenState> = _genState.asStateFlow()

    private val _generationSuccess = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val generationSuccess: SharedFlow<Unit> = _generationSuccess.asSharedFlow()

    fun loadNearbyPlaces(location: LatLng) {
        viewModelScope.launch {
            _selectedItinerary.value = null
            _seekUiState.value = SeekUiState.Loading
            try {
                val events = placesRepository.fetchNearby(location)
                _seekUiState.value = SeekUiState.Success(events)
            } catch (e: Exception) {
                _seekUiState.value = SeekUiState.Error(e.message ?: "Failed to load places")
            }
        }
    }

    fun generateItinerary(currentLocation: LatLng, currentTime: LocalDateTime) {
        val events = (_seekUiState.value as? SeekUiState.Success)?.events ?: return
        viewModelScope.launch {
            _genState.value = GenState.Loading
            try {
                val itinerary = itineraryManager.generatePlan(events, currentLocation, currentTime)
                itineraryRepository.addItinerary(itinerary)
                _savedItineraries.value = itineraryRepository.getItineraries()
                _selectedItinerary.value = itinerary
                _genState.value = GenState.Idle
                _generationSuccess.tryEmit(Unit)
            } catch (e: Exception) {
                _genState.value = GenState.Error(e.message ?: "Generation failed")
            }
        }
    }

    fun selectItinerary(itinerary: Itinerary?) {
        _selectedItinerary.value = itinerary
    }

    fun deleteItinerary(id: String) {
        itineraryRepository.deleteItinerary(id)
        val updated = itineraryRepository.getItineraries()
        _savedItineraries.value = updated
        if (_selectedItinerary.value?.id == id) {
            _selectedItinerary.value = updated.lastOrNull()
        }
    }

    fun clearError() {
        _genState.value = GenState.Idle
    }

    fun reportSeekError(msg: String) {
        _seekUiState.value = SeekUiState.Error(msg)
    }
}
