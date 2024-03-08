package app.xlei.vipexam.ui.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.xlei.vipexam.core.data.constant.Constants
import app.xlei.vipexam.core.ui.OnError
import app.xlei.vipexam.core.ui.OnLoading
import app.xlei.vipexam.ui.UiState
import app.xlei.vipexam.ui.VipexamUiState
import app.xlei.vipexam.ui.components.ExamSearchBar

/**
 * Exam type list view
 * 试卷类型列表
 * @param examTypeListUiState 试卷类型列表
 * @param onExamTypeClick 试卷类型点击事件
 * @param modifier
 * @receiver
 */
@Composable
fun ExamTypeListView(
    examTypeListUiState: UiState<VipexamUiState.ExamTypeListUiState>,
    onExamTypeClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column (
        modifier = modifier
    ){
        ExamSearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )
        when (examTypeListUiState) {
            is UiState.Loading -> {
                OnLoading(examTypeListUiState.loadingMessageId)
            }

            is UiState.Success -> {
                LazyColumn(
                    modifier = modifier
                ) {
                    items(examTypeListUiState.uiState.examTypeList.size) {
                        ListItem(
                            headlineContent = { Text(stringResource(examTypeListUiState.uiState.examTypeList[it])) },
                            modifier = Modifier
                                .clickable {
                                    onExamTypeClick(Constants.EXAM_TYPES[it].first)
                                }
                        )
                        HorizontalDivider()
                    }
                }
            }

            is UiState.Error -> {
                OnError(
                    textId =  examTypeListUiState.errorMessageId,
                    error = examTypeListUiState.msg
                )
            }
        }
    }

}