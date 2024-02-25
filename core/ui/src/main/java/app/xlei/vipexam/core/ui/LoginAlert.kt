package app.xlei.vipexam.core.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun LoginAlert(
    onDismissRequest: () -> Unit,
){
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton  = { TextButton(onClick = { onDismissRequest.invoke() }) {
            Text(text = stringResource(id = R.string.ok))
        } },
        title = {
            Text(text = stringResource(id = R.string.login_to_continue))
        }
    )
}