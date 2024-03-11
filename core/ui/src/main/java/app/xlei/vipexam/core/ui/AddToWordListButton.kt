package app.xlei.vipexam.core.ui

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import app.xlei.vipexam.core.ui.vm.AddToWordListButtonViewModel

/**
 * Add to word list button
 *
 * @param onClick 点击行为
 * @param viewModel 添加到单词表vm
 * @receiver
 */
@Composable
fun AddToWordListButton(
    onClick: () -> Unit,
    viewModel: AddToWordListButtonViewModel = hiltViewModel()
){
    val context = LocalContext.current
    val clipBoardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    TextButton(
        onClick = {
            viewModel.addToWordList(
                clipBoardManager.primaryClip?.getItemAt(0)?.text?.toString()!!
            )
            onClick.invoke()
        }
    ) {
        Text(stringResource(id = R.string.add_to_word_list))
    }
}
