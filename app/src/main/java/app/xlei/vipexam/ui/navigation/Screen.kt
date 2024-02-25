package app.xlei.vipexam.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(
    val route: String,
    val navArguments: List<NamedNavArgument> = emptyList()
) {
    data object Login : Screen("login")

    data object ExamTypeList : Screen(
        route = "examTypeList",
    )

    data object ExamList : Screen(
        route = "examList/{examType}",
        navArguments = listOf(navArgument("examType") {
            type = NavType.StringType
        })
    ) {
        fun createRoute(examType: String) = "examList/${examType}"
    }

    data object Exam : Screen(
        route = "exam/{examId}",
        navArguments = listOf(navArgument("examId") {
            type = NavType.StringType
        })
    ){
        fun createRoute(examId: String) = "exam/${examId}"
    }

    data object Question : Screen(
        route = "question/{examId}/{questionName}",
        navArguments = listOf(
            navArgument("examId") {
                type = NavType.StringType
            },
            navArgument("questionName") {
                type = NavType.StringType
            },
        )
    ){
        fun createRoute(examId: String, question: String) = "question/${examId}/${question}"
    }
}


