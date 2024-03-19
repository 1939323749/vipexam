package app.xlei.vipexam.preference

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class LongPressAction(val value: Int) : Preference() {
    data object None : LongPressAction(0)
    data object ShowQuestion : LongPressAction(1)
    data object ShowTranslation : LongPressAction(2)

    override fun put(context: Context, scope: CoroutineScope) {
        scope.launch {
            context.dataStore.put(
                DataStoreKeys.LongPressAction,
                value
            )
        }
    }

    @Composable
    @ReadOnlyComposable
    fun isShowQuestion() = this is ShowQuestion

    @Composable
    @ReadOnlyComposable
    fun isShowTranslation() = this is ShowTranslation

    companion object {
        val default = None

        fun fromPreferences(preferences: Preferences) =
            when (preferences[DataStoreKeys.LongPressAction.key]) {
                0 -> None
                1 -> ShowQuestion
                2 -> ShowTranslation
                else -> default
            }
    }
}