package app.xlei.vipexam.ui.navigation

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import app.xlei.vipexam.MainActivity
import app.xlei.vipexam.core.network.module.NetWorkRepository
import app.xlei.vipexam.feature.bookmarks.BookmarksScreen
import app.xlei.vipexam.feature.history.HistoryScreen
import app.xlei.vipexam.feature.settings.SettingsScreen
import app.xlei.vipexam.feature.wordlist.WordListScreen
import app.xlei.vipexam.ui.VipExamMainScreenViewModel
import app.xlei.vipexam.ui.screen.HomeScreen

/**
 * Vip exam nav host
 *
 * @param logoText logo
 * @param navHostController app导航控制器
 * @param homeNavController 主页导航控制器
 * @param widthSizeClass 屏幕宽度
 * @param openDrawer 打开抽屉事件
 * @receiver
 * @receiver
 */
@Composable
fun VipExamNavHost(
    logoText:  @Composable () -> Unit = {},
    navHostController: NavHostController,
    homeNavController: NavHostController,
    widthSizeClass: WindowWidthSizeClass,
    openDrawer: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel : VipExamMainScreenViewModel = hiltViewModel()

    NavHost(
        navController = navHostController,
        startDestination = AppDestinations.HOME_ROUTE.name,
        modifier = Modifier
            .fillMaxSize()
    ) {
        composable(
            route = AppDestinations.HOME_ROUTE.name,
        ) {
            HomeScreen(
                logoText = { Icon(imageVector = Icons.Default.Home, contentDescription = null) },
                widthSizeClass = widthSizeClass,
                navController = homeNavController,
                viewModel = viewModel,
                openDrawer = openDrawer,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
        composable(
            route = AppDestinations.SECOND_ROUTE.name,
        ) { _ ->
            WordListScreen(
                openDrawer = openDrawer,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
        composable(
            route = AppDestinations.SETTINGS_ROUTE.name,
        ) { _ ->
            SettingsScreen(
                openDrawer = openDrawer,
                onLanguageChange = {
                    Handler(Looper.getMainLooper()).postDelayed({
                        (context as MainActivity).recreate()
                    }, 100)
                },
                modifier = Modifier
                    .fillMaxSize()
            )
        }
        composable(
            route = AppDestinations.BOOKMARKS.name
        ){
            BookmarksScreen(
                openDrawer = openDrawer,
                modifier = Modifier
                    .fillMaxSize()
            ){examId,question->
                when (NetWorkRepository.isAvailable()) {
                    true -> {
                        navHostController.navigate(AppDestinations.HOME_ROUTE.name){
                            popUpTo(navHostController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        homeNavController.navigate(Screen.Question.createRoute(examId,question)){
                            launchSingleTop = true
                        }
                        true
                    }
                    false -> false
                }
            }
        }
        composable(
            route = AppDestinations.HISTORY.name
        ){
            HistoryScreen(
                openDrawer = openDrawer,
                onHistoryClick = {
                    when (NetWorkRepository.isAvailable()) {
                        true -> {
                            navHostController.navigate(AppDestinations.HOME_ROUTE.name){
                                popUpTo(navHostController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            homeNavController.navigate(Screen.Exam.createRoute(it)){
                                launchSingleTop = true
                                restoreState = true
                            }
                            true
                        }
                        false -> false
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}

