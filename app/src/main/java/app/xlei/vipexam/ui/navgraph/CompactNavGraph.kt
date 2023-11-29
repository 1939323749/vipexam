package app.xlei.vipexam.ui.navgraph

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import app.xlei.vipexam.R
import app.xlei.vipexam.constant.Constants
import app.xlei.vipexam.data.ExamList
import app.xlei.vipexam.ui.navigation.HomeScreen
import app.xlei.vipexam.ui.page.ExamPage
import app.xlei.vipexam.ui.page.examListView
import app.xlei.vipexam.ui.page.examTypeListView

fun NavGraphBuilder.compactHomeGraph(
    selectedExamType: MutableState<String>,
    selectedExamList: MutableState<ExamList>,
    currentPage: MutableState<String>,
    selectedExamId: MutableState<String>,
    onExamTypeClicked: (String) -> Unit,
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
    ) {
        composable(route = HomeScreen.ExamTypeList.name) {
            examTypeListView(
                onExamTypeClicked = onExamTypeClicked,
                onFirstItemAppear = {},
                onFirstItemHidden = {},
            )
        }
        composable(route = HomeScreen.ExamList.name) {
            examListView(
                currentPage = currentPage.value,
                examList = selectedExamList.value,
                isPractice = selectedExamType.value == Constants.EXAMTYPES.toMap()[R.string.practice_exam],
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
        ExamPage(
            examId = selectedExamId.value,
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