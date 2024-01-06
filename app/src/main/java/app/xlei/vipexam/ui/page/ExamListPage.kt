package app.xlei.vipexam.ui.page

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.xlei.vipexam.R
import app.xlei.vipexam.data.ExamUiState
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class
)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun examListView(
    examListUiState: ExamUiState.ExamListUiState,
    onPreviousPageClicked: () -> Unit,
    onNextPageClicked: () -> Unit,
    onExamClick: (String) -> Unit,
    refresh: () -> Unit,
) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val firstVisibleItemIndex by remember { derivedStateOf { scrollState.firstVisibleItemIndex } }
    val bottomAppBarScrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior(
        rememberBottomAppBarState()
    )

    Scaffold(
        modifier = Modifier
            .nestedScroll(bottomAppBarScrollBehavior.nestedScrollConnection),
        bottomBar = {
            BoxWithConstraints {
                if (maxHeight > 360.dp) {
                    BottomAppBar {
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                FilledIconButton(
                                    onClick = {
                                        onPreviousPageClicked()
                                        coroutineScope.launch {
                                            scrollState.animateScrollToItem(0)
                                        }
                                    },
                                    enabled = examListUiState.currentPage.toInt() > 1
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                        contentDescription = "previous page",
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .align(Alignment.Bottom)
                            ) {
                                Text(
                                    text = "${(examListUiState.currentPage.toInt() - 1) * 20 + 1}-" +
                                            "${(examListUiState.currentPage.toInt() - 1) * 20 + 20}/" +
                                            "${examListUiState.examList.count}",
                                    fontSize = 12.sp
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .align(Alignment.CenterVertically)
                            ) {
                                FilledIconButton(
                                    onClick = {
                                        onNextPageClicked()
                                        coroutineScope.launch {
                                            scrollState.animateScrollToItem(0)
                                        }
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = "next page",
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (firstVisibleItemIndex > 0) FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        scrollState.animateScrollToItem(0)
                    }
                }
            ) {
                Icon(Icons.Default.KeyboardArrowUp, "back to top")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) {padding ->
        val refreshing by remember{ mutableStateOf(false) }

        val state = rememberPullRefreshState(refreshing, refresh)


        Box (
            modifier = Modifier
                .pullRefresh(state)
                .padding(bottom = padding.calculateBottomPadding())
        ){
            Column{
                LazyColumn(
                    state = scrollState,
                ) {
                    items(examListUiState.examList.list.size) {
                        if (examListUiState.examType == R.string.practice_exam)
                            ListItem(
                                headlineContent = { Text(getExamNameAndNo(examListUiState.examList.list[it].examname).first) },
                                trailingContent = { Text(getExamNameAndNo(examListUiState.examList.list[it].examname).second) },
                                leadingContent = {
                                    Box(
                                        modifier = Modifier
                                            .height(40.dp)
                                            .clip(CircleShape)
                                            .aspectRatio(1f)
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                    ) {
                                        Text(
                                            text = getExamCET6Keyword(examListUiState.examList.list[it].examname),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .clickable {
                                        onExamClick(examListUiState.examList.list[it].examid)
                                    }
                            )
                        else
                            ListItem(
                                headlineContent = { Text(examListUiState.examList.list[it].examname) },
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
                                modifier = Modifier
                                    .clickable {
                                        onExamClick(examListUiState.examList.list[it].examid)
                                    }
                            )
                        HorizontalDivider()
                    }

                }
            }
            PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))
        }
    }
}

fun getExamNameAndNo(text: String):Pair<String,String>{
    val result = mutableListOf<String>()
    val pattern = Regex("""[0-9]+""")
    val matches = pattern.findAll(text)
    for(match in matches){
        result.add(match.value)
    }
    return text.split(result.last())[0] to result.last()
}

fun getExamCET6Keyword(text: String):String{
    val keywords = listOf("作文", "阅读", "听力","翻译")

    val regex = Regex(keywords.joinToString("|"))
    val matches = regex.findAll(text)

    matches.forEach {
        if (keywords.contains(it.value))return it.value
    }
    return "模拟"
}

