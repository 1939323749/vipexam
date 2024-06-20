package app.xlei.vipexam.core.network.module

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TranslationResponse(
    val code: Int,
    val id: String,
    val data: String,
    val alternatives: List<String>,
    @SerialName("source_lang")
    val sourceLang: String,
    @SerialName("target_lang")
    val targetLang: String,
    val method: String
)
