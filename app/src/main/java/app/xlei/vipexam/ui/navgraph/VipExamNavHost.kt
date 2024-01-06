package app.xlei.vipexam.ui.navgraph

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import app.xlei.vipexam.data.AppContainer
import app.xlei.vipexam.ui.navigation.AppDestinations
import app.xlei.vipexam.ui.navigation.HomeScreen
import app.xlei.vipexam.ui.page.SettingsScreen
import app.xlei.vipexam.ui.page.WordListPage


@Composable
fun VipExamNavHost(
    logoText: MutableState<HomeScreen>,
    navHostController: NavHostController,
    homeNavController: NavHostController,
    appContainer: AppContainer,
    widthSizeClass: WindowWidthSizeClass,
    openDrawer: () -> Unit,
) {
    NavHost(
        navController = navHostController,
        startDestination = AppDestinations.HOME_ROUTE.name
    ) {
        homeScreen(
            logoText = logoText,
            homeNavController = homeNavController,
            widthSizeClass = widthSizeClass,
            openDrawer = openDrawer,
        )
        composable(
            route = AppDestinations.SECOND_ROUTE.name,
        ) { navBackStackEntry ->
            WordListPage(
                openDrawer = openDrawer,
            )
        }
        composable(
            route = AppDestinations.SETTINGS_ROUTE.name,
        ) { navBackStackEntry ->
            SettingsScreen(
                openDrawer = openDrawer,
            )
        }
    }
}

