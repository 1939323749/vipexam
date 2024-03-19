package app.xlei.vipexam.preference

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class ShowAnswerPreference(val value: Boolean) : Preference() {
    data object On : ShowAnswerPreference(true)
    data object Off : ShowAnswerPreference(false)

    @Composable
    @ReadOnlyComposable
    fun isShowAnswer() =
        when (this) {
            On -> true
            Off -> false
        }

    override fun put(context: Context, scope: CoroutineScope) {
        scope.launch {
            context.dataStore.put(
                DataStoreKeys.ShowAnswer,
                value
            )
        }
    }

    companion object {
        val default = Off

        fun fromPreferences(preferences: Preferences) =
            when (preferences[DataStoreKeys.ShowAnswer.key]) {
                true -> On
                false -> Off
                else -> default
            }
    }
}