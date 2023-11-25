package app.xlei.vipexam.ui

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import app.xlei.vipexam.R
import kotlinx.coroutines.launch

enum class AppScreen(@StringRes val title: Int,val icon: ImageVector) {
    First(title = R.string.main, icon = Icons.Filled.Home),
    Second(title = R.string.second, icon = Icons.Filled.Edit),
    Third(title = R.string.setting, icon = Icons.Filled.Settings)
}

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ResourceType")
@Composable
fun App(){
    val items = listOf(
        AppScreen.First,
        AppScreen.Second,
        AppScreen.Third
    )
    var selectedItem by remember { mutableIntStateOf(0) }
    val showBottomBar = remember { mutableStateOf(true) }
    val pageState = rememberPagerState(pageCount = { items.size })
    val coroutine = rememberCoroutineScope()

    Scaffold (
        bottomBar = {
            AnimatedVisibility(showBottomBar.value){
                NavigationBar {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.name) },
                            label = {
                                AnimatedVisibility(pageState.currentPage == index){
                                    if(pageState.currentPage == index)Text(stringResource(items[selectedItem].title))else{
                                        Spacer(Modifier)
                                    }
                                } },
                            selected = pageState.currentPage == index,
                            onClick = {
                                selectedItem = index
                                if (pageState.currentPage != selectedItem) {
                                    coroutine.launch {
                                        pageState.animateScrollToPage(selectedItem)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ){
        HorizontalPager(
            state = pageState,
            userScrollEnabled = showBottomBar.value
        ){page ->
            when (page){
                0 -> VipExamAppMainScreen(
                    showBottomBar = showBottomBar
                )
                1 -> Text("todo")
                2 -> Text("todo")
            }
        }
    }
}