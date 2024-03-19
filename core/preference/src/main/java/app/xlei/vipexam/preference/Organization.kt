package app.xlei.vipexam.preference

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class Organization(val value: String) : Preference() {
    data object Default : Organization("吉林大学")
    data class Custom(val organization: String?) : Organization(value = organization ?: "")

    override fun put(context: Context, scope: CoroutineScope) {
        scope.launch {
            context.dataStore.put(
                DataStoreKeys.Organization,
                value
            )
        }
    }

    companion object {
        val default = Default

        fun fromPreferences(preferences: Preferences) =
            when (preferences[DataStoreKeys.Organization.key]) {
                "吉林大学" -> Default
                else -> Custom(preferences[DataStoreKeys.Organization.key])
            }
    }
}