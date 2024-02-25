package app.xlei.vipexam.feature.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import app.xlei.vipexam.core.ui.DateText
import app.xlei.vipexam.core.ui.LoginAlert
import compose.icons.FeatherIcons
import compose.icons.feathericons.Menu
import compose.icons.feathericons.Trash2

@OptIn(
    ExperimentalMaterial3Api::class,
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
                    IconButton(
                        onClick = { viewModel.cleanHistory() },
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
                            .clickable {
                                showLoginAlert = onHistoryClick.invoke(examHistoryState[index].examId).not()
                            }
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
    }
}