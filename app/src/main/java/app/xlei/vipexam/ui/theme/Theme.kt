package app.xlei.vipexam.ui.theme

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import app.xlei.vipexam.preference.ThemeModePreference
import com.google.android.material.color.utilities.Scheme


const val defaultAccentColor = "fedfe1"
fun String.hexToColor() = Color(android.graphics.Color.parseColor("#$this"))

@SuppressLint("RestrictedApi")
@Composable
fun VipexamTheme(
    themeMode: ThemeModePreference,
    shouldShowNavigationRegion: Boolean = false,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeModePreference.Auto -> isSystemInDarkTheme()
        ThemeModePreference.Light -> false
        ThemeModePreference.Dark, ThemeModePreference.Black -> true
    }
    var colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        else -> {
            val seed = defaultAccentColor.hexToColor().toArgb()
            if (darkTheme) Scheme.dark(seed).toColorScheme() else Scheme.light(seed).toColorScheme()
        }
    }

    if (themeMode == ThemeModePreference.Black) colorScheme =
        colorScheme.copy(background = Color.Black, surface = Color.Black)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as Activity
            activity.window.navigationBarColor = colorScheme.run {
                if (shouldShowNavigationRegion)
                    this.surfaceContainer.toArgb()
                else
                    this.surface.toArgb()
            }
            activity.window.statusBarColor = colorScheme.run {
                if (shouldShowNavigationRegion)
                    this.surfaceContainer.toArgb()
                else
                    this.surface.toArgb()
            }
            WindowCompat.getInsetsController(
                activity.window,
                view
            ).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(
                activity.window,
                view
            ).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@SuppressLint("RestrictedApi")
fun Scheme.toColorScheme() = ColorScheme(
    primary = Color(primary),
    onPrimary = Color(onPrimary),
    primaryContainer = Color(primaryContainer),
    onPrimaryContainer = Color(onPrimaryContainer),
    inversePrimary = Color(inversePrimary),
    secondary = Color(secondary),
    onSecondary = Color(onSecondary),
    secondaryContainer = Color(secondaryContainer),
    onSecondaryContainer = Color(onSecondaryContainer),
    tertiary = Color(tertiary),
    onTertiary = Color(onTertiary),
    tertiaryContainer = Color(tertiaryContainer),
    onTertiaryContainer = Color(onTertiaryContainer),
    background = Color(background),
    onBackground = Color(onBackground),
    surface = Color(surface),
    onSurface = Color(onSurface),
    surfaceVariant = Color(surfaceVariant),
    onSurfaceVariant = Color(onSurfaceVariant),
    surfaceTint = Color(primary),
    inverseSurface = Color(inverseSurface),
    inverseOnSurface = Color(inverseOnSurface),
    error = Color(error),
    onError = Color(onError),
    errorContainer = Color(errorContainer),
    onErrorContainer = Color(onErrorContainer),
    outline = Color(outline),
    outlineVariant = Color(outlineVariant),
    scrim = Color(scrim)
)