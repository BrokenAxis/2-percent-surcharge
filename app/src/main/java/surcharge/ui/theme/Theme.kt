package surcharge.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable


private val oceanLightColors = lightColorScheme(
    primary = ocean_theme_light_primary,
    onPrimary = ocean_theme_light_onPrimary,
    primaryContainer = ocean_theme_light_primaryContainer,
    onPrimaryContainer = ocean_theme_light_onPrimaryContainer,
    secondary = ocean_theme_light_secondary,
    onSecondary = ocean_theme_light_onSecondary,
    secondaryContainer = ocean_theme_light_secondaryContainer,
    onSecondaryContainer = ocean_theme_light_onSecondaryContainer,
    tertiary = ocean_theme_light_tertiary,
    onTertiary = ocean_theme_light_onTertiary,
    tertiaryContainer = ocean_theme_light_tertiaryContainer,
    onTertiaryContainer = ocean_theme_light_onTertiaryContainer,
    error = ocean_theme_light_error,
    errorContainer = ocean_theme_light_errorContainer,
    onError = ocean_theme_light_onError,
    onErrorContainer = ocean_theme_light_onErrorContainer,
    background = ocean_theme_light_background,
    onBackground = ocean_theme_light_onBackground,
    surface = ocean_theme_light_surface,
    onSurface = ocean_theme_light_onSurface,
    surfaceVariant = ocean_theme_light_surfaceVariant,
    onSurfaceVariant = ocean_theme_light_onSurfaceVariant,
    outline = ocean_theme_light_outline,
    inverseOnSurface = ocean_theme_light_inverseOnSurface,
    inverseSurface = ocean_theme_light_inverseSurface,
    inversePrimary = ocean_theme_light_inversePrimary,
    surfaceTint = ocean_theme_light_surfaceTint,
    outlineVariant = ocean_theme_light_outlineVariant,
    scrim = ocean_theme_light_scrim,
)


private val oceanDarkColors = darkColorScheme(
    primary = ocean_theme_dark_primary,
    onPrimary = ocean_theme_dark_onPrimary,
    primaryContainer = ocean_theme_dark_primaryContainer,
    onPrimaryContainer = ocean_theme_dark_onPrimaryContainer,
    secondary = ocean_theme_dark_secondary,
    onSecondary = ocean_theme_dark_onSecondary,
    secondaryContainer = ocean_theme_dark_secondaryContainer,
    onSecondaryContainer = ocean_theme_dark_onSecondaryContainer,
    tertiary = ocean_theme_dark_tertiary,
    onTertiary = ocean_theme_dark_onTertiary,
    tertiaryContainer = ocean_theme_dark_tertiaryContainer,
    onTertiaryContainer = ocean_theme_dark_onTertiaryContainer,
    error = ocean_theme_dark_error,
    errorContainer = ocean_theme_dark_errorContainer,
    onError = ocean_theme_dark_onError,
    onErrorContainer = ocean_theme_dark_onErrorContainer,
    background = ocean_theme_dark_background,
    onBackground = ocean_theme_dark_onBackground,
    surface = ocean_theme_dark_surface,
    onSurface = ocean_theme_dark_onSurface,
    surfaceVariant = ocean_theme_dark_surfaceVariant,
    onSurfaceVariant = ocean_theme_dark_onSurfaceVariant,
    outline = ocean_theme_dark_outline,
    inverseOnSurface = ocean_theme_dark_inverseOnSurface,
    inverseSurface = ocean_theme_dark_inverseSurface,
    inversePrimary = ocean_theme_dark_inversePrimary,
    surfaceTint = ocean_theme_dark_surfaceTint,
    outlineVariant = ocean_theme_dark_outlineVariant,
    scrim = ocean_theme_dark_scrim,
)

private val earthLightColors = lightColorScheme(
    primary = earth_theme_light_primary,
    onPrimary = earth_theme_light_onPrimary,
    primaryContainer = earth_theme_light_primaryContainer,
    onPrimaryContainer = earth_theme_light_onPrimaryContainer,
    secondary = earth_theme_light_secondary,
    onSecondary = earth_theme_light_onSecondary,
    secondaryContainer = earth_theme_light_secondaryContainer,
    onSecondaryContainer = earth_theme_light_onSecondaryContainer,
    tertiary = earth_theme_light_tertiary,
    onTertiary = earth_theme_light_onTertiary,
    tertiaryContainer = earth_theme_light_tertiaryContainer,
    onTertiaryContainer = earth_theme_light_onTertiaryContainer,
    error = earth_theme_light_error,
    errorContainer = earth_theme_light_errorContainer,
    onError = earth_theme_light_onError,
    onErrorContainer = earth_theme_light_onErrorContainer,
    background = earth_theme_light_background,
    onBackground = earth_theme_light_onBackground,
    surface = earth_theme_light_surface,
    onSurface = earth_theme_light_onSurface,
    surfaceVariant = earth_theme_light_surfaceVariant,
    onSurfaceVariant = earth_theme_light_onSurfaceVariant,
    outline = earth_theme_light_outline,
    inverseOnSurface = earth_theme_light_inverseOnSurface,
    inverseSurface = earth_theme_light_inverseSurface,
    inversePrimary = earth_theme_light_inversePrimary,
    surfaceTint = earth_theme_light_surfaceTint,
    outlineVariant = earth_theme_light_outlineVariant,
    scrim = earth_theme_light_scrim,
)

private val earthDarkColors = darkColorScheme(
    primary = earth_theme_dark_primary,
    onPrimary = earth_theme_dark_onPrimary,
    primaryContainer = earth_theme_dark_primaryContainer,
    onPrimaryContainer = earth_theme_dark_onPrimaryContainer,
    secondary = earth_theme_dark_secondary,
    onSecondary = earth_theme_dark_onSecondary,
    secondaryContainer = earth_theme_dark_secondaryContainer,
    onSecondaryContainer = earth_theme_dark_onSecondaryContainer,
    tertiary = earth_theme_dark_tertiary,
    onTertiary = earth_theme_dark_onTertiary,
    tertiaryContainer = earth_theme_dark_tertiaryContainer,
    onTertiaryContainer = earth_theme_dark_onTertiaryContainer,
    error = earth_theme_dark_error,
    errorContainer = earth_theme_dark_errorContainer,
    onError = earth_theme_dark_onError,
    onErrorContainer = earth_theme_dark_onErrorContainer,
    background = earth_theme_dark_background,
    onBackground = earth_theme_dark_onBackground,
    surface = earth_theme_dark_surface,
    onSurface = earth_theme_dark_onSurface,
    surfaceVariant = earth_theme_dark_surfaceVariant,
    onSurfaceVariant = earth_theme_dark_onSurfaceVariant,
    outline = earth_theme_dark_outline,
    inverseOnSurface = earth_theme_dark_inverseOnSurface,
    inverseSurface = earth_theme_dark_inverseSurface,
    inversePrimary = earth_theme_dark_inversePrimary,
    surfaceTint = earth_theme_dark_surfaceTint,
    outlineVariant = earth_theme_dark_outlineVariant,
    scrim = earth_theme_dark_scrim,
)

@Composable
fun SurchargeTheme(
    theme: Int = 0, content: @Composable () -> Unit
) {

    val colors = when (theme) {
        0 -> {
            if (isSystemInDarkTheme()) oceanDarkColors
            else oceanLightColors
        }

        1 -> {
            if (isSystemInDarkTheme()) earthDarkColors
            else earthLightColors
        }

        else -> oceanDarkColors
    }

    MaterialTheme(
        colorScheme = colors, content = content
    )
}