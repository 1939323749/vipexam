package app.xlei.vipexam.ui

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

enum class SCREEN_TYPE(
) {
    COMPACT,
    EXPANDED,
}

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

    fun setScreenType(screenType: SCREEN_TYPE) {
        _uiState.update {
            it.copy(
                screenType = screenType
            )
        }
    }

    fun login() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    loginUiState = it.loginUiState.copy(
                        loginResponse = Repository.getToken(
                            account = it.loginUiState.account,
                            password = it.loginUiState.password,
                        )
                    ),
                )
            }
            _uiState.value.loginUiState.loginResponse?.let {
                if (_uiState.value.examTypeListUiState.examTypeList.isEmpty()) {
                    val examList = Repository.getExamList(
                        page = "1",
                        type = Constants.EXAMTYPES[0].second,
                    )!!
                    _uiState.update {
                        it.copy(
                            examTypeListUiState = it.examTypeListUiState.copy(
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

                }
                if (_uiState.value.screenType == SCREEN_TYPE.EXPANDED)
                    navigate(HomeScreen.ExpandedLoggedIn)
                else
                    navigate(HomeScreen.CompactLoggedIn)
                if (_uiState.value.loginUiState.setting.isRememberAccount == true)
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

    fun navigate(destination: HomeScreen) {
        when (destination) {
            HomeScreen.Exam -> {
                homeScreenNavigationActions.navigateToExam()
                _uiState.update {
                    it.copy(
                        currentRoute = HomeScreen.Exam
                    )
                }
            }

            HomeScreen.ExamList -> {
                homeScreenNavigationActions.navigateToExamList()
                _uiState.update {
                    it.copy(
                        currentRoute = HomeScreen.ExamList
                    )
                }
            }

            HomeScreen.CompactLoggedIn -> {
                homeScreenNavigationActions.navigateToCompactLoggedIn()
                _uiState.update {
                    it.copy(
                        currentRoute = HomeScreen.CompactLoggedIn
                    )
                }
            }

            HomeScreen.ExamListWithQuestions -> {
                homeScreenNavigationActions.navigateToExamListWithQuestions()
                _uiState.update {
                    it.copy(
                        currentRoute = HomeScreen.ExamListWithQuestions
                    )
                }
            }

            HomeScreen.ExamTypeList -> {
                homeScreenNavigationActions.navigateToExamList()
                _uiState.update {
                    it.copy(
                        currentRoute = HomeScreen.ExamTypeList
                    )
                }
            }

            HomeScreen.ExamTypeWithExamList -> {
                homeScreenNavigationActions.navigateToExamList()
                _uiState.update {
                    it.copy(
                        currentRoute = HomeScreen.ExamTypeWithExamList
                    )
                }
            }

            HomeScreen.ExpandedLoggedIn -> {
                homeScreenNavigationActions.navigateToExpandedLoggedIn()
                _uiState.update {
                    it.copy(
                        currentRoute = HomeScreen.ExpandedLoggedIn
                    )
                }
            }

            HomeScreen.QuestionListWithQuestion -> {
                homeScreenNavigationActions.navigateToQuestionsWithQuestion()
                _uiState.update {
                    it.copy(
                        currentRoute = HomeScreen.QuestionListWithQuestion
                    )
                }
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
        if (_uiState.value.loginUiState.setting.isAutoLogin == true
            && _uiState.value.loginUiState.loginResponse == null
        )
            login()
    }

    fun nextPage() {
        viewModelScope.launch {
            val currentPage = "${_uiState.value.examListUiState.currentPage.toInt() + 1}"
            val examList = Repository.getExamList(
                page = currentPage,
                type = Constants.EXAMTYPES.toMap()[_uiState.value.examListUiState.examType]!!,
            )!!
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
        }
    }

    fun previousPage() {
        viewModelScope.launch {
            val currentPage = "${_uiState.value.examListUiState.currentPage.toInt() - 1}"
            val examList = Repository.getExamList(
                page = currentPage,
                type = Constants.EXAMTYPES.toMap()[_uiState.value.examListUiState.examType]!!,
            )!!
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
            val examList = Repository.getExamList(
                page = _uiState.value.examListUiState.currentPage,
                type = Constants.EXAMTYPES.toMap()[_uiState.value.examListUiState.examType]!!,
            )!!
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
            val examList = Repository.getExamList(
                page = "1",
                type = Constants.EXAMTYPES.toMap()[type]!!,
            )!!
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
            if (_uiState.value.screenType == SCREEN_TYPE.COMPACT)
                navigate(HomeScreen.ExamList)
        }
    }

    fun toggleTitle(title: String? = null) {
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
                )
            )
        }
        navigate(HomeScreen.QuestionListWithQuestion)
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                DB.repository.deleteUser(user)
            }
        }
    }

    fun setExam(examId: String) {
        viewModelScope.launch {
            val exam = Repository.getExam(examId)!!
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
            if (_uiState.value.screenType == SCREEN_TYPE.COMPACT)
                navigate(HomeScreen.Exam)
            else
                navigate(HomeScreen.ExamListWithQuestions)
        }
    }
}