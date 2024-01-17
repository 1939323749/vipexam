package app.xlei.vipexam.ui

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.xlei.vipexam.R
import app.xlei.vipexam.ui.navgraph.homeScreenGraph
import app.xlei.vipexam.ui.navigation.HomeScreen
import app.xlei.vipexam.ui.navigation.HomeScreenNavigationActions
import app.xlei.vipexam.ui.page.ExamPage
import app.xlei.vipexam.ui.page.LoginView
import app.xlei.vipexam.ui.page.ShowAnswerOptions
import app.xlei.vipexam.ui.page.examListView
import app.xlei.vipexam.ui.page.examTypeListView
import app.xlei.vipexam.ui.question.questionListView
import app.xlei.vipexam.core.data.util.Preferences
import app.xlei.vipexam.core.data.util.dataStore
import compose.icons.FeatherIcons
import compose.icons.feathericons.Menu
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VipExamAppBar(
    appBarText: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()
    val showAnswer = context.dataStore.data.map {
        it[Preferences.SHOW_ANSWER] ?: false
    }.collectAsState(initial = false)
    LargeTopAppBar(
        title = {
            Text(appBarText)
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(
                    onClick = {
                        navigateUp()
                        if (Preferences
                                .get(
                                    Preferences.alwaysShowAnswerKey,
                                    ShowAnswerOptions.ONCE.value
                                ) == ShowAnswerOptions.ONCE.value
                        ) {
                            coroutine.launch {
                                context.dataStore.edit {
                                    it[Preferences.SHOW_ANSWER] = false
                                }
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            } else {
                IconButton(onClick = openDrawer) {
                    Icon(
                        imageVector = FeatherIcons.Menu,
                        contentDescription = null,
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
            ) {
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
                                    .padding(horizontal = 24.dp)
                            )
                        }

                    },
                    onClick = {
                        coroutine.launch {
                            context.dataStore.edit {
                                it[Preferences.SHOW_ANSWER] = showAnswer.value.not()
                            }
                        }
                        showMenu = false
                    }
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ResourceType")
@Composable
fun HomeRoute(
    logoText: MutableState<HomeScreen>,
    widthSizeClass: WindowWidthSizeClass,
    viewModel: ExamViewModel = hiltViewModel(),
    navController: NavHostController,
    openDrawer: () -> Unit,
) {
    viewModel.setNavigationActions(remember(navController) {
        HomeScreenNavigationActions(
            navController
        )
    })
    viewModel.setScreenType(widthSizeClass)
    val uiState by viewModel.uiState.collectAsState()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            when (widthSizeClass) {
                WindowWidthSizeClass.Compact ->
                    VipExamAppBar(
                        appBarText = when (navController.currentBackStackEntryAsState().value?.destination?.route) {
                            HomeScreen.QuestionListWithQuestion.name -> uiState.title
                            HomeScreen.ExamTypeWithExamList.name -> stringResource(id = R.string.examtype)
                            HomeScreen.ExamListWithQuestions.name -> stringResource(id = R.string.examlist)
                            else -> ""
                        },
                        canNavigateBack = navController.previousBackStackEntry != null,
                        navigateUp = { navController.navigateUp() },
                        openDrawer = openDrawer,
                        scrollBehavior = scrollBehavior,
                    )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = HomeScreen.Login.name,
            modifier = Modifier.padding(padding)
        ) {
            composable(
                route = HomeScreen.Login.name,
            ) {
                LoginView(
                    account = uiState.loginUiState.account,
                    password = uiState.loginUiState.password,
                    users = uiState.loginUiState.users.collectAsState(initial = emptyList()),
                    setting = uiState.loginUiState.setting,
                    loginResponse = uiState.loginUiState.loginResponse,
                    onAccountChange = viewModel::setAccount,
                    onPasswordChange = viewModel::setPassword,
                    onDeleteUser = viewModel::deleteUser,
                    onSettingChange = viewModel::setSetting,
                    onLoginButtonClicked = viewModel::login,
                )
            }
            homeScreenGraph(
                viewModel = viewModel,
                onExamTypeClick = viewModel::setExamType,
                onExamClick = viewModel::setExam,
                onPreviousPageClicked = viewModel::previousPage,
                onNextPageClicked = viewModel::nextPage,
                onQuestionClick = viewModel::setQuestion,
                refresh = viewModel::refresh,
                widthSizeClass = widthSizeClass,
            )
        }
    }
}


@Composable
fun examTypeListWithExamListView(
    examTypeListUiState: VipexamUiState.ExamTypeListUiState,
    onExamTypeClick: (Int) -> Unit,
    onExamClick: (String) -> Unit,
    onPreviousPageClick: () -> Unit,
    onNextPageClick: () -> Unit,
    refresh: () -> Unit,
    widthSizeClass: WindowWidthSizeClass,
    modifier: Modifier = Modifier
) {
    when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            examTypeListView(
                examTypeListUiState = examTypeListUiState,
                onExamTypeClicked = onExamTypeClick,
            )
        }

        WindowWidthSizeClass.Medium -> {
            examTypeListView(
                examTypeListUiState = examTypeListUiState,
                onExamTypeClicked = onExamTypeClick,
            )
        }

        WindowWidthSizeClass.Expanded -> {
            Row(
                modifier = modifier
            ) {
                BoxWithConstraints {
                    if (maxHeight > 360.dp) {
                        ElevatedCard(
                            modifier = Modifier
                                .width(360.dp)
                        ) {
                            examTypeListView(
                                examTypeListUiState = examTypeListUiState,
                                onExamTypeClicked = onExamTypeClick,
                            )
                        }
                    } else {
                        ElevatedCard(
                            modifier = Modifier
                                .width(180.dp)
                        ) {
                            examTypeListView(
                                examTypeListUiState = examTypeListUiState,
                                onExamTypeClicked = onExamTypeClick,
                                modifier = Modifier.weight(1f)
                            )
                            Row(
                                modifier = Modifier
                                    .height(40.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(
                                    onClick = onPreviousPageClick, Modifier.weight(1f),
                                    enabled = (examTypeListUiState.examListUiState?.currentPage?.toInt()
                                        ?: 1) > 1
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                        contentDescription = null
                                    )
                                }
                                IconButton(onClick = onNextPageClick, Modifier.weight(1f)) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
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
    }
}

@Composable
fun examListWithQuestionsView(
    examListUiState: VipexamUiState.ExamListUiState,
    onPreviousPageClicked: () -> Unit,
    onNextPageClicked: () -> Unit,
    refresh: () -> Unit,
    onExamClick: (String) -> Unit,
    onQuestionClick: (String) -> Unit,
    widthSizeClass: WindowWidthSizeClass,
    modifier: Modifier = Modifier
) {
    when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            examListView(
                examListUiState = examListUiState,
                onPreviousPageClicked = onPreviousPageClicked,
                onNextPageClicked = onNextPageClicked,
                onExamClick = onExamClick,
                refresh = refresh
            )
        }
        WindowWidthSizeClass.Medium -> {
            Row(
                modifier = modifier
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .width(360.dp)
                ) {
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

        WindowWidthSizeClass.Expanded -> {
            Row(
                modifier = modifier
            ) {
                BoxWithConstraints {
                    if (maxHeight < 360.dp) {
                        ElevatedCard(
                            modifier = Modifier
                                .width(180.dp)
                        ) {
                            examListView(
                                examListUiState = examListUiState,
                                onPreviousPageClicked = onPreviousPageClicked,
                                onNextPageClicked = onNextPageClicked,
                                onExamClick = onExamClick,
                                refresh = refresh
                            )
                        }
                    } else {
                        ElevatedCard(
                            modifier = Modifier
                                .width(360.dp)
                        ) {
                            examListView(
                                examListUiState = examListUiState,
                                onPreviousPageClicked = onPreviousPageClicked,
                                onNextPageClicked = onNextPageClicked,
                                onExamClick = onExamClick,
                                refresh = refresh
                            )
                        }
                    }
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
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun questionListWithQuestionView(
    questionListUiState: VipexamUiState.QuestionListUiState,
    setQuestion: (String) -> Unit,
    navController: NavHostController = rememberNavController(),
    widthSizeClass: WindowWidthSizeClass,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()

    when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            ExamPage(
                questionListUiState = questionListUiState,
                setQuestion = setQuestion,
                navController = navController
            )
        }

        WindowWidthSizeClass.Medium -> {
            ExamPage(
                questionListUiState = questionListUiState,
                setQuestion = setQuestion,
                navController = navController
            )
        }

        WindowWidthSizeClass.Expanded -> {
            Row(
                modifier = modifier
            ) {
                BoxWithConstraints {
                    if (maxHeight < 360.dp) {
                        ElevatedCard(
                            modifier = Modifier
                                .width(120.dp)
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
                                    if (Preferences.get(
                                            key = Preferences.alwaysShowAnswerKey,
                                            ShowAnswerOptions.ONCE.value
                                        ) == ShowAnswerOptions.ONCE.value
                                    ) {
                                        coroutine.launch {
                                            context.dataStore.edit { preferences ->
                                                preferences[Preferences.SHOW_ANSWER] = false
                                            }
                                        }
                                    }
                                },
                            )
                        }
                    } else {
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
                                    if (Preferences.get(
                                            key = Preferences.alwaysShowAnswerKey,
                                            ShowAnswerOptions.ONCE.value
                                        ) == ShowAnswerOptions.ONCE.value
                                    ) {
                                        coroutine.launch {
                                            context.dataStore.edit { preferences ->
                                                preferences[Preferences.SHOW_ANSWER] = false
                                            }
                                        }
                                    }
                                },
                            )
                        }
                    }
                }
                Spacer(
                    Modifier
                        .width(24.dp)
                        .fillMaxHeight()
                )
                ElevatedCard(
                    modifier = Modifier
                ) {
                    ExamPage(
                        questionListUiState = questionListUiState,
                        setQuestion = setQuestion,
                        navController = navController
                    )
                }
            }
        }
    }
}
