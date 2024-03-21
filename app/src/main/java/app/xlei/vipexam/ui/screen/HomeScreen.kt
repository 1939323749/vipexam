package app.xlei.vipexam.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavHostController
import app.xlei.vipexam.ui.VipExamMainScreenViewModel
import app.xlei.vipexam.ui.appbar.VipExamAppBar
import app.xlei.vipexam.ui.navigation.MainScreenNavigation

/**
 * Home screen
 *
 * @param modifier
 * @param logoText
 * @param widthSizeClass 屏幕宽度
 * @param viewModel 主页vm
 * @param navController 主页导航控制器
 * @param openDrawer 打开侧边抽屉事件
 * @receiver
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    logoText: (@Composable () -> Unit)? = null,
    widthSizeClass: WindowWidthSizeClass,
    viewModel: VipExamMainScreenViewModel,
    navController: NavHostController,
    openDrawer: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsState()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    val localConfiguration = LocalConfiguration.current

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .run {
                // LazyColumn will not be scrollable without this in expanded width size
                when (widthSizeClass) {
                    WindowWidthSizeClass.Compact -> this.nestedScroll(scrollBehavior.nestedScrollConnection)
                    WindowWidthSizeClass.Expanded -> {
                        when (localConfiguration.orientation) {
                            Configuration.ORIENTATION_LANDSCAPE -> this
                            Configuration.ORIENTATION_PORTRAIT -> this.nestedScroll(scrollBehavior.nestedScrollConnection)
                            else -> this
                        }
                    }

                    else -> this
                }
            },
        topBar = {
            when (widthSizeClass) {
                WindowWidthSizeClass.Compact ->
                    VipExamAppBar(
                        appBarTitle = uiState.title,
                        canNavigateBack = navController.previousBackStackEntry != null,
                        navigateUp = { navController.navigateUp() },
                        openDrawer = openDrawer,
                        scrollBehavior = scrollBehavior,
                        myAnswer = viewModel.myAnswer.collectAsState().value,
                    )

                WindowWidthSizeClass.Medium -> {
                    VipExamAppBar(
                        appBarTitle = uiState.title,
                        canNavigateBack = navController.previousBackStackEntry != null,
                        navigateUp = { navController.navigateUp() },
                        openDrawer = openDrawer,
                        scrollBehavior = scrollBehavior,
                        myAnswer = viewModel.myAnswer.collectAsState().value,
                    )
                }

                WindowWidthSizeClass.Expanded -> {
                    when (localConfiguration.orientation) {
                        Configuration.ORIENTATION_PORTRAIT -> {
                            VipExamAppBar(
                                appBarTitle = uiState.title,
                                canNavigateBack = navController.previousBackStackEntry != null,
                                navigateUp = { navController.navigateUp() },
                                openDrawer = openDrawer,
                                scrollBehavior = scrollBehavior,
                                myAnswer = viewModel.myAnswer.collectAsState().value,
                            )
                        }

                        else -> {
                            VipExamAppBar(
                                appBarTitle = uiState.title,
                                canNavigateBack = navController.previousBackStackEntry != null,
                                navigateUp = { navController.navigateUp() },
                                openDrawer = openDrawer,
                                scrollBehavior = scrollBehavior,
                                scrollable = false,
                                myAnswer = viewModel.myAnswer.collectAsState().value,
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        MainScreenNavigation(
            navHostController = navController,
            widthSizeClass = widthSizeClass,
            modifier = Modifier.padding(padding),
            viewModel = viewModel,
        )
    }
}



