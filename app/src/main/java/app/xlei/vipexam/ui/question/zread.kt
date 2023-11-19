package app.xlei.vipexam.ui.question

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.xlei.vipexam.data.Children
import app.xlei.vipexam.data.Muban
import app.xlei.vipexam.data.Shiti

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Ecszread(muban: Muban){
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showQuestionsSheet by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current

    var questions by remember { mutableStateOf(getQuestions(muban.shiti[0])) }
    val options = getZreadOptions()
    val choices by remember { mutableStateOf(getZreadChoices(muban.shiti)) }
    var selectedChoiceIndex by remember { mutableStateOf(-1 to -1) }

    Column {
        Column{
            Text(
                muban.cname,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(start = 12.dp)
            )
        }
        HorizontalDivider(
            modifier = Modifier
                .padding(start = 12.dp, end = 12.dp),
            thickness = 1.dp,
            color = Color.Gray
        )
        for ((no,ti) in muban.shiti.withIndex()){
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {
                            questions = getQuestions(ti)
                            showQuestionsSheet = true
                            haptics.performHapticFeedback(hapticFeedbackType = HapticFeedbackType.LongPress)
                        }
                    )
            ) {
                Text(
                    text = "${no + 1}."+ti.primQuestion,
                    modifier = Modifier
                        .padding(12.dp)
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(start = 12.dp, end = 12.dp),
                thickness = 1.dp,
                color = Color.Gray
            )
            for ((num, t) in ti.children.withIndex()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable {
                            selectedChoiceIndex  = no to num
                            showBottomSheet = true
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "${num+1}." + t.secondQuestion,
                            fontWeight = FontWeight.Bold
                        )
                        HorizontalDivider(
                            modifier = Modifier
                                .padding(start = 12.dp, end = 12.dp),
                            thickness = 1.dp,
                            color = Color.Gray
                        )
                        Text("[A]" + t.first)
                        Text("[B]" + t.second)
                        Text("[C]" + t.third)
                        Text("[D]" + t.fourth)

                        if(getChoice(no,num,choices)!=null){
                            if (getChoice(no,num,choices)!!.value.second.second!=null){
                                SuggestionChip(
                                    onClick = {},
                                    label = {
                                        getChoice(no,num,choices)!!.value.second.second?.let { Text(it) }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if(showBottomSheet){
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
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
                            showBottomSheet = false
                        },
                        label = {
                            Text(option)
                        }
                    )
                }
            }
        }

        if(showQuestionsSheet){
            ModalBottomSheet(
                onDismissRequest = {
                    showQuestionsSheet = false
                },
                sheetState = sheetState
            ){
                for (question in questions) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = question
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getQuestions(shiti: Shiti): MutableList<String> {
    val questions = mutableListOf<String>()

    for((no,ti) in shiti.children.withIndex()){
        questions.add("${no + 1}" + ti.secondQuestion)
        Log.d("",ti.secondQuestion)
    }

    return questions
}

fun getZreadOptions(): List<String> {
    val options = mutableListOf<String>()

    options.add("A")
    options.add("B")
    options.add("C")
    options.add("D")

    return options
}

fun getZreadChoices(shiti: List<Shiti>): MutableList<MutableState<Pair<Int, Pair<Int, String?>>>> {
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

fun getChoice(index:Int,i:Int,choices: MutableList<MutableState<Pair<Int, Pair<Int, String?>>>>): MutableState<Pair<Int, Pair<Int, String?>>>? {
    for(choice in choices){
        if(choice.value.first==index){
            if(choice.value.second.first==i){
                return choice
            }
        }
    }
    return null
}