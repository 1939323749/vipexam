package app.xlei.vipexam.core.network.module.addQCollect


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddQCollectResponse(
    @SerialName("code")
    val code: String,
    @SerialName("msg")
    val msg: String
)