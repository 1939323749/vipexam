package app.xlei.vipexam.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import app.xlei.vipexam.ui.navigation.AppDestinations
import app.xlei.vipexam.ui.navigation.HomeScreen
import app.xlei.vipexam.util.Preferences
import app.xlei.vipexam.util.dataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppNavRail(
    logo: MutableState<HomeScreen>,
    homeNavController: NavHostController,
    currentRoute: String,
    navigationToTopLevelDestination: (AppDestinations) -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    var startTimer by remember { mutableStateOf(true) }
    var resetTimer by remember { mutableStateOf(false) }
    val coroutine = rememberCoroutineScope()
    val context = LocalContext.current

    NavigationRail(
        header = {
            if (homeNavController.currentBackStackEntryAsState().value?.destination?.route == HomeScreen.QuestionListWithQuestion.name)
                Timer(
                    isTimerStart = startTimer,
                    isResetTimer = resetTimer,
                    modifier = Modifier
                        .padding(vertical = 24.dp)
                        .combinedClickable(
                            onClick = {
                                startTimer = !startTimer
                            },
                            onLongClick = {
                                coroutine.launch {
                                    resetTimer = true
                                    delay(50)
                                    resetTimer = false
                                }
                            }
                        )
                )
            else
                Icon(logo.value.icon, "", Modifier.padding(vertical = 24.dp))
            CustomFloatingActionButton(
                expandable = true,
                onFabClick = {},
                iconUnExpanded = Icons.Default.Edit,
                iconExpanded = Icons.Default.Edit,
                items = listOf("SHOW_ANSWER" to stringResource(app.xlei.vipexam.R.string.show_answer)),
                onItemClick = {
                    when (it) {
                        "SHOW_ANSWER" -> {
                            coroutine.launch {
                                context.dataStore.edit { preferences ->
                                    val showAnswer = preferences[Preferences.SHOW_ANSWER]
                                    preferences[Preferences.SHOW_ANSWER] = showAnswer?.not() ?: true
                                }
                            }
                        }

                        else -> {}
                    }
                }
            )
        },
        containerColor = NavigationRailDefaults.ContainerColor,
        modifier = modifier
    ) {
        Spacer(Modifier.weight(1f))
        NavigationRailItem(
            selected = currentRoute == AppDestinations.HOME_ROUTE.name,
            onClick = { navigationToTopLevelDestination(AppDestinations.HOME_ROUTE) },
            icon = { Icon(AppDestinations.HOME_ROUTE.icon, stringResource(AppDestinations.HOME_ROUTE.title)) },
            label = { Text(stringResource(AppDestinations.HOME_ROUTE.title)) },
            alwaysShowLabel = false
        )
        NavigationRailItem(
            selected = currentRoute == AppDestinations.SECOND_ROUTE.name,
            onClick = { navigationToTopLevelDestination(AppDestinations.SECOND_ROUTE) },
            icon = { Icon(AppDestinations.SECOND_ROUTE.icon, stringResource(AppDestinations.SECOND_ROUTE.title)) },
            label = { Text(stringResource(AppDestinations.SECOND_ROUTE.title)) },
            alwaysShowLabel = false
        )
        NavigationRailItem(
            selected = currentRoute == AppDestinations.SETTINGS_ROUTE.name,
            onClick = { navigationToTopLevelDestination(AppDestinations.SETTINGS_ROUTE) },
            icon = { Icon(AppDestinations.SETTINGS_ROUTE.icon, stringResource(AppDestinations.SETTINGS_ROUTE.title)) },
            label = { Text(stringResource(AppDestinations.SETTINGS_ROUTE.title)) },
            alwaysShowLabel = false
        )
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