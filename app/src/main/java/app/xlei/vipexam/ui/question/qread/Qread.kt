package app.xlei.vipexam.ui.question.qread

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.xlei.vipexam.core.network.module.getExamResponse.Muban
import app.xlei.vipexam.core.ui.VipexamArticleContainer
import app.xlei.vipexam.preference.LocalShowAnswer
import app.xlei.vipexam.preference.LocalVibrate

@Composable
fun QreadView(
    viewModel: QreadViewModel = hiltViewModel(),
    muban: Muban,
) {
    viewModel.setMuban(muban)
    viewModel.SetArticles()

    val vibrate = LocalVibrate.current.isVibrate()

    val showAnswer = LocalShowAnswer.current.isShowAnswer()

    val uiState by viewModel.uiState.collectAsState()

    val haptics = LocalHapticFeedback.current
    var selectedQuestionIndex by rememberSaveable { mutableIntStateOf(0) }

    Qread(
        showBottomSheet = uiState.showBottomSheet,
        showOptionsSheet = uiState.showOptionsSheet,
        articles = uiState.articles,
        toggleBottomSheet = viewModel::toggleBottomSheet,
        toggleOptionsSheet = viewModel::toggleOptionsSheet,
        onArticleLongClicked = {
            if (vibrate) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        },
        onQuestionClicked = {
            selectedQuestionIndex = it
            if (vibrate) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        },
        onOptionClicked = { selectedArticleIndex, option ->
            viewModel.setOption(selectedArticleIndex, selectedQuestionIndex, option)
            if (vibrate) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        },
        showAnswer = showAnswer,
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
private fun Qread(
    showBottomSheet: Boolean,
    showOptionsSheet: Boolean,
    articles: List<QreadUiState.Article>,
    toggleBottomSheet: () -> Unit,
    toggleOptionsSheet: () -> Unit,
    onArticleLongClicked: () -> Unit,
    onQuestionClicked: (Int) -> Unit,
    onOptionClicked: (Int, String) -> Unit,
    showAnswer: Boolean,
){
    val scrollState = rememberLazyListState()
    val selectedArticle by rememberSaveable { mutableIntStateOf(0) }

    Column {
        LazyColumn(
            state = scrollState
        ) {
            articles.forEachIndexed { articleIndex, article ->
                item {
                    VipexamArticleContainer(
                        onArticleLongClick = {
                            onArticleLongClicked.invoke()
                            toggleBottomSheet.invoke()
                        },
                        onDragContent = article.title
                                + "\n\n" + article.content
                                + article.questions.joinToString{ "\n\n" +  it.index + ". " +  it.question }
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Text(
                                text = articles[articleIndex].title,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 24.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                            )
                            Text(
                                text = articles[articleIndex].content,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier
                                    .padding(16.dp)
                            )
                        }
                    }
                }
                items(article.questions.size) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable {
                                onQuestionClicked(it)
                                toggleOptionsSheet()
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "${article.questions[it].index}. ${article.questions[it].question}",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                            if (article.questions[it].choice.value != "") {
                                SuggestionChip(
                                    onClick = toggleOptionsSheet,
                                    label = { Text(article.questions[it].choice.value) }
                                )
                            }
                        }
                    }

                    if (showAnswer)
                        articles[articleIndex].questions[it].let { question ->
                            Text(
                                text = question.index + question.refAnswer,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                            Text(
                                text = question.description,
                                modifier = Modifier
                                    .padding(horizontal = 24.dp)
                            )
                        }
                }
            }

        }
        // Questions
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = toggleBottomSheet,
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ) {
                    articles[selectedArticle].questions.forEachIndexed { index, it ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .clickable {
                                    onQuestionClicked(index)
                                    toggleOptionsSheet()
                                }
                        ) {
                            Text(
                                text = "${it.index}. ${it.question}",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(16.dp)
                            )
                            if (it.choice.value != "") {
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
        if (showOptionsSheet) {
            ModalBottomSheet(
                onDismissRequest = toggleOptionsSheet,
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ){
                    FlowRow(
                        horizontalArrangement = Arrangement.Start,
                        maxItemsInEachRow = 5,
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                    ) {
                        articles[selectedArticle].options.forEach {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                SuggestionChip(
                                    onClick = {
                                        onOptionClicked(selectedArticle, it.option)
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
    }
}

