package com.example.core.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ServixDarkBluePrimary,
    secondary = ServixDarkBlueSecondary,
    tertiary = ServixBlueLight,
    primaryContainer = ServixDarkBlueContainer,
    onPrimaryContainer = ServixBlueVeryLight,
    background = ServixDarkBackground,
    surface = ServixDarkSurface,
    surfaceVariant = ServixDarkSurfaceVariant,
    onPrimary = ServixDarkBackground,
    onSecondary = ServixDarkBackground,
    onBackground = ServixDarkTextPrimary,
    onSurface = ServixDarkTextPrimary,
    onSurfaceVariant = ServixDarkTextSecondary,
    outline = ServixDarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = ServixBluePrimary,
    secondary = ServixBlueSecondary,
    tertiary = ServixBlueLight,
    primaryContainer = ServixBlueVeryLight,
    onPrimaryContainer = ServixBlueDark,
    background = ServixBackground,
    surface = ServixWhite,
    surfaceVariant = ServixBlueSurface,
    onPrimary = ServixWhite,
    onSecondary = ServixWhite,
    onBackground = ServixTextPrimary,
    onSurface = ServixTextPrimary,
    onSurfaceVariant = ServixTextSecondary,
    outline = ServixBorder
)

@Composable
fun ServiXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
