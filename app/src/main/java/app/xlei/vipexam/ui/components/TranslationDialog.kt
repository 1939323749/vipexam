package app.xlei.vipexam.ui.components

import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.xlei.vipexam.R
import app.xlei.vipexam.core.network.module.TranslationResponse
import compose.icons.FeatherIcons
import compose.icons.feathericons.Loader
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun TranslateDialog(
    expanded: MutableState<Boolean>,
    onAddButtonClick: (String) -> Unit,
) {
    val coroutine = rememberCoroutineScope()
    val context = LocalContext.current
    val clipBoardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    DisposableEffect(key1 = Unit, effect = {
        onDispose { clipBoardManager.clearPrimaryClip() }
    })
    AlertDialog(
        onDismissRequest = { expanded.value = false },
        title = {
            clipBoardManager.primaryClip?.getItemAt(0)?.text?.toString()?.let { Text(it) }
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
                        text = clipBoardManager.primaryClip?.getItemAt(0)?.text?.toString()!!
                    )
                    res.onSuccess {
                        translation = it
                    }
                    res.onFailure {
                        expanded.value = false
                        Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
                onDispose {

                }
            }
            Column {
                Text(
                    text = translation.data,
                    fontSize = if (translation.alternatives.isNotEmpty()) 24.sp else TextUnit.Unspecified
                )

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
        },
        dismissButton = {
            TextButton(
                onClick = {
                    expanded.value = false
                }
            ) {
                Text(
                    stringResource(
                        R.string.cancel
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onAddButtonClick(clipBoardManager.primaryClip?.getItemAt(0)?.text?.toString()!!)
                    expanded.value = false
                }
            ) {
                Text(stringResource(id = R.string.add_to_word_list))
            }
        }
    )
}
