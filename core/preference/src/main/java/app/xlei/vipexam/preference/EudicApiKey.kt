package app.xlei.vipexam.preference

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class EudicApiKey(val value: String) : Preference() {
    data object Empty : EudicApiKey("")
    data class Some(val apiKey: String?) : EudicApiKey(apiKey ?: "")

    override fun put(context: Context, scope: CoroutineScope) {
        scope.launch {
            context.dataStore.put(
                DataStoreKeys.EudicApiKey,
                value
            )
        }
    }

    companion object {
        val default = Empty

        fun fromPreferences(preferences: Preferences) =
            when (preferences[DataStoreKeys.EudicApiKey.key]) {
                "" -> Empty
                else -> Some(preferences[DataStoreKeys.EudicApiKey.key])
            }
    }
}