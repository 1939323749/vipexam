package app.xlei.vipexam.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun AppDrawer(
    currentRoute: String,
    navigateToHome: () -> Unit,
    navigateToSecond: () -> Unit,
    navigateToSettings: () -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(modifier) {
        NavigationDrawerItem(
            label = { Text(stringResource(id = AppDestinations.HOME_ROUTE.title)) },
            icon = { Icon(AppDestinations.HOME_ROUTE.icon, null) },
            selected = currentRoute == AppDestinations.HOME_ROUTE.name,
            onClick = { navigateToHome(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text(stringResource(id = AppDestinations.SECOND_ROUTE.title)) },
            icon = { Icon(AppDestinations.SECOND_ROUTE.icon, null) },
            selected = currentRoute == AppDestinations.SECOND_ROUTE.name,
            onClick = { navigateToSecond(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text(stringResource(id = AppDestinations.SETTINGS_ROUTE.title)) },
            icon = { Icon(AppDestinations.SETTINGS_ROUTE.icon, null) },
            selected = currentRoute == AppDestinations.SETTINGS_ROUTE.name,
            onClick = { navigateToSettings(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}