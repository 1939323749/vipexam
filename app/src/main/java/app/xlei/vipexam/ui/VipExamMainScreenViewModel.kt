package app.xlei.vipexam.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.xlei.vipexam.R
import app.xlei.vipexam.core.data.constant.Constants
import app.xlei.vipexam.core.data.paging.ExamListApi
import app.xlei.vipexam.core.data.repository.ExamHistoryRepository
import app.xlei.vipexam.core.data.util.Preferences
import app.xlei.vipexam.core.database.module.User
import app.xlei.vipexam.core.domain.AddUserUseCase
import app.xlei.vipexam.core.domain.DeleteUserUseCase
import app.xlei.vipexam.core.domain.GetAllUsersUseCase
import app.xlei.vipexam.core.network.module.NetWorkRepository
import app.xlei.vipexam.ui.appbar.AppBarTitle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class VipExamMainScreenViewModel @Inject constructor(
    vipexamUiState: VipexamUiState,
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val addUserUseCase: AddUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val examHistoryRepository: ExamHistoryRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(vipexamUiState)
    val uiState: StateFlow<VipexamUiState> = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(
                loginUiState = UiState.Loading(R.string.loading)
            )
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val users = getAllUsersUseCase()
                users.collect { usersList ->
                    _uiState.update {
                        it.copy(
                            loginUiState = UiState.Success(
                                uiState = VipexamUiState.LoginUiState(
                                    account = usersList.firstOrNull()?.account ?: "",
                                    password = usersList.firstOrNull()?.password ?: "",
                                    loginResponse = null,
                                    users = users,
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    fun login() {
        _uiState.update {
            it.copy(
                examTypeListUiState = UiState.Loading(R.string.loading)
            )
        }
        viewModelScope.launch {
            var organization: String
            runBlocking {
                organization = Preferences.organization.first()
            }
            val loginUiState = (_uiState.value.loginUiState as UiState.Success).uiState
            NetWorkRepository.getToken(
                account = loginUiState.account,
                password = loginUiState.password,
                organization = organization,
            ).onSuccess { loginResponse ->
                _uiState.update {
                    it.copy(
                        examTypeListUiState = UiState.Success(
                            uiState = VipexamUiState.ExamTypeListUiState(
                                examTypeList = Constants.EXAM_TYPES.toList().map { examType ->
                                    examType.first
                                },
                                examListUiState = UiState.Loading(R.string.loading)
                            ),
                        ),
                        loginUiState = UiState.Success(
                            uiState =  loginUiState.copy(
                                loginResponse = loginResponse
                            )
                        ),
                    )
                }
                addCurrentUser()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        examTypeListUiState = UiState.Error(
                            errorMessageId = R.string.login_error,
                            msg = error.message
                        )
                    )
                }
                return@launch
            }
        }
    }

    private fun addCurrentUser(){
        viewModelScope.launch {
            val loginUiState = (_uiState.value.loginUiState as UiState.Success).uiState
            addUserUseCase(
                User(
                    account = loginUiState.account,
                    password = loginUiState.password,
                )
            )
        }
    }

    fun setAccount(account: String) {
        val loginUiState = (_uiState.value.loginUiState as UiState.Success).uiState

        _uiState.update {
            it.copy(
                loginUiState = UiState.Success(
                    loginUiState.copy(
                        account = account,
                    ),
                )
            )
        }
    }

    fun setPassword(password: String) {
        val loginUiState = (_uiState.value.loginUiState as UiState.Success).uiState

        _uiState.update {
            it.copy(
                loginUiState = UiState.Success(
                    loginUiState.copy(
                        password = password,
                    ),
                )
            )
        }
    }

    fun setExamType(type: Int) {
        ExamListApi.setType(Constants.EXAM_TYPES.toMap()[type]!!)
        viewModelScope.launch {
            NetWorkRepository.getExamList(
                page = "1",
                examStyle = Constants.EXAM_TYPES.toMap()[type]!!,
                examTypeEName = "ve01002"
            ).onSuccess { examList ->
                val examTypeListUiState = (_uiState.value.examTypeListUiState as UiState.Success).uiState
                val examListUiState = UiState.Success(
                    uiState = VipexamUiState.ExamListUiState(
                        examType = type,
                        questionListUiState = UiState.Loading(R.string.loading)
                    )
                )
                _uiState.update {
                    it.copy(
                        examTypeListUiState = UiState.Success(
                            examTypeListUiState.copy(
                            examListUiState = examListUiState
                        ),),
                        examListUiState = examListUiState
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        examListUiState = UiState.Error(R.string.internet_error)
                    )
                }
            }
        }
    }

    fun setTitle(title: AppBarTitle) {
        _uiState.update {
            it.copy(
                title = title
            )
        }
    }

    fun setQuestion(examName: String ,examId: String, question: String) {
        val questionListUiState = (_uiState.value.questionListUiState as UiState.Success<VipexamUiState.QuestionListUiState>).uiState
        _uiState.update {
            it.copy(
                questionListUiState = UiState.Success(
                    questionListUiState.copy(
                        question = question,
                    )
                ) ,
                title = AppBarTitle.Exam(examName = examName, examId = examId, question = question)
            )
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            deleteUserUseCase(user)
        }
    }

    suspend fun setExam(examId: String) {
        _uiState.update {
            it.copy(
                questionListUiState = UiState.Loading(R.string.loading)
            )
        }
        NetWorkRepository.getExam(examId)
            .onSuccess { exam ->
                when (val _questionListUiState = _uiState.value.questionListUiState) {
                    is UiState.Success -> {
                        _uiState.update {
                            it.copy(
                                questionListUiState = UiState.Success(
                                    _questionListUiState.uiState.copy(
                                        exam = exam,
                                        questions = NetWorkRepository.getQuestions(exam.muban)
                                    )
                                ),
                            )
                        }
                    }
                    else -> {
                        val questionListUiState = UiState.Success(
                            VipexamUiState.QuestionListUiState(
                                exam = exam,
                                questions = NetWorkRepository.getQuestions(exam.muban),
                                question = exam.muban.map {muban->
                                    muban.ename }.first()
                            )
                        )
                        _uiState.update {
                            it.copy(
                                questionListUiState = questionListUiState,
                            )
                        }
                    }
                }
                withContext(Dispatchers.IO){
                    examHistoryRepository.insertHistory(examName = exam.examName, examId = examId)
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        questionListUiState = UiState.Error(R.string.internet_error)
                    )
                }
                return
            }
    }
}