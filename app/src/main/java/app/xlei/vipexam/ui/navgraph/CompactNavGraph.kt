package app.xlei.vipexam.ui.navgraph

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import app.xlei.vipexam.ui.ExamViewModel
import app.xlei.vipexam.ui.navigation.HomeScreen
import app.xlei.vipexam.ui.page.ExamPage
import app.xlei.vipexam.ui.page.examListView
import app.xlei.vipexam.ui.page.examTypeListView

fun NavGraphBuilder.compactHomeGraph(
    viewModel: ExamViewModel,
    onExamTypeClicked: (Int) -> Unit,
    onExamClick: (String) -> Unit,
    onPreviousPageClicked: () -> Unit,
    onNextPageClicked: () -> Unit,
    setQuestion: (String) -> Unit,
    refresh: () -> Unit,
){
    navigation(
        startDestination = HomeScreen.ExamTypeList.name,
        route = HomeScreen.CompactLoggedIn.name,
    ) {
        composable(route = HomeScreen.ExamTypeList.name) {
            val uiState by viewModel.uiState.collectAsState()
            examTypeListView(
                examTypeListUiState = uiState.examTypeListUiState,
                onExamTypeClicked = onExamTypeClicked,
            )
        }
        composable(route = HomeScreen.ExamList.name) {
            val uiState by viewModel.uiState.collectAsState()
            examListView(
                examListUiState = uiState.examListUiState,
                onPreviousPageClicked = onPreviousPageClicked,
                onNextPageClicked = onNextPageClicked,
                onExamClick = onExamClick,
                refresh = refresh,
            )
        }
    }
    composable(route = HomeScreen.Exam.name) {
        val uiState by viewModel.uiState.collectAsState()
        ExamPage(
            questionListUiState = uiState.questionListUiState,
            setQuestion = setQuestion,
        )
    }
}