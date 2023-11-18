package app.xlei.vipexam.ui.page

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import app.xlei.vipexam.data.Exam
import app.xlei.vipexam.data.Muban
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun ExamPage(
    exam: Exam
) {
    Column {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Text(exam.examName)
            for (muban in exam.muban){
                when(muban.ename){
                    "ecswriting" -> Ecswriting(muban)
                    "ecscloze"-> Ecscloze(muban)
                    "ecsqread"-> Ecsqread(muban)
                    "ecszread"-> Ecszread(muban)
                    "ecstranslate"-> Ecstranslate(muban)
                    "ecfwriting" -> Ecswriting(muban)
                    "ecfcloze"-> Ecscloze(muban)
                    "ecfqread"-> Ecsqread(muban)
                    "ecfzread"-> Ecszread(muban)
                    "ecftranslate"-> Ecstranslate(muban)
                    "eylhlisteninga"-> Eylhlisteninga(muban)
                    "eylhlisteningb"-> Eylhlisteningb(muban)
                    "eylhlisteningc"-> Eylhlisteningc(muban)
                }
            }
        }
    }
}

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

@Composable
fun Ecswriting(muban: Muban){
    Column {
        Text(muban.cname)
        Text(muban.shiti[0].primQuestion)
    }
}

@Composable
fun Ecscloze(muban: Muban){
    Column {
        Text(muban.cname)
        Text(muban.shiti[0].primQuestion)
        for (ti in muban.shiti[0].children){
            Text(ti.secondQuestion)
        }
    }
}

@Composable
fun Ecsqread(muban: Muban){
    Column {
        Text(muban.cname)
        Text(muban.shiti[0].primQuestion)
        for((no,ti) in muban.shiti[0].children.withIndex()){
            Text((no+1).toString()+". " + ti.secondQuestion)
        }
    }
}

@Composable
fun Ecszread(muban: Muban){
    Column {
        Text(muban.cname)
        for ((no,ti) in muban.shiti.withIndex()){
            Text((no+1).toString())
            Text(ti.primQuestion)
            for ((num,t ) in ti.children.withIndex()){
                Text((num+1).toString()+t.secondQuestion)
                Text("[A]"+t.first)
                Text("[B]"+t.second)
                Text("[C]"+t.third)
                Text("[D]"+t.fourth)
            }
        }
    }
}

@Composable
fun Ecstranslate(muban: Muban){
    Column{
        Text(muban.cname)
        Text(muban.shiti[0].primQuestion)
    }
}

@Composable
fun Eylhlisteninga(muban: Muban){
    Column {
        Text(muban.cname)
        for ((no,ti) in muban.shiti.withIndex()){
            Text((no+1).toString())
            for ((n,t) in ti.children.withIndex()){
                Text((n+1).toString())
                Text("[A]"+t.first)
                Text("[B]"+t.second)
                Text("[C]"+t.third)
                Text("[D]"+t.fourth)
            }
        }
    }
}

@Composable
fun Eylhlisteningb(muban: Muban){
    Column {
        Text(muban.cname)
        for ((no,ti) in muban.shiti.withIndex()){
            Text((no+1).toString())
            for ((n,t) in ti.children.withIndex()){
                Text((n+1).toString())
                Text("[A]"+t.first)
                Text("[B]"+t.second)
                Text("[C]"+t.third)
                Text("[D]"+t.fourth)
            }
        }
    }
}

@Composable
fun Eylhlisteningc(muban: Muban){
    Column {
        Text(muban.cname)
        for ((no,ti) in muban.shiti.withIndex()){
            Text((no+1).toString())
            for ((n,t) in ti.children.withIndex()){
                Text((n+1).toString())
                Text("[A]"+t.first)
                Text("[B]"+t.second)
                Text("[C]"+t.third)
                Text("[D]"+t.fourth)
            }
        }
    }
}
