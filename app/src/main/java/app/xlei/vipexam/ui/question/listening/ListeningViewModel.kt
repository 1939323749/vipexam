package app.xlei.vipexam.ui.question.listening

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import app.xlei.vipexam.data.Muban
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ListeningViewModel:ViewModel() {
    private val _uiState = MutableStateFlow(ListeningUiState())
    val uiState: StateFlow<ListeningUiState> = _uiState.asStateFlow()

    @Composable
    fun init() {
        uiState.value.showBottomSheet = remember { mutableStateOf(false) }
        uiState.value.showOptionsSheet = remember { mutableStateOf(false) }
        uiState.value.selectedChoiceIndex = remember { mutableStateOf(-1 to -1) }
    }

    fun setMuban(muban: Muban){
        _uiState.update {
            it.copy(
                muban = muban
            )
        }
    }

    fun setChoices() {
        _uiState.update {
            it.copy(
                choices = getListeningChoices(it.muban!!.shiti)
            )
        }
    }

    fun setOptions() {
        _uiState.update {
            it.copy(
                options = getListeningOptions()
            )
        }
    }
}