package app.xlei.vipexam.ui.navgraph

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import app.xlei.vipexam.data.AppContainer
import app.xlei.vipexam.ui.VipExamAppMainScreen
import app.xlei.vipexam.ui.navigation.AppDestinations
import app.xlei.vipexam.ui.navigation.HomeScreen

@Composable
fun VipExamNavGraph(
    logoText: MutableState<HomeScreen>,
    showAnswer: MutableState<Boolean>,
    homeNavController: NavHostController,
    appContainer: AppContainer,
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    openDrawer: () -> Unit = {},
    startDestination: String = AppDestinations.HOME_ROUTE.name,
) {
    val showBottomBar = remember { mutableStateOf(true) }
    val isFirstItemHidden = remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            route = AppDestinations.HOME_ROUTE.name,
            deepLinks = listOf(
                navDeepLink {
                    val EXAM_TYPE = "examType"
                    val EXAM_ID = "examId"
                    uriPattern =
                        "${AppDestinations.HOME_ROUTE}?$EXAM_TYPE={$EXAM_TYPE}&${EXAM_ID}=${EXAM_ID}"
                }
            )
        ) {
            VipExamAppMainScreen(
                logoText = logoText,
                navController = homeNavController,
                showAnswer = showAnswer,
                showBottomBar = showBottomBar,
                isExpandedScreen = isExpandedScreen,
                onFirstItemHidden = { },
                onFirstItemAppear = { },
            )
        }
        composable(
            route = AppDestinations.SECOND_ROUTE.name,
        ) { navBackStackEntry ->
            Spacer(Modifier)
        }
        composable(
            route = AppDestinations.SETTINGS_ROUTE.name,
        ) { navBackStackEntry ->
            Column {
//                SettingsDialog(
//                    onDismiss = {}
//                )
            }

        }
    }
}
