package app.xlei.vipexam.ui.navgraph

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import app.xlei.vipexam.data.ExamList
import app.xlei.vipexam.ui.examListWithQuestionsView
import app.xlei.vipexam.ui.examTypeListWithExamListView
import app.xlei.vipexam.ui.navigation.HomeScreen
import app.xlei.vipexam.ui.questionsWithQuestionView

fun NavGraphBuilder.expandedHomeGraph(
    showAnswer: MutableState<Boolean>,
    selectedExamType: MutableState<String>,
    selectedExamList: MutableState<ExamList>,
    currentPage: MutableState<String>,
    selectedExamId: MutableState<String>,
    selectedQuestion: MutableState<String>,
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
            examTypeListWithExamListView(
                onExamTypeClick = { selectedExamType.value = it },
                onExamClick = onExamClick,
                onPreviousPageClicked = onPreviousPageClicked,
                onNextPageClicked = onNextPageClicked,
                refresh = refresh,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }
        composable(
            route = HomeScreen.ExamListWithQuestions.name,
        ){
            examListWithQuestionsView(
                examList = selectedExamList.value,
                currentPage = currentPage.value,
                examType = selectedExamType.value,
                examId = selectedExamId.value,
                onPreviousPageClicked = onPreviousPageClicked,
                onNextPageClicked = onNextPageClicked,
                onExamClick = onExamClick,
                refresh = {},
                onQuestionClick = onQuestionClick,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }
        composable(
            route = HomeScreen.QuestionsWithQuestion.name,
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