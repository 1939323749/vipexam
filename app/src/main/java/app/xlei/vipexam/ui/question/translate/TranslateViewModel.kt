package app.xlei.vipexam.ui.question.translate

import androidx.lifecycle.ViewModel
import app.xlei.vipexam.data.Muban
import app.xlei.vipexam.ui.question.writing.WritingUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TranslateViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(WritingUiState())
    val uiState: StateFlow<WritingUiState> = _uiState.asStateFlow()

    fun setMuban(muban: Muban){
        _uiState.update {
            it.copy(
                muban = muban
            )
        }
    }
}