package app.xlei.vipexam.ui.question.qread

import android.util.Log
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
import app.xlei.vipexam.ui.question.cloze.getClozeOptions
import kotlinx.coroutines.launch

@Composable
fun qreadView(
    viewModel: QreadViewModel = viewModel(),
    muban: Muban,
    onFirstItemHidden: (String) -> Unit,
    onFirstItemAppear: ()->Unit,
    showAnswer: MutableState<Boolean>,
){
    viewModel.setMuban(muban)
    viewModel.setArticles()
    val uiState by viewModel.uiState.collectAsState()
    val haptics = LocalHapticFeedback.current
    var selectedQuestionIndex by remember { mutableStateOf(0) }

    qread(
        name = uiState.muban!!.cname,
        showBottomSheet = uiState.showBottomSheet,
        showOptionsSheet = uiState.showOptionsSheet,
        articles = uiState.articles,
        toggleBottomSheet = { viewModel.toggleBottomSheet() },
        toggleOptionsSheet = { viewModel.toggleOptionsSheet() },
        onArticleLongClicked = {
            viewModel.toggleBottomSheet()
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        },
        onQuestionClicked = {
            selectedQuestionIndex = it
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        },
        onOptionClicked = {selectedArticleIndex,option->
            viewModel.setOption(selectedArticleIndex,selectedQuestionIndex,option)
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
private fun qread(
    name: String,
    showBottomSheet: Boolean,
    showOptionsSheet: Boolean,
    articles: List<QreadUiState.Article>,
    toggleBottomSheet: () -> Unit,
    toggleOptionsSheet: () -> Unit,
    onArticleLongClicked:()->Unit,
    onQuestionClicked: (Int)->Unit,
    onOptionClicked: (Int,QreadUiState.Option)->Unit,
    onFirstItemHidden: (String) -> Unit,
    onFirstItemAppear: ()->Unit,
    showAnswer: MutableState<Boolean>,
){
    val scrollState = rememberLazyListState()
    var selectedArticle by remember { mutableStateOf(0) }
    val firstVisibleItemIndex by remember { derivedStateOf { scrollState.firstVisibleItemIndex } }
    val coroutine = rememberCoroutineScope()

    Column {
        LazyColumn(
            state = scrollState
        ) {
            item {
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
            items(articles.size) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .combinedClickable (
                            onClick = {},
                            onLongClick = {
                                selectedArticle = it
                                onArticleLongClicked() }
                        )
                ) {
                    Text(
                        text = articles[it].title,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = articles[it].content,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .padding(12.dp)
                    )
                }

                if (showAnswer.value )
                    articles[it].questions.forEach {question->
                        Text(question.index+question.refAnswer)
                        Text(question.description)
                    }
            }

        }

        // questions
        if(showBottomSheet){
            ModalBottomSheet(
                onDismissRequest = toggleBottomSheet,
            ){
                Column (
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ){
                    articles[selectedArticle].questions.forEachIndexed{index,it->
                        Column (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .clickable {
                                    onQuestionClicked(index)
                                    toggleOptionsSheet()
                                }
                        ){
                            Text(
                                text = "${it.index}. ${it.question}",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(12.dp)
                            )
                            Log.d("",it.choice.value)
                            if (it.choice.value!="") {
                                SuggestionChip(
                                    onClick = toggleOptionsSheet,
                                    label = { Text(it.choice.value) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // options
        if(showOptionsSheet){
            ModalBottomSheet(
                onDismissRequest = toggleOptionsSheet,
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
                        articles[selectedArticle].options.forEach {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                SuggestionChip(
                                    onClick = {
                                        onOptionClicked(selectedArticle,it)
                                        toggleOptionsSheet()
                                    },
                                    label = {
                                        Text(it.option)
                                    }
                                )
                            }
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

