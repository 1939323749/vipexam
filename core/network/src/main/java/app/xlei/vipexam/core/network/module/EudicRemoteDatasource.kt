package app.xlei.vipexam.core.network.module

import app.xlei.vipexam.core.network.module.eudic.AddNewCategoryResponse
import app.xlei.vipexam.core.network.module.eudic.AddWordsResponse
import app.xlei.vipexam.core.network.module.eudic.GetAllCategoryResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object EudicRemoteDatasource {
    lateinit var api: String

    private val httpClient by lazy {
        _httpClient.value
    }

    private val _httpClient = lazy {
        HttpClient(CIO) {
            engine {
                requestTimeout = 0
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    private suspend fun getAllCategory(): Result<GetAllCategoryResponse> {
        return try {
            Result.success(
                httpClient.get("https://api.frdic.com/api/open/v1/studylist/category?language=en") {
                    headers {
                        append("Authorization", api)
                    }
                }.body<GetAllCategoryResponse>().also { println(it) }
            )
        } catch (e: Exception) {
            println(e)
            Result.failure(e)
        }
    }

    private suspend fun uploadAllWords(words: List<String>, id: String): Result<AddWordsResponse> {
        return try {
            Result.success(
                httpClient.post("https://api.frdic.com/api/open/v1/studylist/words") {
                    headers {
                        append("Authorization", api)
                    }
                    setBody(
                        AddNewWordsPayload(
                            id,
                            "en",
                            words
                        )
                    )
                    contentType(ContentType.Application.Json)
                }.body<AddWordsResponse>().also { println(it) }
            )
        } catch (e: Exception) {
            println(e)
            Result.failure(e)
        }
    }

    private suspend fun createNewCategory(): Result<String> {
        return try {
            Result.success(
                httpClient.post("https://api.frdic.com/api/open/v1/studylist/category") {
                    headers {
                        append("Authorization", api)
                    }
                    setBody("{\n    \"language\": \"en\",\n    \"name\": \"vipexam\"\n}")
                    contentType(ContentType.Application.Json)
                }.run {
                    bodyAsText().also { println(it) }
                    body<AddNewCategoryResponse>().data.id
                }
            )
        } catch (e: Exception) {
            println(e)
            Result.failure(e)
        }
    }

    suspend fun check() = getAllCategory()

    suspend fun sync(words: List<String>): Boolean {
        if (words.isEmpty()) {
            return false
        } else {
            getAllCategory()
                .onSuccess { resp ->
                    resp.data.firstOrNull { it.name == "vipexam" }
                        .let { data ->
                            if (data != null)
                                uploadAllWords(words, data.id)
                                    .onSuccess { return true }
                                    .onFailure { return false }
                            else {
                                createNewCategory()
                                    .onSuccess {
                                        uploadAllWords(words, it)
                                            .onSuccess { return true }
                                            .onFailure { return false }
                                    }
                                    .onFailure { return false }
                            }
                        }
                }
                .onFailure {
                    return false
                }
            return false
        }
    }
}

@Serializable
private data class AddNewWordsPayload(
    val id: String,
    val language: String,
    val words: List<String>,
)