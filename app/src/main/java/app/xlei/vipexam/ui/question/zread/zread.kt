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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.xlei.vipexam.data.Muban
import app.xlei.vipexam.data.Shiti

@Composable
fun zreadView(
    muban: Muban,
    viewModel: ZreadViewModel = hiltViewModel(),
    onFirstItemHidden: (String) -> Unit,
    onFirstItemAppear: () -> Unit,
    showAnswer: MutableState<Boolean>,
){
    viewModel.setMuban(muban)
    viewModel.setArticles()

    val uiState by viewModel.uiState.collectAsState()
    val haptics = LocalHapticFeedback.current
    var selectedQuestionIndex by rememberSaveable { mutableStateOf(0) }

    zread(
        name = uiState.muban!!.cname,
        articles = uiState.articles,
        showBottomSheet = uiState.showBottomSheet,
        showQuestionsSheet = uiState.showQuestionsSheet,
        toggleBottomSheet = { viewModel.toggleBottomSheet() },
        toggleQuestionsSheet = { viewModel.toggleQuestionsSheet() },
        onArticleLongClick = {
            selectedQuestionIndex = it
            viewModel.toggleQuestionsSheet()
            haptics.performHapticFeedback(hapticFeedbackType = HapticFeedbackType.LongPress)
        },
        onQuestionClicked = {
            selectedQuestionIndex = it
            viewModel.toggleBottomSheet()
            haptics.performHapticFeedback(hapticFeedbackType = HapticFeedbackType.LongPress)
        },
        onOptionClicked = {selectedArticleIndex,option->
            viewModel.setOption(selectedArticleIndex,selectedQuestionIndex,option)
            viewModel.toggleBottomSheet()
            haptics.performHapticFeedback(hapticFeedbackType = HapticFeedbackType.LongPress)
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


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun zread(
    name: String,
    articles: List<ZreadUiState.Article>,
    showBottomSheet: Boolean,
    showQuestionsSheet: Boolean,
    toggleBottomSheet: () -> Unit,
    toggleQuestionsSheet: () -> Unit,
    onArticleLongClick:(Int)->Unit,
    onQuestionClicked: (Int)->Unit,
    onOptionClicked: (Int,String)->Unit,
    onFirstItemHidden: (String) -> Unit,
    onFirstItemAppear: ()->Unit,
    showAnswer: MutableState<Boolean>,
){
    val scrollState = rememberLazyListState()
    val firstVisibleItemIndex by remember { derivedStateOf { scrollState.firstVisibleItemIndex } }
    val coroutine = rememberCoroutineScope()
    var selectedArticle by rememberSaveable { mutableStateOf(0) }

    Column {
        LazyColumn(
            state = scrollState,
        ) {
            if (firstVisibleItemIndex > 0)
                stickyHeader {
                    LinearProgressIndicator(
                        progress = { scrollState.firstVisibleItemIndex / scrollState.layoutInfo.totalItemsCount.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            item {
                Column{
                    Text(
                        text = name,
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
            articles.forEachIndexed {articleIndex,ti->
                item{
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    selectedArticle = articleIndex
                                    onArticleLongClick(articleIndex)
                                }
                            )
                    ) {
                        Text(ti.index)
                        Text(
                            text = ti.content,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
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
                }
                items(ti.questions.size){index->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable {
                                selectedArticle = articleIndex
                                onQuestionClicked(index)
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "${ti.questions[index].index}. "+ti.questions[index].question,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(start = 12.dp, end = 12.dp),
                                thickness = 1.dp,
                                color = Color.Gray
                            )

                            ti.questions[index].options.forEach {option->
//                                Column(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .padding(12.dp)
//                                        .clip(RoundedCornerShape(12.dp))
//                                        .background(MaterialTheme.colorScheme.primaryContainer)
//                                        .clickable {
//                                        }
//                                ) {
//                                    Column(
//                                        modifier = Modifier.padding(12.dp)
//                                    ) {
                                        Text(
                                            text = "[${option.index}]"+option.option,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        )
//                                    }
//                                }
                            }
                        if (ti.questions[index].choice.value!="")
                            SuggestionChip(
                                onClick = {},
                                label = { Text(ti.questions[index].choice.value) }
                            )
                        }
                    }
                    if (showAnswer.value)
                        articles[articleIndex].questions.forEach {
                            Text("${it.index}."+it.refAnswer)
                            Text(it.description)
                        }
                }
            }
        }

        if(showBottomSheet){
            ModalBottomSheet(
                onDismissRequest = toggleBottomSheet,
            ){
                articles[selectedArticle].options.forEach{
                    SuggestionChip(
                        onClick = {
                            onOptionClicked(selectedArticle , it)
                        },
                        label = {
                            Text(it)
                        }
                    )
                }
            }
        }

        if(showQuestionsSheet){
            ModalBottomSheet(
                onDismissRequest = toggleQuestionsSheet,
            ){
                articles[selectedArticle].questions.forEach {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable {
                                toggleQuestionsSheet()
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = it.question,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }
            }
        }

        if ( firstVisibleItemIndex > 0 )
            onFirstItemHidden(name)
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