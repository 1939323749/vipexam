package app.xlei.vipexam.feature.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import app.xlei.vipexam.feature.settings.R
import app.xlei.vipexam.preference.LanguagePreference
import app.xlei.vipexam.preference.LocalLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LanguagePreferenceDialog(
    onDismiss: () -> Unit
) {
    val language = LocalLanguage.current
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()

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
                coroutine.launch(Dispatchers.Main) {
                    LanguagePreference.setLocale(this@apply)
                }
            }.put(context, coroutine)
        }
    )
}
