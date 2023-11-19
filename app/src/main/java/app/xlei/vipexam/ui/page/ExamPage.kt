package app.xlei.vipexam.ui.page

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.xlei.vipexam.data.Children
import app.xlei.vipexam.data.Exam
import app.xlei.vipexam.data.Muban
import app.xlei.vipexam.ui.question.*
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.launch

@Composable
fun ExamPage(
    exam: Exam
) {
    questions(
        tabItems = getTabItems(exam.muban),
        mubanList = exam.muban
    )
}

fun getTabItems(mubanList: List<Muban>):List<TabItem>{
    val tabItems = mutableListOf<TabItem>()
    for (muban in mubanList){
        tabItems.add(
            TabItem(
                title = muban.cname,
            )
        )
    }
    return tabItems
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun questions(
    tabItems: List<TabItem>,
    mubanList: List<Muban>
) {
    var selectedTabIndex by remember {
        mutableIntStateOf(0) // or use mutableStateOf(0)
    }

    var pagerState = rememberPagerState {
        tabItems.size
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { index ->
                when (mubanList[index].ename) {
                    "ecswriting" -> Ecswriting(mubanList[index])
                    "ecscloze" -> Ecscloze(mubanList[index])
                    "ecsqread" -> Ecsqread(mubanList[index])
                    "ecszread" -> Ecszread(mubanList[index])
                    "ecstranslate" -> Ecstranslate(mubanList[index])
                    "ecfwriting" -> Ecswriting(mubanList[index])
                    "ecfcloze" -> Ecscloze(mubanList[index])
                    "ecfqread" -> Ecsqread(mubanList[index])
                    "ecfzread" -> Ecszread(mubanList[index])
                    "ecftranslate" -> Ecstranslate(mubanList[index])
                    "eylhlisteninga" -> Eylhlisteninga(mubanList[index])
                    "eylhlisteningb" -> Eylhlisteninga(mubanList[index])
                    "eylhlisteningc" -> Eylhlisteninga(mubanList[index])
                }
        }
    }

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            selectedTabIndex = pagerState.currentPage
        }
    }
}

data class TabItem(
    val title: String,
)

suspend fun getExam(examId: String, account: String, token: String): Exam? {
    val client = HttpClient()

    val response = client.post("https://vipexam.cn/exam/getExamList.action") {
        header("Accept", "application/json, text/javascript, */*; q=0.01")
        header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
        header("Connection", "keep-alive")
        header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
        header("Origin", "https://vipexam.cn")
        header("Referer", "https://vipexam.cn/begin_testing2.html?id=$examId")
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
        setBody("examID=$examId&account=$account&token=$token")
    }
    Log.d("",response.bodyAsText())
    client.close()
    val gson = Gson()
    return gson.fromJson(response.bodyAsText(), Exam::class.java)
}