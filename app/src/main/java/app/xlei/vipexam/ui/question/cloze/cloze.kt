package app.xlei.vipexam.ui.question.cloze

import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.xlei.vipexam.R
import app.xlei.vipexam.data.Muban
import app.xlei.vipexam.data.TranslationResponse
import app.xlei.vipexam.logic.DB
import app.xlei.vipexam.ui.components.translateDialog
import app.xlei.vipexam.ui.login.EmptyTextToolbar
import app.xlei.vipexam.ui.page.SelectableItem
import app.xlei.vipexam.ui.page.Word
import app.xlei.vipexam.util.Preferences
import compose.icons.FeatherIcons
import compose.icons.feathericons.Loader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun clozeView(
    viewModel: ClozeViewModel = hiltViewModel(),
    muban: Muban,
    showAnswer: MutableState<Boolean>,
){
    viewModel.setMuban(muban)
    viewModel.setClozes()

    val uiState by viewModel.uiState.collectAsState()
    val haptics = LocalHapticFeedback.current
    var selectedQuestionIndex by rememberSaveable { mutableStateOf(0) }

    cloze(
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
        showAnswer = showAnswer
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
private fun cloze(
    clozes: List<ClozeUiState.Cloze>,
    showBottomSheet: Boolean,
    onBlankClick: (Int)->Unit,
    onOptionClicked: (Int,ClozeUiState.Option)->Unit,
    toggleBottomSheet: () -> Unit,
    showAnswer: MutableState<Boolean>
){
    val scrollState = rememberLazyListState()
    var selectedClozeIndex by rememberSaveable { mutableStateOf(0) }
    val expanded = remember { mutableStateOf(false) }

    Column{
        LazyColumn(
            state =  scrollState,
            modifier = Modifier
        ) {
            items(clozes.size){ clozeIndex ->
                Column(
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {

                    CompositionLocalProvider(
                        LocalTextToolbar provides EmptyTextToolbar(expended = expanded)
                    ) {
                        SelectionContainer {
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

        if (expanded.value)
            translateDialog(expanded)
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

