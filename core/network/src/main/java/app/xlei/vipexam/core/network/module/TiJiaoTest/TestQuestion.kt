package app.xlei.vipexam.core.network.module.TiJiaoTest


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TestQuestion(
    @SerialName("basic")
    val basic: String,
    @SerialName("grade")
    val grade: String,
    @SerialName("questionCode")
    val questionCode: String,
    @SerialName("questiontype")
    val questiontype: String,
    @SerialName("refAnswer")
    val refAnswer: String
)