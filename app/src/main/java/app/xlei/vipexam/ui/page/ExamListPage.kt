package app.xlei.vipexam.ui.page

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import app.xlei.vipexam.R
import app.xlei.vipexam.core.data.ExamListItem
import app.xlei.vipexam.core.ui.DateText
import app.xlei.vipexam.core.ui.ErrorMessage
import app.xlei.vipexam.core.ui.LoadingNextPageItem
import app.xlei.vipexam.core.ui.PageLoader
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterialApi::class,
)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ExamListView(
    modifier: Modifier = Modifier,
    type: Int,
    viewModel: ExamListViewModel = hiltViewModel(),
    onExamClick: (String) -> Unit,
) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val firstVisibleItemIndex by remember { derivedStateOf { scrollState.firstVisibleItemIndex } }
    val examListPagingItems: LazyPagingItems<ExamListItem> = viewModel.examListState.collectAsLazyPagingItems()

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visible = firstVisibleItemIndex > 10,
                enter = fadeIn(animationSpec = tween(200)),
                exit = fadeOut(animationSpec = tween(200)),
            ) {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            scrollState.animateScrollToItem(0)
                        }
                    }
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, "back to top")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        modifier = modifier,
    ) {padding ->

        val refreshing = when (examListPagingItems.loadState.refresh) {
            is LoadState.Loading -> true
            else -> false
        }

        val state = rememberPullRefreshState(refreshing, { examListPagingItems.refresh() })

        Box (
            modifier = Modifier
                .pullRefresh(state)
                .padding(bottom = padding.calculateBottomPadding())
        ){
            Column{
                LazyColumn(
                    state = scrollState,
                ) {

                    items(examListPagingItems.itemCount) {
                        if (type == R.string.practice_exam)
                            ListItem(
                                headlineContent = { Text(getExamNameAndNo(examListPagingItems[it]!!.exam.examname).first) },
                                trailingContent = { Text(getExamNameAndNo(examListPagingItems[it]!!.exam.examname).second) },
                                leadingContent = {
                                    Box(
                                        modifier = Modifier
                                            .height(40.dp)
                                            .clip(CircleShape)
                                            .aspectRatio(1f)
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                    ) {
                                        Text(
                                            text = getExamCET6Keyword(examListPagingItems[it]!!.exam.examname),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                        )
                                    }
                                },
                                supportingContent = { examListPagingItems[it]?.lastOpen?.let { time->
                                    DateText(time)
                                } },
                                modifier = Modifier
                                    .clickable {
                                        onExamClick(examListPagingItems[it]!!.exam.examid)
                                    }
                            )
                        else
                            ListItem(
                                headlineContent = { Text(examListPagingItems[it]!!.exam.examname) },
                                leadingContent = {
                                    Box(
                                        modifier = Modifier
                                            .height(40.dp)
                                            .clip(CircleShape)
                                            .aspectRatio(1f)
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                    ) {
                                        Text(
                                            text = "真题",
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                        )
                                    }
                                },
                                supportingContent = { examListPagingItems[it]?.lastOpen?.let { time->
                                    DateText(time)
                                } },
                                modifier = Modifier
                                    .clickable {
                                        onExamClick(examListPagingItems[it]!!.exam.examid)
                                    }
                            )
                        HorizontalDivider()
                    }
                    examListPagingItems.apply {
                        when {
                            loadState.refresh is LoadState.Loading -> {
                                item { PageLoader(modifier = Modifier.fillParentMaxSize()) }
                            }

                            loadState.refresh is LoadState.Error -> {
                                val error = examListPagingItems.loadState.refresh as LoadState.Error
                                item {
                                    ErrorMessage(
                                        modifier = Modifier.fillParentMaxSize(),
                                        message = error.error.localizedMessage!!,
                                        onClickRetry = { retry() })
                                }
                            }

                            loadState.append is LoadState.Loading -> {
                                item { LoadingNextPageItem(modifier = Modifier) }
                            }

                            loadState.append is LoadState.Error -> {
                                val error = examListPagingItems.loadState.append as LoadState.Error
                                item {
                                    ErrorMessage(
                                        modifier = Modifier,
                                        message = error.error.localizedMessage!!,
                                        onClickRetry = { retry() })
                                }
                            }
                        }
                    }
                }
            }
            PullRefreshIndicator(
                refreshing = refreshing,
                state = state,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

private fun getExamNameAndNo(text: String):Pair<String,String>{
    val result = mutableListOf<String>()
    val pattern = Regex("""[0-9]+""")
    val matches = pattern.findAll(text)
    for(match in matches){
        result.add(match.value)
    }
    return text.split(result.last())[0] to result.last()
}

private fun getExamCET6Keyword(text: String):String{
    val keywords = listOf("作文", "阅读", "听力","翻译")

    val regex = Regex(keywords.joinToString("|"))
    val matches = regex.findAll(text)

    matches.forEach {
        if (keywords.contains(it.value))return it.value
    }
    return "模拟"
}
