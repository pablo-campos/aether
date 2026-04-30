package com.oeuvre.aether.ui.keep

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oeuvre.aether.model.Itinerary
import com.oeuvre.aether.ui.MainViewModel

@Composable
fun KeepContent(viewModel: MainViewModel) {
    val itineraries by viewModel.savedItineraries.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Text("Keep", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        if (itineraries.isEmpty()) {
            Text(
                text = "No saved itineraries yet",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 8.dp),
            ) {
                items(itineraries) { itinerary ->
                    ItineraryCard(itinerary = itinerary)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun ItineraryCard(itinerary: Itinerary, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Itinerary: ${itinerary.id}",
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Duration: ${itinerary.totalDuration}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${itinerary.stops.size} stop${if (itinerary.stops.size == 1) "" else "s"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
            )
            
            if (itinerary.stops.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                itinerary.stops.forEach { stop ->
                    Text(
                        text = "• ${stop.startTime}: ${stop.event.name}",
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }

            if (itinerary.routeInstructions.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Routes:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                )
                itinerary.routeInstructions.forEach { instruction ->
                    Text(
                        text = "→ $instruction",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = "Generated at: ${itinerary.generatedAt}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
            )
        }
    }
}
