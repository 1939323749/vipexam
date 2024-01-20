package app.xlei.vipexam.feature.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.datastore.preferences.core.edit
import app.xlei.vipexam.core.data.constant.ThemeMode
import app.xlei.vipexam.core.data.util.Preferences
import app.xlei.vipexam.core.data.util.dataStore
import app.xlei.vipexam.feature.settings.R
import kotlinx.coroutines.launch

@Composable
fun ThemeModeDialog(
    onDismiss: () -> Unit,
) {
    val themeMode =
        ThemeMode.entries[Preferences.themeMode.collectAsState(ThemeMode.AUTO.value).value]
    val coroutine = rememberCoroutineScope()
    val context = LocalContext.current
    ListPreferenceDialog(
        title = stringResource(R.string.select_theme),
        onDismissRequest = {
            onDismiss.invoke()
        },
        options = listOf(
            ListPreferenceOption(
                name = stringResource(R.string.theme_auto),
                value = ThemeMode.AUTO.value,
                isSelected = themeMode == ThemeMode.AUTO
            ),
            ListPreferenceOption(
                name = stringResource(R.string.theme_light),
                value = ThemeMode.LIGHT.value,
                isSelected = themeMode == ThemeMode.LIGHT
            ),
            ListPreferenceOption(
                name = stringResource(R.string.theme_dark),
                value = ThemeMode.DARK.value,
                isSelected = themeMode == ThemeMode.DARK
            ),
            ListPreferenceOption(
                name = stringResource(R.string.theme_black),
                value = ThemeMode.BLACK.value,
                isSelected = themeMode == ThemeMode.BLACK
            )
        ),
        onOptionSelected = {option->
            coroutine.launch {
                context.dataStore.edit {
                    it[Preferences.THEME_MODE] = option.value
                }
            }
        }
    )
}