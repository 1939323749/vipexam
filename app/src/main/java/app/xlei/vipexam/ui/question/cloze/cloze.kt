package app.xlei.vipexam.ui.question.cloze

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.xlei.vipexam.core.network.module.Muban
import app.xlei.vipexam.ui.components.TranslateDialog
import app.xlei.vipexam.ui.page.EmptyTextToolbar
import app.xlei.vipexam.ui.page.LongPressActions
import app.xlei.vipexam.util.Preferences

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun clozeView(
    viewModel: ClozeViewModel = hiltViewModel(),
    muban: Muban,
){
    viewModel.setMuban(muban)
    viewModel.setClozes()
    val showAnswer = Preferences.showAnswerFlow.collectAsState(initial = false)

    val uiState by viewModel.uiState.collectAsState()
    val haptics = LocalHapticFeedback.current
    var selectedQuestionIndex by rememberSaveable { mutableIntStateOf(0) }

    cloze(
        clozes = uiState.clozes,
        showBottomSheet = uiState.showBottomSheet,
        onBlankClick = {
            selectedQuestionIndex = it
            viewModel.toggleBottomSheet()
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        },
        onOptionClicked = { selectedClozeIndex, option ->
            viewModel.setOption(selectedClozeIndex, selectedQuestionIndex, option)
            viewModel.toggleBottomSheet()
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        },
        toggleBottomSheet = { viewModel.toggleBottomSheet() },
        showAnswer = showAnswer,
        addToWordList = viewModel::addToWordList,
    )
}

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
private fun cloze(
    clozes: List<ClozeUiState.Cloze>,
    showBottomSheet: Boolean,
    onBlankClick: (Int) -> Unit,
    onOptionClicked: (Int, ClozeUiState.Option) -> Unit,
    toggleBottomSheet: () -> Unit,
    showAnswer: State<Boolean>,
    addToWordList: (String) -> Unit,
) {
    val scrollState = rememberLazyListState()
    var selectedClozeIndex by rememberSaveable { mutableStateOf(0) }
    val expanded = remember { mutableStateOf(false) }
    val content: @Composable (Int) -> Unit = { clozeIndex ->
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
                        onBlankClick(index)
                    }
                }
            },
            modifier = Modifier
                .padding(start = 4.dp, end = 4.dp)
        )
    }

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
                    if (Preferences.get(
                            Preferences.longPressActionKey,
                            LongPressActions.SHOW_QUESTION.value
                        )
                        == LongPressActions.TRANSLATE.value
                    )
                        CompositionLocalProvider(
                            LocalTextToolbar provides EmptyTextToolbar(expended = expanded)
                        ) {
                            SelectionContainer {
                                content(clozeIndex)
                            }
                        }
                    else content(clozeIndex)
                }
                FlowRow(
                    horizontalArrangement = Arrangement.Start,
                    maxItemsInEachRow = 2,
                ) {
                    clozes[clozeIndex].blanks.forEachIndexed {index,blank->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            SuggestionChip(
                                onClick = {
                                    selectedClozeIndex = clozeIndex
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
                if(showAnswer.value)
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

        if (expanded.value && Preferences.get(
                Preferences.longPressActionKey,
                LongPressActions.SHOW_QUESTION.value
            )
            == LongPressActions.TRANSLATE.value
        )
            TranslateDialog(
                expanded = expanded,
                onAddButtonClick = addToWordList
            )
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
                    clozes[selectedClozeIndex].options.forEach{
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ){
                            SuggestionChip(
                                onClick = {
                                    onOptionClicked(selectedClozeIndex,it)
                                },
                                label = {
                                    Text("[${it.index}]${it.word}")
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

