package app.xlei.vipexam.core.network.module

data class TranslationResponse(
    val code: Int,
    val id: String,
    val data: String,
    val alternatives: List<String>,
)
