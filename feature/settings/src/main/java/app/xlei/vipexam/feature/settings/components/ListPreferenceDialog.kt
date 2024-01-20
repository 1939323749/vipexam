package app.xlei.vipexam.feature.settings.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.xlei.vipexam.feature.settings.R

@Composable
fun ListPreferenceDialog(
    onDismissRequest: () -> Unit,
    options: List<ListPreferenceOption>,
    currentValue: Int? = null,
    title: String? = null,
    onOptionSelected: (ListPreferenceOption) -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            if (title != null)
                Text(title)
        },
        text = {
            LazyColumn {
                items(options) {
                    SelectableItem(
                        text = if (it.value == currentValue) "${it.name}   ✓" else it.name,
                        onClick = {
                            onOptionSelected.invoke(it)
                            onDismissRequest.invoke()
                        },
                        isSelected = it.isSelected
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest.invoke()
                }
            ) {
                Text(
                    stringResource(
                        R.string.cancel
                    )
                )
            }
        },
    )
}

data class ListPreferenceOption(
    val name: String,
    val value: Int,
    val isSelected: Boolean = false
)