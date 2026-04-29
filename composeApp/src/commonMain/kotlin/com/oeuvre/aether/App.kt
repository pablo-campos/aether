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
    background           = Color(0xFF000000),
    onBackground         = Color(0xFFFFFFFF),
    surface              = Color(0xFF000000),
    onSurface            = Color(0xFFFFFFFF),
    surfaceVariant       = Color(0xFF000000),
    onSurfaceVariant     = Color(0xFFCACACA),
    surfaceDim           = Color(0xFF000000),
    surfaceBright        = Color(0xFF000000),
    surfaceContainerLowest  = Color(0xFF000000),
    surfaceContainerLow     = Color(0xFF000000),
    surfaceContainer        = Color(0xFF000000),
    surfaceContainerHigh    = Color(0xFF000000),
    surfaceContainerHighest = Color(0xFF000000),
    primary              = Color(0xFF7DB9FD),
    onPrimary            = Color(0xFF000000),
    primaryContainer     = Color(0xFF000000),
    onPrimaryContainer   = Color(0xFF7DB9FD),
    secondary            = Color(0xFF7DB9FD),
    onSecondary          = Color(0xFF000000),
    secondaryContainer   = Color(0xFF000000),
    onSecondaryContainer = Color(0xFF7DB9FD),
    tertiaryContainer    = Color(0xFF000000),
    onTertiaryContainer   = Color(0xFF7DB9FD),
    surfaceTint          = Color(0xFF000000),
    outline              = Color(0xFF444444),
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
            ) {
                scope.launch {
                    state = try {
                        controller.providePermission(Permission.LOCATION)
                        resolveState(controller, locationService)
                    } catch (_: Exception) {
                        AppState.NeedPermission
                    }
                }
            }

            AppState.GpsDisabled -> LocationPermissionDialog(
                title = "Enable Location Services",
                message = "GPS is currently off. Please enable Location Services in your device settings so Aether can find you on the map.",
                actionLabel = "Check Again",
            ) {
                scope.launch {
                    state = resolveState(controller, locationService)
                }
            }

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
