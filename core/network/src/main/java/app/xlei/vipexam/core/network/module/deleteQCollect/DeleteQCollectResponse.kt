package app.xlei.vipexam.core.network.module.deleteQCollect


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeleteQCollectResponse(
    @SerialName("code")
    val code: String,
    @SerialName("msg")
    val msg: String
)