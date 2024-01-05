package app.xlei.vipexam.ui.question.listening

import android.media.MediaPlayer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TooltipState
import androidx.compose.runtime.MutableState
import app.xlei.vipexam.data.Muban

data class ListeningUiState(
    val muban: Muban?=null,
    var showOptionsSheet: Boolean=false,

    val listenings: List<Listening>,
){
    data class Listening(
        val originalText: String,
        val audioFile: String,
        val questions: List<Question>,
        val options: List<String>,
        val player: Player,
    )

    data class Question @OptIn(ExperimentalMaterial3Api::class) constructor(
        val index: String,
        val options: List<Option>,
        var choice: MutableState<String>,
        val refAnswer: String,
        val description: String,
        val tooltipState: TooltipState,
    )

    data class Option(
        val index: String,
        val option: String,
    )

    data class Player(
        val mediaPlayer: MediaPlayer,
        val prepared: MutableState<Boolean>,
    )
}
