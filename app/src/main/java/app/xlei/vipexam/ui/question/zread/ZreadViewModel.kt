package app.xlei.vipexam.ui.question.zread

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel
import app.xlei.vipexam.core.network.module.getExamResponse.Children
import app.xlei.vipexam.core.network.module.getExamResponse.Muban
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ZreadViewModel @Inject constructor(
    zreadUiState: ZreadUiState,
) : ViewModel() {

    private val _uiState = MutableStateFlow(zreadUiState)
    val uiState: StateFlow<ZreadUiState> = _uiState.asStateFlow()

    fun setMuban(muban: Muban) {
        _uiState.update {
            it.copy(
                muban = muban
            )
        }
    }

    @Composable
    fun SetArticles() {
        val articles = mutableListOf<ZreadUiState.Article>()

        _uiState.value.muban!!.shiti.forEachIndexed {index,it->
            articles.add(
                ZreadUiState.Article(
                    index = "${index + 1}",
                    content = it.primQuestion,
                    questions = getQuestions(it.children)
                )
            )
        }

        _uiState.update {
            it.copy(
                articles = articles
            )
        }
    }

    @Composable
    private fun getQuestions(children: List<Children>): MutableList<ZreadUiState.Question> {
        val questions = mutableListOf<ZreadUiState.Question>()

        children.forEachIndexed { index, it ->
            val options = mutableListOf<String>()

            options.add(it.first)
            options.add(it.second)
            options.add(it.third)
            options.add(it.fourth)
            questions.add(
                ZreadUiState.Question(
                    index = "${index + 1}",
                    question = it.secondQuestion,
                    options = getOptions(it),
                    choice = rememberSaveable { mutableStateOf("") },
                    refAnswer = it.refAnswer,
                    description = it.discription,
                )
            )
        }

        return questions
    }

    private fun getOptions(children: Children): MutableList<ZreadUiState.Option> {
        val options = mutableListOf<ZreadUiState.Option>()

        options.add(
            ZreadUiState.Option(
                index = "A",
                option = children.first,
            )
        )
        options.add(
            ZreadUiState.Option(
                index = "B",
                option = children.second,
            )
        )
        options.add(
            ZreadUiState.Option(
                index = "C",
                option = children.third,
            )
        )
        options.add(
            ZreadUiState.Option(
                index = "D",
                option = children.fourth,
            )
        )

        return options
    }

    fun setOption(selectedArticleIndex: Int, selectedQuestionIndex: Int, option: String) {
        _uiState.value.articles[selectedArticleIndex].questions[selectedQuestionIndex].choice.value = option
    }

    fun toggleBottomSheet() {
        _uiState.update {
            it.copy(
                showBottomSheet = !it.showBottomSheet
            )
        }
    }

    fun toggleQuestionsSheet() {
        _uiState.update {
            it.copy(
                showQuestionsSheet = !it.showQuestionsSheet
            )
        }
    }

}