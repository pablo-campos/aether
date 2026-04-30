package com.oeuvre.aether.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oeuvre.aether.location.rememberLocationService
import com.oeuvre.aether.ui.keep.KeepContent
import com.oeuvre.aether.ui.map.MapScreen
import com.oeuvre.aether.ui.seek.SeekContent
import kotlinx.coroutines.launch

private enum class NavTab { Seek, Keep }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val locationService = rememberLocationService()
    val viewModel = viewModel { MainViewModel() }
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf<NavTab?>(null) }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false,
        ),
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
                    selected = selectedTab == NavTab.Seek,
                    onClick = {
                        if (selectedTab == NavTab.Seek) {
                            scope.launch { scaffoldState.bottomSheetState.hide() }
                        } else {
                            selectedTab = NavTab.Seek
                            scope.launch { scaffoldState.bottomSheetState.partialExpand() }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == NavTab.Seek) AetherIcons.SeekFilled else AetherIcons.SeekOutlined,
                            contentDescription = "Seek",
                        )
                    },
                    label = {
                        val alpha by animateFloatAsState(
                            targetValue = if (selectedTab == NavTab.Seek) 1f else 0f,
                            animationSpec = tween(durationMillis = 100),
                        )
                        Text(
                            text = "Seek",
                            modifier = Modifier.graphicsLayer { this.alpha = alpha },
                        )
                    },
                    alwaysShowLabel = false,
                )
                NavigationBarItem(
                    selected = selectedTab == NavTab.Keep,
                    onClick = {
                        if (selectedTab == NavTab.Keep) {
                            scope.launch { scaffoldState.bottomSheetState.hide() }
                        } else {
                            selectedTab = NavTab.Keep
                            scope.launch { scaffoldState.bottomSheetState.partialExpand() }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == NavTab.Keep) AetherIcons.KeepFilled else AetherIcons.KeepOutlined,
                            contentDescription = "Keep",
                        )
                    },
                    label = {
                        val alpha by animateFloatAsState(
                            targetValue = if (selectedTab == NavTab.Keep) 1f else 0f,
                            animationSpec = tween(durationMillis = 100),
                        )
                        Text(
                            text = "Keep",
                            modifier = Modifier.graphicsLayer { this.alpha = alpha },
                        )
                    },
                    alwaysShowLabel = false,
                )
            }
        },
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
                        .fillMaxSize(),
                ) {
                    when (selectedTab) {
                        NavTab.Seek -> SeekContent(viewModel = viewModel, locationService = locationService)
                        NavTab.Keep -> KeepContent(viewModel = viewModel)
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
