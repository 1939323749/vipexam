package app.xlei.vipexam.feature.settings.components

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import app.xlei.vipexam.core.network.module.EudicRemoteDatasource
import app.xlei.vipexam.feature.settings.R
import app.xlei.vipexam.preference.DataStoreKeys
import app.xlei.vipexam.preference.LocalEudicApiKey
import app.xlei.vipexam.preference.dataStore
import app.xlei.vipexam.preference.put
import compose.icons.FeatherIcons
import compose.icons.feathericons.Check
import compose.icons.feathericons.CheckCircle
import compose.icons.feathericons.X
import kotlinx.coroutines.launch

@Composable
fun EudicApiKeyDialog(
    onDismissRequest: () -> Unit,
) {
    val coroutine = rememberCoroutineScope()
    val context = LocalContext.current
    val apiKey = LocalEudicApiKey.current
    var eudicApiKey by remember {
        mutableStateOf(apiKey.value)
    }
    var available by remember {
        mutableStateOf(false to false)
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                coroutine.launch {
                    context.dataStore.put(
                        DataStoreKeys.EudicApiKey,
                        eudicApiKey
                    )
                    onDismissRequest.invoke()
                }
            }) {
                Text(text = stringResource(id = R.string.okay))
            }
        },
        title = {
            Text(text = stringResource(id = R.string.edit_eudic_apikey))
        },
        text = {
            Column {
                OutlinedTextField(
                    value = eudicApiKey,
                    onValueChange = { eudicApiKey = it },
                    shape = RoundedCornerShape(8.dp),
                )
                Row(
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null)
                    Text(
                        text = stringResource(id = R.string.get_your_eudic_apikey),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    "https://my.eudic.net/OpenAPI/Authorization".toUri()
                                )
                                val packageManager = context.packageManager
                                context.startActivity(
                                    intent.setPackage(
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            packageManager.resolveActivity(
                                                intent,
                                                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong())
                                            )
                                        } else {
                                            packageManager.resolveActivity(
                                                intent,
                                                PackageManager.MATCH_DEFAULT_ONLY
                                            )
                                        }!!.activityInfo.packageName
                                    )
                                )
                            }
                            .padding(start = 4.dp)
                    )
                }
                Row(
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(imageVector = FeatherIcons.CheckCircle, contentDescription = null)
                    Text(
                        text = stringResource(id = R.string.check_availability),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable {
                                coroutine.launch {
                                    EudicRemoteDatasource.api = eudicApiKey
                                    EudicRemoteDatasource
                                        .check()
                                        .onSuccess {
                                            available = true to true
                                        }
                                        .onFailure {
                                            available = true to false
                                        }
                                }
                            }
                            .padding(start = 4.dp)
                    )
                    if (available.first)
                        when (available.second) {
                            true -> {
                                Icon(imageVector = FeatherIcons.Check, contentDescription = null)
                            }

                            false -> {
                                Icon(imageVector = FeatherIcons.X, contentDescription = null)
                            }
                        }
                }
            }
        }
    )
}
