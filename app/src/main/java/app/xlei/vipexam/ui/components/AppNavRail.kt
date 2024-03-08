package app.xlei.vipexam.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import app.xlei.vipexam.ui.navigation.AppDestinations

/**
 * App nav rail
 *
 * @param logo 侧边导航的logo
 * @param homeNavController 主页导航控制器
 * @param currentRoute 当前导航页面
 * @param navigationToTopLevelDestination 导航函数
 * @param modifier
 * @receiver
 * @receiver
 */
@Composable
fun AppNavRail(
    logo:  @Composable () -> Unit = {},
    homeNavController: NavHostController,
    currentRoute: String,
    navigationToTopLevelDestination: (AppDestinations) -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    NavigationRail(
        header = {
            logo()
        },
        containerColor = NavigationRailDefaults.ContainerColor,
        modifier = modifier
    ) {
        Spacer(Modifier.weight(1f))
        AppDestinations.entries.forEach {
            NavigationRailItem(
                currentRoute = currentRoute,
                appDestination = it,
                navigationToTopLevelDestination = navigationToTopLevelDestination
            )
        }
        if (currentRoute == AppDestinations.HOME_ROUTE.name &&
            homeNavController.previousBackStackEntry != null
        )
            IconButton(
                onClick = { homeNavController.navigateUp() },
            ) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null)
            }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun NavigationRailItem(
    currentRoute: String,
    appDestination: AppDestinations,
    navigationToTopLevelDestination: (AppDestinations) -> Unit,
){
    NavigationRailItem(
        selected = currentRoute == appDestination.name,
        onClick = { navigationToTopLevelDestination(appDestination) },
        icon = { Icon(appDestination.icon, stringResource(appDestination.title)) },
        label = { Text(stringResource(appDestination.title)) },
        alwaysShowLabel = false
    )
}