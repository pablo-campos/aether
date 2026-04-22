package com.oeuvre.aether

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.oeuvre.aether.location.LocationService
import com.oeuvre.aether.location.rememberLocationService
import com.oeuvre.aether.permission.LocationPermissionDialog
import com.oeuvre.aether.ui.MainScreen
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import dev.icerock.moko.permissions.location.LOCATION
import kotlinx.coroutines.launch

private sealed interface AppState {
    data object Checking : AppState
    data object NeedPermission : AppState
    data object GpsDisabled : AppState
    data object Ready : AppState
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        val factory = rememberPermissionsControllerFactory()
        val controller = remember(factory) { factory.createPermissionsController() }
        BindEffect(controller)

        val locationService = rememberLocationService()
        val scope = rememberCoroutineScope()
        var state by remember { mutableStateOf<AppState>(AppState.Checking) }

        LaunchedEffect(Unit) {
            state = resolveState(controller, locationService)
        }

        when (state) {
            AppState.Checking -> Unit

            AppState.NeedPermission -> LocationPermissionDialog(
                title = "Location Access Required",
                message = "Aether needs access to your precise location to show your position on the map and surface nearby destinations.",
                actionLabel = "Allow",
                onAction = {
                    scope.launch {
                        try {
                            controller.providePermission(Permission.LOCATION)
                            state = resolveState(controller, locationService)
                        } catch (_: Exception) {
                            state = AppState.NeedPermission
                        }
                    }
                },
            )

            AppState.GpsDisabled -> LocationPermissionDialog(
                title = "Enable Location Services",
                message = "GPS is currently off. Please enable Location Services in your device settings so Aether can find you on the map.",
                actionLabel = "Check Again",
                onAction = {
                    scope.launch {
                        state = resolveState(controller, locationService)
                    }
                },
            )

            AppState.Ready -> MainScreen()
        }
    }
}

private suspend fun resolveState(
    controller: PermissionsController,
    locationService: LocationService,
): AppState {
    if (!controller.isPermissionGranted(Permission.LOCATION)) return AppState.NeedPermission
    if (!locationService.isLocationEnabled()) return AppState.GpsDisabled
    return AppState.Ready
}
