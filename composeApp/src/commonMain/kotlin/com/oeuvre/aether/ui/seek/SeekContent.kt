package com.oeuvre.aether.ui.seek

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oeuvre.aether.location.LocationService
import com.oeuvre.aether.ui.MainViewModel
import kotlinx.coroutines.launch
import com.oeuvre.aether.util.nowLocalDateTime

@Composable
fun SeekContent(viewModel: MainViewModel, locationService: LocationService) {
    val events by viewModel.events.collectAsState()
    val genState by viewModel.genState.collectAsState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Text("Seek", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false),
            contentPadding = PaddingValues(bottom = 8.dp),
        ) {
            items(events) { event ->
                EventCard(event = event)
                Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        when (val state = genState) {
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
                    text = state.msg,
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

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun GenerateButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Generate Itinerary")
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
