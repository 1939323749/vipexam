package app.xlei.vipexam.ui.question

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.xlei.vipexam.data.Muban
import app.xlei.vipexam.data.Shiti

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Eylhlisteninga(muban: Muban){
    var showOptionsSheet by remember { mutableStateOf(false) }
    val choices by remember { mutableStateOf(getListeningChoices(muban.shiti)) }
    val options = getListeningOptions()
    var selectedChoiceIndex by remember { mutableStateOf(-1 to -1) }

    Column {
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
        for ((no,ti) in muban.shiti.withIndex()){
            Text((no+1).toString())
            AudioPlayer("https://rang.vipexam.org/Sound/${ti.audioFiles}.mp3")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable {
                        showOptionsSheet = true
                        selectedChoiceIndex = no to 0
                    }
            ) {
                Column (
                    modifier = Modifier
                        .padding(12.dp)
                ){
                    Text("${1}")
                    Text("[A]" + ti.first)
                    Text("[B]" + ti.second)
                    Text("[C]" + ti.third)
                    Text("[D]" + ti.fourth)
                }

                if(getChoice(no,0,choices)!=null){
                    if (getChoice(no,0,choices)!!.value.second.second!=null){
                        SuggestionChip(
                            onClick = {},
                            label = {
                                getChoice(no,0,choices)!!.value.second.second?.let { Text(it) }
                            }
                        )
                    }
                }
            }
            for ((n,t) in ti.children.withIndex()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable {
                            showOptionsSheet = true
                            selectedChoiceIndex = no to n+1
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

                    if(getChoice(no,n+1,choices)!=null){
                        if (getChoice(no,n+1,choices)!!.value.second.second!=null){
                            SuggestionChip(
                                onClick = {},
                                label = {
                                    getChoice(no,n+1,choices)!!.value.second.second?.let { Text(it) }
                                }
                            )
                        }
                    }
                }
            }
        }

        if(showOptionsSheet){
            ModalBottomSheet(
                onDismissRequest = {
                    showOptionsSheet = false
                }
            ){
                for (option in options){
                    SuggestionChip(
                        onClick = {
                            if(selectedChoiceIndex!=-1 to -1){
                                for(choice in  choices){
                                    if(choice.value.first==selectedChoiceIndex.first){
                                        if(choice.value.second.first==selectedChoiceIndex.second){
                                            choice.value=choice.value.first to (choice.value.second.first to option)
                                        }
                                    }
                                }
                            }
                            showOptionsSheet = false
                        },
                        label = {
                            Text(option)
                        }
                    )
                }
            }
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
fun AudioPlayer(audioUrl: String) {
    var isPlaying by remember { mutableStateOf(false) }
    val mediaPlayer = remember { MediaPlayer() }

    DisposableEffect(Unit) {
        mediaPlayer.setDataSource(audioUrl)
        mediaPlayer.prepare()

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