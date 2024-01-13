package app.xlei.vipexam.ui.question.qread

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.xlei.vipexam.core.data.repository.Repository
import app.xlei.vipexam.core.database.module.Word
import app.xlei.vipexam.data.Children
import app.xlei.vipexam.data.Muban
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QreadViewModel @Inject constructor(
    qreadUiState: QreadUiState,
    private val wordRepository: Repository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(qreadUiState)
    val uiState: StateFlow<QreadUiState> = _uiState.asStateFlow()

    fun setMuban(muban: Muban) {
        _uiState.update {
            it.copy(
                muban = muban
            )
        }
    }

    @Composable
    fun setArticles () {
        val articles = mutableListOf<QreadUiState.Article>()
        _uiState.value.muban!!.shiti.forEach {
            articles.add(
                QreadUiState.Article(
                    title = extractFirstPart(it.primQuestion),
                    content = extractSecondPart(it.primQuestion),
                    questions = getQuestions(it.children),
                    options = getOptions(it.primQuestion),
            ))
        }
        _uiState.update {
            it.copy(
                articles = articles
            )
        }
    }

    fun toggleBottomSheet(){
        _uiState.update {
            it.copy(
                showBottomSheet = !it.showBottomSheet
            )
        }
    }

    fun toggleOptionsSheet() {
        _uiState.update {
            it.copy(
                showOptionsSheet = !it.showOptionsSheet
            )
        }
    }

    fun setOption(selectedArticleIndex: Int, selectedQuestion: Int, option: String) {
        _uiState.value.articles[selectedArticleIndex].questions[selectedQuestion].choice.value = option
    }

    private fun getOptions(text: String): MutableList<QreadUiState.Option> {
        val result = mutableListOf<QreadUiState.Option>()
        var pattern = Regex("""([A-Z])([)])""")
        var matches = pattern.findAll(text)
        if (matches.count() < 10) {
            pattern = Regex("""([A-Z])(])""")
            matches = pattern.findAll(text)
        }
        for ((index, match) in matches.withIndex()) {
            result.add(
                QreadUiState.Option(
                    index = index + 1,
                    option = match.groupValues[1]
                )
            )
        }
        return result
    }

    @Composable
    private fun getQuestions(children: List<Children>): MutableList<QreadUiState.Question> {
        val questions = mutableListOf<QreadUiState.Question>()

        children.forEachIndexed {index,it->
            questions.add(
                QreadUiState.Question(
                    index = "${index + 1}",
                    question = it.secondQuestion,
                    choice = rememberSaveable { mutableStateOf("") },
                    refAnswer = it.refAnswer,
                    description = it.discription
                )
            )
        }

        return questions
    }
    private fun extractFirstPart(text: String): String {
        val lines = text.split("\n")
        if (lines.isNotEmpty()) {
            return lines.first().trim()
        }
        return ""
    }

    private fun extractSecondPart(text: String): String {
        val index = text.indexOf("\n")
        if (index != -1) {
            return text.substring(index + 1)
        }
        return ""
    }

    fun addToWordList(string: String) {
        viewModelScope.launch(Dispatchers.IO) {
            string.let {
                wordRepository.addWord(
                    Word(
                        word = it
                    )
                )
            }
        }
    }

}