package app.xlei.vipexam.ui.components

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import app.xlei.vipexam.R
import app.xlei.vipexam.core.data.repository.Repository
import app.xlei.vipexam.core.database.module.Word
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

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

@HiltViewModel
class AddToWordListButtonViewModel @Inject constructor(
    private val wordRepository: Repository<Word>
) : ViewModel() {
    fun addToWordList(word: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                wordRepository.add(
                    Word(
                        word = word
                    )
                )
            }
        }
    }
}