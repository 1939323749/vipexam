package app.xlei.vipexam.ui

import android.annotation.SuppressLint
import android.graphics.drawable.Icon
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.xlei.vipexam.R

enum class AppScreen(@StringRes val title: Int,val icon: ImageVector) {
    First(title = R.string.main, icon = Icons.Filled.Home),
    Second(title = R.string.second, icon = Icons.Filled.Edit),
    Third(title = R.string.setting, icon = Icons.Filled.Settings)
}
@Composable
private fun MainScreenNavigationConfigurations(
    navController: NavHostController,
    showBottomBar: MutableState<Boolean>
) {
    NavHost(navController, startDestination = AppScreen.First.name) {
        composable(AppScreen.First.name) {
            VipExamAppMainScreen(
                showBottomBar = showBottomBar
            )
        }
        composable(AppScreen.Second.name) {
            Text("todo")
        }
        composable(AppScreen.Third.name) {
            Text("todo")
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun App(
    navController: NavHostController = rememberNavController()
){
    val items = listOf(
        AppScreen.First,
        AppScreen.Second,
        AppScreen.Third
    )
    var selectedItem by remember { mutableIntStateOf(0) }
    val showBottomBar = remember { mutableStateOf(true) }

    Scaffold (
        bottomBar = {
            AnimatedVisibility(showBottomBar.value){
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentScreen = AppScreen.valueOf(
                    backStackEntry?.destination?.route?: AppScreen.First.name
                )
                NavigationBar {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.name) },
                            label = {
                                AnimatedVisibility(selectedItem == index){
                                    if(selectedItem == index)Text(stringResource(items[selectedItem].title))else{
                                        Spacer(Modifier)
                                    }
                                } },
                            selected = selectedItem == index,
                            onClick = {
                                selectedItem = index
                                if (currentScreen != items[selectedItem]) {
                                    navController.navigate(items[selectedItem].name)
                                }
                            }
                        )
                    }
                }
            }
        }
    ){
        MainScreenNavigationConfigurations(
            navController = navController,
            showBottomBar = showBottomBar
        )
    }
}