package app.xlei.vipexam.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.xlei.vipexam.R
import app.xlei.vipexam.data.LoginResponse
import app.xlei.vipexam.data.models.room.Setting
import app.xlei.vipexam.data.models.room.User
import app.xlei.vipexam.logic.SETTING
import app.xlei.vipexam.ui.page.examListView
import app.xlei.vipexam.ui.page.ExamPage
import kotlinx.coroutines.*

enum class VipExamScreen(@StringRes val title: Int) {
    Login(title = R.string.login),
    ExamList(title = R.string.examlist),
    Exam(title = R.string.exam)
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
    viewModel: ExamViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
    showBottomBar: MutableState<Boolean>
) {
    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentScreen = VipExamScreen.valueOf(
        backStackEntry?.destination?.route?: VipExamScreen.Login.name
    )

    var currentTitle by remember { mutableStateOf("Exam") }

    var isFirstItemHidden by remember { mutableStateOf(false) }

    val showAnswer = remember { mutableStateOf(false) }

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
                users = SETTING.repository.getAllUsers()
                setting = SETTING.repository.getSetting()
                setting?.let {
                    Log.d("", it.isRememberAccount.toString())
                    Log.d("", it.isAutoLogin.toString())
                    viewModel.setSetting(it)
                }
            }
            if (setting == null) {
                withContext(Dispatchers.IO){
                    SETTING.repository.insertSetting(
                        Setting(
                            id = 0,
                            isRememberAccount = false,
                            isAutoLogin = false,
                        )
                    )
                    setting = SETTING.repository.getSetting()
                }
                viewModel.setSetting(setting!!)
            }
            setting?.let {
                if (it.isRememberAccount){
                    if (users.isNotEmpty()) {
                        viewModel.setAccount(users[0].account)
                        viewModel.setPassword(users[0].password)
                    }
                    if (it.isAutoLogin) {
                        if (users.isNotEmpty()) {
                            viewModel.login(
                                users[0].account,
                                users[0].password
                            )
                            viewModel._getExamList()
                        }
                        showBottomBar.value = false
                        navController.navigate(VipExamScreen.ExamList.name)
                    }
                }
            }
            initialized = true
        }
        onDispose {  }
    }

    Scaffold (
        topBar={
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
        NavHost(
            navController = navController,
            startDestination = VipExamScreen.Login.name,
            modifier = Modifier.padding(padding)
        ){
            composable(route = VipExamScreen.Login.name){
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
                                SETTING.repository.deleteUser(it)
                                users = SETTING.repository.getAllUsers()
                            }
                        }
                    },
                    onSettingChange = {
                        viewModel.setSetting(it)
                        coroutine.launch {
                            withContext(Dispatchers.IO){
                                SETTING.repository.updateSetting(it)
                            }
                        }
                    }
                ) {
                    coroutine.launch {
                        loginResponse=viewModel.login(
                            account = uiState.account,
                            password = uiState.password
                        )
                        if(loginResponse!!.code=="1"){
                            showBottomBar.value = false
                            viewModel._getExamList()
                            navController.navigate(VipExamScreen.ExamList.name)
                        }
                        setting?.let {
                            if (it.isRememberAccount && loginResponse!!.code=="1") {
                                withContext(Dispatchers.IO){
                                    SETTING.repository.insertUser(
                                        User(
                                            account = uiState.account,
                                            password = uiState.password,
                                        )
                                    )
                                    users = SETTING.repository.getAllUsers()
                                }
                            }
                        }
                    }
                }
            }
            composable(route= VipExamScreen.ExamList.name){
                uiState.examList?.let { examList ->
                    examListView(
                        currentPage = uiState.currentPage,
                        examList = examList,
                        onPreviousPageClicked = {
                            coroutine.launch {
                                viewModel.previousPage()
                            } },
                        onNextPageClicked = {
                            coroutine.launch {
                                viewModel.nextPage()
                            }
                        },
                        onExamClicked = {
                            coroutine.launch {
                                val getExamResponse=viewModel._getExam(examId = it)
                                if(getExamResponse){
                                    navController.navigate(VipExamScreen.Exam.name)
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
                        onFirstItemAppear = {
                            isFirstItemHidden=false
                        }
                    )
                }
            }
            composable(route = VipExamScreen.Exam.name){
                uiState.exam?.let { exam ->
                    ExamPage(
                        exam = exam,
                        onFirstItemHidden = {
                            isFirstItemHidden = true
                            currentTitle = it
                        },
                        onFirstItemAppear = {
                            isFirstItemHidden = false
                        },
                        showAnswer = showAnswer
                    )
                }
            }
        }
    }
}
