package app.xlei.vipexam.ui.question

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
            for ((n,t) in ti.children.withIndex()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable {
                            showOptionsSheet = true
                            selectedChoiceIndex = no to n
                        }
                ) {
                    Column (
                        modifier = Modifier
                            .padding(12.dp)
                    ){
                        Text((n + 1).toString())
                        Text("[A]" + t.first)
                        Text("[B]" + t.second)
                        Text("[C]" + t.third)
                        Text("[D]" + t.fourth)
                    }

                    if(getChoice(no,n,choices)!=null){
                        if (getChoice(no,n,choices)!!.value.second.second!=null){
                            SuggestionChip(
                                onClick = {},
                                label = {
                                    getChoice(no,n,choices)!!.value.second.second?.let { Text(it) }
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
        for ((i,_) in ti.children.withIndex()){
            choices.add(
                mutableStateOf(index to (i to null))
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