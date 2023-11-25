package app.xlei.vipexam.ui.question.listening

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel
import app.xlei.vipexam.data.Children
import app.xlei.vipexam.data.Muban
import app.xlei.vipexam.data.Shiti
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ListeningViewModel:ViewModel() {
    private val _uiState = MutableStateFlow(ListeningUiState(
        listenings = emptyList(),
    ))
    val uiState: StateFlow<ListeningUiState> = _uiState.asStateFlow()

    fun setMuban(muban: Muban){
        _uiState.update {
            it.copy(
                muban = muban
            )
        }
    }

    @Composable
    fun setListenings() {
        _uiState.update {
            it.copy(
                listenings = getListenings()
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

    fun setOption(selectedListeningIndex: Int,selectedQuestionIndex: Int, option: String){
        Log.d("",selectedQuestionIndex.toString())
        _uiState.value.listenings[selectedListeningIndex].questions[selectedQuestionIndex].choice.value = option
    }

    @Composable
    private fun getListenings(): MutableList<ListeningUiState.Listening> {
        val listenings = mutableListOf<ListeningUiState.Listening>()

        _uiState.value.muban!!.shiti.forEach {
            listenings.add(
                ListeningUiState.Listening(
                    originalText = it.originalText,
                    audioFile = "https://rang.vipexam.org/Sound/${it.audioFiles}.mp3",
                    questions = getQuestions(it),
                    options = listOf("A","B","C","D"),
                )
            )
        }

        return listenings
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun getQuestions(shiti: Shiti): MutableList<ListeningUiState.Question> {
        val questions = mutableListOf<ListeningUiState.Question>()

        questions.add(
            ListeningUiState.Question(
                index = "1",
                options = listOf(
                    ListeningUiState.Option(
                        index = "A",
                        option = shiti.first,
                    ),
                    ListeningUiState.Option(
                        index = "B",
                        option = shiti.second,
                    ),
                    ListeningUiState.Option(
                        index = "C",
                        option = shiti.third,
                    ),
                    ListeningUiState.Option(
                        index = "D",
                        option = shiti.fourth,
                    )
                ),
                choice = rememberSaveable { mutableStateOf("") },
                refAnswer = shiti.refAnswer,
                tooltipState = rememberTooltipState(isPersistent = true)
            )
        )
        shiti.children.forEachIndexed {index,it->
            questions.add(
                ListeningUiState.Question(
                    index = "${index + 2}",
                    options = getOptions(it),
                    choice = rememberSaveable { mutableStateOf("") },
                    refAnswer = it.refAnswer,
                    tooltipState = rememberTooltipState(isPersistent = true)
                )
            )
        }

        return questions
    }

    private fun getOptions(children: Children): MutableList<ListeningUiState.Option> {
        val options = mutableListOf<ListeningUiState.Option>()

        options.add(
            ListeningUiState.Option(
                index = "A",
                option = children.first,
            )
        )
        options.add(
            ListeningUiState.Option(
                index = "B",
                option = children.second,
            )
        )
        options.add(
            ListeningUiState.Option(
                index = "C",
                option = children.third,
            )
        )
        options.add(
            ListeningUiState.Option(
                index = "D",
                option = children.fourth,
            )
        )

        return options
    }
}