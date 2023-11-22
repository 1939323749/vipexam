package app.xlei.vipexam.ui.page

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import app.xlei.vipexam.R
import app.xlei.vipexam.constant.Constants
import app.xlei.vipexam.data.ExamList
import app.xlei.vipexam.ui.theme.VipexamTheme
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("CoroutineCreationDuringComposition", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ExamListView(
    currentPage: String,
    examList: ExamList,
    onPreviousPageClicked:()->Unit,
    onNextPageClicked:()->Unit,
    onExamClicked:(String)->Unit,
    refresh: ()->Unit,
    onFirstItemHidden: ()->Unit,
    onFirstItemAppear: ()->Unit
){
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val firstVisibleItemIndex by remember { derivedStateOf { scrollState.firstVisibleItemIndex } }

    Scaffold(
        floatingActionButton = {
            if(firstVisibleItemIndex >0) FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        scrollState.scrollToItem(0)
                    }
                }
            ){
                Icons.Filled.KeyboardArrowUp
            }
        }
    ) {
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
        ){
            Column {
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.weight(9f)
                ){
                    items(examList.list.size){
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 60.dp)
                                .padding(vertical = 4.dp)
                                .padding(start = 12.dp, end = 12.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .clickable {
                                    onExamClicked(examList.list[it].examid)
                                }
                        ){
                            Text(
                                text = examList.list[it].examname,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .align(Alignment.CenterStart)
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.weight(1f).align(Alignment.CenterHorizontally)
                ){
                    if(currentPage.toInt()>1){
                        Button(
                            onClick = onPreviousPageClicked
                        ){
                            Text("Previous page")
                        }
                    }
                    Button(
                        onClick = onNextPageClicked
                    ){
                        Text("Next page")
                    }
                }
            }

            PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))
        }
    }
}

suspend fun getExamList(account: String,token: String,currentPage: String): ExamList? {
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
        header("Origin", Constants.URL)
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
        setBody("data={\"account\":\"$account\",\"token\":\"$token\",\"typeCode\":\"ve01002\",\"resourceType\":\"4\",\"courriculumType\":\"0\",\"classHourS\":\"0\",\"classHourE\":\"0\",\"yearPublishedS\":\"0\",\"yearPublishedE\":\"0\",\"page\":$currentPage,\"limit\":20,\"collegeName\":\"吉林大学\"}")
    }


    client.close()
    val gson = Gson()
    return gson.fromJson(response.bodyAsText(), ExamList::class.java)
}