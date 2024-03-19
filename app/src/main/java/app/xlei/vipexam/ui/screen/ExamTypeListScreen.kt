package app.xlei.vipexam.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.xlei.vipexam.core.data.constant.ExamType
import app.xlei.vipexam.ui.UiState
import app.xlei.vipexam.ui.VipexamUiState
import app.xlei.vipexam.ui.page.ExamTypeListView

/**
 * Exam type list screen
 * 试卷类型列表页面
 * @param examTypeListUiState 试卷类型列表
 * @param onExamTypeClick 试卷类型点击事件
 * @param widthSizeClass 屏幕宽度
 * @param modifier
 * @receiver
 * @receiver
 */
@Composable
fun ExamTypeListScreen(
    examTypeListUiState: UiState<VipexamUiState.ExamTypeListUiState>,
    onExamTypeClick: (ExamType) -> Unit,
    onLastViewedClick: (String) -> Unit,
    widthSizeClass: WindowWidthSizeClass,
    modifier: Modifier = Modifier
) {
    when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            ExamTypeListView(
                examTypeListUiState = examTypeListUiState,
                onExamTypeClick = onExamTypeClick,
                onLastViewedClick = onLastViewedClick
            )
        }

        WindowWidthSizeClass.Medium -> {
            ExamTypeListView(
                examTypeListUiState = examTypeListUiState,
                onExamTypeClick = onExamTypeClick,
                onLastViewedClick = onLastViewedClick
            )
        }

        WindowWidthSizeClass.Expanded -> {
            Column(
                modifier = modifier
                    .padding(horizontal = 24.dp)
                    .clip(MaterialTheme.shapes.extraLarge)
                //.background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                ExamTypeListView(
                    examTypeListUiState = examTypeListUiState,
                    onExamTypeClick = onExamTypeClick,
                    onLastViewedClick = onLastViewedClick,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }
}


