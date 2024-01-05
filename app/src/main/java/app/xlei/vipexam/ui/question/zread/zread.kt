package app.xlei.vipexam.ui.question.zread

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.xlei.vipexam.data.Muban
import app.xlei.vipexam.ui.components.translateDialog
import app.xlei.vipexam.ui.login.EmptyTextToolbar
import app.xlei.vipexam.ui.page.LongPressActions
import app.xlei.vipexam.util.Preferences

@Composable
fun zreadView(
    muban: Muban,
    viewModel: ZreadViewModel = hiltViewModel(),
    showAnswer: MutableState<Boolean>,
){
    viewModel.setMuban(muban)
    viewModel.setArticles()

    val uiState by viewModel.uiState.collectAsState()
    val haptics = LocalHapticFeedback.current
    var selectedQuestionIndex by rememberSaveable { mutableStateOf(0) }

    zread(
        articles = uiState.articles,
        showBottomSheet = uiState.showBottomSheet,
        showQuestionsSheet = uiState.showQuestionsSheet,
        toggleBottomSheet = viewModel::toggleBottomSheet,
        toggleQuestionsSheet = viewModel::toggleQuestionsSheet,
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
        onOptionClicked = { selectedArticleIndex, option ->
            viewModel.setOption(selectedArticleIndex, selectedQuestionIndex, option)
            viewModel.toggleBottomSheet()
            haptics.performHapticFeedback(hapticFeedbackType = HapticFeedbackType.LongPress)
        },
        showAnswer = showAnswer
    )
}


@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun zread(
    articles: List<ZreadUiState.Article>,
    showBottomSheet: Boolean,
    showQuestionsSheet: Boolean,
    toggleBottomSheet: () -> Unit,
    toggleQuestionsSheet: () -> Unit,
    onArticleLongClick:(Int)->Unit,
    onQuestionClicked: (Int)->Unit,
    onOptionClicked: (Int,String)->Unit,
    showAnswer: MutableState<Boolean>,
){
    val scrollState = rememberLazyListState()
    var selectedArticle by rememberSaveable { mutableStateOf(0) }

    val expanded = remember { mutableStateOf(false) }
    Column {
        LazyColumn(
            state = scrollState,
        ) {
            articles.forEachIndexed {articleIndex,ti->
                item {
                    CompositionLocalProvider(
                        LocalTextToolbar provides EmptyTextToolbar(expended = expanded)
                    ) {
                        SelectionContainer {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .clip(RoundedCornerShape(16.dp))
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
                                        .padding(16.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp),
                        thickness = 1.dp,
                        color = Color.Gray
                    )
                }
                items(ti.questions.size){index->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable {
                                selectedArticle = articleIndex
                                onQuestionClicked(index)
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "${ti.questions[index].index}. "+ti.questions[index].question,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(start = 16.dp, end = 16.dp),
                                thickness = 1.dp,
                                color = Color.Gray
                            )

                            ti.questions[index].options.forEach { option ->
                                Text(
                                    text = "[${option.index}]" + option.option,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            }
                            if (ti.questions[index].choice.value != "")
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(ti.questions[index].choice.value) }
                                )
                        }
                    }
                    if (showAnswer.value)
                        articles[articleIndex].questions[index].let {
                            Text(
                                text = "${it.index}." + it.refAnswer,
                                modifier = Modifier
                                    .padding(horizontal = 24.dp)
                            )
                            Text(
                                text = it.description,
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
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = toggleBottomSheet,
            ) {
                articles[selectedArticle].options.forEach {
                    SuggestionChip(
                        onClick = {
                            onOptionClicked(selectedArticle, it)
                        },
                        label = {
                            Text(it)
                        }
                    )
                }
            }
        }

        if (showQuestionsSheet &&
            Preferences.get(
                Preferences.longPressActionKey,
                LongPressActions.SHOW_QUESTION.value
            )
            == LongPressActions.SHOW_QUESTION.value
        ) {
            ModalBottomSheet(
                onDismissRequest = toggleQuestionsSheet,
            ) {
                articles[selectedArticle].questions.forEach {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable {
                                toggleQuestionsSheet()
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
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
    }
}