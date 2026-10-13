package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AgriGreenPrimaryDark,
    onPrimary = AgriGreenOnContainer,
    primaryContainer = AgriGreenContainerDark,
    onPrimaryContainer = AgriGreenPrimaryDark,
    secondary = HarvestAmber,
    onSecondary = AgriGreenOnPrimary,
    secondaryContainer = HarvestAmberContainer,
    onSecondaryContainer = HarvestAmberOnContainer,
    tertiary = WaterBlue,
    background = AgriBackgroundDark,
    surface = AgriSurfaceDark,
    surfaceVariant = AgriSurfaceVariantDark,
    onSurface = AgriOnSurfaceDark,
    onSurfaceVariant = AgriOnSurfaceDark
)

private val LightColorScheme = lightColorScheme(
    primary = AgriGreenPrimary,
    onPrimary = AgriGreenOnPrimary,
    primaryContainer = AgriGreenContainer,
    onPrimaryContainer = AgriGreenOnContainer,
    secondary = HarvestAmber,
    onSecondary = AgriGreenOnPrimary,
    secondaryContainer = HarvestAmberContainer,
    onSecondaryContainer = HarvestAmberOnContainer,
    tertiary = WaterBlue,
    onTertiary = AgriGreenOnPrimary,
    tertiaryContainer = WaterBlueContainer,
    onTertiaryContainer = WaterBlueOnContainer,
    background = AgriBackground,
    surface = AgriSurface,
    surfaceVariant = AgriSurfaceVariant,
    onBackground = AgriOnSurface,
    onSurface = AgriOnSurface,
    onSurfaceVariant = AgriOnSurfaceVariant,
    outline = AgriOutline
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use intentional AgriConnect branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
