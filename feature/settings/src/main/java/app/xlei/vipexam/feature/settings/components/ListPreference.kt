package app.xlei.vipexam.feature.settings.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import app.xlei.vipexam.core.data.util.Preferences

@Composable
fun ListPreference(
    title: String,
    summary: String? = null,
    preferenceKey: String,
    defaultValue: String,
    entries: List<String>,
    values: List<String>,
    onOptionSelected: (ListPreferenceOption) -> Unit = {}
) {
    var showDialog by remember {
        mutableStateOf(false)
    }

    var selectedIndex by remember {
        mutableIntStateOf(values.indexOf(Preferences.get(preferenceKey, defaultValue)))
    }

    PreferenceItem(
        title = title,
        summary = summary ?: entries.getOrElse(selectedIndex) { entries.first() },
        modifier = Modifier.fillMaxWidth()
    ) {
        showDialog = true
    }

    if (showDialog) {
        ListPreferenceDialog(
            title = title,
            onDismissRequest = {
                showDialog = false
            },
            options = entries.mapIndexed { index, entry ->
                ListPreferenceOption(entry, index)
            },
            onOptionSelected = {
                Preferences.put(preferenceKey, values[it.value])
                selectedIndex = it.value
                onOptionSelected.invoke(it)
            },
            currentValue = selectedIndex
        )
    }
}