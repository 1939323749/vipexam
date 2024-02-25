package app.xlei.vipexam.ui.expanded

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import app.xlei.vipexam.R
import app.xlei.vipexam.core.ui.DateText
import app.xlei.vipexam.feature.history.HistoryViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
fun ExamListScreenSupportingPane(
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel(),
    onExamClick: (String) -> Unit = {},
){
    val history by viewModel.examHistory.collectAsState()
    Scaffold(
        topBar = { TopAppBar(title = { Text(text = stringResource(id = R.string.history)) }) },
        modifier = modifier
    ) {padding->
        ElevatedCard(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ){
                items(history.size){
                    ListItem(
                        headlineContent = {
                            Text(
                                text = history[it].examName
                            ) },
                        supportingContent = {
                            DateText(history[it].lastOpen)
                        },
                        modifier = Modifier
                            .clickable {
                                onExamClick.invoke(history[it].examId)
                            }
                    )
                }
            }
        }
    }
}
