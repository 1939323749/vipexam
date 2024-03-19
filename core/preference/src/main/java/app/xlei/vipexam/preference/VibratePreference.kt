package app.xlei.vipexam.preference

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class VibratePreference(val value: Boolean) : Preference() {
    data object On : VibratePreference(true)
    data object Off : VibratePreference(false)

    override fun put(context: Context, scope: CoroutineScope) {
        scope.launch {
            context.dataStore.put(
                DataStoreKeys.Vibrate,
                value
            )
        }
    }

    fun isVibrate() =
        when (this) {
            On -> true
            Off -> false
        }

    companion object {
        val default = On

        fun fromPreferences(preferences: Preferences) =
            when (preferences[DataStoreKeys.Vibrate.key]) {
                true -> On
                false -> Off
                else -> default
            }
    }
}