package app.xlei.vipexam.core.data.util

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import app.xlei.vipexam.core.data.constant.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(
    name = "preferences",
)
object Preferences {
    const val longPressActionKey = "longPressAction"
    const val alwaysShowAnswerKey = "showAnswer"
    const val appLanguageKey = "appLanguage"
    const val themeModeKey = "themeModeKey"
    const val accentColorKey = "accentColor"
    const val rememberAccountKey = "rememberAccount"
    const val autoLoginKey = "autoLogin"
    val SHOW_ANSWER = booleanPreferencesKey("show_answer")
    lateinit var showAnswerFlow: Flow<Boolean>
    lateinit var prefs: SharedPreferences

    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(
            "preferences",
            Context.MODE_PRIVATE
        )
        showAnswerFlow = context.dataStore.data.map { preferences ->
            preferences[SHOW_ANSWER] ?: false
        }
    }
    fun <T> put(key: String, value: T) {
        when (value) {
            is Boolean -> prefs.edit().putBoolean(key, value).apply()
            is String -> prefs.edit().putString(key, value).apply()
            is Int -> prefs.edit().putInt(key, value).apply()
            is Float -> prefs.edit().putFloat(key, value).apply()
            is Long -> prefs.edit().putLong(key, value).apply()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String, defValue: T): T {
        return when (defValue) {
            is Boolean -> prefs.getBoolean(key, defValue) as T
            is Int -> (prefs.getInt(key, defValue)) as T
            is Long -> (prefs.getLong(key, defValue)) as T
            is Float -> (prefs.getFloat(key, defValue)) as T
            else -> (prefs.getString(key, defValue.toString()) ?: defValue) as T
        }
    }

    fun getThemeMode() =
        ThemeMode.entries[get(themeModeKey, ThemeMode.AUTO.value.toString()).toInt()]

    fun getAccentColor() = prefs.getString(accentColorKey, null)
}
