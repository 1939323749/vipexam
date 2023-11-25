package app.xlei.vipexam.ui.question.translate

import android.transition.Transition.TransitionListener
import androidx.lifecycle.ViewModel
import app.xlei.vipexam.data.Muban
import app.xlei.vipexam.ui.question.writing.WritingUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TranslateViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(TranslateUiState(
        translations = emptyList()
    ))
    val uiState: StateFlow<TranslateUiState> = _uiState.asStateFlow()

    fun setMuban(muban: Muban){
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