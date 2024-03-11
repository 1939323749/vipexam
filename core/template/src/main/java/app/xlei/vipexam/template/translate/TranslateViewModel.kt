package app.xlei.vipexam.template.translate

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
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

    fun setTranslations() {
        val translations = mutableListOf<TranslateUiState.Translation>()

        _uiState.value.muban!!.shiti.forEach {
            translations.add(
                TranslateUiState.Translation(
                    content = getContent(it.primQuestion),
                    sentences = getUnderlinedSentences(it.primQuestion)
                        .mapIndexed { index, sentence ->
                            TranslateUiState.Sentence(
                                index = index + 1,
                                sentence = sentence,
                                refAnswer = it.children[index].refAnswer
                            )
                        }
                )
            )
        }

        _uiState.update {
            it.copy(
                translations = translations
            )
        }
    }

    private fun getContent(content: String): AnnotatedString {

        val pattern = Regex("""<u>(.*?)<\/u>""")
        val matches = pattern.findAll(content)

        return buildAnnotatedString {
            var currentPosition = 0
            matches.forEach { match ->
                val startIndex = match.range.first
                val endIndex = match.range.last + 1
                append(content.substring(currentPosition, startIndex))
                withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append(
                        content.substring(startIndex, endIndex).removePrefix("<u>")
                            .removeSuffix("</u>")
                    )
                }
                currentPosition = endIndex
            }
        }
    }

    private fun getUnderlinedSentences(content: String) =
        Regex("""<u>(.*?)<\/u>""")
            .findAll(content)
            .map { it.value.removePrefix("<u>").removeSuffix("</u>") }
            .toList()

}