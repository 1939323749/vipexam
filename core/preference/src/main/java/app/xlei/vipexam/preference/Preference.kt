package app.xlei.vipexam.preference

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope

sealed class Preference {
    abstract fun put(context: Context, scope: CoroutineScope)
}

fun Preferences.toSettings(): Settings {
    return Settings(
        themeMode = ThemeModePreference.fromPreferences(this),
        vibrate = VibratePreference.fromPreferences(this),
        showAnswer = ShowAnswerPreference.fromPreferences(this),
        longPressAction = LongPressAction.fromPreferences(this),
        showAnswerOptionPreference = ShowAnswerOptionPreference.fromPreferences(this),
        organization = Organization.fromPreferences(this),
        language = LanguagePreference.fromPreferences(this),
        localEudicApiKey = EudicApiKey.fromPreferences(this)
    )
}