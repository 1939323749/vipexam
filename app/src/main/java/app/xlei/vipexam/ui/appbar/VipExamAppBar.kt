package app.xlei.vipexam.ui.appbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.xlei.vipexam.R
import app.xlei.vipexam.preference.DataStoreKeys
import app.xlei.vipexam.preference.LocalShowAnswer
import app.xlei.vipexam.preference.dataStore
import app.xlei.vipexam.preference.put
import app.xlei.vipexam.ui.components.VipexamCheckbox
import compose.icons.FeatherIcons
import compose.icons.feathericons.Menu
import compose.icons.feathericons.Star
import kotlinx.coroutines.launch

/**
 * Vip exam app bar
 *
 * @param modifier
 * @param scrollable 大屏情况下禁用滚动
 * @param appBarTitle 标题
 * @param canNavigateBack 是否能导航返回
 * @param navigateUp 导航返回的函数
 * @param openDrawer 打开侧边抽屉
 * @param scrollBehavior 滚动行为
 * @param viewModel 标题vm
 * @receiver
 * @receiver
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VipExamAppBar(
    modifier: Modifier = Modifier,
    scrollable: Boolean = true,
    appBarTitle: AppBarTitle,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    openDrawer: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    myAnswer: Map<String, String>,
    viewModel: AppBarViewModel = hiltViewModel()
) {
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()
    val showAnswer = LocalShowAnswer.current.isShowAnswer()

    val uiState by viewModel.bookmarks.collectAsState()
    val submitState by viewModel.submitState.collectAsState()

    val isInBookmark = when (appBarTitle) {
        is AppBarTitle.Exam -> uiState.any { it.examId == appBarTitle.exam.examID && it.question == appBarTitle.question }
        else -> false
    }
    val title = @Composable {
        when (appBarTitle) {
            is AppBarTitle.Exam -> Text(text = appBarTitle.question)
            else -> Text(text = stringResource(id = appBarTitle.nameId))
        }
    }

    val navigationIcon = @Composable {
        when {
            canNavigateBack -> {
                IconButton(
                    onClick = {
                        navigateUp()
                        if (showAnswer) {
                            coroutine.launch {
                                context.dataStore.put(
                                    DataStoreKeys.ShowAnswer,
                                    false
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }

            scrollable ->
                IconButton(onClick = openDrawer) {
                    Icon(
                        imageVector = FeatherIcons.Menu,
                        contentDescription = null,
                    )
                }
        }
    }
    val actions = @Composable {
        when (appBarTitle) {
            is AppBarTitle.Exam -> {
                IconButton(onClick = {
                    if (isInBookmark) viewModel.removeFromBookmarks(
                        bookmark = uiState.first {
                            it.examId == appBarTitle.exam.examID
                                    && it.question == appBarTitle.question
                        }
                    )
                    else viewModel.addToBookmark(
                        appBarTitle.exam.examName,
                        appBarTitle.exam.examID,
                        appBarTitle.question,
                    )
                }) {
                    Icon(
                        imageVector = if (isInBookmark) Icons.Default.Star else FeatherIcons.Star,
                        contentDescription = null
                    )
                }
                IconButton(
                    onClick = { showMenu = !showMenu }
                ) {
                    Icon(Icons.Default.MoreVert, "")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Row {
                                VipexamCheckbox(
                                    checked = showAnswer,
                                    onCheckedChange = null,
                                )
                                Text(
                                    text = stringResource(R.string.show_answer),
                                    modifier = Modifier
                                        .padding(horizontal = 24.dp)
                                )
                            }

                        },
                        onClick = {
                            coroutine.launch {
                                context.dataStore.put(
                                    DataStoreKeys.ShowAnswer,
                                    showAnswer.not()
                                )
                            }
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(id = R.string.submit),
                                modifier = Modifier
                                    .padding(horizontal = 24.dp)
                            )
                        },
                        onClick = {
                            viewModel.submit(appBarTitle.exam, myAnswer)
                        }
                    )
                }
            }

            else -> {}
        }
    }
    when (scrollable) {
        true -> LargeTopAppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = { actions() },
            scrollBehavior = scrollBehavior,
            modifier = modifier
        )

        false -> TopAppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = { actions() },
            modifier = modifier
        )
    }

    if (submitState is SubmitState.Success || submitState is SubmitState.Failed) {
        AlertDialog(
            text = {
                when (submitState) {
                    is SubmitState.Success -> {
                        Column {
                            Text(
                                style = MaterialTheme.typography.bodyLarge,
                                text = "${stringResource(id = R.string.total)}: ${(submitState as SubmitState.Success).grade}"
                            )
                            Text(
                                text = (submitState as SubmitState.Success).gradeCount.split(";")
                                    .joinToString("\n")
                            )
                        }
                    }

                    is SubmitState.Failed -> {
                        Text(text = (submitState as SubmitState.Failed).msg)
                    }

                    else -> {}
                }
            },
            onDismissRequest = { viewModel.resetSubmitState() },
            confirmButton = { }
        )
    }
}
