package app.xlei.vipexam.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import app.xlei.vipexam.R

enum class HomeScreen(@StringRes val title: Int, val icon: ImageVector) {
    Login(title = R.string.login, Icons.Default.Home),
    LoggedIn(title = R.string.expandedLoggedIn, Icons.Default.Edit),
    ExamTypeWithExamList(title = R.string.examTypeWithExamList, Icons.AutoMirrored.Filled.List),
    QuestionListWithQuestion(title = R.string.questionsWithQuestion, Icons.Default.Edit),
    ExamListWithQuestions(title = R.string.examListWithQuestions, Icons.AutoMirrored.Filled.List),
}

class HomeScreenNavigationActions(navController: NavController) {
    val navigateToExamListWithQuestions: () -> Unit = {
        navController.navigate(HomeScreen.ExamListWithQuestions.name) {
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToQuestionsWithQuestion: () -> Unit = {
        navController.navigate(HomeScreen.QuestionListWithQuestion.name) {
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToLoggedIn: () -> Unit = {
        navController.navigate(HomeScreen.LoggedIn.name) {
            launchSingleTop = true
            restoreState = true
        }
    }
}