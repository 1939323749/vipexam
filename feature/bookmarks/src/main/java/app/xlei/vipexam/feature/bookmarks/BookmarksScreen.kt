package app.xlei.vipexam.feature.bookmarks

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.xlei.vipexam.core.ui.DateText
import app.xlei.vipexam.core.ui.LoginAlert
import compose.icons.FeatherIcons
import compose.icons.feathericons.Menu

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
)
@Composable
fun BookmarksScreen(
    modifier: Modifier = Modifier,
    viewModel: BookmarksViewModel = hiltViewModel(),
    openDrawer: () -> Unit,
    onBookmarkClick: (String,String) -> Boolean,
) {
    val bookmarksState by viewModel.bookmarks.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    var showFilter by rememberSaveable {
        mutableStateOf(false)
    }
    var showLoginAlert by remember {
        mutableStateOf(false)
    }
    val questions = bookmarksState.map { it.question }.toSet()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.bookmarks)
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
                stickyHeader {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        LazyRow {
                            item {
                                AssistChip(
                                    onClick = { showFilter = !showFilter },
                                    label = { Text(text = stringResource(id = R.string.question)) },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = if (showFilter) {
                                                Icons.Default.KeyboardArrowUp
                                            } else {
                                                Icons.Default.KeyboardArrowDown
                                            },
                                            contentDescription = null,
                                        )
                                    },
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                )
                            }
                        }
                        LazyRow {
                            if (showFilter)
                                questions.forEach {
                                    item {
                                        FilterChip(
                                            onClick = {
                                                when (filter) {
                                                    it -> viewModel.setFilter()
                                                    else -> viewModel.setFilter(it)
                                                }
                                            },
                                            label = { Text(text = it) },
                                            selected = it == filter,
                                            modifier = Modifier
                                                .padding(horizontal = 8.dp)
                                        )
                                    }
                                }
                        }
                    }
                }
                items(bookmarksState.size) { index ->
                    ListItem(
                        headlineContent = { Text(bookmarksState[index].question) },
                        supportingContent = {
                            Text(
                                text = bookmarksState[index].examName,
                                style = MaterialTheme.typography.bodySmall,
                            ) },
                        trailingContent = { DateText(bookmarksState[index].created) },
                        modifier = Modifier
                            .clickable {
                                showLoginAlert = onBookmarkClick.invoke(bookmarksState[index].examId,bookmarksState[index].question).not()
                            }
                    )
                }
                item {
                    if (bookmarksState.isEmpty())
                        Text(
                            text = stringResource(R.string.empty_bookmarks_tips),
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