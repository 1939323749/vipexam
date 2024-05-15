package app.xlei.vipexam.core.ui.container

import android.content.ClipData
import android.view.View
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VipexamImageContainer(
    imageId: String
){
    Row {
        Spacer(Modifier.weight(2f))
        AsyncImage(
            model = "https://rang.vipexam.org/images/$imageId.jpg",
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .padding(top = 24.dp)
                .align(Alignment.CenterVertically)
                .weight(6f)
                .fillMaxWidth()
                .dragAndDropSource {
                    detectTapGestures(
                        onLongPress = {
                            startTransfer(
                                DragAndDropTransferData(
                                    clipData = ClipData.newHtmlText("", "","<img src=${"https://rang.vipexam.org/images/$imageId.jpg"}>"),
                                    flags = View.DRAG_FLAG_GLOBAL,
                                )
                            )
                        },
                    )
                }
        )
        Spacer(Modifier.weight(2f))
    }
}