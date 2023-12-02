package app.xlei.vipexam.ui.navgraph

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import app.xlei.vipexam.ui.ExamViewModel
import app.xlei.vipexam.ui.examListWithQuestionsView
import app.xlei.vipexam.ui.examTypeListWithExamListView
import app.xlei.vipexam.ui.navigation.HomeScreen
import app.xlei.vipexam.ui.questionListWithQuestionView

fun NavGraphBuilder.expandedHomeGraph(
    viewModel: ExamViewModel,
    showAnswer: MutableState<Boolean>,
    onExamTypeClick: (Int) -> Unit,
    onExamClick: (String) -> Unit,
    onPreviousPageClicked: () -> Unit,
    onNextPageClicked: () -> Unit,
    onQuestionClick: (String) -> Unit,
    refresh: () -> Unit,
){
    navigation(
        startDestination = HomeScreen.ExamTypeWithExamList.name,
        route = HomeScreen.ExpandedLoggedIn.name
    ){
        composable(
            route = HomeScreen.ExamTypeWithExamList.name,
        ){
            val uiState by viewModel.uiState.collectAsState()
            examTypeListWithExamListView(
                examTypeListUiState = uiState.examTypeListUiState,
                onExamTypeClick = onExamTypeClick,
                onExamClick = onExamClick,
                onPreviousPageClick = onPreviousPageClicked,
                onNextPageClick = onNextPageClicked,
                refresh = refresh,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }
        composable(
            route = HomeScreen.ExamListWithQuestions.name,
        ){
            val uiState by viewModel.uiState.collectAsState()
            examListWithQuestionsView(
                examListUiState = uiState.examListUiState,
                onPreviousPageClicked = onPreviousPageClicked,
                onNextPageClicked = onNextPageClicked,
                onExamClick = onExamClick,
                refresh = {},
                onQuestionClick = onQuestionClick,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }
        composable(
            route = HomeScreen.QuestionListWithQuestion.name,
        ) {
            val uiState by viewModel.uiState.collectAsState()
            questionListWithQuestionView(
                questionListUiState = uiState.questionListUiState,
                showAnswer = showAnswer,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }
    }
}