package app.xlei.vipexam.core.network.module.momoLookUp


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    @SerialName("phrases")
    val phrases: List<Phrase>,
    @SerialName("time_cost")
    val timeCost: Int,
    @SerialName("total")
    val total: Total
)