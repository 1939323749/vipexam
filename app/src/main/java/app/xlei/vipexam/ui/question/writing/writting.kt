package app.xlei.vipexam.ui.question.writing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.xlei.vipexam.core.network.module.getExamResponse.Muban
import app.xlei.vipexam.core.ui.container.VipexamArticleContainer
import app.xlei.vipexam.core.ui.container.VipexamImageContainer
import app.xlei.vipexam.preference.LocalShowAnswer


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WritingView(
    viewModel: WritingViewModel = hiltViewModel(),
    muban: Muban,
){
    val showAnswer = LocalShowAnswer.current.isShowAnswer()
    viewModel.setMuban(muban)
    viewModel.setWritings()
    val uiState by viewModel.uiState.collectAsState()
    writing(
        writings = uiState.writings,
        showAnswer = showAnswer
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun writing(
    writings: List<WritingUiState.Writing>,
    showAnswer: Boolean,
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
                    VipexamImageContainer(imageId = writings[it].image)
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