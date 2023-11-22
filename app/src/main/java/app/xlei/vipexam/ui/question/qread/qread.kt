package app.xlei.vipexam.ui.question.qread

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.xlei.vipexam.data.Children
import app.xlei.vipexam.data.Muban

@Composable
fun qreadView(
    viewModel: QreadViewModel = viewModel(),
    muban: Muban,
    onFirstItemHidden: (String) -> Unit,
    onFirstItemAppear: ()->Unit,
    showAnswer: MutableState<Boolean>,
){
    viewModel.init()
    viewModel.setMuban(muban)
    viewModel.setTitle()
    viewModel.setArticle()
    viewModel.setChoices()
    viewModel.setOptions()
    val uiState by viewModel.uiState.collectAsState()
    val haptics = LocalHapticFeedback.current

    qread(
        muban = uiState.muban!!,
        showBottomSheet = uiState.showBottomSheet!!,
        showOptionsSheet = uiState.showOptionsSheet!!,
        title = uiState.title!!,
        article = uiState.article!!,
        options = uiState.options!!,
        choices  = uiState.choices!!,
        onArticleLongClicked = {
            uiState.showBottomSheet!!.value=true
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        },
        onQuestionClicked = {
            uiState.selectedChoiceIndex!!.value = it
            uiState.showOptionsSheet!!.value = true
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        },
        onOptionClicked = {
            viewModel.setChoice(it)
            uiState.showOptionsSheet!!.value = false
        },
        onFirstItemHidden = {
            onFirstItemHidden(it)
        },
        onFirstItemAppear = {
            onFirstItemAppear()
        },
        showAnswer = showAnswer
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
private fun qread(
    muban: Muban,
    showBottomSheet:MutableState<Boolean>,
    showOptionsSheet:MutableState<Boolean>,
    title:String,
    article:String,
    options:List<String>,
    choices: MutableState<MutableList<MutableState<Pair<String, String?>>>> ,
    onArticleLongClicked:()->Unit,
    onQuestionClicked:(Int)->Unit,
    onOptionClicked:(String)->Unit,
    onFirstItemHidden: (String) -> Unit,
    onFirstItemAppear: ()->Unit,
    showAnswer: MutableState<Boolean>,
){
    val scrollState = rememberLazyListState()
    val firstVisibleItemIndex by remember { derivedStateOf { scrollState.firstVisibleItemIndex } }

    Column {
        LazyColumn(
            state = scrollState
        ) {
            item {
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
            item {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .combinedClickable (
                            onClick = {},
                            onLongClick = onArticleLongClicked
                        )
                ) {
                    Text(
                        text = title,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = article,
                        modifier = Modifier
                            .padding(12.dp)
                    )
                }
            }

            items(choices.value.size){
                Column (
                    modifier = Modifier
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable {
                            onQuestionClicked(it)
                        }
                ){
                    Text(
                        text = "${it+1}. " + choices.value[it].value.first,
                        modifier = Modifier.padding(12.dp)
                    )
                    choices.value[it].value.second?.let{ choice ->
                        SuggestionChip(
                            onClick = {
                                onQuestionClicked(it)
                            },
                            label = {
                                Text(
                                    text = choice
                                )
                            }
                        )
                    }
                }
            }
            if (showAnswer.value){
                items(muban.shiti[0].children.size){
                    Text("${it + 1}"+ muban.shiti[0].children[it].refAnswer)
                    Text(muban.shiti[0].children[it].discription)
                }
            }
        }

        if(showBottomSheet.value){
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet.value=false
                },
            ){
                Column (
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ){
                    for((no,ti) in muban.shiti[0].children.withIndex()){
                        Column (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ){
                            Text(
                                text = "${no+1}. " + ti.secondQuestion,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }

        if(showOptionsSheet.value){
            ModalBottomSheet(
                onDismissRequest = {
                    showOptionsSheet.value=false
                },
            ){
                Column (
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ){
                    FlowRow(
                        horizontalArrangement = Arrangement.Start,
                        maxItemsInEachRow = 5,
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                    ) {
                        for(option in options){
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                            ) {
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
                }
            }
        }

        if ( firstVisibleItemIndex > 0 )
            onFirstItemHidden(muban.cname)
        else
            onFirstItemAppear()
    }
}

fun extractFirstPart(text: String): String {
    val lines = text.split("\n")
    if (lines.isNotEmpty()) {
        return lines.first()
    }
    return ""
}

fun extractSecondPart(text: String): String {
    val index = text.indexOf("\n")
    if (index != -1) {
        return text.substring(index + 1)
    }
    return ""
}

fun getQreadOptions(text: String):List<String>{
    val result = mutableListOf<String>()
    val pattern = Regex("""([A-Z])([)])""")
    val matches = pattern.findAll(text)
    for(match in matches){
        result.add(match.groupValues[1])
    }
    return result
}

fun getQreadChoices(children: List<Children>): MutableList<MutableState<Pair<String, String?>>> {
    val choices = mutableListOf<MutableState<Pair<String, String?>>>()

    for(ti in children){
        choices.add(mutableStateOf(ti.secondQuestion to null) )
    }

    return choices
}