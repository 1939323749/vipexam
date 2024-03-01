package app.xlei.vipexam.core.network.module.momoLookUp


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Total(
    @SerialName("relation")
    val relation: String,
    @SerialName("value")
    val value: Int
)