package app.xlei.vipexam.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.xlei.vipexam.data.AppContainer
import app.xlei.vipexam.ui.components.AppNavRail
import app.xlei.vipexam.ui.navgraph.VipExamNavHost
import app.xlei.vipexam.ui.navigation.AppDestinations
import app.xlei.vipexam.ui.navigation.HomeScreen
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ResourceType")
@Composable
fun App(
    appContainer: AppContainer,
    widthSizeClass: WindowWidthSizeClass,
    appState: VipExamState = rememberVipExamAppState(
        windowSizeClass = widthSizeClass,
    ),
) {
    val homeNavController = rememberNavController()

    val navBackStackEntry by appState.navController.currentBackStackEntryAsState()
    val currentRoute =
        navBackStackEntry?.destination?.route ?: AppDestinations.HOME_ROUTE.name

    val sizeAwareDrawerState = rememberSizeAwareDrawerState(appState.shouldShowTopBar)

    val currentHomeScreenRoute = homeNavController.currentBackStackEntryAsState()
        .value?.destination?.route ?: HomeScreen.Login.name

    val logoText = remember { mutableStateOf(
        HomeScreen.valueOf(currentHomeScreenRoute)
    ) }

    val coroutine = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            AppDrawer(
                currentRoute = currentRoute,
                navigationToTopLevelDestination = { appState.navigateToAppDestination(it) },
                closeDrawer = { coroutine.launch { sizeAwareDrawerState.close() } },
                modifier = Modifier
                    .width(300.dp)
                    .padding(top = 24.dp)
            )
        },
        drawerState = sizeAwareDrawerState,
        gesturesEnabled = appState.shouldShowTopBar,
    ){
        Row {
            if (appState.shouldShowNavRail) {
                AppNavRail(
                    logo = logoText,
                    homeNavController = homeNavController,
                    currentRoute = currentRoute,
                    navigationToTopLevelDestination = { appState.navigateToAppDestination(it) },
                )
            }
            VipExamNavHost(
                logoText = logoText,
                navHostController = appState.navController,
                homeNavController = homeNavController,
                appContainer = appContainer,
                widthSizeClass = widthSizeClass,
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