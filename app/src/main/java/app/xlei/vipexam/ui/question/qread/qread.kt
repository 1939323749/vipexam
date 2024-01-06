package app.xlei.vipexam.ui.question.qread

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.xlei.vipexam.data.Muban
import app.xlei.vipexam.ui.components.translateDialog
import app.xlei.vipexam.ui.login.EmptyTextToolbar
import app.xlei.vipexam.ui.page.LongPressActions
import app.xlei.vipexam.util.Preferences

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun qreadView(
    viewModel: QreadViewModel = hiltViewModel(),
    muban: Muban,
){
    viewModel.setMuban(muban)
    viewModel.setArticles()
    val showAnswer = Preferences.showAnswerFlow.collectAsState(initial = false)

    val uiState by viewModel.uiState.collectAsState()
    val haptics = LocalHapticFeedback.current
    var selectedQuestionIndex by rememberSaveable { mutableStateOf(0) }

    qread(
        showBottomSheet = uiState.showBottomSheet,
        showOptionsSheet = uiState.showOptionsSheet,
        articles = uiState.articles,
        toggleBottomSheet = viewModel::toggleBottomSheet,
        toggleOptionsSheet = viewModel::toggleOptionsSheet,
        onArticleLongClicked = {
            viewModel.toggleBottomSheet()
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        },
        onQuestionClicked = {
            selectedQuestionIndex = it
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        },
        onOptionClicked = { selectedArticleIndex, option ->
            viewModel.setOption(selectedArticleIndex, selectedQuestionIndex, option)
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        },
        showAnswer = showAnswer,
    )
}

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalLayoutApi::class
)
@Composable
private fun qread(
    showBottomSheet: Boolean,
    showOptionsSheet: Boolean,
    articles: List<QreadUiState.Article>,
    toggleBottomSheet: () -> Unit,
    toggleOptionsSheet: () -> Unit,
    onArticleLongClicked: () -> Unit,
    onQuestionClicked: (Int) -> Unit,
    onOptionClicked: (Int, String) -> Unit,
    showAnswer: State<Boolean>,
){
    val scrollState = rememberLazyListState()
    var selectedArticle by rememberSaveable { mutableStateOf(0) }
    val expanded = remember { mutableStateOf(false) }
    val content: @Composable (Int) -> Unit = { articleIndex ->
        Column(
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .combinedClickable(
                    onClick = {},
                    onLongClick = {
                        selectedArticle = articleIndex
                        onArticleLongClicked()
                    }
                )
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

    Column {
        LazyColumn(
            state = scrollState
        ) {
            articles.forEachIndexed { articleIndex, article ->
                item {
                    if (Preferences.get(
                            Preferences.longPressActionKey,
                            LongPressActions.SHOW_QUESTION.value
                        )
                        == LongPressActions.SHOW_QUESTION.value
                    ) CompositionLocalProvider(
                        LocalTextToolbar provides EmptyTextToolbar(expended = expanded)
                    ) {
                        SelectionContainer {
                            content(articleIndex)
                        }
                    }
                    else content(articleIndex)
                }
                items(article.questions.size) { it ->
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

                    if (showAnswer.value)
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
        if (expanded.value &&
            Preferences.get(
                Preferences.longPressActionKey,
                LongPressActions.SHOW_QUESTION.value
            )
            == LongPressActions.TRANSLATE.value
        )
            translateDialog(expanded)
        // questions
        if (showBottomSheet &&
            Preferences.get(
                Preferences.longPressActionKey,
                LongPressActions.SHOW_QUESTION.value
            )
            == LongPressActions.SHOW_QUESTION.value
        ) {
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

