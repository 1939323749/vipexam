package app.xlei.vipexam.core.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.xlei.vipexam.core.data.repository.Repository
import app.xlei.vipexam.core.database.module.Word
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddToWordListButtonViewModel @Inject constructor(
    private val wordRepository: Repository<Word>
) : ViewModel() {
    fun addToWordList(word: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                wordRepository.add(
                    Word(
                        word = word
                    )
                )
            }
        }
    }
}