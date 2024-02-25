package app.xlei.vipexam.core.network.module

import kotlinx.serialization.Serializable

@Serializable
data class TranslationResponse(
    val code: Int,
    val id: String,
    val data: String,
    val alternatives: List<String>,
)
