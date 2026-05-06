package com.oeuvre.aether.ui.seek

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oeuvre.aether.location.LocationService
import com.oeuvre.aether.ui.MainViewModel
import com.oeuvre.aether.util.nowLocalDateTime
import kotlinx.coroutines.launch

@Composable
fun SeekContent(viewModel: MainViewModel, locationService: LocationService) {
    val seekUiState by viewModel.seekUiState.collectAsState()
    val genState by viewModel.genState.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val currentState = seekUiState
        val shouldLoad = when (currentState) {
            is MainViewModel.SeekUiState.Success -> currentState.events.isEmpty()
            else -> true
        }

        if (shouldLoad) {
            val location = locationService.getCurrentLocation()
            if (location == null) {
                viewModel.reportSeekError("Location unavailable")
                return@LaunchedEffect
            }
            viewModel.loadNearbyPlaces(location)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Seek",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f),
            )
            IconButton(
                onClick = {
                    scope.launch {
                        val location = locationService.getCurrentLocation()
                        if (location == null) {
                            viewModel.reportSeekError("Location unavailable")
                        } else {
                            viewModel.loadNearbyPlaces(location)
                        }
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Refresh",
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        when (val state = seekUiState) {
            is MainViewModel.SeekUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .height(200.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is MainViewModel.SeekUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = state.msg,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                    Button(onClick = {
                        scope.launch {
                            val location = locationService.getCurrentLocation()
                            if (location == null) {
                                viewModel.reportSeekError("Location unavailable")
                            } else {
                                viewModel.loadNearbyPlaces(location)
                            }
                        }
                    }) {
                        Text("Retry")
                    }
                }
            }

            is MainViewModel.SeekUiState.Success -> {
                if (state.events.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                            .height(200.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("No places found nearby.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        contentPadding = PaddingValues(bottom = 8.dp),
                    ) {
                        items(state.events) { event ->
                            EventCard(event = event)
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                when (val gs = genState) {
                    is MainViewModel.GenState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is MainViewModel.GenState.Error -> {
                        Text(
                            text = gs.msg,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                        GenerateButton {
                            scope.launch { triggerGenerate(viewModel, locationService) }
                        }
                    }

                    is MainViewModel.GenState.Idle -> {
                        GenerateButton {
                            scope.launch { triggerGenerate(viewModel, locationService) }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun GenerateButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Magic Plan")
    }
}

private suspend fun triggerGenerate(
    viewModel: MainViewModel,
    locationService: LocationService,
) {
    val location = locationService.getCurrentLocation() ?: return
    val time = nowLocalDateTime()
    viewModel.generateItinerary(location, time)
}
