package com.oeuvre.aether.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.oeuvre.aether.location.rememberLocationService
import com.oeuvre.aether.ui.explore.ExploreContent
import com.oeuvre.aether.ui.favorites.FavoritesContent
import com.oeuvre.aether.ui.map.MapScreen
import kotlinx.coroutines.launch

private enum class NavTab { Explore, Favorites }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val locationService = rememberLocationService()
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf<NavTab?>(null) }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false,
        )
    )

    // Sync selectedTab with sheet state. 
    // If user swipes down to hide, we should clear the selected tab.
    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Hidden) {
            selectedTab = null
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black,
                tonalElevation = 0.dp,
            ) {
                NavigationBarItem(
                    selected = selectedTab == NavTab.Explore,
                    onClick = {
                        if (selectedTab == NavTab.Explore) {
                            scope.launch { scaffoldState.bottomSheetState.hide() }
                        } else {
                            selectedTab = NavTab.Explore
                            scope.launch { scaffoldState.bottomSheetState.partialExpand() }
                        }
                    },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Explore") },
                    label = { Text("Explore") },
                )
                NavigationBarItem(
                    selected = selectedTab == NavTab.Favorites,
                    onClick = {
                        if (selectedTab == NavTab.Favorites) {
                            scope.launch { scaffoldState.bottomSheetState.hide() }
                        } else {
                            selectedTab = NavTab.Favorites
                            scope.launch { scaffoldState.bottomSheetState.partialExpand() }
                        }
                    },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
                    label = { Text("Favorites") },
                )
            }
        }
    ) { paddingValues ->
        // We wrap BottomSheetScaffold in a Scaffold to keep the NavigationBar 
        // interactive and always visible at the bottom.
        BottomSheetScaffold(
            modifier = Modifier.padding(paddingValues),
            scaffoldState = scaffoldState,
            sheetContent = {
                // Ensure the sheet can expand to fill the available space.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize() 
                ) {
                    when (selectedTab) {
                        NavTab.Explore -> ExploreContent()
                        NavTab.Favorites -> FavoritesContent()
                        null -> Spacer(Modifier.height(1.dp))
                    }
                }
            },
            // sheetPeekHeight defines the "half-expanded" or "peek" state.
            sheetPeekHeight = 320.dp,
            sheetContainerColor = Color.Black,
            sheetContentColor = Color.White,
            sheetTonalElevation = 0.dp,
            sheetDragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) },
        ) {
            MapScreen(
                locationService = locationService,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
