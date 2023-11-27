package app.xlei.vipexam.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import app.xlei.vipexam.ui.AppDestinations
import app.xlei.vipexam.ui.HomeScreen
import io.ktor.http.*

@Composable
fun AppNavRail(
    logo: MutableState<HomeScreen>,
    showAnswer: MutableState<Boolean>,
    homeNavController: NavHostController,
    currentRoute: String,
    navigateToHome: () -> Unit,
    navigateToSecond: () -> Unit,
    navigateToSettings: () -> Unit,
    openDrawer: () -> Unit = {},
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    NavigationRail(
        header = {
            Icon(logo.value.icon,"",Modifier.padding(vertical = 24.dp))
            CustomFloatingActionButton(
                expandable = true,
                onFabClick = {},
                iconUnExpanded = Icons.Default.Edit,
                iconExpanded = Icons.Default.Edit,
                items = listOf("SHOW_ANSWER" to "show answer"),
                onItemClick = {
                    when (it) {
                        "SHOW_ANSWER" -> { showAnswer.value = !showAnswer.value }
                        else -> {}
                    }
                }
            )
//            FloatingActionButton(
//                onClick = { showAnswer.value = !showAnswer.value },
//            ){
//                Icon(Icons.Default.Edit,"")
//            }
        },
        containerColor = NavigationRailDefaults.ContainerColor,
        modifier = modifier
    ) {
        Spacer(Modifier.weight(1f))
        NavigationRailItem(
            selected = currentRoute == AppDestinations.HOME_ROUTE.name,
            onClick = navigateToHome,
            icon = { Icon(AppDestinations.HOME_ROUTE.icon, stringResource(AppDestinations.HOME_ROUTE.title)) },
            label = { Text(stringResource(AppDestinations.HOME_ROUTE.title)) },
            alwaysShowLabel = false
        )
        NavigationRailItem(
            selected = currentRoute == AppDestinations.SECOND_ROUTE.name,
            onClick = navigateToSecond,
            icon = { Icon(AppDestinations.SECOND_ROUTE.icon, stringResource(AppDestinations.SECOND_ROUTE.title)) },
            label = { Text(stringResource(AppDestinations.SECOND_ROUTE.title)) },
            alwaysShowLabel = false
        )
        NavigationRailItem(
            selected = currentRoute == AppDestinations.SETTINGS_ROUTE.name,
            onClick = navigateToSettings,
            icon = { Icon(AppDestinations.SETTINGS_ROUTE.icon, stringResource(AppDestinations.SETTINGS_ROUTE.title)) },
            label = { Text(stringResource(AppDestinations.SETTINGS_ROUTE.title)) },
            alwaysShowLabel = false
        )
        if (currentRoute == AppDestinations.HOME_ROUTE.name &&
            homeNavController.currentBackStack.value.size > 2)
            IconButton(
                onClick = { homeNavController.navigateUp() },
            ){
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft,null)
            }
        Spacer(Modifier.weight(1f))
    }
}