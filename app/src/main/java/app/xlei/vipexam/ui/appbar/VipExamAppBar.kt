package app.xlei.vipexam.ui.appbar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
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
import androidx.datastore.preferences.core.edit
import androidx.hilt.navigation.compose.hiltViewModel
import app.xlei.vipexam.R
import app.xlei.vipexam.core.data.util.Preferences
import app.xlei.vipexam.core.data.util.dataStore
import app.xlei.vipexam.ui.components.VipexamCheckbox
import compose.icons.FeatherIcons
import compose.icons.feathericons.Menu
import compose.icons.feathericons.Star
import kotlinx.coroutines.flow.map
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
    viewModel: AppBarViewModel = hiltViewModel()
) {
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()
    val showAnswer = context.dataStore.data.map {
        it[Preferences.SHOW_ANSWER] ?: false
    }.collectAsState(initial = false)

    val uiState by viewModel.bookmarks.collectAsState()

    val isInBookmark = when (appBarTitle) {
        is AppBarTitle.Exam -> uiState.any { it.examId == appBarTitle.examId && it.question == appBarTitle.question }
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
                        if (showAnswer.value) {
                            coroutine.launch {
                                context.dataStore.edit {
                                    it[Preferences.SHOW_ANSWER] = false
                                }
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
                            it.examId == appBarTitle.examId
                                    && it.question == appBarTitle.question
                        }
                    )
                    else viewModel.addToBookmark(
                        appBarTitle.examName,
                        appBarTitle.examId,
                        appBarTitle.question
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
                                    checked = showAnswer.value,
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
                                context.dataStore.edit {
                                    it[Preferences.SHOW_ANSWER] = showAnswer.value.not()
                                }
                            }
                            showMenu = false
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
}
