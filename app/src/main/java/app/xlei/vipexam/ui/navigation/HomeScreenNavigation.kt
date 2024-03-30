package app.xlei.vipexam.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import app.xlei.vipexam.core.network.module.NetWorkRepository
import app.xlei.vipexam.core.network.module.getExamResponse.GetExamResponse
import app.xlei.vipexam.core.ui.OnError
import app.xlei.vipexam.core.ui.OnLoading
import app.xlei.vipexam.preference.LocalOrganization
import app.xlei.vipexam.ui.UiState
import app.xlei.vipexam.ui.VipExamMainScreenViewModel
import app.xlei.vipexam.ui.VipexamUiState
import app.xlei.vipexam.ui.appbar.AppBarTitle
import app.xlei.vipexam.ui.page.LoginScreen
import app.xlei.vipexam.ui.page.QuestionMapToView
import app.xlei.vipexam.ui.screen.ExamListScreen
import app.xlei.vipexam.ui.screen.ExamScreen
import app.xlei.vipexam.ui.screen.ExamTypeListScreen
import kotlinx.coroutines.launch

/**
 * Main screen navigation
 *
 * @param modifier
 * @param navHostController 主页导航控制器，在主页页面之间跳转
 * @param viewModel 主页vm
 * @param widthSizeClass 用于根据屏幕宽度切换显示内容
 */
@Composable
fun MainScreenNavigation(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: VipExamMainScreenViewModel,
    widthSizeClass: WindowWidthSizeClass,
){
    val organization = LocalOrganization.current
    val uiState by viewModel.uiState.collectAsState()
    val coroutine = rememberCoroutineScope()

    NavHost(
        navController = navHostController,
        startDestination = Screen.Login.route,
        modifier = modifier
            .fillMaxSize(), // must add this to avoid weird navigation animation
    ){
        slideInSlideOutNavigationContainer(
            route = Screen.Login.route,
        ){
            viewModel.setTitle(AppBarTitle.Login)
            when (uiState.loginUiState) {
                is UiState.Loading -> {
                    OnLoading()
                }

                is UiState.Success -> {
                    val loginUiState = (uiState.loginUiState as UiState.Success<VipexamUiState.LoginUiState>).uiState
                    LoginScreen(
                        account = loginUiState.account,
                        password = loginUiState.password,
                        users = loginUiState.users.collectAsState(initial = emptyList()).value,
                        loginResponse = loginUiState.loginResponse,
                        onAccountChange = viewModel::setAccount,
                        onPasswordChange = viewModel::setPassword,
                        onDeleteUser = viewModel::deleteUser,
                        onLoginButtonClicked = {
                            coroutine.launch {
                                viewModel.login(organization.value)
                                navHostController.navigate(Screen.ExamTypeList.route)
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }

                is UiState.Error -> {
                    OnError()
                }
            }

        }
        slideInSlideOutNavigationContainer(
            route = Screen.ExamTypeList.route,
        ){
            viewModel.setTitle(AppBarTitle.ExamType)
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                ExamTypeListScreen(
                    examTypeListUiState = uiState.examTypeListUiState,
                    onExamTypeClick = {
                        viewModel.setExamType(it)
                        navHostController.navigate(Screen.ExamList.createRoute(it.name))
                    },
                    widthSizeClass = widthSizeClass,
                    onLastViewedClick = {
                        navHostController.navigate(Screen.Exam.createRoute(it))
                    },
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

        }
        slideInSlideOutNavigationContainer(
            route = Screen.ExamList.route,
            arguments = Screen.ExamList.navArguments,
        ){
            viewModel.setTitle(AppBarTitle.ExamList)
            ExamListScreen(
                examListUiState = uiState.examListUiState,
                onLastExamClick = { navHostController.navigate(Screen.Exam.route) },
                onExamClick = {
                    coroutine.launch {
                        navHostController.navigate(Screen.Exam.createRoute(it))
                    }
                },
                widthSizeClass = widthSizeClass,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
        slideInSlideOutNavigationContainer(
            route = Screen.Exam.route,
            arguments = Screen.Exam.navArguments,
        ) { backStackEntry ->
            LaunchedEffect(key1 = Unit, block = {
                backStackEntry.arguments?.let { bundle ->
                    bundle.getString(Screen.Question.navArguments[0].name)?.let {examId->
                        coroutine.launch {
                            uiState.questionListUiState.let {
                                when (it) {
                                    is UiState.Success -> {
                                        it.uiState.exam.let {exam->
                                            if (exam.examID != examId)
                                                viewModel.setExam(examId)
                                        }
                                    }

                                    else -> {
                                        viewModel.setExam(examId)
                                    }
                                }
                            }
                        }
                    }
                }
            })

            when (uiState.questionListUiState) {
                is UiState.Loading -> {
                    OnLoading((uiState.questionListUiState as UiState.Loading).loadingMessageId)
                }

                is UiState.Success -> {
                    val questionListUiState = (uiState.questionListUiState as UiState.Success<VipexamUiState.QuestionListUiState>).uiState
                    questionListUiState.question?.let {
                        AppBarTitle.Exam(
                            it,
                            questionListUiState.exam
                        )
                    }?.let {
                        viewModel.setTitle(it)
                    }
                    ExamScreen(
                        questionListUiState = questionListUiState,
                        setQuestion = viewModel::setQuestion,
                        widthSizeClass = widthSizeClass,
                        submitMyAnswer = viewModel::submitMyAnswer,
                        modifier = Modifier
                            .fillMaxSize(),
                        onExamClick = {
                            navHostController.navigate(Screen.Exam.createRoute(it))
                        }
                    )
                }

                is UiState.Error -> {
                    uiState.questionListUiState.let {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource((it as UiState.Error).errorMessageId),
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
        slideInSlideOutNavigationContainer(
            route = Screen.Question.route,
            arguments = Screen.Question.navArguments
        ) { backStackEntry ->
            var exam by remember {
                mutableStateOf<GetExamResponse?>(null)
            }
            var question by remember {
                mutableStateOf<String?>(null)
            }
            backStackEntry.arguments?.let { bundle ->
                bundle.getString(Screen.Question.navArguments[0].name)?.let {
                    if (exam == null)
                        coroutine.launch {
                            NetWorkRepository.getExam(it)
                                .onSuccess {
                                    exam = it
                                }
                        }
                }
                bundle.getString(Screen.Question.navArguments[1].name)?.let {
                    if (question == null)
                        question = it
                }
            }
            if (exam!=null) {
                question?.let { q ->
                    viewModel.setTitle(
                        AppBarTitle.Exam(
                            question = q,
                            exam = exam!!
                        ) )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        QuestionMapToView(
                            question = exam!!.muban.first { it.cname == question }.ename,
                            muban = exam!!.muban.first { it.cname == question },
                            submitMyAnswer = viewModel::submitMyAnswer
                        )
                    }
                }
            } else {
                OnLoading()
            }
        }
    }
}





private fun NavGraphBuilder.slideInSlideOutNavigationContainer(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit){
    return composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(200)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(200)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(200)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(200)
            )
        },
        content = content
    )
}