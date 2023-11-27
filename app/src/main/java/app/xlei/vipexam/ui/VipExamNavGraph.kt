package app.xlei.vipexam.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import app.xlei.vipexam.data.AppContainer

@Composable
fun VipExamNavGraph(
    logoText: MutableState<VipExamScreen>,
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
            Spacer(Modifier)
        }
    }
}
