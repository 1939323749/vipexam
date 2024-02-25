package app.xlei.vipexam.feature.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.datastore.preferences.core.edit
import app.xlei.vipexam.core.data.constant.ShowAnswerOption
import app.xlei.vipexam.core.data.util.Preferences
import app.xlei.vipexam.core.data.util.dataStore
import app.xlei.vipexam.feature.settings.R
import kotlinx.coroutines.launch


@Composable
fun ShowAnswerDialog(
    onDismiss: () -> Unit
) {
    val showAnswerOption = ShowAnswerOption.entries[
        Preferences.showAnswerOption.collectAsState(initial = ShowAnswerOption.ONCE.value).value
    ]
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()

    ListPreferenceDialog(
        title = stringResource(R.string.show_answer),
        onDismissRequest = {
            onDismiss.invoke()
        },
        options = listOf(
            ListPreferenceOption(
                name = stringResource(R.string.always),
                value = ShowAnswerOption.ALWAYS.value,
                isSelected = showAnswerOption == ShowAnswerOption.ALWAYS
            ),
            ListPreferenceOption(
                name = stringResource(R.string.once),
                value = ShowAnswerOption.ONCE.value,
                isSelected = showAnswerOption == ShowAnswerOption.ONCE
            ),
        ),
        onOptionSelected = {option->
            context.dataStore.edit {
                it[Preferences.SHOW_ANSWER_OPTION] = option.value
            }
        }
    )
}