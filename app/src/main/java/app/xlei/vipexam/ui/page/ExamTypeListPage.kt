package app.xlei.vipexam.ui.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.xlei.vipexam.R
import app.xlei.vipexam.core.data.constant.ExamType
import app.xlei.vipexam.core.ui.Banner
import app.xlei.vipexam.core.ui.OnError
import app.xlei.vipexam.core.ui.OnLoading
import app.xlei.vipexam.feature.history.HistoryViewModel
import app.xlei.vipexam.ui.UiState
import app.xlei.vipexam.ui.VipexamUiState
import app.xlei.vipexam.ui.components.ExamSearchBar
import compose.icons.FeatherIcons
import compose.icons.feathericons.Clock

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
    onExamTypeClick: (ExamType) -> Unit,
    onLastViewedClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    Column (
        modifier = modifier
    ){
        ExamSearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        )
        when (examTypeListUiState) {
            is UiState.Loading -> {
                OnLoading(examTypeListUiState.loadingMessageId)
            }

            is UiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        viewModel.examHistory.collectAsState().value.firstOrNull()?.let {
                            Banner(
                                title = stringResource(id = R.string.lastViewd),
                                desc = it.examName,
                                icon = FeatherIcons.Clock,
                                action = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                                        contentDescription = null
                                    )
                                }
                            ) {
                                onLastViewedClick.invoke(it.examId)
                            }
                        }
                    }
                    items(ExamType.entries.size) {
                        ListItem(
                            headlineContent = { Text(ExamType.entries[it].examTypeName) },
                            colors = ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                headlineColor = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier
                                .clickable {
                                    onExamTypeClick(ExamType.entries[it])
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