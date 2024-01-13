package app.xlei.vipexam.core.network.module

import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.gson.gson

object NetWorkRepository {
    private lateinit var account: String
    private lateinit var password: String
    private lateinit var token: String

    suspend fun getToken(account: String, password: String): Result<LoginResponse> {
        NetWorkRepository.account = account
        NetWorkRepository.password = password
        val client = HttpClient()


        val response: HttpResponse?
        try {
            response = client.post("https://vipexam.cn/user/login.action") {
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
                    append(
                        HttpHeaders.AcceptLanguage,
                        "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6"
                    )
                    append(
                        HttpHeaders.ContentType,
                        "application/x-www-form-urlencoded; charset=UTF-8"
                    )
                }
                setBody("account=$account&password=$password")
            }
        } catch (e: Exception) {
            return Result.failure(e)
        } finally {
            client.close()
        }

        val gson = Gson()
        var loginResponse: LoginResponse? = null
        try {
            if (response != null) {
                loginResponse = gson.fromJson(response.bodyAsText(), LoginResponse::class.java)
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }

        return if (loginResponse != null) {
            when (loginResponse.code) {
                "1" -> {
                    token = loginResponse.token
                    Result.success(loginResponse)
                }

                else -> Result.failure(Exception(loginResponse.msg))
            }
        } else {
            Result.failure(Exception("login response is null"))
        }
    }

    suspend fun getExam(examId: String): Result<Exam> {
        val client = HttpClient()

        val response: HttpResponse?
        try {
            response = client.post("https://vipexam.cn/exam/getExamList.action") {
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
        } catch (e: Exception) {
            return Result.failure(e)
        } finally {
            client.close()
        }

        val gson = Gson()
        var exam: Exam? = null
        try {
            if (response != null) {
                exam = gson.fromJson(response.bodyAsText(), Exam::class.java)
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
        return if (exam != null) {
            Result.success(exam)
        } else {
            Result.failure(Exception("exam is null"))
        }
    }

    fun getQuestions(mubanList: List<Muban>): List<Pair<String, String>> {
        val questions = mutableListOf<Pair<String, String>>()

        for (muban in mubanList) {
            questions.add(muban.ename to muban.cname)
        }

        return questions
    }

    suspend fun getExamList(page: String, type: String): Result<ExamList> {
        val client = HttpClient(OkHttp) {
            engine {
                config {
                    followRedirects(true)
                }
            }
        }
        val response: HttpResponse?
        try {
            response = client.post("https://vipexam.cn/web/moreCourses") {
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
                header(
                    "sec-ch-ua",
                    "\"Microsoft Edge\";v=\"119\", \"Chromium\";v=\"119\", \"Not?A_Brand\";v=\"24\""
                )
                header("sec-ch-ua-mobile", "?0")
                header("sec-ch-ua-platform", "\"macOS\"")
                setBody("data={\"account\":\"$account\",\"token\":\"$token\",\"typeCode\":\"ve01002\",\"resourceType\":\"${type}\",\"courriculumType\":\"0\",\"classHourS\":\"0\",\"classHourE\":\"0\",\"yearPublishedS\":\"0\",\"yearPublishedE\":\"0\",\"page\":$page,\"limit\":20,\"collegeName\":\"吉林大学\"}")
            }
        } catch (e: Exception) {
            return Result.failure(e)
        } finally {
            client.close()
        }
        var examList: ExamList? = null
        val gson = Gson()
        try {
            if (response != null) {
                examList = gson.fromJson(response.bodyAsText(), ExamList::class.java)
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
        return if (examList != null) {
            Result.success(examList)
        } else {
            Result.failure(Exception("exam list is null"))
        }
    }

    suspend fun translateToZH(text: String): Result<TranslationResponse> {
        val client = HttpClient(CIO) {
            engine {
                requestTimeout = 0
            }
            install(ContentNegotiation) {
                gson()
            }
        }

        val response: HttpResponse?
        try {
            response = client.post("https://api.deeplx.org/translate") {
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
        } catch (e: Exception) {
            return Result.failure(e)
        } finally {
            client.close()
        }

        var translationResponse: TranslationResponse? = null
        val gson = Gson()
        try {
            if (response != null) {
                translationResponse =
                    gson.fromJson(response.bodyAsText(), TranslationResponse::class.java)
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }

        return if (translationResponse != null) {
            Result.success(translationResponse)
        } else {
            Result.failure(Exception("Translation response was null"))
        }
    }
}

