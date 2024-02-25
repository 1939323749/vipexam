package app.xlei.vipexam.core.network.module.getExamResponse


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Children(
    @SerialName("answerPic")
    val answerPic: String = "",
    @SerialName("audioFiles")
    val audioFiles: String = "",
    @SerialName("discPic")
    val discPic: String = "",
    @SerialName("discription")
    val discription: String = "",
    @SerialName("fifth")
    val fifth: String = "",
    @SerialName("fifthPic")
    val fifthPic: String = "",
    @SerialName("first")
    val first: String = "",
    @SerialName("firstPic")
    val firstPic: String = "",
    @SerialName("fourth")
    val fourth: String = "",
    @SerialName("fourthPic")
    val fourthPic: String = "",
    @SerialName("isCollect")
    val isCollect: String = "",
    @SerialName("originalText")
    val originalText: String = "",
    @SerialName("primPic")
    val primPic: String = "",
    @SerialName("primQuestion")
    val primQuestion: String = "",
    @SerialName("questionCode")
    val questionCode: String = "",
    @SerialName("refAnswer")
    val refAnswer: String = "",
    @SerialName("second")
    val second: String = "",
    @SerialName("secondPic")
    val secondPic: String = "",
    @SerialName("secondQuestion")
    val secondQuestion: String = "",
    @SerialName("subPrimPic")
    val subPrimPic: String = "",
    @SerialName("subjectTypeEname")
    val subjectTypeEname: String = "",
    @SerialName("third")
    val third: String = "",
    @SerialName("thirdPic")
    val thirdPic: String = ""
)