package app.xlei.vipexam.ui.question

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier

@Composable
fun questionListView(
    name: String,
    questions: List<Pair<String, String>>,
    onQuestionClick: (String) -> Unit,
){
    Column {
        ListItem(
            headlineContent = { Text(name) }
        )
        questions.forEach {
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