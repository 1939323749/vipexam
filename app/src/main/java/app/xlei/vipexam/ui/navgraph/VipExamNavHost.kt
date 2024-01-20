package app.xlei.vipexam.ui.navgraph

import android.os.Handler
import android.os.Looper
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import app.xlei.vipexam.MainActivity
import app.xlei.vipexam.feature.settings.SettingsScreen
import app.xlei.vipexam.feature.wordlist.WordListScreen
import app.xlei.vipexam.ui.navigation.AppDestinations
import app.xlei.vipexam.ui.navigation.HomeScreen


@Composable
fun VipExamNavHost(
    logoText:  @Composable () -> Unit = {},
    navHostController: NavHostController,
    homeNavController: NavHostController,
    widthSizeClass: WindowWidthSizeClass,
    openDrawer: () -> Unit,
) {
    val context = LocalContext.current
    NavHost(
        navController = navHostController,
        startDestination = AppDestinations.HOME_ROUTE.name
    ) {
        homeScreen(
            logoText = logoText,
            homeNavController = homeNavController,
            widthSizeClass = widthSizeClass,
            openDrawer = openDrawer,
        )
        composable(
            route = AppDestinations.SECOND_ROUTE.name,
        ) { navBackStackEntry ->
            WordListScreen(
                openDrawer = openDrawer,
            )
        }
        composable(
            route = AppDestinations.SETTINGS_ROUTE.name,
        ) { navBackStackEntry ->
            SettingsScreen(
                openDrawer = openDrawer,
                onLanguageChange = {
                    Handler(Looper.getMainLooper()).postDelayed({
                        (context as MainActivity).recreate()
                    }, 100)
                }
            )
        }
    }
}

