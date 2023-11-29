package app.xlei.vipexam.ui.question.writing

import androidx.lifecycle.ViewModel
import app.xlei.vipexam.data.Muban
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class WritingViewModel @Inject constructor(
    private val muban: Muban
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        WritingUiState(
            writings = emptyList()
        )
    )
    val uiState: StateFlow<WritingUiState> = _uiState.asStateFlow()

    fun setMuban(muban: Muban) {
        _uiState.update {
            it.copy(
                muban = muban // function scope
            )
        }
    }

    fun setWritings(){
        val writings = mutableListOf<WritingUiState.Writing>()

        _uiState.value.muban!!.shiti.forEach {
            writings.add(
                WritingUiState.Writing(
                    question = it.primQuestion,
                    refAnswer = it.refAnswer,
                )
            )
        }
        _uiState.update {
            it.copy(
                writings = writings
            )
        }
    }
}
