package app.xlei.vipexam.preference

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class PinnedExams(val value: String) : Preference() {
    data object None : PinnedExams("")
    data class Some(val exam: String?) : PinnedExams(value = exam ?: "")

    override fun put(context: Context, scope: CoroutineScope) {
        scope.launch {
            context.dataStore.put(
                DataStoreKeys.Organization,
                value
            )
        }
    }

    companion object {
        val default = None

        fun fromPreferences(preferences: Preferences) =
            when (preferences[DataStoreKeys.PinnedExams.key]) {
                "" -> None
                else -> Some(preferences[DataStoreKeys.PinnedExams.key])
            }
    }
}