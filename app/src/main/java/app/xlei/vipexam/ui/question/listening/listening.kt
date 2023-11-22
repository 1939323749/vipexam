package app.xlei.vipexam.ui.question.listening

import android.media.MediaPlayer
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.xlei.vipexam.data.Muban
import app.xlei.vipexam.data.Shiti
import app.xlei.vipexam.ui.question.zread.getChoice
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun listeningView(
    muban: Muban,
    viewModel: ListeningViewModel = viewModel(),
    onFirstItemHidden: (String) -> Unit,
    onFirstItemAppear: ()->Unit
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
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun listening(
    muban: Muban,
    showOptionsSheet: MutableState<Boolean>,
    options: List<String>,
    selectedChoiceIndex: MutableState<Pair<Int, Int>>,
    onFirstItemHidden: (String) -> Unit,
    onFirstItemAppear: ()->Unit
){
    val choices by remember { mutableStateOf(getListeningChoices(muban.shiti)) }
    val scrollState = rememberLazyListState()
    val firstVisibleItemIndex by remember { derivedStateOf { scrollState.firstVisibleItemIndex } }

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

            items(muban.shiti.size){
                Text((it+1).toString())
                AudioPlayer("https://rang.vipexam.org/Sound/${muban.shiti[it].audioFiles}.mp3")
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
                        }
                ) {
                    Column (
                        modifier = Modifier
                            .padding(12.dp)
                    ){
                        Text("${1}")
                        Text("[A]" + muban.shiti[it].first)
                        Text("[B]" + muban.shiti[it].second)
                        Text("[C]" + muban.shiti[it].third)
                        Text("[D]" + muban.shiti[it].fourth)
                    }

                    if(getChoice(it,0,choices) !=null){
                        if (getChoice(it,0,choices)!!.value.second.second!=null){
                            SuggestionChip(
                                onClick = {},
                                label = {
                                    getChoice(it,0,choices)!!.value.second.second?.let { choice-> Text(choice) }
                                }
                            )
                        }
                    }
                }
                for ((n,t) in muban.shiti[it].children.withIndex()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable {
                                showOptionsSheet.value = true
                                selectedChoiceIndex.value = it to n+1
                            }
                    ) {
                        Column (
                            modifier = Modifier
                                .padding(12.dp)
                        ){
                            Text((n + 2).toString())
                            Text("[A]" + t.first)
                            Text("[B]" + t.second)
                            Text("[C]" + t.third)
                            Text("[D]" + t.fourth)
                        }

                        if(getChoice(it,n+1,choices) !=null){
                            if (getChoice(it,n+1,choices)!!.value.second.second!=null){
                                SuggestionChip(
                                    onClick = {},
                                    label = {
                                        getChoice(it,n+1,choices)!!.value.second.second?.let { choice-> Text(choice) }
                                    }
                                )
                            }
                        }
                    }
                }

                Text("1"+muban.shiti[it].refAnswer)
                for((no,children) in muban.shiti[it].children.withIndex()){
                    Text("${no + 2}"+children.refAnswer)
                }
                Text(muban.shiti[it].originalText)
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

@Composable
fun AudioPlayer(
    audioUrl: String
) {
    var isPlaying by remember { mutableStateOf(false) }
    val mediaPlayer = remember { MediaPlayer() }
    val coroutine = rememberCoroutineScope()

    DisposableEffect(Unit) {
        coroutine.launch {
            delay(500)
            mediaPlayer.setDataSource(audioUrl)
            mediaPlayer.prepare()
        }
        onDispose {
            mediaPlayer.release()
        }
    }

    Column {
        Button(
            onClick = {
                isPlaying = !isPlaying
                if (isPlaying) {
                    mediaPlayer.start()
                } else {
                    mediaPlayer.pause()
                }
            }
        ) {
            Text(if (isPlaying) "Pause" else "Play")
        }
    }
}