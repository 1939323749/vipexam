package app.xlei.vipexam.feature.wordlist

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.xlei.vipexam.feature.wordlist.components.CopyToClipboardButton
import app.xlei.vipexam.feature.wordlist.components.TranslationSheet
import app.xlei.vipexam.feature.wordlist.constant.SortMethod
import compose.icons.FeatherIcons
import compose.icons.feathericons.Menu
import java.util.Date

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
)
@Composable
fun WordListScreen(
    viewModel: WordListViewModel = hiltViewModel(),
    openDrawer: () -> Unit,
) {
    val wordListState by viewModel.wordList.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    var textToTranslate by remember { mutableStateOf("") }
    var showTranslationSheet by remember { mutableStateOf(false) }

    var showSortMethods by rememberSaveable {
        mutableStateOf(false)
    }
    val sortMethod = viewModel.sortMethod.collectAsState()
    val context = LocalContext.current
    Scaffold(
        topBar = {
            LargeTopAppBar(
                actions = {
                    IconButton(onClick = { viewModel.exportWordsToCSV(context) }) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null)
                    }
                },
                title = {
                    Text(
                        stringResource(R.string.words)
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
                                    onClick = { showSortMethods = !showSortMethods },
                                    label = { Text(text = stringResource(id = R.string.sort)) },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = if (showSortMethods) {
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
                            if (showSortMethods)
                                SortMethod.entries.forEach {
                                    item {
                                        FilterChip(
                                            onClick = {
                                                viewModel.setSortMethod(it)
                                            },
                                            label = { Text(text = stringResource(id = it.method)) },
                                            selected = it == sortMethod.value,
                                            modifier = Modifier
                                                .padding(horizontal = 8.dp)
                                        )
                                    }
                                }
                        }
                    }
                }
                items(wordListState.size) { index ->
                    ListItem(
                        headlineContent = { Text(wordListState[index].word) },
                        supportingContent = { Text(Date(wordListState[index].created).toString()) },
                        trailingContent = { CopyToClipboardButton(wordListState[index].word) },
                        modifier = Modifier
                            .combinedClickable(
                                onClick = {
                                    textToTranslate = wordListState[index].word
                                    showTranslationSheet = true
                                },
                                onLongClick = { viewModel.removeWord(wordListState[index]) }
                            )
                    )
                }
                item {
                    if (wordListState.isEmpty())
                        Text(
                            text = stringResource(R.string.empty_wordlist_tips),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                }
            }

            if (showTranslationSheet)
                TranslationSheet(
                    textToTranslate
                ) {
                    showTranslationSheet = !showTranslationSheet
                }
        }
    }
}
