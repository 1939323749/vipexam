package app.xlei.vipexam.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import app.xlei.vipexam.R

enum class AppDestinations(@StringRes val title: Int, val icon: ImageVector) {
    HOME_ROUTE(title = R.string.main, icon = Icons.Filled.Home),
    SECOND_ROUTE(title = R.string.second, icon = Icons.Filled.Edit),
    SETTINGS_ROUTE(title = R.string.setting, icon = Icons.Filled.Settings)
}

class VipExamNavigationActions(navController: NavHostController) {
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
}