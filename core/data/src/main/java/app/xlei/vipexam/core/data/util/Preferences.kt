package app.xlei.vipexam.core.data.util

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import app.xlei.vipexam.core.data.constant.LongPressAction
import app.xlei.vipexam.core.data.constant.ShowAnswerOption
import app.xlei.vipexam.core.data.constant.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(
    name = "preferences",
)
object Preferences {
    const val appLanguageKey = "appLanguage"

    val REMEMBER_ACCOUNT = booleanPreferencesKey("rememberAccount")
    val AUTOLOGIN = booleanPreferencesKey("autoLogin")
    val VIBRATE = booleanPreferencesKey("vibrate")
    val LONG_PRESS_ACTION = intPreferencesKey("long_press_action")
    val THEME_MODE = intPreferencesKey("theme_mode")
    val SHOW_ANSWER = booleanPreferencesKey("show_answer")
    val SHOW_ANSWER_OPTION = intPreferencesKey("show_answer_option")

    lateinit var rememberAccount: Flow<Boolean>
    lateinit var autoLogin: Flow<Boolean>
    lateinit var vibrate: Flow<Boolean>
    lateinit var longPressAction: Flow<Int>
    lateinit var showAnswer: Flow<Boolean>
    lateinit var showAnswerOption: Flow<Int>
    lateinit var themeMode: Flow<Int>
    lateinit var prefs: SharedPreferences

    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(
            "preferences",
            Context.MODE_PRIVATE
        )
        autoLogin = getPreferenceValue(context, AUTOLOGIN, false)
        vibrate = getPreferenceValue(context, VIBRATE, true)
        showAnswer = getPreferenceValue(context, SHOW_ANSWER, false)
        showAnswerOption = getPreferenceValue(context, SHOW_ANSWER_OPTION, ShowAnswerOption.ONCE.value)
        themeMode = getPreferenceValue(context, THEME_MODE, ThemeMode.AUTO.value)
        longPressAction = getPreferenceValue(context, LONG_PRESS_ACTION, LongPressAction.SHOW_QUESTION.value)
        rememberAccount = getPreferenceValue(context, REMEMBER_ACCOUNT, false)
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

    private fun <T> getPreferenceValue(context: Context, key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }
}
