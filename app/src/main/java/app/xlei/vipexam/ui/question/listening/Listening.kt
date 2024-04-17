package app.xlei.vipexam.ui.question.listening

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.xlei.vipexam.core.network.module.getExamResponse.Muban
import app.xlei.vipexam.core.ui.VipexamArticleContainer
import app.xlei.vipexam.preference.LocalShowAnswer
import app.xlei.vipexam.preference.LocalVibrate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun ListeningView(
    submitMyAnswer: (String, String) -> Unit,
    muban: Muban,
    viewModel: ListeningViewModel = hiltViewModel(),
){
    viewModel.setMuban(muban)
    viewModel.SetListening()
    val showAnswer = LocalShowAnswer.current.isShowAnswer()
    val uiState by viewModel.uiState.collectAsState()
    val vibrate = LocalVibrate.current.isVibrate()

    var selectedQuestionIndex by remember { mutableIntStateOf(0) }
    val haptics = LocalHapticFeedback.current

    Listening(
        listenings = uiState.listenings,
        showOptionsSheet = uiState.showOptionsSheet,
        toggleOptionsSheet = { viewModel.toggleOptionsSheet() },
        onQuestionClick = {
            selectedQuestionIndex = it
            viewModel.toggleOptionsSheet()
            if (vibrate) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        },
        onOptionClick = { selectedListeningIndex, option ->
            if (selectedListeningIndex == 0)
                submitMyAnswer(muban.shiti[selectedListeningIndex].questionCode, option)
            else
                submitMyAnswer(
                    muban.shiti[selectedListeningIndex].children[selectedQuestionIndex - 1].questionCode,
                    option
                )
            viewModel.setOption(selectedListeningIndex, selectedQuestionIndex, option)
            viewModel.toggleOptionsSheet()
            if (vibrate) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        },
        showAnswer = showAnswer,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Listening(
    listenings: List<ListeningUiState.Listening>,
    showOptionsSheet: Boolean,
    toggleOptionsSheet: () -> Unit,
    onQuestionClick: (Int) -> Unit,
    onOptionClick: (Int, String) -> Unit,
    showAnswer: Boolean,
){
    var selectedListening by rememberSaveable { mutableIntStateOf(0) }
    val scrollState = rememberLazyListState()
    val coroutine = rememberCoroutineScope()


    DisposableEffect(Unit) {
        coroutine.launch {
            withContext(Dispatchers.IO){
                listenings.forEach{
                    it.player.mediaPlayer.setDataSource(it.audioFile)
                    it.player.mediaPlayer.prepare()
                    it.player.prepared.value = true
                }
            }
        }
        onDispose {
            listenings.forEach {
                it.player.mediaPlayer.release()
            }
        }
    }
    Column {
        LazyColumn(
            state = scrollState
        ) {
            items(listenings.size) {
                Text("${it + 1}")
                AudioPlayer(
                    mediaPlayer = listenings[it].player.mediaPlayer,
                    enabled = listenings[it].player.prepared.value
                )
                listenings[it].questions.forEachIndexed {index,question ->
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                        tooltip = {
                            if (showAnswer)
                                RichTooltip(
                                    title = { Text(question.index + question.refAnswer) },
                                    modifier = Modifier
                                        .padding(top = 100.dp, bottom = 100.dp)
                                ) {
                                    LazyColumn {
                                        item { Text(listenings[it].originalText) }
                                    }
                                }
                        },
                        state = question.tooltipState
                    ) {
                        VipexamArticleContainer(
                            onDragContent = listenings[selectedListening].originalText
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .clickable {
                                        selectedListening = it
                                        onQuestionClick(index)
                                    }
                            ) {
                                Text(question.index)
                                Column (
                                    modifier = Modifier
                                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                ){
                                    question.options.forEach { option ->
                                        Text("${option.index}. " + option.option)
                                    }
                                    if (showAnswer) {
                                        Spacer(
                                            Modifier
                                                .fillMaxWidth()
                                                .height(24.dp)
                                        )
                                        Text(
                                            text = question.description,
                                            modifier = Modifier
                                                .padding(horizontal = 24.dp)
                                        )
                                    }
                                }
                                if (question.choice.value!="")
                                    SuggestionChip(
                                        onClick = {},
                                        label = {
                                            Text(question.choice.value)
                                        }
                                    )
                            }
                        }
                    }
                }

            }
        }


        if(showOptionsSheet){
            ModalBottomSheet(
                onDismissRequest = toggleOptionsSheet
            ){
                listenings[selectedListening].options.forEach {
                    SuggestionChip(
                        onClick = {
                            onOptionClick(selectedListening,it) },
                        label = {
                            Text(it)
                        }
                    )
                }
            }
        }
    }
}

@Stable
@Composable
fun AudioPlayer(
    mediaPlayer: MediaPlayer,
    enabled: Boolean,
) {
    val vibrate = LocalVibrate.current.isVibrate()
    var isPlaying by rememberSaveable { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current

    Column {
        Button(
            onClick = {
                isPlaying = !isPlaying
                if (isPlaying) {
                    mediaPlayer.start()
                } else {
                    mediaPlayer.pause()
                }
                if (vibrate) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            },
            enabled = enabled
        ) {
            Text(if (isPlaying) "Pause" else "Play")
        }
    }
}