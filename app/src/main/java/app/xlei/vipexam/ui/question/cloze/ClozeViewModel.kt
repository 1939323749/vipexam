package app.xlei.vipexam.ui.question.cloze

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import app.xlei.vipexam.data.ExamUiState
import app.xlei.vipexam.data.Muban
import app.xlei.vipexam.ui.question.cloze.getClozeChoices
import app.xlei.vipexam.ui.question.cloze.getClozeOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class ClozeViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(ClozeUiState())
    val uiState: StateFlow<ClozeUiState> = _uiState.asStateFlow()

    @Composable
    fun init() {
        uiState.value.showBottomSheet = remember { mutableStateOf(false) }
        uiState.value.showOptionsSheet = remember { mutableStateOf(false) }
        uiState.value.selectedChoiceIndex = remember { mutableStateOf(-1) }
    }
    fun setMuban(muban: Muban){
        _uiState.update {
            it.copy(
                muban = muban
            )
        }
    }

    fun setOptions(){
        _uiState.update {
            it.copy(
                options = getClozeOptions(
                    text = _uiState.value.muban!!.shiti[0].primQuestion
                )
            )
        }
    }

    @Composable
    fun setChoices(){
        _uiState.update {
            it.copy(
                choices = remember { getClozeChoices(_uiState.value.muban!!.shiti[0].children) }
            )
        }
    }

}