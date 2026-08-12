package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = if (ThemeState.isDark) {
        darkColorScheme(
            primary = AccentCyan,
            background = AppBackground,
            surface = CardBackground,
            onPrimary = AppBackground,
            onBackground = TextPrimary,
            onSurface = TextPrimary,
            surfaceVariant = Color(0xFF1E293B),
            onSurfaceVariant = TextSecondary,
            error = ErrorRed,
            onError = TextPrimary
        )
    } else {
        lightColorScheme(
            primary = AccentCyan,
            background = AppBackground,
            surface = CardBackground,
            onPrimary = AppBackground,
            onBackground = TextPrimary,
            onSurface = TextPrimary,
            surfaceVariant = Color(0xFFF1F5F9),
            onSurfaceVariant = TextSecondary,
            error = ErrorRed,
            onError = AppBackground
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
