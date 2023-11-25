package app.xlei.vipexam.ui.question.listening

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.xlei.vipexam.data.Muban
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun listeningView(
    muban: Muban,
    viewModel: ListeningViewModel = viewModel(),
    onFirstItemHidden: (String) -> Unit,
    onFirstItemAppear: ()->Unit,
    showAnswer: MutableState<Boolean>,
){
    viewModel.setMuban(muban)
    viewModel.setListenings()

    val uiState by viewModel.uiState.collectAsState()
    var selectedQuestionIndex by remember { mutableStateOf(0) }
    val haptics = LocalHapticFeedback.current

    listening(
        name = uiState.muban!!.cname,
        listenings = uiState.listenings,
        showOptionsSheet = uiState.showOptionsSheet,
        toggleOptionsSheet = { viewModel.toggleOptionsSheet() },
        onQuestionClick = {
            selectedQuestionIndex = it
            viewModel.toggleOptionsSheet()
        },
        onOptionClick = {selectedListeningIndex,option->
            viewModel.setOption(selectedListeningIndex,selectedQuestionIndex,option)
            viewModel.toggleOptionsSheet()
        },
        onFirstItemHidden = {
            onFirstItemHidden(it)
        },
        onFirstItemAppear = {
            onFirstItemAppear()
        },
        showAnswer = showAnswer,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun listening(
    name: String,
    listenings: List<ListeningUiState.Listening>,
    showOptionsSheet: Boolean,
    toggleOptionsSheet: () -> Unit,
    onQuestionClick: (Int) -> Unit,
    onOptionClick: (Int,String) -> Unit,
    onFirstItemHidden: (String) -> Unit,
    onFirstItemAppear: ()->Unit,
    showAnswer: MutableState<Boolean>,
){
    val mediaPlayers = mutableListOf<Pair<MediaPlayer,ListeningUiState.Listening>>()
    var selectedListening by rememberSaveable { mutableStateOf(0) }

    listenings.forEach {
        mediaPlayers.add(remember { MediaPlayer() } to it)
    }

    val scrollState = rememberLazyListState()
    val firstVisibleItemIndex by remember { derivedStateOf { scrollState.firstVisibleItemIndex } }
    val coroutine = rememberCoroutineScope()


    DisposableEffect(Unit) {
        coroutine.launch {
            withContext(Dispatchers.IO){
                mediaPlayers.forEach{
                    it.first.setDataSource(it.second.audioFile)
                    it.first.prepare()
                }
            }
        }
        onDispose {
            mediaPlayers.forEach {
                it.first.release()
            }
        }
    }
    Column {
        LazyColumn(
            state = scrollState
        ) {
            item{
                Text(
                    name,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .padding(start = 12.dp)
                )
                HorizontalDivider(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 12.dp),
                    thickness = 1.dp,
                    color = Color.Gray
                )
            }

            items(listenings.size) {
                Text("${it + 1}")
                AudioPlayer(mediaPlayers[it].first)
                listenings[it].questions.forEachIndexed {index,question ->
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                        tooltip = {
                            if (showAnswer.value)
                                RichTooltip(
                                    title = { Text(question.index+question.refAnswer) },
                                    modifier = Modifier
                                        .padding(top = 100.dp, bottom = 100.dp)
                                ){
                                    LazyColumn {
                                        item { Text(listenings[it].originalText) }
                                    }
                                }
                        },
                        state = question.tooltipState
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .clickable {
                                    selectedListening = it
                                    onQuestionClick(index)
                                }
                        ) {
                            Text(question.index)
                            Column (
                                modifier = Modifier
                                    .padding(12.dp)
                            ){
                                question.options.forEach {option->
                                    Text("${option.index}. "+option.option)
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

        if (firstVisibleItemIndex > 0)
            onFirstItemHidden(name)
        else
            onFirstItemAppear()

    }
}

@Stable
@Composable
fun AudioPlayer(
    mediaPlayer: MediaPlayer
) {
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
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        ) {
            Text(if (isPlaying) "Pause" else "Play")
        }
    }
}