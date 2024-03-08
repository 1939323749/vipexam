package app.xlei.vipexam.ui.components

import android.content.ClipData
import android.view.View
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.platform.LocalTextToolbar
import app.xlei.vipexam.core.data.constant.LongPressAction
import app.xlei.vipexam.core.data.util.Preferences
import app.xlei.vipexam.core.ui.EmptyTextToolbar
import app.xlei.vipexam.core.ui.TranslateDialog

/**
 * Vipexam article container
 *
 * @param onArticleLongClick 内容点击事件
 * @param onDragContent 可以拖动的内容
 * @param content 内容
 * @receiver
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VipexamArticleContainer(
    onArticleLongClick: (() -> Unit)? = {},
    onDragContent: String? = null,
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

        LongPressAction.NONE -> Column(
            modifier = Modifier
                .dragAndDropSource {
                    detectTapGestures(
                        onLongPress = {
                            startTransfer(
                                DragAndDropTransferData(
                                    clipData = ClipData.newPlainText("", onDragContent),
                                    flags = View.DRAG_FLAG_GLOBAL,
                                )
                            )
                        }
                    )
                }
        ){
            content()
        }
    }


    if (longPressAction==LongPressAction.TRANSLATE && showTranslateDialog)
        TranslateDialog(
            onDismissRequest = {
                showTranslateDialog = false
            }
        ) {
            AddToWordListButton(onClick = {
                showTranslateDialog = false
            })
        }
}
