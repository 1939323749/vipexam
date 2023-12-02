package app.xlei.vipexam.ui.question.cloze

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.xlei.vipexam.data.Muban

@Composable
fun clozeView(
    viewModel: ClozeViewModel = hiltViewModel(),
    muban: Muban,
    onFirstItemHidden: (String) -> Unit,
    onFirstItemAppear: () -> Unit,
    showAnswer: MutableState<Boolean>,
){
    viewModel.setMuban(muban)
    viewModel.setClozes()

    val uiState by viewModel.uiState.collectAsState()
    val haptics = LocalHapticFeedback.current
    var selectedQuestionIndex by rememberSaveable { mutableStateOf(0) }

    cloze(
        name = uiState.muban!!.cname,
        clozes = uiState.clozes,
        showBottomSheet = uiState.showBottomSheet,
        onBlankClick = {
            selectedQuestionIndex = it
            viewModel.toggleBottomSheet()
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        },
        onOptionClicked = {selectedClozeIndex,option->
            viewModel.setOption(selectedClozeIndex,selectedQuestionIndex,option)
            viewModel.toggleBottomSheet()
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        },
        toggleBottomSheet = {viewModel.toggleBottomSheet()},
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
    name: String,
    clozes: List<ClozeUiState.Cloze>,
    showBottomSheet: Boolean,
    onBlankClick: (Int)->Unit,
    onOptionClicked: (Int,ClozeUiState.Option)->Unit,
    toggleBottomSheet: () -> Unit,
    onFirstItemHidden: (String) -> Unit,
    onFirstItemAppear: ()->Unit,
    showAnswer: MutableState<Boolean>
){
    val scrollState = rememberLazyListState()
    val firstVisibleItemIndex by remember { derivedStateOf { scrollState.firstVisibleItemIndex } }
    var selectedClozeIndex by rememberSaveable { mutableStateOf(0) }

    Column{
        LazyColumn(
            state =  scrollState,
            modifier = Modifier
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
            items(clozes.size){ clozeIndex ->
                Column(
                    modifier = Modifier
                        .padding(top = 12.dp,start = 12.dp, end = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    SelectionContainer {
                        ClickableText(
                            text = clozes[clozeIndex].article.article,
                            style = LocalTextStyle.current.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            onClick = {
                                clozes[clozeIndex].article.tags.forEachIndexed { index,tag->
                                    clozes[clozeIndex].article.article.getStringAnnotations(tag = tag, start = it, end = it).firstOrNull()?.let {
                                        selectedClozeIndex = clozeIndex
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
                    clozes[clozeIndex].blanks.forEach {blank->
                        Text(blank.index+blank.refAnswer)
                        Text(blank.description)
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
                        .padding(bottom = 20.dp)
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

        if(firstVisibleItemIndex>0)
            onFirstItemHidden(name)
        else
            onFirstItemAppear()

    }
}

