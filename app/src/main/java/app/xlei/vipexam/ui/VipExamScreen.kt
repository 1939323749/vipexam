package app.xlei.vipexam.ui

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.xlei.vipexam.R
import app.xlei.vipexam.data.LoginResponse
import app.xlei.vipexam.ui.page.ExamListView
import app.xlei.vipexam.ui.page.ExamPage
import io.ktor.http.websocket.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch

enum class VipExamScreen(@StringRes val title: Int) {
    Login(title = R.string.login),
    ExamList(title = R.string.examlist),
    Exam(title = R.string.exam)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VipExamAppBar(
    currentScreen: VipExamScreen,
    canNavigateBack:Boolean,
    navigateUp:()->Unit,
    modifier: Modifier=Modifier
){
    TopAppBar(
        title = {
            Text(stringResource(currentScreen.title))
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if(canNavigateBack){
                IconButton(onClick = navigateUp){
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ResourceType")
@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun VipExamApp(
    viewModel: ExamViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
    ) {
    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentScreen = VipExamScreen.valueOf(
        backStackEntry?.destination?.route?: VipExamScreen.Login.name
    )

    Scaffold (
        topBar={
            VipExamAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry !=null,
                navigateUp = {navController.navigateUp()}
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
                val coroutine= rememberCoroutineScope()
                var loginResponse by remember { mutableStateOf<LoginResponse?>(null) }
                login(
                    account = uiState.account,
                    password = uiState.password,
                    loginResponse = loginResponse,
                    onAccountChange = {viewModel.setAccount(it)},
                    onPasswordChange = {viewModel.setPassword(it)},
                ) {
                    coroutine.launch {
                        loginResponse=viewModel.login(
                            account = uiState.account,
                            password = uiState.password
                        )
                        if(loginResponse!!.code=="1"){
                            viewModel._getExamList()
                            navController.navigate(VipExamScreen.ExamList.name)
                        }
                    }
                }
            }
            composable(route= VipExamScreen.ExamList.name){
                val coroutine= rememberCoroutineScope()
                uiState.examList?.let { examList ->
                    ExamListView(
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
                        }
                    )
                }
            }
            composable(route = VipExamScreen.Exam.name){
                uiState.exam?.let { exam ->
                    ExamPage(
                        exam = exam
                    )
                }
            }
        }
    }
}
