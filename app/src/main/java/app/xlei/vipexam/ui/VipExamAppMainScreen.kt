package app.xlei.vipexam.ui

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.xlei.vipexam.R
import app.xlei.vipexam.data.Exam
import app.xlei.vipexam.data.ExamUiState
import app.xlei.vipexam.data.Muban
import app.xlei.vipexam.ui.components.TextIconDialog
import app.xlei.vipexam.ui.login.loginView
import app.xlei.vipexam.ui.navgraph.compactHomeGraph
import app.xlei.vipexam.ui.navgraph.expandedHomeGraph
import app.xlei.vipexam.ui.navigation.HomeScreen
import app.xlei.vipexam.ui.navigation.HomeScreenNavigationActions
import app.xlei.vipexam.ui.page.examListView
import app.xlei.vipexam.ui.page.examTypeListView
import app.xlei.vipexam.ui.question.cloze.clozeView
import app.xlei.vipexam.ui.question.listening.listeningView
import app.xlei.vipexam.ui.question.qread.qreadView
import app.xlei.vipexam.ui.question.questionListView
import app.xlei.vipexam.ui.question.translate.translateView
import app.xlei.vipexam.ui.question.writing.writingView
import app.xlei.vipexam.ui.question.zread.zreadView
import compose.icons.FeatherIcons
import compose.icons.feathericons.Maximize
import compose.icons.feathericons.Minimize


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VipExamAppBar(
    question: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    showAnswer: MutableState<Boolean>,
    onShowAnswerClick: (Boolean) -> Unit,
){
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(question)
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
                        Row {
                            Checkbox(
                                checked = showAnswer.value,
                                onCheckedChange = null,
                            )
                            Text(
                                text = stringResource(R.string.show_answer),
                                modifier = Modifier
                                    .padding(start = 24.dp)
                            )
                        }

                    },
                    onClick = {
                        showAnswer.value = !showAnswer.value
                        showMenu = false
                    }
                )
            }
        }
    )
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ResourceType")
@Composable
fun HomeRoute(
    logoText: MutableState<HomeScreen>,
    isExpandedScreen: Boolean,
    showAnswer: MutableState<Boolean>,
    viewModel: ExamViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
    showBottomBar: MutableState<Boolean>,
) {
    viewModel.setScreenType(
        when (isExpandedScreen) {
            true -> SCREEN_TYPE.EXPANDED
            false -> SCREEN_TYPE.COMPACT
        }
    )
    viewModel.setNavigationActions(remember(navController) { HomeScreenNavigationActions(navController) })
    val uiState by viewModel.uiState.collectAsState()

    val openDialog = remember { mutableStateOf(false) }

    val isInternetAvailable = isInternetAvailable(LocalContext.current)
    val connectivity = remember { mutableStateOf(isInternetAvailable) }

    if (navController.previousBackStackEntry == null){
        showBottomBar.value = true
    }

    Scaffold (
        topBar={
            if (!isExpandedScreen)
                VipExamAppBar(
                    question = when (navController.currentBackStackEntryAsState().value?.destination?.route) {
                        HomeScreen.Exam.name -> uiState.title
                        else -> ""
                    },
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() },
                    showAnswer = showAnswer,
                    onShowAnswerClick = {
                        showAnswer.value = it
                    }
                )
        }
    ){ padding ->
        when {
            openDialog.value ->
                TextIconDialog(
                    onDismissRequest = { openDialog.value = false },
                    onConfirmation = { openDialog.value = false },
                    dialogTitle = stringResource(R.string.internet_error),
                    dialogText = stringResource(R.string.connect_to_continue),
                    icon = Icons.Default.Info
                )
        }

        NavHost(
            navController = navController,
            startDestination = HomeScreen.Login.name,
            modifier = Modifier.padding(padding)
        ){
            composable(
                route = HomeScreen.Login.name,
            ){
                connectivity.value = isInternetAvailable(LocalContext.current)
                loginView(
                    account = uiState.loginUiState.account,
                    password = uiState.loginUiState.password,
                    users = uiState.loginUiState.users,
                    setting = uiState.loginUiState.setting,
                    loginResponse = uiState.loginUiState.loginResponse,
                    onAccountChange = viewModel::setAccount,
                    onPasswordChange = viewModel::setPassword,
                    onDeleteUser = viewModel::deleteUser,
                    onSettingChange = viewModel::setSetting,
                    onLoginButtonClicked = viewModel::login,
                )
            }
            compactHomeGraph(
                viewModel = viewModel,
                onFirstItemHidden = viewModel::toggleTitle,
                onFirstItemAppear = viewModel::toggleTitle,
                showAnswer = showAnswer,
                onNextPageClicked = viewModel::nextPage,
                onExamTypeClicked = viewModel::setExamType,
                onExamClick = viewModel::setExam,
                onPreviousPageClicked = viewModel::previousPage,
                refresh = viewModel::refresh
            )
            expandedHomeGraph(
                viewModel = viewModel,
                showAnswer = showAnswer,
                onExamTypeClick = viewModel::setExamType,
                onExamClick = viewModel::setExam,
                onPreviousPageClicked = viewModel::previousPage,
                onNextPageClicked = viewModel::nextPage,
                onQuestionClick = viewModel::setQuestion,
                refresh = viewModel::refresh,
            )
        }
    }
}



