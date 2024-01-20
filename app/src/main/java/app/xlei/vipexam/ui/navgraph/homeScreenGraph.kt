package app.xlei.vipexam.ui.navgraph

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import app.xlei.vipexam.ui.ExamListWithQuestionsView
import app.xlei.vipexam.ui.ExamTypeListWithExamListView
import app.xlei.vipexam.ui.ExamViewModel
import app.xlei.vipexam.ui.HomeRoute
import app.xlei.vipexam.ui.QuestionListWithQuestionView
import app.xlei.vipexam.ui.navigation.AppDestinations
import app.xlei.vipexam.ui.navigation.HomeScreen

fun NavGraphBuilder.homeScreen(
    logoText: @Composable () -> Unit = {},
    homeNavController: NavHostController,
    widthSizeClass: WindowWidthSizeClass,
    openDrawer: () -> Unit,
) {
    composable(
        route = AppDestinations.HOME_ROUTE.name,
        deepLinks = listOf(
            navDeepLink {
                val EXAM_TYPE = "examType"
                val EXAM_ID = "examId"
                uriPattern =
                    "${AppDestinations.HOME_ROUTE}?$EXAM_TYPE={$EXAM_TYPE}&${EXAM_ID}=${EXAM_ID}"
            }
        )
    ) {
        HomeRoute(
            logoText = logoText,
            navController = homeNavController,
            widthSizeClass = widthSizeClass,
            openDrawer = openDrawer,
        )
    }
}

fun NavGraphBuilder.homeScreenGraph(
    viewModel: ExamViewModel,
    onExamTypeClick: (Int) -> Unit,
    onExamClick: (String) -> Unit,
    onPreviousPageClicked: () -> Unit,
    onNextPageClicked: () -> Unit,
    onQuestionClick: (String) -> Unit,
    refresh: () -> Unit,
    widthSizeClass: WindowWidthSizeClass,
) {
    navigation(
        startDestination = HomeScreen.ExamTypeWithExamList.name,
        route = HomeScreen.LoggedIn.name
    ){
        composable(
            route = HomeScreen.ExamTypeWithExamList.name,
        ){
            val uiState by viewModel.uiState.collectAsState()
            viewModel.setCurrentRoute(HomeScreen.ExamTypeWithExamList)
            ExamTypeListWithExamListView(
                examTypeListUiState = uiState.examTypeListUiState,
                onExamTypeClick = onExamTypeClick,
                onExamClick = onExamClick,
                onPreviousPageClick = onPreviousPageClicked,
                onNextPageClick = onNextPageClicked,
                refresh = refresh,
                modifier = Modifier.padding(horizontal = 24.dp),
                widthSizeClass = widthSizeClass,
            )
        }
        composable(
            route = HomeScreen.ExamListWithQuestions.name,
        ){
            val uiState by viewModel.uiState.collectAsState()
            viewModel.setCurrentRoute(HomeScreen.ExamListWithQuestions)
            ExamListWithQuestionsView(
                examListUiState = uiState.examListUiState,
                onPreviousPageClicked = onPreviousPageClicked,
                onNextPageClicked = onNextPageClicked,
                onExamClick = onExamClick,
                refresh = {},
                onQuestionClick = onQuestionClick,
                widthSizeClass = widthSizeClass,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }
        composable(
            route = HomeScreen.QuestionListWithQuestion.name,
        ) {
            val uiState by viewModel.uiState.collectAsState()
            viewModel.setCurrentRoute(HomeScreen.QuestionListWithQuestion)
            QuestionListWithQuestionView(
                questionListUiState = uiState.questionListUiState,
                setQuestion = viewModel::setTitle,
                widthSizeClass = widthSizeClass,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }
    }
}