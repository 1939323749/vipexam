package app.xlei.vipexam.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import app.xlei.vipexam.constant.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

const val defaultAccentColor = "1e131d"
fun String.hexToColor() = Color(android.graphics.Color.parseColor("#$this"))

@Composable
fun VipexamTheme(
    themeMode: ThemeMode = ThemeMode.AUTO,
    accentColor: Color? = null,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.AUTO -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK, ThemeMode.BLACK -> true
    }

    var colorScheme = when {
        accentColor == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        else -> {
            val primary = accentColor ?: defaultAccentColor.hexToColor()
            val onPrimary = MaterialTheme.colorScheme.contentColorFor(primary)
            val blendColor =
                if (darkTheme) android.graphics.Color.WHITE else android.graphics.Color.BLACK
            val secondary = Color(ColorUtils.blendARGB(primary.toArgb(), blendColor, 0.3f))
            val onSecondary = MaterialTheme.colorScheme.contentColorFor(secondary)
            if (darkTheme) {
                darkColorScheme(
                    primary,
                    onPrimary,
                    secondary = secondary,
                    onSecondary = onSecondary
                )
            } else {
                lightColorScheme(
                    primary,
                    onPrimary,
                    secondary = secondary,
                    onSecondary = onSecondary
                )
            }
        }
    }
    if (themeMode == ThemeMode.BLACK) colorScheme =
        colorScheme.copy(background = Color.Black, surface = Color.Black)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as Activity
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activity.window.navigationBarColor = colorScheme.background.toArgb()
                activity.window.statusBarColor = colorScheme.background.toArgb()
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
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
