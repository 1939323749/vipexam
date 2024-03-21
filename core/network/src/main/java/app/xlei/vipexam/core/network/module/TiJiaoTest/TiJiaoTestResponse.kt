package app.xlei.vipexam.core.network.module.TiJiaoTest


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TiJiaoTestResponse(
    @SerialName("code")
    val code: Int,
    @SerialName("grade")
    val grade: Double,
    @SerialName("gradedCount")
    val gradedCount: String,
    @SerialName("msg")
    val msg: String
)