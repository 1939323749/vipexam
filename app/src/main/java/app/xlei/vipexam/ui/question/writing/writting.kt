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
    viewModel.setWritings()
    val uiState by viewModel.uiState.collectAsState()
    writing(
        name = uiState.muban!!.cname,
        writings = uiState.writings,
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
    name: String,
    writings: List<WritingUiState.Writing>,
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
                text = name,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(start = 12.dp)
            )
            HorizontalDivider(
                modifier = Modifier
                    .padding(start = 12.dp, end = 12.dp),
                thickness = 1.dp,
                color = Color.Gray,
            )
        }
        items(writings.size){
            Column(
                modifier = Modifier
                    .padding(top = 12.dp,start = 12.dp, end = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = writings[it].question,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .padding(start = 4.dp, end = 4.dp)
                )
            }
            if (showAnswer.value)
                Text(
                    text = writings[it].refAnswer
                )
        }

    }

    if( firstVisibleItemIndex > 0)
        onFirstItemHidden(name)
    else
        onFirstItemAppear()

}