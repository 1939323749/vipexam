package app.xlei.vipexam.core.network.module.momoLookUp


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Option(
    @SerialName("option")
    val option: String,
    @SerialName("phrase")
    val phrase: String,
    @SerialName("translation")
    val translation: String
)