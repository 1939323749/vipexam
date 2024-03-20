package app.xlei.vipexam.core.network.module.eudic


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddWordsResponse(
    @SerialName("message")
    val message: String
)