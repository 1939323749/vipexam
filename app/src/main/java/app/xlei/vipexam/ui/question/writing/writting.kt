package app.xlei.vipexam.ui.question.writing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.xlei.vipexam.data.Muban


@Composable
fun writingView(
    viewModel: WritingViewModel = viewModel(),
    muban: Muban,
    onFirstItemHidden: (String) -> Unit,
    onFirstItemAppear: ()->Unit,
    showAnswer: MutableState<Boolean>,
){
    viewModel.setMuban(muban)
    val uiState by viewModel.uiState.collectAsState()
    writing(
        muban = uiState.muban!!,
        onFirstItemHidden = {
            onFirstItemHidden(it)
        },
        onFirstItemAppear = {
            onFirstItemAppear()
        },
        showAnswer = showAnswer
    )
}

@Composable
private fun writing(
    muban: Muban,
    onFirstItemHidden: (String) -> Unit,
    onFirstItemAppear: () -> Unit,
    showAnswer: MutableState<Boolean>,
){
    val scrollState = rememberLazyListState()
    val firstVisibleItemIndex by remember { derivedStateOf { scrollState.firstVisibleItemIndex } }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        state = scrollState
    ) {
        item {
            Text(
                muban.cname,
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
        item {
            Column(
                modifier = Modifier
                    .padding(top = 12.dp,start = 12.dp, end = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    muban.shiti[0].primQuestion,
                    modifier = Modifier
                        .padding(start = 4.dp, end = 4.dp)
                )
            }
        }
        if (showAnswer.value){
            item {
                Text(muban.shiti[0].refAnswer)
            }
        }

    }

    if( firstVisibleItemIndex > 0)
        onFirstItemHidden(muban.cname)
    else
        onFirstItemAppear()

}