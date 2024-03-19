package app.xlei.vipexam.preference

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class ShowAnswerOptionPreference(val value: Int) : Preference() {
    data object Once : ShowAnswerOptionPreference(0)
    data object Always : ShowAnswerOptionPreference(1)

    override fun put(context: Context, scope: CoroutineScope) {
        scope.launch {
            context.dataStore.put(
                DataStoreKeys.ShowAnswerOption,
                value
            )
        }
    }

    companion object {
        val default = Once

        fun fromPreferences(preferences: Preferences) =
            when (preferences[DataStoreKeys.ShowAnswerOption.key]) {
                0 -> Once
                1 -> Always
                else -> default
            }
    }
}
