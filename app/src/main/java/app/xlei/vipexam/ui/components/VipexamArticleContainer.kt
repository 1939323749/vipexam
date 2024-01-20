package app.xlei.vipexam.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalTextToolbar
import app.xlei.vipexam.core.data.constant.LongPressAction
import app.xlei.vipexam.core.data.util.Preferences
import app.xlei.vipexam.ui.page.EmptyTextToolbar

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VipexamArticleContainer(
    onArticleLongClick: (() -> Unit)?={},
    content: @Composable () -> Unit
){
    var showTranslateDialog by rememberSaveable { mutableStateOf(false) }
    val longPressAction = LongPressAction.entries[Preferences
        .longPressAction.collectAsState(initial = LongPressAction.SHOW_QUESTION.value)
        .value]

    when (longPressAction) {
        LongPressAction.TRANSLATE ->
            CompositionLocalProvider(
                value = LocalTextToolbar provides EmptyTextToolbar {
                    showTranslateDialog = true
                }
            ) {
                SelectionContainer {
                    content()
                }
            }
        LongPressAction.SHOW_QUESTION ->
            Column(
                modifier = Modifier
                    .combinedClickable(
                        onClick = {},
                        onLongClick = onArticleLongClick
                    )
            ) {
                content()
            }
        LongPressAction.NONE -> content()
    }


    if (longPressAction==LongPressAction.TRANSLATE && showTranslateDialog)
        TranslateDialog(
            confirmButton = {
                AddToWordListButton(onClick = {
                    showTranslateDialog = false
                })
            }
        ){
            showTranslateDialog = false
        }
}