package app.xlei.vipexam.template.cloze

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.xlei.vipexam.core.data.repository.Repository
import app.xlei.vipexam.core.database.module.Word
import app.xlei.vipexam.core.network.module.getExamResponse.Muban
import app.xlei.vipexam.core.network.module.getExamResponse.Shiti
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClozeViewModel @Inject constructor(
    clozeUiState: ClozeUiState,
    private val repository: Repository<Word>,
) : ViewModel() {

    private val _uiState = MutableStateFlow(clozeUiState)
    val uiState: StateFlow<ClozeUiState> = _uiState.asStateFlow()

    fun setMuban(muban: Muban) {
        _uiState.update {
            it.copy(
                muban = muban
            )
        }
    }

    @Composable
    fun SetClozes() {
        val clozes = mutableListOf<ClozeUiState.Cloze>()

        uiState.collectAsState().value.muban!!.shiti.forEach {
            clozes.add(
                ClozeUiState.Cloze(
                    article = getClickableArticle(it.primQuestion),
                    blanks = getBlanks(it),
                    options = getOptions(it),
                )
            )
        }

        _uiState.update {
            it.copy(
                clozes = clozes
            )
        }
    }

    private fun getOptions(shiti: Shiti): List<ClozeUiState.Option> {
        val options = mutableListOf<ClozeUiState.Option>()
        shiti.children.forEach {
            options.add(
                ClozeUiState.Option(
                    it.secondQuestion,
                    listOf(
                        ClozeUiState.Word(
                            index = "A",
                            word = it.first
                        ),
                        ClozeUiState.Word(
                            index = "B",
                            word = it.second
                        ),
                        ClozeUiState.Word(
                            index = "C",
                            word = it.third
                        ),
                        ClozeUiState.Word(
                            index = "D",
                            word = it.fourth
                        ),
                    )
                )
            )
        }
        return options
    }

    @Composable
    private fun getBlanks(shiti: Shiti): MutableList<ClozeUiState.Blank> {
        val blanks = mutableListOf<ClozeUiState.Blank>()

        shiti.children.forEach {
            blanks.add(
                ClozeUiState.Blank(
                    index = it.secondQuestion,
                    choice = rememberSaveable { mutableStateOf("") },
                    refAnswer = it.refAnswer,
                    description = it.discription,
                )
            )
        }
        return blanks
    }

    private fun getClickableArticle(text: String): ClozeUiState.Article {
        val pattern = Regex("""C\d+""")
        val matches = pattern.findAll(text)

        val tags = mutableListOf<String>()

        val annotatedString = buildAnnotatedString {
            var currentPosition = 0
            for (match in matches) {
                val startIndex = match.range.first
                val endIndex = match.range.last + 1

                append(text.substring(currentPosition, startIndex))

                val tag = text.substring(startIndex, endIndex)

                pushStringAnnotation(tag = tag, annotation = tag)
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = Color.Unspecified
                    )
                ) {
                    append(text.substring(startIndex, endIndex))
                }
                pop()
                tags.add(tag)

                currentPosition = endIndex
            }

            if (currentPosition < text.length) {
                append(text.substring(currentPosition, text.length))
            }
        }

        return ClozeUiState.Article(
            article = annotatedString,
            tags = tags
        )
    }

    fun toggleBottomSheet() {
        _uiState.update {
            it.copy(
                showBottomSheet = !it.showBottomSheet
            )
        }
    }

    fun setOption(
        selectedClozeIndex: Int,
        selectedQuestionIndex: Int,
        option: String
    ) {
        _uiState.value.clozes[selectedClozeIndex].blanks[selectedQuestionIndex].choice.value =
            option
    }

    fun addToWordList(word: String) {
        viewModelScope.launch {
            repository.add(
                Word(
                    word = word
                )
            )
        }
    }
}