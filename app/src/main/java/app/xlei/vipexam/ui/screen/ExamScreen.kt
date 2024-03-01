package app.xlei.vipexam.ui.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import app.xlei.vipexam.ui.VipexamUiState
import app.xlei.vipexam.ui.expanded.ExamScreenSupportingPane
import app.xlei.vipexam.ui.page.ExamPage


@Composable
fun ExamScreen(
    questionListUiState: VipexamUiState.QuestionListUiState,
    setQuestion: (String,String,String) -> Unit,
    widthSizeClass: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            ExamPage(
                questionListUiState = questionListUiState,
                setQuestion = setQuestion,
                navController = navController
            )
        }

        WindowWidthSizeClass.Medium -> {
            ExamPage(
                questionListUiState = questionListUiState,
                setQuestion = setQuestion,
                navController = navController
            )
        }

        WindowWidthSizeClass.Expanded -> {
            Row (
                modifier = modifier
            ){
                ElevatedCard(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .weight(1f)
                ) {
                    ExamPage(
                        questionListUiState = questionListUiState,
                        setQuestion = setQuestion,
                        navController = navController,
                        showFab = false
                    )
                }
                ExamScreenSupportingPane(
                    questionListUiState = questionListUiState,
                    navController = navController,
                    modifier = Modifier
                        .width(360.dp)
                        .padding(end = 24.dp)
                        .fillMaxSize()
                )
            }
        }
    }
}
