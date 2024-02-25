package app.xlei.vipexam.core.network.module.getExamList


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetExamListResponse(
    @SerialName("code")
    val code: String = "",
    @SerialName("count")
    val count: Int = 0,
    @SerialName("list")
    val list: List<Exam> = listOf(),
    @SerialName("msg")
    val msg: String = "",
    @SerialName("resourceType")
    val resourceType: Int = 0
)