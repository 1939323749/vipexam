package app.xlei.vipexam.ui.question.cloze

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
import kotlinx.coroutines.Dispatchers
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
    private var addToWordListState = AddToWordListState.PROCESSING

    fun setMuban(muban: Muban) {
        _uiState.update {
            it.copy(
                muban = muban
            )
        }
    }

    @Composable
    fun SetClozes(){
        val clozes = mutableListOf<ClozeUiState.Cloze>()

        _uiState.collectAsState().value.muban!!.shiti.forEach {
            clozes.add(
                ClozeUiState.Cloze(
                    article = getClickableArticle(it.primQuestion),
                    blanks = getBlanks(it),
                    options = getOptions(it.primQuestion),
                )
            )
        }

        _uiState.update {
            it.copy(
                clozes = clozes
            )
        }
        when (addToWordListState) {
            AddToWordListState.PROCESSING -> addToWordList()
            AddToWordListState.OK -> return
        }
    }

    private fun addToWordList(string: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            string?.let {
                repository.add(
                    Word(
                        word = it
                    )
                )
                return@launch
            }
            viewModelScope.launch(Dispatchers.IO) {
                _uiState.value.clozes.forEach { cloze ->
                    cloze.options.forEach { option ->
                        repository.add(
                            Word(
                                word = option.word
                            )
                        )
                    }
                }
                addToWordListState = AddToWordListState.OK
            }
        }
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

    private fun getOptions(text: String): List<ClozeUiState.Option>{
        val pattern = Regex("""([A-O])\)\s*([^A-O]+)""")
        val matches = pattern.findAll(text)
        val options = mutableListOf<ClozeUiState.Option>()

        for (match in matches) {
            val index = match.groupValues[1]
            val word = match.groupValues[2].trim()
            options.add(
                ClozeUiState.Option(
                    index = index,
                    word = word,
                )
            )
        }

        return options.sortedBy { it.index }
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

                pushStringAnnotation(tag = tag, annotation = tag )
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Unspecified)) {
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
        option: ClozeUiState.Option
    ) {
        _uiState.value.clozes[selectedClozeIndex].blanks[selectedQuestionIndex].choice.value =
            option.word
    }
}

enum class AddToWordListState {
    OK,
    PROCESSING
}