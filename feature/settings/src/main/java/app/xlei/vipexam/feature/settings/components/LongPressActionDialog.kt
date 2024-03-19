package app.xlei.vipexam.feature.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import app.xlei.vipexam.feature.settings.R
import app.xlei.vipexam.preference.DataStoreKeys
import app.xlei.vipexam.preference.LocalLongPressAction
import app.xlei.vipexam.preference.LongPressAction
import app.xlei.vipexam.preference.dataStore
import app.xlei.vipexam.preference.put

@Composable
fun LongPressActionDialog(
    onDismiss: () -> Unit
) {
    val longPressAction = LocalLongPressAction.current
    val context = LocalContext.current

    ListPreferenceDialog(
        title = stringResource(R.string.long_press_action),
        onDismissRequest = {
            onDismiss.invoke()
        },
        options = listOf(
            ListPreferenceOption(
                name = stringResource(R.string.show_question),
                value = LongPressAction.ShowQuestion.value,
                isSelected = longPressAction.isShowQuestion()
            ),
            ListPreferenceOption(
                name = stringResource(R.string.show_translation),
                value = LongPressAction.ShowTranslation.value,
                isSelected = longPressAction.isShowTranslation()
            ),
            ListPreferenceOption(
                name = stringResource(R.string.none),
                value = LongPressAction.None.value,
                isSelected = longPressAction == LongPressAction.None
            ),
        ),
        onOptionSelected = { option ->
            context.dataStore.put(
                DataStoreKeys.LongPressAction,
                option.value
            )
        }
    )
}
