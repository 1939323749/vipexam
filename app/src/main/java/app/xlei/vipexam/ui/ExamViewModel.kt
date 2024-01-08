package app.xlei.vipexam.ui

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.xlei.vipexam.constant.Constants
import app.xlei.vipexam.data.ExamUiState
import app.xlei.vipexam.data.models.room.User
import app.xlei.vipexam.data.network.Repository
import app.xlei.vipexam.logic.DB
import app.xlei.vipexam.ui.navigation.HomeScreen
import app.xlei.vipexam.ui.navigation.HomeScreenNavigationActions
import app.xlei.vipexam.util.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


data class LoginSetting(
    val isRememberAccount: Boolean,
    val isAutoLogin: Boolean,
)

@HiltViewModel
class ExamViewModel @Inject constructor(
    examUiState: ExamUiState,
) : ViewModel() {
    private val _uiState = MutableStateFlow(examUiState)
    private lateinit var homeScreenNavigationActions: HomeScreenNavigationActions
    val uiState: StateFlow<ExamUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _uiState.update {
                    it.copy(
                        loginUiState = it.loginUiState.copy(
                            users = DB.repository.getAllUsers(),
                        )
                    )
                }
                if (_uiState.value.loginUiState.users.isNotEmpty()) {
                    _uiState.update {
                        it.copy(
                            loginUiState = it.loginUiState.copy(
                                account = it.loginUiState.users[0].account,
                                password = it.loginUiState.users[0].password,
                            )
                        )
                    }
                }
            }
        }
    }

    fun setScreenType(widthSizeClass: WindowWidthSizeClass) {
        _uiState.update {
            it.copy(
                screenType = widthSizeClass
            )
        }
    }

    fun login() {
        viewModelScope.launch {
            Repository.getToken(
                account = _uiState.value.loginUiState.account,
                password = _uiState.value.loginUiState.password,
            ).onSuccess { loginResponse ->
                _uiState.update {
                    it.copy(
                        loginUiState = it.loginUiState.copy(
                            loginResponse = loginResponse
                        ),
                    )
                }
            }.onFailure {
                return@launch
            }
            _uiState.value.loginUiState.loginResponse?.let {
                if (it.code != "1") {
                    return@launch
                }
                if (_uiState.value.examTypeListUiState.examTypeList.isEmpty()) {
                    Repository.getExamList(
                        page = "1",
                        type = Constants.EXAMTYPES[0].second,
                    ).onSuccess { examList ->
                        _uiState.update { examUiState ->
                            examUiState.copy(
                                loginUiState = examUiState.loginUiState.copy(
                                    users = examUiState.loginUiState.users.filterNot { user ->
                                        user.account == _uiState.value.loginUiState.account
                                    }.plus(
                                        User(
                                            account = _uiState.value.loginUiState.account,
                                            password = _uiState.value.loginUiState.password,
                                        )
                                    )
                                ),
                                examTypeListUiState = examUiState.examTypeListUiState.copy(
                                    examTypeList = Constants.EXAMTYPES.toList().map { examType ->
                                        examType.first
                                    },
                                    examListUiState = ExamUiState.ExamListUiState(
                                        examType = Constants.EXAMTYPES[0].first,
                                        examList = examList,
                                        currentPage = "1",
                                        questionListUiState = null,
                                    )
                                ),
                                examListUiState = ExamUiState.ExamListUiState(
                                    examType = Constants.EXAMTYPES[0].first,
                                    examList = examList,
                                    currentPage = "1",
                                    questionListUiState = null,
                                ),
                            )
                        }
                    }.onFailure {
                        return@launch
                    }
                }
                navigate(HomeScreen.LoggedIn)
                if (_uiState.value.loginUiState.setting.isRememberAccount)
                    withContext(Dispatchers.IO) {
                        DB.repository.insertUser(
                            user = User(
                                account = _uiState.value.loginUiState.account,
                                password = _uiState.value.loginUiState.password,
                            )
                        )
                    }
            }
        }
    }

    private fun navigate(destination: HomeScreen) {
        when (destination) {
            HomeScreen.ExamListWithQuestions -> {
                homeScreenNavigationActions.navigateToExamListWithQuestions()
            }

            HomeScreen.LoggedIn -> {
                homeScreenNavigationActions.navigateToLoggedIn()
            }

            HomeScreen.QuestionListWithQuestion -> {
                homeScreenNavigationActions.navigateToQuestionsWithQuestion()
            }

            else -> return
        }
        _uiState.update {
            it.copy(
                currentRoute = destination
            )
        }
    }

    fun setNavigationActions(homeScreenNavigationActions: HomeScreenNavigationActions) {
        this.homeScreenNavigationActions = homeScreenNavigationActions
        if (_uiState.value.loginUiState.setting.isAutoLogin
            && _uiState.value.loginUiState.loginResponse == null
        )
            login()
    }

    fun nextPage() {
        viewModelScope.launch {
            val currentPage = "${_uiState.value.examListUiState.currentPage.toInt() + 1}"
            Repository.getExamList(
                page = currentPage,
                type = Constants.EXAMTYPES.toMap()[_uiState.value.examListUiState.examType]!!,
            ).onSuccess { examList ->
                _uiState.update {
                    it.copy(
                        examTypeListUiState = it.examTypeListUiState.copy(
                            examListUiState = it.examListUiState.copy(
                                examList = examList,
                                currentPage = currentPage,
                            )
                        ),
                        examListUiState = it.examListUiState.copy(
                            examList = examList,
                            currentPage = currentPage,
                        )
                    )
                }
            }.onFailure {
                return@launch
            }
        }
    }

    fun previousPage() {
        viewModelScope.launch {
            val currentPage = "${_uiState.value.examListUiState.currentPage.toInt() - 1}"
            Repository.getExamList(
                page = currentPage,
                type = Constants.EXAMTYPES.toMap()[_uiState.value.examListUiState.examType]!!,
            ).onSuccess { examList ->
                _uiState.update {
                    it.copy(
                        examTypeListUiState = it.examTypeListUiState.copy(
                            examListUiState = it.examListUiState.copy(
                                examList = examList,
                                currentPage = currentPage,
                            )
                        ),
                        examListUiState = it.examListUiState.copy(
                            examList = examList,
                            currentPage = currentPage,
                        )
                    )
                }
            }.onFailure {
                return@launch
            }
        }
    }

    fun setAccount(account: String) {
        _uiState.update {
            it.copy(
                loginUiState = it.loginUiState.copy(
                    account = account,
                ),
            )
        }
    }

    fun setPassword(password: String) {
        _uiState.update {
            it.copy(
                loginUiState = it.loginUiState.copy(
                    password = password,
                ),
            )
        }
    }

    fun refresh() {
        viewModelScope.launch {
            Repository.getExamList(
                page = _uiState.value.examListUiState.currentPage,
                type = Constants.EXAMTYPES.toMap()[_uiState.value.examListUiState.examType]!!,
            ).onSuccess { examList ->
                _uiState.update {
                    it.copy(
                        examTypeListUiState = it.examTypeListUiState.copy(
                            examListUiState = it.examListUiState.copy(
                                examList = examList,
                            )
                        ),
                        examListUiState = it.examListUiState.copy(
                            examList = examList,
                        )
                    )
                }
            }.onFailure {
                return@launch
            }

        }
    }

    fun setSetting(setting: LoginSetting) {
        _uiState.update {
            it.copy(
                loginUiState = it.loginUiState.copy(
                    setting = setting
                )
            )
        }
        Preferences.put(Preferences.autoLoginKey, setting.isAutoLogin)
        Preferences.put(Preferences.rememberAccountKey, setting.isRememberAccount)
    }

    fun setExamType(type: Int) {
        viewModelScope.launch {
            Repository.getExamList(
                page = "1",
                type = Constants.EXAMTYPES.toMap()[type]!!,
            ).onSuccess { examList ->
                _uiState.update {
                    it.copy(
                        examTypeListUiState = it.examTypeListUiState.copy(
                            examListUiState = it.examListUiState.copy(
                                examType = type,
                                examList = examList,
                            )
                        ),
                        examListUiState = it.examListUiState.copy(
                            examType = type,
                            examList = examList,
                            currentPage = "1",
                        )
                    )
                }
                navigate(HomeScreen.ExamListWithQuestions)
            }.onFailure {
                return@launch
            }
        }
    }

    fun setTitle(title: String? = null) {
        _uiState.update {
            it.copy(
                title = title ?: ""
            )
        }
    }

    fun setQuestion(question: String) {
        _uiState.update {
            it.copy(
                questionListUiState = it.questionListUiState.copy(
                    question = question,
                ),
                title = question
            )
        }
        navigate(HomeScreen.QuestionListWithQuestion)
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                DB.repository.deleteUser(user)
            }
            _uiState.update {
                it.copy(
                    loginUiState = it.loginUiState.copy(
                        users = it.loginUiState.users.filter { it != user }
                    )
                )
            }
        }
    }

    fun setExam(examId: String) {
        viewModelScope.launch {
            Repository.getExam(examId)
                .onSuccess { exam ->
                    _uiState.update {
                        it.copy(
                            examListUiState = it.examListUiState.copy(
                                examList = it.examTypeListUiState.examListUiState!!.examList,
                                questionListUiState = it.questionListUiState.copy(
                                    exam = exam,
                                    questions = Repository.getQuestions(exam.muban)
                                )
                            ),
                            questionListUiState = it.questionListUiState.copy(
                                exam = exam,
                                questions = Repository.getQuestions(exam.muban)
                            ),
                        )
                    }
                    if (_uiState.value.currentRoute == HomeScreen.ExamTypeWithExamList)
                        navigate(HomeScreen.ExamListWithQuestions)
                    else navigate(HomeScreen.QuestionListWithQuestion)
                }.onFailure {
                    return@launch
                }
        }
    }

    fun setCurrentRoute(homeScreen: HomeScreen) {
        _uiState.update {
            it.copy(
                currentRoute = homeScreen
            )
        }
    }
}