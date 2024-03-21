package app.xlei.vipexam.template.read

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.xlei.vipexam.core.network.module.getExamResponse.Muban
import app.xlei.vipexam.core.ui.VipexamArticleContainer
import app.xlei.vipexam.preference.LocalShowAnswer


@Composable
fun ReadView(
    submitMyAnswer: (String, String) -> Unit,
    muban: Muban,
    viewModel: ReadViewModel = hiltViewModel(),
) {
    viewModel.setMuban(muban)
    viewModel.SetArticles()

    val uiState by viewModel.uiState.collectAsState()
    val haptics = LocalHapticFeedback.current
    var selectedQuestionIndex by rememberSaveable { mutableIntStateOf(0) }

    Read(
        articles = uiState.articles,
        showBottomSheet = uiState.showBottomSheet,
        showQuestionsSheet = uiState.showQuestionsSheet,
        toggleBottomSheet = viewModel::toggleBottomSheet,
        toggleQuestionsSheet = viewModel::toggleQuestionsSheet,
        onArticleLongClick = {
            selectedQuestionIndex = it
            haptics.performHapticFeedback(hapticFeedbackType = HapticFeedbackType.LongPress)
        },
        onQuestionClicked = {
            selectedQuestionIndex = it
            viewModel.toggleBottomSheet()
            haptics.performHapticFeedback(hapticFeedbackType = HapticFeedbackType.LongPress)
        },
        onOptionClicked = { selectedArticleIndex, option ->
            submitMyAnswer(
                muban.shiti[selectedArticleIndex].children[selectedQuestionIndex].questionCode,
                option
            )
            viewModel.setOption(selectedArticleIndex, selectedQuestionIndex, option)
            viewModel.toggleBottomSheet()
            haptics.performHapticFeedback(hapticFeedbackType = HapticFeedbackType.LongPress)
        },
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Read(
    articles: List<ReadUiState.Article>,
    showBottomSheet: Boolean,
    showQuestionsSheet: Boolean,
    toggleBottomSheet: () -> Unit,
    toggleQuestionsSheet: () -> Unit,
    onArticleLongClick: (Int) -> Unit,
    onQuestionClicked: (Int) -> Unit,
    onOptionClicked: (Int, String) -> Unit,
) {
    val showAnswer = LocalShowAnswer.current.isShowAnswer()
    val scrollState = rememberLazyListState()
    var selectedArticle by rememberSaveable { mutableIntStateOf(0) }

    Column {
        LazyColumn(
            state = scrollState,
        ) {
            articles.forEachIndexed { articleIndex, ti ->
                item {
                    VipexamArticleContainer(
                        onArticleLongClick = {
                            selectedArticle = articleIndex
                            onArticleLongClick(articleIndex)
                            toggleQuestionsSheet.invoke()
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
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
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp),
                        thickness = 1.dp,
                        color = Color.Gray
                    )
                }
                items(ti.questions.size) { index ->
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
                                text = "${ti.questions[index].index}. " + ti.questions[index].question,
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
                                option.option.takeIf { it != "" }?.let {
                                    Text(
                                        text = "[${option.index}]" + option.option,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    )
                                }
                            }
                            if (ti.questions[index].choice.value != "")
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(ti.questions[index].choice.value) }
                                )
                        }
                    }
                    if (showAnswer)
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

        if (showQuestionsSheet) {
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