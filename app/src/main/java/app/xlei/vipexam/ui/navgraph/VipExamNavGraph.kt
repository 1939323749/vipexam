package app.xlei.vipexam.ui.navgraph

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import app.xlei.vipexam.data.AppContainer
import app.xlei.vipexam.ui.HomeRoute
import app.xlei.vipexam.ui.navigation.AppDestinations
import app.xlei.vipexam.ui.navigation.HomeScreen
import app.xlei.vipexam.ui.page.SettingsScreen

@Composable
fun VipExamNavHost(
    logoText: MutableState<HomeScreen>,
    showAnswer: MutableState<Boolean>,
    navHostController: NavHostController,
    homeNavController: NavHostController,
    appContainer: AppContainer,
    isExpandedScreen: Boolean,
) {
    NavHost(
        navController = navHostController,
        startDestination = AppDestinations.HOME_ROUTE.name
    ) {
        homeScreen(
            logoText, homeNavController, showAnswer, isExpandedScreen
        )
        composable(
            route = AppDestinations.SECOND_ROUTE.name,
        ) { navBackStackEntry ->
            Scaffold(
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                ) { }
            }
        }
        composable(
            route = AppDestinations.SETTINGS_ROUTE.name,
        ) { navBackStackEntry ->
            SettingsScreen()
        }
    }
}

fun NavGraphBuilder.homeScreen(
    logoText: MutableState<HomeScreen>,
    homeNavController: NavHostController,
    showAnswer: MutableState<Boolean>,
    isExpandedScreen: Boolean,
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
        HomeRoute(
            logoText = logoText,
            navController = homeNavController,
            showAnswer = showAnswer,
            showBottomBar = remember { mutableStateOf(false) },
            isExpandedScreen = isExpandedScreen,
        )
    }
}