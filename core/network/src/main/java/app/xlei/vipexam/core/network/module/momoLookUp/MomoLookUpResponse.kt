package app.xlei.vipexam.core.network.module.momoLookUp


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MomoLookUpResponse(
    @SerialName("data")
    val `data`: Data,
    @SerialName("errors")
    val errors: List<String>,
    @SerialName("success")
    val success: Boolean
)