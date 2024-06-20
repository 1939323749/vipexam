package app.xlei.vipexam.core.network.module

import android.os.Environment
import app.xlei.vipexam.core.network.module.TiJiaoTest.TiJiaoTestPayload
import app.xlei.vipexam.core.network.module.TiJiaoTest.TiJiaoTestResponse
import app.xlei.vipexam.core.network.module.addQCollect.AddQCollectResponse
import app.xlei.vipexam.core.network.module.deleteQCollect.DeleteQCollectResponse
import app.xlei.vipexam.core.network.module.getExamList.GetExamListResponse
import app.xlei.vipexam.core.network.module.getExamResponse.GetExamResponse
import app.xlei.vipexam.core.network.module.getExamResponse.Muban
import app.xlei.vipexam.core.network.module.login.LoginResponse
import app.xlei.vipexam.core.network.module.momoLookUp.MomoLookUpResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File


object NetWorkRepository {
    private lateinit var account: String
    private lateinit var password: String
    private lateinit var token: String
    private lateinit var organization: String

    private val httpClient by lazy {
        _httpClient.value
    }

    private val _httpClient = lazy {
        HttpClient(CIO) {
            engine {
                requestTimeout = 0
            }
            install(ContentNegotiation) {
                json()
            }
        }
    }

    fun isAvailable() = this::account.isInitialized && this::token.isInitialized

