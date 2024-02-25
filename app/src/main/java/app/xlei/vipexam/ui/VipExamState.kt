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
import app.xlei.vipexam.core.data.util.NetworkMonitor
import app.xlei.vipexam.ui.navigation.AppDestinations
import app.xlei.vipexam.ui.navigation.VipExamNavigationActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Composable
fun rememberVipExamAppState(
    windowSizeClass: WindowWidthSizeClass,
    networkMonitor: NetworkMonitor,
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
            networkMonitor,
        )
    }
}

@Stable
class VipExamState(
    val vipExamNavigationActions: VipExamNavigationActions,
    val coroutineScope: CoroutineScope,
    val widthSizeClass: WindowWidthSizeClass,
    val navController: NavHostController,
    networkMonitor: NetworkMonitor,
) {
    val shouldShowTopBar: Boolean
        get() = widthSizeClass == WindowWidthSizeClass.Compact

    val shouldShowNavRail: Boolean
        get() = widthSizeClass == WindowWidthSizeClass.Medium

    val shouldShowAppDrawer: Boolean
        get() = widthSizeClass == WindowWidthSizeClass.Expanded
    fun navigateToAppDestination(appDestination: AppDestinations) {
        when (appDestination) {
            AppDestinations.HOME_ROUTE -> vipExamNavigationActions.navigateToHome()
            AppDestinations.SECOND_ROUTE -> vipExamNavigationActions.navigateToSecond()
            AppDestinations.SETTINGS_ROUTE -> vipExamNavigationActions.navigateToSettings()
            AppDestinations.HISTORY -> vipExamNavigationActions.navigateToHistory()
            AppDestinations.BOOKMARKS -> vipExamNavigationActions.navigateToBookmarks()
        }
    }

    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )
}