package app.xlei.vipexam.data.network

import android.util.Log
import app.xlei.vipexam.data.*
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.serialization.jackson.*
import kotlinx.coroutines.time.withTimeout
import kotlinx.coroutines.withTimeout
import org.apache.http.Header

object Repository {
    lateinit var account: String
    lateinit var password: String
    private lateinit var token: String

    suspend fun getToken(account: String, password: String): LoginResponse {
        this.account = account
        this.password = password
        val client = HttpClient()

        val response = client.post("https://vipexam.cn/user/login.action") {
            headers {
                append(HttpHeaders.Host, "vipexam.cn")
                append(HttpHeaders.Connection, "keep-alive")
                append(
                    "sec-ch-ua",
                    "\"Microsoft Edge\";v=\"119\", \"Chromium\";v=\"119\", \"Not?A_Brand\";v=\"24\""
                )
                append(HttpHeaders.Accept, "application/json, text/javascript, */*; q=0.01")
                append("sec-ch-ua-mobile", "?0")
                append(
                    HttpHeaders.UserAgent,
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0"
                )
                append("sec-ch-ua-platform", "\"macOS\"")
                append(HttpHeaders.Origin, "https://vipexam.cn")
                append(HttpHeaders.Referrer, "https://vipexam.cn/login2.html")
                append(HttpHeaders.AcceptLanguage, "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
                append(HttpHeaders.ContentType, "application/x-www-form-urlencoded; charset=UTF-8")
            }
            setBody("account=$account&password=$password")
        }
        client.close()
        val gson = Gson()
        val loginResponse = gson.fromJson(response.bodyAsText(), LoginResponse::class.java)

        return when (loginResponse.code) {
            "1" -> {
                token = loginResponse.token
                loginResponse
            }

            else -> loginResponse
        }
    }

    suspend fun getExam(examId: String): Exam? {
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
            header(
                "sec-ch-ua",
                "\"Microsoft Edge\";v=\"119\", \"Chromium\";v=\"119\", \"Not?A_Brand\";v=\"24\""
            )
            header("sec-ch-ua-mobile", "?0")
            header("sec-ch-ua-platform", "\"macOS\"")
            setBody("examID=$examId&account=$account&token=$token")
        }
        client.close()
        val gson = Gson()
        return gson.fromJson(response.bodyAsText(), Exam::class.java)
    }

    fun getQuestions(mubanList: List<Muban>): List<Pair<String, String>> {
        val questions = mutableListOf<Pair<String, String>>()

        for (muban in mubanList) {
            questions.add(muban.ename to muban.cname)
        }

        return questions
    }

    suspend fun getExamList(page: String, type: String): ExamList? {
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
            setBody("data={\"account\":\"$account\",\"token\":\"$token\",\"typeCode\":\"ve01002\",\"resourceType\":\"${type}\",\"courriculumType\":\"0\",\"classHourS\":\"0\",\"classHourE\":\"0\",\"yearPublishedS\":\"0\",\"yearPublishedE\":\"0\",\"page\":$page,\"limit\":20,\"collegeName\":\"吉林大学\"}")
        }


        client.close()
        val gson = Gson()
        return gson.fromJson(response.bodyAsText(), ExamList::class.java)
    }

    suspend fun translateToZH(text: String): TranslationResponse? {
        val client = HttpClient(CIO) {
            engine {
                requestTimeout = 0
            }
            install(ContentNegotiation) {
                gson()
            }
        }

        val response = client.post("https://api.deeplx.org/translate") {
            header("Accept", "application/json, text/javascript, */*; q=0.01")
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "text" to text,
                    "source_lang" to "EN",
                    "target_lang" to "ZH"
                )
            )
        }

        client.close()
        val gson = Gson()
        return gson.fromJson(response.bodyAsText(), TranslationResponse::class.java)
    }
}