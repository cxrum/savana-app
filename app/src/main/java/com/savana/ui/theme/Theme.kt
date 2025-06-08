package com.savana.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = RichBlack,
    onPrimary = Alabaster,
    primaryContainer = SalmonPink,
    onPrimaryContainer = RichBlack,
    secondaryContainer = PaleSurface,
    onSecondaryContainer = RichBlack,
    background = Alabaster,
    onBackground = RichBlack,
    surface = Alabaster,
    onSurface = RichBlack,
    surfaceVariant = Zinnwaldite,
    onSurfaceVariant = RichBlack,

    outline = Zinnwaldite,
    secondary = SealBrown,
    onSecondary = Alabaster,
    tertiary = EarthyGreen,
    onTertiary = Alabaster,

    tertiaryContainer = EarthyGreenContainer,
    onTertiaryContainer = OnEarthyGreenContainer,
    error = MaterialErrorRed,
    errorContainer = MaterialErrorContainer,
    onError = Alabaster,
    onErrorContainer = OnMaterialErrorContainer,
)

@Composable
fun SavanaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}