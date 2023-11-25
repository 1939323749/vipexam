package app.xlei.vipexam.ui.question.listening

import android.media.MediaPlayer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.xlei.vipexam.data.Muban
import app.xlei.vipexam.data.Shiti
import app.xlei.vipexam.ui.question.zread.getChoice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
    viewModel.init()
    viewModel.setMuban(muban)
    viewModel.setChoices()
    viewModel.setOptions()

    val uiState by viewModel.uiState.collectAsState()
    listening(
        muban = muban,
        showOptionsSheet = uiState.showOptionsSheet!!,
        options = uiState.options!!,
        selectedChoiceIndex = uiState.selectedChoiceIndex!!,
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
    muban: Muban,
    showOptionsSheet: MutableState<Boolean>,
    options: List<String>,
    selectedChoiceIndex: MutableState<Pair<Int, Int>>,
    onFirstItemHidden: (String) -> Unit,
    onFirstItemAppear: ()->Unit,
    showAnswer: MutableState<Boolean>,
){
    val choices by remember { mutableStateOf(getListeningChoices(muban.shiti)) }
    val mediaPlayers = mutableListOf<Pair<MediaPlayer,String>>()
    muban.shiti.forEach {
        mediaPlayers.add(remember { MediaPlayer() } to it.audioFiles)
    }

    val scrollState = rememberLazyListState()
    val firstVisibleItemIndex by remember { derivedStateOf { scrollState.firstVisibleItemIndex } }
    val tooltipStates = getListeningToolTipStates(muban.shiti)
    val coroutine = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current

    DisposableEffect(Unit) {
        coroutine.launch {
            delay(500)
            withContext(Dispatchers.IO){
                mediaPlayers.forEach{
                    it.first.setDataSource("https://rang.vipexam.org/Sound/${it.second}.mp3")
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
                    muban.cname,
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

            items(muban.shiti.size) {
                Text("${it + 1}")
                AudioPlayer(mediaPlayers[it].first)
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                    tooltip = {
                        if (showAnswer.value)
                            RichTooltip(
                                title = { Text("1" + muban.shiti[0].refAnswer) },
                                modifier = Modifier
                                    .padding(top = 100.dp, bottom = 100.dp)
                            ){
                                LazyColumn {
                                    item { Text(muban.shiti[it].originalText) }
                                }
                            }
                    },
                    state = getListeningToolTipState(it,0,tooltipStates)!!.second.second
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable {
                                //onQuestionClick()
                                showOptionsSheet.value = true
                                selectedChoiceIndex.value= it to 0
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                    ) {
                        Column (
                            modifier = Modifier
                                .padding(12.dp)
                        ){
                            Text(
                                text = "${1}",
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "[A]" + muban.shiti[it].first,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "[B]" + muban.shiti[it].second,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "[C]" + muban.shiti[it].third,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "[D]" + muban.shiti[it].fourth,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        if(getChoice(it,0,choices) !=null){
                            if (getChoice(it,0,choices)!!.value.second.second!=null){
                                SuggestionChip(
                                    onClick = {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    },
                                    label = {
                                        getChoice(it,0,choices)!!.value.second.second?.let { choice-> Text(choice) }
                                    }
                                )
                            }
                        }
                    }
                }

                for ((n,t) in muban.shiti[it].children.withIndex()) {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                        tooltip = {
                            if (showAnswer.value)
                                RichTooltip(
                                    title = { Text("${n  + 2}" + t.refAnswer) },
                                    modifier = Modifier
                                        .padding(top = 100.dp, bottom = 100.dp)
                                ){
                                    LazyColumn {
                                        item { Text(muban.shiti[it].originalText) }
                                    }
                                }
                        },
                        state = getListeningToolTipState(it, n + 1 ,tooltipStates)!!.second.second,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .clickable {
                                    showOptionsSheet.value = true
                                    selectedChoiceIndex.value = it to n + 1
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "${n + 2}",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                                Text(
                                    text = "[A]" + t.first,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "[B]" + t.second,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "[C]" + t.third,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "[D]" + t.fourth,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            if (getChoice(it, n + 1, choices) != null) {
                                if (getChoice(it, n + 1, choices)!!.value.second.second != null) {
                                    SuggestionChip(
                                        onClick = {
                                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        },
                                        label = {
                                            getChoice(it, n + 1, choices)!!.value.second.second?.let { choice ->
                                                Text(
                                                    choice
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }





        }


        if(showOptionsSheet.value){
            ModalBottomSheet(
                onDismissRequest = {
                    showOptionsSheet.value = false
                }
            ){
                for (option in options){
                    SuggestionChip(
                        onClick = {
                            if(selectedChoiceIndex.value!=-1 to -1){
                                for(choice in  choices){
                                    if(choice.value.first==selectedChoiceIndex.value.first){
                                        if(choice.value.second.first==selectedChoiceIndex.value.second){
                                            choice.value=choice.value.first to (choice.value.second.first to option)
                                        }
                                    }
                                }
                            }
                            showOptionsSheet.value = false
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        label = {
                            Text(option)
                        }
                    )
                }
            }
        }

        if(firstVisibleItemIndex>0){
            onFirstItemHidden(muban.cname)
        }else{
            onFirstItemAppear()
        }
    }
}

fun getListeningChoices(shiti: List<Shiti>): MutableList<MutableState<Pair<Int, Pair<Int, String?>>>> {
    val choices = mutableListOf<MutableState<Pair<Int,Pair<Int, String?>>>>()

    for((index,ti) in shiti.withIndex()){
        choices.add(
            mutableStateOf(index to (0 to null))
        )
        for ((i,_) in ti.children.withIndex()){
            choices.add(
                mutableStateOf(index to (i+1 to null))
            )
        }
    }

    return choices
}

fun getListeningOptions(): List<String> {
    val options = mutableListOf<String>()

    options.add("A")
    options.add("B")
    options.add("C")
    options.add("D")

    return options
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun getListeningToolTipStates(shiti: List<Shiti>): MutableList<Pair<Int, Pair<Int, TooltipState>>> {
    val toolTipStates = mutableListOf<Pair<Int,Pair<Int,TooltipState>>>()

    for((index,ti) in shiti.withIndex()){
        toolTipStates.add(
            index to (0 to rememberTooltipState(isPersistent = true))
        )
        for ((i,_) in ti.children.withIndex()){
            toolTipStates.add(
                index to (i+1 to rememberTooltipState(isPersistent = true))
            )
        }
    }

    return toolTipStates
}

@OptIn(ExperimentalMaterial3Api::class)
fun getListeningToolTipState(index:Int, i:Int, toolTipStates: MutableList<Pair<Int, Pair<Int, TooltipState>>>): Pair<Int, Pair<Int, TooltipState>>? {
    for(toolTipState in toolTipStates){
        if(toolTipState.first==index){
            if(toolTipState.second.first==i){
                return toolTipState
            }
        }
    }
    return null
}