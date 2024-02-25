package app.xlei.vipexam.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import app.xlei.vipexam.R
import compose.icons.FeatherIcons
import compose.icons.feathericons.Bookmark

enum class AppDestinations(@StringRes val title: Int, val icon: ImageVector) {
    HOME_ROUTE(title = R.string.main, icon = Icons.Filled.Home),
    SECOND_ROUTE(title = R.string.second, icon = Icons.Filled.Edit),
    SETTINGS_ROUTE(title = R.string.settings, icon = Icons.Filled.Settings),
    HISTORY(title = R.string.history, icon = Icons.Filled.DateRange),
    BOOKMARKS(title = R.string.bookmarks, icon = FeatherIcons.Bookmark)
}

class VipExamNavigationActions(navController: NavController) {
    val navigateToHome: () -> Unit = {
        navController.navigate(AppDestinations.HOME_ROUTE.name) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToSecond: () -> Unit = {
        navController.navigate(AppDestinations.SECOND_ROUTE.name) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToSettings: () -> Unit = {
        navController.navigate(AppDestinations.SETTINGS_ROUTE.name) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToHistory: () -> Unit = {
        navController.navigate(AppDestinations.HISTORY.name) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToBookmarks: () -> Unit = {
        navController.navigate(AppDestinations.BOOKMARKS.name) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}