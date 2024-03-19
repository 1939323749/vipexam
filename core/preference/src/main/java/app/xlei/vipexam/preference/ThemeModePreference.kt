package app.xlei.vipexam.preference

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class ThemeModePreference(val value: Int) : Preference() {
    data object Auto : ThemeModePreference(0)
    data object Light : ThemeModePreference(1)
    data object Dark : ThemeModePreference(2)
    data object Black : ThemeModePreference(3)

    override fun put(context: Context, scope: CoroutineScope) {
        scope.launch {
            context.dataStore.put(
                DataStoreKeys.ThemeMode,
                value
            )
        }
    }

    @Composable
    @ReadOnlyComposable
    fun isDarkTheme() =
        when (this) {
            Auto -> isSystemInDarkTheme()
            Light -> false
            Dark -> true
            Black -> true
        }

    companion object {
        val default = Auto
        val values = listOf(Auto, Light, Dark, Black)

        fun fromPreferences(preferences: Preferences) =
            when (preferences[DataStoreKeys.ThemeMode.key]) {
                0 -> Auto
                1 -> Light
                2 -> Dark
                3 -> Black
                else -> default
            }
    }
}