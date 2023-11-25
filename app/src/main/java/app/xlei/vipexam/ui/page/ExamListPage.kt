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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.xlei.vipexam.data.ExamList
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun examListView(
    currentPage: String,
    examList: ExamList,
    isPractice: Boolean,
    onPreviousPageClicked: () -> Unit,
    onNextPageClicked: () -> Unit,
    onExamClicked: (String) -> Unit,
    refresh: () -> Unit,
    onFirstItemHidden: () -> Unit,
    onFirstItemAppear: () -> Unit
){
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val firstVisibleItemIndex by remember { derivedStateOf { scrollState.firstVisibleItemIndex } }

    Scaffold(
        bottomBar = {
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
                                } },
                            enabled = currentPage.toInt() > 1
                        ){
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
                            text = "${(currentPage.toInt()-1) * 20 +1}-" +
                                    "${(currentPage.toInt()-1) * 20 +20}/" +
                                    "${examList.count}",
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
                                } },
                        ){
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "next page",
                            )
                        }

                    }
                }
            }
        },
        floatingActionButton = {
            if(firstVisibleItemIndex >0) FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        scrollState.animateScrollToItem(0)
                    }
                }
            ){
                Icon(Icons.Default.KeyboardArrowUp,"back to top")
            }
        }
    ) {padding ->
        val refreshing by remember{ mutableStateOf(false) }

        val state = rememberPullRefreshState(refreshing, refresh)

        if(firstVisibleItemIndex >0){
            onFirstItemHidden()
        }else{
            onFirstItemAppear()
        }

        Box (
            modifier = Modifier
                .pullRefresh(state)
                .padding(padding)
        ){
            Column{
                LazyColumn(
                    state = scrollState,
                ){
                    items(examList.list.size){
                        if (isPractice)
                            ListItem(
                                headlineContent = { Text(getExamNameAndNo(examList.list[it].examname).first) },
                                trailingContent = { Text(getExamNameAndNo(examList.list[it].examname).second) },
                                leadingContent = {
                                    Box(
                                        modifier = Modifier
                                            .height(40.dp)
                                            .clip(CircleShape)
                                            .aspectRatio(1f)
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                    ){
                                        Text(
                                            text = getExamCET6Keyword(examList.list[it].examname),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .clickable {
                                        onExamClicked(examList.list[it].examid)
                                    }
                            )
                        else
                            ListItem(
                                headlineContent = { Text(examList.list[it].examname) },
                                leadingContent = {
                                    Box(
                                        modifier = Modifier
                                            .height(40.dp)
                                            .clip(CircleShape)
                                            .aspectRatio(1f)
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                    ){
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
                                        onExamClicked(examList.list[it].examid)
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

suspend fun getExamList(account: String, token: String, currentPage: String, type: String): ExamList? {
    val client = HttpClient(OkHttp) {
        engine {
            config {
                followRedirects(true)
            }
        }
    }
    val response = client.post("https://vipexam.cn/web/moreCourses") {
        header("Accept", "application/json, text/javascript, */*; q=0.01")
        header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
        header("Connection", "keep-alive")
        header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
        header("Origin", "https://vipexam.cn")
        header("Referer", "https://vipexam.cn/resources_kinds.html?id=ve01002")
        header("Sec-Fetch-Dest", "empty")
        header("Sec-Fetch-Mode", "cors")
        header("Sec-Fetch-Site", "same-origin")
        header(
            "User-Agent",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0"
        )
        header("X-Requested-With", "XMLHttpRequest")
        header("sec-ch-ua", "\"Microsoft Edge\";v=\"119\", \"Chromium\";v=\"119\", \"Not?A_Brand\";v=\"24\"")
        header("sec-ch-ua-mobile", "?0")
        header("sec-ch-ua-platform", "\"macOS\"")
        setBody("data={\"account\":\"$account\",\"token\":\"$token\",\"typeCode\":\"ve01002\",\"resourceType\":\"${type}\",\"courriculumType\":\"0\",\"classHourS\":\"0\",\"classHourE\":\"0\",\"yearPublishedS\":\"0\",\"yearPublishedE\":\"0\",\"page\":$currentPage,\"limit\":20,\"collegeName\":\"吉林大学\"}")
    }


    client.close()
    val gson = Gson()
    return gson.fromJson(response.bodyAsText(), ExamList::class.java)
}