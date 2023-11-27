package app.xlei.vipexam.ui

import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import app.xlei.vipexam.R
import app.xlei.vipexam.constant.Constants
import app.xlei.vipexam.data.ExamUiState
import app.xlei.vipexam.ui.page.ExamPage
import app.xlei.vipexam.ui.page.examListView
import app.xlei.vipexam.ui.page.examTypeListView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun NavGraphBuilder.compactHomeGraph(
    uiState: ExamUiState,
    coroutine: CoroutineScope,
    onExamTypeClicked:(String)->Unit,
    onExamClick: (String) -> Unit,
    onPreviousPageClicked: () -> Unit,
    onNextPageClicked: () -> Unit,
    showAnswer: MutableState<Boolean>,
    navController: NavController,
    isFirstItemHidden: MutableState<Boolean>,
    onFirstItemHidden: (String) -> Unit,
    onFirstItemAppear: () -> Unit,
    refresh: () -> Unit,
){
    navigation(
        startDestination = VipExamScreen.ExamType.name,
        route = VipExamScreen.CompactLoggedIn.name,
    ){
        composable(route = VipExamScreen.ExamType.name){
            examTypeListView(
                onExamTypeClicked = onExamTypeClicked,
                onFirstItemAppear = {},
                onFirstItemHidden = {},
            )
        }
        composable(route= VipExamScreen.ExamList.name){
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
                        isFirstItemHidden.value =true
                    },
                ) {
                    isFirstItemHidden.value = false
                }
            }
        }
        composable(route = VipExamScreen.Exam.name){
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