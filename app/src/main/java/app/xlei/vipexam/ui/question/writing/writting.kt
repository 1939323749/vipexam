package app.xlei.vipexam.ui.question.writing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.xlei.vipexam.data.Muban
import coil.compose.AsyncImage


@Composable
fun writingView(
    viewModel: WritingViewModel = hiltViewModel(),
    muban: Muban,
    showAnswer: MutableState<Boolean>,
){
    viewModel.setMuban(muban)
    viewModel.setWritings()
    val uiState by viewModel.uiState.collectAsState()
    writing(
        writings = uiState.writings,
        showAnswer = showAnswer
    )
}

@Composable
private fun writing(
    writings: List<WritingUiState.Writing>,
    showAnswer: MutableState<Boolean>,
) {
    val scrollState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        state = scrollState
    ) {
        items(writings.size) {
            Column(
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = writings[it].question,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .padding(start = 4.dp, end = 4.dp)
                )
                if (shouldShowImage(writings[it].question)) {
                    Row {
                        Spacer(Modifier.weight(2f))
                        AsyncImage(
                            model = "https://rang.vipexam.org/images/${writings[it].image}.jpg",
                            contentDescription = null,
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .padding(top = 24.dp)
                                .align(Alignment.CenterVertically)
                                .weight(6f)
                                .fillMaxWidth()
                        )
                        Spacer(Modifier.weight(2f))
                    }
                }

            }
            if (showAnswer.value) {
                Column {
                    Text(
                        text = writings[it].refAnswer,
                        modifier = Modifier
                            .padding(horizontal = 24.dp),
                    )
                    Text(
                        text = writings[it].description,
                        modifier = Modifier
                            .padding(horizontal = 24.dp),
                    )
                }
            }

        }

    }
}

private fun shouldShowImage(text: String): Boolean {
    val pattern = Regex("""\[\*\]""")
    return pattern.findAll(text).toList().isNotEmpty()
}