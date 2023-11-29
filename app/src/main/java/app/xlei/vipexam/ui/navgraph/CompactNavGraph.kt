package app.xlei.vipexam.ui.navgraph

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import app.xlei.vipexam.R
import app.xlei.vipexam.constant.Constants
import app.xlei.vipexam.data.ExamUiState
import app.xlei.vipexam.ui.navigation.HomeScreen
import app.xlei.vipexam.ui.page.ExamPage
import app.xlei.vipexam.ui.page.examListView
import app.xlei.vipexam.ui.page.examTypeListView

fun NavGraphBuilder.compactHomeGraph(
    uiState: ExamUiState,
    onExamTypeClicked:(String)->Unit,
    onExamClick: (String) -> Unit,
    onPreviousPageClicked: () -> Unit,
    onNextPageClicked: () -> Unit,
    showAnswer: MutableState<Boolean>,
    isFirstItemHidden: MutableState<Boolean>,
    onFirstItemHidden: (String) -> Unit,
    onFirstItemAppear: () -> Unit,
    refresh: () -> Unit,
){
    navigation(
        startDestination = HomeScreen.ExamTypeList.name,
        route = HomeScreen.CompactLoggedIn.name,
    ){
        composable(route = HomeScreen.ExamTypeList.name) {
            examTypeListView(
                onExamTypeClicked = onExamTypeClicked,
                onFirstItemAppear = {},
                onFirstItemHidden = {},
            )
        }
        composable(route = HomeScreen.ExamList.name) {
            uiState.examList?.let { examList ->
                examListView(
                    currentPage = uiState.currentPage,
                    examList = examList,
                    isPractice = uiState.examType == Constants.EXAMTYPES.toMap()[R.string.practice_exam],
                    onPreviousPageClicked = onPreviousPageClicked,
                    onNextPageClicked = onNextPageClicked,
                    onExamClick = onExamClick,
                    refresh = refresh,
                    onFirstItemHidden = {
                        isFirstItemHidden.value = true
                    },
                ) {
                    isFirstItemHidden.value = false
                }
            }
        }
        composable(route = HomeScreen.Exam.name) {
            uiState.exam?.let { exam ->
                ExamPage(
                    exam = exam,
                    onFirstItemHidden = {
                        isFirstItemHidden.value = true
                        onFirstItemHidden(it)
                    },
                    onFirstItemAppear = {
                        isFirstItemHidden.value = false
                        onFirstItemAppear()
                    },
                    showAnswer = showAnswer,
                )
            }
        }
    }
}