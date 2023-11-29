package app.xlei.vipexam.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.xlei.vipexam.data.AppContainer
import app.xlei.vipexam.ui.AppDrawer
import app.xlei.vipexam.ui.VipExamState
import app.xlei.vipexam.ui.components.AppNavRail
import app.xlei.vipexam.ui.navgraph.VipExamNavGraph
import app.xlei.vipexam.ui.rememberVipExamAppState
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ResourceType")
@Composable
fun App(
    appContainer: AppContainer,
    widthSizeClass: WindowSizeClass,
    appState: VipExamState = rememberVipExamAppState(
        windowSizeClass = widthSizeClass,
    )
){
    val navController = rememberNavController()
    val navigationActions = remember(navController) {
        VipExamNavigationActions(navController)
    }

    val homeNavController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute =
        navBackStackEntry?.destination?.route ?: AppDestinations.HOME_ROUTE.name

    val sizeAwareDrawerState = rememberSizeAwareDrawerState(appState.shouldShowBottomBar)

    val currentHomeScreenRoute = homeNavController.currentBackStackEntryAsState()
        .value?.destination?.route ?: HomeScreen.Login.name

    val logoText = remember { mutableStateOf(
        HomeScreen.valueOf(currentHomeScreenRoute)
    ) }

    val showAnswer = rememberSaveable() { mutableStateOf(false) }

    val coroutine = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            AppDrawer(
                currentRoute = currentRoute,
                navigateToHome = navigationActions.navigateToHome,
                navigateToSecond = navigationActions.navigateToSecond,
                navigateToSettings = navigationActions.navigateToSettings,
                closeDrawer = { coroutine.launch { sizeAwareDrawerState.close() } }
            )
        },
        drawerState = sizeAwareDrawerState,
        gesturesEnabled = appState.shouldShowBottomBar,
    ){
        Row {
            if (appState.shouldShowNavRail) {
                AppNavRail(
                    logo = logoText,
                    showAnswer = showAnswer,
                    homeNavController = homeNavController,
                    currentRoute = currentRoute,
                    navigateToHome = navigationActions.navigateToHome,
                    navigateToSecond = navigationActions.navigateToSecond,
                    navigateToSettings = navigationActions.navigateToSettings,
                    openDrawer = { coroutine.launch { sizeAwareDrawerState.open() } },
                )
            }
            VipExamNavGraph(
                logoText = logoText,
                showAnswer = showAnswer,
                homeNavController = homeNavController,
                appContainer = appContainer,
                isExpandedScreen = appState.shouldShowNavRail,
                navController = navController,
                openDrawer = { coroutine.launch { sizeAwareDrawerState.open() } },
            )
        }
    }
}


@Composable
private fun rememberSizeAwareDrawerState(isExpandedScreen: Boolean): DrawerState {
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    return if (!isExpandedScreen) {
        drawerState
    } else {
        DrawerState(DrawerValue.Closed)
    }
}