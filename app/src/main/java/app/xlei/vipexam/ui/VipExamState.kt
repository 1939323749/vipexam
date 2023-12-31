package app.xlei.vipexam.ui

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.xlei.vipexam.ui.navigation.AppDestinations
import app.xlei.vipexam.ui.navigation.VipExamNavigationActions
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberVipExamAppState(
    windowSizeClass: WindowWidthSizeClass,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): VipExamState {
    return remember(
        navController,
        coroutineScope,
        windowSizeClass,
    ) {
        VipExamState(
            vipExamNavigationActions = VipExamNavigationActions(navController),
            coroutineScope = coroutineScope,
            widthSizeClass = windowSizeClass,
            navController = navController,
        )
    }
}

@Stable
class VipExamState(
    val vipExamNavigationActions: VipExamNavigationActions,
    val coroutineScope: CoroutineScope,
    val widthSizeClass: WindowWidthSizeClass,
    val navController: NavHostController,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentAppDestination: AppDestinations?
        @Composable get() = when (currentDestination?.route) {
            AppDestinations.HOME_ROUTE.name -> AppDestinations.HOME_ROUTE
            AppDestinations.SECOND_ROUTE.name -> AppDestinations.SECOND_ROUTE
            AppDestinations.SETTINGS_ROUTE.name -> AppDestinations.SETTINGS_ROUTE
            else -> null
        }

    val shouldShowTopBar: Boolean
        get() = widthSizeClass == WindowWidthSizeClass.Compact

    val shouldShowNavRail: Boolean
        get() = !shouldShowTopBar

    val appDestinations: List<AppDestinations> = AppDestinations.entries

    fun navigateToAppDestination(AppDestination: AppDestinations) {
        when (AppDestination) {
            AppDestinations.HOME_ROUTE -> vipExamNavigationActions.navigateToHome()
            AppDestinations.SECOND_ROUTE -> vipExamNavigationActions.navigateToSecond()
            AppDestinations.SETTINGS_ROUTE -> vipExamNavigationActions.navigateToSettings()
        }
    }
}