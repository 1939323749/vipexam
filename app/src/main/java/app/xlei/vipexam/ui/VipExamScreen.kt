package app.xlei.vipexam.ui

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
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
    currentScreen: String,
    canNavigateBack:Boolean,
    navigateUp:()->Unit,
    modifier: Modifier=Modifier
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
                        Row (
                           modifier = Modifier.align(Alignment.CenterHorizontally)
                        ){
                            Checkbox(
                                checked = false,
                                onCheckedChange = null,
                            )
                            Text(
                                text = "test",
                                modifier = Modifier
                                    .padding(start = 4.dp)
                            )
                        }
                    },
                    onClick = {}
                )
                DropdownMenuItem(
                    text = {
                        Text("test")
                    },
                    onClick = {}
                )
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

    var currentTitle by remember { mutableStateOf("Exam") }

    var isFirstItemHidden by remember { mutableStateOf(false) }

    Scaffold (
        topBar={
            VipExamAppBar(
                currentScreen =
                if(!isFirstItemHidden || currentScreen==VipExamScreen.ExamList)
                    currentScreen.name
                else
                    currentTitle,
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
                        }
                    )
                }
            }
        }

    }
}
