package app.xlei.vipexam.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.xlei.vipexam.ui.navigation.AppDestinations

/**
 * App drawer
 *
 * @param currentRoute 当前导航
 * @param navigationToTopLevelDestination 导航函数
 * @param closeDrawer 关闭抽屉函数
 * @param modifier
 * @receiver
 * @receiver
 */
@Composable
fun AppDrawer(
    currentRoute: String,
    navigationToTopLevelDestination: (AppDestinations) -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        drawerContentColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        AppDestinations.entries.forEach {
            VipexamDrawerItem(
                currentRoute = currentRoute,
                destination = it,
                navigationToTopLevelDestination = navigationToTopLevelDestination
            ) {
                closeDrawer.invoke()
            }
        }
    }
}

@Composable
private fun VipexamDrawerItem(
    currentRoute: String,
    destination: AppDestinations,
    navigationToTopLevelDestination: (AppDestinations) -> Unit,
    closeDrawer: () -> Unit,
){
    NavigationDrawerItem(
        label = { Text(stringResource(id = destination.title)) },
        icon = { Icon(destination.icon, null) },
        selected = currentRoute == destination.name,
        onClick = { navigationToTopLevelDestination(destination); closeDrawer() },
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}