package app.xlei.vipexam.preference

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale

val Context.languages: Int
    get() = this.dataStore.get(DataStoreKeys.Language) ?: 0

sealed class LanguagePreference(val value: Int) : Preference() {

    data object UseDeviceLanguages : LanguagePreference(0)
    data object ChineseSimplified : LanguagePreference(1)
    data object English : LanguagePreference(2)

    override fun put(context: Context, scope: CoroutineScope) {
        scope.launch {
            context.dataStore.put(
                DataStoreKeys.Language,
                value
            )
        }
    }

    private fun toLocale(): Locale? = when (this) {
        UseDeviceLanguages -> null
        ChineseSimplified -> Locale.forLanguageTag("zh-Hans")
        English -> Locale("en")
    }

    private fun toLocaleList(): LocaleListCompat =
        toLocale()?.let { LocaleListCompat.create(it) } ?: LocaleListCompat.getEmptyLocaleList()

    @Composable
    fun toDesc() =
        stringResource(
            id = when (this) {
                UseDeviceLanguages -> R.string.use_device_languages
                ChineseSimplified -> R.string.chinese_simplified
                English -> R.string.english
            }
        )

    companion object {
        val default = UseDeviceLanguages

        val values = listOf(
            UseDeviceLanguages,
            ChineseSimplified,
            English,
        )

        fun fromPreferences(preferences: Preferences) =
            when (preferences[DataStoreKeys.Language.key]) {
                0 -> UseDeviceLanguages
                1 -> ChineseSimplified
                2 -> English
                else -> default
            }

        fun fromValue(value: Int): LanguagePreference = when (value) {
            0 -> UseDeviceLanguages
            1 -> ChineseSimplified
            2 -> English
            else -> default
        }

        fun setLocale(preference: LanguagePreference) {
            AppCompatDelegate.setApplicationLocales(preference.toLocaleList())
        }
    }
}