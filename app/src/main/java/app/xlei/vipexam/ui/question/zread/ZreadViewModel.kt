package app.xlei.vipexam.ui.question.zread

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel
import app.xlei.vipexam.data.Children
import app.xlei.vipexam.data.Muban
import app.xlei.vipexam.ui.page.getQuestions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ZreadViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(
        ZreadUiState(
            articles = emptyList()
        )
    )
    val uiState: StateFlow<ZreadUiState> = _uiState.asStateFlow()

    fun setMuban(muban: Muban) {
        _uiState.update {
            it.copy(
                muban = muban
            )
        }
    }

    @Composable
    fun setArticles() {
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
                option = children.third,
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