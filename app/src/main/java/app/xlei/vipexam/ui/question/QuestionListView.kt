package app.xlei.vipexam.ui.question

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.xlei.vipexam.data.ExamUiState

@Composable
fun questionListView(
    questionListUiState: ExamUiState.QuestionListUiState,
    onQuestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        item {
            ListItem(
                headlineContent = {
                    Text(questionListUiState.exam.examName)
                }
            )
        }
        questionListUiState.questions.forEach {
            item {
                Column {
                    ListItem(
                        headlineContent = { Text(it.second) },
                        modifier = Modifier
                            .clickable {
                                onQuestionClick(it.first)
                            }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}