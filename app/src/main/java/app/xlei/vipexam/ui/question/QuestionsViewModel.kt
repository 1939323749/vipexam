package app.xlei.vipexam.ui.question

import androidx.lifecycle.ViewModel
import app.xlei.vipexam.core.network.module.getExamResponse.Muban
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class QuestionsViewModel @Inject constructor(
    questionsUiState: QuestionsUiState
) : ViewModel() {
    private val _uiState = MutableStateFlow(questionsUiState)
    val uiState: StateFlow<QuestionsUiState> = _uiState.asStateFlow()

    fun setMubanList(mubanList: List<Muban>) {
        _uiState.update {
            it.copy(
                mubanList = mubanList
            )
        }
    }
}