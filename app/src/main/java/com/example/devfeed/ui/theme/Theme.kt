package com.example.devfeed.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DevFeedColorScheme = lightColorScheme(
    primary = ForestPrimary,
    onPrimary = MintSurface,
    primaryContainer = ForestPrimaryContainer,
    onPrimaryContainer = ForestPrimary,
    secondary = ForestSecondary,
    onSecondary = MintSurface,
    background = MintBackground,
    onBackground = TextDarkPrimary,
    surface = MintSurface,
    onSurface = TextDarkPrimary,
    surfaceContainer = MintSurfaceContainer,
    surfaceVariant = MintSurfaceVariant,
    onSurfaceVariant = TextDarkSecondary,
    outline = MintBorder,
    outlineVariant = MintBorder.copy(alpha = 0.5f)
)

@Composable
fun DevFeedTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DevFeedColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Status bar & nav bar ko splash green tone se match karna
            window.statusBarColor = SplashGreenTop.toArgb()
            window.navigationBarColor = MintSurfaceContainer.toArgb()

            // Dark icons on light status bar
            val insets = WindowCompat.getInsetsController(window, view)
            insets.isAppearanceLightStatusBars = true
            insets.isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}