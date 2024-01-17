package app.xlei.vipexam.ui.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.xlei.vipexam.core.data.constant.Constants
import app.xlei.vipexam.ui.VipexamUiState

@Composable
fun examTypeListView(
    examTypeListUiState: VipexamUiState.ExamTypeListUiState,
    onExamTypeClicked: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(examTypeListUiState.examTypeList.size) {
            ListItem(
                headlineContent = { Text(stringResource(examTypeListUiState.examTypeList[it])) },
                modifier = Modifier
                    .clickable {
                        onExamTypeClicked(Constants.EXAM_TYPES[it].first)
                    }
            )
            HorizontalDivider()
        }
    }
}