package app.xlei.vipexam.core.network.module.TiJiaoTest


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TiJiaoTestPayload(
    @SerialName("account")
    val account: String? = null,
    @SerialName("count")
    val count: String,
    @SerialName("examID")
    val examID: String,
    @SerialName("examName")
    val examName: String,
    @SerialName("examStyle")
    val examStyle: String,
    @SerialName("examTypeCode")
    val examTypeCode: String,
    @SerialName("TestQuestion")
    val testQuestion: List<TestQuestion>,
    @SerialName("token")
    val token: String? = null
)