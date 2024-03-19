package app.xlei.vipexam.feature.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import app.xlei.vipexam.feature.settings.R
import app.xlei.vipexam.preference.DataStoreKeys
import app.xlei.vipexam.preference.LocalShowAnswerOption
import app.xlei.vipexam.preference.ShowAnswerOptionPreference
import app.xlei.vipexam.preference.dataStore
import app.xlei.vipexam.preference.put


@Composable
fun ShowAnswerDialog(
    onDismiss: () -> Unit
) {
    val showAnswerOption = LocalShowAnswerOption.current
    val context = LocalContext.current

    ListPreferenceDialog(
        title = stringResource(R.string.show_answer),
        onDismissRequest = {
            onDismiss.invoke()
        },
        options = listOf(
            ListPreferenceOption(
                name = stringResource(R.string.always),
                value = ShowAnswerOptionPreference.Always.value,
                isSelected = showAnswerOption == ShowAnswerOptionPreference.Always
            ),
            ListPreferenceOption(
                name = stringResource(R.string.once),
                value = ShowAnswerOptionPreference.Once.value,
                isSelected = showAnswerOption == ShowAnswerOptionPreference.Once
            ),
        ),
        onOptionSelected = { option ->
            context.dataStore.put(
                DataStoreKeys.ShowAnswerOption,
                option.value
            )
        }
    )
}