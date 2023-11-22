package app.xlei.vipexam.ui.page

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.xlei.vipexam.data.Exam
import app.xlei.vipexam.data.Muban
import app.xlei.vipexam.ui.components.CustomFloatingActionButton
import app.xlei.vipexam.ui.question.*
import app.xlei.vipexam.ui.question.cloze.clozeView
import app.xlei.vipexam.ui.question.listening.listeningView
import app.xlei.vipexam.ui.question.qread.qreadView
import app.xlei.vipexam.ui.question.translate.translateView
import app.xlei.vipexam.ui.question.writing.writingView
import app.xlei.vipexam.ui.question.zread.zreadView
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

@Composable
fun ExamPage(
    exam: Exam,
    onFirstItemHidden: (String) -> Unit,
    onFirstItemAppear: ()->Unit
) {
    questions(
        mubanList = exam.muban,
        onFirstItemHidden = {
            onFirstItemHidden(it)
        },
        onFirstItemAppear = {
            onFirstItemAppear()
        }
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun questions(
    mubanList: List<Muban>,
    viewModel: QuestionsViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
    onFirstItemHidden: (String) -> Unit,
    onFirstItemAppear: ()->Unit
) {
    viewModel.setMubanList(mubanList)
    val uiState by viewModel.uiState.collectAsState()
    val questions = getQuestions(uiState.mubanList!!)

    Scaffold(
        floatingActionButton = {
            CustomFloatingActionButton(
                expandable = true,
                onFabClick = {},
                iconExpanded = Icons.Filled.KeyboardArrowDown,
                iconUnExpanded = Icons.Filled.KeyboardArrowUp,
                items = questions,
                onItemClick = {
                    navController.navigate(it)
                }
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            NavHost(
                navController = navController,
                startDestination = questions[0].first,
                modifier = Modifier
            ) {
                for ((index,q) in questions.withIndex()){
                    composable(route = q.first){
                        when (q.first) {
                            "ecswriting" -> writingView(
                                muban = mubanList[index],
                                onFirstItemHidden = {
                                    onFirstItemHidden(it)
                                },
                                onFirstItemAppear = {
                                    onFirstItemAppear()
                                }
                            )
                            "ecscloze" -> clozeView(
                                muban = mubanList[index],
                                onFirstItemHidden = {
                                    onFirstItemHidden(it)
                                },
                                onFirstItemAppear = {
                                    onFirstItemAppear()
                                }
                            )
                            "ecsqread" -> qreadView(
                                muban = mubanList[index],
                                onFirstItemHidden = {
                                    onFirstItemHidden(it)
                                },
                                onFirstItemAppear = {
                                    onFirstItemAppear()
                                }
                            )
                            "ecszread" -> zreadView(
                                muban = mubanList[index],
                                onFirstItemHidden = {
                                    onFirstItemHidden(it)
                                },
                                onFirstItemAppear = {
                                    onFirstItemAppear()
                                },
                            )
                            "ecstranslate" -> translateView(
                                muban = mubanList[index],
                                onFirstItemHidden = {
                                    onFirstItemHidden(it)
                                },
                                onFirstItemAppear = {
                                    onFirstItemAppear()
                                },
                            )
                            "ecfwriting" -> writingView(
                                muban = mubanList[index],
                                onFirstItemHidden = {
                                    onFirstItemHidden(it)
                                },
                                onFirstItemAppear = {
                                    onFirstItemAppear()
                                }
                            )
                            "ecfcloze" -> clozeView(
                                muban = mubanList[index],
                                onFirstItemHidden = {
                                    onFirstItemHidden(it)
                                },
                                onFirstItemAppear = {
                                    onFirstItemAppear()
                                }
                            )
                            "ecfqread" -> qreadView(
                                muban = mubanList[index],
                                onFirstItemHidden = {
                                    onFirstItemHidden(it)
                                },
                                onFirstItemAppear = {
                                    onFirstItemAppear()
                                }
                            )
                            "ecfzread" -> zreadView(
                                muban = mubanList[index],
                                onFirstItemHidden = {
                                    onFirstItemHidden(it)
                                },
                                onFirstItemAppear = {
                                    onFirstItemAppear()
                                },
                            )
                            "ecftranslate" -> translateView(
                                muban = mubanList[index],
                                onFirstItemHidden = {
                                    onFirstItemHidden(it)
                                },
                                onFirstItemAppear = {
                                    onFirstItemAppear()
                                },
                            )
                            "eylhlisteninga" -> listeningView(
                                muban = mubanList[index],
                                onFirstItemHidden = {
                                    onFirstItemHidden(it)
                                },
                                onFirstItemAppear = {
                                    onFirstItemAppear()
                                },
                            )
                            "eylhlisteningb" -> listeningView(
                                muban = mubanList[index],
                                onFirstItemHidden = {
                                    onFirstItemHidden(it)
                                },
                                onFirstItemAppear = {
                                    onFirstItemAppear()
                                },
                            )
                            "eylhlisteningc" -> listeningView(
                                muban = mubanList[index],
                                onFirstItemHidden = {
                                    onFirstItemHidden(it)
                                },
                                onFirstItemAppear = {
                                    onFirstItemAppear()
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getQuestions(mubanList: List<Muban>): MutableList<Pair<String, String>> {
    val questions = mutableListOf<Pair<String,String>>()

    for (muban in mubanList){
        questions.add(muban.ename to muban.cname)
    }

    return questions
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