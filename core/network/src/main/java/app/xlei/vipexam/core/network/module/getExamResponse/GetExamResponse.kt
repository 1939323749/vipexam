package app.xlei.vipexam.core.network.module.getExamResponse


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetExamResponse(
    @SerialName("code")
    val code: Int = 0,
    @SerialName("count")
    val count: Int = 0,
    @SerialName("examID")
    val examID: String = "",
    @SerialName("examName")
    val examName: String = "",
    @SerialName("examTypeCode")
    val examTypeCode: String = "",
    @SerialName("examstyle")
    val examstyle: String = "",
    @SerialName("msg")
    val msg: String = "",
    @SerialName("muban")
    val muban: List<Muban> = listOf(),
    @SerialName("planID")
    val planID: String = "",
    @SerialName("timelimit")
    val timelimit: Int = 0
)