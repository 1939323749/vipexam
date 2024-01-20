package app.xlei.vipexam.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.xlei.vipexam.R
import app.xlei.vipexam.core.data.util.NetworkMonitor
import app.xlei.vipexam.ui.components.AppDrawer
import app.xlei.vipexam.ui.components.AppNavRail
import app.xlei.vipexam.ui.navgraph.VipExamNavHost
import app.xlei.vipexam.ui.navigation.AppDestinations
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ResourceType")
@Composable
fun App(
    widthSizeClass: WindowWidthSizeClass,
    networkMonitor: NetworkMonitor,
    appState: VipExamState = rememberVipExamAppState(
        windowSizeClass = widthSizeClass,
        networkMonitor = networkMonitor,
    ),
) {
    val homeNavController = rememberNavController()

    val navBackStackEntry by appState.navController.currentBackStackEntryAsState()
    val currentRoute =
        navBackStackEntry?.destination?.route ?: AppDestinations.HOME_ROUTE.name

    val sizeAwareDrawerState = rememberSizeAwareDrawerState(appState.shouldShowTopBar)

    val coroutine = rememberCoroutineScope()

    val snackBarHostState = remember { SnackbarHostState() }

    val isOffline by appState.isOffline.collectAsState()

    val notConnectedMessage = stringResource(R.string.not_connected)
    LaunchedEffect(isOffline) {
        if (isOffline) {
            snackBarHostState.showSnackbar(
                message = notConnectedMessage,
                duration = SnackbarDuration.Indefinite,
            )
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 24),
        snackbarHost = { SnackbarHost(snackBarHostState) },
    ) { padding ->
        ModalNavigationDrawer(
            drawerContent = {
                AppDrawer(
                    currentRoute = currentRoute,
                    navigationToTopLevelDestination = { appState.navigateToAppDestination(it) },
                    closeDrawer = { coroutine.launch { sizeAwareDrawerState.close() } },
                    modifier = Modifier
                        .width(300.dp)
                        .padding(top = 24.dp)
                )
            },
            drawerState = sizeAwareDrawerState,
            gesturesEnabled = appState.shouldShowTopBar,
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            Row {
                if (appState.shouldShowNavRail) {
                    AppNavRail(
                        homeNavController = homeNavController,
                        currentRoute = currentRoute,
                        navigationToTopLevelDestination = { appState.navigateToAppDestination(it) },
                    )
                }
                VipExamNavHost(
                    navHostController = appState.navController,
                    homeNavController = homeNavController,
                    widthSizeClass = widthSizeClass,
                    openDrawer = { coroutine.launch { sizeAwareDrawerState.open() } },
                )
            }
        }
    }

}


@Composable
private fun rememberSizeAwareDrawerState(isExpandedScreen: Boolean): DrawerState {
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    return if (!isExpandedScreen) {
        drawerState
    } else {
        DrawerState(DrawerValue.Closed)
    }
}