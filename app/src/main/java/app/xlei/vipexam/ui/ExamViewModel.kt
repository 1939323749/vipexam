package app.xlei.vipexam.ui

import androidx.lifecycle.ViewModel
import app.xlei.vipexam.data.ExamUiState
import app.xlei.vipexam.data.LoginResponse
import app.xlei.vipexam.ui.page.getExam
import app.xlei.vipexam.ui.page.getExamList
import app.xlei.vipexam.ui.page.getToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ExamViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(ExamUiState())
    val uiState: StateFlow<ExamUiState> = _uiState.asStateFlow()

    suspend fun login(account: String, password: String): LoginResponse {
        val loginResponse=getToken(account,password)
        if(loginResponse.code=="1"){
            _uiState.update {
                it.copy(
                    account=account,
                    password=password,
                    token = loginResponse.token
                )
            }
        }
        return loginResponse
    }

    suspend fun _getExamList(){
        _uiState.update {
            it.copy(
                examList = it.token?.let { it1 -> getExamList(account = it.account, token = it1, currentPage = it.currentPage) }
            )
        }
    }

    suspend fun nextPage() {
        _uiState.update {
            it.copy(
                examList = it.token?.let { it1 -> getExamList(account = it.account, token = it1, currentPage = (it.currentPage.toInt()+1).toString()) },
                currentPage = (it.currentPage.toInt()+1).toString()
            )
        }
    }

    suspend fun previousPage() {
        _uiState.update {
            it.copy(
                examList = it.token?.let { it1 -> getExamList(account = it.account, token = it1, currentPage = (it.currentPage.toInt()-1).toString()) },
                currentPage = (it.currentPage.toInt()-1).toString()
            )
        }
    }

    fun setAccount(account: String){
        _uiState.update {
            it.copy(
                account = account
            )
        }
    }

    fun setPassword(password: String){
        _uiState.update {
            it.copy(
                password = password
            )
        }
    }

    suspend fun _getExam(examId: String):Boolean{
        _uiState.update {
            it.copy(
                exam = uiState.value.token?.let { it1 -> getExam(examId = examId, account = uiState.value.account, token = it1) }
            )
        }
        return true
    }

    suspend fun refresh() {
        _uiState.update {
            it.copy(
                examList = it.token?.let { it1 -> getExamList(account = it.account, token = it1, currentPage = it.currentPage) }
            )
        }
    }
}