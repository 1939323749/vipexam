package app.xlei.vipexam.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.xlei.vipexam.R
import app.xlei.vipexam.core.ui.OnError
import app.xlei.vipexam.core.ui.OnLoading
import app.xlei.vipexam.core.ui.PageLoader
import app.xlei.vipexam.ui.UiState
import app.xlei.vipexam.ui.VipexamUiState
import app.xlei.vipexam.ui.expanded.ExamListScreenSupportingPane
import app.xlei.vipexam.ui.page.ExamListView

/**
 * Exam list screen
 * 试卷列表页面
 * @param examListUiState 试卷列表
 * @param onLastExamClick 最近试卷点击事件
 * @param onExamClick 试卷点击事件
 * @param widthSizeClass 屏幕宽度
 * @param modifier
 * @receiver
 * @receiver
 * @receiver
 */
@Composable
fun ExamListScreen(
    examListUiState: UiState<VipexamUiState.ExamListUiState>,
    onLastExamClick: (String) -> Unit,
    onExamClick: (String) -> Unit,
    widthSizeClass: WindowWidthSizeClass,
    modifier: Modifier = Modifier
) {
    when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            when (examListUiState) {
                is UiState.Success -> {
                    ExamListView(
                        type = examListUiState.uiState.examType,
                        onExamClick = onExamClick,
                    )
                }

                is UiState.Loading -> {
                    PageLoader()
                }

                is UiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.internet_error),
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        WindowWidthSizeClass.Medium -> {
            when (examListUiState) {
                is UiState.Success -> {
                    Row(
                        modifier = modifier
                    ) {
                        ElevatedCard(
                            modifier = Modifier
                                .width(360.dp)
                        ) {
                            ExamListView(
                                type = examListUiState.uiState.examType,
                                onExamClick = onExamClick,
                            )
                        }
                        Spacer(modifier = Modifier.width(24.dp))
                    }
                }

                is UiState.Loading -> {
                    OnLoading()
                }

                is UiState.Error -> {
                    OnError()
                }
            }

        }

        WindowWidthSizeClass.Expanded -> {
            Row(
                modifier = modifier
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .weight(1f)
                ) {
                    when (examListUiState) {
                        is UiState.Success -> {
                            ExamListView(
                                type = examListUiState.uiState.examType,
                                onExamClick = onExamClick,
                            )
                        }

                        is UiState.Loading -> {
                            //PageLoader()
                        }

                        is UiState.Error -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(R.string.internet_error),
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
                ExamListScreenSupportingPane(
                    modifier = Modifier
                        .width(360.dp)
                        .padding(end = 24.dp)
                        .fillMaxSize()
                ){
                    onExamClick.invoke(it)
                }
            }
        }
    }
}