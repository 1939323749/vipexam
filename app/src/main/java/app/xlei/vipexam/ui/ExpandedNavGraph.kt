package app.xlei.vipexam.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import app.xlei.vipexam.data.ExamList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun NavGraphBuilder.expandedHomeGraph(
    showAnswer: MutableState<Boolean>,
    coroutine: CoroutineScope,
    viewModel: ExamViewModel,
    selectedExamType: MutableState<String>,
    selectedExamList: MutableState<ExamList>,
    currentPage: MutableState<String>,
    selectedExamId: MutableState<String>,
    selectedQuestion: MutableState<String>,
    onExamClick: (String) -> Unit,
    onPreviousPageClicked: () -> Unit,
    onNextPageClicked: () -> Unit,
    onQuestionClick: (String) -> Unit,
){
    navigation(
        startDestination = VipExamScreen.ExamTypeWithExamList.name,
        route = VipExamScreen.ExpandedLoggedIn.name
    ){
        composable(
            route = VipExamScreen.ExamTypeWithExamList.name,
        ){
            examTypeListWithExamListView(
                onExamTypeClick = { selectedExamType.value = it },
                onExamClick = onExamClick,
                onPreviousPageClicked = onPreviousPageClicked,
                onNextPageClicked = onNextPageClicked,
                refresh = {
                    coroutine.launch {
                        viewModel.refresh()
                    } },
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }
        composable(
            route = VipExamScreen.ExamListWithQuestions.name,
        ){
            examListWithQuestionsView(
                examList = selectedExamList.value,
                currentPage = currentPage.value,
                examType = selectedExamType.value,
                examId = selectedExamId.value,
                onPreviousPageClicked = onPreviousPageClicked,
                onNextPageClicked = onNextPageClicked,
                onExamClick = {
                    selectedExamId.value = it },
                refresh = {},
                onQuestionClick = onQuestionClick,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }
        composable(
            route = VipExamScreen.QuestionsWithQuestion.name,
        ){
            questionsWithQuestionView(
                examId = selectedExamId.value,
                question = selectedQuestion.value,
                showAnswer = showAnswer,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }
    }
}