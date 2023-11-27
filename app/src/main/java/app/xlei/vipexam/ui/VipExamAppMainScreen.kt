package app.xlei.vipexam.ui

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.xlei.vipexam.R
import app.xlei.vipexam.constant.Constants
import app.xlei.vipexam.data.*
import app.xlei.vipexam.data.models.room.Setting
import app.xlei.vipexam.data.models.room.User
import app.xlei.vipexam.data.network.Repository
import app.xlei.vipexam.data.network.Repository.getExamList
import app.xlei.vipexam.logic.DB
import app.xlei.vipexam.ui.components.TextIconDialog
import app.xlei.vipexam.ui.page.*
import app.xlei.vipexam.ui.question.cloze.clozeView
import app.xlei.vipexam.ui.question.listening.listeningView
import app.xlei.vipexam.ui.question.qread.qreadView
import app.xlei.vipexam.ui.question.questionListView
import app.xlei.vipexam.ui.question.translate.translateView
import app.xlei.vipexam.ui.question.writing.writingView
import app.xlei.vipexam.ui.question.zread.zreadView
import kotlinx.coroutines.*

enum class VipExamScreen(@StringRes val title: Int,val icon: ImageVector) {
    Login(title = R.string.login,Icons.Default.Home),
    ExamType(title = R.string.examtype, Icons.AutoMirrored.Filled.List),
    ExamList(title = R.string.examlist, Icons.AutoMirrored.Filled.List),
    Exam(title = R.string.exam, Icons.Default.Edit),
    Question(title = R.string.question, Icons.Default.Edit),
    ExamTypeWithExamList(title = R.string.examTypeWithExamList, Icons.AutoMirrored.Filled.List),
    QuestionsWithQuestion(title = R.string.questionsWithQuestion, Icons.Default.Edit),
    ExamListWithQuestions(title = R.string.examListWithQuestions, Icons.AutoMirrored.Filled.List),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VipExamAppBar(
    currentScreen: String,
    canNavigateBack:Boolean,
    navigateUp:()->Unit,
    modifier: Modifier=Modifier,
    showAnswer: MutableState<Boolean>,
    onShowAnswerClick: (Boolean)->Unit
){
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(currentScreen)
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if(canNavigateBack){
                IconButton(onClick = navigateUp){
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = { showMenu = !showMenu }
            ) {
                Icon(Icons.Default.MoreVert, "")
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ){
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "show answer",
                            modifier = Modifier
                                .padding(start = 4.dp)
                        )
                    },
                    leadingIcon = {
                                  Checkbox(
                                      checked = showAnswer.value,
                                      onCheckedChange = {
                                          onShowAnswerClick(it)
                                      }
                                  )
                    },
                    onClick = {
                        showMenu = false
                    }
                )
            }
        }
    )
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ResourceType")
@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun VipExamAppMainScreen(
    logoText: MutableState<VipExamScreen>,
    isExpandedScreen: Boolean,
    showAnswer: MutableState<Boolean>,
    openDrawer: () -> Unit,
    viewModel: ExamViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
    showBottomBar: MutableState<Boolean>,
    onFirstItemHidden: (String) -> Unit,
    onFirstItemAppear: () -> Unit,
) {
    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentScreen = VipExamScreen.valueOf(
        backStackEntry?.destination?.route?: VipExamScreen.Login.name
    )

    var currentTitle by remember { mutableStateOf("Exam") }

    var isFirstItemHidden by remember { mutableStateOf(false) }

    var openDialog by remember { mutableStateOf(false) }

    var connectivity = isInternetAvailable(LocalContext.current)

    if (navController.previousBackStackEntry == null){
        showBottomBar.value = true
    }

    val coroutine = rememberCoroutineScope()

    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var setting by remember { mutableStateOf<Setting?>(null) }

    var initialized by remember { mutableStateOf(false) }

    DisposableEffect(Unit){
        coroutine.launch {
            withContext(Dispatchers.IO){
                users = DB.repository.getAllUsers()
                setting = DB.repository.getSetting()
                setting?.let {
                    viewModel.setSetting(it)
                }
            }
            if (setting == null) {
                withContext(Dispatchers.IO){
                    DB.repository.insertSetting(
                        Setting(
                            id = 0,
                            isRememberAccount = false,
                            isAutoLogin = false,
                        )
                    )
                    setting = DB.repository.getSetting()
                }
                viewModel.setSetting(setting!!)
            }
            setting?.let {
                if (it.isRememberAccount){
                    if (users.isNotEmpty()) {
                        viewModel.setAccount(users[0].account)
                        viewModel.setPassword(users[0].password)
                    }
                    if (it.isAutoLogin && connectivity) {
                        if (users.isNotEmpty()) {
                            viewModel.login(
                                users[0].account,
                                users[0].password
                            )
                            showBottomBar.value = false
                            navController.navigate(VipExamScreen.ExamType.name){
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                }
            }
            initialized = true
        }
        onDispose {
            initialized = false
        }
    }

    Scaffold (
        topBar={
            if (!isExpandedScreen)
                VipExamAppBar(
                    currentScreen =
                    if(!isFirstItemHidden || currentScreen==VipExamScreen.ExamList)
                        currentScreen.name
                    else
                        currentTitle,
                    canNavigateBack = navController.previousBackStackEntry !=null,
                    navigateUp = { navController.navigateUp() },
                    showAnswer = showAnswer,
                    onShowAnswerClick = {
                        showAnswer.value=it
                    }
                )
        }
    ){ padding ->
        val uiState by viewModel.uiState.collectAsState()

        when {
            openDialog ->
                TextIconDialog(
                    onDismissRequest = {openDialog=false},
                    onConfirmation = {openDialog=false},
                    dialogTitle = "Internet Error",
                    dialogText = "Connect to Internet to continue.",
                    icon = Icons.Default.Info
                )
        }

        val selectedExamType = remember { mutableStateOf(Constants.EXAMTYPES[0].second) }
        val selectedExamList = remember { mutableStateOf(ExamList("","",0, emptyList(),0)) }
        val currentPage = remember { mutableStateOf("1") }
        val selectedExamId = remember { mutableStateOf("") }
        val selectedQuestion = remember { mutableStateOf("") }

        NavHost(
            navController = navController,
            startDestination = VipExamScreen.Login.name,
            modifier = Modifier.padding(padding)
        ){
            composable(route = VipExamScreen.Login.name){
                connectivity = isInternetAvailable(LocalContext.current)
                var loginResponse by remember { mutableStateOf<LoginResponse?>(null) }
                if(initialized) loginView(
                    account = uiState.account,
                    password = uiState.password,
                    users = users,
                    setting = uiState.setting?: Setting(
                        id = 0,
                        isRememberAccount = false,
                        isAutoLogin = false
                    ),
                    loginResponse = loginResponse,
                    onAccountChange = {viewModel.setAccount(it)},
                    onPasswordChange = {viewModel.setPassword(it)},
                    onDeleteUser = {
                        coroutine.launch {
                            withContext(Dispatchers.IO){
                                DB.repository.deleteUser(it)
                                users = DB.repository.getAllUsers()
                            }
                        }
                    },
                    onSettingChange = {
                        viewModel.setSetting(it)
                        coroutine.launch {
                            withContext(Dispatchers.IO){
                                DB.repository.updateSetting(it)
                            }
                        }
                    }
                ) {
                    if (!connectivity) {
                        openDialog = true
                        return@loginView
                    }
                    coroutine.launch {
                        val loginSuccess = viewModel.login(
                            account = uiState.account,
                            password = uiState.password
                        )
                        if(loginSuccess){
                            showBottomBar.value = false
                            if (isExpandedScreen) {
                                navController.navigate(VipExamScreen.ExamTypeWithExamList.name){
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                logoText.value = VipExamScreen.ExamTypeWithExamList
                            }
                            else {
                                navController.navigate(VipExamScreen.ExamType.name){
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }

                        }
                        setting?.let {
                            if (it.isRememberAccount && loginSuccess) {
                                withContext(Dispatchers.IO){
                                    DB.repository.insertUser(
                                        User(
                                            account = uiState.account,
                                            password = uiState.password,
                                        )
                                    )
                                    users = DB.repository.getAllUsers()
                                }
                            }
                        }
                    }
                }
            }
            composable(route = VipExamScreen.ExamType.name){
                examTypeListView(
                    onExamTypeClicked = {
                        viewModel.setExamType(it)
                        coroutine.launch {
                            viewModel.refresh()
                            navController.navigate(VipExamScreen.ExamList.name){
                                launchSingleTop = true
                                restoreState = true
                            }
                        } },
                    onFirstItemAppear = {},
                    onFirstItemHidden = {},
                )
            }
            composable(route= VipExamScreen.ExamList.name){
                uiState.examList?.let { examList ->
                    examListView(
                        currentPage = uiState.currentPage,
                        examList = examList,
                        isPractice = uiState.examType == Constants.EXAMTYPES.toMap()[R.string.practice_exam],
                        onPreviousPageClicked = {
                            coroutine.launch {
                                viewModel.previousPage()
                            } },
                        onNextPageClicked = {
                            coroutine.launch {
                                viewModel.nextPage()
                            }
                        },
                        onExamClick = {
                            coroutine.launch {
                                val getExamResponse = viewModel.getExam(examId = it)
                                if(getExamResponse){
                                    navController.navigate(VipExamScreen.Exam.name){
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        },
                        refresh = {
                            coroutine.launch {
                                viewModel.refresh()
                            }
                        },
                        onFirstItemHidden = {
                            isFirstItemHidden=true
                        },
                    ) {
                        isFirstItemHidden = false
                    }
                }
            }
            composable(route = VipExamScreen.Exam.name){
                uiState.exam?.let { exam ->
                    ExamPage(
                        exam = exam,
                        onFirstItemHidden = {
                            isFirstItemHidden = true
                            onFirstItemHidden(it)
                        },
                        onFirstItemAppear = {
                            isFirstItemHidden = false
                            onFirstItemAppear()
                        },
                        showAnswer = showAnswer,
                    )
                }
            }
            composable(route = VipExamScreen.ExamTypeWithExamList.name){
                examTypeListWithExamListView(
                    onExamTypeClick = { selectedExamType.value = it },
                    onExamClick = {
                        selectedExamId.value = it
                        coroutine.launch {
                            selectedExamList.value = Repository.getExamList(
                                page = currentPage.value,
                                type = selectedExamType.value,
                            )!!
                            navController.navigate(VipExamScreen.ExamListWithQuestions.name){
                                launchSingleTop = true
                                restoreState = true
                            }
                            logoText.value = VipExamScreen.ExamListWithQuestions
                        }
                    },
                    onPreviousPageClicked = {
                        currentPage.value = "${currentPage.value.toInt() - 1}"
                        coroutine.launch {
                            selectedExamList.value = Repository.getExamList(
                                page = currentPage.value,
                                type = selectedExamType.value,
                            )!!
                        } },
                    onNextPageClicked = {
                        currentPage.value = "${currentPage.value.toInt() + 1}"
                        coroutine.launch {
                            selectedExamList.value = Repository.getExamList(
                                page = currentPage.value,
                                type = selectedExamType.value,
                            )!!
                        } },
                    refresh = {
                        coroutine.launch {
                            viewModel.refresh()
                        } },
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
            }
            composable(route = VipExamScreen.ExamListWithQuestions.name){
                examListWithQuestionsView(
                    examList = selectedExamList.value,
                    currentPage = currentPage.value,
                    examType = selectedExamType.value,
                    examId = selectedExamId.value,
                    onPreviousPageClicked = {
                        currentPage.value = "${currentPage.value.toInt() - 1}"
                        coroutine.launch {
                            selectedExamList.value = Repository.getExamList(
                                page = currentPage.value,
                                type = selectedExamType.value,
                            )!!
                        } },
                    onNextPageClicked = {
                        currentPage.value = "${currentPage.value.toInt() + 1}"
                        coroutine.launch {
                            selectedExamList.value = Repository.getExamList(
                                page = currentPage.value,
                                type = selectedExamType.value,
                            )!!
                        } },
                    onExamClick = {
                        selectedExamId.value = it },
                    refresh = {},
                    onQuestionClick = {
                        selectedQuestion.value = it
                        navController.navigate(VipExamScreen.QuestionsWithQuestion.name){
                            launchSingleTop = true
                            restoreState = true
                        }
                        logoText.value = VipExamScreen.QuestionsWithQuestion
                    },
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
            }
            composable(route = VipExamScreen.QuestionsWithQuestion.name){
                questionsWithQuestionView(
                    examId = selectedExamId.value,
                    question = selectedQuestion.value,
                    showAnswer = showAnswer,
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
            }
        }
    }
}

@Composable
fun examTypeListWithExamListView(
    onExamTypeClick: (String) -> Unit,
    onExamClick: (String) -> Unit,
    onPreviousPageClicked: () -> Unit,
    onNextPageClicked: () -> Unit,
    refresh: () -> Unit,
    modifier: Modifier = Modifier
){
    val examType = remember { mutableStateOf(Constants.EXAMTYPES[0].second) }
    val currentPage = remember { mutableStateOf("1") }
    val examList = remember { mutableStateOf<ExamList?>(null) }
    val coroutine = rememberCoroutineScope()

    DisposableEffect(Unit){
        coroutine.launch {
            examList.value = getExamList(currentPage.value, examType.value)
        }
        onDispose {  }
    }

    Row (
        modifier = modifier
    ){
        ElevatedCard (
            modifier = Modifier
                .width(360.dp)
//                .weight(1f)
        ) {
            examTypeListView(
                onExamTypeClicked = {
                    currentPage.value = "1"
                    examType.value = it
                    onExamTypeClick(it) },
                onFirstItemAppear = {},
                onFirstItemHidden = {},
            )
        }
        examList.value?.let {
            Spacer(modifier = Modifier.width(24.dp))
            ElevatedCard (
                modifier = Modifier
//                    .weight(1f)
            ){
                examListView(
                    currentPage = currentPage.value,
                    examList = it,
                    isPractice = examType.value == Constants.EXAMTYPES.toMap()[R.string.practice_exam],
                    onPreviousPageClicked = {
                        onPreviousPageClicked()
                        currentPage.value = "${currentPage.value.toInt()-1}"
                        coroutine.launch {
                            examList.value = getExamList(currentPage.value,examType.value)
                        } },
                    onNextPageClicked = {
                        onNextPageClicked()
                        currentPage.value = "${currentPage.value.toInt()+1}"
                        coroutine.launch {
                            examList.value = getExamList(currentPage.value,examType.value)
                        } },
                    onFirstItemHidden = {},
                    onFirstItemAppear = {},
                    onExamClick = onExamClick,
                    refresh = refresh,
                )
            }
        }

    }
}

@Composable
fun examListWithQuestionsView(
    examList: ExamList,
    currentPage: String,
    examType: String,
    examId: String,

    onPreviousPageClicked: () -> Unit,
    onNextPageClicked: () -> Unit,
    refresh: () -> Unit,
    onExamClick: (String) -> Unit,

    onQuestionClick: (String) -> Unit,

    modifier: Modifier = Modifier
){
    val selectedExam = remember { mutableStateOf<Exam?>(null) }
    val selectedExamId = remember { mutableStateOf(examId) }
    val questions = remember { mutableStateOf<List<Pair<String,String>>?>(null) }
    val coroutine = rememberCoroutineScope()

    DisposableEffect(Unit){
        coroutine.launch {
            selectedExam.value = Repository.getExam(selectedExamId.value)!!
            questions.value = Repository.getQuestions(
                selectedExam.value!!.muban
            )
        }
        onDispose {  }
    }

    Row(
        modifier = modifier
    ) {
        ElevatedCard (
            modifier = Modifier
                .width(360.dp)
        ){
            examListView(
                currentPage = currentPage,
                examList = examList,
                isPractice = examType == Constants.EXAMTYPES.toMap()[R.string.practice_exam],
                onPreviousPageClicked = onPreviousPageClicked,
                onNextPageClicked = onNextPageClicked,
                onFirstItemHidden = {},
                onFirstItemAppear = {},
                onExamClick = {
                    coroutine.launch {
                        selectedExam.value = Repository.getExam(it)!!
                        questions.value = Repository.getQuestions(
                            selectedExam.value!!.muban
                        )
                    }
                    onExamClick(it)
                    selectedExamId.value = it
                },
                refresh = refresh
            )
        }
        questions.value?.let {
            Spacer(modifier = Modifier.width(24.dp))
            ElevatedCard (
                modifier = Modifier
//                    .weight(1f)
            ) {
                questionListView(
                    name = selectedExam.value!!.examName,
                    questions = it,
                    onQuestionClick = onQuestionClick,
                )
            }
        }
    }
}
@Composable
fun questionsWithQuestionView(
    examId: String,
    question: String,
    showAnswer: MutableState<Boolean>,
    modifier: Modifier = Modifier,
){
    val coroutine = rememberCoroutineScope()
    val exam = remember { mutableStateOf<Exam?>(null) }
    val questions  = remember { mutableStateOf<List<Pair<String,String>>?>(null) }
    val selectedQuestion = remember { mutableStateOf(question) }
    DisposableEffect(Unit){
        coroutine.launch{
            exam.value = Repository.getExam(
                examId
            )
            exam.value?.let {
                questions.value = Repository.getQuestions(
                    mubanList =  it.muban
                )
            }
        }
        onDispose {  }
    }


    Row(
        modifier = modifier
    ) {
        questions.value?.let {
            ElevatedCard (
                modifier = Modifier
                    .width(360.dp)
///                    .weight(1f)
            ){
                questionListView(
                    name = exam.value!!.examName,
                    questions = it,
                    onQuestionClick = { selectedQuestion.value = it }
                )
            }
        }
        exam.value?.let{
            Spacer(modifier = Modifier.width(24.dp))
            ElevatedCard (
                modifier = Modifier
//                    .weight(1f)
            ){
                val muban = getMuban(selectedQuestion.value,it)
                muban?.let {
                    when (selectedQuestion.value) {
                        "ecswriting" -> writingView(
                        muban = it,
                        onFirstItemHidden = {},
                        onFirstItemAppear = {},
                        showAnswer = showAnswer,
                        )
                        "ecscloze" -> clozeView(
                        muban = it,
                        onFirstItemHidden = {},
                        onFirstItemAppear = {},
                        showAnswer = showAnswer,
                        )
                        "ecsqread" -> qreadView(
                        muban = it,
                        onFirstItemHidden = {},
                        onFirstItemAppear = {},
                        showAnswer = showAnswer,
                        )
                        "ecszread" -> zreadView(
                        muban = it,
                        onFirstItemHidden = {},
                        onFirstItemAppear = {},
                        showAnswer = showAnswer,
                        )
                        "ecstranslate" -> translateView(
                        muban = it,
                        onFirstItemHidden = {},
                        onFirstItemAppear = {},
                        showAnswer = showAnswer,
                        )
                        "ecfwriting" -> writingView(
                        muban = it,
                        onFirstItemHidden = {},
                        onFirstItemAppear = {},
                        showAnswer = showAnswer,
                        )
                        "ecfcloze" -> clozeView(
                        muban = it,
                        onFirstItemHidden = {},
                        onFirstItemAppear = {},
                        showAnswer = showAnswer,
                        )
                        "ecfqread" -> qreadView(
                        muban = it,
                        onFirstItemHidden = {},
                        onFirstItemAppear = {},
                        showAnswer = showAnswer,
                        )
                        "ecfzread" -> zreadView(
                        muban = it,
                        onFirstItemHidden = {},
                        onFirstItemAppear = {},
                        showAnswer = showAnswer,
                        )
                        "ecftranslate" -> translateView(
                        muban = it,
                        onFirstItemHidden = {},
                        onFirstItemAppear = {},
                        showAnswer = showAnswer,
                        )
                        "eylhlisteninga" -> listeningView(
                        muban = it,
                        onFirstItemHidden = {},
                        onFirstItemAppear = {},
                        showAnswer = showAnswer,
                        )
                        "eylhlisteningb" -> listeningView(
                        muban = it,
                        onFirstItemHidden = {},
                        onFirstItemAppear = {},
                        showAnswer = showAnswer,
                        )
                        "eylhlisteningc" -> listeningView(
                        muban = it,
                        onFirstItemHidden = {},
                        onFirstItemAppear = {},
                        showAnswer = showAnswer,
                        )
                    }
                }
            }
        }
    }
}


fun isInternetAvailable(context: Context): Boolean {
    val result: Boolean
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = connectivityManager.activeNetwork ?: return false
    val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
    result = when {
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
    return result
}

private fun getMuban(question: String,exam: Exam): Muban? {
    exam.muban.forEach {
        if (it.ename==question)
            return it
    }
    return null
}
