package app.xlei.vipexam.ui.question.qread

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import app.xlei.vipexam.data.Muban
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class QreadViewModel : ViewModel(){
    private val _uiState = MutableStateFlow(QreadUiState())
    val uiState: StateFlow<QreadUiState> = _uiState.asStateFlow()

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

    fun setTitle() {
        _uiState.update {
            it.copy(
                title = extractFirstPart(
                    text = it.muban!!.shiti[0].primQuestion
                ).trim()
            )
        }
    }

    fun setArticle() {
        _uiState.update {
            it.copy(
                article = extractSecondPart(
                    text = it.muban!!.shiti[0].primQuestion
                )
            )
        }
    }

    @Composable
    fun setChoices() {
        _uiState.update {
            it.copy(
                choices = remember { mutableStateOf(getQreadChoices(it.muban!!.shiti[0].children)) }
            )
        }
    }

    fun setOptions() {
        _uiState.update {
            it.copy(
                options = getQreadOptions(it.article!!)
            )
        }
    }

    fun setChoice(option: String) {
        _uiState.update { uiState ->
            val selectedChoiceIndex = uiState.selectedChoiceIndex?.value ?: return@update uiState
            val newChoices = uiState.choices

            if (selectedChoiceIndex >= 0 && selectedChoiceIndex < newChoices!!.value.size) {
                val pair = newChoices.value[selectedChoiceIndex].value
                newChoices.value[selectedChoiceIndex].value = pair.first to option
            }

            uiState.copy(choices = newChoices)
        }
    }

}