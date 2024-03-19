package app.xlei.vipexam.feature.settings.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.xlei.vipexam.feature.settings.R
import app.xlei.vipexam.preference.DataStoreKeys
import app.xlei.vipexam.preference.LocalOrganization
import app.xlei.vipexam.preference.dataStore
import app.xlei.vipexam.preference.put
import kotlinx.coroutines.launch

@Composable
fun OrganizationDialog(
    onDismissRequest: () -> Unit,
) {
    val coroutine = rememberCoroutineScope()
    val context = LocalContext.current
    val org = LocalOrganization.current
    var organization by remember {
        mutableStateOf(org.value)
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                coroutine.launch {
                    context.dataStore.put(
                        DataStoreKeys.Organization,
                        organization
                    )
                    onDismissRequest.invoke()
                }
            }) {
                Text(text = stringResource(id = R.string.okay))
            }
        },
        title = {
            Text(text = stringResource(id = R.string.edit_organiztion))
        },
        text = {
            OutlinedTextField(
                value = organization,
                onValueChange = { organization = it },
                shape = RoundedCornerShape(8.dp),
            )
        }
    )
}