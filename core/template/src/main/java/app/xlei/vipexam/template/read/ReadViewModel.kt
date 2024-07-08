package app.xlei.vipexam.template.read

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
class ReadViewModel @Inject constructor(
    readUiState: ReadUiState,
) : ViewModel() {

    private val _uiState = MutableStateFlow(readUiState)
    val uiState: StateFlow<ReadUiState> = _uiState.asStateFlow()

    fun setMuban(muban: Muban) {
        _uiState.update {
            it.copy(
                muban = muban
            )
        }
    }

    @Composable
    fun SetArticles() {
        val articles = mutableListOf<ReadUiState.Article>()

        _uiState.collectAsState().value.muban!!.shiti.forEachIndexed { index, it ->
            articles.add(
                ReadUiState.Article(
                    index = "${index + 1}",
                    content = it.primQuestion,
                    questions = getQuestions(it.children).apply {
                        if (it.secondQuestion != null) {
                            add(
                                0, ReadUiState.Question(
                                    index = "1",
                                    question = it.secondQuestion!!,
                                    options = mutableListOf(
                                        ReadUiState.Option(index = "A", option = it.first),
                                        ReadUiState.Option(index = "B", option = it.second),
                                        ReadUiState.Option(index = "C", option = it.third),
                                        ReadUiState.Option(index = "D", option = it.fourth),
                                    ),
                                    choice = rememberSaveable { mutableStateOf("") },
                                    refAnswer = it.refAnswer,
                                    description = it.discription,
                                )
                            )
                        }
                    }
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
    private fun getQuestions(children: List<Children>): MutableList<ReadUiState.Question> {
        val questions = mutableListOf<ReadUiState.Question>()

        children.forEachIndexed { index, it ->
            val options = mutableListOf<String>()

            options.add(it.first)
            options.add(it.second)
            options.add(it.third)
            options.add(it.fourth)
            questions.add(
                ReadUiState.Question(
                    index = "${index + 2}",
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

    private fun getOptions(children: Children): MutableList<ReadUiState.Option> {
        val options = mutableListOf<ReadUiState.Option>()

        options.add(
            ReadUiState.Option(
                index = "A",
                option = children.first,
            )
        )
        options.add(
            ReadUiState.Option(
                index = "B",
                option = children.second,
            )
        )
        options.add(
            ReadUiState.Option(
                index = "C",
                option = children.third,
            )
        )
        options.add(
            ReadUiState.Option(
                index = "D",
                option = children.fourth,
            )
        )

        return options
    }

    fun setOption(selectedArticleIndex: Int, selectedQuestionIndex: Int, option: String) {
        _uiState.value.articles[selectedArticleIndex].questions[selectedQuestionIndex].choice.value =
            option
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