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
import app.xlei.vipexam.ui.navgraph.VipExamNavHost
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
) {
    val navigationActions = remember(appState.navController) {
        VipExamNavigationActions(appState.navController)
    }

    val homeNavController = rememberNavController()

    val navBackStackEntry by appState.navController.currentBackStackEntryAsState()
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
                navigationToTopLevelDestination = { appState.navigateToAppDestination(it) },
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
                    navigationToTopLevelDestination = { appState.navigateToAppDestination(it) },
                    openDrawer = { coroutine.launch { sizeAwareDrawerState.open() } },
                )
            }
            VipExamNavHost(
                logoText = logoText,
                showAnswer = showAnswer,
                navHostController = appState.navController,
                homeNavController = homeNavController,
                appContainer = appContainer,
                isExpandedScreen = appState.shouldShowNavRail,
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