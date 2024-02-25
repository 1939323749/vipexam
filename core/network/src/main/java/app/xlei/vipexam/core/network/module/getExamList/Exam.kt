package app.xlei.vipexam.core.network.module.getExamList


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Exam(
    @SerialName("collectDate")
    val collectDate: String? = null,
    @SerialName("examTypeEName")
    val examTypeEName: String = "",
    @SerialName("examdate")
    val examdate: String = "",
    @SerialName("examid")
    val examid: String = "",
    @SerialName("examname")
    val examname: String = "",
    @SerialName("examstyle")
    val examstyle: String = "",
    @SerialName("examtyleStr")
    val examtyleStr: String = "",
    @SerialName("examtypeII")
    val examtypeII: String? = null,
    @SerialName("examtypecode")
    val examtypecode: String = "",
    @SerialName("fullName")
    val fullName: String = "",
    @SerialName("temlExamtimeLimit")
    val temlExamtimeLimit: Int = 0,
    @SerialName("templatecode")
    val templatecode: String = "",
    @SerialName("tid")
    val tid: Int = 0,
    @SerialName("tmplExamScore")
    val tmplExamScore: Int = 0
)