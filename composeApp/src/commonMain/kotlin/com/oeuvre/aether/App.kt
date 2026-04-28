package com.oeuvre.aether

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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

private val AetherDarkColors = darkColorScheme(
    background       = Color(0xFF000000),
    onBackground     = Color(0xFFFFFFFF),
    surface          = Color(0xFF0A0A0A),
    onSurface        = Color(0xFFFFFFFF),
    surfaceVariant   = Color(0xFF1C1C1C),
    onSurfaceVariant = Color(0xFFCACACA),
    primary          = Color(0xFF4FC3F7),
    onPrimary        = Color(0xFF000000),
    secondary        = Color(0xFF80CBC4),
    onSecondary      = Color(0xFF000000),
    outline          = Color(0xFF444444),
)

private sealed interface AppState {
    data object Checking : AppState
    data object NeedPermission : AppState
    data object GpsDisabled : AppState
    data object Ready : AppState
}

@Composable
@Preview
fun App() {
    MaterialTheme(colorScheme = AetherDarkColors) {
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
