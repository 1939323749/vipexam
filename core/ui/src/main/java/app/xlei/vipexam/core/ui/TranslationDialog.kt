package app.xlei.vipexam.core.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.xlei.vipexam.core.network.module.TranslationResponse
import compose.icons.FeatherIcons
import compose.icons.feathericons.Loader
import kotlinx.coroutines.launch

@Composable
fun TranslateDialog(
    onDismissRequest: () -> Unit,
    dismissButton: @Composable () -> Unit = {
        TextButton(onClick = onDismissRequest) {
            Text(text = stringResource(R.string.cancel))
        }
    },
    confirmButton: @Composable () -> Unit = {
        TextButton(onClick = onDismissRequest) {
            Text(text = stringResource(id = R.string.ok))
        }
    },
) {
    val coroutine = rememberCoroutineScope()
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(
        Context.CLIPBOARD_SERVICE
    ) as ClipboardManager
    val localConfiguration = LocalConfiguration.current

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            clipboardManager.primaryClip?.getItemAt(0)?.text?.toString()?.let {
                LazyColumn (
                    modifier = Modifier
                        .heightIn(max = (localConfiguration.screenHeightDp/2).dp)
                ){
                    item {
                        Text(
                            text = it
                        )
                    }
                }
            }
        },
        text = {
            var translation by remember {
                mutableStateOf(
                    TranslationResponse(
                        code = 200,
                        id = "",
                        data = "",
                        emptyList()
                    )
                )
            }
            DisposableEffect(Unit) {
                coroutine.launch {
                    val res = app.xlei.vipexam.core.network.module.NetWorkRepository.translateToZH(
                        text = clipboardManager.primaryClip?.getItemAt(0)?.text?.toString()!!
                    )
                    res.onSuccess {
                        translation = it
                    }.onFailure {
                        onDismissRequest.invoke()
                        Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
                onDispose {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        clipboardManager.clearPrimaryClip()
                    } else {
                        clipboardManager.setPrimaryClip(
                            ClipData.newPlainText("", "")
                        )
                    }
                }
            }
            LazyColumn {
                item {
                    Text(
                        text = translation.data,
                        fontSize = if (translation.alternatives.isNotEmpty()) 24.sp else TextUnit.Unspecified,
                    )
                }

                item {
                    LazyRow {
                        when {
                            translation.alternatives.isEmpty() && translation.data == "" -> {
                                item {
                                    Icon(
                                        imageVector = FeatherIcons.Loader,
                                        contentDescription = null,
                                    )
                                }
                            }

                            else -> {
                                items(translation.alternatives.size) {
                                    Text(
                                        text = translation.alternatives[it],
                                        modifier = Modifier.padding(end = 12.dp)
                                    )
                                }
                            }
                        }
                    }
                }

            }
        },
        dismissButton = dismissButton,
        confirmButton = confirmButton
    )
}
