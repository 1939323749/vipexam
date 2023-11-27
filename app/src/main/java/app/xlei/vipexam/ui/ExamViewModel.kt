package app.xlei.vipexam.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import app.xlei.vipexam.data.ExamUiState
import app.xlei.vipexam.data.LoginResponse
import app.xlei.vipexam.data.models.room.Setting
import app.xlei.vipexam.data.network.Repository
import app.xlei.vipexam.data.network.Repository.getToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ExamViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(ExamUiState())
    val uiState: StateFlow<ExamUiState> = _uiState.asStateFlow()

    suspend fun login(account: String, password: String):Boolean {
        val loginResponse=getToken(account,password)
        loginResponse?.let {
            _uiState.update {
                it.copy(
                    account=account,
                    password=password,
                )
            }
            return true
        }
        return false
    }

    suspend fun nextPage() {
        _uiState.update {
            it.copy(
                examList = Repository.getExamList(
                    page = it.currentPage,
                    type = it.examType,
                ),
                currentPage = (it.currentPage.toInt()+1).toString(),
            )
        }
    }

    suspend fun previousPage() {
        _uiState.update {
            it.copy(
                examList = Repository.getExamList(
                    page = it.currentPage,
                    type = it.examType,
                ),
                currentPage = (it.currentPage.toInt()-1).toString(),
            )
        }
    }

    fun setAccount(account: String){
        _uiState.update {
            it.copy(
                account = account,
            )
        }
    }

    fun setPassword(password: String){
        _uiState.update {
            it.copy(
                password = password,
            )
        }
    }

    suspend fun getExam(examId: String):Boolean{
        _uiState.update {
            it.copy(
                exam = Repository.getExam(examId = examId),
            )
        }
        return true
    }

    suspend fun refresh() {
        _uiState.update {
            it.copy(
                examList = Repository.getExamList(
                    page = it.currentPage,
                    type = it.examType,
                ),
                currentPage = it.currentPage,
            )
        }
    }

    fun setSetting(setting: Setting) {
        _uiState.update {
            it.copy(
                setting = setting
            )
        }
    }

    fun setExamType(type: String) {
        _uiState.update {
            it.copy(
                examType = type
            )
        }
    }
}