package app.xlei.vipexam.core.network.module.momoLookUp


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Context(
    @SerialName("answers")
    val answers: List<String>,
    @SerialName("options")
    val options: List<Option>,
    @SerialName("phrase")
    val phrase: String,
    @SerialName("translation")
    val translation: String
)