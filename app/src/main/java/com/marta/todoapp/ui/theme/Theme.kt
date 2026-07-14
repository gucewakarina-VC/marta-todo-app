package com.marta.todoapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.marta.todoapp.data.preferences.ThemeMode

private val LightColors = lightColorScheme(
    primary = Color(0xFFFF6B9D),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD6E5),
    onPrimaryContainer = Color(0xFF5C1A35),
    secondary = Color(0xFF6BCBFF),
    onSecondary = Color(0xFF003549),
    secondaryContainer = Color(0xFFD0EEFF),
    onSecondaryContainer = Color(0xFF003549),
    tertiary = Color(0xFFFFD93D),
    onTertiary = Color(0xFF4A3800),
    tertiaryContainer = Color(0xFFFFF3B0),
    onTertiaryContainer = Color(0xFF4A3800),
    background = Color(0xFFFFF8FC),
    onBackground = Color(0xFF2D1B2E),
    surface = Color.White,
    onSurface = Color(0xFF2D1B2E),
    surfaceVariant = Color(0xFFF5EEF2),
    onSurfaceVariant = Color(0xFF5C4D5E),
    error = Color(0xFFE53935),
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFF8FB8),
    onPrimary = Color(0xFF3D0A22),
    primaryContainer = Color(0xFF8B2E55),
    onPrimaryContainer = Color(0xFFFFD6E5),
    secondary = Color(0xFF8DD4FF),
    onSecondary = Color(0xFF003549),
    secondaryContainer = Color(0xFF004D6B),
    onSecondaryContainer = Color(0xFFD0EEFF),
    tertiary = Color(0xFFFFE566),
    onTertiary = Color(0xFF4A3800),
    tertiaryContainer = Color(0xFF6B5200),
    onTertiaryContainer = Color(0xFFFFF3B0),
    background = Color(0xFF1A1218),
    onBackground = Color(0xFFF5E6EE),
    surface = Color(0xFF241A22),
    onSurface = Color(0xFFF5E6EE),
    surfaceVariant = Color(0xFF3D2E38),
    onSurfaceVariant = Color(0xFFD4C2CE),
    error = Color(0xFFFF6B6B),
    onError = Color(0xFF3D0000)
)

@Composable
fun MartaTodoTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
