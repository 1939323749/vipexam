package app.xlei.vipexam.core.network.module.eudic


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetAllCategoryResponse(
    @SerialName("data")
    val `data`: List<Data>,
    @SerialName("message")
    val message: String
)