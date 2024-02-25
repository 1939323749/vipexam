package app.xlei.vipexam.ui.question.listening

import android.media.MediaPlayer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel
import app.xlei.vipexam.core.network.module.getExamResponse.Children
import app.xlei.vipexam.core.network.module.getExamResponse.Muban
import app.xlei.vipexam.core.network.module.getExamResponse.Shiti
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ListeningViewModel @Inject constructor(
    listeningUiState: ListeningUiState
) : ViewModel() {
    private val _uiState = MutableStateFlow(listeningUiState)
    val uiState: StateFlow<ListeningUiState> = _uiState.asStateFlow()

    fun setMuban(muban: Muban) {
        _uiState.update {
            it.copy(
                muban = muban
            )
        }
    }

    @Composable
    fun SetListening() {
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
                    player = getPlayers()
                )
            )
        }

        return listenings
    }

    @Composable
    fun getPlayers(): ListeningUiState.Player {
        return ListeningUiState.Player(
            mediaPlayer = remember { MediaPlayer() },
            prepared = rememberSaveable { mutableStateOf(false) }
        )
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
                tooltipState = rememberTooltipState(isPersistent = true),
                description = shiti.discription,
            )
        )
        shiti.children.forEachIndexed {index,it->
            questions.add(
                ListeningUiState.Question(
                    index = "${index + 2}",
                    options = getOptions(it),
                    choice = rememberSaveable { mutableStateOf("") },
                    refAnswer = it.refAnswer,
                    tooltipState = rememberTooltipState(isPersistent = true),
                    description = it.discription,
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