    private fun HttpRequestBuilder.vipExamHeaders(
        referrer: String
    ): HeadersBuilder {
        return headers {
            append(HttpHeaders.Accept,"application/json, text/javascript, */*; q=0.01")
            append(HttpHeaders.AcceptLanguage,"zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
            append(HttpHeaders.Connection,"keep-alive")
            append(HttpHeaders.ContentType,"application/x-www-form-urlencoded; charset=UTF-8")
            append(HttpHeaders.Origin,"https://vipexam.cn")
            append("Sec-Fetch-Dest", "empty")
            append("Sec-Fetch-Mode", "cors")
            append("Sec-Fetch-Site", "same-origin")
            append(HttpHeaders.UserAgent, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0")
            append("X-Requested-With", "XMLHttpRequest")
            append(
                "sec-ch-ua",
                "\"Microsoft Edge\";v=\"119\", \"Chromium\";v=\"119\", \"Not?A_Brand\";v=\"24\""
            )
            append("sec-ch-ua-mobile", "?0")
            append("sec-ch-ua-platform", "\"macOS\"")
            append(HttpHeaders.Referrer,referrer)
        }
    }

    suspend fun getToken(
        account: String,
        password: String,
        organization: String,
    ): Result<LoginResponse> {
        this.account = account
        this.password = password
        this.organization = organization

        return try {
            httpClient.post("https://vipexam.cn/user/login.action") {
                vipExamHeaders(referrer = "https://vipexam.cn/login2.html")
                setBody("account=$account&password=$password")
            }.body<LoginResponse>().also {
                if (it.code == "1") this.token = it.token
            }.run {
                when (code) {
                    "1" -> Result.success(this)
                    else -> Result.failure(error(msg))
                }
            }.also {
                println(organization)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExam(examId: String): Result<GetExamResponse> {
        return try {
            Result.success(
                httpClient.post("https://vipexam.cn/exam/getExamList.action") {
                    vipExamHeaders(referrer = "https://vipexam.cn/begin_testing2.html?id=$examId")
                    setBody("examID=$examId&account=$account&token=$token")
                }.body()
            )
        }catch (e: Exception){
            println(e)
            Result.failure(e)
        }
    }

    fun getQuestions(mubanList: List<Muban>): List<Pair<String, String>> {
        val questions = mutableListOf<Pair<String, String>>()

        for (muban in mubanList) {
            questions.add(muban.ename to muban.cname)
        }

        return questions
    }

    suspend fun getExamList(
        page: String,
        examStyle: Int,
        examTypeEName: String,
    ): Result<GetExamListResponse> {
        return try {
            Result.success(
                httpClient.post("https://vipexam.cn/web/moreCourses") {
                    vipExamHeaders(referrer = "https://vipexam.cn/resources_kinds.html?id=$examTypeEName")
                    setBody("data={\"account\":\"$account\",\"token\":\"$token\",\"typeCode\":\"$examTypeEName\",\"resourceType\":\"${examStyle}\",\"courriculumType\":\"0\",\"classHourS\":\"0\",\"classHourE\":\"0\",\"yearPublishedS\":\"0\",\"yearPublishedE\":\"0\",\"page\":$page,\"limit\":20,\"collegeName\":\"$organization\"}")
                }.body<GetExamListResponse>().also { println(it) }
            )
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun searchExam(
        page: String,
        searchContent: String
    ): Result<GetExamListResponse> {
        return try {
            Result.success(
                httpClient.post("https://vipexam.cn/examseek/speedinessSearch"){
                    vipExamHeaders(referrer = "exam_paper.html?field=1&exam_paper.html?field=1&keys=$searchContent")
                    setBody("""
                        account=$account&token=$token&data={"page":$page,"limit":20,"fields":"1","keyword":"$searchContent","matching":"like"}
                    """.trimIndent())
                }.body()
            )
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun translateToZH(text: String): Result<TranslationResponse> {
        return try {
            Result.success(
                httpClient.post("https://deeplx-next.netlify.app/api/translate") {
                    header("Accept", "application/json, text/javascript, */*; q=0.01")
                    contentType(ContentType.Application.Json)
                    setBody(
                        mapOf(
                            "text" to text,
                            "source_lang" to "EN",
                            "target_lang" to "ZH"
                        )
                    )
                }.body()
            )
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    suspend fun download(
        fileName: String,
        examId: String,
    ){
        val client = HttpClient(CIO)

        try {
            val res = client.get("""
                https://vipexam.cn/web/getExamWordByStu?examID=$examId&account=$account&token=$token
        """.trimIndent()){
                headers {
                    append("Host", "vipexam.cn")
                    append("Connection", "keep-alive")
                    append("sec-ch-ua", "\"Not A(Brand\";v=\"99\", \"Microsoft Edge\";v=\"121\", \"Chromium\";v=\"121\"")
                    append("sec-ch-ua-mobile", "?0")
                    append("sec-ch-ua-platform", "\"macOS\"")
                    append("Upgrade-Insecure-Requests", "1")
                    append("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36 Edg/121.0.0.0")
                    append("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                    append("Sec-Fetch-Site", "none")
                    append("Sec-Fetch-Mode", "navigate")
                    append("Sec-Fetch-User", "?1")
                    append("Sec-Fetch-Dest", "document")
                    append("Accept-Encoding", "gzip, deflate, br")
                    append("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
                }
            }.bodyAsChannel().also {
                val downloadsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsPath, "$fileName.doc")
                if (!downloadsPath.exists()) {
                    downloadsPath.mkdirs()
                }
                it.copyAndClose(file.writeChannel())
            }
            println(res)
        } catch (e: Exception) {
            println(e)
        }
    }

    suspend fun addQCollect(
        examId: String,
        questionCode: String
    ): Result<AddQCollectResponse> {
        return try {
            Result.success(
                httpClient.post("https://vipexam.cn/questioncollect/addQCollect.action") {
                    vipExamHeaders("https://vipexam.cn/begin_testing2.html?id=$examId")
                    setBody(
                        """
                        account=$account&token=$token&ExamID=$examId&QuestionCode=$questionCode
                    """.trimIndent()
                    )
                }.body()
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteQCollect(
        examId: String,
        questionCode: String
    ): Result<DeleteQCollectResponse> {
        return try {
            Result.success(
                httpClient.post("https://vipexam.cn/questioncollect/deleteQCollect.action") {
                    vipExamHeaders("https://vipexam.cn/begin_testing2.html?id=$examId")
                    setBody(
                        """
                        account=$account&token=$token&ExamID=$examId&QuestionCode=$questionCode
                    """.trimIndent()
                    )
                }.body()
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun tiJiaoTest(payload: TiJiaoTestPayload): Result<TiJiaoTestResponse> {
        return try {
            println(Json.encodeToString(payload))
            Result.success(
                httpClient.post("https://vipexam.cn/exam/TiJiaoTest") {
                    vipExamHeaders("https://vipexam.cn/begin_testing.html?id=${payload.examID}")
                    setBody(
                        "data=" + Json.encodeToString(
                            payload.copy(
                                account = account,
                                token = token,
                            )
                        )
                    )
                }.run {
                    println(this.bodyAsText())
                    this.body<TiJiaoTestResponse>()
                }
            )
        } catch (e: Exception) {
            println(e)
            Result.failure(e)
        }
    }

    suspend fun momoLookUp(
        offset: Int,
        keyword: String,
        paperType: String = "CET6"
    ): Result<MomoLookUpResponse> {
        return try {
            Result.success(
                httpClient.get("https://lookup.maimemo.com/api/v1/search?offset=$offset&limit=10&keyword=$keyword&paper_type=$paperType")
                    .body<MomoLookUpResponse>()
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
