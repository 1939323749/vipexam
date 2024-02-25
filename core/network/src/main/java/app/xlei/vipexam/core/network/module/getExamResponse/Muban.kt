package app.xlei.vipexam.core.network.module.getExamResponse


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Muban(
    @SerialName("basic")
    val basic: String = "",
    @SerialName("cname")
    val cname: String = "",
    @SerialName("cunt")
    val cunt: Int = 0,
    @SerialName("ename")
    val ename: String = "",
    @SerialName("grade")
    val grade: String = "",
    @SerialName("shiti")
    val shiti: List<Shiti> = listOf()
)