package app.xlei.vipexam.feature.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import app.xlei.vipexam.feature.settings.R
import app.xlei.vipexam.preference.DataStoreKeys
import app.xlei.vipexam.preference.LocalThemeMode
import app.xlei.vipexam.preference.ThemeModePreference
import app.xlei.vipexam.preference.dataStore
import app.xlei.vipexam.preference.put

@Composable
fun ThemeModeDialog(
    onDismiss: () -> Unit,
) {
    val themeMode = LocalThemeMode.current
    val context = LocalContext.current

    ListPreferenceDialog(
        title = stringResource(R.string.select_theme),
        onDismissRequest = {
            onDismiss.invoke()
        },
        options = listOf(
            ListPreferenceOption(
                name = stringResource(R.string.theme_auto),
                value = ThemeModePreference.Auto.value,
                isSelected = themeMode == ThemeModePreference.Auto
            ),
            ListPreferenceOption(
                name = stringResource(R.string.theme_light),
                value = ThemeModePreference.Light.value,
                isSelected = themeMode == ThemeModePreference.Light
            ),
            ListPreferenceOption(
                name = stringResource(R.string.theme_dark),
                value = ThemeModePreference.Dark.value,
                isSelected = themeMode == ThemeModePreference.Dark
            ),
            ListPreferenceOption(
                name = stringResource(R.string.theme_black),
                value = ThemeModePreference.Black.value,
                isSelected = themeMode == ThemeModePreference.Black
            )
        ),
        onOptionSelected = { option ->
            context.dataStore.put(DataStoreKeys.ThemeMode, option.value)
        }
    )
}