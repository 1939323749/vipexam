package app.xlei.vipexam.template.readCloze

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel
import app.xlei.vipexam.core.data.repository.Repository
import app.xlei.vipexam.core.database.module.Word
import app.xlei.vipexam.core.network.module.getExamResponse.Children
import app.xlei.vipexam.core.network.module.getExamResponse.Muban
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ReadClozeViewModel @Inject constructor(
    readClozeUiState: ReadClozeUiState,
    private val repository: Repository<Word>,
) : ViewModel() {

    private val _uiState = MutableStateFlow(readClozeUiState)
    val uiState: StateFlow<ReadClozeUiState> = _uiState.asStateFlow()

    fun setMuban(muban: Muban) {
        _uiState.update {
            it.copy(
                muban = muban
            )
        }
    }

    @Composable
    fun SetArticles() {
        val articles = mutableListOf<ReadClozeUiState.Article>()
        _uiState.collectAsState().value.muban!!.shiti.forEach {
            articles.add(
                ReadClozeUiState.Article(
                    content = it.primQuestion,
                    questions = getQuestions(it.children),
                    options = getOptions(it.primQuestion),
                )
            )
        }
        _uiState.update {
            it.copy(
                articles = articles
            )
        }
    }

    fun toggleBottomSheet() {
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
        _uiState.value.articles[selectedArticleIndex].questions[selectedQuestion].choice.value =
            option
    }

    private fun getOptions(text: String): MutableList<ReadClozeUiState.Option> {
        val result = mutableListOf<ReadClozeUiState.Option>()
        var pattern = Regex("""([A-Z])([)])""")
        var matches = pattern.findAll(text)
        if (matches.count() < 10) {
            pattern = Regex("""([A-Z])(])""")
            matches = pattern.findAll(text)
        }
        for ((index, match) in matches.withIndex()) {
            result.add(
                ReadClozeUiState.Option(
                    index = index + 1,
                    option = match.groupValues[1]
                )
            )
        }
        return result
    }

    @Composable
    private fun getQuestions(children: List<Children>): MutableList<ReadClozeUiState.Question> {
        val questions = mutableListOf<ReadClozeUiState.Question>()

        children.forEachIndexed { index, it ->
            questions.add(
                ReadClozeUiState.Question(
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
}