package com.oeuvre.aether.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.oeuvre.aether.location.rememberLocationService
import com.oeuvre.aether.ui.explore.ExploreContent
import com.oeuvre.aether.ui.favorites.FavoritesContent
import com.oeuvre.aether.ui.map.MapScreen

private enum class NavTab { Explore, Favorites }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val locationService = rememberLocationService()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var selectedTab by remember { mutableStateOf<NavTab?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        MapScreen(
            locationService = locationService,
            modifier = Modifier.fillMaxSize(),
        )

        NavigationBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            containerColor = Color.Black,
            tonalElevation = 0.dp,
        ) {
            NavigationBarItem(
                selected = selectedTab == NavTab.Explore,
                onClick = {
                    selectedTab = if (selectedTab == NavTab.Explore) null else NavTab.Explore
                },
                icon = { Icon(Icons.Default.Search, contentDescription = "Explore") },
                label = { Text("Explore") },
            )
            NavigationBarItem(
                selected = selectedTab == NavTab.Favorites,
                onClick = {
                    selectedTab = if (selectedTab == NavTab.Favorites) null else NavTab.Favorites
                },
                icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
                label = { Text("Favorites") },
            )
        }
    }

    if (selectedTab != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedTab = null },
            sheetState = sheetState,
            containerColor = Color.Black,
            tonalElevation = 0.dp,
        ) {
            when (selectedTab) {
                NavTab.Explore -> ExploreContent()
                NavTab.Favorites -> FavoritesContent()
                null -> Unit
            }
        }
    }
}
