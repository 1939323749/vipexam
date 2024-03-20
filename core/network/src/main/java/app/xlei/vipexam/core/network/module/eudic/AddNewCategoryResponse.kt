package app.xlei.vipexam.core.network.module.eudic


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddNewCategoryResponse(
    @SerialName("data")
    val `data`: Data,
    @SerialName("message")
    val message: String
)