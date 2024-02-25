package app.xlei.vipexam.feature.settings.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import app.xlei.vipexam.core.data.constant.Constants
import app.xlei.vipexam.core.data.util.Preferences
import app.xlei.vipexam.core.data.util.dataStore
import app.xlei.vipexam.feature.settings.R
import kotlinx.coroutines.launch

@Composable
fun OrganizationDialog(
    onDismissRequest: () -> Unit,
){
    val coroutine = rememberCoroutineScope()
    val context = LocalContext.current
    val organization = Preferences.organization.collectAsState(initial = Constants.ORGANIZATION)

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                onDismissRequest.invoke()
            }) {
                Text(text = stringResource(id = R.string.okay))
            }
        },
        title = {
            Text(text = stringResource(id = R.string.edit_organiztion))
        },
        text = {
            OutlinedTextField(
                value = organization.value,
                onValueChange = {value->
                    coroutine.launch {
                        context.dataStore.edit {
                            it[Preferences.ORGANIZATION] = value
                        }
                } },
                shape = RoundedCornerShape(8.dp),
            )
        }
    )
}