package app.xlei.vipexam.core.network.module.eudic


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    @SerialName("id")
    val id: String,
    @SerialName("language")
    val language: String,
    @SerialName("name")
    val name: String
)