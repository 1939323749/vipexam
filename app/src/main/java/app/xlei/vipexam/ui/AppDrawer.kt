package app.xlei.vipexam.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.xlei.vipexam.ui.navigation.AppDestinations

@Composable
fun AppDrawer(
    currentRoute: String,
    navigationToTopLevelDestination: (AppDestinations) -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(modifier) {
        NavigationDrawerItem(
            label = { Text(stringResource(id = AppDestinations.HOME_ROUTE.title)) },
            icon = { Icon(AppDestinations.HOME_ROUTE.icon, null) },
            selected = currentRoute == AppDestinations.HOME_ROUTE.name,
            onClick = { navigationToTopLevelDestination(AppDestinations.HOME_ROUTE); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text(stringResource(id = AppDestinations.SECOND_ROUTE.title)) },
            icon = { Icon(AppDestinations.SECOND_ROUTE.icon, null) },
            selected = currentRoute == AppDestinations.SECOND_ROUTE.name,
            onClick = { navigationToTopLevelDestination(AppDestinations.SECOND_ROUTE); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text(stringResource(id = AppDestinations.SETTINGS_ROUTE.title)) },
            icon = { Icon(AppDestinations.SETTINGS_ROUTE.icon, null) },
            selected = currentRoute == AppDestinations.SETTINGS_ROUTE.name,
            onClick = { navigationToTopLevelDestination(AppDestinations.SETTINGS_ROUTE); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}