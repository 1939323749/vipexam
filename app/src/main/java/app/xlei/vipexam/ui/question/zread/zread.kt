package app.xlei.vipexam.ui.question.zread

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.xlei.vipexam.data.Muban
import app.xlei.vipexam.data.Shiti

@Composable
fun zreadView(
    muban: Muban,
    viewModel: ZreadViewModel = viewModel(),
    onFirstItemHidden: (String) -> Unit,
    onFirstItemAppear: ()->Unit
){
    viewModel.init()
    viewModel.setMuban(muban)
    viewModel.setChoices()
    viewModel.setOptions()
    viewModel.setQuestions()
    val uiState by viewModel.uiState.collectAsState()
    val haptics = LocalHapticFeedback.current

    zread(
        muban = muban,
        showBottomSheet = uiState.showBottomSheet!!,
        showQuestionsSheet = uiState.showQuestionsSheet!!,
        options = uiState.options!!,
        choices = uiState.choices!!,
        questions = uiState.questions!!,
        onArticleLongClick = {
            uiState.questions!!.value = getZreadQuestions(shiti = uiState.muban!!.shiti[it])
            uiState.showQuestionsSheet!!.value = true
            haptics.performHapticFeedback(hapticFeedbackType = HapticFeedbackType.LongPress)
        },
        onQuestionClicked = {
            uiState.selectedChoiceIndex!!.value = it
            uiState.showBottomSheet!!.value = true
            haptics.performHapticFeedback(hapticFeedbackType = HapticFeedbackType.LongPress)
        },
        onOptionClicked = {
            if(uiState.selectedChoiceIndex!!.value!=-1 to -1){
                for(choice in  uiState.choices!!.value){
                    if(choice.value.first==uiState.selectedChoiceIndex!!.value.first){
                        if(choice.value.second.first==uiState.selectedChoiceIndex!!.value.second){
                            choice.value=choice.value.first to (choice.value.second.first to it)
                        }
                    }
                }
            }
            uiState.showBottomSheet!!.value = false
            haptics.performHapticFeedback(hapticFeedbackType = HapticFeedbackType.LongPress)

        },
        onFirstItemHidden = {
            onFirstItemHidden(it)
        },
        onFirstItemAppear = {
            onFirstItemAppear()
        },
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun zread(
    muban: Muban,
    showBottomSheet:MutableState<Boolean>,
    showQuestionsSheet:MutableState<Boolean>,
    options:List<String>,
    choices: MutableState<MutableList<MutableState<Pair<Int, Pair<Int, String?>>>>>,
    questions: MutableState<MutableList<String>>,
    onArticleLongClick:(Int)->Unit,
    onQuestionClicked:(Pair<Int,Int>)->Unit,
    onOptionClicked:(String)->Unit,
    onFirstItemHidden: (String) -> Unit,
    onFirstItemAppear: ()->Unit
){
    val scrollState = rememberLazyListState()
    val firstVisibleItemIndex by remember { derivedStateOf { scrollState.firstVisibleItemIndex } }

    Column {
        LazyColumn(
            state = scrollState
        ) {
            item {
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
            }
            items(muban.shiti.size){
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .combinedClickable(
                            onClick = {},
                            onLongClick = {
                                onArticleLongClick(it)
                            }
                        )
                ) {
                    Text(
                        text = "${it + 1}."+muban.shiti[it].primQuestion,
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
                for ((num, t) in muban.shiti[it].children.withIndex()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable {
                                onQuestionClicked(it to num)
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

                            if(getChoice(it,num,choices.value) !=null){
                                if (getChoice(it,num,choices.value)!!.value.second.second!=null){
                                    SuggestionChip(
                                        onClick = {},
                                        label = {
                                            getChoice(it,num,choices.value)!!.value.second.second?.let { Text(it) }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                for ((no,children) in muban.shiti[it].children.withIndex()){
                    Text("${no +1}"+children.refAnswer)
                    Text(children.discription)
                }
            }



        }

        if(showBottomSheet.value){
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet.value = false
                },
            ){
                for (option in options){
                    SuggestionChip(
                        onClick = {
                            onOptionClicked(option)
                        },
                        label = {
                            Text(option)
                        }
                    )
                }
            }
        }

        if(showQuestionsSheet.value){
            ModalBottomSheet(
                onDismissRequest = {
                    showQuestionsSheet.value = false
                },
            ){
                for (question in questions.value) {
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

        if ( firstVisibleItemIndex > 0 )
            onFirstItemHidden(muban.cname)
        else
            onFirstItemAppear()
    }
}


fun getZreadQuestions(shiti: Shiti): MutableList<String> {
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