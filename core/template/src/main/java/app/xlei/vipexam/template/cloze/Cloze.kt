package app.xlei.vipexam.template.cloze

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.xlei.vipexam.core.network.module.getExamResponse.Muban
import app.xlei.vipexam.core.ui.VipexamArticleContainer
import app.xlei.vipexam.preference.LocalShowAnswer
import app.xlei.vipexam.preference.LocalVibrate

@Composable
fun ClozeView(
    viewModel: ClozeViewModel = hiltViewModel(),
    muban: Muban,
) {
    viewModel.setMuban(muban)
    viewModel.SetClozes()
    val showAnswer = LocalShowAnswer.current.isShowAnswer()
    val vibrate = LocalVibrate.current.isVibrate()

    val uiState by viewModel.uiState.collectAsState()
    val haptics = LocalHapticFeedback.current
    var selectedQuestionIndex by rememberSaveable { mutableIntStateOf(0) }

    cloze(
        clozes = uiState.clozes,
        showBottomSheet = uiState.showBottomSheet,
        onBlankClick = {
            selectedQuestionIndex = it
            viewModel.toggleBottomSheet()
            if (vibrate) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        },
        onOptionClicked = { selectedClozeIndex, word ->
            viewModel.setOption(selectedClozeIndex, selectedQuestionIndex, word)
            viewModel.toggleBottomSheet()
            if (vibrate) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        },
        toggleBottomSheet = { viewModel.toggleBottomSheet() },
        showAnswer = showAnswer,
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
private fun cloze(
    clozes: List<ClozeUiState.Cloze>,
    showBottomSheet: Boolean,
    onBlankClick: (Int) -> Unit,
    onOptionClicked: (Int, String) -> Unit,
    toggleBottomSheet: () -> Unit,
    showAnswer: Boolean,
) {
    val scrollState = rememberLazyListState()
    var selectedClozeIndex by rememberSaveable { mutableStateOf(0) }
    var selectedOption by rememberSaveable { mutableStateOf(0) }

    Column {
        LazyColumn(
            state = scrollState,
            modifier = Modifier
        ) {
            items(clozes.size) { clozeIndex ->
                Column(
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    VipexamArticleContainer {
                        ClickableText(
                            text = clozes[clozeIndex].article.article,
                            style = LocalTextStyle.current.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            onClick = {
                                clozes[clozeIndex].article.tags.forEachIndexed { index, tag ->
                                    clozes[clozeIndex].article.article.getStringAnnotations(
                                        tag = tag,
                                        start = it,
                                        end = it
                                    ).firstOrNull()?.let {
                                        selectedClozeIndex = clozeIndex
                                        selectedOption = index
                                        onBlankClick(index)
                                    }
                                }
                            },
                            modifier = Modifier
                                .padding(start = 4.dp, end = 4.dp)
                        )
                    }
                }
                FlowRow(
                    horizontalArrangement = Arrangement.Start,
                    maxItemsInEachRow = 2,
                ) {
                    clozes[clozeIndex].blanks.forEachIndexed { index, blank ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            SuggestionChip(
                                onClick = {
                                    selectedClozeIndex = clozeIndex
                                    selectedOption = index
                                    onBlankClick(index)
                                },
                                label = {
                                    Text(
                                        text = blank.index + blank.choice.value
                                    )
                                }
                            )
                        }
                    }
                }
                if (showAnswer)
                    clozes[clozeIndex].blanks.forEach { blank ->
                        Text(
                            text = blank.index + blank.refAnswer,
                            modifier = Modifier
                                .padding(horizontal = 24.dp)
                        )
                        Text(
                            text = blank.description,
                            modifier = Modifier
                                .padding(horizontal = 24.dp)
                        )
                    }

            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = toggleBottomSheet,
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.Start,
                    maxItemsInEachRow = 2,
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                ) {
                    clozes[selectedClozeIndex].options[selectedOption].words.forEach {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            SuggestionChip(
                                onClick = {
                                    onOptionClicked(selectedClozeIndex, it.word)
                                },
                                label = {
                                    Text("[${it.index}] ${it.word}")
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}