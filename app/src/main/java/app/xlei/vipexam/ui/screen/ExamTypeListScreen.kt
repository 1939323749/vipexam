package app.xlei.vipexam.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.xlei.vipexam.ui.UiState
import app.xlei.vipexam.ui.VipexamUiState
import app.xlei.vipexam.ui.page.ExamTypeListView

@Composable
fun ExamTypeListScreen(
    examTypeListUiState: UiState<VipexamUiState.ExamTypeListUiState>,
    onExamTypeClick: (Int) -> Unit,
    onExamClick: (String) -> Unit,
    widthSizeClass: WindowWidthSizeClass,
    modifier: Modifier = Modifier
) {
    when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            ExamTypeListView(
                examTypeListUiState = examTypeListUiState,
                onExamTypeClick = onExamTypeClick,
            )
        }

        WindowWidthSizeClass.Medium -> {
            ExamTypeListView(
                examTypeListUiState = examTypeListUiState,
                onExamTypeClick = onExamTypeClick,
            )
        }

        WindowWidthSizeClass.Expanded -> {
            ElevatedCard(
                modifier = modifier
                    .padding(horizontal = 24.dp)
            ) {
                ExamTypeListView(
                    examTypeListUiState = examTypeListUiState,
                    onExamTypeClick = onExamTypeClick,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }
}


