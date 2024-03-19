package app.xlei.vipexam.preference

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.map

val LocalThemeMode = compositionLocalOf<ThemeModePreference> { ThemeModePreference.default }
val LocalVibrate = compositionLocalOf<VibratePreference> { VibratePreference.default }
val LocalShowAnswer = compositionLocalOf<ShowAnswerPreference> { ShowAnswerPreference.default }
val LocalLongPressAction = compositionLocalOf<LongPressAction> { LongPressAction.default }
val LocalShowAnswerOption =
    compositionLocalOf<ShowAnswerOptionPreference> { ShowAnswerOptionPreference.default }
val LocalOrganization = compositionLocalOf<Organization> { Organization.default }
val LocalLanguage = compositionLocalOf<LanguagePreference> { LanguagePreference.default }

data class Settings(
    val themeMode: ThemeModePreference = ThemeModePreference.default,
    val vibrate: VibratePreference = VibratePreference.default,
    val showAnswer: ShowAnswerPreference = ShowAnswerPreference.default,
    val longPressAction: LongPressAction = LongPressAction.default,
    val showAnswerOptionPreference: ShowAnswerOptionPreference = ShowAnswerOptionPreference.default,
    val organization: Organization = Organization.default,
    val language: LanguagePreference = LanguagePreference.default,
)

@Composable
fun SettingsProvider(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val settings by remember {
        context.dataStore.data.map {
            it.toSettings()
        }
    }.collectAsState(initial = Settings())

    CompositionLocalProvider(
        LocalThemeMode provides settings.themeMode,
        LocalVibrate provides settings.vibrate,
        LocalShowAnswer provides settings.showAnswer,
        LocalLongPressAction provides settings.longPressAction,
        LocalShowAnswerOption provides settings.showAnswerOptionPreference,
        LocalOrganization provides settings.organization,
        LocalLanguage provides settings.language,
    ) {
        content()
    }
}