@Composable
fun examTypeListWithExamListView(
    examTypeListUiState: ExamUiState.ExamTypeListUiState,
    onExamTypeClick: (Int) -> Unit,
    onExamClick: (String) -> Unit,
    onPreviousPageClick: () -> Unit,
    onNextPageClick: () -> Unit,
    refresh: () -> Unit,
    modifier: Modifier = Modifier
){
    Row (
        modifier = modifier
    ){
        ElevatedCard(
            modifier = Modifier
                .width(360.dp)
        ) {
            examTypeListView(
                examTypeListUiState = examTypeListUiState,
                onExamTypeClicked = onExamTypeClick,
            )
        }
        Spacer(modifier = Modifier.width(24.dp))
        ElevatedCard(
            modifier = Modifier
        ) {
            examTypeListUiState.examListUiState?.let { examListUiState ->
                examListView(
                    examListUiState = examListUiState,
                    onPreviousPageClicked = onPreviousPageClick,
                    onNextPageClicked = onNextPageClick,
                    onExamClick = onExamClick,
                    refresh = refresh,
                )
            }
        }
    }
}

@Composable
fun examListWithQuestionsView(
    examListUiState: ExamUiState.ExamListUiState,
    onPreviousPageClicked: () -> Unit,
    onNextPageClicked: () -> Unit,
    refresh: () -> Unit,
    onExamClick: (String) -> Unit,
    onQuestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier
    ) {
        ElevatedCard (
            modifier = Modifier
                .width(360.dp)
        ){
            examListView(
                examListUiState = examListUiState,
                onPreviousPageClicked = onPreviousPageClicked,
                onNextPageClicked = onNextPageClicked,
                onExamClick = onExamClick,
                refresh = refresh
            )
        }
        Spacer(modifier = Modifier.width(24.dp))
        ElevatedCard(
            modifier = Modifier
        ) {
            examListUiState.questionListUiState?.let { questionListUiState ->
                questionListView(
                    questionListUiState = questionListUiState,
                    onQuestionClick = onQuestionClick,
                )
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun questionListWithQuestionView(
    questionListUiState: ExamUiState.QuestionListUiState,
    navController: NavHostController = rememberNavController(),
    showAnswer: MutableState<Boolean>,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    var isMaximize by rememberSaveable { mutableStateOf(false) }
    Row(
        modifier = modifier
    ) {
        if (!isMaximize) {
            ElevatedCard(
                modifier = Modifier
                    .width(360.dp)
            ) {
                questionListView(
                    questionListUiState = questionListUiState,
                    onQuestionClick = {
                        navController.navigate(it) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
            Spacer(Modifier.width(24.dp).fillMaxHeight())
        }

        ElevatedCard(
            modifier = if (isMaximize)
                Modifier.fillMaxWidth().weight(1f)
            else
                Modifier
        ) {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { isMaximize = !isMaximize },
                    ) {
                        Icon(
                            imageVector = if (isMaximize) FeatherIcons.Minimize
                            else
                                FeatherIcons.Maximize,
                            contentDescription = null,
                        )
                    }
                }
            ) {
                NavHost(
                    navController = navController,
                    startDestination = questionListUiState.question ?: questionListUiState.questions[0].first,
                ) {
                    val mubanList = questionListUiState.exam.muban
                    for ((index, q) in questionListUiState.questions.withIndex()) {
                        composable(route = q.first) {
                            when (q.first) {
                                "ecswriting" -> writingView(
                                    muban = mubanList[index],
                                    onFirstItemHidden = {},
                                    onFirstItemAppear = {},
                                    showAnswer = showAnswer,
                                )

                                "ecscloze" -> clozeView(
                                    muban = mubanList[index],
                                    onFirstItemHidden = {},
                                    onFirstItemAppear = {},
                                    showAnswer = showAnswer,
                                )

                                "ecsqread" -> qreadView(
                                    muban = mubanList[index],
                                    onFirstItemHidden = {},
                                    onFirstItemAppear = {},
                                    showAnswer = showAnswer,
                                )

                                "ecszread" -> zreadView(
                                    muban = mubanList[index],
                                    onFirstItemHidden = {},
                                    onFirstItemAppear = {},
                                    showAnswer = showAnswer,
                                )

                                "ecstranslate" -> translateView(
                                    muban = mubanList[index],
                                    onFirstItemHidden = {},
                                    onFirstItemAppear = {},
                                    showAnswer = showAnswer,
                                )

                                "ecfwriting" -> writingView(
                                    muban = mubanList[index],
                                    onFirstItemHidden = {},
                                    onFirstItemAppear = {},
                                    showAnswer = showAnswer,
                                )

                                "ecfcloze" -> clozeView(
                                    muban = mubanList[index],
                                    onFirstItemHidden = {},
                                    onFirstItemAppear = {},
                                    showAnswer = showAnswer,
                                )

                                "ecfqread" -> qreadView(
                                    muban = mubanList[index],
                                    onFirstItemHidden = {},
                                    onFirstItemAppear = {},
                                    showAnswer = showAnswer,
                                )

                                "ecfzread" -> zreadView(
                                    muban = mubanList[index],
                                    onFirstItemHidden = {},
                                    onFirstItemAppear = {},
                                    showAnswer = showAnswer,
                                )

                                "ecftranslate" -> translateView(
                                    muban = mubanList[index],
                                    onFirstItemHidden = {},
                                    onFirstItemAppear = {},
                                    showAnswer = showAnswer,
                                )

                                "eylhlisteninga" -> listeningView(
                                    muban = mubanList[index],
                                    onFirstItemHidden = {},
                                    onFirstItemAppear = {},
                                    showAnswer = showAnswer,
                                )

                                "eylhlisteningb" -> listeningView(
                                    muban = mubanList[index],
                                    onFirstItemHidden = {},
                                    onFirstItemAppear = {},
                                    showAnswer = showAnswer,
                                )

                                "eylhlisteningc" -> listeningView(
                                    muban = mubanList[index],
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

        if (isMaximize) {
            Spacer(Modifier.width(24.dp).fillMaxHeight())
            ElevatedCard(
                modifier = Modifier
                    .width(360.dp)
            ) {
                questionListView(
                    questionListUiState = questionListUiState,
                    onQuestionClick = {
                        navController.navigate(it) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
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

private fun getMuban(question: String, exam: Exam): Muban? {
    exam.muban.forEach {
        if (it.ename == question)
            return it
    }
    return null
}