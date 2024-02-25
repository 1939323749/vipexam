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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus
import app.xlei.vipexam.core.data.constant.LongPressAction
import app.xlei.vipexam.core.data.util.Preferences

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
            confirmButton = {
                AddToWordListButton(onClick = {
                    showTranslateDialog = false
                })
            }
        ){
            showTranslateDialog = false
        }
}

class EmptyTextToolbar(
    private val onSelect: () -> Unit,
) : TextToolbar {
    override val status: TextToolbarStatus = TextToolbarStatus.Hidden

    override fun hide() {
        //expended.value=false
    }

    override fun showMenu(
        rect: Rect,
        onCopyRequested: (() -> Unit)?,
        onPasteRequested: (() -> Unit)?,
        onCutRequested: (() -> Unit)?,
        onSelectAllRequested: (() -> Unit)?,
    ) {
        onSelect.invoke()
        onCopyRequested?.invoke()
    }
}