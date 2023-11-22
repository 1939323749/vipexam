package app.xlei.vipexam.ui.question.cloze

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.xlei.vipexam.data.Children
import app.xlei.vipexam.data.Muban
import io.ktor.util.reflect.*
import okhttp3.internal.wait

@Composable
fun clozeView(
    viewModel: ClozeViewModel = viewModel(),
    muban: Muban,
    onFirstItemHidden: (String) -> Unit,
    onFirstItemAppear: ()->Unit,
    showAnswer: MutableState<Boolean>,
){
    viewModel.init()
    viewModel.setMuban(muban)
    viewModel.setChoices()
    viewModel.setOptions()
    val uiState by viewModel.uiState.collectAsState()
    val haptics = LocalHapticFeedback.current
    cloze(
        muban = muban,
        showBottomSheet = uiState.showBottomSheet!!,
        options = uiState.options!!,
        choices = uiState.choices!!,
        onButtonsClicked = {
            uiState.showBottomSheet!!.value=true
            uiState.selectedChoiceIndex!!.value=it
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        },
        onOptionClicked = {
            uiState.showBottomSheet!!.value = false
            uiState.choices!![uiState.selectedChoiceIndex!!.value].value=uiState
                .choices!![uiState.selectedChoiceIndex!!.value].value.first to it
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
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
private fun cloze(
    muban: Muban,
    showBottomSheet:MutableState<Boolean>,
    options: List<Pair<String,String>>,
    choices : MutableList<MutableState<Pair<String, String?>>>,
    onButtonsClicked:(Int)->Unit,
    onOptionClicked:(String)->Unit,
    onFirstItemHidden: (String) -> Unit,
    onFirstItemAppear: ()->Unit,
    showAnswer: MutableState<Boolean>
){
    val scrollState = rememberLazyListState()
    val firstVisibleItemIndex by remember { derivedStateOf { scrollState.firstVisibleItemIndex } }

    Column{
        LazyColumn(
            state =  scrollState,
            modifier = Modifier
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
                        .padding(top = 12.dp,start = 12.dp, end = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    val annotatedStringAndTags = getClickableArticle(muban.shiti[0].primQuestion)
                    ClickableText(
                        text = annotatedStringAndTags.first,
                        style = LocalTextStyle.current.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        onClick = {
                            for ((index,tag) in annotatedStringAndTags.second.withIndex()){
                                annotatedStringAndTags.first.getStringAnnotations(tag = tag, start = it, end = it).firstOrNull()?.let {
                                    onButtonsClicked(index)
                                }
                            }
                        },
                        modifier = Modifier
                            .padding(start = 4.dp, end = 4.dp)
                    )
                }
            }
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.Start,
                    maxItemsInEachRow = 2,
                ) {
                    for (index in choices.indices){
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            SuggestionChip(
                                onClick = {
                                    onButtonsClicked(index)
                                },
                                label = {
                                    Text(
                                        text = choices[index].value.first +
                                                if(choices[index].value.second==null){
                                                    ""
                                                }else{
                                                    choices[index].value.second
                                                }
                                    )
                                }
                            )
                        }
                    }
                }
            }
            if(showAnswer.value){
                items(muban.shiti[0].children.size){
                    Text(muban.shiti[0].children[it].secondQuestion + muban.shiti[0].children[it].refAnswer)
                    Text(muban.shiti[0].children[it].discription)
                }
            }

        }

        if (showBottomSheet.value) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet.value = false
                },
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.Start,
                    maxItemsInEachRow = 2,
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                ) {
                    for (option in options){
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ){
                            SuggestionChip(
                                onClick = {
                                    onOptionClicked(option.second)
                                },
                                label = {
                                    Text("[${option.first}]${option.second}")
                                },
                            )
                        }
                    }
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

fun getClickableArticle(text: String): Pair<AnnotatedString,List<String>> {
    val pattern = Regex("""C\d+""")
    val matches = pattern.findAll(text)

    val tags = mutableListOf<String>()

    val annotatedString = buildAnnotatedString {
        var currentPosition = 0
        for (match in matches) {
            val startIndex = match.range.first
            val endIndex = match.range.last + 1

            append(text.substring(currentPosition, startIndex))

            val tag = text.substring(startIndex, endIndex)

            pushStringAnnotation(tag = tag, annotation = tag )
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Unspecified)) {
                append(text.substring(startIndex, endIndex))
            }
            pop()
            tags.add(tag)

            currentPosition = endIndex
        }

        if (currentPosition < text.length) {
            append(text.substring(currentPosition, text.length))
        }
    }

    return annotatedString to tags
}

fun getClozeChoices(children: List<Children>): MutableList<MutableState<Pair<String, String?>>> {
    val choices = mutableListOf<MutableState<Pair<String, String?>>>()

    for(ti in children){
        choices.add(mutableStateOf(ti.secondQuestion to null) )
    }

    return choices
}

fun getClozeOptions(text: String):List<Pair<String,String>>{
    val pattern = Regex("""([A-O])\)\s*([^A-O]+)""")
    val matches = pattern.findAll(text)
    val options = mutableListOf<Pair<String, String>>()

    for (match in matches) {
        val option = match.groupValues[1]
        val optionText = match.groupValues[2].trim()
        options.add(option to optionText)
    }

    return options.sortedBy { it.first }
}