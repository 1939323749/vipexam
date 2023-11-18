package app.xlei.vipexam.ui.page

import app.xlei.vipexam.data.LoginResponse
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*


suspend fun getToken(account: String, password: String): LoginResponse {
    val client = HttpClient()

    val response = client.post("https://vipexam.cn/user/login.action") {
        headers {
            append(HttpHeaders.Host, "vipexam.cn")
            append(HttpHeaders.Connection, "keep-alive")
            append("sec-ch-ua", "\"Microsoft Edge\";v=\"119\", \"Chromium\";v=\"119\", \"Not?A_Brand\";v=\"24\"")
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

    return gson.fromJson(response.bodyAsText(), LoginResponse::class.java)
}