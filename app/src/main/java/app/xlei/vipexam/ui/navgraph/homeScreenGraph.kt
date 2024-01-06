package app.xlei.vipexam.ui.navgraph

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import app.xlei.vipexam.ui.ExamViewModel
import app.xlei.vipexam.ui.HomeRoute
import app.xlei.vipexam.ui.examListWithQuestionsView
import app.xlei.vipexam.ui.examTypeListWithExamListView
import app.xlei.vipexam.ui.navigation.AppDestinations
import app.xlei.vipexam.ui.navigation.HomeScreen
import app.xlei.vipexam.ui.questionListWithQuestionView

fun NavGraphBuilder.homeScreen(
    logoText: MutableState<HomeScreen>,
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

@RequiresApi(Build.VERSION_CODES.P)
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
            examTypeListWithExamListView(
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
            examListWithQuestionsView(
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
            questionListWithQuestionView(
                questionListUiState = uiState.questionListUiState,
                setQuestion = viewModel::setTitle,
                widthSizeClass = widthSizeClass,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }
    }
}