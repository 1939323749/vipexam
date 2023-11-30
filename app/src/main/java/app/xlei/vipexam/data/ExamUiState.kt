package app.xlei.vipexam.data

import app.xlei.vipexam.data.models.room.Setting
import app.xlei.vipexam.data.models.room.User
import app.xlei.vipexam.ui.SCREEN_TYPE
import app.xlei.vipexam.ui.navigation.HomeScreen

data class ExamUiState(
    val loginUiState: LoginUiState,
    val examTypeListUiState: ExamTypeListUiState,
    val examListUiState: ExamListUiState,
    val questionListUiState: QuestionListUiState,
    val title: String,
    val screenType: SCREEN_TYPE = SCREEN_TYPE.COMPACT,
    val currentRoute: HomeScreen = HomeScreen.Login,
) {
    data class LoginUiState(
        val account: String,
        val password: String,
        val loginResponse: LoginResponse?,
        val users: List<User>,
        val setting: Setting?,
        val connectivity: Boolean,
    )

    data class ExamTypeListUiState(
        val examTypeList: List<Int>,
        val examListUiState: ExamListUiState?,
    )

    data class ExamListUiState(
        val examType: Int,
        val examList: ExamList,
        val currentPage: String,
        val questionListUiState: QuestionListUiState?,
    )

    data class QuestionListUiState(
        val exam: Exam,
        val questions: List<Pair<String, String>>,
        val question: String?,
    )

}