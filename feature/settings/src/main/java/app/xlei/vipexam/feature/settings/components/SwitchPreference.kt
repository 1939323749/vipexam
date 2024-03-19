package app.xlei.vipexam.feature.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.xlei.vipexam.preference.DataStoreKeys
import app.xlei.vipexam.preference.dataStore
import app.xlei.vipexam.preference.put
import kotlinx.coroutines.launch

@Composable
fun SwitchPreference(
    title: String,
    summary: String,
    checked: Boolean,
    preferencesKey: DataStoreKeys<Boolean>,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                coroutine.launch {
                    context.dataStore.put(
                        preferencesKey,
                        !checked
                    )
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title)
            Spacer(Modifier.height(4.dp))
            Text(
                text = summary,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Column {
            Switch(
                checked = checked,
                onCheckedChange = {
                    coroutine.launch {
                        context.dataStore.put(
                            preferencesKey,
                            !checked
                        )
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp)
                )
                )
        }
    }
}