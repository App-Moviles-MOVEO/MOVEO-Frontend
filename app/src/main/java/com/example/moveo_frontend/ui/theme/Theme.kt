package com.example.moveo_frontend.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    primaryContainer = BlueLight,
    onPrimaryContainer = BluePrimary,
    secondary = BlueAccent,
    onSecondary = Color.White,
    tertiary = GreenSuccess,
    onTertiary = Color.White,
    background = GrayBackground,
    onBackground = TextDark,
    surface = GraySurface,
    onSurface = TextDark,
    surfaceVariant = GrayBackground,
    onSurfaceVariant = TextMuted,
    outline = GrayBorder,
    error = RedAlert,
    onError = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = BlueAccent,
    onPrimary = Color.White,
    secondary = BluePrimary,
    background = Color(0xFF0B1220),
    surface = Color(0xFF111827),
    onBackground = Color.White,
    onSurface = Color.White,
)

@Composable
fun MoveofrontendTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
