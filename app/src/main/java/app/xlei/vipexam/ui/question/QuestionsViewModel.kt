package app.xlei.vipexam.ui.question

import androidx.lifecycle.ViewModel
import app.xlei.vipexam.data.ExamUiState
import app.xlei.vipexam.data.Muban
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class QuestionsViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(QuestionsUiState())
    val uiState: StateFlow<QuestionsUiState> = _uiState.asStateFlow()

    fun setMubanList(mubanList: List<Muban>){
        _uiState.update {
            it.copy(
                mubanList = mubanList
            )
        }
    }
}