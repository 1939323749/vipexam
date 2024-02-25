package app.xlei.vipexam.ui.question.translate

import androidx.lifecycle.ViewModel
import app.xlei.vipexam.core.network.module.getExamResponse.Muban
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TranslateViewModel @Inject constructor(
    translateUiState: TranslateUiState
) : ViewModel() {
    private val _uiState = MutableStateFlow(translateUiState)
    val uiState: StateFlow<TranslateUiState> = _uiState.asStateFlow()

    fun setMuban(muban: Muban) {
        _uiState.update {
            it.copy(
                muban = muban
            )
        }
    }

    fun setTranslations(){
        val translations = mutableListOf<TranslateUiState.Translation>()

        _uiState.value.muban!!.shiti.forEach {
            translations.add(
                TranslateUiState.Translation(
                    question = it.primQuestion,
                    refAnswer = it.refAnswer,
                    description = it.discription,
                )
            )
        }

        _uiState.update {
            it.copy(
                translations = translations
            )
        }
    }
}