package app.xlei.vipexam.ui.question.writing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.xlei.vipexam.core.network.module.getExamResponse.Muban
import app.xlei.vipexam.core.ui.VipexamArticleContainer
import app.xlei.vipexam.preference.LocalShowAnswer
import coil.compose.AsyncImage


@Composable
fun WritingView(
    viewModel: WritingViewModel = hiltViewModel(),
    muban: Muban,
) {
    viewModel.setMuban(muban)
    viewModel.setWritings()
    val uiState by viewModel.uiState.collectAsState()
    writing(
        writings = uiState.writings,
    )
}

@Composable
private fun writing(
    writings: List<WritingUiState.Writing>,
) {
    val showAnswer = LocalShowAnswer.current.isShowAnswer()
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
                VipexamArticleContainer(
                    onDragContent = writings[it].question
                            + "\n\n" + writings[it].refAnswer
                            + "\n\n" + writings[it].description
                ) {
                    Text(
                        text = writings[it].question,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .padding(start = 4.dp, end = 4.dp)
                    )
                }

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
            if (showAnswer) {
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