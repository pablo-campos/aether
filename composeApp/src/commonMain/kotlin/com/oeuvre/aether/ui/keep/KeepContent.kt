package com.oeuvre.aether.ui.keep

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oeuvre.aether.model.Itinerary
import com.oeuvre.aether.ui.MainViewModel
import com.oeuvre.aether.util.formatCategory
import kotlinx.datetime.LocalDateTime

@Composable
fun KeepContent(
    viewModel: MainViewModel,
    onItinerarySelected: (Itinerary) -> Unit = {}
) {
    val itineraries by viewModel.savedItineraries.collectAsState()
    var selectedItineraryId by remember { mutableStateOf<String?>(null) }

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
                    if (selectedItineraryId == itinerary.id) {
                        ItineraryDetailCard(
                            itinerary = itinerary,
                            onBack = { selectedItineraryId = null },
                        )
                    } else {
                        ItineraryListItem(
                            itinerary = itinerary,
                            onClick = { selectedItineraryId = itinerary.id },
                            onMapClick = { onItinerarySelected(itinerary) },
                            onDelete = { viewModel.deleteItinerary(itinerary.id) }
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun ItineraryListItem(
    itinerary: Itinerary,
    onClick: () -> Unit,
    onMapClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val date = try {
        val dt = LocalDateTime.parse(itinerary.generatedAt)
        "${dt.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${dt.dayOfMonth}, ${dt.year}"
    } catch (_: Exception) {
        itinerary.generatedAt
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = itinerary.city,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Duration: ${itinerary.totalDuration}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
            Row {
                IconButton(onClick = onMapClick) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = "Show on Map",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ItineraryDetailCard(
    itinerary: Itinerary,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onBack),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = itinerary.city,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "Duration: ${itinerary.totalDuration}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
                Text(
                    text = "${itinerary.stops.size} stop${if (itinerary.stops.size == 1) "" else "s"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            
            if (itinerary.stops.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                itinerary.stops.forEach { stop ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(
                            text = "• ${stop.startTime}: ${stop.event.name}",
                            style = MaterialTheme.typography.labelMedium,
                        )
                        Text(
                            text = stop.event.category.formatCategory(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                        Text(
                            text = stop.why,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(start = 12.dp, top = 2.dp)
                        )
                    }
                }
            }

            if (itinerary.routeInstructions.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
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
                        modifier = Modifier.padding(vertical = 2.dp)
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
