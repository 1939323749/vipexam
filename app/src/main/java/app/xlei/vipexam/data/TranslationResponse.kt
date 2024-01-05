package app.xlei.vipexam.data

data class TranslationResponse(
    val code: Int,
    val id: String,
    val data: String,
    val alternatives: List<String>,
)
