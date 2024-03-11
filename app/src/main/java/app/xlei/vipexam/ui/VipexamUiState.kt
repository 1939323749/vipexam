package app.xlei.vipexam.ui

import androidx.annotation.StringRes
import app.xlei.vipexam.core.database.module.User
import app.xlei.vipexam.core.network.module.getExamResponse.GetExamResponse
import app.xlei.vipexam.core.network.module.login.LoginResponse
import app.xlei.vipexam.ui.appbar.AppBarTitle
import kotlinx.coroutines.flow.Flow

data class VipexamUiState(
    var loginUiState: UiState<LoginUiState>,
    var examTypeListUiState: UiState<ExamTypeListUiState>,
    var examListUiState: UiState<ExamListUiState>,
    var questionListUiState: UiState<QuestionListUiState>,
    val title: AppBarTitle,
) {
    data class LoginUiState(
        val account: String,
        val password: String,
        val loginResponse: LoginResponse?,
        val users: Flow<List<User>>,
    )

    data class ExamTypeListUiState(
        val examListUiState: UiState<ExamListUiState>,
    )

    data class ExamListUiState(
        val isReal: Boolean,
        val questionListUiState: UiState<QuestionListUiState>,
    )

    data class QuestionListUiState(
        val exam: GetExamResponse,
        val questions: List<Pair<String, String>>,
        val question: String?,
    )
}

sealed class UiState<out T> {
    data class Success<T>(val uiState: T) : UiState<T>()

    data class Loading<T>(@StringRes val loadingMessageId: Int) : UiState<T>()

    data class Error(@StringRes val errorMessageId: Int, val msg: String? = null) : UiState<Nothing>()
}