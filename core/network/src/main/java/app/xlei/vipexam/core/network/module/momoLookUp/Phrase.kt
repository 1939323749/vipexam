package app.xlei.vipexam.core.network.module.momoLookUp


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Phrase(
    @SerialName("context")
    val context: Context,
    @SerialName("highlight")
    val highlight: Highlight,
    @SerialName("paper_id")
    val paperId: String,
    @SerialName("path")
    val path: String,
    @SerialName("phrase_en")
    val phraseEn: String,
    @SerialName("phrase_id")
    val phraseId: String,
    @SerialName("phrase_zh")
    val phraseZh: String,
    @SerialName("source")
    val source: String
)