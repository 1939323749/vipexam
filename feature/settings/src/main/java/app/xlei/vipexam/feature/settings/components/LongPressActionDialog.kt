package app.xlei.vipexam.feature.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.datastore.preferences.core.edit
import app.xlei.vipexam.core.data.constant.LongPressAction
import app.xlei.vipexam.core.data.util.Preferences
import app.xlei.vipexam.core.data.util.dataStore
import app.xlei.vipexam.feature.settings.R
import kotlinx.coroutines.launch

@Composable
fun LongPressActionDialog(
    onDismiss: () -> Unit
) {
    val longPressAction =
        LongPressAction.entries[Preferences.longPressAction.collectAsState(
            LongPressAction.SHOW_QUESTION.value
        ).value]
    val context = LocalContext.current

    ListPreferenceDialog(
        title = stringResource(R.string.long_press_action),
        onDismissRequest = {
            onDismiss.invoke()
        },
        options = listOf(
            ListPreferenceOption(
                name = stringResource(R.string.show_question),
                value = LongPressAction.SHOW_QUESTION.value,
                isSelected = longPressAction == LongPressAction.SHOW_QUESTION
            ),
            ListPreferenceOption(
                name = stringResource(R.string.show_translation),
                value = LongPressAction.TRANSLATE.value,
                isSelected = longPressAction == LongPressAction.TRANSLATE
            ),
            ListPreferenceOption(
                name = stringResource(R.string.none),
                value = LongPressAction.NONE.value,
                isSelected = longPressAction == LongPressAction.NONE
            ),
        ),
        onOptionSelected = {option->
            context.dataStore.edit {
                it[Preferences.LONG_PRESS_ACTION] = option.value
            }
        }
    )
}
