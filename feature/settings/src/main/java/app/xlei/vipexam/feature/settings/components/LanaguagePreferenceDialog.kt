package app.xlei.vipexam.feature.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import app.xlei.vipexam.feature.settings.R
import app.xlei.vipexam.preference.DataStoreKeys
import app.xlei.vipexam.preference.LanguagePreference
import app.xlei.vipexam.preference.LocalLanguage
import app.xlei.vipexam.preference.dataStore
import app.xlei.vipexam.preference.put

@Composable
fun LanguagePreferenceDialog(
    onDismiss: () -> Unit
) {
    val language = LocalLanguage.current
    val context = LocalContext.current

    ListPreferenceDialog(
        title = stringResource(R.string.app_language),
        onDismissRequest = {
            onDismiss.invoke()
        },
        options = LanguagePreference.values.map {
            ListPreferenceOption(
                name = it.toDesc(),
                value = it.value,
                isSelected = it == language
            )
        },
        onOptionSelected = { option ->
            LanguagePreference.values.first {
                it.value == option.value
            }.apply {
                context.dataStore.put(
                    DataStoreKeys.Language,
                    option.value
                )
                LanguagePreference.setLocale(this@apply)
            }
        }
    )
}
