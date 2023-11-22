package app.xlei.vipexam.ui.question.zread

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import app.xlei.vipexam.data.Muban
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ZreadViewModel: ViewModel(){
    private val _uiState = MutableStateFlow(ZreadUiState())
    val uiState: StateFlow<ZreadUiState> = _uiState.asStateFlow()

    @Composable
    fun init() {
        uiState.value.showBottomSheet = remember { mutableStateOf(false) }
        uiState.value.showQuestionsSheet = remember { mutableStateOf(false) }
        uiState.value.selectedChoiceIndex = remember { mutableStateOf(-1 to -1) }
    }

    fun setMuban(muban: Muban){
        _uiState.update {
            it.copy(
                muban = muban
            )
        }
    }

    @Composable
    fun setChoices() {
        _uiState.update {
            it.copy(
                choices = remember { mutableStateOf(getZreadChoices(it.muban!!.shiti)) }
            )
        }
    }

    fun setOptions() {
        _uiState.update {
            it.copy(
                options = getZreadOptions()
            )
        }
    }

    @Composable
    fun setQuestions() {
        _uiState.update {
            it.copy(
                questions = remember { mutableStateOf(getZreadQuestions(it.muban!!.shiti[0])) }
            )
        }
    }

}