package app.xlei.vipexam.feature.history

import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import app.xlei.vipexam.core.ui.DateText
import app.xlei.vipexam.core.ui.LoginAlert
import app.xlei.vipexam.core.ui.slideInSlideOutNavigationContainer
import app.xlei.vipexam.feature.history.component.HistoryHeatMapCalendar
import compose.icons.FeatherIcons
import compose.icons.feathericons.Activity
import compose.icons.feathericons.Menu
import compose.icons.feathericons.Trash2
import java.time.Instant
import java.time.ZoneId

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
)
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel(),
    openDrawer: () -> Unit,
    onHistoryClick: (String) -> Boolean,
) {
    val examHistoryState by viewModel.examHistory.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    var showLoginAlert by remember {
        mutableStateOf(false)
    }
    var warnDeleteHistory by remember {
        mutableStateOf(false)
    }

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HistoryScreen.List.name
    ) {
        slideInSlideOutNavigationContainer(
            route = HistoryScreen.List.name
        ) {
            Scaffold(
                topBar = {
                    LargeTopAppBar(
                        title = {
                            Text(
                                stringResource(R.string.history)
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = openDrawer
                            ) {
                                Icon(
                                    imageVector = FeatherIcons.Menu,
                                    contentDescription = null,
                                )
                            }
                        },
                        actions = {
                            if (Build.VERSION.SDK_INT >= 26) {
                                IconButton(onClick = { navController.navigate(HistoryScreen.HeatMap.name) }) {
                                    Icon(
                                        imageVector = FeatherIcons.Activity,
                                        contentDescription = null
                                    )
                                }
                            }
                            IconButton(
                                onClick = { warnDeleteHistory = true },
                                enabled = examHistoryState.isNotEmpty()
                            ) {
                                Icon(imageVector = FeatherIcons.Trash2, contentDescription = null)
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                ) {
                    LazyColumn {
                        items(examHistoryState.size) { index ->
                            ListItem(
                                headlineContent = { Text(examHistoryState[index].examName) },
                                supportingContent = { DateText(examHistoryState[index].lastOpen) },
                                modifier = Modifier
                                    .combinedClickable(
                                        onClick = {
                                            showLoginAlert =
                                                onHistoryClick.invoke(examHistoryState[index].examId)
                                                    .not()
                                        },
                                        onLongClick = {
                                            viewModel.delete(index)
                                        }
                                    )
                            )
                        }
                        item {
                            if (examHistoryState.isEmpty())
                                Text(
                                    text = stringResource(R.string.empty_history_tips),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxSize()
                                )
                        }
                    }

                    if (showLoginAlert) LoginAlert {
                        showLoginAlert = false
                    }
                }

                if (warnDeleteHistory) {
                    AlertDialog(
                        text = {
                            Text(text = stringResource(id = R.string.check_delete_history))
                        },
                        onDismissRequest = { warnDeleteHistory = false },
                        dismissButton = {
                            TextButton(onClick = { warnDeleteHistory = false }) {
                                Text(text = stringResource(id = R.string.cancel))
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.cleanHistory()
                                warnDeleteHistory = false
                            }) {
                                Text(text = stringResource(id = R.string.ok))
                            }
                        }
                    )
                }
            }
        }
        slideInSlideOutNavigationContainer(
            route = HistoryScreen.HeatMap.name
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                },
                modifier = Modifier.fillMaxSize()
            ) { padding ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    if (Build.VERSION.SDK_INT >= 26) {
                        HistoryHeatMapCalendar {
                            return@HistoryHeatMapCalendar viewModel
                                .getHistoryByDateRange()
                                .run {
                                    this.map {
                                        Instant.ofEpochMilli(it.lastOpen)
                                            .atZone(ZoneId.systemDefault()).toLocalDate()
                                    }.run {
                                        this.map {
                                            it to this.count { date -> it == date }
                                        }
                                    }
                                }.toMap().also { println(it) }
                        }
                    }
                }
            }
        }
    }
}